package gov.lanl.micot.infrastructure.ep.model.dew;

import gov.lanl.micot.infrastructure.ep.model.Battery;
import gov.lanl.micot.infrastructure.ep.model.Bus;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.Generator;
import gov.lanl.micot.infrastructure.ep.model.Line;
import gov.lanl.micot.infrastructure.ep.model.Load;
import gov.lanl.micot.infrastructure.ep.model.ShuntCapacitor;
import gov.lanl.micot.infrastructure.ep.model.Transformer;
import gov.lanl.micot.infrastructure.ep.model.dew.DewEngineJNA.TxtMsg;
import gov.lanl.micot.infrastructure.model.Asset;
import gov.lanl.micot.util.collection.Pair;
import gov.lanl.micot.util.geometry.LineImpl;
import gov.lanl.micot.util.geometry.Point;
import gov.lanl.micot.util.geometry.PointImpl;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import com.sun.jna.Pointer;

/**
 * A wrapper class to ease interaction with the DewEngine. Each of the methods
 * potentially throws a DewException that provides (a tiny modicum of)
 * information on where an error might have occurred.
 * 
 * @author Russell Bent
 * 
 */
public class Dew {
  
  private String _connectionString = null;

  private DewEngineJNA _engine = null;

  private String _ptlinespcFile = null;
  
  /**
   * This is the exact contents of the .dew file, we will maintain this, and
   * update the file
   * as we need. It will be reloaded into DEW as necessary. If/when DEW exposes
   * the model parameters
   * through the library, we can do away with this
   * 
   */
  private ArrayList<ArrayList<String>> _systemData = null;
  
  private DewFileData dewFileData = null;

    
  /**
   * Caches results for quick access of results
   */
  private LinkedHashMap<Pair<Integer,String>, Map<String,DewResult>> resultCache = null;
  
  /**
   * Just a cache for warning messages
   */
  private HashSet<String> warningCache = null;

  /**
   * Create a new Dew instance for connecting to a specified "data source name".
   * 
   * @param connectionString
   *          A space-delimited connection string. The first field should be the
   *          username, the second the password, and the third the Windows DSN,
   *          or "data source name".
   */
  public Dew(String connectionString) {
    _connectionString = connectionString;
    _systemData = new ArrayList<ArrayList<String>>();
    dewFileData = new CompressedDewFileData();
    resultCache = new LinkedHashMap<Pair<Integer,String>, Map<String,DewResult>>();
    warningCache = new HashSet<String>();
  }

  /**
   * A constructor with the default DEW connection string
   */
  public Dew() {
    _connectionString = "a a DEW_DSN";
    _systemData = new ArrayList<ArrayList<String>>();
    dewFileData = new CompressedDewFileData();
    resultCache = new LinkedHashMap<Pair<Integer,String>, Map<String,DewResult>>();
    warningCache = new HashSet<String>();
  }
    
  /**
   * Load and start the DEW engine with the connection string specified in the
   * constructor.
   * 
   * @return Whether or not the connection attempt was successful.
   */
  public void connect() throws DewException {
    // dynamically set the path of the ini file
    String path = DewEngineJNA.path;
    
//    String path = System.getProperty("user.dir"); 
  //  int idx = path.lastIndexOf(File.separatorChar);
   // path = path.substring(0,idx+1) + "InfrastructureModeling";
    //path = path + File.separatorChar + "dewlib" + File.separatorChar;
        
    String file = path + "DEWEngine.ini";
    try {
      PrintStream ps = new PrintStream(file);
      ps.println("[DEWEngine]");
      ps.println("DewGUIPath = \"" + path + "\"");
      ps.close();
    }
    catch (FileNotFoundException e) {
      throw new DewException("Failure to set up DEW ini file. " + e.getMessage());
    }

    _engine = DewEngineJNA.INSTANCE;
    int loadResponse = _engine.LoadDewEngine();
    int startResponse = -1;
    try {
      startResponse = _engine.StartDewEngine(_connectionString);
    }
    catch (java.lang.Error e) {
      throw new DewException("Failure to start engine with connection: " + _connectionString
          + ". Often caused because the JVM stack is not big enough.  Adding these flags seems to help -XX:StackShadowPages=35 -Xss100m. Other common problem is that another instance is using the ODBC connection"); 
    }
    
    if (loadResponse != 1) {
      Object message = _engine.GetEWIMessages();
      _engine = null;
      throw new DewException("Failure to load engine. Likely cause is that DEWEngine.ini file is not configured to point at the right location for the DEW dll files. DEW error message is " + message);
    }
    else if (startResponse != 1) {
      Object message = _engine.GetEWIMessages();
      _engine = null;
      throw new DewException("Failure to start engine with connection: " + _connectionString
          + ". Often caused because the JVM stack is not big enough.  Adding these flags seems to help -XX:StackShadowPages=35 -Xss20m. DEW error message is " + message);
    }

  }

  /**
   * Attempts to connect to the DewEngine and then open the system from the
   * specified file. This is simply a shorthand utility method that is
   * equivalent to making separate calls to connect() and then
   * openSystem(filename).
   * 
   * @param filename
   *          The name of the file containing the system.
   * @throws DewException
   */
  public void connect(String filename) throws DewException {
    connect();
    
    try {
      openSystem(filename);
    } 
    catch (IOException | NumberFormatException | ClassNotFoundException e) {
      throw new DewException("Failure opening file: " + filename + " " + e.getMessage());
    }
  }

  /**
   * Load a system from a .dew file.
   * 
   * @param filename
   *          The name of the file containing the system.
   * @throws DewException
   * @throws IOException 
   * @throws ClassNotFoundException 
   * @throws NumberFormatException 
   */
  public void openSystem(String filename) throws DewException, IOException, NumberFormatException, ClassNotFoundException {
    int response = engine().OpenSystemFromFile(filename);
    if (response != 1) {
      throw new DewException("Failure opening system: " + filename);
    }
        
    // let us go ahead an populate a lot of the data we might need
    engine().RunAnalysis(DewVariables.APPID_RADIAL_POWER_FLOW, "", 0);
    
    // doing it this way uses an excessive amount of memory... sigh... maybe find another way
    DataInputStream in = new DataInputStream(new FileInputStream(filename));
    BufferedReader br = new BufferedReader(new InputStreamReader(in));
    
    // load the system data
    boolean foundAComponent = false;
    String currentLine = null;
    while (!foundAComponent && (currentLine = br.readLine()) != null) {      
      if (currentLine.startsWith(DewVariables.DEW_CMP_HEADER + ",")) {
        foundAComponent = true;
      }
      else {
        StringTokenizer tokenizer = new StringTokenizer(currentLine, ",");
        ArrayList<String> data = new ArrayList<String>();
        while (tokenizer.hasMoreTokens()) {
          data.add(tokenizer.nextToken().trim());
        }
        addSystemData(data);
      }
    }
    
    // load the component data
    DewLegacyId idx = new DewLegacyId(-1000,-1000);
    while (currentLine != null) {
      
      StringTokenizer tokenizer = new StringTokenizer(currentLine, ",");
      ArrayList<String> data = new ArrayList<String>();
      while (tokenizer.hasMoreTokens()) {
        data.add(tokenizer.nextToken().trim());
      }

      // sometimes, for an entirely unknown reason, a component will appear twice in a row
      // with slightly different data... and it is necessary... so here is a hack that I hope 
      // holds up
      DewLegacyId idx2 = null;
      if (data.get(DewVariables.DEW_HEADER_IDX).equals(DewVariables.DEW_CMP_HEADER)) {
        int feeder = Integer.parseInt(data.get(2));
        int id = Integer.parseInt(data.get(1));
        idx2 = new DewLegacyId(feeder,id);
      }
      
      if (data.get(DewVariables.DEW_HEADER_IDX).equals(DewVariables.DEW_CMP_HEADER) && !idx.equals(idx2)) {
        int feeder = Integer.parseInt(data.get(2));
        int id = Integer.parseInt(data.get(1));
        
        idx = new DewLegacyId(feeder,id);
        dewFileData.initComponentDataById(idx);
      }
      dewFileData.addComponentDataById(idx,data);      
      currentLine = br.readLine();      
    }
    dewFileData.dataReadComplete();
    br.close();
    in.close();
  }

  /**
   * Perform an analysis of a specific circuit.
   * 
   * @param analysisType
   *          The type of analysis to perform.
   * @param circuitName
   *          The name of the circuit to analyze.
   * @param hour
   *          The hour of the year, starting from 0.
   * @throws DewException
   */
  public void run(int analysisType, String circuitName, int hour) throws DewException {
    int response = engine().RunAnalysis(analysisType, circuitName, hour);
    if (response != 1) {
      throw new DewException("Failure running analysis.");
    }
  }

  /**
   * Perform an analysis of all circuits.
   * 
   * @param analysisType
   *          The type of analysis to perform.
   * @param hour
   *          The hour of the year, starting from 0.
   */
  public void run(int analysisType, int hour) throws DewException {
    run(analysisType, "", hour);
  }

  /**
   * Get results from a previous analysis.
   * 
   * @param analysisType
   *          The type of analysis to get the results for (should have been
   *          previously run).
   * @param variable
   *          The name of the variable to obtain the results for. Valid variable
   *          names for each analysis type can be found in the DewVariables
   *          class.
   * @return An array of the results.
   * @throws DewException
   */
  public Map<String,DewResult> getResults(int analysisType, String variable) throws DewException {
    Pair<Integer,String> cache = new Pair<Integer,String>(analysisType, variable);
    if (resultCache.get(cache) != null) {
      return resultCache.get(cache);
    }
    
    DewVariables vars = new DewVariables();
    Pointer structure = engine().GetAllResultsByUID(analysisType, vars.getVariableIndex(analysisType, variable));
    if (structure == null || structure == Pointer.NULL)
      throw new DewException("Failure retrieving analysis results.");

    List<DewResult> list = new LinkedList<DewResult>();

    ResultBUID result = new ResultBUID(structure);
    ResultBUID it = result;
    while (it != null) {
      DewResult res = new DewResult(it.getUID(), it.isSingelValuedResult(), it.getPhaseValue());
      list.add(res);
      Pointer p = it.getNextResult();
      if (p != null) {
        it = new ResultBUID(p);
      }
      else {
        it = null;
      }
    }
    // engine().ReleaseResults();
    HashMap<String, DewResult> map = new HashMap<String, DewResult>();
    for (DewResult r : list) {
      map.put(r.getUID(), r);
    }
    resultCache.put(cache, map);
    return map;
    
//    return list.toArray(new DewResult[] {});
  }

  /**
   * This might be the place to reload the model into the library... we could be
   * clever here... this would get called any time an analysis function is
   * called AND there had been a change in the text file data...
   * 
   * @throws FileNotFoundException
   * @throws DewException
   */
  public void reloadSystem() throws FileNotFoundException, DewException {
    String filename = "temp-" + System.currentTimeMillis() + ".dew";
    saveData(filename);

    // It is possible this will cause memory to leak, since we don't close out the previous system
        
    //engine().closeSystem(""); 
    int response = engine().OpenSystemFromFile(filename);
    if (response != 1) {
      TxtMsg msg = engine().GetEWIMessages();
      throw new DewException("Failure opening system: " + filename + ". " + msg.toString(true));
    }

    File file = new File(filename);
    file.delete();
  }

  /**
   * Save the data to the file....
   * 
   * @param filename
   * @throws FileNotFoundException
   */
  public void saveData(String filename) throws FileNotFoundException {
    PrintStream ps = new PrintStream(filename);
    int size = systemDataSize();

    for (int j = 0; j < size; ++j) {
      ArrayList<String> string = getSystemData(j);
      for (int i = 0; i < string.size(); ++i) {
        if (i > 0) {
          ps.print(", ");
        }
        ps.print(string.get(i));
      }
      ps.println();
    }

    Collection<DewLegacyId> keys = getComponentIds();
    for (DewLegacyId id : keys) {      
      size = componentDataByIdSize(id);
      for (int j = 0; j < size; ++j) {
        ArrayList<String> str = getComponentDataById(id,j);
        for (int i = 0; i < str.size(); ++i) {
          if (i > 0) {
            ps.print(", ");
          }
          ps.print(str.get(i));
        }
        ps.println();
      }
    }
    ps.close();
  }

  /**
   * Sync the bus data stored in memory with the object in DEW
   * @throws DewException 
   * @throws NumberFormatException 
   */
  protected void syncBus(Bus bus) throws NumberFormatException, DewException {
    DewLegacyId legacyid = bus.getAttribute(DewVariables.DEW_LEGACY_ID_KEY, DewLegacyId.class);    
    String name = bus.getAttribute(Bus.NAME_KEY, String.class);
    boolean isFailed = Integer.parseInt(getComponentData(Asset.IS_FAILED_KEY, legacyid, name).toString()) > 0;
    boolean status = Integer.parseInt(getComponentData(Asset.DESIRED_STATUS_KEY, legacyid, name).toString()) == 1;
    double x = Double.parseDouble(getComponentData(DewVariables.DEW_X_KEY, legacyid, name).toString());
    double y = Double.parseDouble(getComponentData(DewVariables.DEW_Y_KEY, legacyid, name).toString());
    Point point = new PointImpl(x,y);              
    boolean actualFailed = bus.getAttribute(DewVariables.DEW_AGGREGATE_FAILED_KEY, Boolean.class);
    boolean actualStatus = bus.getAttribute(DewVariables.DEW_AGGREGATE_STATUS_KEY, Boolean.class);
    
    if (actualFailed != isFailed) {
      setComponentData(Asset.IS_FAILED_KEY, legacyid, name, actualFailed ? 1 : 0);
    }    
    if (actualStatus != status) {
      setComponentData(Asset.DESIRED_STATUS_KEY, legacyid, name, actualStatus ? 1 : 0);
    }
    if (!bus.getCoordinate().equals(point)) {
      setComponentData(Bus.VOLTAGE_PU_KEY, legacyid, name, bus.getCoordinate());
    }
    
    Object obj = getComponentData(Bus.SYSTEM_VOLTAGE_KV_KEY, legacyid, name);
    double baseKV = obj == null ? -1 : Double.parseDouble(obj.toString());  
    if (bus.getSystemVoltageKV() != baseKV) {
      setComponentData(Bus.SYSTEM_VOLTAGE_KV_KEY, legacyid, name, bus.getSystemVoltageKV());
    }

    double desiredVoltage = getComponentData(Bus.VOLTAGE_PU_KEY, legacyid, name) == null ? 1.0 : Double.parseDouble(getComponentData(Bus.VOLTAGE_PU_KEY, legacyid, name).toString());       
    if (!bus.getAttribute(Bus.VOLTAGE_PU_KEY).equals(desiredVoltage)) {
      setComponentData(Bus.VOLTAGE_PU_KEY, legacyid, name, bus.getAttribute(Bus.VOLTAGE_PU_KEY));
    }


  }

  private int getPhases(boolean hasPhaseA, boolean hasPhaseB, boolean hasPhaseC) {
    if (hasPhaseA && hasPhaseB && hasPhaseC) {
      return 7;
    }
    else if (hasPhaseA && hasPhaseB) {
      return 3;
    }
    else if (hasPhaseA && hasPhaseC) {
      return 5;
    }
    else if (hasPhaseB && hasPhaseC) {
      return 6;
    }
    else if (hasPhaseA) {
      return 1;
    }
    else if (hasPhaseB) {
      return 2;
    }
    else if (hasPhaseC) {
      return 4;
    }
    return 0;
  }
  
  /**
   * Sync the generator data stored in memory with the object in DEW
   * @throws DewException 
   * @throws NumberFormatException 
   */
  protected void syncGenerator(Generator generator) throws NumberFormatException, DewException {
    DewLegacyId legacyid = generator.getAttribute(DewVariables.DEW_LEGACY_ID_KEY, DewLegacyId.class);    
    String name = generator.getAttribute(Generator.NAME_KEY, String.class);
    boolean isFailed = Integer.parseInt(getComponentData(Asset.IS_FAILED_KEY, legacyid, name).toString()) > 0;
    boolean status = Integer.parseInt(getComponentData(Asset.DESIRED_STATUS_KEY, legacyid, name).toString()) == 1;
    double x = Double.parseDouble(getComponentData(DewVariables.DEW_X_KEY, legacyid, name).toString());
    double y = Double.parseDouble(getComponentData(DewVariables.DEW_Y_KEY, legacyid, name).toString());
    int numPhases = Integer.parseInt(getComponentData(Generator.NUM_PHASE_KEY, legacyid, name).toString());
    int phases = Integer.parseInt(getComponentData(DewVariables.DEW_PHASES_KEY, legacyid, name).toString());
    boolean hasPhaseA = generator.getAttribute(Generator.HAS_PHASE_A_KEY, Boolean.class);
    boolean hasPhaseB = generator.getAttribute(Generator.HAS_PHASE_B_KEY, Boolean.class);
    boolean hasPhaseC = generator.getAttribute(Generator.HAS_PHASE_C_KEY, Boolean.class);    
    int generatorPhases = getPhases(hasPhaseA,hasPhaseB,hasPhaseC);
        
    Point point = new PointImpl(x,y);           
   
    boolean actualFailed = generator.getAttribute(DewVariables.DEW_AGGREGATE_FAILED_KEY, Boolean.class);
    boolean actualStatus = generator.getAttribute(DewVariables.DEW_AGGREGATE_STATUS_KEY, Boolean.class);
    
    if (actualFailed != isFailed) {
      setComponentData(Asset.IS_FAILED_KEY, legacyid, name, actualFailed ? 1 : 0);
    }    
    if (actualStatus != status) {
      setComponentData(Asset.DESIRED_STATUS_KEY, legacyid, name, actualStatus ? 1 : 0);
    }
    if (!generator.getAttribute(Generator.NUM_PHASE_KEY).equals(numPhases)) {
      setComponentData(Generator.NUM_PHASE_KEY, legacyid, name, generator.getAttribute(Generator.NUM_PHASE_KEY));
    }
    if (phases != generatorPhases) {
      setComponentData(DewVariables.DEW_PHASES_KEY, legacyid, name, generatorPhases);
    }
    
    setComponentData(Generator.DESIRED_REAL_GENERATION_KEY, legacyid, name, generator.getActualRealGeneration());
    setComponentData(Generator.DESIRED_REACTIVE_GENERATION_KEY, legacyid, name, generator.getActualReactiveGeneration());
  }

  /**
   * Sync the load data stored in memory with the object in DEW
   * @throws DewException 
   */
  protected void syncLoad(Load load) throws DewException {
    DewLegacyId legacyid = load.getAttribute(DewVariables.DEW_LEGACY_ID_KEY, DewLegacyId.class);    
    String name = load.getAttribute(Load.LOAD_NAME_KEY, String.class);
    boolean isFailed = Integer.parseInt(getComponentData(Asset.IS_FAILED_KEY, legacyid, name).toString()) > 0;
    boolean status = Integer.parseInt(getComponentData(Asset.DESIRED_STATUS_KEY, legacyid, name).toString()) == 1;
    double x = Double.parseDouble(getComponentData(DewVariables.DEW_X_KEY, legacyid, name).toString());
    double y = Double.parseDouble(getComponentData(DewVariables.DEW_Y_KEY, legacyid, name).toString());
    int numPhases = Integer.parseInt(getComponentData(Load.NUM_PHASE_KEY, legacyid, name).toString());
    int phases = Integer.parseInt(getComponentData(DewVariables.DEW_PHASES_KEY, legacyid, name).toString());
    boolean hasPhaseA = load.getAttribute(Load.HAS_PHASE_A_KEY, Boolean.class);
    boolean hasPhaseB = load.getAttribute(Load.HAS_PHASE_B_KEY, Boolean.class);
    boolean hasPhaseC = load.getAttribute(Load.HAS_PHASE_C_KEY, Boolean.class);
    int loadPhases = getPhases(hasPhaseA,hasPhaseB,hasPhaseC);
    int dewType = Integer.parseInt(getComponentData(DewVariables.DEW_COMPONENT_TYPE_KEY, legacyid, name).toString());
    Point point = new PointImpl(x,y);           
    boolean actualFailed = load.getAttribute(DewVariables.DEW_AGGREGATE_FAILED_KEY, Boolean.class);
    boolean actualStatus = load.getAttribute(DewVariables.DEW_AGGREGATE_STATUS_KEY, Boolean.class);
   
    if (actualFailed != isFailed) {
      setComponentData(Asset.IS_FAILED_KEY, legacyid, name, actualFailed ? 1 : 0);
    }    
    if (actualStatus != status) {
      setComponentData(Asset.DESIRED_STATUS_KEY, legacyid, name, actualStatus ? 1 : 0);
    }
    if (!load.getAttribute(Generator.NUM_PHASE_KEY).equals(numPhases)) {
      setComponentData(Generator.NUM_PHASE_KEY, legacyid, name, load.getAttribute(Generator.NUM_PHASE_KEY));
    }
    if (phases != loadPhases) {
      setComponentData(DewVariables.DEW_PHASES_KEY, legacyid, name, loadPhases);
    }
    
    if (dewType == 9) {
      setComponentData(Load.DESIRED_REAL_LOAD_A_KEY, legacyid, name,load.getAttribute(Load.ACTUAL_REAL_LOAD_A_KEY, Double.class) * 1000.0);
      setComponentData(Load.DESIRED_REAL_LOAD_B_KEY, legacyid, name,load.getAttribute(Load.ACTUAL_REAL_LOAD_B_KEY, Double.class) * 1000.0);
      setComponentData(Load.DESIRED_REAL_LOAD_C_KEY, legacyid, name,load.getAttribute(Load.ACTUAL_REAL_LOAD_C_KEY, Double.class) * 1000.0);
      setComponentData(Load.DESIRED_REACTIVE_LOAD_A_KEY, legacyid, name,load.getAttribute(Load.ACTUAL_REACTIVE_LOAD_A_KEY, Double.class) * 1000.0);
      setComponentData(Load.DESIRED_REACTIVE_LOAD_B_KEY, legacyid, name,load.getAttribute(Load.ACTUAL_REACTIVE_LOAD_B_KEY, Double.class) * 1000.0);
      setComponentData(Load.DESIRED_REACTIVE_LOAD_C_KEY, legacyid, name,load.getAttribute(Load.ACTUAL_REACTIVE_LOAD_C_KEY, Double.class) * 1000.0);
    }
  }

  /**
   * Sync the line data stored in memory with the object in DEW
   * @throws DewException 
   */
  @SuppressWarnings("unchecked")
  protected void syncLine(Line line) throws DewException {
    
    // ignore the dummy lines that were created
    if (line.getAttribute(Line.NAME_KEY).toString().startsWith("pseudoLine-")) {
      return;
    }
        
    DewLegacyId legacyid = line.getAttribute(DewVariables.DEW_LEGACY_ID_KEY, DewLegacyId.class);  
    Object obj = getComponentData(Bus.NAME_KEY, legacyid, null);
    String name = obj == null ? "" : obj.toString();
    boolean status = Integer.parseInt(getComponentData(Asset.DESIRED_STATUS_KEY, legacyid, name).toString()) == 1;
    boolean isFailed = Integer.parseInt(getComponentData(Asset.IS_FAILED_KEY, legacyid, name).toString()) > 0;
    ArrayList<Point> points = (ArrayList<Point>) getComponentData(Line.COORDINATE_KEY, legacyid, name);
    LineImpl l = new LineImpl(points.toArray(new Point[0]));
    int numPhases = Integer.parseInt(getComponentData(Load.NUM_PHASE_KEY, legacyid, name).toString());
    int phases = Integer.parseInt(getComponentData(DewVariables.DEW_PHASES_KEY, legacyid, name).toString());
    boolean hasPhaseA = line.getAttribute(Line.IS_PHASE_A_KEY, Boolean.class);
    boolean hasPhaseB = line.getAttribute(Line.IS_PHASE_B_KEY, Boolean.class);
    boolean hasPhaseC = line.getAttribute(Line.IS_PHASE_C_KEY, Boolean.class);
    int linePhases = getPhases(hasPhaseA,hasPhaseB,hasPhaseC);
    boolean actualFailed = line.getAttribute(DewVariables.DEW_AGGREGATE_FAILED_KEY, Boolean.class);
    boolean actualStatus = line.getAttribute(DewVariables.DEW_AGGREGATE_STATUS_KEY, Boolean.class);
              
    if (actualFailed != isFailed) {
      setComponentData(Asset.IS_FAILED_KEY, legacyid, name, actualFailed ? 1 : 0);
    }    
    if (actualStatus != status) {
      setComponentData(Asset.DESIRED_STATUS_KEY, legacyid, name, actualStatus ? 1 : 0);
    }
    if (!line.getAttribute(Generator.NUM_PHASE_KEY).equals(numPhases)) {
      setComponentData(Generator.NUM_PHASE_KEY, legacyid, name, line.getAttribute(Generator.NUM_PHASE_KEY));
    }
    if (phases != linePhases) {
      setComponentData(DewVariables.DEW_PHASES_KEY, legacyid, name, linePhases);
    }
    
    Object rA = getComponentData(ElectricPowerFlowConnection.RESISTANCE_PHASE_A_KEY, legacyid, name);
    Object rB = getComponentData(ElectricPowerFlowConnection.RESISTANCE_PHASE_B_KEY, legacyid, name);
    Object rC = getComponentData(ElectricPowerFlowConnection.RESISTANCE_PHASE_C_KEY, legacyid, name);
    Object xA = getComponentData(ElectricPowerFlowConnection.REACTANCE_PHASE_A_KEY, legacyid, name);
    Object xB = getComponentData(ElectricPowerFlowConnection.REACTANCE_PHASE_B_KEY, legacyid, name);
    Object xC = getComponentData(ElectricPowerFlowConnection.REACTANCE_PHASE_C_KEY, legacyid, name);
    Object capA = getComponentData(ElectricPowerFlowConnection.CAPACITY_RATING_A_KEY, legacyid, name);
    Object capB = getComponentData(ElectricPowerFlowConnection.CAPACITY_RATING_B_KEY, legacyid, name);
    Object capC = getComponentData(ElectricPowerFlowConnection.CAPACITY_RATING_C_KEY, legacyid, name);
    
    double resistanceA = rA == null ? .0001 : Double.parseDouble(rA.toString());
    double resistanceB = rB == null ? .0001 : Double.parseDouble(rB.toString());
    double resistanceC = rC == null ? .0001 : Double.parseDouble(rC.toString());
    double reactanceA = xA == null ? .0001 : Double.parseDouble(xA.toString());
    double reactanceB = xB == null ? .0001 : Double.parseDouble(xB.toString());
    double reactanceC = xC == null ? .0001 : Double.parseDouble(xC.toString());
    Double charging = getComponentData(ElectricPowerFlowConnection.LINE_CHARGING_KEY, legacyid, name) == null ? null : Double.parseDouble(getComponentData(ElectricPowerFlowConnection.LINE_CHARGING_KEY, legacyid, name).toString());
    double ratingA = capA == null ? 1e20 : Double.parseDouble(capA.toString());
    double ratingB = capB == null ? 1e20 : Double.parseDouble(capB.toString());
    double ratingC = capC == null ? 1e20 : Double.parseDouble(capC.toString()); 
        
    if (!line.getAttribute(Line.RESISTANCE_PHASE_A_KEY).equals(resistanceA)) {
      setComponentData(Line.RESISTANCE_PHASE_A_KEY, legacyid, name, line.getAttribute(Line.RESISTANCE_PHASE_A_KEY));
    }
    if (!line.getAttribute(Line.RESISTANCE_PHASE_B_KEY).equals(resistanceB)) {
      setComponentData(Line.RESISTANCE_PHASE_B_KEY, legacyid, name, line.getAttribute(Line.RESISTANCE_PHASE_B_KEY));
    }
    if (!line.getAttribute(Line.RESISTANCE_PHASE_C_KEY).equals(resistanceC)) {
      setComponentData(Line.RESISTANCE_PHASE_C_KEY, legacyid, name, line.getAttribute(Line.RESISTANCE_PHASE_C_KEY));
    }
    
    if (!line.getAttribute(Line.REACTANCE_PHASE_A_KEY).equals(reactanceA)) {
      setComponentData(Line.REACTANCE_PHASE_A_KEY, legacyid, name, line.getAttribute(Line.REACTANCE_PHASE_A_KEY));
    }
    if (!line.getAttribute(Line.REACTANCE_PHASE_B_KEY).equals(reactanceB)) {
      setComponentData(Line.REACTANCE_PHASE_B_KEY, legacyid, name, line.getAttribute(Line.REACTANCE_PHASE_B_KEY));
    }
    if (!line.getAttribute(Line.REACTANCE_PHASE_C_KEY).equals(reactanceC)) {
      setComponentData(Line.REACTANCE_PHASE_C_KEY, legacyid, name, line.getAttribute(Line.REACTANCE_PHASE_C_KEY));
    }

    
    if (!line.getAttribute(Line.CAPACITY_RATING_A_KEY).equals(ratingA)) {
      setComponentData(Line.CAPACITY_RATING_A_KEY, legacyid, name, line.getAttribute(Line.CAPACITY_RATING_A_KEY));
    }
    if (!line.getAttribute(Line.CAPACITY_RATING_B_KEY).equals(ratingB)) {
      setComponentData(Line.CAPACITY_RATING_B_KEY, legacyid, name, line.getAttribute(Line.CAPACITY_RATING_B_KEY));
    }
    if (!line.getAttribute(Line.CAPACITY_RATING_C_KEY).equals(ratingC)) {
      setComponentData(Line.CAPACITY_RATING_C_KEY, legacyid, name, line.getAttribute(Line.CAPACITY_RATING_C_KEY));
    }

    if (!(charging == null && line.getAttribute(Line.LINE_CHARGING_KEY) == null)) {
      if (!line.getAttribute(Line.LINE_CHARGING_KEY).equals(charging)) {
        setComponentData(Line.LINE_CHARGING_KEY, legacyid, name, line.getAttribute(Line.LINE_CHARGING_KEY));
      }
    }
  }

  /**
   * Sync the bus data stored in memory with the object in DEW
   * @throws DewException 
   */
  @SuppressWarnings("unchecked")
  protected void syncTransformer(Transformer line) throws DewException {
    DewLegacyId legacyid = line.getAttribute(DewVariables.DEW_LEGACY_ID_KEY, DewLegacyId.class);    
    Object obj = getComponentData(Bus.NAME_KEY, legacyid, null);
    String name = obj == null ? "" : obj.toString();
    boolean isFailed = Integer.parseInt(getComponentData(Asset.IS_FAILED_KEY, legacyid, name).toString()) > 0;
    boolean status = Integer.parseInt(getComponentData(Asset.DESIRED_STATUS_KEY, legacyid, name).toString()) == 1;
    ArrayList<Point> points = (ArrayList<Point>) getComponentData(Line.COORDINATE_KEY, legacyid, name);
    LineImpl l = new LineImpl(points.toArray(new Point[0]));
    int numPhases = Integer.parseInt(getComponentData(Load.NUM_PHASE_KEY, legacyid, name).toString());
    int phases = Integer.parseInt(getComponentData(DewVariables.DEW_PHASES_KEY, legacyid, name).toString());
    boolean hasPhaseA = line.getAttribute(Line.IS_PHASE_A_KEY, Boolean.class);
    boolean hasPhaseB = line.getAttribute(Line.IS_PHASE_B_KEY, Boolean.class);
    boolean hasPhaseC = line.getAttribute(Line.IS_PHASE_C_KEY, Boolean.class);
    int linePhases = getPhases(hasPhaseA,hasPhaseB,hasPhaseC);
    boolean actualFailed = line.getAttribute(DewVariables.DEW_AGGREGATE_FAILED_KEY, Boolean.class);
    boolean actualStatus = line.getAttribute(DewVariables.DEW_AGGREGATE_STATUS_KEY, Boolean.class);    
   
    if (actualFailed != isFailed) {
      setComponentData(Asset.IS_FAILED_KEY, legacyid, name, actualFailed ? 1 : 0);
    }    
    if (actualStatus != status) {
      setComponentData(Asset.DESIRED_STATUS_KEY, legacyid, name, actualStatus ? 1 : 0);      
    }
    if (!line.getAttribute(Generator.NUM_PHASE_KEY).equals(numPhases)) {
      setComponentData(Generator.NUM_PHASE_KEY, legacyid, name, line.getAttribute(Generator.NUM_PHASE_KEY));
    }
    if (phases != linePhases) {
      setComponentData(DewVariables.DEW_PHASES_KEY, legacyid, name, linePhases);
    }
    
    Object rA = getComponentData(ElectricPowerFlowConnection.RESISTANCE_PHASE_A_KEY, legacyid, name);
    Object rB = getComponentData(ElectricPowerFlowConnection.RESISTANCE_PHASE_B_KEY, legacyid, name);
    Object rC = getComponentData(ElectricPowerFlowConnection.RESISTANCE_PHASE_C_KEY, legacyid, name);
    Object xA = getComponentData(ElectricPowerFlowConnection.REACTANCE_PHASE_A_KEY, legacyid, name);
    Object xB = getComponentData(ElectricPowerFlowConnection.REACTANCE_PHASE_B_KEY, legacyid, name);
    Object xC = getComponentData(ElectricPowerFlowConnection.REACTANCE_PHASE_C_KEY, legacyid, name);
    Object capA = getComponentData(ElectricPowerFlowConnection.CAPACITY_RATING_A_KEY, legacyid, name);
    Object capB = getComponentData(ElectricPowerFlowConnection.CAPACITY_RATING_B_KEY, legacyid, name);
    Object capC = getComponentData(ElectricPowerFlowConnection.CAPACITY_RATING_C_KEY, legacyid, name);
    
    double resistanceA = rA == null ? .0001 : Double.parseDouble(rA.toString());
    double resistanceB = rB == null ? .0001 : Double.parseDouble(rB.toString());
    double resistanceC = rC == null ? .0001 : Double.parseDouble(rC.toString());
    double reactanceA = xA == null ? .0001 : Double.parseDouble(xA.toString());
    double reactanceB = xB == null ? .0001 : Double.parseDouble(xB.toString());
    double reactanceC = xC == null ? .0001 : Double.parseDouble(xC.toString());
    Double charging = getComponentData(ElectricPowerFlowConnection.LINE_CHARGING_KEY, legacyid, name) == null ? 0 : Double.parseDouble(getComponentData(ElectricPowerFlowConnection.LINE_CHARGING_KEY, legacyid, name).toString());
    double ratingA = capA == null ? 1e20 : Double.parseDouble(capA.toString());
    double ratingB = capB == null ? 1e20 : Double.parseDouble(capB.toString());
    double ratingC = capC == null ? 1e20 : Double.parseDouble(capC.toString()); 
    
    
    if (!line.getAttribute(Line.RESISTANCE_PHASE_A_KEY).equals(resistanceA)) {
      setComponentData(Line.RESISTANCE_PHASE_A_KEY, legacyid, name, line.getAttribute(Line.RESISTANCE_PHASE_A_KEY));
    }
    if (!line.getAttribute(Line.RESISTANCE_PHASE_B_KEY).equals(resistanceB)) {
      setComponentData(Line.RESISTANCE_PHASE_B_KEY, legacyid, name, line.getAttribute(Line.RESISTANCE_PHASE_B_KEY));
    }
    if (!line.getAttribute(Line.RESISTANCE_PHASE_C_KEY).equals(resistanceC)) {
      setComponentData(Line.RESISTANCE_PHASE_C_KEY, legacyid, name, line.getAttribute(Line.RESISTANCE_PHASE_C_KEY));
    }
    
    if (!line.getAttribute(Line.REACTANCE_PHASE_A_KEY).equals(reactanceA)) {
      setComponentData(Line.REACTANCE_PHASE_A_KEY, legacyid, name, line.getAttribute(Line.REACTANCE_PHASE_A_KEY));
    }
    if (!line.getAttribute(Line.REACTANCE_PHASE_B_KEY).equals(reactanceB)) {
      setComponentData(Line.REACTANCE_PHASE_B_KEY, legacyid, name, line.getAttribute(Line.REACTANCE_PHASE_B_KEY));
    }
    if (!line.getAttribute(Line.REACTANCE_PHASE_C_KEY).equals(reactanceC)) {
      setComponentData(Line.REACTANCE_PHASE_C_KEY, legacyid, name, line.getAttribute(Line.REACTANCE_PHASE_C_KEY));
    }

    
    if (!line.getAttribute(Line.CAPACITY_RATING_A_KEY).equals(ratingA)) {
      setComponentData(Line.CAPACITY_RATING_A_KEY, legacyid, name, line.getAttribute(Line.CAPACITY_RATING_A_KEY));
    }
    if (!line.getAttribute(Line.CAPACITY_RATING_B_KEY).equals(ratingB)) {
      setComponentData(Line.CAPACITY_RATING_B_KEY, legacyid, name, line.getAttribute(Line.CAPACITY_RATING_B_KEY));
    }
    if (!line.getAttribute(Line.CAPACITY_RATING_C_KEY).equals(ratingC)) {
      setComponentData(Line.CAPACITY_RATING_C_KEY, legacyid, name, line.getAttribute(Line.CAPACITY_RATING_C_KEY));
    }

    if (!(charging == null && line.getAttribute(Line.LINE_CHARGING_KEY) == null)) {
      if (!line.getAttribute(Line.LINE_CHARGING_KEY).equals(charging)) {
        setComponentData(Line.LINE_CHARGING_KEY, legacyid, name, line.getAttribute(Line.LINE_CHARGING_KEY));
      }
    }
    
  }

  /**
   * Sync the shunt data stored in memory with the object in DEW
   * @throws DewException 
   * @throws NumberFormatException 
   */
  protected void syncCapacitor(ShuntCapacitor capacitor) throws NumberFormatException, DewException {
    DewLegacyId legacyid = capacitor.getAttribute(DewVariables.DEW_LEGACY_ID_KEY, DewLegacyId.class);    
    Object obj =  getComponentData(DewVariables.DEW_SHUNT_NAME_KEY, legacyid, null);  
    String name = obj == null ? "" : obj.toString();
    boolean isFailed = Integer.parseInt(getComponentData(Asset.IS_FAILED_KEY, legacyid, name).toString()) > 0;
    boolean status = Integer.parseInt(getComponentData(Asset.DESIRED_STATUS_KEY, legacyid, name).toString()) == 1;
    double x = Double.parseDouble(getComponentData(DewVariables.DEW_X_KEY, legacyid, name).toString());
    double y = Double.parseDouble(getComponentData(DewVariables.DEW_Y_KEY, legacyid, name).toString());
    Point point = new PointImpl(x,y);          
    boolean actualFailed = capacitor.getAttribute(DewVariables.DEW_AGGREGATE_FAILED_KEY, Boolean.class);
    boolean actualStatus = capacitor.getAttribute(DewVariables.DEW_AGGREGATE_STATUS_KEY, Boolean.class);
       
    if (actualFailed != isFailed) {
      setComponentData(Asset.IS_FAILED_KEY, legacyid, name, actualFailed ? 1 : 0);
    }    
    if (actualStatus != status) {
      setComponentData(Asset.DESIRED_STATUS_KEY, legacyid, name, actualStatus ? 1 : 0);
    }
  }

  /**
   * Sync the Battery data stored in memory with the object in DEW
   */
  protected void syncBattery(Battery battery) {
    // TODO
    throw new RuntimeException("Have not implemented");
  }
  
  /**
   * Does an id exist or not
   * @param id
   * @return
   */
  protected boolean hasId(DewLegacyId id) {
    return dewFileData.hasId(id);
  }
  
  /**
   * Get the component ids
   * @return
   */
  protected Collection<DewLegacyId> getComponentIds() {
    return dewFileData.getComponentIds();
  }

  /**
   * Remove some data
   * @param id
   */
  protected void remove(DewLegacyId id) {
    System.err.println("Nothing we are doing in the early stages should remove a component.  So I want to see if that ever happens.  When it does happen and this is correct, we can remove this sentence");
    System.err.println("The component is " + id);
    dewFileData.remove(id);
  }
  
  protected void addBus(Bus bus) {
    throw new RuntimeException("Have not implemented");    
  }
  
  protected void addTransformer(Transformer transformer) {
    throw new RuntimeException("Have not implemented");    
  }
  
  protected void addLine(Line line) {
    throw new RuntimeException("Have not implemented");    
  }
  
  protected void addLoad(Load load) {
    throw new RuntimeException("Have not implemented");    
  }
  
  protected void addGenerator(Generator generator) {
    throw new RuntimeException("Have not implemented");    
  }

  /**
   * Stops the DEW engine and closes the connection.
   * 
   * @throws DewException
   */
  public void close() throws DewException {
    engine().StopDewEngine();
    _engine = null;
    dewFileData.close();
  }

  @Override
  public void finalize() {
    if (_engine != null) {
      try {
        close();
      } 
      catch (DewException e) {
        e.printStackTrace();
      }
    }
  }
  
  /**
   * Provides a method of accessing the DewEngine instance that internally
   * checks to make sure a valid instance exists. If not, a DewException is
   * thrown. Otherwise, a reference to the engine instance is returned. An
   * exception would be thrown if, for example, a call to connect() was not made
   * before trying to open or run an analysis or if the call to connect()
   * resulted in a failure.
   * 
   * @return The engine instance.
   * @throws DewException
   */
  private DewEngineJNA engine() throws DewException {
    if (_engine == null)
      throw new DewException("No DewEngine instance found. You must first start the engine using Dew.connect().");
    else
      return _engine;
  }

  /**
   * get the connection string to the database
   * @return
   */
  public String getConnectionString() {
    return _connectionString;
  }
  
  /**
   * Get some data associated a component
   * @param key
   * @param dewid
   * @return
   * @throws DewException 
   */
  public Object getComponentData(String key, DewLegacyId dewid, String name) throws DewException {
    if (DewVariables.dataSources.get(key).equals(DewModelTypeEnum.TEXTFILE_TYPE)) {
      return getTextfileData(key, dewid);      
    }
    else if (DewVariables.dataSources.get(key).equals(DewModelTypeEnum.LIBRARY_TYPE)) {
      return getLibraryData(key, name, DewVariables.phaseSources.get(key));
    }
    else if (DewVariables.dataSources.get(key).equals(DewModelTypeEnum.TEXTFILE_FIRST_TYPE)) {
      Object obj = getTextfileData(key, dewid);
      if (obj == null) {
        obj = getLibraryData(key, name, DewVariables.phaseSources.get(key));
      }
      return obj;
    }
    else if (DewVariables.dataSources.get(key).equals(DewModelTypeEnum.COORDINATE_TYPE)) {
      return getCoordinateData(dewid);
    }    
    else if (DewVariables.dataSources.get(key).equals(DewModelTypeEnum.LINECHARGING_TYPE)) {
      return getLineChargingData(dewid);
    }    
    return null;
  }

  /**
   * Custom function for getting line charging data
   * @param dewid
   * @return
   */
  private String getLineChargingData(DewLegacyId dewid) {
    ArrayList<String> cmprtsCharging = null;
    ArrayList<ArrayList<String>> cmpxys = new ArrayList<ArrayList<String>>();
 
    int size = componentDataByIdSize(dewid);
    for (int i = 0; i < size; ++i) {
      ArrayList<String> data = getComponentDataById(dewid,i);
      if (data.get(0).equals(DewVariables.DEW_CMPXY_HEADER)) {
        cmpxys.add(data);
      }
      if (data.get(0).equals(DewVariables.DEW_CMPRTS_HEADER) && data.get(DewVariables.DEW_CMPRTS_INDICATOR_IDX).equals(DewVariables.DEW_IS_LINE_CHARGING_HEADER)) {
        cmprtsCharging = data;
      }
    }
    
    String charging = null;
    if (cmprtsCharging != null) {
      charging = cmprtsCharging.get(DewVariables.DEW_CHARGING_IDX);
    }
    return charging; 
  }
  
  /**
   * Special call to get geometry information
   * @param dewid
   * @return
   */
  private ArrayList<Point> getCoordinateData(DewLegacyId dewid) {
    ArrayList<String> cmp = null;
    ArrayList<ArrayList<String>> cmpxys = new ArrayList<ArrayList<String>>();

    int size = componentDataByIdSize(dewid);
    for (int i = 0; i < size; ++i) {
      ArrayList<String> data = getComponentDataById(dewid,i);
      if (data.get(DewVariables.DEW_HEADER_IDX).equals(DewVariables.DEW_CMP_HEADER)) {
        cmp = data;
      }
      if (data.get(0).equals(DewVariables.DEW_CMPXY_HEADER)) {
        cmpxys.add(data);
      }
    }

    ArrayList<Point> points = new ArrayList<Point>();
    double firstPointX =  Double.parseDouble(cmp.get(DewVariables.DEW_LINE_FIRST_X_IDX));
    double firstPointY =  Double.parseDouble(cmp.get(DewVariables.DEW_LINE_FIRST_Y_IDX));
    double lastPointX =  Double.parseDouble(cmp.get(DewVariables.DEW_LINE_LAST_X_IDX));
    double lastPointY =  Double.parseDouble(cmp.get(DewVariables.DEW_LINE_LAST_Y_IDX));
    points.add(new PointImpl(firstPointX, firstPointY));
    for (ArrayList<String> str : cmpxys) {
      double x = Double.parseDouble(str.get(DewVariables.DEW_LINE_INTERNAL_X_IDX));
      double y = Double.parseDouble(str.get(DewVariables.DEW_LINE_INTERNAL_Y_IDX));
      points.add(new PointImpl(x, y));

    }
    points.add(new PointImpl(lastPointX, lastPointY));
    return points;
  }
  
  /**
   * Get the library data
   * @param key
   * @param dewid
   * @return
   * @throws DewException 
   */
  private Object getLibraryData(String key, String dewid, int phase) throws DewException {
    if (dewid.length() <= 2) {
      return null;
    }
    
    if (DewVariables.libraryIndicies.get(key) == null) {
      return null;
    }
    
    String libraryTag = DewVariables.libraryIndicies.get(key).getRight();    
    int appTag = DewVariables.libraryIndicies.get(key).getLeft();    
    Map<String, DewResult> results = getResults(appTag, libraryTag);
    if (results.get(dewid.substring(1,dewid.length()-1)) != null) {
      return results.get(dewid.substring(1,dewid.length()-1)).getPhaseValue()[phase];      
    }
    else {
      if (!warningCache.contains(dewid)) {
        System.err.println("Warning: DEW id: " + dewid + " does not appear in the DEW model.  This id appears in the .dew file but does not appear in the list of UIDs provided by the DEW libraries.  Perhaps it is an orphaned component?");      
        warningCache.add(dewid);
      }
      else {
      }
      return null;      
    }    
  }
  
  /**
   * Get text file data
   * @param key
   * @param dewid
   * @return
   */
  private Object getTextfileData(String key, DewLegacyId dewid) {
    int size = componentDataByIdSize(dewid);    
    Pair<String, Integer> indicies = DewVariables.textFileIndicies.get(key);
    String header = indicies.getLeft();
    int index = indicies.getRight();
        
    ArrayList<String> row = null;
    for (int i = 0; i < size; ++i) {
      ArrayList<String> temp = getComponentDataById(dewid,i);
      if (temp.get(DewVariables.DEW_HEADER_IDX).equals(header)) {
        row = temp;
      }
    }
    
    if (row == null) {
      return null;
    }            
    return row.get(index);    
  }
  
  
  /**
   * Set some data associated with a component
   * @param key
   * @param dewid
   * @return
   * @throws DewException 
   */
  protected void setComponentData(String key, DewLegacyId dewid, String name, Object value) throws DewException {
    if (DewVariables.dataSources.get(key).equals(DewModelTypeEnum.TEXTFILE_TYPE) || DewVariables.dataSources.get(key).equals(DewModelTypeEnum.TEXTFILE_FIRST_TYPE)) {
      setTextfileData(key, dewid, value);      
    }
    else  {
      System.err.println("Warning: Not able to update DEW field " + key);
    }
  }
  
  
  /**
   * Get text file data
   * @param key
   * @param dewid
   * @return
   */
  private void setTextfileData(String key, DewLegacyId dewid, Object value) {
    int size = componentDataByIdSize(dewid);    
    Pair<String, Integer> indicies = DewVariables.textFileIndicies.get(key);
    String header = indicies.getLeft();
    int index = indicies.getRight();
        
    ArrayList<String> row = null;
    for (int i = 0; i < size; ++i) {
      ArrayList<String> temp = getComponentDataById(dewid, i);
      if (temp.get(DewVariables.DEW_HEADER_IDX).equals(header)) {
        row = temp;
      }
    }
    
    if (row == null) {
      return;
    }            
    row.set(index, value.toString());    
  }
  
  
  /**
   * Debugging function to get the data
   * @param id
   */
  public void printData(DewLegacyId id) {
    int size = componentDataByIdSize(id);
    
    for (int i = 0; i < size; ++i) {
      ArrayList<String> list = getComponentDataById(id,i);
      for (String str : list) {
        System.out.print(str + ", ");
      }
      System.out.println();
    }
    System.out.println();
  }
  
  /**
   * Run a simple test of the DEW engine dll and wrappers.
   * 
   * @param args
   */
  public static void main(String[] args) {
    testWrappedEngine();
  }

  /**
   * Performs a simple test of the DEW engine dll and wrappers.
   */
  public static void testWrappedEngine() {
    try {

      // run a simulation and get the results
      Dew dew = new Dew("a a DEW_DSN");
      dew.connect("dewlib/Distribution_Circuit_1.dew");
      
      dew.run(DewVariables.APPID_RADIAL_POWER_FLOW, 0);
      Map<String,DewResult> results = dew.getResults(DewVariables.APPID_RADIAL_POWER_FLOW, "VMagKv");

      //dew.run(DewVariables.APPID_LINE_IMPEDANCE, 0);
      Map<String,DewResult> results2 = dew.getResults(DewVariables.APPID_LINE_IMPEDANCE, DewVariables.ZDGRE);
      
      dew.close();

      // print the results
      for (DewResult r : results.values()) {
        System.out.println(r.getUID() + "\t" + r.getPhaseValue()[0] + "\t" + r.getPhaseValue()[1] + "\t" + r.getPhaseValue()[2]);
      }

      System.out.println("Next analysis");
      
      // print the results
      for (DewResult r : results2.values()) {
        System.out.println(r.getUID() + "\t" + r.getPhaseValue()[0] + "\t" + r.getPhaseValue()[1] + "\t" + r.getPhaseValue()[2]);
      }
      
      System.out.println("Run completed normally.");
    }
    catch (DewException e) {
      System.out.println("Run failed: " + e.getMessage());
      e.printStackTrace();
    }
  }
  
  /**
   * Clears the cached result
   */
  public void clearCache() {
    resultCache = new LinkedHashMap<Pair<Integer,String>, Map<String,DewResult>>();    
  }

  /**
   * Set the ptline database
   * @param ptlinespcFile
   */
  public void setPtlinespcDatabase(String ptlinespcFile) {
    _ptlinespcFile = ptlinespcFile;    
  }

  /**
   * get the ptline datbase
   * @return
   */
  public String getPtcapDatabase() {
    return _ptcapFile;
  }
  
  /**
   * Set the ptline database
   * @param ptlinespcFile
   */
  public void setPtcapDatabase(String ptcapFile) {
    _ptgapFile = ptcapFile;    
  }

  /**
   * get the ptline datbase
   * @return
   */
  public String getPtlinespcDatabase() {
    return _ptlinespcFile;
  }  


  /**
   * Get the system data by index
   * @param index
   * @return
   */
  private ArrayList<String> getSystemData(int index) {
    return _systemData.get(index);
  }

  /**
   * Add some system data
   * @param str
   */
  private void addSystemData(ArrayList<String> str) {
    _systemData.add(str);
  }
  
  /**
   * Get component data by Id
   * @param id
   * @param index
   * @return
   */
  private ArrayList<String> getComponentDataById(DewLegacyId id, int index) {
    return dewFileData.getComponentDataById(id, index);
  }
  
  /**
   * get the system data size
   * @return
   */
  private int systemDataSize() {
    return _systemData.size();
  }

  /**
   * get component data by size
   * @return
   */
  private int componentDataByIdSize() {
    return dewFileData.componentDataByIdSize();
  }

  /**
   * get a hash map size
   * @param id
   * @return
   */
  private int componentDataByIdSize(DewLegacyId id) {
    return dewFileData.componentDataByIdSize(id);
  }



  
}
