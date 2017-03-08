package gov.lanl.micot.application.rdt.io;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;

import gov.lanl.micot.infrastructure.ep.model.Bus;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.Generator;
import gov.lanl.micot.infrastructure.ep.model.Load;
import gov.lanl.micot.infrastructure.ep.model.Transformer;
import gov.lanl.micot.infrastructure.ep.optimize.OptimizationConstants;
import gov.lanl.micot.infrastructure.model.Asset;
import gov.lanl.micot.infrastructure.model.FlowConnection;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.project.AlgorithmConfiguration;
import gov.lanl.micot.application.lpnorm.io.LPNormIOConstants;
import gov.lanl.micot.application.rdt.algorithm.AlgorithmConstants;
import gov.lanl.micot.util.io.json.JSON;
import gov.lanl.micot.util.io.json.JSONArrayBuilder;
import gov.lanl.micot.util.io.json.JSONObjectBuilder;
import gov.lanl.micot.util.io.json.JSONWriter;

/** 
 * The purpose of this class is to export specifications for an RDT specification in a JSON format
 * for LPNORM
 * @author Russell Bent
 *
 */
public class RdtLPNormExporter {
  
   
  private String tag = Asset.ASSET_ID_KEY;

  
  /**
   * This is the key routine for exporting the project information in JSON format
   * @param configuration
   * @param filename
   */
  public void exportJsonPowerModel(ElectricPowerModel model, AlgorithmConfiguration configuration, Collection<Scenario> scenarios, String filename) {
    if (model.getAssets().iterator().next().getAttribute("OPENDSS_LEGACY_ID") != null) {
      tag = "OPENDSS_LEGACY_ID";
    }

    if (model.getAssets().iterator().next().getAttribute("DEW_LEGACY_ID") != null) {
      tag = "DEW_LEGACY_ID";
    }
    
    JSONObjectBuilder builder = JSON.getDefaultJSON().createObjectBuilder();

    // write the bus data
    builder = writeBusData(model.getBuses(), model, builder);
    builder = writeLoadData(model.getLoads(), model, builder);
    builder = writeGeneratorData(model.getGenerators(), model, builder);
    
    ArrayList<ElectricPowerFlowConnection> connections = new ArrayList<ElectricPowerFlowConnection>();
    connections.addAll(model.getFlowConnections());
    
    // write the line data
    builder = writeLineCodeData(connections, builder);
    builder = writeLineData(connections, model, builder);

    double epsilon = configuration.getAlgorithmFlags().get(AlgorithmConstants.CHANCE_CONSTRAINT_EPSILON_KEY) == null ? 1.0 : configuration.getAlgorithmFlags().getDouble(AlgorithmConstants.CHANCE_CONSTRAINT_EPSILON_KEY);
    
    // write parameters
    builder = builder.add(LPNormIOConstants.CRITICAL_LOAD_MET_TAG, configuration.getAlgorithmFlags().getDouble(AlgorithmConstants.CRITICAL_LOAD_MET_KEY));
    builder = builder.add(LPNormIOConstants.TOTAL_LOAD_MET_TAG, configuration.getAlgorithmFlags().getDouble(AlgorithmConstants.LOAD_MET_KEY));
    builder = builder.add(LPNormIOConstants.CHANCE_CONSTRAINT_TAG, epsilon);
    builder = builder.add(LPNormIOConstants.PHASE_VARIATION_TAG, configuration.getAlgorithmFlags().getDouble(AlgorithmConstants.PHASE_VARIATION_KEY));

    // write the scenarios
    JSONArrayBuilder scenarioBuilder = JSON.getDefaultJSON().createArrayBuilder();
    for (Scenario scenario : scenarios) {
      scenarioBuilder = writeScenarioData(scenario,connections,scenarioBuilder);
    }
    builder = builder.add(LPNormIOConstants.SCENARIOS_TAG, scenarioBuilder);
    
    // write the main data file
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
   * Write the power system bus data
   * @param buses
   * @param model
   * @param data
   * @return
   */
  private JSONObjectBuilder writeBusData(Collection<? extends Bus> buses, ElectricPowerModel model, JSONObjectBuilder builder) {
    
   JSONArrayBuilder arrayBuilder = JSON.getDefaultJSON().createArrayBuilder();
    
    for (Bus bus : buses) {
      // don't add a bus if it does not exist or is not active
      if (!OptimizationConstants.allActive(null,bus)) {
        continue;
      }
            
      boolean hasPhaseA = false;
      boolean hasPhaseB = false;
      boolean hasPhaseC = false;
      for (Load load : model.getNode(bus).getComponents(Load.class)) {
        if (load.getAttribute(Load.HAS_PHASE_A_KEY,Boolean.class)) {
          hasPhaseA = true;
        }
        if (load.getAttribute(Load.HAS_PHASE_B_KEY,Boolean.class)) {
          hasPhaseB = true;
        }
        if (load.getAttribute(Load.HAS_PHASE_C_KEY,Boolean.class)) {
          hasPhaseC = true;
        }  
      }
     
      for (Generator gen : model.getNode(bus).getComponents(Generator.class)) {
        if (gen.getAttribute(Generator.HAS_PHASE_A_KEY,Boolean.class)) {
          hasPhaseA = true;
        }
        if (gen.getAttribute(Generator.HAS_PHASE_B_KEY,Boolean.class)) {
          hasPhaseB = true;
        }
        if (gen.getAttribute(Generator.HAS_PHASE_C_KEY,Boolean.class)) {
          hasPhaseC = true;
        }  
      }
      
      for (FlowConnection connection : model.getFlowConnections(model.getNode(bus))) {
        if (connection.getAttribute(ElectricPowerFlowConnection.IS_PHASE_A_KEY,Boolean.class)) {
          hasPhaseA = true;
        }
        if (connection.getAttribute(ElectricPowerFlowConnection.IS_PHASE_B_KEY,Boolean.class)) {
          hasPhaseB = true;
        }
        if (connection.getAttribute(ElectricPowerFlowConnection.IS_PHASE_C_KEY,Boolean.class)) {
          hasPhaseC = true;
        }        
      }
      
            
      JSONArrayBuilder phaseBuilder = JSON.getDefaultJSON().createArrayBuilder();
      phaseBuilder = phaseBuilder.add(hasPhaseA);
      phaseBuilder = phaseBuilder.add(hasPhaseB);
      phaseBuilder = phaseBuilder.add(hasPhaseC);

      JSONArrayBuilder voltageBuilder = JSON.getDefaultJSON().createArrayBuilder();
      voltageBuilder = voltageBuilder.add(bus.getAttribute(Bus.VOLTAGE_PU_A_KEY, Number.class).doubleValue());
      voltageBuilder = voltageBuilder.add(bus.getAttribute(Bus.VOLTAGE_PU_B_KEY, Number.class).doubleValue());
      voltageBuilder = voltageBuilder.add(bus.getAttribute(Bus.VOLTAGE_PU_C_KEY, Number.class).doubleValue());

      JSONObjectBuilder busBuilder = JSON.getDefaultJSON().createObjectBuilder();
      busBuilder = busBuilder.add(LPNormIOConstants.BUS_ID_TAG, bus.getAttribute(tag).toString());
      busBuilder = busBuilder.add(LPNormIOConstants.BUS_LONGITUDE_TAG, bus.getCoordinate().getX());
      busBuilder = busBuilder.add(LPNormIOConstants.BUS_LATITUDE_TAG, bus.getCoordinate().getY());
      busBuilder = busBuilder.add(LPNormIOConstants.BUS_MIN_VOLTAGE_TAG, bus.getMinimumVoltagePU());
      busBuilder = busBuilder.add(LPNormIOConstants.BUS_MAX_VOLTAGE_TAG, bus.getMaximumVoltagePU());
      busBuilder = busBuilder.add(LPNormIOConstants.BUS_VOLTAGE_TAG, voltageBuilder);
      busBuilder = busBuilder.add(LPNormIOConstants.BUS_HAS_PHASE_TAG, phaseBuilder);
      busBuilder = busBuilder.add(LPNormIOConstants.BUS_HAS_GENERATOR_TAG, model.getNode(bus).getGenerator() != null);   
      arrayBuilder = arrayBuilder.add(busBuilder);
    }

    return builder.add(LPNormIOConstants.BUSES_TAG,arrayBuilder);
  }

  /**
   * Write the load data to a file
   * @param buses
   * @param model
   * @param builder
   * @return
   */
  private JSONObjectBuilder writeLoadData(Collection<? extends Load> loads, ElectricPowerModel model, JSONObjectBuilder builder) {
    
    JSONArrayBuilder arrayBuilder = JSON.getDefaultJSON().createArrayBuilder();
     
     for (Load load : loads) {
       Bus loadBus = model.getNode(load).getBus();
       
       // don't add a bus if it does not exist or is not active
       if (!OptimizationConstants.allActive(null, loadBus, load)) {
         continue;
       }
             
             
       JSONArrayBuilder phaseBuilder = JSON.getDefaultJSON().createArrayBuilder();
       phaseBuilder = phaseBuilder.add(load.getAttribute(Load.HAS_PHASE_A_KEY, Boolean.class));
       phaseBuilder = phaseBuilder.add(load.getAttribute(Load.HAS_PHASE_B_KEY, Boolean.class));
       phaseBuilder = phaseBuilder.add(load.getAttribute(Load.HAS_PHASE_C_KEY, Boolean.class));

       JSONArrayBuilder realBuilder = JSON.getDefaultJSON().createArrayBuilder();
       realBuilder = realBuilder.add(load.getAttribute(Load.DESIRED_REAL_LOAD_A_KEY, Number.class).doubleValue());
       realBuilder = realBuilder.add(load.getAttribute(Load.DESIRED_REAL_LOAD_B_KEY, Number.class).doubleValue());
       realBuilder = realBuilder.add(load.getAttribute(Load.DESIRED_REAL_LOAD_C_KEY, Number.class).doubleValue());

       JSONArrayBuilder reactiveBuilder = JSON.getDefaultJSON().createArrayBuilder();
       reactiveBuilder = reactiveBuilder.add(load.getAttribute(Load.DESIRED_REACTIVE_LOAD_A_KEY, Number.class).doubleValue());
       reactiveBuilder = reactiveBuilder.add(load.getAttribute(Load.DESIRED_REACTIVE_LOAD_B_KEY, Number.class).doubleValue());
       reactiveBuilder = reactiveBuilder.add(load.getAttribute(Load.DESIRED_REACTIVE_LOAD_C_KEY, Number.class).doubleValue());
       
       boolean isCritical = load.getAttribute(AlgorithmConstants.IS_CRITICAL_LOAD_KEY) == null ? false : load.getAttribute(AlgorithmConstants.IS_CRITICAL_LOAD_KEY, Boolean.class);
       
       JSONObjectBuilder loadBuilder = JSON.getDefaultJSON().createObjectBuilder();
       loadBuilder = loadBuilder.add(LPNormIOConstants.LOAD_ID_TAG, load.getAttribute(tag).toString());
       loadBuilder = loadBuilder.add(LPNormIOConstants.LOAD_BUS_ID_TAG, loadBus.getAttribute(tag).toString());
       loadBuilder = loadBuilder.add(LPNormIOConstants.LOAD_HAS_PHASE_TAG, phaseBuilder);
       loadBuilder = loadBuilder.add(LPNormIOConstants.LOAD_REAL_TAG, realBuilder);
       loadBuilder = loadBuilder.add(LPNormIOConstants.LOAD_REACTIVE_TAG, reactiveBuilder);
       loadBuilder = loadBuilder.add(LPNormIOConstants.LOAD_IS_CRITICAL_LOAD_TAG, isCritical);
       
       arrayBuilder = arrayBuilder.add(loadBuilder);
     }

     return builder.add(LPNormIOConstants.LOADS_TAG,arrayBuilder);
   }

  
  
  /**
   * Write the generator data to a file
   * @param buses
   * @param model
   * @param builder
   * @return
   */
  private JSONObjectBuilder writeGeneratorData(Collection<? extends Generator> generators, ElectricPowerModel model, JSONObjectBuilder builder) {
    
    JSONArrayBuilder arrayBuilder = JSON.getDefaultJSON().createArrayBuilder();
     
     for (Generator gen : generators) {
       Bus genBus = model.getNode(gen).getBus();
       
       // don't add a bus if it does not exist or is not active
       if (!OptimizationConstants.allActive(null, genBus, gen)) {
         continue;
       }
             
             
       JSONArrayBuilder phaseBuilder = JSON.getDefaultJSON().createArrayBuilder();
       phaseBuilder = phaseBuilder.add(gen.getAttribute(Generator.HAS_PHASE_A_KEY, Boolean.class));
       phaseBuilder = phaseBuilder.add(gen.getAttribute(Generator.HAS_PHASE_B_KEY, Boolean.class));
       phaseBuilder = phaseBuilder.add(gen.getAttribute(Generator.HAS_PHASE_C_KEY, Boolean.class));

       JSONArrayBuilder realBuilder = JSON.getDefaultJSON().createArrayBuilder();
       realBuilder = realBuilder.add(gen.getAttribute(Generator.DESIRED_REAL_GENERATION_A_KEY, Number.class).doubleValue());
       realBuilder = realBuilder.add(gen.getAttribute(Generator.DESIRED_REAL_GENERATION_B_KEY, Number.class).doubleValue());
       realBuilder = realBuilder.add(gen.getAttribute(Generator.DESIRED_REAL_GENERATION_C_KEY, Number.class).doubleValue());

       JSONArrayBuilder reactiveBuilder = JSON.getDefaultJSON().createArrayBuilder();
       reactiveBuilder = reactiveBuilder.add(gen.getAttribute(Generator.DESIRED_REACTIVE_GENERATION_A_KEY, Number.class).doubleValue());
       reactiveBuilder = reactiveBuilder.add(gen.getAttribute(Generator.DESIRED_REACTIVE_GENERATION_B_KEY, Number.class).doubleValue());
       reactiveBuilder = reactiveBuilder.add(gen.getAttribute(Generator.DESIRED_REACTIVE_GENERATION_C_KEY, Number.class).doubleValue());
       
       double microgridCost  = gen.getAttribute(AlgorithmConstants.MICROGRID_COST_KEY) == null ? 0.0 : gen.getAttribute(AlgorithmConstants.MICROGRID_COST_KEY,Number.class).doubleValue();
       double microgridFixedCost  = gen.getAttribute(AlgorithmConstants.MICROGRID_FIXED_COST_KEY) == null ? 0.0 : gen.getAttribute(AlgorithmConstants.MICROGRID_FIXED_COST_KEY,Number.class).doubleValue();
       double microgridMax  = gen.getAttribute(AlgorithmConstants.MAX_MICROGRID_KEY) == null ? 0.0 : gen.getAttribute(AlgorithmConstants.MAX_MICROGRID_KEY,Number.class).doubleValue();
       boolean isNew = microgridCost > 0 || microgridFixedCost > 0;
              
       JSONObjectBuilder genBuilder = JSON.getDefaultJSON().createObjectBuilder();
       genBuilder = genBuilder.add(LPNormIOConstants.GEN_ID_TAG, gen.getAttribute(tag).toString());
       genBuilder = genBuilder.add(LPNormIOConstants.GEN_BUS_ID_TAG, genBus.getAttribute(tag).toString());
       genBuilder = genBuilder.add(LPNormIOConstants.GEN_HAS_PHASE_TAG, phaseBuilder);
       genBuilder = genBuilder.add(LPNormIOConstants.GEN_REAL_TAG, realBuilder);
       genBuilder = genBuilder.add(LPNormIOConstants.GEN_REACTIVE_TAG, reactiveBuilder);       
       genBuilder = genBuilder.add(LPNormIOConstants.GEN_MICROGRID_COST_TAG, microgridCost);       
       genBuilder = genBuilder.add(LPNormIOConstants.GEN_MICROGRID_FIXED_COST_TAG, microgridFixedCost);       
       genBuilder = genBuilder.add(LPNormIOConstants.GEN_MICROGRID_MAX_TAG, microgridMax);       
       genBuilder = genBuilder.add(LPNormIOConstants.GEN_MICROGRID_IS_NEW_TAG, isNew);       
                
       arrayBuilder = arrayBuilder.add(genBuilder);
     }

     return builder.add(LPNormIOConstants.GENERATORS_TAG,arrayBuilder);
   }
  
  
  /**
   * Makes each line have its own line code data
   * @param connections
   * @return
   */
  private JSONObjectBuilder writeLineCodeData(ArrayList<ElectricPowerFlowConnection> connections, JSONObjectBuilder builder) {
    JSONArrayBuilder arrayBuilder = JSON.getDefaultJSON().createArrayBuilder();
    
    for (int i = 0; i < connections.size(); ++i) {
      ElectricPowerFlowConnection connection = connections.get(i);       
      double length = connection.getAttribute(ElectricPowerFlowConnection.LENGTH_KEY, Number.class).doubleValue();
      
      
      JSONArrayBuilder reactanceBuilder = JSON.getDefaultJSON().createArrayBuilder();
      JSONArrayBuilder xABuilder  = JSON.getDefaultJSON().createArrayBuilder();
      JSONArrayBuilder xBBuilder  = JSON.getDefaultJSON().createArrayBuilder();
      JSONArrayBuilder xCBuilder  = JSON.getDefaultJSON().createArrayBuilder();
      
      xABuilder = xABuilder.add(connection.getAttribute(ElectricPowerFlowConnection.REACTANCE_PHASE_A_KEY, Number.class).doubleValue() / length);
      xABuilder = xABuilder.add(connection.getAttribute(ElectricPowerFlowConnection.REACTANCE_PHASE_AB_KEY, Number.class).doubleValue() / length);
      xABuilder = xABuilder.add(connection.getAttribute(ElectricPowerFlowConnection.REACTANCE_PHASE_AC_KEY, Number.class).doubleValue() / length);
      reactanceBuilder = reactanceBuilder.add(xABuilder);

      xBBuilder = xBBuilder.add(connection.getAttribute(ElectricPowerFlowConnection.REACTANCE_PHASE_BA_KEY, Number.class).doubleValue() / length);
      xBBuilder = xBBuilder.add(connection.getAttribute(ElectricPowerFlowConnection.REACTANCE_PHASE_B_KEY, Number.class).doubleValue() / length);
      xBBuilder = xBBuilder.add(connection.getAttribute(ElectricPowerFlowConnection.REACTANCE_PHASE_BC_KEY, Number.class).doubleValue() / length);
      reactanceBuilder = reactanceBuilder.add(xBBuilder);

      xCBuilder = xCBuilder.add(connection.getAttribute(ElectricPowerFlowConnection.REACTANCE_PHASE_CA_KEY, Number.class).doubleValue() / length);
      xCBuilder = xCBuilder.add(connection.getAttribute(ElectricPowerFlowConnection.REACTANCE_PHASE_CB_KEY, Number.class).doubleValue() / length);
      xCBuilder = xCBuilder.add(connection.getAttribute(ElectricPowerFlowConnection.REACTANCE_PHASE_C_KEY, Number.class).doubleValue() / length);
      reactanceBuilder = reactanceBuilder.add(xCBuilder);
      
      JSONArrayBuilder resistanceBuilder = JSON.getDefaultJSON().createArrayBuilder();
      JSONArrayBuilder rABuilder  = JSON.getDefaultJSON().createArrayBuilder();
      JSONArrayBuilder rBBuilder  = JSON.getDefaultJSON().createArrayBuilder();
      JSONArrayBuilder rCBuilder  = JSON.getDefaultJSON().createArrayBuilder();
      
      rABuilder = rABuilder.add(connection.getAttribute(ElectricPowerFlowConnection.RESISTANCE_PHASE_A_KEY, Number.class).doubleValue() / length);
      rABuilder = rABuilder.add(connection.getAttribute(ElectricPowerFlowConnection.RESISTANCE_PHASE_AB_KEY, Number.class).doubleValue() / length);
      rABuilder = rABuilder.add(connection.getAttribute(ElectricPowerFlowConnection.RESISTANCE_PHASE_AC_KEY, Number.class).doubleValue() / length);
      resistanceBuilder = resistanceBuilder.add(rABuilder);

      rBBuilder = rBBuilder.add(connection.getAttribute(ElectricPowerFlowConnection.RESISTANCE_PHASE_BA_KEY, Number.class).doubleValue() / length);
      rBBuilder = rBBuilder.add(connection.getAttribute(ElectricPowerFlowConnection.RESISTANCE_PHASE_B_KEY, Number.class).doubleValue() / length);
      rBBuilder = rBBuilder.add(connection.getAttribute(ElectricPowerFlowConnection.RESISTANCE_PHASE_BC_KEY, Number.class).doubleValue() / length);
      resistanceBuilder = resistanceBuilder.add(rBBuilder);

      rCBuilder = rCBuilder.add(connection.getAttribute(ElectricPowerFlowConnection.RESISTANCE_PHASE_CA_KEY, Number.class).doubleValue() / length);
      rCBuilder = rCBuilder.add(connection.getAttribute(ElectricPowerFlowConnection.RESISTANCE_PHASE_CB_KEY, Number.class).doubleValue() / length);
      rCBuilder = rCBuilder.add(connection.getAttribute(ElectricPowerFlowConnection.RESISTANCE_PHASE_C_KEY, Number.class).doubleValue() / length);
      resistanceBuilder = resistanceBuilder.add(rCBuilder);

      JSONObjectBuilder codeBuilder = JSON.getDefaultJSON().createObjectBuilder();
      codeBuilder = codeBuilder.add(LPNormIOConstants.LINE_CODE_ID_TAG, i);
      codeBuilder = codeBuilder.add(LPNormIOConstants.LINE_CODE_PHASES_TAG, connection.getAttribute(ElectricPowerFlowConnection.NUMBER_OF_PHASES_KEY, Number.class).intValue());
      codeBuilder = codeBuilder.add(LPNormIOConstants.LINE_CODE_RMATRIX_TAG, resistanceBuilder);
      codeBuilder = codeBuilder.add(LPNormIOConstants.LINE_CODE_XMATRIX_TAG, reactanceBuilder);
      arrayBuilder = arrayBuilder.add(codeBuilder);
    }

    return builder.add(LPNormIOConstants.LINE_CODES_TAG,arrayBuilder);
  }
  
  /**
   * Write all the line data
   * @param connections
   * @param model
   * @param builder
   * @return
   */
  private JSONObjectBuilder writeLineData(ArrayList<ElectricPowerFlowConnection> connections, ElectricPowerModel model, JSONObjectBuilder builder) {
    
    JSONArrayBuilder arrayBuilder = JSON.getDefaultJSON().createArrayBuilder();
     
     for (int i = 0; i < connections.size(); ++i) {
       ElectricPowerFlowConnection connection = connections.get(i);
       
       Bus bus1 = model.getFirstNode(connection).getBus();
       Bus bus2 = model.getSecondNode(connection).getBus();
       
       // don't add a bus if it does not exist or is not active
       if (!OptimizationConstants.allActive(null, connection, bus1, bus2)) {
         continue;
       }

       Double constructionCost = connection.getAttribute(AlgorithmConstants.LINE_CONSTRUCTION_COST_KEY, Double.class);
       Double hardenCost = connection.getAttribute(AlgorithmConstants.LINE_HARDEN_COST_KEY, Double.class);
       Double switchCost = connection.getAttribute(AlgorithmConstants.LINE_SWITCH_COST_KEY, Double.class);       
       boolean isNew = constructionCost != null && constructionCost > 0;
       boolean hasSwitch = connection.getAttribute(ElectricPowerFlowConnection.HAS_SWITCH_KEY) == null ? false : connection.getAttribute(ElectricPowerFlowConnection.HAS_SWITCH_KEY, Boolean.class);
       
//       System.out.println(connection.getAttribute(AlgorithmConstants.LINE_SWITCH_COST_KEY));
       
       int numPoles =  connection.getAttribute(ElectricPowerFlowConnection.NUMBER_OF_POLES_KEY) == null ? 2 : connection.getAttribute(ElectricPowerFlowConnection.NUMBER_OF_POLES_KEY, Number.class).intValue();
       
       JSONArrayBuilder phaseBuilder = JSON.getDefaultJSON().createArrayBuilder();
       phaseBuilder = phaseBuilder.add(connection.getAttribute(ElectricPowerFlowConnection.IS_PHASE_A_KEY, Boolean.class));
       phaseBuilder = phaseBuilder.add(connection.getAttribute(ElectricPowerFlowConnection.IS_PHASE_B_KEY, Boolean.class));
       phaseBuilder = phaseBuilder.add(connection.getAttribute(ElectricPowerFlowConnection.IS_PHASE_C_KEY, Boolean.class));
       
       JSONObjectBuilder lineBuilder = JSON.getDefaultJSON().createObjectBuilder();
       lineBuilder = lineBuilder.add(LPNormIOConstants.LINE_ID_TAG, connection.getAttribute(tag).toString());
       lineBuilder = lineBuilder.add(LPNormIOConstants.LINE_BUS1_ID_TAG, bus1.getAttribute(tag).toString());
       lineBuilder = lineBuilder.add(LPNormIOConstants.LINE_BUS2_ID_TAG, bus2.getAttribute(tag).toString());
       lineBuilder = lineBuilder.add(LPNormIOConstants.LINE_HAS_PHASE_TAG, phaseBuilder);
       lineBuilder = lineBuilder.add(LPNormIOConstants.LINE_CAPACITY_TAG, connection.getCapacityRating());
       lineBuilder = lineBuilder.add(LPNormIOConstants.LINE_LENGTH_TAG, connection.getAttribute(ElectricPowerFlowConnection.LENGTH_KEY, Number.class).doubleValue());
       lineBuilder = lineBuilder.add(LPNormIOConstants.LINE_NUM_PHASES_TAG, connection.getAttribute(ElectricPowerFlowConnection.NUMBER_OF_PHASES_KEY, Number.class).intValue());
       lineBuilder = lineBuilder.add(LPNormIOConstants.LINE_IS_TRANSFORMER_TAG, connection instanceof Transformer);
       lineBuilder = lineBuilder.add(LPNormIOConstants.LINE_LINE_CODE_TAG, i);
       lineBuilder = lineBuilder.add(LPNormIOConstants.LINE_NUM_POLES_TAG, numPoles);
       
       if (constructionCost != null) {
         lineBuilder = lineBuilder.add(LPNormIOConstants.LINE_CONSTRUCTION_COST_TAG, constructionCost);
       }
       if (hardenCost != null) {
         lineBuilder = lineBuilder.add(LPNormIOConstants.LINE_HARDEN_COST_TAG, hardenCost);
         lineBuilder = lineBuilder.add(LPNormIOConstants.LINE_CAN_HARDEN_TAG, true);
       }
       
       if (switchCost != null) {
         lineBuilder = lineBuilder.add(LPNormIOConstants.LINE_SWITCH_COST_TAG, switchCost);
       }
       lineBuilder = lineBuilder.add(LPNormIOConstants.LINE_IS_NEW_TAG, isNew);
       lineBuilder = lineBuilder.add(LPNormIOConstants.LINE_HAS_SWITCH_TAG, hasSwitch);
       
       arrayBuilder = arrayBuilder.add(lineBuilder);
     }

     return builder.add(LPNormIOConstants.LINES_TAG,arrayBuilder);
   }

   /**
    * Write the scenario data
    * @param scenario
    * @param connections
    * @return
    */
   private JSONArrayBuilder writeScenarioData(Scenario scenario, ArrayList<ElectricPowerFlowConnection> connections, JSONArrayBuilder arrayBuilder) {
     JSONObjectBuilder builder = JSON.getDefaultJSON().createObjectBuilder();
     
     builder.add(LPNormIOConstants.SCENARIO_ID_TAG, scenario.getIndex()+"");

     JSONArrayBuilder hardenedBuilder = JSON.getDefaultJSON().createArrayBuilder();
     for (ElectricPowerFlowConnection connection : connections) {
       Boolean isDisabled = scenario.getModification(connection, AlgorithmConstants.HARDENED_DISABLED_KEY, Boolean.class);
       if (isDisabled == null) {
         isDisabled = false;
       }
       if (isDisabled) {
         hardenedBuilder = hardenedBuilder.add(connection.getAttribute(tag).toString());
       }
     }

     JSONArrayBuilder disabledBuilder = JSON.getDefaultJSON().createArrayBuilder();
     for (ElectricPowerFlowConnection connection : connections) {
       Boolean isDisabled = scenario.getModification(connection, ElectricPowerFlowConnection.IS_FAILED_KEY, Boolean.class);
       if (isDisabled == null) {
         isDisabled = false;
       }

       if (isDisabled) {
         disabledBuilder = disabledBuilder.add(connection.getAttribute(tag).toString());
       }
     }     
     builder.add(LPNormIOConstants.SCENARIO_HARDENED_DISABLED_TAG, hardenedBuilder);
     builder.add(LPNormIOConstants.SCENARIO_DISABLED_TAG, disabledBuilder);
     return arrayBuilder.add(builder);
   }

  
}
