package gov.lanl.micot.infrastructure.ep.exec;

import java.io.OutputStream;

import gov.lanl.micot.infrastructure.ep.model.Bus;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.Generator;
import gov.lanl.micot.infrastructure.ep.model.Load;
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
      busBuilder = busBuilder.add("bus_i", bus.toString());
      busBuilder = busBuilder.add("vm", bus.getVoltagePU());
      busBuilder = busBuilder.add("va", bus.getPhaseAngle());      
      busBuilder = busBuilder.add("status", bus.getActualStatus() && bus.getDesiredStatus());
      busesBuilder = busesBuilder.add(busBuilder);
    }
    mainBuilder = mainBuilder.add("bus", busesBuilder);

    // get the branch results
    JSONArrayBuilder branchesBuilder = json.createArrayBuilder();
    for (ElectricPowerFlowConnection connection : model.getFlowConnections()) {
      JSONObjectBuilder branchBuilder = json.createObjectBuilder();
      branchBuilder = branchBuilder.add("branch_i", connection.toString());
      branchBuilder = branchBuilder.add("mw_i", connection.getAttribute(ElectricPowerFlowConnection.MW_FLOW_SIDE1_KEY, Number.class).doubleValue());
      branchBuilder = branchBuilder.add("mw_j", connection.getAttribute(ElectricPowerFlowConnection.MW_FLOW_SIDE2_KEY, Number.class).doubleValue());
      branchBuilder = branchBuilder.add("mvar_i", connection.getAttribute(ElectricPowerFlowConnection.MVAR_FLOW_SIDE1_KEY, Number.class).doubleValue());
      branchBuilder = branchBuilder.add("mvar_j", connection.getAttribute(ElectricPowerFlowConnection.MVAR_FLOW_SIDE2_KEY, Number.class).doubleValue());
      branchBuilder = branchBuilder.add("status", connection.getActualStatus() && connection.getDesiredStatus());
      branchesBuilder = branchesBuilder.add(branchBuilder);
    }
    mainBuilder = mainBuilder.add("branch", branchesBuilder);

    // get the generator results
    JSONArrayBuilder generatorsBuilder = json.createArrayBuilder();
    for (Generator generator : model.getGenerators()) {
      JSONObjectBuilder generatorBuilder = json.createObjectBuilder();
      generatorBuilder = generatorBuilder.add("gen_i", generator.toString());
      generatorBuilder = generatorBuilder.add("mw", generator.getActualRealGeneration().doubleValue());
      generatorBuilder = generatorBuilder.add("mvar", generator.getActualReactiveGeneration().doubleValue());
      generatorBuilder = generatorBuilder.add("status", generator.getActualStatus() && generator.getDesiredStatus());
      generatorsBuilder = generatorsBuilder.add(generatorBuilder);
    }
    mainBuilder = mainBuilder.add("gen", generatorsBuilder);

    // get the load results
    JSONArrayBuilder loadsBuilder = json.createArrayBuilder();
    for (Load load : model.getLoads()) {
      JSONObjectBuilder loadBuilder = json.createObjectBuilder();
      loadBuilder = loadBuilder.add("load_i", load.toString());
      loadBuilder = loadBuilder.add("mw", load.getActualRealLoad().doubleValue());
      loadBuilder = loadBuilder.add("mvar", load.getActualReactiveLoad().doubleValue());
      loadBuilder = loadBuilder.add("status", load.getActualStatus() && load.getDesiredStatus());
      loadsBuilder = loadsBuilder.add(loadBuilder);
    }
    mainBuilder = mainBuilder.add("load", loadsBuilder);

    // write to a generic output stream
    JSONWriter writer = json.createWriter(ps);
    writer.write(mainBuilder.build());
    
  }

}
