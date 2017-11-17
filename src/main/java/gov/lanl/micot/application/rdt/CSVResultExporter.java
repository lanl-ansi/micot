package gov.lanl.micot.application.rdt;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Collection;
import java.util.TreeSet;

import gov.lanl.micot.infrastructure.application.ApplicationOutput;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.Generator;
import gov.lanl.micot.infrastructure.ep.model.Load;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.model.ScenarioAttribute;
import gov.lanl.micot.infrastructure.project.ProjectConfiguration;
import gov.lanl.micot.infrastructure.project.ScenarioConfiguration;
import gov.lanl.micot.application.rdt.algorithm.AlgorithmConstants;

/**
 * This is a simple utility class for exporting results in csv format
 * @author Russell Bent
 *
 */
public class CSVResultExporter {
	
	public static String objectiveCSV = "objective.csv";
    public static String  constructionCSV ="construction.csv";
    public static String  hardenCSV ="harden.csv";
    public static String  switchCSV ="switch.csv";
    public static String  switchConstructionCSV ="switchconstruction.csv";
    public static String  microGridPhaseACSV ="microgridPhaseA.csv";
    public static String  microGridPhaseBCSV ="microgridPhaseB.csv";
    public static String  microGridPhaseCCSV ="microgridPhaseC.csv";
    public static String  generationPhaseACSV ="generationPhaseA.csv";
    public static String  generationPhaseBCSV ="generationPhaseB.csv";
    public static String  generationPhaseCCSV ="generationPhaseC.csv";
    public static String  demandPhaseACSV ="demandPhaseA.csv";
    public static String  demandPhaseBCSV ="demandPhaseB.csv";
    public static String  demandPhaseCCSV ="demandPhaseC.csv";
    public static String  flowPhaseACSV ="flowPhaseA.csv";
    public static String  flowPhaseBCSV ="flowPhaseB.csv";
    public static String  flowPhaseCCSV ="flowPhaseC.csv";
    public static String  damageCSV ="damage.csv";
    
  
  /**
   * The export routine for the results
   * @param model
   * @throws FileNotFoundException 
   */
  public void exportResults(ApplicationOutput output, ProjectConfiguration configuration, String path) throws FileNotFoundException {
    ElectricPowerModel model = output.get(RDTApplication.MODEL_FLAG, ElectricPowerModel.class);
    double objective = output.getDouble(RDTApplication.OBJECTIVE_FLAG);
    boolean isFeasible = output.getBoolean(RDTApplication.IS_FEASIBLE_FLAG);
    
    //String path = System.getProperty("user.dir");
        
    // create all csv file
    PrintStream objectivePS = new PrintStream(path + File.separatorChar + objectiveCSV);
    PrintStream constructionPS = new PrintStream(path + File.separatorChar + constructionCSV);
    PrintStream hardenPS = new PrintStream(path + File.separatorChar + hardenCSV);
    PrintStream switchPS = new PrintStream(path + File.separatorChar + switchCSV);
    PrintStream switchConstructionPS = new PrintStream(path + File.separatorChar + switchConstructionCSV);
    PrintStream microAPS = new PrintStream(path + File.separatorChar + microGridPhaseACSV);
    PrintStream microBPS = new PrintStream(path + File.separatorChar + microGridPhaseBCSV);
    PrintStream microCPS = new PrintStream(path + File.separatorChar + microGridPhaseCCSV);
    PrintStream generationAPS = new PrintStream(path + File.separatorChar + generationPhaseACSV);
    PrintStream generationBPS = new PrintStream(path + File.separatorChar + generationPhaseBCSV);
    PrintStream generationCPS = new PrintStream(path + File.separatorChar + generationPhaseCCSV);
    PrintStream demandAPS = new PrintStream(path + File.separatorChar + demandPhaseACSV);
    PrintStream demandBPS = new PrintStream(path + File.separatorChar + demandPhaseBCSV);
    PrintStream demandCPS = new PrintStream(path + File.separatorChar + demandPhaseCCSV);
    PrintStream flowAPS = new PrintStream(path + File.separatorChar + flowPhaseACSV);
    PrintStream flowBPS = new PrintStream(path + File.separatorChar + flowPhaseBCSV);
    PrintStream flowCPS = new PrintStream(path + File.separatorChar + flowPhaseCCSV);
    PrintStream damagePS = new PrintStream(path + File.separatorChar + damageCSV);

    // output the objective function
    objectivePS.println("," + "Budget Spent, Is Feasible");
    objectivePS.println("," + -objective + "," + isFeasible);

    TreeSet<ElectricPowerFlowConnection> edges = new TreeSet<ElectricPowerFlowConnection>();
    edges.addAll((Collection<? extends ElectricPowerFlowConnection>) model.getFlowConnections());
    TreeSet<Generator> generators = new TreeSet<Generator>();
    generators.addAll(model.getComponents(Generator.class));
    TreeSet<Load> loads = new TreeSet<Load>();
    loads.addAll(model.getComponents(Load.class));

    // output the switch operation
    for (ElectricPowerFlowConnection edge : edges) {
      if (edge.getAttribute(AlgorithmConstants.IS_SWITCH_OPEN_KEY) != null) {
        switchPS.print(edge + ",");
      }
    }
    switchPS.println();
    for (ScenarioConfiguration scenarioConfiguration : configuration.getScenarioConfigurations()) {
      Scenario scenario = scenarioConfiguration.getScenario();
      for (ElectricPowerFlowConnection edge : edges) {
        if (edge.getAttribute(AlgorithmConstants.IS_SWITCH_OPEN_KEY) != null) {
          ScenarioAttribute attribute = edge.getAttribute(AlgorithmConstants.IS_SWITCH_OPEN_KEY, ScenarioAttribute.class);
          switchPS.print(attribute.getValue(scenario).intValue() == 1 ? true : false + ",");
        }
      }
      switchPS.println();
    }

    // output the switch construction
    for (ElectricPowerFlowConnection edge : edges) {
      if (edge.getAttribute(AlgorithmConstants.IS_SWITCH_CONSTRUCTED_KEY) != null) {
        switchConstructionPS.print(edge + ",");
      }
    }
    switchConstructionPS.println();
    for (ElectricPowerFlowConnection edge : edges) {
      if (edge.getAttribute(AlgorithmConstants.IS_SWITCH_CONSTRUCTED_KEY) != null) {
        switchConstructionPS.print(edge.getAttribute(AlgorithmConstants.IS_SWITCH_CONSTRUCTED_KEY) + ",");
      }
    }
    switchConstructionPS.println();

    // output line construction
    for (ElectricPowerFlowConnection edge : edges) {
      if (edge.getAttribute(AlgorithmConstants.IS_CONSTRUCTED_KEY) != null) {
        constructionPS.print(edge + ",");
      }
    }
    constructionPS.println();
    for (ElectricPowerFlowConnection edge : edges) {
      if (edge.getAttribute(AlgorithmConstants.IS_CONSTRUCTED_KEY) != null) {
        constructionPS.print(edge.getAttribute(AlgorithmConstants.IS_CONSTRUCTED_KEY) + ",");
      }
    }
    constructionPS.println();

    // output the hardening attribute
    for (ElectricPowerFlowConnection edge : edges) {
      if (edge.getAttribute(AlgorithmConstants.IS_HARDENED_KEY) != null) {
        hardenPS.print(edge + "(" + model.getFirstNode(edge) + " " + model.getSecondNode(edge) + "),");
      }
    }
    hardenPS.println();
    for (ElectricPowerFlowConnection edge : edges) {
      if (edge.getAttribute(AlgorithmConstants.IS_HARDENED_KEY) != null) {
        hardenPS.print(edge.getAttribute(AlgorithmConstants.IS_HARDENED_KEY) + ",");
      }
    }
    hardenPS.println();

    // output microgrid capacity
    for (Generator generator : generators) {
      if (generator.getAttribute(AlgorithmConstants.MICROGRID_CAPACITY_PHASE_A_KEY) != null || generator.getAttribute(AlgorithmConstants.MICROGRID_CAPACITY_PHASE_B_KEY) != null || generator.getAttribute(AlgorithmConstants.MICROGRID_CAPACITY_PHASE_C_KEY) != null) {
        microAPS.print(generator + ",");
        microBPS.print(generator + ",");
        microCPS.print(generator + ",");
      }
    }
    microAPS.println();
    microBPS.println();
    microCPS.println();

    for (Generator generator : generators) {
      if (generator.getAttribute(AlgorithmConstants.MICROGRID_CAPACITY_PHASE_A_KEY) != null || generator.getAttribute(AlgorithmConstants.MICROGRID_CAPACITY_PHASE_B_KEY) != null || generator.getAttribute(AlgorithmConstants.MICROGRID_CAPACITY_PHASE_C_KEY) != null) {
        double capacity = generator.getAttribute(AlgorithmConstants.MICROGRID_CAPACITY_PHASE_A_KEY) == null ? 0 : generator.getAttribute(AlgorithmConstants.MICROGRID_CAPACITY_PHASE_A_KEY, Number.class).doubleValue();
        microAPS.print(capacity + ",");
      }
    }
    microAPS.println();
    for (Generator generator : generators) {
      if (generator.getAttribute(AlgorithmConstants.MICROGRID_CAPACITY_PHASE_A_KEY) != null || generator.getAttribute(AlgorithmConstants.MICROGRID_CAPACITY_PHASE_B_KEY) != null || generator.getAttribute(AlgorithmConstants.MICROGRID_CAPACITY_PHASE_C_KEY) != null) {
        double capacity = generator.getAttribute(AlgorithmConstants.MICROGRID_CAPACITY_PHASE_B_KEY) == null ? 0 : generator.getAttribute(AlgorithmConstants.MICROGRID_CAPACITY_PHASE_B_KEY, Number.class).doubleValue();
        microBPS.print(capacity + ",");
      }
    }
    microBPS.println();
    for (Generator generator : generators) {
      if (generator.getAttribute(AlgorithmConstants.MICROGRID_CAPACITY_PHASE_A_KEY) != null || generator.getAttribute(AlgorithmConstants.MICROGRID_CAPACITY_PHASE_B_KEY) != null || generator.getAttribute(AlgorithmConstants.MICROGRID_CAPACITY_PHASE_C_KEY) != null) {
        double capacity = generator.getAttribute(AlgorithmConstants.MICROGRID_CAPACITY_PHASE_C_KEY) == null ? 0 : generator.getAttribute(AlgorithmConstants.MICROGRID_CAPACITY_PHASE_C_KEY, Number.class).doubleValue();
        microCPS.print(capacity + ",");
      }
    }
    microCPS.println();

    // Output the actual microgrid generation
    for (Generator generator : generators) {
      if (generator.getAttribute(Generator.REAL_GENERATION_A_KEY) != null || generator.getAttribute(Generator.REAL_GENERATION_B_KEY) != null || generator.getAttribute(Generator.REAL_GENERATION_C_KEY) != null) {
        generationAPS.print(generator + ",");
        generationBPS.print(generator + ",");
        generationCPS.print(generator + ",");
      }
    }
    generationAPS.println();
    generationBPS.println();
    generationCPS.println();

    for (ScenarioConfiguration scenarioConfiguration : configuration.getScenarioConfigurations()) {
      Scenario scenario = scenarioConfiguration.getScenario();
      for (Generator generator : generators) {
        if (generator.getAttribute(Generator.REAL_GENERATION_A_KEY) != null || generator.getAttribute(Generator.REAL_GENERATION_B_KEY) != null || generator.getAttribute(Generator.REAL_GENERATION_C_KEY) != null) {
          ScenarioAttribute attribute = generator.getAttribute(Generator.REAL_GENERATION_A_KEY, ScenarioAttribute.class);
          double capacity = attribute == null ? 0 : attribute.getValue(scenario) == null ? 0 : attribute.getValue(scenario).doubleValue();
          generationAPS.print(capacity + ",");
        }
      }
      generationAPS.println();
      for (Generator generator : generators) {
        if (generator.getAttribute(Generator.REAL_GENERATION_A_KEY) != null || generator.getAttribute(Generator.REAL_GENERATION_B_KEY) != null || generator.getAttribute(Generator.REAL_GENERATION_C_KEY) != null) {
          ScenarioAttribute attribute = generator.getAttribute(Generator.REAL_GENERATION_B_KEY, ScenarioAttribute.class);
          double capacity = attribute == null ? 0 : attribute.getValue(scenario) == null ? 0 : attribute.getValue(scenario).doubleValue();
          generationBPS.print(capacity + ",");
        }
      }
      generationBPS.println();
      for (Generator generator : generators) {
        if (generator.getAttribute(Generator.REAL_GENERATION_A_KEY) != null || generator.getAttribute(Generator.REAL_GENERATION_B_KEY) != null || generator.getAttribute(Generator.REAL_GENERATION_C_KEY) != null) {
          ScenarioAttribute attribute = generator.getAttribute(Generator.REAL_GENERATION_C_KEY, ScenarioAttribute.class);
          double capacity = attribute == null ? 0 : attribute.getValue(scenario) == null ? 0 : attribute.getValue(scenario).doubleValue();
          generationCPS.print(capacity + ",");
        }
      }
      generationCPS.println();
    }
    
    // output the load served
    for (Load load : loads) {
      if (load.getAttribute(Load.REAL_LOAD_A_KEY) != null || load.getAttribute(Load.REAL_LOAD_B_KEY) != null || load.getAttribute(Load.REAL_LOAD_C_KEY) != null) {
        demandAPS.print(load + ",");
        demandBPS.print(load + ",");
        demandCPS.print(load + ",");
      }
    }
    demandAPS.println();
    demandBPS.println();
    demandCPS.println();

    for (ScenarioConfiguration scenarioConfiguration : configuration.getScenarioConfigurations()) {
      Scenario scenario = scenarioConfiguration.getScenario();
      for (Load load : loads) {
        if (load.getAttribute(Load.REAL_LOAD_A_KEY) != null || load.getAttribute(Load.REAL_LOAD_B_KEY) != null || load.getAttribute(Load.REAL_LOAD_C_KEY) != null) {
          ScenarioAttribute attribute = load.getAttribute(Load.REAL_LOAD_A_KEY, ScenarioAttribute.class);
          double demand = attribute == null ? 0 : attribute.getValue(scenario) == null ? 0 : attribute.getValue(scenario).doubleValue();
          demandAPS.print(demand + ",");
        }
      }
      demandAPS.println();
      for (Load load : loads) {
        if (load.getAttribute(Load.REAL_LOAD_A_KEY) != null || load.getAttribute(Load.REAL_LOAD_B_KEY) != null || load.getAttribute(Load.REAL_LOAD_C_KEY) != null) {
          ScenarioAttribute attribute = load.getAttribute(Load.REAL_LOAD_B_KEY, ScenarioAttribute.class);
          double demand = attribute == null ? 0 : attribute.getValue(scenario) == null ? 0 : attribute.getValue(scenario).doubleValue();
          demandBPS.print(demand + ",");
        }
      }
      demandBPS.println();
      for (Load load : loads) {
        if (load.getAttribute(Load.REAL_LOAD_A_KEY) != null || load.getAttribute(Load.REAL_LOAD_B_KEY) != null || load.getAttribute(Load.REAL_LOAD_C_KEY) != null) {
          ScenarioAttribute attribute = load.getAttribute(Load.REAL_LOAD_C_KEY, ScenarioAttribute.class);
          double demand = attribute == null ? 0 : attribute.getValue(scenario) == null ? 0 : attribute.getValue(scenario).doubleValue();
          demandCPS.print(demand + ",");
        }
      }
      demandCPS.println();
    }
    
    
    // output the line flows
    for (ElectricPowerFlowConnection edge : edges) {
      if (edge.getAttribute(ElectricPowerFlowConnection.MW_FLOW_PHASE_A_KEY) != null || edge.getAttribute(ElectricPowerFlowConnection.MW_FLOW_PHASE_B_KEY) != null || edge.getAttribute(ElectricPowerFlowConnection.MW_FLOW_PHASE_C_KEY) != null) {
        flowAPS.print(edge + ",");
        flowBPS.print(edge + ",");
        flowCPS.print(edge + ",");
      }
    }
    flowAPS.println();
    flowBPS.println();
    flowCPS.println();

    for (ScenarioConfiguration scenarioConfiguration : configuration.getScenarioConfigurations()) {
      Scenario scenario = scenarioConfiguration.getScenario();
      for (ElectricPowerFlowConnection edge : edges) {
        if (edge.getAttribute(ElectricPowerFlowConnection.MW_FLOW_PHASE_A_KEY) != null || edge.getAttribute(ElectricPowerFlowConnection.MW_FLOW_PHASE_B_KEY) != null || edge.getAttribute(ElectricPowerFlowConnection.MW_FLOW_PHASE_C_KEY) != null) {
          ScenarioAttribute attribute = edge.getAttribute(ElectricPowerFlowConnection.MW_FLOW_PHASE_A_KEY, ScenarioAttribute.class);
          double demand = attribute == null ? 0 : attribute.getValue(scenario) == null ? 0 : attribute.getValue(scenario).doubleValue();
          flowAPS.print(demand + ",");
        }
      }
      flowAPS.println();
      for (ElectricPowerFlowConnection edge : edges) {
        if (edge.getAttribute(ElectricPowerFlowConnection.MW_FLOW_PHASE_A_KEY) != null || edge.getAttribute(ElectricPowerFlowConnection.MW_FLOW_PHASE_B_KEY) != null || edge.getAttribute(ElectricPowerFlowConnection.MW_FLOW_PHASE_C_KEY) != null) {
          ScenarioAttribute attribute = edge.getAttribute(ElectricPowerFlowConnection.MW_FLOW_PHASE_B_KEY, ScenarioAttribute.class);
          double demand = attribute == null ? 0 : attribute.getValue(scenario) == null ? 0 : attribute.getValue(scenario).doubleValue();
          flowBPS.print(demand + ",");
        }
      }
      flowBPS.println();
      for (ElectricPowerFlowConnection edge : edges) {
        if (edge.getAttribute(ElectricPowerFlowConnection.MW_FLOW_PHASE_A_KEY) != null || edge.getAttribute(ElectricPowerFlowConnection.MW_FLOW_PHASE_B_KEY) != null || edge.getAttribute(ElectricPowerFlowConnection.MW_FLOW_PHASE_C_KEY) != null) {
          ScenarioAttribute attribute = edge.getAttribute(ElectricPowerFlowConnection.MW_FLOW_PHASE_C_KEY, ScenarioAttribute.class);
          double demand = attribute == null ? 0 : attribute.getValue(scenario) == null ? 0 : attribute.getValue(scenario).doubleValue();
          flowCPS.print(demand + ",");
        }
      }
      flowCPS.println();
    }

    // output scenario damage
    for (ElectricPowerFlowConnection edge : edges) {
      damagePS.print(edge + "(" + model.getFirstNode(edge) + " " + model.getSecondNode(edge) + "),");
    }
    damagePS.println();
    for (ScenarioConfiguration scenarioConfiguration : configuration.getScenarioConfigurations()) {
      Scenario scenario = scenarioConfiguration.getScenario();
      for (ElectricPowerFlowConnection edge : edges) {
        boolean damage = !scenario.computeActualStatus(edge, true);
        damagePS.print(damage + ",");
      }
      damagePS.println();
    }
    
    
    // close all the csv files
    objectivePS.close();
    constructionPS.close();
    hardenPS.close();
    switchPS.close();
    switchConstructionPS.close();
    microAPS.close();
    microBPS.close();
    microCPS.close();
    generationAPS.close();
    generationBPS.close();
    generationCPS.close();
    demandAPS.close();
    demandBPS.close();
    demandCPS.close();
    flowAPS.close();
    flowBPS.close();
    flowCPS.close();
    damagePS.close();

    
  }

}
