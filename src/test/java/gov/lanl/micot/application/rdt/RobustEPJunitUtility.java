package gov.lanl.micot.application.rdt;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.Generator;
import gov.lanl.micot.infrastructure.ep.model.Load;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.model.ScenarioAttribute;
import gov.lanl.micot.infrastructure.project.ProjectConfiguration;
import gov.lanl.micot.infrastructure.project.ScenarioConfiguration;
import gov.lanl.micot.application.rdt.algorithm.AlgorithmConstants;

import java.util.ArrayList;
import java.util.Collection;

public class RobustEPJunitUtility {
  
  /**
   * Output the solution
   * 
   * @param model
   */
  protected static void outputSolution(ProjectConfiguration configuration, ElectricPowerModel model) {
    Collection<Scenario> scenarios = new ArrayList<Scenario>();
    for (ScenarioConfiguration c : configuration.getScenarioConfigurations()) {
      scenarios.add(c.getScenario());
    }
    
    // output the switch operation
    System.out.println("Switch Operations");
    for (ElectricPowerFlowConnection edge : model.getFlowConnections()) {
      if (edge.getAttribute(AlgorithmConstants.IS_SWITCH_OPEN_KEY) != null) {
        System.out.print(edge + ",");
      }
    }
    System.out.println();
    for (Scenario scenario : scenarios) {
      for (ElectricPowerFlowConnection edge : model.getFlowConnections()) {
        if (edge.getAttribute(AlgorithmConstants.IS_SWITCH_OPEN_KEY) != null) {
          ScenarioAttribute attribute = edge.getAttribute(AlgorithmConstants.IS_SWITCH_OPEN_KEY, ScenarioAttribute.class);
          System.out.print(attribute.getValue(scenario).intValue() == 1 ? true : false + ",");
        }
      }
      System.out.println();
    }

    System.out.println();
    System.out.println("Switch Constrution");
    // output the switch construction
    for (ElectricPowerFlowConnection edge : model.getFlowConnections()) {
      if (edge.getAttribute(AlgorithmConstants.IS_SWITCH_CONSTRUCTED_KEY) != null) {
        System.out.print(edge + ",");
      }
    }
    System.out.println();
    for (ElectricPowerFlowConnection edge : model.getFlowConnections()) {
      if (edge.getAttribute(AlgorithmConstants.IS_SWITCH_CONSTRUCTED_KEY) != null) {
        System.out.print(edge.getAttribute(AlgorithmConstants.IS_SWITCH_CONSTRUCTED_KEY) + ",");
      }
    }
    System.out.println();
    System.out.println();

    // output line construction
    System.out.println("Line Construction");
    for (ElectricPowerFlowConnection edge : model.getFlowConnections()) {
      if (edge.getAttribute(AlgorithmConstants.IS_CONSTRUCTED_KEY) != null) {
        System.out.print(edge + ",");
      }
    }
    System.out.println();
    for (ElectricPowerFlowConnection edge : model.getFlowConnections()) {
      if (edge.getAttribute(AlgorithmConstants.IS_CONSTRUCTED_KEY) != null) {
        System.out.print(edge.getAttribute(AlgorithmConstants.IS_CONSTRUCTED_KEY) + ",");
      }
    }
    System.out.println();
    System.out.println();

    // output the hardening attribute
    System.out.println("Hardening assets");
    for (ElectricPowerFlowConnection edge : model.getFlowConnections()) {
      if (edge.getAttribute(AlgorithmConstants.IS_HARDENED_KEY) != null) {
        System.out.print(edge + "(" + model.getFirstNode(edge) + " " + model.getSecondNode(edge) + "),");
      }
    }
    System.out.println();
    for (ElectricPowerFlowConnection edge : model.getFlowConnections()) {
      if (edge.getAttribute(AlgorithmConstants.IS_HARDENED_KEY) != null) {
        System.out.print(edge.getAttribute(AlgorithmConstants.IS_HARDENED_KEY) + ",");
      }
    }
    System.out.println();
    System.out.println();

    // output microgrid capacity
    System.out.println("Microgrid Capacity");
    for (Generator generator : model.getGenerators()) {
      if (generator.getAttribute(AlgorithmConstants.MICROGRID_CAPACITY_PHASE_A_KEY) != null || generator.getAttribute(AlgorithmConstants.MICROGRID_CAPACITY_PHASE_B_KEY) != null || generator.getAttribute(AlgorithmConstants.MICROGRID_CAPACITY_PHASE_C_KEY) != null) {
        System.out.print(generator + ",");
        System.out.print(generator + ",");
        System.out.print(generator + ",");
      }
    }
    System.out.println();
    // System.out.println();
    // System.out.println();

    for (Generator generator : model.getGenerators()) {
      if (generator.getAttribute(AlgorithmConstants.MICROGRID_CAPACITY_PHASE_A_KEY) != null || generator.getAttribute(AlgorithmConstants.MICROGRID_CAPACITY_PHASE_B_KEY) != null || generator.getAttribute(AlgorithmConstants.MICROGRID_CAPACITY_PHASE_C_KEY) != null) {
        double capacity = generator.getAttribute(AlgorithmConstants.MICROGRID_CAPACITY_PHASE_A_KEY) == null ? 0 : generator.getAttribute(AlgorithmConstants.MICROGRID_CAPACITY_PHASE_A_KEY, Number.class).doubleValue();
        System.out.print(capacity + ",");
      }
    }
    System.out.println();
    for (Generator generator : model.getGenerators()) {
      if (generator.getAttribute(AlgorithmConstants.MICROGRID_CAPACITY_PHASE_A_KEY) != null || generator.getAttribute(AlgorithmConstants.MICROGRID_CAPACITY_PHASE_B_KEY) != null || generator.getAttribute(AlgorithmConstants.MICROGRID_CAPACITY_PHASE_C_KEY) != null) {
        double capacity = generator.getAttribute(AlgorithmConstants.MICROGRID_CAPACITY_PHASE_B_KEY) == null ? 0 : generator.getAttribute(AlgorithmConstants.MICROGRID_CAPACITY_PHASE_B_KEY, Number.class).doubleValue();
        System.out.print(capacity + ",");
      }
    }
    System.out.println();
    for (Generator generator : model.getGenerators()) {
      if (generator.getAttribute(AlgorithmConstants.MICROGRID_CAPACITY_PHASE_A_KEY) != null || generator.getAttribute(AlgorithmConstants.MICROGRID_CAPACITY_PHASE_B_KEY) != null || generator.getAttribute(AlgorithmConstants.MICROGRID_CAPACITY_PHASE_C_KEY) != null) {
        double capacity = generator.getAttribute(AlgorithmConstants.MICROGRID_CAPACITY_PHASE_C_KEY) == null ? 0 : generator.getAttribute(AlgorithmConstants.MICROGRID_CAPACITY_PHASE_C_KEY, Number.class).doubleValue();
        System.out.print(capacity + ",");
      }
    }
    System.out.println();
    System.out.println();

    // Output the actual microgrid generation
    System.out.println("Actual Microgrid Generation");
    for (Generator generator : model.getGenerators()) {
      if (generator.getAttribute(Generator.REAL_GENERATION_A_KEY) != null || generator.getAttribute(Generator.REAL_GENERATION_B_KEY) != null || generator.getAttribute(Generator.REAL_GENERATION_C_KEY) != null) {
        System.out.print(generator + ",");
        System.out.print(generator + ",");
        System.out.print(generator + ",");
      }
    }
    System.out.println();
    // System.out.println();
    // System.out.println();

    for (Scenario scenario : scenarios) {
      for (Generator generator : model.getGenerators()) {
        if (generator.getAttribute(Generator.REAL_GENERATION_A_KEY) != null || generator.getAttribute(Generator.REAL_GENERATION_B_KEY) != null || generator.getAttribute(Generator.REAL_GENERATION_C_KEY) != null) {
          ScenarioAttribute attribute = generator.getAttribute(Generator.REAL_GENERATION_A_KEY, ScenarioAttribute.class);
          double capacity = attribute == null ? 0 : attribute.getValue(scenario) == null ? 0 : attribute.getValue(scenario).doubleValue();
          System.out.print(capacity + ",");
        }
      }
      System.out.println();
      for (Generator generator : model.getGenerators()) {
        if (generator.getAttribute(Generator.REAL_GENERATION_A_KEY) != null || generator.getAttribute(Generator.REAL_GENERATION_B_KEY) != null || generator.getAttribute(Generator.REAL_GENERATION_C_KEY) != null) {
          ScenarioAttribute attribute = generator.getAttribute(Generator.REAL_GENERATION_B_KEY, ScenarioAttribute.class);
          double capacity = attribute == null ? 0 : attribute.getValue(scenario) == null ? 0 : attribute.getValue(scenario).doubleValue();
          System.out.print(capacity + ",");
        }
      }
      System.out.println();
      for (Generator generator : model.getGenerators()) {
        if (generator.getAttribute(Generator.REAL_GENERATION_A_KEY) != null || generator.getAttribute(Generator.REAL_GENERATION_B_KEY) != null || generator.getAttribute(Generator.REAL_GENERATION_C_KEY) != null) {
          ScenarioAttribute attribute = generator.getAttribute(Generator.REAL_GENERATION_C_KEY, ScenarioAttribute.class);
          double capacity = attribute == null ? 0 : attribute.getValue(scenario) == null ? 0 : attribute.getValue(scenario).doubleValue();
          System.out.print(capacity + ",");
        }
      }
      System.out.println();
    }
    System.out.println();

    // output the load served
    System.out.println("Load Served");
    for (Load load : model.getLoads()) {
      if (load.getAttribute(Load.ACTUAL_REAL_LOAD_A_KEY) != null || load.getAttribute(Load.ACTUAL_REAL_LOAD_B_KEY) != null || load.getAttribute(Load.ACTUAL_REAL_LOAD_C_KEY) != null) {
        System.out.print(load + ",");
        System.out.print(load + ",");
        System.out.print(load + ",");
      }
    }
    System.out.println();
    // System.out.println();
    // System.out.println();

    for (Scenario scenario : scenarios) {
      for (Load load : model.getLoads()) {
        if (load.getAttribute(Load.ACTUAL_REAL_LOAD_A_KEY) != null || load.getAttribute(Load.ACTUAL_REAL_LOAD_B_KEY) != null || load.getAttribute(Load.ACTUAL_REAL_LOAD_C_KEY) != null) {
          ScenarioAttribute attribute = load.getAttribute(Load.ACTUAL_REAL_LOAD_A_KEY, ScenarioAttribute.class);
          double demand = attribute == null ? 0 : attribute.getValue(scenario) == null ? 0 : attribute.getValue(scenario).doubleValue();
          System.out.print(demand + ",");
        }
      }
      System.out.println();
      for (Load load : model.getLoads()) {
        if (load.getAttribute(Load.ACTUAL_REAL_LOAD_A_KEY) != null || load.getAttribute(Load.ACTUAL_REAL_LOAD_B_KEY) != null || load.getAttribute(Load.ACTUAL_REAL_LOAD_C_KEY) != null) {
          ScenarioAttribute attribute = load.getAttribute(Load.ACTUAL_REAL_LOAD_B_KEY, ScenarioAttribute.class);
          double demand = attribute == null ? 0 : attribute.getValue(scenario) == null ? 0 : attribute.getValue(scenario).doubleValue();
          System.out.print(demand + ",");
        }
      }
      System.out.println();
      for (Load load : model.getLoads()) {
        if (load.getAttribute(Load.ACTUAL_REAL_LOAD_A_KEY) != null || load.getAttribute(Load.ACTUAL_REAL_LOAD_B_KEY) != null || load.getAttribute(Load.ACTUAL_REAL_LOAD_C_KEY) != null) {
          ScenarioAttribute attribute = load.getAttribute(Load.ACTUAL_REAL_LOAD_C_KEY, ScenarioAttribute.class);
          double demand = attribute == null ? 0 : attribute.getValue(scenario) == null ? 0 : attribute.getValue(scenario).doubleValue();
          System.out.print(demand + ",");
        }
      }
      System.out.println();
    }
    System.out.println();

    // output the line flows
    System.out.println("Line Flows");
    for (ElectricPowerFlowConnection edge : model.getFlowConnections()) {
      if (edge.getAttribute(ElectricPowerFlowConnection.MW_FLOW_PHASE_A_KEY) != null || edge.getAttribute(ElectricPowerFlowConnection.MW_FLOW_PHASE_B_KEY) != null || edge.getAttribute(ElectricPowerFlowConnection.MW_FLOW_PHASE_C_KEY) != null) {
        System.out.print(edge + ", ");
        System.out.print(edge + ", ");
        System.out.print(edge + ", ");
      }
    }
    System.out.println();
    // System.out.println();
    // System.out.println();

    for (Scenario scenario : scenarios) {
      for (ElectricPowerFlowConnection edge : model.getFlowConnections()) {
        if (edge.getAttribute(ElectricPowerFlowConnection.MW_FLOW_PHASE_A_KEY) != null || edge.getAttribute(ElectricPowerFlowConnection.MW_FLOW_PHASE_B_KEY) != null || edge.getAttribute(ElectricPowerFlowConnection.MW_FLOW_PHASE_C_KEY) != null) {
          ScenarioAttribute attribute = edge.getAttribute(ElectricPowerFlowConnection.MW_FLOW_PHASE_A_KEY, ScenarioAttribute.class);
          double demand = attribute == null ? 0 : attribute.getValue(scenario) == null ? 0 : attribute.getValue(scenario).doubleValue();
          System.out.print(demand + ",");
        }
      }
      System.out.println();
      for (ElectricPowerFlowConnection edge : model.getFlowConnections()) {
        if (edge.getAttribute(ElectricPowerFlowConnection.MW_FLOW_PHASE_A_KEY) != null || edge.getAttribute(ElectricPowerFlowConnection.MW_FLOW_PHASE_B_KEY) != null || edge.getAttribute(ElectricPowerFlowConnection.MW_FLOW_PHASE_C_KEY) != null) {
          ScenarioAttribute attribute = edge.getAttribute(ElectricPowerFlowConnection.MW_FLOW_PHASE_B_KEY, ScenarioAttribute.class);
          double demand = attribute == null ? 0 : attribute.getValue(scenario) == null ? 0 : attribute.getValue(scenario).doubleValue();
          System.out.print(demand + ",");
        }
      }
      System.out.println();
      for (ElectricPowerFlowConnection edge : model.getFlowConnections()) {
        if (edge.getAttribute(ElectricPowerFlowConnection.MW_FLOW_PHASE_A_KEY) != null || edge.getAttribute(ElectricPowerFlowConnection.MW_FLOW_PHASE_B_KEY) != null || edge.getAttribute(ElectricPowerFlowConnection.MW_FLOW_PHASE_C_KEY) != null) {
          ScenarioAttribute attribute = edge.getAttribute(ElectricPowerFlowConnection.MW_FLOW_PHASE_C_KEY, ScenarioAttribute.class);
          double demand = attribute == null ? 0 : attribute.getValue(scenario) == null ? 0 : attribute.getValue(scenario).doubleValue();
          System.out.print(demand + ",");
        }
      }
      System.out.println();
    }
    System.out.println();

    // output scenario damage
    System.out.println("Scenario Damage");
    for (ElectricPowerFlowConnection edge : model.getFlowConnections()) {
      System.out.print(edge + "(" + model.getFirstNode(edge) + " " + model.getSecondNode(edge) + "),");
    }
    System.out.println();
    for (Scenario scenario : scenarios) {
      for (ElectricPowerFlowConnection edge : model.getFlowConnections()) {
        boolean damage = !scenario.computeActualStatus(edge, true);
        System.out.print(damage + ",");
      }
      System.out.println();
    }
  }


}
