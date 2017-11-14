package gov.lanl.micot.infrastructure.ep.simulate.powerworld;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import gov.lanl.micot.infrastructure.ep.io.powerworld.PowerworldIOConstants;
import gov.lanl.micot.infrastructure.ep.io.powerworld.PowerworldModelFile;
import gov.lanl.micot.infrastructure.ep.model.Bus;
import gov.lanl.micot.infrastructure.ep.model.DCTwoTerminalLine;
import gov.lanl.micot.infrastructure.ep.model.DCVoltageSourceLine;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.ep.model.Generator;
import gov.lanl.micot.infrastructure.ep.model.powerworld.PowerworldModel;
import gov.lanl.micot.infrastructure.ep.model.powerworld.PowerworldModelConstants;
import gov.lanl.micot.infrastructure.ep.model.powerworld.PowerworldModelFactory;
import gov.lanl.micot.infrastructure.ep.simulate.ElectricPowerSimulatorImpl;
import gov.lanl.micot.infrastructure.model.Component;
import gov.lanl.micot.infrastructure.model.FlowConnection;
import gov.lanl.micot.util.collection.Pair;
import gov.lanl.micot.util.collection.Triple;
import gov.lanl.micot.util.io.dcom.ComDataObject;
import gov.lanl.micot.util.io.dcom.ComObject;
import gov.lanl.micot.util.io.dcom.ComObjectFactory;
import gov.lanl.micot.util.io.dcom.ComObjectUtilities;

/**
 * Specific instantation of the Powerworld simulator
 * 
 * @author Russell Bent
 */
public class PowerworldSimulator extends ElectricPowerSimulatorImpl {

  private boolean debug = false;
  private String preOutputModelFile  = "preTemp.pwb";
  private String postOutputModelFile = "postTemp.pwb";
  private String tempModelFile       = "temp.pwb"; 
  
  private boolean checkPhaseShifters = false; 
  private boolean checkShunts = false;
  private boolean checkSVC = false; 
  private boolean checkTaps = false; 
  private boolean checkArea = false; 
  private boolean checkMVarLimits = false; 
  private boolean checkMWLimits = false;   
  
  /**
   * Constructor
   * 
   * @param nextGenerationPFWFilename
   */
  protected PowerworldSimulator() {
    super();
  }

  @Override
  protected SimulatorSolveState simulateModel(ElectricPowerModel model) {
  	for (Bus bus : model.getBuses()) {
			ElectricPowerNode node = model.getNode(bus);
  		if (!bus.getStatus()) {
  			for (Component asset : node.getComponents(Component.class)) {
  				asset.setStatus(false);
  			}
  			
  			for (FlowConnection connection : model.getFlowConnections(node)) {
  			  connection.setStatus(false);
  			}  			
  		}  		
  	}

  	// make sure we are in a power world regime
  	PowerworldModelFactory factory = PowerworldModelFactory.getInstance();
  	PowerworldModel powerWorldModel = factory.constructPowerworldModel(model);
  	ComObject powerworld = powerWorldModel.getPowerworld();
	  	 
  	// turn off simulator control logic
  	String[] fields = new String[] { PowerworldIOConstants.SIMULATION_OPTION_CHECK_SHUNTS, 
  	                                 PowerworldIOConstants.SIMULATION_OPTION_PHASE_SHIFTERS,
  	                                 PowerworldIOConstants.SIMULATION_OPTION_CHECK_SVC,
                                     PowerworldIOConstants.SIMULATION_OPTION_CHECK_TAPS,
                                     PowerworldIOConstants.SIMULATION_OPTION_CHECK_AREA,
                                     PowerworldIOConstants.SIMULATION_OPTION_CHECK_MVAR_LIMITS,
                                     PowerworldIOConstants.SIMULATION_OPTION_CHECK_MW_LIMITS
  	                               };
  	
  	String[] values = new String[] {    checkShunts == false ? PowerworldIOConstants.SIMULATION_OPTION_NO : PowerworldIOConstants.SIMULATION_OPTION_YES,
  	                                    checkPhaseShifters == false ? PowerworldIOConstants.SIMULATION_OPTION_NO : PowerworldIOConstants.SIMULATION_OPTION_YES, 
  	                                    checkSVC == false ? PowerworldIOConstants.SIMULATION_OPTION_NO : PowerworldIOConstants.SIMULATION_OPTION_YES, 
  	                                    checkTaps == false ? PowerworldIOConstants.SIMULATION_OPTION_NO : PowerworldIOConstants.SIMULATION_OPTION_YES, 
  	                                    checkArea == false ? PowerworldIOConstants.SIMULATION_OPTION_NO : PowerworldIOConstants.SIMULATION_OPTION_YES, 
  	                                    checkMVarLimits == true ? PowerworldIOConstants.SIMULATION_OPTION_NO : PowerworldIOConstants.SIMULATION_OPTION_YES, 
  	                                    checkMWLimits == false ? PowerworldIOConstants.SIMULATION_OPTION_NO : PowerworldIOConstants.SIMULATION_OPTION_YES, 
  	                               };

  	
  	String command = PowerworldIOConstants.createCreateDataCommand(PowerworldIOConstants.SIM_SOLUTION_OPTIONS, fields, values);
  	powerworld.callData(PowerworldIOConstants.RUN_SCRIPT_COMMAND, command);
  
    if (debug) {
      PowerworldModelFile mf = new PowerworldModelFile();
      try {
        mf.saveFile(preOutputModelFile, powerWorldModel);
      }
      catch (IOException e) {
        e.printStackTrace();
      }
    }
  	    
    // the setting of data creates a feedback loop with powerworld where the same
    // data is updated. This slows things down and also resets some simulation results
    // to base case
    powerWorldModel.removeModelListener(powerWorldModel);    
    
  	// go into run mode
  	String scriptcommand = PowerworldIOConstants.POWERFLOW_MODE;
  	ComDataObject object = powerworld.callData(PowerworldIOConstants.RUN_SCRIPT_COMMAND, scriptcommand);
  	ArrayList<ComDataObject> o = object.getArrayValue();
    String errorString = o.get(0).getStringValue();
    if (!errorString.equals("")) {
      System.err.println("Error getting into run mode: " + errorString);                
    }

  	// run the power flow solve
    SimulatorSolveState s = SimulatorSolveState.CONVERGED_SOLUTION;
    boolean ok = simulateACNormal(powerworld);

    if (!ok) {
      System.err.println("Trying robust solver");
      s = SimulatorSolveState.ERROR_SOLUTION;
      ok = simulateACRobust(powerworld, powerWorldModel);
    }
    else {
      s = SimulatorSolveState.CONVERGED_SOLUTION;      
    }
    
    if (!ok) {
      System.err.println("Trying file I/O");
      s = SimulatorSolveState.ERROR_SOLUTION;
      ok = simulateACFileIO(powerworld,powerWorldModel);
    }
    else {
      s = SimulatorSolveState.CONVERGED_SOLUTION;      
    }
    
    if (ok) {
      s = SimulatorSolveState.CONVERGED_SOLUTION;
    }
    
    // get the flows and losses
    fields = new String[]{
    		PowerworldIOConstants.BRANCH_BUS_FROM_NUM, 
    		PowerworldIOConstants.BRANCH_BUS_TO_NUM, 
    		PowerworldIOConstants.BRANCH_NUM, 
        PowerworldIOConstants.BRANCH_REACTIVE_LOSS, 
        PowerworldIOConstants.BRANCH_REAL_LOSS, 
        PowerworldIOConstants.BRANCH_FROM_REACTIVE_FLOW, 
        PowerworldIOConstants.BRANCH_TO_REACTIVE_FLOW, 
        PowerworldIOConstants.BRANCH_FROM_REAL_FLOW, 
        PowerworldIOConstants.BRANCH_TO_REAL_FLOW, 
        PowerworldIOConstants.BRANCH_STATUS};
    
    ArrayList<ElectricPowerFlowConnection> branches = new ArrayList<ElectricPowerFlowConnection>();
    branches.addAll(model.getTransformers());
    branches.addAll(model.getLines());
    
    for (ElectricPowerFlowConnection line : branches) {
    	Triple<Integer,Integer,Integer> legacyid = powerWorldModel.getConnectionId(line);
      values = new String[] {legacyid.getOne()+"", legacyid.getTwo()+"", legacyid.getThree()+"", "", "", "", "", "", "", ""};
          
      ComDataObject dataObject = powerworld.callData(PowerworldIOConstants.GET_PARAMETERS_SINGLE_ELEMENT, PowerworldIOConstants.BRANCH, fields, values);
      ArrayList<ComDataObject> branchData = dataObject.getArrayValue();
      errorString = branchData.get(0).getStringValue();
      if (!errorString.equals("")) {
        System.err.println("Error getting powerworld branch solution data: " + errorString);                
      }
      
      ArrayList<ComDataObject> bData = branchData.get(1).getArrayValue();                       
      String reactiveLossStr = bData.get(3).getStringValue();
      String realLossStr = bData.get(4).getStringValue();
      String reactiveFromStr = bData.get(5).getStringValue();
      String reactiveToStr = bData.get(6).getStringValue();    
      String realFromStr = bData.get(7).getStringValue();
      String realToStr = bData.get(8).getStringValue();
      String status = bData.get(9).getStringValue();
      
      double reactiveLoss = Double.parseDouble(reactiveLossStr);
      double realLoss = Double.parseDouble(realLossStr);
      double reactiveFrom = Double.parseDouble(reactiveFromStr);
      double reactiveTo = Double.parseDouble(reactiveToStr);
      double realFrom = Double.parseDouble(realFromStr);
      double realTo = Double.parseDouble(realToStr);
      double mwFlow = Math.max(realFrom, realTo);
      double mVarFlow = Math.max(reactiveFrom, reactiveTo);
      
      line.setMWFlow(mwFlow);
      line.setMVarFlow(mVarFlow);
      line.setRealLoss(realLoss);
      line.setReactiveLoss(reactiveLoss);
      line.setAttribute(ElectricPowerFlowConnection.MVAR_FLOW_SIDE1_KEY, reactiveFrom);
      line.setAttribute(ElectricPowerFlowConnection.MVAR_FLOW_SIDE2_KEY, reactiveTo);
      line.setAttribute(ElectricPowerFlowConnection.MW_FLOW_SIDE1_KEY, realFrom);
      line.setAttribute(ElectricPowerFlowConnection.MW_FLOW_SIDE2_KEY, realTo);
      line.setStatus(status.toLowerCase().equals(PowerworldIOConstants.BRANCH_CLOSED));
    }

    // get the bus data
    fields = new String[]{PowerworldIOConstants.BUS_NUM, 
    		PowerworldIOConstants.BUS_PU_VOLTAGE, 
    		PowerworldIOConstants.BUS_ANGLE}; 

    for (Bus bus : model.getBuses()) {
    	Object id = powerWorldModel.getBusId(bus);
    	values = null;
    	String type = null;
    	
    	if (bus.getAttribute(PowerworldModelConstants.POWERWORLD_BUS_CATEGORY_KEY,String.class).equals(PowerworldModelConstants.POWER_WORLD_DC_BUS_CAT)) {
        type = PowerworldIOConstants.DC_BUS;
        values = new String[] {((Pair)id).getOne()+"", ((Pair)id).getTwo()+"", ""};
        fields = new String[]{PowerworldIOConstants.DC_BUS_NUM, 
            PowerworldIOConstants.DC_RECORD_NUM, 
            PowerworldIOConstants.DC_BUS_VOLTAGE_KV};         
    	}
    	else {
        fields = new String[]{PowerworldIOConstants.BUS_NUM, 
            PowerworldIOConstants.BUS_PU_VOLTAGE, 
            PowerworldIOConstants.BUS_ANGLE};     	  
    	  values = new String[] {id+"", "", ""};
    	  type = PowerworldIOConstants.BUS;
    	}
      
      ComDataObject dataObject = powerworld.callData(PowerworldIOConstants.GET_PARAMETERS_SINGLE_ELEMENT, type, fields, values);
      ArrayList<ComDataObject> busData = dataObject.getArrayValue();
      errorString = busData.get(0).getStringValue();
      if (!errorString.equals("")) {
        System.err.println("Error getting powerworld bus results: " + errorString);                
      }
      
      ArrayList<ComDataObject> bData = busData.get(1).getArrayValue();                       

      if (bus.getAttribute(PowerworldModelConstants.POWERWORLD_BUS_CATEGORY_KEY,String.class).equals(PowerworldModelConstants.POWER_WORLD_DC_BUS_CAT)) {
        String kvString = bData.get(2).getStringValue();              
        double kv = Double.parseDouble(kvString.trim());        
        bus.setVoltagePU(kv);
      }
      else {
        String puString = bData.get(1).getStringValue();
        String angleString = bData.get(2).getStringValue();      
        double pu = Double.parseDouble(puString.trim());
        double angle = Double.parseDouble(angleString.trim());     
        bus.setPhaseAngle(angle);
        bus.setVoltagePU(pu);    
      }
    }
    
    // get the generator data
    fields = new String[]{PowerworldIOConstants.BUS_NUM, 
    		PowerworldIOConstants.GEN_NUM, 
    		PowerworldIOConstants.GEN_MVAR, 
    		PowerworldIOConstants.GEN_MW,
    		PowerworldIOConstants.GEN_VOLTAGE}; 
    
    for (Generator generator : model.getGenerators()) {
      Bus bus = model.getNode(generator).getBus();
    	Pair<Integer,Integer> id = powerWorldModel.getGeneratorId(generator);
      values = new String[] {id.getLeft()+"", id.getRight()+"", "", "", ""};
      
      ComDataObject dataObject = powerworld.callData(PowerworldIOConstants.GET_PARAMETERS_SINGLE_ELEMENT, PowerworldIOConstants.GENERATOR, fields, values);
      ArrayList<ComDataObject> genData = dataObject.getArrayValue();
      errorString = genData.get(0).getStringValue();
      if (!errorString.equals("")) {
        System.err.println("Error getting powerworld generator simulation results: " + errorString);                
      }

      ArrayList<ComDataObject> gData = genData.get(1).getArrayValue();                       
      String mvarString = gData.get(2).getStringValue();
      String mwString = gData.get(3).getStringValue();
      String voltageString = gData.get(4).getStringValue();

      double voltage = Double.parseDouble(voltageString.trim());
      double mvar = Double.parseDouble(mvarString.trim());
      double mw = Double.parseDouble(mwString.trim());
      generator.setActualRealGeneration(mw);
      generator.setDesiredRealGeneration(mw);
      generator.setActualReactiveGeneration(mvar);
      generator.setDesiredReactiveGeneration(mvar);
    }

    // get the flows on Voltage Source lines
    fields = new String[]{
        PowerworldIOConstants.VOLTAGE_SOURCE_NAME, 
        PowerworldIOConstants.VOLTAGE_SOURCE_FROM_FLOW_MW, 
        PowerworldIOConstants.VOLTAGE_SOURCE_TO_FLOW_MW, 
        PowerworldIOConstants.VOLTAGE_SOURCE_FROM_FLOW_MVAR, 
        PowerworldIOConstants.VOLTAGE_SOURCE_TO_FLOW_MVAR, 
        PowerworldIOConstants.VOLTAGE_SOURCE_STATUS};
    
    for (DCVoltageSourceLine line : model.getDCVoltageSourceLines()) {
      String name = line.getAttribute(DCVoltageSourceLine.NAME_KEY, String.class);
      values = new String[] { name, "", "", "", "", "" };
          
      ComDataObject dataObject = powerworld.callData(PowerworldIOConstants.GET_PARAMETERS_SINGLE_ELEMENT, PowerworldIOConstants.DC_VOLTAGE_SOURCE, fields, values);
      ArrayList<ComDataObject> branchData = dataObject.getArrayValue();
      errorString = branchData.get(0).getStringValue();
      if (!errorString.equals("")) {
        System.err.println("Error getting powerworld two terminal solution data: " + errorString);                
      }
      
      ArrayList<ComDataObject> bData = branchData.get(1).getArrayValue();                       
      String fromRealStr = bData.get(1).getStringValue();
      String toRealStr = bData.get(2).getStringValue();
      String fromReactiveStr = bData.get(3).getStringValue();
      String toReactiveStr = bData.get(4).getStringValue();    
      String status = bData.get(5).getStringValue();
      
      double reactiveFrom = Double.parseDouble(fromReactiveStr);
      double reactiveTo = Double.parseDouble(toReactiveStr);
      double realFrom = Double.parseDouble(fromRealStr);
      double realTo = Double.parseDouble(toRealStr);
      double mwFlow = Math.max(realFrom, realTo);
      double mVarFlow = Math.max(reactiveFrom, reactiveTo);
      
      line.setMWFlow(mwFlow);
      line.setMVarFlow(mVarFlow);
      line.setAttribute(ElectricPowerFlowConnection.MVAR_FLOW_SIDE1_KEY, reactiveFrom);
      line.setAttribute(ElectricPowerFlowConnection.MVAR_FLOW_SIDE2_KEY, reactiveTo);
      line.setAttribute(ElectricPowerFlowConnection.MW_FLOW_SIDE1_KEY, realFrom);
      line.setAttribute(ElectricPowerFlowConnection.MW_FLOW_SIDE2_KEY, realTo);
      line.setStatus(status.toLowerCase().equals(PowerworldIOConstants.VOLTAGE_SOURCE_CLOSED));
    }
    
    // get the flows on Two Terminal lines
    fields = new String[]{
        PowerworldIOConstants.TWO_TERMINAL_BUS_FROM_NUM, 
        PowerworldIOConstants.TWO_TERMINAL_BUS_TO_NUM, 
        PowerworldIOConstants.TWO_TERMINAL_NUM, 
        PowerworldIOConstants.TWO_TERMINAL_INVERTER_REAL_FLOW, 
        PowerworldIOConstants.TWO_TERMINAL_INVERTER_REACTIVE_FLOW, 
        PowerworldIOConstants.TWO_TERMINAL_RECTIFIER_REAL_FLOW, 
        PowerworldIOConstants.TWO_TERMINAL_RECTIFIER_REACTIVE_FLOW, 
        PowerworldIOConstants.TWO_TERMINAL_STATUS};
    
    for (DCTwoTerminalLine line : model.getDCTwoTerminalLines()) {
      Triple<Integer,Integer,Integer> legacyid = powerWorldModel.getConnectionId(line);
      values = new String[] {
          legacyid.getOne()+"", 
          legacyid.getTwo()+"", legacyid.getThree()+"", 
          "", "", "", "", ""};
          
      ComDataObject dataObject = powerworld.callData(PowerworldIOConstants.GET_PARAMETERS_SINGLE_ELEMENT, PowerworldIOConstants.DC_TWO_TERMINAL, fields, values);
      ArrayList<ComDataObject> branchData = dataObject.getArrayValue();
      errorString = branchData.get(0).getStringValue();
      if (!errorString.equals("")) {
        System.err.println("Error getting powerworld two terminal solution data: " + errorString);                
      }
      
      ArrayList<ComDataObject> bData = branchData.get(1).getArrayValue();                       
      String inverterRealStr = bData.get(3).getStringValue();
      String inverterReactiveStr = bData.get(4).getStringValue();
      String rectifierRealStr = bData.get(5).getStringValue();
      String rectifierReactiveStr = bData.get(6).getStringValue();    
      String status = bData.get(7).getStringValue();
      
      double reactiveFrom = Double.parseDouble(inverterReactiveStr);
      double reactiveTo = Double.parseDouble(rectifierReactiveStr);
      double realFrom = Double.parseDouble(inverterRealStr);
      double realTo = Double.parseDouble(rectifierRealStr);
      double mwFlow = Math.max(realFrom, realTo);
      double mVarFlow = Math.max(reactiveFrom, reactiveTo);
      
      line.setMWFlow(mwFlow);
      line.setMVarFlow(mVarFlow);
      line.setAttribute(ElectricPowerFlowConnection.MVAR_FLOW_SIDE1_KEY, reactiveFrom);
      line.setAttribute(ElectricPowerFlowConnection.MVAR_FLOW_SIDE2_KEY, reactiveTo);
      line.setAttribute(ElectricPowerFlowConnection.MW_FLOW_SIDE1_KEY, realFrom);
      line.setAttribute(ElectricPowerFlowConnection.MW_FLOW_SIDE2_KEY, realTo);
      line.setStatus(status.toLowerCase().equals(PowerworldIOConstants.TWO_TERMINAL_CLOSED));
    }

    // reactivate the listener for going back to powerworld
    powerWorldModel.addModelListener(powerWorldModel);        
    return s;
  }

  /**
   * Just testing out some calls...
   * @param args
   */
  public static void main(String[] args) {
	  ComObjectFactory factory = ComObjectUtilities.getDefaultFactory();

	  ComObject comObject = factory.createComObject("pwrworld.SimulatorAuto");
	  comObject.callData("OpenCase", "C:\\Program Files (x86)\\PowerWorld\\Simulator18\\Sample Cases\\b7flat.pwb");    
	  ComDataObject busesObject = comObject.callData("ListOfDevices", "bus", "");
	  ArrayList<ComDataObject> buses = busesObject.getArrayValue();
    String errorString = buses.get(0).getStringValue();
    if (errorString.equals("")) {
      ArrayList<ComDataObject> data = buses.get(1).getArrayValue();
      ArrayList<Integer> ids = data.get(0).getIntArrayValue();
            
      System.out.println("Bus ids");
      for (int i = 0; i < ids.size(); ++i) {
        System.out.println(ids.get(i));
      }
      System.out.println();
      
      
      System.out.println("Bus Data");
      for (int i = 0; i < ids.size(); ++i) {
        ComDataObject dataObject = comObject.callData("GetParametersSingleElement", "bus", new String[]{"pwBusNum", "pwBusName", "pwAreaName", "pwBusPUVolt", "pwBusAngle"}, new String[] {ids.get(i)+"", "", "", "", ""});
        ArrayList<ComDataObject> busData = dataObject.getArrayValue();
        String errorString2 = busData.get(0).getStringValue();
        if (errorString2.equals("")) {
          ArrayList<ComDataObject> bData = busData.get(1).getArrayValue();                       
          String id = bData.get(0).getStringValue();
          System.out.print(id + " ");
          String name = bData.get(1).getStringValue();
          System.out.print(name + " ");
          String areaName = bData.get(2).getStringValue();
          System.out.print(areaName + " ");
          String pu = bData.get(3).getStringValue();
          System.out.print(pu + " ");
          String angle = bData.get(4).getStringValue();
          System.out.print(angle + " ");        
          System.out.println();
        }
        else {
          System.out.println("Error: " + errorString2);                
        }
      }      
    }
    else {
      System.out.println("Error: " + errorString);      
    }
	  
  }
  
  /**
   * The normal way of running the AC simulator of Powerworld
   * @return
   */
  private boolean simulateACNormal(ComObject powerworld) {
    String scriptcommand = PowerworldIOConstants.POWER_FLOW_COMMAND_RECT_NEWTON;
    ComDataObject object = powerworld.callData(PowerworldIOConstants.RUN_SCRIPT_COMMAND, scriptcommand);
    ArrayList<ComDataObject> o = object.getArrayValue();
    String errorString = o.get(0).getStringValue();
    
    if (!errorString.equals("")) {
      System.err.println("PowerFlow Simulation Failed: " + errorString);
    }
    return errorString.equals("");    
  }

  /**
   * Robust way of running the AC simulator of Powerworld
   * @param powerworld
   * @return
   */
  private boolean simulateACRobust(ComObject powerworld, PowerworldModel powerWorldModel) {
    powerworld.callData(PowerworldIOConstants.SAVE_CASE, tempModelFile, PowerworldIOConstants.PWB, true); 
    File file = new File(tempModelFile);
    file.delete();

    
    // drop into robust solving if first solve fails, and do a flat restart
    String scriptcommand = PowerworldIOConstants.RESET_TO_FLAT_START;
    ComDataObject object = powerworld.callData(PowerworldIOConstants.RUN_SCRIPT_COMMAND, scriptcommand);
    ArrayList<ComDataObject> o = object.getArrayValue();
    String errorString = o.get(0).getStringValue();
    if (!errorString.equals("")) {
      System.err.println("Error running flat start: " + errorString);
    }
          
    scriptcommand = PowerworldIOConstants.POWER_FLOW_COMMAND_RECT_NEWTON;
    object = powerworld.callData(PowerworldIOConstants.RUN_SCRIPT_COMMAND, scriptcommand);
    o = object.getArrayValue();
    errorString = o.get(0).getStringValue();
    if (!errorString.equals("")) {
      System.err.println("Robust PowerFlow Simulation Failed: " + errorString);
      
      if (debug) {
        PowerworldModelFile mf = new PowerworldModelFile();
        try {
          mf.saveFile(postOutputModelFile, powerWorldModel);
        }
        catch (IOException e) {
          e.printStackTrace();
        }
      }
      return false;
    }
    return true;
  }

  /**
   * Sometimes a save and reload of a model is enough to make everything happy...
   * @param powerworld
   * @return
   */
  private boolean simulateACFileIO(ComObject powerworld, PowerworldModel powerWorldModel) {
    ComDataObject object = powerworld.callData(PowerworldIOConstants.SAVE_CASE, tempModelFile, PowerworldIOConstants.PWB, true); 
    ArrayList<ComDataObject> o = object.getArrayValue();
    String errorString = o.get(0).getStringValue();
    if (!errorString.equals("")) {
      System.err.println("Error Saving Case: " + errorString);
    }

    object = powerworld.callData(PowerworldIOConstants.CLOSE_CASE);    
    o = object.getArrayValue();
    errorString = o.get(0).getStringValue();
    if (!errorString.equals("")) {
      System.err.println("Error Closing Case: " + errorString);
    }

    object = powerworld.callData(PowerworldIOConstants.OPEN_CASE, tempModelFile);    
    o = object.getArrayValue();
    errorString = o.get(0).getStringValue();
    if (!errorString.equals("")) {
      System.err.println("Error Opening Case: " + errorString);
    }

    File file = new File(tempModelFile);
    file.delete();

    String scriptcommand = PowerworldIOConstants.POWERFLOW_MODE;
    powerworld.callData(PowerworldIOConstants.RUN_SCRIPT_COMMAND, scriptcommand);
    scriptcommand = PowerworldIOConstants.POWER_FLOW_COMMAND_RECT_NEWTON;
    object = powerworld.callData(PowerworldIOConstants.RUN_SCRIPT_COMMAND, scriptcommand);
    o = object.getArrayValue();
    errorString = o.get(0).getStringValue();
    if (!errorString.equals("")) {
      System.err.println("File I/O PowerFlow Simulation Failed: " + errorString);
    }
      
    return errorString.equals("");    
  }

  /**
   * Set the check phase shifter parameter
   * @param checkPhaseShifters
   */
  public void setCheckPhaseShifters(boolean checkPhaseShifters) {
    this.checkPhaseShifters = checkPhaseShifters;
  }

  /**
   * Set the check shunts parameter
   * @param checkShunts
   */
  public void setCheckShunts(boolean checkShunts) {
    this.checkShunts = checkShunts;
  }

  /**
   * Set the check SVC parameter
   * @param checkSVC
   */
  public void setCheckSVC(boolean checkSVC) {
    this.checkSVC = checkSVC;
  }

  /**
   * Set the check taps parameter
   * @param checkTaps
   */
  public void setCheckTaps(boolean checkTaps) {
    this.checkTaps = checkTaps;
  }

  /**
   * Set the check area parameter
   * @param checkArea
   */
  public void setCheckArea(boolean checkArea) {
    this.checkArea = checkArea;
  }

  /**
   * Set the check mvar limits parameter
   * @param checkMVarLimits
   */
  public void setCheckMVarLimits(boolean checkMVarLimits) {
    this.checkMVarLimits = checkMVarLimits;
  }

  /**
   * Set the check mw limits parameter
   * @param checkMWLimits
   */
  public void setCheckMWLimits(boolean checkMWLimits) {
    this.checkMWLimits = checkMWLimits;
  }

  
  
  
}
