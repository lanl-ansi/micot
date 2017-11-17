package gov.lanl.micot.infrastructure.ep.simulate.opendss;

import java.io.OutputStream;

import gov.lanl.micot.infrastructure.ep.model.Bus;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.Generator;
import gov.lanl.micot.infrastructure.ep.model.Load;
import gov.lanl.micot.infrastructure.ep.model.ShuntCapacitor;
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
      
      JSONArrayBuilder voltageBuilder = json.createArrayBuilder();
      voltageBuilder.add(bus.getMinimumVoltagePU() * baseKV);
      voltageBuilder.add(bus.getVoltagePU().doubleValue() * baseKV);
      voltageBuilder.add(bus.getMaximumVoltagePU() * baseKV);
      busBuilder = busBuilder.add("vm", voltageBuilder);            
      busBuilder = busBuilder.add("va", bus.getPhaseAngle());
      busBuilder = busBuilder.add("base_kv", baseKV);
      busBuilder = busBuilder.add("owner", bus.getOwnerName());
      busBuilder = busBuilder.add("name", bus.getAttribute(Bus.NAME_KEY, String.class));
      busBuilder = busBuilder.add("status", bus.getStatus());
      busesBuilder = busesBuilder.add(busBuilder);
    }
    mainBuilder = mainBuilder.add("bus", busesBuilder);

    // get the branch results
    JSONArrayBuilder branchesBuilder = json.createArrayBuilder();
    for (ElectricPowerFlowConnection connection : model.getFlowConnections()) {
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
      branchBuilder = branchBuilder.add("status", connection.getStatus());
      branchesBuilder = branchesBuilder.add(branchBuilder);
    }
    mainBuilder = mainBuilder.add("branch", branchesBuilder);

    // get the generator results
    JSONArrayBuilder generatorsBuilder = json.createArrayBuilder();
    for (Generator generator : model.getGenerators()) {
      JSONObjectBuilder generatorBuilder = json.createObjectBuilder();
      generatorBuilder = generatorBuilder.add("gen_i", generator.toString());
      JSONArrayBuilder mwBuilder = json.createArrayBuilder();
      mwBuilder.add(generator.getRealGenerationMin());
      mwBuilder.add(generator.getRealGeneration().doubleValue());
      mwBuilder.add(generator.getRealGenerationMax());
      generatorBuilder = generatorBuilder.add("mw", mwBuilder);
      JSONArrayBuilder mvarBuilder = json.createArrayBuilder();
      mvarBuilder.add(generator.getReactiveGenerationMin());
      mvarBuilder.add(generator.getReactiveGeneration().doubleValue());
      mvarBuilder.add(generator.getReactiveGenerationMax());
      generatorBuilder = generatorBuilder.add("mvar", mvarBuilder);
      generatorBuilder = generatorBuilder.add("status", generator.getStatus());
      generatorsBuilder = generatorsBuilder.add(generatorBuilder);
    }
    mainBuilder = mainBuilder.add("gen", generatorsBuilder);

    // get the load results
    JSONArrayBuilder loadsBuilder = json.createArrayBuilder();
    for (Load load : model.getLoads()) {
      JSONObjectBuilder loadBuilder = json.createObjectBuilder();
      loadBuilder = loadBuilder.add("load_i", load.toString());
      JSONArrayBuilder mwBuilder = json.createArrayBuilder();
      mwBuilder.add(0.0);
      mwBuilder.add(load.getRealLoad().doubleValue());
      mwBuilder.add(load.getRealLoadMax());
      loadBuilder = loadBuilder.add("mw", mwBuilder);
      JSONArrayBuilder mvarBuilder = json.createArrayBuilder();
      mvarBuilder.add(0.0);
      mvarBuilder.add(load.getReactiveLoad().doubleValue());
      mvarBuilder.add(load.getReactiveLoadMax());
      loadBuilder = loadBuilder.add("mvar", mvarBuilder);
      loadBuilder = loadBuilder.add("status", load.getStatus());
      loadsBuilder = loadsBuilder.add(loadBuilder);
    }
    mainBuilder = mainBuilder.add("load", loadsBuilder);

    // get the shunt results
    JSONArrayBuilder shuntsBuilder = json.createArrayBuilder();
    for (ShuntCapacitor shunt : model.getShuntCapacitors()) {
      JSONObjectBuilder shuntBuilder = json.createObjectBuilder();
      shuntBuilder = shuntBuilder.add("shunt_i", shunt.toString());
      shuntBuilder = shuntBuilder.add("bs", shunt.getRealCompensation() * model.getMVABase());
      shuntBuilder = shuntBuilder.add("gs", shunt.getReactiveCompensation() * model.getMVABase());
      shuntBuilder = shuntBuilder.add("status", shunt.getStatus());
      shuntsBuilder = shuntsBuilder.add(shuntBuilder);
    }
    mainBuilder = mainBuilder.add("shunt", shuntsBuilder);
    
    // write to a generic output stream
    JSONWriter writer = json.createWriter(ps);
    writer.write(mainBuilder.build());
    
  }

}
