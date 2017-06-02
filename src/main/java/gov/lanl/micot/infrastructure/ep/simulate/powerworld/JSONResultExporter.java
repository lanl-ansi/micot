package gov.lanl.micot.infrastructure.ep.simulate.powerworld;

import java.io.OutputStream;

import gov.lanl.micot.infrastructure.ep.model.Bus;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.Generator;
import gov.lanl.micot.infrastructure.ep.model.Line;
import gov.lanl.micot.infrastructure.ep.model.Load;
import gov.lanl.micot.infrastructure.ep.model.ShuntCapacitor;
import gov.lanl.micot.infrastructure.ep.model.ShuntCapacitorSwitch;
import gov.lanl.micot.infrastructure.ep.model.Transformer;
import gov.lanl.micot.util.io.json.JSON;
import gov.lanl.micot.util.io.json.JSONArrayBuilder;
import gov.lanl.micot.util.io.json.JSONObjectBuilder;
import gov.lanl.micot.util.io.json.JSONWriter;

/**
 * Export the results in a JSON format
 * @author Russell Bent
 *
 */
public class JSONResultExporter {

  /**
   * Constructor
   */
  public JSONResultExporter() {    
  }
  
  /**
   * Export the results into a JSON format
   * @param ps
   */
  public void exportJSON(OutputStream ps, ElectricPowerModel model) {
    
    // write the results to a json output stream
    JSON json = JSON.getDefaultJSON();
    JSONObjectBuilder mainBuilder = json.createObjectBuilder();

    // get the bus results
    JSONArrayBuilder busesBuilder = json.createArrayBuilder();
    for (Bus bus : model.getBuses()) {
      JSONObjectBuilder busBuilder = json.createObjectBuilder();
      double baseKV = bus.getSystemVoltageKV();
      busBuilder = busBuilder.add("bus_i", bus.toString());
      
      busBuilder = busBuilder.add("vm", bus.getVoltagePU().doubleValue() * baseKV);            
      busBuilder = busBuilder.add("vm_min", bus.getMinimumVoltagePU() * baseKV);            
      busBuilder = busBuilder.add("vm_max", bus.getMaximumVoltagePU() * baseKV);                  
      busBuilder = busBuilder.add("va", bus.getPhaseAngle());
      busBuilder = busBuilder.add("base_kv", baseKV);
      busBuilder = busBuilder.add("owner", bus.getOwnerName());
      busBuilder = busBuilder.add("name", bus.getAttribute(Bus.NAME_KEY, String.class));
      busBuilder = busBuilder.add("status", bus.getActualStatus() && bus.getDesiredStatus());
      busesBuilder = busesBuilder.add(busBuilder);
    }
    mainBuilder = mainBuilder.add("bus", busesBuilder);

    // get the branch results
    JSONArrayBuilder branchesBuilder = json.createArrayBuilder();
    for (Line connection : model.getLines()) {
      JSONObjectBuilder branchBuilder = json.createObjectBuilder();
      Bus bus1 = model.getFirstNode(connection).getBus();
      Bus bus2 = model.getSecondNode(connection).getBus();      
      double baseKV = Math.max(bus1.getSystemVoltageKV(), bus2.getSystemVoltageKV());      
      branchBuilder = branchBuilder.add("branch_i", connection.toString());
      branchBuilder = branchBuilder.add("mw_i", connection.getAttribute(ElectricPowerFlowConnection.MW_FLOW_SIDE1_KEY, Number.class).doubleValue());
      branchBuilder = branchBuilder.add("mw_j", connection.getAttribute(ElectricPowerFlowConnection.MW_FLOW_SIDE2_KEY, Number.class).doubleValue());
      branchBuilder = branchBuilder.add("mvar_i", connection.getAttribute(ElectricPowerFlowConnection.MVAR_FLOW_SIDE1_KEY, Number.class).doubleValue());
      branchBuilder = branchBuilder.add("mvar_j", connection.getAttribute(ElectricPowerFlowConnection.MVAR_FLOW_SIDE2_KEY, Number.class).doubleValue());
      branchBuilder = branchBuilder.add("base_kv", baseKV);      
      branchBuilder = branchBuilder.add("status", connection.getActualStatus() && connection.getDesiredStatus());
      branchBuilder = branchBuilder.add("rating", connection.getCapacityRating().equals(Double.POSITIVE_INFINITY) ? "infinity" : connection.getCapacityRating());
      branchBuilder = branchBuilder.add("bus_i", bus1.toString());
      branchBuilder = branchBuilder.add("bus_j", bus2.toString());      
      branchesBuilder = branchesBuilder.add(branchBuilder);
    }
    mainBuilder = mainBuilder.add("branch", branchesBuilder);

    // get the generator results
    JSONArrayBuilder generatorsBuilder = json.createArrayBuilder();
    for (Generator generator : model.getGenerators()) {
      Bus bus = model.getNode(generator).getBus();      
      JSONObjectBuilder generatorBuilder = json.createObjectBuilder();
      generatorBuilder = generatorBuilder.add("gen_i", generator.toString());
      generatorBuilder = generatorBuilder.add("mw", generator.getActualRealGeneration().doubleValue());
      generatorBuilder = generatorBuilder.add("mw_min", generator.getRealGenerationMin());
      generatorBuilder = generatorBuilder.add("mw_max", generator.getDesiredRealGenerationMax());
      generatorBuilder = generatorBuilder.add("mvar", generator.getActualReactiveGeneration().doubleValue());
      generatorBuilder = generatorBuilder.add("mvar_min", generator.getReactiveMin());
      generatorBuilder = generatorBuilder.add("mvar_max", generator.getDesiredReactiveMax());
      generatorBuilder = generatorBuilder.add("status", generator.getActualStatus() && generator.getDesiredStatus());
      generatorBuilder = generatorBuilder.add("bus", bus.toString());            
      generatorsBuilder = generatorsBuilder.add(generatorBuilder);
    }
    mainBuilder = mainBuilder.add("gen", generatorsBuilder);

    // get the load results
    JSONArrayBuilder loadsBuilder = json.createArrayBuilder();
    for (Load load : model.getLoads()) {
      Bus bus = model.getNode(load).getBus();      

      JSONObjectBuilder loadBuilder = json.createObjectBuilder();
      loadBuilder = loadBuilder.add("load_i", load.toString());
      loadBuilder = loadBuilder.add("mw", load.getActualRealLoad().doubleValue());
      loadBuilder = loadBuilder.add("mw_min", 0.0);
      loadBuilder = loadBuilder.add("mw_max", load.getDesiredRealLoad().doubleValue());
      loadBuilder = loadBuilder.add("mvar", load.getActualReactiveLoad().doubleValue());
      loadBuilder = loadBuilder.add("mvar_min", 0.0);
      loadBuilder = loadBuilder.add("mvar_max", load.getDesiredReactiveLoad().doubleValue());      
      loadBuilder = loadBuilder.add("status", load.getActualStatus() && load.getDesiredStatus());
      loadBuilder = loadBuilder.add("bus", bus.toString());            
      loadsBuilder = loadsBuilder.add(loadBuilder);
    }
    mainBuilder = mainBuilder.add("load", loadsBuilder);

    
    // get the transformer results
    JSONArrayBuilder transformersBuilder = json.createArrayBuilder();
    for (Transformer connection : model.getTransformers()) {
      JSONObjectBuilder transformerBuilder = json.createObjectBuilder();
      Bus bus1 = model.getFirstNode(connection).getBus();
      Bus bus2 = model.getSecondNode(connection).getBus();
      transformerBuilder = transformerBuilder.add("branch_i", connection.toString());
      transformerBuilder = transformerBuilder.add("mw_i", connection.getAttribute(ElectricPowerFlowConnection.MW_FLOW_SIDE1_KEY, Number.class).doubleValue());
      transformerBuilder = transformerBuilder.add("mw_j", connection.getAttribute(ElectricPowerFlowConnection.MW_FLOW_SIDE2_KEY, Number.class).doubleValue());
      transformerBuilder = transformerBuilder.add("mvar_i", connection.getAttribute(ElectricPowerFlowConnection.MVAR_FLOW_SIDE1_KEY, Number.class).doubleValue());
      transformerBuilder = transformerBuilder.add("mvar_j", connection.getAttribute(ElectricPowerFlowConnection.MVAR_FLOW_SIDE2_KEY, Number.class).doubleValue());
      transformerBuilder = transformerBuilder.add("base_kv_i", bus1.getSystemVoltageKV());      
      transformerBuilder = transformerBuilder.add("base_kv_j", bus2.getSystemVoltageKV());      
      transformerBuilder = transformerBuilder.add("ratio", connection.getAttribute(Transformer.TAP_RATIO_KEY,Number.class).doubleValue());
      transformerBuilder = transformerBuilder.add("shift", connection.getAttribute(Transformer.TAP_ANGLE_KEY,Number.class).doubleValue());
      transformerBuilder = transformerBuilder.add("status", connection.getActualStatus() && connection.getDesiredStatus());
      transformerBuilder = transformerBuilder.add("rating", connection.getCapacityRating().equals(Double.POSITIVE_INFINITY) ? "infinity" : connection.getCapacityRating());
      transformerBuilder = transformerBuilder.add("bus_i", bus1.toString());
      transformerBuilder = transformerBuilder.add("bus_j", bus2.toString());      
      transformersBuilder = transformersBuilder.add(transformerBuilder);
    }
    mainBuilder = mainBuilder.add("transformer", transformersBuilder);    
    
    // get the fixed shunt results
    JSONArrayBuilder shuntsBuilder = json.createArrayBuilder();
    for (ShuntCapacitor shunt : model.getShuntCapacitors()) {
      Bus bus = model.getNode(shunt).getBus();      
      JSONObjectBuilder shuntBuilder = json.createObjectBuilder();
      shuntBuilder = shuntBuilder.add("shunt_i", shunt.toString());
      shuntBuilder = shuntBuilder.add("bs", shunt.getRealCompensation());
      shuntBuilder = shuntBuilder.add("gs", shunt.getReactiveCompensation());
      shuntBuilder = shuntBuilder.add("status", shunt.getActualStatus() && shunt.getDesiredStatus());
      shuntBuilder = shuntBuilder.add("bus", bus.toString());            
      shuntsBuilder = shuntsBuilder.add(shuntBuilder);
    }
    mainBuilder = mainBuilder.add("fixed_shunt", shuntsBuilder);
        
    
    // get the switched shunt results
    JSONArrayBuilder switchedShuntsBuilder = json.createArrayBuilder();
    for (ShuntCapacitorSwitch shunt : model.getShuntCapacitorSwitches()) {
      JSONObjectBuilder shuntBuilder = json.createObjectBuilder();
      Bus bus = model.getNode(shunt).getBus();      
      shuntBuilder = shuntBuilder.add("shunt_i", shunt.toString());
      shuntBuilder = shuntBuilder.add("bs", shunt.getRealCompensation().doubleValue());
      shuntBuilder = shuntBuilder.add("gs", shunt.getReactiveCompensation().doubleValue());
      shuntBuilder = shuntBuilder.add("status", shunt.getActualStatus() && shunt.getDesiredStatus());
      shuntBuilder = shuntBuilder.add("bus", bus.toString());            
      switchedShuntsBuilder = switchedShuntsBuilder.add(shuntBuilder);
    }
    mainBuilder = mainBuilder.add("switched_shunt", switchedShuntsBuilder);
    
    // write to a generic output stream
    JSONWriter writer = json.createWriter(ps);
    writer.write(mainBuilder.build());

    
  }

}
