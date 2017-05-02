package gov.lanl.micot.infrastructure.ep.io.dew;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Properties;

import gov.lanl.micot.infrastructure.ep.io.ElectricPowerModelFile;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.dew.Dew;
import gov.lanl.micot.infrastructure.ep.model.dew.DewException;
import gov.lanl.micot.infrastructure.ep.model.dew.DewModelFactory;
import gov.lanl.micot.infrastructure.ep.model.dew.DewModelImpl;
import gov.lanl.micot.infrastructure.ep.model.dew.DewPtcabcondData;
import gov.lanl.micot.infrastructure.ep.model.dew.DewPtlinecondData;
import gov.lanl.micot.infrastructure.ep.model.dew.DewPtlinespcData;
import gov.lanl.micot.infrastructure.ep.model.dew.DewPtxfrmData;
import gov.lanl.micot.infrastructure.ep.model.dew.DewPtcapData;
import gov.lanl.micot.infrastructure.model.Component;
import gov.lanl.micot.infrastructure.model.Connection;
import gov.lanl.micot.infrastructure.model.Model;
import gov.lanl.micot.util.geometry.GeometryOperations;
import gov.lanl.micot.util.geometry.GeometryOperationsFactory;
import gov.lanl.micot.util.geometry.Line;
import gov.lanl.micot.util.geometry.Point;
import gov.lanl.micot.util.io.FileParser;
import gov.lanl.micot.util.io.PropertiesFactory;

/**
 * Reads a DEW file format
 * 
 * @author Russell Bent
 * 
 */
public class DewFile extends FileParser implements ElectricPowerModelFile {

  private static final String MODEL_FILE_TAG = "model.file";
  private static final String CONNECTION_STRING_TAG = "connection.string";
  private static final String PROJECTION_STRING_TAG = "projection";
  private static final String PTLINESPC_DATABASE_TAG = "ptlinespc.database";
  private static final String PTXFRM_DATABASE_TAG = "ptxfrm.database";
  private static final String PTCAP_DATABASE_TAG = "ptcap.database";

  /**
   * Constructor
   */
  public DewFile() {
  }

  @Override
  public ElectricPowerModel readModel(String filename) throws IOException {
    Properties properties = PropertiesFactory.getInstance().createProperties(filename);

    String modelFile = properties.getProperty(MODEL_FILE_TAG);
    String connectionString = properties.getProperty(CONNECTION_STRING_TAG);
    String projectionString = properties.getProperty(PROJECTION_STRING_TAG);
    // this string is for the ms access database, should also work for xfrm table
    String ptlinespcString = properties.getProperty(PTLINESPC_DATABASE_TAG);
//    String ptxfrmString = properties.getProperty(PTXFRM_DATABASE_TAG);
//    String ptcapString = properties.getProperty(PTCAP_DATABASE_TAG);
    
    ElectricPowerModel state = null;
    try {
      state = parseFile(modelFile.trim(), connectionString, projectionString, ptlinespcString);
    }
    catch (DewException e) {
      throw new IOException("Error parsing Dew file: " + e.getMessage());
    }
    return state;
  }

  @Override
  public void saveFile(String filename, ElectricPowerModel model) throws IOException {
    DewModelImpl tempModel = DewModelFactory.getInstance().constructModel(model);
    try {
      tempModel.syncModel();
    }
    catch (DewException e) {
      throw new IOException("Error writing Dew file: " + e.getMessage());
    }

    // save the .dewc file
    PrintStream ps = new PrintStream(filename);
    String dewfilename = filename.substring(0, filename.length() - 2);
    ps.println(MODEL_FILE_TAG + "=" + dewfilename);
    ps.println(CONNECTION_STRING_TAG + "=" + tempModel.getDew().getConnectionString());
    ps.println(PTLINESPC_DATABASE_TAG + "=" + tempModel.getDew().getPtlinespcDatabase());
    ps.println(PTXFRM_DATABASE_TAG + "=" + tempModel.getDew().getPtcapDatabase());    
    ps.println(PTCAP_DATABASE_TAG + "=" + tempModel.getDew().getPtcapDatabase());

    ps.close();

    tempModel.getDew().saveData(dewfilename);
  }

  /**
   * Parses the file and returns a model
   * 
   * @throws IOException
   * @throws DewException
   */
  private ElectricPowerModel parseFile(String filename, String connectionString, String projectionString, String ptlinespcFile) throws IOException, DewException {
    DewPtlinespcDatabase database = new DewPtlinespcDatabase();
    DewPtlinecondDatabase database2 = new DewPtlinecondDatabase();
    DewPtcabcondDatabase database3 = new DewPtcabcondDatabase();
    DewPtxfrmDatabase database4 = new DewPtxfrmDatabase();
    DewPtcapDatabase database5 = new DewPtcapDatabase();    

    Collection<DewPtlinespcData> lineData = null;
    Collection<DewPtlinecondData> linecondData = null;
    Collection<DewPtcabcondData> cabcondData = null;
    Collection<DewPtxfrmData> xfrmData = null;
    Collection<DewPtcapData> capData = null;    

    try {
      lineData = database.getData(ptlinespcFile);
      linecondData = database2.getData(ptlinespcFile);
      cabcondData = database3.getData(ptlinespcFile);
      xfrmData = database4.getData(ptlinespcFile);
      capData = database5.getData(ptlinespcFile);
    }
    catch (SQLException e) {
      e.printStackTrace();
    }

    Dew dewEngine = connectionString == null ? new Dew() : new Dew(connectionString);
    dewEngine.connect(filename);
    DewModelFactory factory = DewModelFactory.getInstance();
    ElectricPowerModel model = factory.createModel(dewEngine, lineData, linecondData, cabcondData, xfrmData, capData);
    dewEngine.setPtlinespcDatabase(ptlinespcFile);

    // convert all the coordinates to the standard coordinate system, if we
    // have a projection string defined
    if (projectionString != null) {
      GeometryOperations operations = GeometryOperationsFactory.getInstance().getDefaultGeometryOperations();
      for (Component component : model.getComponents()) {
        Point oldPoint = component.getCoordinate();
        Point newPoint = operations.transformProjection(oldPoint, projectionString, "EPSG:4326");
        component.setCoordinate(newPoint);
      }
      for (Connection connection : model.getConnections()) {
        Line oldLine = connection.getCoordinates();
        Line newLine = operations.transformProjection(oldLine, projectionString, "EPSG:4326");
        connection.setCoordinates(newLine);
      }
    }

    return model;
  }

  @Override
  public void saveFile(String filename, Model model) throws IOException {
    saveFile(filename, (ElectricPowerModel) model);
  }

  /**
   * Read in a simple test file
   * 
   * @param args
   * @throws IOException
   */
  public static void main(String[] args) throws IOException {
    String initialFile = "data" + File.separatorChar + "ep" + File.separatorChar + "dew" + File.separatorChar + "Distribution_Circuit_1.dewc";
    // String initialFile = "data" + File.separatorChar + "ep" +
    // File.separatorChar + "dew" + File.separatorChar +
    // "_IEEE-14 Xmission.dewc";
    DewFile file = new DewFile();
    ElectricPowerModel model = file.readModel(initialFile);
    file.saveFile("temp.dew", model);
  }

}
