package gov.lanl.micot.infrastructure.ep.optimize.jump;

import java.util.Collection;

import gov.lanl.micot.infrastructure.ep.model.Bus;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.Generator;
import gov.lanl.micot.infrastructure.ep.model.GeneratorDefaults;
import gov.lanl.micot.infrastructure.ep.model.Load;
import gov.lanl.micot.infrastructure.ep.model.ShuntCapacitor;
import gov.lanl.micot.infrastructure.ep.model.ShuntCapacitorSwitch;
import gov.lanl.micot.infrastructure.ep.model.Transformer;
import gov.lanl.micot.infrastructure.ep.model.matpower.MatPowerModelConstants;
import gov.lanl.micot.infrastructure.ep.optimize.OptimizationConstants;
import gov.lanl.micot.infrastructure.model.Asset;
import gov.lanl.micot.infrastructure.optimize.jump.JumpOptimizerFlags;
import gov.lanl.micot.util.io.json.JSON;
import gov.lanl.micot.util.io.json.JSONArrayBuilder;
import gov.lanl.micot.util.io.json.JSONObjectBuilder;

/**
 * General class for aiding 
 * @author 210117
 *
 */
public class PowerModelJLUtilities {

  private static final String BRANCH_TAG = "branch";
  private static final String BUS_TAG = "bus";  
  private static final String GENERATOR_TAG = "gen";
  private static final String GENERATOR_COST_TAG = "gencost";
  
  private static final String INDEX_TAG = "index";
  
  // branch related fields
  private static final String ANG_MAX_TAG = "angmax";
  private static final String ANG_MIN_TAG = "angmin";
  private static final String LINE_CHARGING_TAG = "br_b";
  private static final String LINE_RESISTANCE_TAG = "br_r";
  private static final String STATUS_TAG = "br_status";
  private static final String LINE_REACTANCE_TAG = "br_x";
  private static final String FROM_BUS_TAG = "f_bus";
  private static final String RATE_A_TAG = "rate_a";
  private static final String RATE_B_TAG = "rate_b";
  private static final String RATE_C_TAG = "rate_c";
  private static final String SHIFT_TAG = "shift";
  private static final String TO_BUS_TAG = "t_bus";
  private static final String TAP_TAG = "tap";

  // bus related fields
  private static final String AREA_TAG = "area";
  private static final String BASE_KV_TAG = "base_kv";
  private static final String REACTIVE_SHUNT_TAG = "bs";
  private static final String BUS_ID_TAG = "bus_i";
  private static final String BUS_TYPE_TAG = "bus_type";
  private static final String REAL_SHUNT_TAG = "gs";
  private static final String REAL_LOAD_TAG = "pd";
  private static final String REACTIVE_LOAD_TAG = "qd";
  private static final String VOLTAGE_ANGLE_TAG = "va";
  private static final String VOLTAGE_MAGNITUDE_TAG = "vm";
  private static final String VOLTAGE_MAX_TAG = "vmax";
  private static final String VOLTAGE_MIN_TAG = "vmin";
  private static final String ZONE_TAG = "zone";
  
  // generator related fields
  private static final String AREA_PARTICIPATION_FACTOR_TAG = "apf";
  private static final String GENERATOR_BUS_TAG = "gen_bus";
  private static final String GENERATOR_STATUS_TAG = "gen_status";
  private static final String MBASE_TAG = "mbase";
  private static final String LOWER_REAL_POWER_OUTPUT_TAG = "pc1";
  private static final String UPPER_REAL_POWER_OUTPUT_TAG = "pc2";
  private static final String REAL_GENERATION_TAG = "pg";
  private static final String REAL_GENERATION_MAX_TAG = "pmax";
  private static final String REAL_GENERATION_MIN_TAG = "pmin";
  private static final String MAX_REACTIVE_GENERATION_PC1_TAG = "qc1max";
  private static final String MIN_REACTIVE_GENERATION_PC1_TAG = "qc1min";
  private static final String MAX_REACTIVE_GENERATION_PC2_TAG = "qc2max";
  private static final String MIN_REACTIVE_GENERATION_PC2_TAG = "qc2min";
  private static final String REACTIVE_GENERATION_TAG = "qg";
  private static final String REACTIVE_GENERATION_MAX_TAG = "qmax";
  private static final String REACTIVE_GENERATION_MIN_TAG = "qmin";  
  private static final String RAMP_30_TAG = "ramp_30";
  private static final String RAMP_AGC_TAG = "ramp_agc";
  private static final String RAMP_Q_TAG = "ramp_q";
  private static final String VOLTAGE_SETPOINT_TAG = "vg";

  // generator economic cost parameters
  private static final String GENERATOR_COST_FUNCTION_TAG = "cost";
  private static final String GENERATOR_COST_MODEL_TAG = "model";
  private static final String GENERATOR_COST_POINTS_TAG = "ncost";
  private static final String GENERATOR_COST_SHUTDOWN_TAG = "shutdown";
  private static final String GENERATOR_COST_STARTUP_TAG = "startup";
  
  private static final double DEFAULT_ANGLE_DIFF_LIMIT = Math.toDegrees(Math.PI / 2.0);

  
  /**
   * No constructor
   */
  private PowerModelJLUtilities() {    
  }

  /**
   * 
   * @param optimizerFlags
   * @param builder
   * @return
   */
  public static JSONObjectBuilder writeSolver(JumpOptimizerFlags optimizerFlags, JSONObjectBuilder builder) {
    for (String key : optimizerFlags.keySet()) {
      Object value = optimizerFlags.get(key);
      if (value instanceof Boolean) {     
        builder = builder.add(key,(Boolean)value);
      }
      else if (value instanceof Integer || value instanceof Long) {     
        builder = builder.add(key,((Number)value).intValue());
      }
      else if (value instanceof Number) {     
        builder = builder.add(key,((Number)value).doubleValue());
      }
      else {
        builder = builder.add(key,value.toString());        
      }
    }
    
    
    return builder;
  }
  
  /**
   * Write the branch data in PowerModel.jl format
   * @param connections
   * @param model
   * @param data
   * @return
   */
  public static JSONObjectBuilder writePowerModelJLBranches(Collection<? extends ElectricPowerFlowConnection> connections, ElectricPowerModel model, JSONObjectBuilder builder) {
    JSONArrayBuilder arrayBuilder = JSON.getDefaultJSON().createArrayBuilder();
    
    // write the branch data
    for (ElectricPowerFlowConnection line : connections) {
      Bus bus_i = model.getFirstNode(line).getBus();
      Bus bus_j = model.getSecondNode(line).getBus();

      if (!OptimizationConstants.allActive(null,line,bus_i,bus_j)) {
        continue;
      }

      JSONObjectBuilder branchBuilder = JSON.getDefaultJSON().createObjectBuilder();
      
      double angleDiffLimit = line.getAttribute(ElectricPowerFlowConnection.PHASE_ANGLE_DIFFERENCE_LIMIT_KEY) == null ? DEFAULT_ANGLE_DIFF_LIMIT : line.getAttribute(ElectricPowerFlowConnection.PHASE_ANGLE_DIFFERENCE_LIMIT_KEY, Number.class).doubleValue();
      double shift = line.getAttribute(Transformer.TAP_ANGLE_KEY) == null ? 0.0 : line.getAttribute(Transformer.TAP_ANGLE_KEY,Number.class).doubleValue();
      double tap = line.getAttribute(Transformer.TAP_RATIO_KEY) == null ? 1.0 : line.getAttribute(Transformer.TAP_RATIO_KEY,Number.class).doubleValue();
      double rateA = Double.isInfinite(line.getCapacityRating()) ? 10000 : line.getCapacityRating();
      double rateB = Double.isInfinite(line.getShortTermEmergencyCapacityRating()) ? 10000 : line.getShortTermEmergencyCapacityRating();
      double rateC = Double.isInfinite(line.getLongTermEmergencyCapacityRating()) ? 10000 : line.getLongTermEmergencyCapacityRating();
      
      branchBuilder.add(INDEX_TAG, line.getAttribute(Asset.ASSET_ID_KEY, Number.class).intValue());
      branchBuilder.add(ANG_MAX_TAG, angleDiffLimit / 2.0);
      branchBuilder.add(ANG_MIN_TAG, -angleDiffLimit / 2.0);
      branchBuilder.add(LINE_CHARGING_TAG, line.getLineCharging());
      branchBuilder.add(LINE_RESISTANCE_TAG, line.getResistance());
      branchBuilder.add(STATUS_TAG, line.getActualStatus() ? 1 : 0);
      branchBuilder.add(LINE_REACTANCE_TAG, line.getReactance());
      branchBuilder.add(FROM_BUS_TAG, bus_j.getAttribute(Asset.ASSET_ID_KEY, Number.class).intValue());
      branchBuilder.add(RATE_A_TAG, rateA);
      branchBuilder.add(RATE_B_TAG, rateB);
      branchBuilder.add(RATE_C_TAG, rateC);
      branchBuilder.add(SHIFT_TAG, shift);
      branchBuilder.add(TO_BUS_TAG, bus_i.getAttribute(Asset.ASSET_ID_KEY, Number.class).intValue());
      branchBuilder.add(TAP_TAG, tap);
      
      arrayBuilder = arrayBuilder.add(branchBuilder);
    }

    return builder.add(BRANCH_TAG, arrayBuilder);
  }
  
  /**
   * Write the power system bus data
   * @param buses
   * @param model
   * @param data
   * @return
   */
  public static JSONObjectBuilder writePowerModelJLBuses(Collection<? extends Bus> buses, ElectricPowerModel model, JSONObjectBuilder builder) {
    JSONArrayBuilder arrayBuilder = JSON.getDefaultJSON().createArrayBuilder();
    
    for (Bus bus : buses) {
      // don't add a bus if it does not exist
      if (!OptimizationConstants.allActive(null,bus)) {
        continue;
      }
      
      double realLoad = 0;
      double reactiveLoad = 0;
      for (Load load : model.getNode(bus).getComponents(Load.class)) {
        if (OptimizationConstants.allActive(null,bus, load)) {
          realLoad += load.getDesiredRealLoad().doubleValue();
          reactiveLoad += load.getDesiredReactiveLoad().doubleValue();
        }
      }

      double realShunt = 0;
      double reactiveShunt = 0;
      for (ShuntCapacitor capacitor : model.getNode(bus).getComponents(ShuntCapacitor.class)) {
        if (OptimizationConstants.allActive(null,bus, capacitor)) {
          realShunt += capacitor.getRealCompensation();
          reactiveShunt += capacitor.getReactiveCompensation();
        }        
      }
      for (ShuntCapacitorSwitch capacitor : model.getNode(bus).getComponents(ShuntCapacitorSwitch.class)) {
        if (OptimizationConstants.allActive(null,bus, capacitor)) {
          realShunt += capacitor.getRealCompensation().doubleValue();
          reactiveShunt += capacitor.getReactiveCompensation().doubleValue();
        }        
      }
   
      int  type = 1;
      if (model.getSlackBuses().contains(bus)) {
        type = 3;        
      }
      else if (model.getNode(bus).getComponents(Generator.class).size() > 0) {
        Generator generator = model.getNode(bus).getGenerator();
        type = generator.getType().getGeneratorType();
      }
      
      JSONObjectBuilder busBuilder = JSON.getDefaultJSON().createObjectBuilder();

      int area = model.getControlArea(bus) == null ? -1 : model.getControlArea(bus).getAttribute(Asset.ASSET_ID_KEY, Number.class).intValue();
      int zone = model.getZone(bus) == null ? -1 : model.getZone(bus).getAttribute(Asset.ASSET_ID_KEY, Number.class).intValue();
      
      busBuilder.add(AREA_TAG, area);
      busBuilder.add(BASE_KV_TAG, bus.getSystemVoltageKV());
      busBuilder.add(REACTIVE_SHUNT_TAG, reactiveShunt);
      busBuilder.add(BUS_ID_TAG, bus.getAttribute(Bus.ASSET_ID_KEY,Number.class).intValue());
      busBuilder.add(INDEX_TAG, bus.getAttribute(Bus.ASSET_ID_KEY,Number.class).intValue());
      busBuilder.add(BUS_TYPE_TAG, type);
      busBuilder.add(REAL_SHUNT_TAG, realShunt);
      busBuilder.add(REAL_LOAD_TAG, realLoad);
      busBuilder.add(REACTIVE_LOAD_TAG, reactiveLoad);
      busBuilder.add(VOLTAGE_ANGLE_TAG, bus.getPhaseAngle().doubleValue());
      busBuilder.add(VOLTAGE_MAGNITUDE_TAG, bus.getVoltagePU().doubleValue());
      busBuilder.add(VOLTAGE_MAX_TAG, bus.getMaximumVoltagePU());
      busBuilder.add(VOLTAGE_MIN_TAG, bus.getMinimumVoltagePU());
      busBuilder.add(ZONE_TAG, zone);
      
      arrayBuilder = arrayBuilder.add(busBuilder);
    }

    return builder.add(BUS_TAG,arrayBuilder);
  }
 
  /**
   * Write the generator data
   * @param generators
   * @param model
   * @param data
   * @return
   */
  public static JSONObjectBuilder writePowerModelJLGenerators(Collection<? extends Generator> generators, ElectricPowerModel model, JSONObjectBuilder builder ) {
    JSONArrayBuilder arrayBuilder = JSON.getDefaultJSON().createArrayBuilder();

    for (Generator gen : generators) {
      Bus bus_i = model.getNode(gen).getBus();

      if (!OptimizationConstants.allActive(null,gen,bus_i)) {
        continue;
      }
      
      double apf = gen.getAttribute(MatPowerModelConstants.MATPOWER_AREA_PARTICIPATION_FACTOR_KEY) == null ? 0.0 : 
        gen.getAttribute(MatPowerModelConstants.MATPOWER_AREA_PARTICIPATION_FACTOR_KEY,Number.class).doubleValue();

      double pc1 = gen.getAttribute(MatPowerModelConstants.MATPOWER_GENERATOR_MIN_REAL_CURVE_KEY) == null ? 0.0 : 
        gen.getAttribute(MatPowerModelConstants.MATPOWER_GENERATOR_MIN_REAL_CURVE_KEY,Number.class).doubleValue();

      double pc2 = gen.getAttribute(MatPowerModelConstants.MATPOWER_GENERATOR_MAX_REAL_CURVE_KEY) == null ? 0.0 : 
        gen.getAttribute(MatPowerModelConstants.MATPOWER_GENERATOR_MAX_REAL_CURVE_KEY,Number.class).doubleValue();

      double qc1max = gen.getAttribute(MatPowerModelConstants.MATPOWER_GENERATOR_MAX_REACTIVE_CURVE_FOR_MIN_REAL_KEY) == null ? 0.0 : 
        gen.getAttribute(MatPowerModelConstants.MATPOWER_GENERATOR_MAX_REACTIVE_CURVE_FOR_MIN_REAL_KEY,Number.class).doubleValue();

      double qc1min = gen.getAttribute(MatPowerModelConstants.MATPOWER_GENERATOR_MIN_REACTIVE_CURVE_FOR_MIN_REAL_KEY) == null ? 0.0 : 
        gen.getAttribute(MatPowerModelConstants.MATPOWER_GENERATOR_MIN_REACTIVE_CURVE_FOR_MIN_REAL_KEY,Number.class).doubleValue();

      double qc2max = gen.getAttribute(MatPowerModelConstants.MATPOWER_GENERATOR_MAX_REACTIVE_CURVE_FOR_MAX_REAL_KEY) == null ? 0.0 : 
        gen.getAttribute(MatPowerModelConstants.MATPOWER_GENERATOR_MAX_REACTIVE_CURVE_FOR_MAX_REAL_KEY,Number.class).doubleValue();

      double qc2min = gen.getAttribute(MatPowerModelConstants.MATPOWER_GENERATOR_MIN_REACTIVE_CURVE_FOR_MAX_REAL_KEY) == null ? 0.0 : 
        gen.getAttribute(MatPowerModelConstants.MATPOWER_GENERATOR_MIN_REACTIVE_CURVE_FOR_MAX_REAL_KEY,Number.class).doubleValue();

      double ramp30 = gen.getAttribute(MatPowerModelConstants.MATPOWER_THIRTY_MINUTE_RESERVE_RAMP_RATE_KEY) == null ? 0.0 : 
        gen.getAttribute(MatPowerModelConstants.MATPOWER_THIRTY_MINUTE_RESERVE_RAMP_RATE_KEY,Number.class).doubleValue();

      double rampagc = gen.getAttribute(MatPowerModelConstants.MATPOWER_TEN_MINUTE_RESERVE_RAMP_RATE_KEY) == null ? 0.0 : 
        gen.getAttribute(MatPowerModelConstants.MATPOWER_TEN_MINUTE_RESERVE_RAMP_RATE_KEY,Number.class).doubleValue();

      double rampq = gen.getAttribute(MatPowerModelConstants.MATPOWER_REACTIVE_RAMP_RATE_KEY) == null ? 0.0 : 
        gen.getAttribute(MatPowerModelConstants.MATPOWER_REACTIVE_RAMP_RATE_KEY,Number.class).doubleValue();
      
      double mvaBase = gen.getAttribute(Generator.MVA_BASE_KEY) == null ? 0.0 : gen.getAttribute(Generator.MVA_BASE_KEY, Number.class).doubleValue();

      JSONObjectBuilder generatorBuilder = JSON.getDefaultJSON().createObjectBuilder();
   
      generatorBuilder.add(AREA_PARTICIPATION_FACTOR_TAG, apf);
      generatorBuilder.add(GENERATOR_BUS_TAG, bus_i.getAttribute(Bus.ASSET_ID_KEY,Number.class).intValue());
      generatorBuilder.add(GENERATOR_STATUS_TAG, gen.getActualStatus() ? 1 : 0);
      generatorBuilder.add(MBASE_TAG, mvaBase);
      generatorBuilder.add(LOWER_REAL_POWER_OUTPUT_TAG, pc1);
      generatorBuilder.add(UPPER_REAL_POWER_OUTPUT_TAG, pc2);
      generatorBuilder.add(REAL_GENERATION_TAG, gen.getDesiredRealGeneration().doubleValue());
      generatorBuilder.add(REAL_GENERATION_MAX_TAG, gen.getDesiredRealGenerationMax());
      generatorBuilder.add(REAL_GENERATION_MIN_TAG, gen.getRealGenerationMin());
      generatorBuilder.add(MAX_REACTIVE_GENERATION_PC1_TAG, qc1max);
      generatorBuilder.add(MIN_REACTIVE_GENERATION_PC1_TAG, qc1min);
      generatorBuilder.add(MAX_REACTIVE_GENERATION_PC2_TAG, qc2max);
      generatorBuilder.add(MIN_REACTIVE_GENERATION_PC2_TAG, qc2min);
      generatorBuilder.add(REACTIVE_GENERATION_TAG, gen.getDesiredReactiveGeneration().doubleValue());
      generatorBuilder.add(REACTIVE_GENERATION_MAX_TAG, gen.getDesiredReactiveMax());
      generatorBuilder.add(REACTIVE_GENERATION_MIN_TAG, gen.getReactiveMin());
      generatorBuilder.add(RAMP_30_TAG, ramp30);
      generatorBuilder.add(RAMP_AGC_TAG, rampagc);
      generatorBuilder.add(RAMP_Q_TAG, rampq);
      generatorBuilder.add(VOLTAGE_SETPOINT_TAG, bus_i.getVoltagePU().doubleValue());
      generatorBuilder.add(INDEX_TAG, gen.getAttribute(Generator.ASSET_ID_KEY,Number.class).intValue());

      
      arrayBuilder = arrayBuilder.add(generatorBuilder);
    }

    return builder.add(GENERATOR_TAG, arrayBuilder);
  }
  
  /**
   * Write the generator cost data
   * @param generators
   * @param model
   * @param data
   * @return
   */
  public static JSONObjectBuilder writePowerModelJLGeneratorCosts(Collection<? extends Generator> generators, ElectricPowerModel model, JSONObjectBuilder builder) {
    GeneratorDefaults defaults = GeneratorDefaults.getInstance();

    JSONArrayBuilder arrayBuilder = JSON.getDefaultJSON().createArrayBuilder();

    for (Generator gen : generators) {
      Bus bus_i = model.getNode(gen).getBus();

      if (!OptimizationConstants.allActive(null,gen,bus_i)) {
        continue;
      }
      
      double quadcoeff = defaults.getQuadraticPartOfCost(gen).doubleValue();
      quadcoeff = (quadcoeff == 0) ? 0 : -quadcoeff;
      double linearcoeff = defaults.getLinearPartOfCost(gen).doubleValue();
      linearcoeff = (linearcoeff == 0) ? 0 : -linearcoeff;
      double constant = defaults.getConstantPartOfCost(gen).doubleValue();
      constant = (constant == 0) ? 0 : -constant;

      int m = 2;
      int ncost = 3;
      
      double shutdown = gen.getAttribute(Generator.SHUTDOWN_COST_KEY) == null ? 0.0 : 
        gen.getAttribute(Generator.SHUTDOWN_COST_KEY, Number.class).doubleValue();

      double startup = gen.getAttribute(Generator.STARTUP_COST_KEY) == null ? 0.0 : 
        gen.getAttribute(Generator.STARTUP_COST_KEY, Number.class).doubleValue();

      JSONObjectBuilder generatorBuilder = JSON.getDefaultJSON().createObjectBuilder();
      
      JSONArrayBuilder costBuilder = JSON.getDefaultJSON().createArrayBuilder();
      costBuilder = costBuilder.add(quadcoeff);
      costBuilder = costBuilder.add(linearcoeff);
      costBuilder = costBuilder.add(constant);
      
      generatorBuilder = generatorBuilder.add(GENERATOR_COST_FUNCTION_TAG, costBuilder);
      generatorBuilder = generatorBuilder.add(GENERATOR_COST_MODEL_TAG, m);
      generatorBuilder = generatorBuilder.add(GENERATOR_COST_POINTS_TAG, ncost);
      generatorBuilder = generatorBuilder.add(GENERATOR_COST_SHUTDOWN_TAG, shutdown);
      generatorBuilder = generatorBuilder.add(GENERATOR_COST_STARTUP_TAG, startup);
      generatorBuilder.add(INDEX_TAG, gen.getAttribute(Generator.ASSET_ID_KEY,Number.class).intValue());
      
      arrayBuilder = arrayBuilder.add(generatorBuilder);
    }

    return builder.add(GENERATOR_COST_TAG, arrayBuilder);
  }
  
}
