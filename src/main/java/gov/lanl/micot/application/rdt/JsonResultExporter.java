package gov.lanl.micot.application.rdt;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Collection;

import gov.lanl.micot.infrastructure.application.ApplicationOutput;
import gov.lanl.micot.infrastructure.ep.model.Bus;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.Generator;
import gov.lanl.micot.infrastructure.ep.model.Load;
import gov.lanl.micot.infrastructure.model.Asset;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.model.ScenarioAttribute;
import gov.lanl.micot.infrastructure.project.ProjectConfiguration;
import gov.lanl.micot.infrastructure.project.ScenarioConfiguration;
import gov.lanl.micot.application.rdt.algorithm.AlgorithmConstants;
import gov.lanl.micot.util.io.json.JSON;
import gov.lanl.micot.util.io.json.JSONArrayBuilder;
import gov.lanl.micot.util.io.json.JSONObjectBuilder;
import gov.lanl.micot.util.io.json.JSONWriter;

/**
 * This is a simple utility class for exporting results in csv format
 * @author Russell Bent
 *
 */
public class JsonResultExporter {

  private static final String DESIGN_SOLUTION_TAG = "design_solution";
  private static final String DESIGN_COST_TAG = "design_cost";
  private static final String IS_FEASIBLE_TAG = "is_feasible";
  private static final String CRITICAL_LOAD_MET_TAG = "critical_load_met";
  private static final String NON_CRITICAL_LOAD_MET_TAG = "non_critical_load_met";
  private static final String TOTAL_LOAD_TAG = "total_load";
  
  private static final String LINES_TAG = "lines";
  private static final String LINE_ID_TAG = "id";
  private static final String IS_HARDENED_TAG = "hardened";
  private static final String IS_CONSTRUCTED_TAG = "built";
  private static final String IS_SWITCH_CONSTRUCTED_TAG = "switch_built";

  private static final String GENERATORS_TAG = "generators";
  private static final String GENERATOR_ID_TAG = "id";
  private static final String BUILT_CAPACITY_TAG = "built_capacity";
  private static final String REAL_GENERATION_TAG = "real_phase";
  private static final String REACTIVE_GENERATION_TAG = "reactive_phase";
  
  private static final String SCENARIO_SOLUTION_TAG = "scenario_solution";
  private static final String SCENARIO_ID_TAG = "id";
  private static final String IN_USE_TAG = "in_use";

  private static final String BUSES_TAG = "buses";
  private static final String BUS_ID_TAG = "id";
  private static final String VOLTAGE_TAG = "voltage";
 
  private static final String LOADS_TAG = "loads";
  private static final String LOAD_ID_TAG = "id";
  private static final String REAL_LOAD_TAG = "real_phase";
  private static final String REACTIVE_LOAD_TAG = "reactive_phase";
  
  // a hack for the exporting
  private String tag = Asset.ASSET_ID_KEY;
  
  /**
   * The export routine for the results
   * @param model
   * @throws FileNotFoundException 
   */
  public void exportResults(ApplicationOutput output, ProjectConfiguration configuration, String filename) throws FileNotFoundException {
    JSONObjectBuilder builder = JSON.getDefaultJSON().createObjectBuilder();
    JSONObjectBuilder designSolutionBuilder = JSON.getDefaultJSON().createObjectBuilder();
    JSONArrayBuilder scenarioSolutionBuilder = JSON.getDefaultJSON().createArrayBuilder();
        
    ElectricPowerModel model = output.get(RDTApplication.MODEL_FLAG, ElectricPowerModel.class);
    if (model.getAssets().iterator().next().getAttribute("OPENDSS_LEGACY_ID") != null) {
      tag = "OPENDSS_LEGACY_ID";
    }

    if (model.getAssets().iterator().next().getAttribute("DEW_LEGACY_ID") != null) {
      tag = "DEW_LEGACY_ID";
    }
    if (model.getAssets().iterator().next().getAttribute("LPNORM_LEGACY_ID") != null) {
      tag = "LPNORM_LEGACY_ID";
    }
    
    double objective = output.getDouble(RDTApplication.OBJECTIVE_FLAG);
    boolean isFeasible = output.getBoolean(RDTApplication.IS_FEASIBLE_FLAG);
    
    double totalLoad       = 0;
    for (Load load : model.getLoads()) {
      Number loadA = load.getAttribute(Load.DESIRED_REAL_LOAD_A_KEY, Number.class);
      Number loadB =  load.getAttribute(Load.DESIRED_REAL_LOAD_B_KEY, Number.class);
      Number loadC = load.getAttribute(Load.DESIRED_REAL_LOAD_C_KEY, Number.class);

      if (loadA != null) {
        totalLoad += loadA.doubleValue();
      }
      if (loadB != null) {
        totalLoad += loadB.doubleValue();
      }
      if (loadC != null) {
        totalLoad += loadC.doubleValue();
      }
    }
    
    designSolutionBuilder = designSolutionBuilder.add(DESIGN_COST_TAG, Math.abs(objective));
    designSolutionBuilder = designSolutionBuilder.add(IS_FEASIBLE_TAG, isFeasible);
    designSolutionBuilder = designSolutionBuilder.add(TOTAL_LOAD_TAG, totalLoad);

    designSolutionBuilder = writeLineDesignData(model.getFlowConnections(), model, designSolutionBuilder);
    designSolutionBuilder = writeGeneratorDesignData(model.getGenerators(), model, designSolutionBuilder);    
    builder.add(DESIGN_SOLUTION_TAG, designSolutionBuilder);
    
    for (ScenarioConfiguration scenarioConfiguration : configuration.getScenarioConfigurations()) {
      Scenario scenario = scenarioConfiguration.getScenario();
      double criticalLoad    = 0;
      double nonCriticalLoad = 0;
      
      for (Load load : model.getLoads()) {
        Object attribute = load.getAttribute(AlgorithmConstants.IS_CRITICAL_LOAD_KEY);
        ScenarioAttribute scenarioAttributeA = load.getAttribute(Load.ACTUAL_REAL_LOAD_A_KEY, ScenarioAttribute.class);
        ScenarioAttribute scenarioAttributeB = load.getAttribute(Load.ACTUAL_REAL_LOAD_B_KEY, ScenarioAttribute.class);
        ScenarioAttribute scenarioAttributeC = load.getAttribute(Load.ACTUAL_REAL_LOAD_C_KEY, ScenarioAttribute.class);
        
        
        if (attribute != null && (Boolean)attribute) {
          if (scenarioAttributeA != null && scenarioAttributeA.getValue(scenario) != null) {
            criticalLoad += scenarioAttributeA.getValue(scenario).doubleValue();
          }
          if (scenarioAttributeB != null && scenarioAttributeB.getValue(scenario) != null) {
            criticalLoad += scenarioAttributeB.getValue(scenario).doubleValue();
          }
          if (scenarioAttributeC != null && scenarioAttributeC.getValue(scenario) != null) {
            criticalLoad += scenarioAttributeC.getValue(scenario).doubleValue();
          }
        }
        else {
          if (scenarioAttributeA != null && scenarioAttributeA.getValue(scenario) != null) {
            nonCriticalLoad += scenarioAttributeA.getValue(scenario).doubleValue();
          }
          if (scenarioAttributeB != null && scenarioAttributeB.getValue(scenario) != null) {
            nonCriticalLoad += scenarioAttributeB.getValue(scenario).doubleValue();
          }
          if (scenarioAttributeC != null && scenarioAttributeC.getValue(scenario) != null) {
            nonCriticalLoad += scenarioAttributeC.getValue(scenario).doubleValue();
          }
        }
      }
    	
      JSONObjectBuilder scenarioBuilder = JSON.getDefaultJSON().createObjectBuilder();
      scenarioBuilder = scenarioBuilder.add(SCENARIO_ID_TAG, scenario.getIndex());
      scenarioBuilder = scenarioBuilder.add(CRITICAL_LOAD_MET_TAG, criticalLoad);
      scenarioBuilder = scenarioBuilder.add(NON_CRITICAL_LOAD_MET_TAG, nonCriticalLoad);
      scenarioBuilder = writeLineScenarioData(scenario, model.getFlowConnections(), model, scenarioBuilder);      
      scenarioBuilder = writeBusScenarioData(scenario, model.getBuses(), model, scenarioBuilder);      
      scenarioBuilder = writeGeneratorScenarioData(scenario, model.getGenerators(), model, scenarioBuilder);      
      scenarioBuilder = writeLoadScenarioData(scenario, model.getLoads(), model, scenarioBuilder);      
      scenarioSolutionBuilder = scenarioSolutionBuilder.add(scenarioBuilder);
    }
    
    builder.add(SCENARIO_SOLUTION_TAG, scenarioSolutionBuilder);
    
    // write the  data file
    try {
      PrintStream out = new PrintStream(filename);
      JSONWriter writer = JSON.getDefaultJSON().createWriter(out);
      writer.write(builder.build()); 
      writer.close();
      out.close();
    }
    catch (FileNotFoundException e) {
      e.printStackTrace();
    }

  }
   
  /** 
   * Write the line design data
   * @param connections
   * @param model
   * @param builder
   * @return
   */
  private JSONObjectBuilder writeLineDesignData(Collection<? extends ElectricPowerFlowConnection> connections, ElectricPowerModel model, JSONObjectBuilder builder) {
    
    JSONArrayBuilder arrayBuilder = JSON.getDefaultJSON().createArrayBuilder();
     
     for (ElectricPowerFlowConnection connection : connections) {
       JSONObjectBuilder lineBuilder = JSON.getDefaultJSON().createObjectBuilder();
       lineBuilder = lineBuilder.add(LINE_ID_TAG, connection.getAttribute(tag).toString());
       
       if (connection.getAttribute(AlgorithmConstants.IS_HARDENED_KEY) != null) {
         lineBuilder = lineBuilder.add(IS_HARDENED_TAG, connection.getAttribute(AlgorithmConstants.IS_HARDENED_KEY, Boolean.class));
       }
       
       if (connection.getAttribute(AlgorithmConstants.IS_CONSTRUCTED_KEY) != null) {
         lineBuilder = lineBuilder.add(IS_CONSTRUCTED_TAG, connection.getAttribute(AlgorithmConstants.IS_CONSTRUCTED_KEY, Boolean.class));
       }
       
       if (connection.getAttribute(AlgorithmConstants.IS_SWITCH_CONSTRUCTED_KEY) != null) {
         lineBuilder = lineBuilder.add(IS_SWITCH_CONSTRUCTED_TAG, connection.getAttribute(AlgorithmConstants.IS_SWITCH_CONSTRUCTED_KEY, Boolean.class));
       }
       
       arrayBuilder = arrayBuilder.add(lineBuilder);
     }
     return builder.add(LINES_TAG,arrayBuilder);
   }

  
  /**
   * Write the generator design data
   * @param generators
   * @param model
   * @param builder
   * @return
   */
  private JSONObjectBuilder writeGeneratorDesignData(Collection<? extends Generator> generators, ElectricPowerModel model, JSONObjectBuilder builder) {
   JSONArrayBuilder arrayBuilder = JSON.getDefaultJSON().createArrayBuilder();
    
   for (Generator generator : generators) {
     JSONObjectBuilder generatorBuilder = JSON.getDefaultJSON().createObjectBuilder();
     generatorBuilder = generatorBuilder.add(GENERATOR_ID_TAG, generator.getAttribute(tag).toString());
 
     if (generator.getAttribute(AlgorithmConstants.MICROGRID_CAPACITY_PHASE_A_KEY) != null || generator.getAttribute(AlgorithmConstants.MICROGRID_CAPACITY_PHASE_B_KEY) != null || generator.getAttribute(AlgorithmConstants.MICROGRID_CAPACITY_PHASE_C_KEY) != null) {
       
       double capacityA = generator.getAttribute(AlgorithmConstants.MICROGRID_CAPACITY_PHASE_A_KEY) == null ? 0 : generator.getAttribute(AlgorithmConstants.MICROGRID_CAPACITY_PHASE_A_KEY, Number.class).doubleValue();
       double capacityB = generator.getAttribute(AlgorithmConstants.MICROGRID_CAPACITY_PHASE_B_KEY) == null ? 0 : generator.getAttribute(AlgorithmConstants.MICROGRID_CAPACITY_PHASE_B_KEY, Number.class).doubleValue();
       double capacityC = generator.getAttribute(AlgorithmConstants.MICROGRID_CAPACITY_PHASE_C_KEY) == null ? 0 : generator.getAttribute(AlgorithmConstants.MICROGRID_CAPACITY_PHASE_C_KEY, Number.class).doubleValue();

       JSONArrayBuilder capacityBuilder = JSON.getDefaultJSON().createArrayBuilder();
       capacityBuilder.add(capacityA);
       capacityBuilder.add(capacityB);
       capacityBuilder.add(capacityC);
       
       generatorBuilder = generatorBuilder.add(BUILT_CAPACITY_TAG, capacityBuilder);
     }          
     arrayBuilder = arrayBuilder.add(generatorBuilder);  
   }
   return builder.add(GENERATORS_TAG,arrayBuilder);   
 }
  
  
  /** 
   * Write the line scenario data
   * @param connections
   * @param model
   * @param builder
   * @return
   */
  private JSONObjectBuilder writeLineScenarioData(Scenario scenario, Collection<? extends ElectricPowerFlowConnection> connections, ElectricPowerModel model, JSONObjectBuilder builder) {
    
    JSONArrayBuilder arrayBuilder = JSON.getDefaultJSON().createArrayBuilder();
     
     for (ElectricPowerFlowConnection connection : connections) {
       JSONObjectBuilder lineBuilder = JSON.getDefaultJSON().createObjectBuilder();
       lineBuilder = lineBuilder.add(LINE_ID_TAG, connection.getAttribute(tag).toString());

       double MWA = 0.0;
       if (connection.getAttribute(ElectricPowerFlowConnection.MW_FLOW_PHASE_A_KEY) != null && connection.getAttribute(ElectricPowerFlowConnection.MW_FLOW_PHASE_A_KEY, ScenarioAttribute.class).getValue(scenario) != null) {
         MWA = connection.getAttribute(ElectricPowerFlowConnection.MW_FLOW_PHASE_A_KEY, ScenarioAttribute.class).getValue(scenario).doubleValue(); 
       }

       double MWB = 0.0;
       if (connection.getAttribute(ElectricPowerFlowConnection.MW_FLOW_PHASE_B_KEY) != null && connection.getAttribute(ElectricPowerFlowConnection.MW_FLOW_PHASE_B_KEY, ScenarioAttribute.class).getValue(scenario) != null) {
         MWB = connection.getAttribute(ElectricPowerFlowConnection.MW_FLOW_PHASE_B_KEY, ScenarioAttribute.class).getValue(scenario).doubleValue(); 
       }

       double MWC = 0.0;
       if (connection.getAttribute(ElectricPowerFlowConnection.MW_FLOW_PHASE_C_KEY) != null && connection.getAttribute(ElectricPowerFlowConnection.MW_FLOW_PHASE_C_KEY, ScenarioAttribute.class).getValue(scenario) != null) {
         MWC = connection.getAttribute(ElectricPowerFlowConnection.MW_FLOW_PHASE_C_KEY, ScenarioAttribute.class).getValue(scenario).doubleValue(); 
       }


       double MVARA = 0.0;
       if (connection.getAttribute(ElectricPowerFlowConnection.MVAR_FLOW_PHASE_A_KEY) != null && connection.getAttribute(ElectricPowerFlowConnection.MVAR_FLOW_PHASE_A_KEY, ScenarioAttribute.class).getValue(scenario) != null) {
         MVARA = connection.getAttribute(ElectricPowerFlowConnection.MVAR_FLOW_PHASE_A_KEY, ScenarioAttribute.class).getValue(scenario).doubleValue(); 
       }

       double MVARB = 0.0;
       if (connection.getAttribute(ElectricPowerFlowConnection.MVAR_FLOW_PHASE_B_KEY) != null && connection.getAttribute(ElectricPowerFlowConnection.MVAR_FLOW_PHASE_B_KEY, ScenarioAttribute.class).getValue(scenario) != null) {
         MVARB = connection.getAttribute(ElectricPowerFlowConnection.MVAR_FLOW_PHASE_B_KEY, ScenarioAttribute.class).getValue(scenario).doubleValue(); 
       }

       double MVARC = 0.0;
       if (connection.getAttribute(ElectricPowerFlowConnection.MVAR_FLOW_PHASE_C_KEY) != null && connection.getAttribute(ElectricPowerFlowConnection.MVAR_FLOW_PHASE_C_KEY, ScenarioAttribute.class).getValue(scenario) != null) {
         MVARC = connection.getAttribute(ElectricPowerFlowConnection.MVAR_FLOW_PHASE_C_KEY, ScenarioAttribute.class).getValue(scenario).doubleValue(); 
       }

       boolean in_use = connection.getAttribute(AlgorithmConstants.IS_USED_KEY, ScenarioAttribute.class).getValue(scenario).intValue() == 1 ? true : false;
         
       lineBuilder = lineBuilder.add(IN_USE_TAG, in_use);
       
       JSONArrayBuilder realBuilder = JSON.getDefaultJSON().createArrayBuilder();
       realBuilder.add(MWA);
       realBuilder.add(MWB);
       realBuilder.add(MWC);
       
       JSONArrayBuilder reactiveBuilder = JSON.getDefaultJSON().createArrayBuilder();
       reactiveBuilder.add(MVARA);
       reactiveBuilder.add(MVARB);
       reactiveBuilder.add(MVARC);
       
       lineBuilder = lineBuilder.add(REAL_LOAD_TAG, realBuilder);
       lineBuilder = lineBuilder.add(REACTIVE_LOAD_TAG, reactiveBuilder);
       
       arrayBuilder = arrayBuilder.add(lineBuilder);
     }
     return builder.add(LINES_TAG,arrayBuilder);
   }
  
  /**
   * Write the bus level scenario data
   * @param buses
   * @param model
   * @param builder
   * @return
   */
  private JSONObjectBuilder writeBusScenarioData(Scenario scenario, Collection<? extends Bus> buses, ElectricPowerModel model, JSONObjectBuilder builder) {
    
    JSONArrayBuilder arrayBuilder = JSON.getDefaultJSON().createArrayBuilder();
     
     for (Bus bus : buses) {
       JSONObjectBuilder busBuilder = JSON.getDefaultJSON().createObjectBuilder();
       busBuilder = busBuilder.add(BUS_ID_TAG, bus.getAttribute(tag).toString());

       double VA = 1.0;
       if (bus.getAttribute(Bus.VOLTAGE_PU_A_KEY) != null && bus.getAttribute(Bus.VOLTAGE_PU_A_KEY) instanceof ScenarioAttribute && bus.getAttribute(Bus.VOLTAGE_PU_A_KEY,ScenarioAttribute.class).getValue(scenario) != null) {
         VA = bus.getAttribute(Bus.VOLTAGE_PU_A_KEY, ScenarioAttribute.class).getValue(scenario).doubleValue(); 
       }

       double VB = 1.0;
       if (bus.getAttribute(Bus.VOLTAGE_PU_B_KEY) != null && bus.getAttribute(Bus.VOLTAGE_PU_A_KEY) instanceof ScenarioAttribute && bus.getAttribute(Bus.VOLTAGE_PU_B_KEY,ScenarioAttribute.class).getValue(scenario) != null) {
         VB = bus.getAttribute(Bus.VOLTAGE_PU_B_KEY, ScenarioAttribute.class).getValue(scenario).doubleValue(); 
       }

       double VC = 1.0;
       if (bus.getAttribute(Bus.VOLTAGE_PU_C_KEY) != null && bus.getAttribute(Bus.VOLTAGE_PU_A_KEY) instanceof ScenarioAttribute && bus.getAttribute(Bus.VOLTAGE_PU_C_KEY,ScenarioAttribute.class).getValue(scenario) != null) {
         VC = bus.getAttribute(Bus.VOLTAGE_PU_C_KEY, ScenarioAttribute.class).getValue(scenario).doubleValue(); 
       }
       
       JSONArrayBuilder voltageBuilder = JSON.getDefaultJSON().createArrayBuilder();
       voltageBuilder.add(VA);
       voltageBuilder.add(VB);
       voltageBuilder.add(VC);
                
       busBuilder = busBuilder.add(VOLTAGE_TAG, voltageBuilder);
       arrayBuilder = arrayBuilder.add(busBuilder);
     }
     return builder.add(BUSES_TAG,arrayBuilder);
   }

  /**
   * Write the generator scenario data
   * @param generators
   * @param model
   * @param builder
   * @return
   */
  private JSONObjectBuilder writeGeneratorScenarioData(Scenario scenario, Collection<? extends Generator> generators, ElectricPowerModel model, JSONObjectBuilder builder) {
    
    JSONArrayBuilder arrayBuilder = JSON.getDefaultJSON().createArrayBuilder();
     
     for (Generator generator : generators) {
       JSONObjectBuilder generatorBuilder = JSON.getDefaultJSON().createObjectBuilder();
       generatorBuilder = generatorBuilder.add(GENERATOR_ID_TAG, generator.getAttribute(tag).toString());

       double realA = 0.0;
       if (generator.getAttribute(Generator.ACTUAL_REAL_GENERATION_A_KEY) != null && generator.getAttribute(Generator.ACTUAL_REAL_GENERATION_A_KEY) instanceof ScenarioAttribute && generator.getAttribute(Generator.ACTUAL_REAL_GENERATION_A_KEY,ScenarioAttribute.class).getValue(scenario) != null) {
         realA = generator.getAttribute(Generator.ACTUAL_REAL_GENERATION_A_KEY, ScenarioAttribute.class).getValue(scenario).doubleValue(); 
       }

       double realB = 0.0;
       if (generator.getAttribute(Generator.ACTUAL_REAL_GENERATION_B_KEY) != null && generator.getAttribute(Generator.ACTUAL_REAL_GENERATION_B_KEY) instanceof ScenarioAttribute && generator.getAttribute(Generator.ACTUAL_REAL_GENERATION_B_KEY,ScenarioAttribute.class).getValue(scenario) != null) {
         realB = generator.getAttribute(Generator.ACTUAL_REAL_GENERATION_B_KEY, ScenarioAttribute.class).getValue(scenario).doubleValue(); 
       }

       double realC = 0.0;
       if (generator.getAttribute(Generator.ACTUAL_REAL_GENERATION_C_KEY) != null && generator.getAttribute(Generator.ACTUAL_REAL_GENERATION_C_KEY) instanceof ScenarioAttribute && generator.getAttribute(Generator.ACTUAL_REAL_GENERATION_C_KEY,ScenarioAttribute.class).getValue(scenario) != null) {
         realC = generator.getAttribute(Generator.ACTUAL_REAL_GENERATION_C_KEY, ScenarioAttribute.class).getValue(scenario).doubleValue(); 
       }

       double reactiveA = 0.0;
       if (generator.getAttribute(Generator.ACTUAL_REACTIVE_GENERATION_A_KEY) != null && generator.getAttribute(Generator.ACTUAL_REACTIVE_GENERATION_A_KEY) instanceof ScenarioAttribute && generator.getAttribute(Generator.ACTUAL_REACTIVE_GENERATION_A_KEY,ScenarioAttribute.class).getValue(scenario) != null) {
         reactiveA = generator.getAttribute(Generator.ACTUAL_REACTIVE_GENERATION_A_KEY, ScenarioAttribute.class).getValue(scenario).doubleValue(); 
       }

       double reactiveB = 0.0;
       if (generator.getAttribute(Generator.ACTUAL_REACTIVE_GENERATION_B_KEY) != null && generator.getAttribute(Generator.ACTUAL_REACTIVE_GENERATION_B_KEY) instanceof ScenarioAttribute && generator.getAttribute(Generator.ACTUAL_REACTIVE_GENERATION_B_KEY,ScenarioAttribute.class).getValue(scenario) != null) {
         reactiveB = generator.getAttribute(Generator.ACTUAL_REACTIVE_GENERATION_B_KEY, ScenarioAttribute.class).getValue(scenario).doubleValue(); 
       }

       double reactiveC = 0.0;
       if (generator.getAttribute(Generator.ACTUAL_REACTIVE_GENERATION_C_KEY) != null && generator.getAttribute(Generator.ACTUAL_REACTIVE_GENERATION_C_KEY) instanceof ScenarioAttribute && generator.getAttribute(Generator.ACTUAL_REACTIVE_GENERATION_C_KEY,ScenarioAttribute.class).getValue(scenario) != null) {
         reactiveC = generator.getAttribute(Generator.ACTUAL_REACTIVE_GENERATION_C_KEY, ScenarioAttribute.class).getValue(scenario).doubleValue(); 
       }

       JSONArrayBuilder realBuilder = JSON.getDefaultJSON().createArrayBuilder();
       realBuilder.add(realA);
       realBuilder.add(realB);
       realBuilder.add(realC);
       
       JSONArrayBuilder reactiveBuilder = JSON.getDefaultJSON().createArrayBuilder();
       reactiveBuilder.add(reactiveA);
       reactiveBuilder.add(reactiveB);
       reactiveBuilder.add(reactiveC);
       
       generatorBuilder = generatorBuilder.add(REAL_GENERATION_TAG, realBuilder);
       generatorBuilder = generatorBuilder.add(REACTIVE_GENERATION_TAG, reactiveBuilder);
       arrayBuilder = arrayBuilder.add(generatorBuilder);
     }
     return builder.add(GENERATORS_TAG,arrayBuilder);
   }

  
  /**
   * Write the scenario load data
   * @param scenario
   * @param loads
   * @param model
   * @param builder
   * @return
   */
  private JSONObjectBuilder writeLoadScenarioData(Scenario scenario, Collection<? extends Load> loads, ElectricPowerModel model, JSONObjectBuilder builder) {
    
    JSONArrayBuilder arrayBuilder = JSON.getDefaultJSON().createArrayBuilder();
     
     for (Load load : loads) {
       JSONObjectBuilder loadBuilder = JSON.getDefaultJSON().createObjectBuilder();
       loadBuilder = loadBuilder.add(LOAD_ID_TAG, load.getAttribute(tag).toString());

       double realA = 0.0;
       if (load.getAttribute(Load.ACTUAL_REAL_LOAD_A_KEY) != null && load.getAttribute(Load.ACTUAL_REAL_LOAD_A_KEY) instanceof ScenarioAttribute && load.getAttribute(Load.ACTUAL_REAL_LOAD_A_KEY,ScenarioAttribute.class).getValue(scenario) != null) {
         realA = load.getAttribute(Load.ACTUAL_REAL_LOAD_A_KEY, ScenarioAttribute.class).getValue(scenario).doubleValue(); 
       }

       double realB = 0.0;
       if (load.getAttribute(Load.ACTUAL_REAL_LOAD_B_KEY) != null && load.getAttribute(Load.ACTUAL_REAL_LOAD_B_KEY) instanceof ScenarioAttribute && load.getAttribute(Load.ACTUAL_REAL_LOAD_B_KEY,ScenarioAttribute.class).getValue(scenario) != null) {
         realB = load.getAttribute(Load.ACTUAL_REAL_LOAD_B_KEY, ScenarioAttribute.class).getValue(scenario).doubleValue(); 
       }
       
       double realC = 0.0;
       if (load.getAttribute(Load.ACTUAL_REAL_LOAD_C_KEY) != null && load.getAttribute(Load.ACTUAL_REAL_LOAD_C_KEY) instanceof ScenarioAttribute && load.getAttribute(Load.ACTUAL_REAL_LOAD_C_KEY,ScenarioAttribute.class).getValue(scenario) != null) {
         realC = load.getAttribute(Load.ACTUAL_REAL_LOAD_C_KEY, ScenarioAttribute.class).getValue(scenario).doubleValue(); 
       }

       double reactiveA = 0.0;
       if (load.getAttribute(Load.ACTUAL_REACTIVE_LOAD_A_KEY) != null && load.getAttribute(Load.ACTUAL_REACTIVE_LOAD_A_KEY) instanceof ScenarioAttribute && load.getAttribute(Load.ACTUAL_REACTIVE_LOAD_A_KEY,ScenarioAttribute.class).getValue(scenario) != null) {
         reactiveA = load.getAttribute(Load.ACTUAL_REACTIVE_LOAD_A_KEY, ScenarioAttribute.class).getValue(scenario).doubleValue(); 
       }

       double reactiveB = 0.0;
       if (load.getAttribute(Load.ACTUAL_REACTIVE_LOAD_B_KEY) != null && load.getAttribute(Load.ACTUAL_REACTIVE_LOAD_B_KEY) instanceof ScenarioAttribute && load.getAttribute(Load.ACTUAL_REACTIVE_LOAD_B_KEY,ScenarioAttribute.class).getValue(scenario) != null) {
         reactiveB = load.getAttribute(Load.ACTUAL_REACTIVE_LOAD_B_KEY, ScenarioAttribute.class).getValue(scenario).doubleValue(); 
       }
       
       double reactiveC = 0.0;
       if (load.getAttribute(Load.ACTUAL_REACTIVE_LOAD_C_KEY) != null && load.getAttribute(Load.ACTUAL_REACTIVE_LOAD_C_KEY) instanceof ScenarioAttribute && load.getAttribute(Load.ACTUAL_REACTIVE_LOAD_C_KEY,ScenarioAttribute.class).getValue(scenario) != null) {
         reactiveC = load.getAttribute(Load.ACTUAL_REACTIVE_LOAD_C_KEY, ScenarioAttribute.class).getValue(scenario).doubleValue(); 
       }
       
       JSONArrayBuilder realBuilder = JSON.getDefaultJSON().createArrayBuilder();
       realBuilder.add(realA);
       realBuilder.add(realB);
       realBuilder.add(realC);
       
       JSONArrayBuilder reactiveBuilder = JSON.getDefaultJSON().createArrayBuilder();
       reactiveBuilder.add(reactiveA);
       reactiveBuilder.add(reactiveB);
       reactiveBuilder.add(reactiveC);
       
       loadBuilder = loadBuilder.add(REAL_LOAD_TAG, realBuilder);
       loadBuilder = loadBuilder.add(REACTIVE_LOAD_TAG, reactiveBuilder);
       arrayBuilder = arrayBuilder.add(loadBuilder);
     }
     return builder.add(LOADS_TAG,arrayBuilder);
   }

  
}
