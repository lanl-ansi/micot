package gov.lanl.micot.infrastructure.ep.simulate.dew;

import java.io.FileNotFoundException;

import gov.lanl.micot.infrastructure.ep.model.Bus;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.Generator;
import gov.lanl.micot.infrastructure.ep.model.Load;
import gov.lanl.micot.infrastructure.ep.model.dew.Dew;
import gov.lanl.micot.infrastructure.ep.model.dew.DewException;
import gov.lanl.micot.infrastructure.ep.model.dew.DewLegacyId;
import gov.lanl.micot.infrastructure.ep.model.dew.DewModel;
import gov.lanl.micot.infrastructure.ep.model.dew.DewVariables;
import gov.lanl.micot.infrastructure.ep.simulate.ElectricPowerSimulatorImpl;

/**
 * This is an instantion of the simulator that uses the DEW solver
 * @author Russell Bent
 */
public class DewSimulator extends ElectricPowerSimulatorImpl {

  /**
	 * Constructor
	 * @param nextGenerationPFWFilename
	 */
	protected DewSimulator() {
		super();
	}
	
  @Override
	protected SimulatorSolveState simulateModel(ElectricPowerModel modelState) {
	  if (!(modelState instanceof DewModel)) {	  
	    throw new RuntimeException("Error: Cannot currently convert an arbitrary model in a DEW Model");
	  }	  
	  
	  DewModel model = (DewModel)modelState;
    Dew dew = model.getDew();
    
    try {
      model.syncModel();
      dew.reloadSystem();
      dew.clearCache();
      dew.run(DewVariables.APPID_RADIAL_POWER_FLOW, 0);
    
      // voltage magnitudes and phase angles
      for (Bus bus : model.getBuses()) {
        DewLegacyId legacyid = bus.getAttribute(DewVariables.DEW_LEGACY_ID_KEY, DewLegacyId.class);
        String name = bus.getAttribute(Bus.NAME_KEY, String.class);      
        Object vma = dew.getComponentData(Bus.VOLTAGE_PU_A_KEY, legacyid, name);
        Object vmb = dew.getComponentData(Bus.VOLTAGE_PU_B_KEY, legacyid, name);
        Object vmc = dew.getComponentData(Bus.VOLTAGE_PU_C_KEY, legacyid, name);
        Object vaa = dew.getComponentData(Bus.PHASE_ANGLE_A_KEY, legacyid, name);
        Object vab = dew.getComponentData(Bus.PHASE_ANGLE_B_KEY, legacyid, name);
        Object vac = dew.getComponentData(Bus.PHASE_ANGLE_C_KEY, legacyid, name);
                
        double voltageMagA = vma == null ? 0.0 : Double.parseDouble(vma.toString());
        double phaseAngleA = vaa == null ? 0.0 : Double.parseDouble(vaa.toString());
        double voltageMagB = vmb == null ? 0.0 :Double.parseDouble(vmb.toString());
        double phaseAngleB = vab == null ? 0.0 :Double.parseDouble(vab.toString());
        double voltageMagC = vmc == null ? 0.0 :Double.parseDouble(vmc.toString());
        double phaseAngleC = vac == null ? 0.0 :Double.parseDouble(vac.toString());
      
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
      for (Generator generator : model.getGenerators()) {
        DewLegacyId legacyid = generator.getAttribute(DewVariables.DEW_LEGACY_ID_KEY, DewLegacyId.class);
        String name = generator.getAttribute(Generator.NAME_KEY, String.class);   
        
        Object realA = dew.getComponentData(Generator.REAL_GENERATION_A_KEY, legacyid, name);
        Object realB = dew.getComponentData(Generator.REAL_GENERATION_B_KEY, legacyid, name);
        Object realC = dew.getComponentData(Generator.REAL_GENERATION_C_KEY, legacyid, name);
        Object reactiveA = dew.getComponentData(Generator.REACTIVE_GENERATION_A_KEY, legacyid, name);
        Object reactiveB = dew.getComponentData(Generator.REACTIVE_GENERATION_B_KEY, legacyid, name);
        Object reactiveC = dew.getComponentData(Generator.REACTIVE_GENERATION_C_KEY, legacyid, name);
                
        double mwA = realA == null ? 0.0 : Double.parseDouble(realA.toString());
        double mwB = realB == null ? 0.0 : Double.parseDouble(realB.toString());
        double mwC = realC == null ? 0.0 : Double.parseDouble(realC.toString());
        double mvarA = reactiveA == null ? 0.0 : Double.parseDouble(reactiveA.toString());
        double mvarB = reactiveB == null ? 0.0 : Double.parseDouble(reactiveB.toString());
        double mvarC = reactiveC == null ? 0.0 : Double.parseDouble(reactiveC.toString());
      
        generator.setAttribute(Generator.REAL_GENERATION_A_KEY, mwA);
        generator.setAttribute(Generator.REAL_GENERATION_B_KEY, mwB);
        generator.setAttribute(Generator.REAL_GENERATION_C_KEY, mwC);
        generator.setAttribute(Generator.REACTIVE_GENERATION_A_KEY, mvarA);
        generator.setAttribute(Generator.REACTIVE_GENERATION_B_KEY, mvarB);
        generator.setAttribute(Generator.REACTIVE_GENERATION_C_KEY, mvarC);
        generator.setRealGeneration(mwA + mwB + mwC);
        generator.setReactiveGeneration(mvarA + mvarB + mvarC);
        
      
        
        
      }
        
      // load mw and mvar
      for (Load load : model.getLoads()) {
        DewLegacyId legacyid = load.getAttribute(DewVariables.DEW_LEGACY_ID_KEY, DewLegacyId.class);
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
        load.setRealLoad(mwA + mwB + mwC);
        load.setReactiveLoad(mvarA + mvarB + mvarC);
        
      }
       
      // line mw and mvar
      for (ElectricPowerFlowConnection edge : model.getFlowConnections()) {
        DewLegacyId legacyid = edge.getAttribute(DewVariables.DEW_LEGACY_ID_KEY, DewLegacyId.class);
        String name = edge.getAttribute(ElectricPowerFlowConnection.NAME_KEY, String.class);

        Object realA = dew.getComponentData(ElectricPowerFlowConnection.MW_FLOW_PHASE_A_KEY, legacyid, name);
        Object realB = dew.getComponentData(ElectricPowerFlowConnection.MW_FLOW_PHASE_B_KEY, legacyid, name);
        Object realC = dew.getComponentData(ElectricPowerFlowConnection.MW_FLOW_PHASE_C_KEY, legacyid, name);        
        Object reactiveA = dew.getComponentData(ElectricPowerFlowConnection.MVAR_FLOW_PHASE_A_KEY, legacyid, name);
        Object reactiveB = dew.getComponentData(ElectricPowerFlowConnection.MVAR_FLOW_PHASE_B_KEY, legacyid, name);
        Object reactiveC = dew.getComponentData(ElectricPowerFlowConnection.MVAR_FLOW_PHASE_C_KEY, legacyid, name);

        double mwA = realA == null ? 0 : Double.parseDouble(realA.toString());
        double mwB = realB == null ? 0 : Double.parseDouble(realB.toString());
        double mwC = realC == null ? 0 : Double.parseDouble(realC.toString());
        double mvarA = reactiveA == null ? 0 : Double.parseDouble(reactiveA.toString());
        double mvarB = reactiveB == null ? 0 : Double.parseDouble(reactiveB.toString());
        double mvarC = reactiveC == null ? 0 : Double.parseDouble(reactiveC.toString());
      
        edge.setAttribute(ElectricPowerFlowConnection.MW_FLOW_PHASE_A_KEY, mwA);
        edge.setAttribute(ElectricPowerFlowConnection.MW_FLOW_PHASE_B_KEY, mwB);
        edge.setAttribute(ElectricPowerFlowConnection.MW_FLOW_PHASE_C_KEY, mwC);
        edge.setAttribute(ElectricPowerFlowConnection.MVAR_FLOW_PHASE_A_KEY, mvarA);
        edge.setAttribute(ElectricPowerFlowConnection.MVAR_FLOW_PHASE_B_KEY, mvarB);
        edge.setAttribute(ElectricPowerFlowConnection.MVAR_FLOW_PHASE_C_KEY, mvarC);
        edge.setMWFlow(mwA + mwB + mwC);
        edge.setMVarFlow(mvarA + mvarB + mvarC);
      } 						
    }
    catch (DewException e) {
      e.printStackTrace();
    }
    catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    
    // calculate the violations
		//ViolationCalculator calculator = ViolationCalculator.getInstance();
		//modelState.clearViolations();
    //calculator.calculateOverloads(modelState);
    //calculator.calculateVoltageDepressions(modelState);
    //calculator.calculateGeneratorOverloads(modelState);
    //calculator.calculateLoadSheds(modelState);
		
    return SimulatorSolveState.CONVERGED_SOLUTION;

	}

}

