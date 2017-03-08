package gov.lanl.micot.infrastructure.ep.io.opendss;

import java.io.File;
import java.io.IOException;

import gov.lanl.micot.infrastructure.ep.io.ElectricPowerModelFile;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.opendss.OpenDSSModel;
import gov.lanl.micot.infrastructure.ep.model.opendss.OpenDSSModelFactory;
import gov.lanl.micot.infrastructure.model.Model;
import gov.lanl.micot.util.io.dcom.ComObject;
import gov.lanl.micot.util.io.dcom.ComObjectFactory;
import gov.lanl.micot.util.io.dcom.ComObjectUtilities;
import gov.lanl.micot.util.math.solver.JNAOSUtilities;
import gov.lanl.micot.util.math.solver.JNAOSUtilities.OSType;

/**
 * File for creating opendss model files
 * 
 * @author Russell Bent
 */
public class OpenDSSModelFile implements ElectricPowerModelFile {

  /**
   * Constructor
   */
  public OpenDSSModelFile() {
  }

  @Override
  public  void saveFile(String filename, ElectricPowerModel model) throws IOException {
    OpenDSSModel tempModel = OpenDSSModelFactory.getInstance().constructModel(model);
    tempModel.syncModel();

    ComObject openDSSModel = tempModel.getOpenDSSModel();
    try {
      ComObject text = openDSSModel.call(OpenDSSIOConstants.TEXT);      
      text.put(OpenDSSIOConstants.COMMAND, OpenDSSIOConstants.getSaveModelString(filename));
    }
    catch (Throwable e) {
      e.printStackTrace();
      throw new IOException(e.getMessage());
    }
  }

  @Override
  public ElectricPowerModel readModel(String filename) throws IOException {
    boolean isWindows = JNAOSUtilities.getOSType().equals(OSType.Windows32) || JNAOSUtilities.getOSType().equals(OSType.Windows64);  
	//isWindows = false;
	
	
    // opendss will not allow relative paths
    if (!filename.contains(":") && isWindows) {
      filename = System.getProperty("user.dir") + File.separatorChar + filename;
    }
    
    OpenDSSModel openDSSModel = null;
    
    if (isWindows) {
      ComObjectFactory factory = ComObjectUtilities.getDefaultFactory();
      ComObject model = factory.createComObject(OpenDSSIOConstants.OPENDSS);
      model.getBoolean(OpenDSSIOConstants.OPENDSS_START, 0);    
      ComObject text = model.call(OpenDSSIOConstants.TEXT);      
      text.put(OpenDSSIOConstants.COMMAND, OpenDSSIOConstants.getLoadModelString(filename));    
      openDSSModel = new OpenDSSModel(model);
    }
    else {
      OpenDSSData data = new OpenDSSData(filename);
      openDSSModel = new OpenDSSModel(data);
    }
    
    return openDSSModel;
  }

  @Override
  public void saveFile(String filename, Model model) throws IOException {
    saveFile(filename, (ElectricPowerModel) model);
  }

 
}
