package gov.lanl.micot.infrastructure.ep.simulate.opendss;

import java.util.ArrayList;
import java.util.HashMap;

import gov.lanl.micot.infrastructure.ep.io.opendss.OpenDSSIOConstants;
import gov.lanl.micot.infrastructure.ep.model.Bus;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.opendss.OpenDSSModel;
import gov.lanl.micot.infrastructure.ep.model.opendss.OpenDSSModelConstants;
import gov.lanl.micot.infrastructure.ep.simulate.ElectricPowerSimulatorImpl;
import gov.lanl.micot.util.io.dcom.ComObject;
import gov.lanl.micot.util.io.dcom.ComObjectFactory;
import gov.lanl.micot.util.io.dcom.ComObjectUtilities;

/**
 * Specific instantation of the OpenDss simulator
 * 
 * @author Russell Bent
 */
public class OpenDSSSimulator extends ElectricPowerSimulatorImpl {
  
  /**
   * Constructor
   * 
   * @param nextGenerationPFWFilename
   */
  protected OpenDSSSimulator() {
    super();
  }

  @Override
  protected SimulatorSolveState simulateModel(ElectricPowerModel emodel) {
    if (!(emodel instanceof OpenDSSModel)) {   
      throw new RuntimeException("Error: Cannot currently convert an arbitrary model into an OpenDSS Model");
    }   
    
    OpenDSSModel model = (OpenDSSModel)emodel;
    ComObject openDSS = model.getOpenDSS();
        
    //try {
      model.syncModel();
      ComObject circuit = openDSS.call(OpenDSSIOConstants.MODEL);
      ComObject solution = circuit.call(OpenDSSIOConstants.SOLUTION);
      solution.call(OpenDSSIOConstants.SOLVE);
      
      //circuit.getDoubleArray("AllNodeVmagPUByPhase", new Object[] {1});
      
      HashMap<String, Bus> buses = new HashMap<String, Bus>();
      for (Bus bus : model.getBuses()) {
        buses.put(bus.getAttribute(OpenDSSModelConstants.OPENDSS_LEGACY_ID_KEY, String.class), bus); 
      }
      
      // get the voltage magnitudes and phase angles
      int numBuses = circuit.getInteger(OpenDSSIOConstants.NUMBER_OF_BUSES);    
      for (int i = 0; i < numBuses; ++i) {
        ComObject localbus = circuit.call(OpenDSSIOConstants.BUSES, i);
        String name = localbus.getString(OpenDSSIOConstants.BUS_NAME, new Object[0]);  
        Bus bus = buses.get(name);
      
        ArrayList<Double> voltages = localbus.getDoubleArray(OpenDSSIOConstants.BUS_PU_VOLTAGES);
        double voltageMagA = voltages.get(0);
        double phaseAngleA = voltages.get(1);
        double voltageMagB = voltages.get(2);
        double phaseAngleB = voltages.get(3);
        double voltageMagC = voltages.get(4);
        double phaseAngleC = voltages.get(5);

        
        bus.setAttribute(Bus.VOLTAGE_PU_A_KEY, voltageMagA);
        bus.setAttribute(Bus.VOLTAGE_PU_B_KEY, voltageMagB);
        bus.setAttribute(Bus.VOLTAGE_PU_C_KEY, voltageMagC);
        bus.setAttribute(Bus.PHASE_ANGLE_A_KEY, phaseAngleA);
        bus.setAttribute(Bus.PHASE_ANGLE_B_KEY, phaseAngleB);
        bus.setAttribute(Bus.PHASE_ANGLE_C_KEY, phaseAngleC);
        bus.setVoltagePU(voltageMagA);
        bus.setPhaseAngle(phaseAngleA);
      }
                
      // generation mw and mvar
     /* for (Generator generator : model.getGenerators()) {
        int legacyid = generator.getAttribute(DewVariables.DEW_LEGACY_ID_KEY, Integer.class);
        String name = generator.getAttribute(Generator.NAME_KEY, String.class);      
        double mwA = Double.parseDouble(dew.getComponentData(Generator.ACTUAL_REAL_GENERATION_A_KEY, legacyid, name).toString());
        double mwB = Double.parseDouble(dew.getComponentData(Generator.ACTUAL_REAL_GENERATION_B_KEY, legacyid, name).toString());
        double mwC = Double.parseDouble(dew.getComponentData(Generator.ACTUAL_REAL_GENERATION_C_KEY, legacyid, name).toString());
        double mvarA = Double.parseDouble(dew.getComponentData(Generator.ACTUAL_REACTIVE_GENERATION_A_KEY, legacyid, name).toString());
        double mvarB = Double.parseDouble(dew.getComponentData(Generator.ACTUAL_REACTIVE_GENERATION_B_KEY, legacyid, name).toString());
        double mvarC = Double.parseDouble(dew.getComponentData(Generator.ACTUAL_REACTIVE_GENERATION_C_KEY, legacyid, name).toString());
      
        generator.setAttribute(Generator.ACTUAL_REAL_GENERATION_A_KEY, mwA);
        generator.setAttribute(Generator.ACTUAL_REAL_GENERATION_B_KEY, mwB);
        generator.setAttribute(Generator.ACTUAL_REAL_GENERATION_C_KEY, mwC);
        generator.setAttribute(Generator.ACTUAL_REACTIVE_GENERATION_A_KEY, mvarA);
        generator.setAttribute(Generator.ACTUAL_REACTIVE_GENERATION_B_KEY, mvarB);
        generator.setAttribute(Generator.ACTUAL_REACTIVE_GENERATION_C_KEY, mvarC);
        generator.setActualRealGeneration(mwA + mwB + mwC);
        generator.setActualReactiveGeneration(mvarA + mvarB + mvarC);
      }
        
      // load mw and mvar
      for (Load load : model.getLoads()) {
        int legacyid = load.getAttribute(DewVariables.DEW_LEGACY_ID_KEY, Integer.class);
        String name = load.getAttribute(Load.LOAD_NAME_KEY, String.class);      
        double mwA = Double.parseDouble(dew.getComponentData(Load.ACTUAL_REAL_LOAD_A_KEY, legacyid, name).toString());
        double mwB = Double.parseDouble(dew.getComponentData(Load.ACTUAL_REAL_LOAD_B_KEY, legacyid, name).toString());
        double mwC = Double.parseDouble(dew.getComponentData(Load.ACTUAL_REAL_LOAD_C_KEY, legacyid, name).toString());
        double mvarA = Double.parseDouble(dew.getComponentData(Load.ACTUAL_REACTIVE_LOAD_A_KEY, legacyid, name).toString());
        double mvarB = Double.parseDouble(dew.getComponentData(Load.ACTUAL_REACTIVE_LOAD_B_KEY, legacyid, name).toString());
        double mvarC = Double.parseDouble(dew.getComponentData(Load.ACTUAL_REACTIVE_LOAD_C_KEY, legacyid, name).toString());
      
        load.setAttribute(Load.ACTUAL_REAL_LOAD_A_KEY, mwA);
        load.setAttribute(Load.ACTUAL_REAL_LOAD_B_KEY, mwB);
        load.setAttribute(Load.ACTUAL_REAL_LOAD_C_KEY, mwC);
        load.setAttribute(Load.ACTUAL_REACTIVE_LOAD_A_KEY, mvarA);
        load.setAttribute(Load.ACTUAL_REACTIVE_LOAD_B_KEY, mvarB);
        load.setAttribute(Load.ACTUAL_REACTIVE_LOAD_C_KEY, mvarC);
        load.setActualRealLoad(mwA + mwB + mwC);
        load.setActualReactiveLoad(mvarA + mvarB + mvarC);
      }
       
      // line mw and mvar
      for (ElectricPowerFlowEdge edge : model.getFlowEdges()) {
        int legacyid = edge.getAttribute(DewVariables.DEW_LEGACY_ID_KEY, Integer.class);
        String name = edge.getAttribute(ElectricPowerFlowEdge.NAME_KEY, String.class);

        Object realA = dew.getComponentData(ElectricPowerFlowEdge.MW_FLOW_PHASE_A_KEY, legacyid, name);
        Object realB = dew.getComponentData(ElectricPowerFlowEdge.MW_FLOW_PHASE_B_KEY, legacyid, name);
        Object realC = dew.getComponentData(ElectricPowerFlowEdge.MW_FLOW_PHASE_C_KEY, legacyid, name);        
        Object reactiveA = dew.getComponentData(ElectricPowerFlowEdge.MVAR_FLOW_PHASE_A_KEY, legacyid, name);
        Object reactiveB = dew.getComponentData(ElectricPowerFlowEdge.MVAR_FLOW_PHASE_B_KEY, legacyid, name);
        Object reactiveC = dew.getComponentData(ElectricPowerFlowEdge.MVAR_FLOW_PHASE_C_KEY, legacyid, name);

        double mwA = realA == null ? 0 : Double.parseDouble(realA.toString());
        double mwB = realB == null ? 0 : Double.parseDouble(realB.toString());
        double mwC = realC == null ? 0 : Double.parseDouble(realC.toString());
        double mvarA = reactiveA == null ? 0 : Double.parseDouble(reactiveA.toString());
        double mvarB = reactiveB == null ? 0 : Double.parseDouble(reactiveB.toString());
        double mvarC = reactiveC == null ? 0 : Double.parseDouble(reactiveC.toString());
      
        edge.setAttribute(ElectricPowerFlowEdge.MW_FLOW_PHASE_A_KEY, mwA);
        edge.setAttribute(ElectricPowerFlowEdge.MW_FLOW_PHASE_B_KEY, mwB);
        edge.setAttribute(ElectricPowerFlowEdge.MW_FLOW_PHASE_C_KEY, mwC);
        edge.setAttribute(ElectricPowerFlowEdge.MVAR_FLOW_PHASE_A_KEY, mvarA);
        edge.setAttribute(ElectricPowerFlowEdge.MVAR_FLOW_PHASE_B_KEY, mvarB);
        edge.setAttribute(ElectricPowerFlowEdge.MVAR_FLOW_PHASE_C_KEY, mvarC);
        edge.setMWFlow(mwA + mwB + mwC);
        edge.setMVarFlow(mvarA + mvarB + mvarC);
      }   */          
   // }
    //catch (FileNotFoundException e) {
     // e.printStackTrace();
   // }
    
    // calculate the violations
//    ViolationCalculator calculator = ViolationCalculator.getInstance();
  //  model.clearViolations();
   // calculator.calculateOverloads(model);
   // calculator.calculateVoltageDepressions(model);
   // calculator.calculateGeneratorOverloads(model);
    //calculator.calculateLoadSheds(model);
    
    return SimulatorSolveState.CONVERGED_SOLUTION;

  }

  /**
   * Just testing out some calls...
   * @param args
   */
  public static void main(String[] args) {
    ComObjectFactory factory = ComObjectUtilities.getDefaultFactory();
    ComObject comObject = factory.createComObject("OpenDSSEngine.DSS");
    comObject.getBoolean("Start", 0);    
    ComObject text = comObject.call("Text");
    text.put("Command", "Compile (C:\\Program Files\\opendss\\IEEETestCases\\123Bus\\IEEE123Master.dss)");    
   // text.put("Command", "Compile (C:\\Program Files\\opendss\\IEEETestCases\\34Bus\\ieee34Mod1.dss)");    
    

    ComObject circuit = comObject.call("ActiveCircuit");
    ComObject solution = circuit.call("Solution");
    solution.call("Solve");
    System.out.println("All node voltage magnitudes: before");
    ArrayList<Double> array = circuit.getDoubleArray("AllNodeVmagPUByPhase", new Object[] {1});    
    for (int i = 0; i < array.size(); ++i) {
      System.out.println(array.get(i));
    }    
    System.out.println("Individual bus stuff: before");
    int numBuses = circuit.getInteger(OpenDSSIOConstants.NUMBER_OF_BUSES);    
    for (int i = 0; i < numBuses; ++i) {
      ComObject localbus = circuit.call(OpenDSSIOConstants.BUSES, i);
     // ComObject activeElement = circuit.call(OpenDSSIOConstants.ELEMENT);    
      ArrayList<Double> voltages = localbus.getDoubleArray("puVoltages");
      for (int j = 0; j < voltages.size(); ++j) {
        System.out.print(voltages.get(j) + " ");
      }
      System.out.println();
    }
    
    System.out.println();

    // Now try turning off a line
    circuit.call("Disable", "L2");
    solution = circuit.call("Solution");
    solution.call("Solve");
    System.out.println("All node voltage magnitudes: after");
    array = circuit.getDoubleArray("AllNodeVmagPUByPhase", new Object[] {1});
    for (int i = 0; i < array.size(); ++i) {
      System.out.println(array.get(i));
    }
    System.out.println("Individual bus stuff: before");
    for (int i = 0; i < numBuses; ++i) {
      ComObject localbus = circuit.call(OpenDSSIOConstants.BUSES, i);
     // ComObject activeElement = circuit.call(OpenDSSIOConstants.ELEMENT);    
      ArrayList<Double> voltages = localbus.getDoubleArray("puVoltages");
      for (int j = 0; j < voltages.size(); ++j) {
        System.out.print(voltages.get(j) + " ");
      }
      System.out.println();
    }
    
    System.out.println();
    
    text.put("Command", "Save Circuit Dir=" + System.getProperty("user.dir"));
            
  }
  
}
