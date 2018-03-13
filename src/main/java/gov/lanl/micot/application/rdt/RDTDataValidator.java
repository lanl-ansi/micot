package gov.lanl.micot.application.rdt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Stack;

import gov.lanl.micot.application.lpnorm.io.LPNormIOConstants;
import gov.lanl.micot.application.rdt.algorithm.AlgorithmConstants;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.variable.scenario.ScenarioVariableFactoryUtility;
import gov.lanl.micot.infrastructure.ep.model.Bus;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.Generator;
import gov.lanl.micot.infrastructure.ep.model.Load;
import gov.lanl.micot.infrastructure.model.FlowConnection;
import gov.lanl.micot.infrastructure.model.Node;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.project.ScenarioConfiguration;
import gov.lanl.micot.infrastructure.util.CycleGenerator;
import gov.lanl.micot.infrastructure.util.IslandGenerator;
import gov.lanl.micot.util.collection.Pair;

/**
 * This is a class used to check the data with an electric power model for inconsitencies that could potentially
 * cause problems when running RDT 
 * @author Russell Bent *
 */
public class RDTDataValidator {

  /**
   * Empty constructor
   */
  public RDTDataValidator() {    
  }
  
  /**
   * Checks the data of the electric power model
   * @param model
   */
  public void checkData(ElectricPowerModel model, Collection<ScenarioConfiguration> scenarios) {
    System.out.println("Performing Data Validation Checks on RDT input...");
    
    checkLineDamageState(model, scenarios);
    checkLineHardenState(model);
    checkLineHardenAndNewState(model);
    checkLoadData(model);
    checkCycles(model);
    checkIslands(model);
    checkLoadBalance(model);
    checkEdgeConnectivity(model);
    
    System.out.println("Data Validation Checks on RDT completed.");
    
  }

  /**
   * Checks to see if a line is in both a damage and a hardened damage state
   * @param model
   * @return
   */
  private boolean checkLineDamageState(ElectricPowerModel model, Collection<ScenarioConfiguration> scenarios) {        
    HashSet<Pair<Scenario,ElectricPowerFlowConnection>> set = new HashSet<Pair<Scenario,ElectricPowerFlowConnection>>();
    
    for (ScenarioConfiguration config : scenarios) {
      for (ElectricPowerFlowConnection edge : model.getFlowConnections()) {
        boolean isHardenedDamaged = ScenarioVariableFactoryUtility.isHardenedDamaged(edge, config.getScenario());         
        boolean isDamaged = ScenarioVariableFactoryUtility.isDamaged(edge, config.getScenario());                 
        if (!isDamaged && isHardenedDamaged) {
          set.add(new Pair<Scenario,ElectricPowerFlowConnection>(config.getScenario(),edge));          
        }
      }
    }
    
    if (set.size() == 0) {
      System.out.println("Data Validation Checks on Line Damage Status completed... success");
    }
    else {
      System.out.println("Data Validation Checks on Line Damage Status completed... fail");      
      System.out.println("\tIn some damage scenarios, a line is not damaged in its normal state but it is damaged in its hardened state. This is not supported. RDT will correct this data by assuming the line is damaged in its normal state in this scenario.");
      System.out.println("\tThe following damage scenarios and lines are in this state");
      System.out.print("\t");
      for (Pair<Scenario,ElectricPowerFlowConnection> pair : set) {
        System.out.print(" (scenario="+pair.getLeft().getIndex()+ ",line="+pair.getRight().toString() + ")");
      }
      System.out.println();      
    }
     return set.size() == 0;
  }
  
  private boolean checkEdgeConnectivity(ElectricPowerModel model) {
    HashSet<ElectricPowerFlowConnection> lines = new HashSet<ElectricPowerFlowConnection>();
     
    for (ElectricPowerFlowConnection connection : model.getFlowConnections()) {
      if (model.getFirstNode(connection) == null || model.getSecondNode(connection) == null) {
        lines.add(connection);
      }
    }
        
    if (lines.size() == 0) {
      System.out.println("Data Validation Checks on Line Connectivity... success");
    }
    else {
      System.out.println("Data Validation Checks on Line Hardening options completed... fail");
      System.out.println("\tSome lines are connected to buses (nodes) that are not defined.");
      System.out.println("\tThe following lines have undefined noes");
      System.out.print("\t\t");
      for (ElectricPowerFlowConnection line : lines) {
        System.out.print(" " +line);
      }
      System.out.println();
    }

    return lines.size() == 0;
  }
  
  /**
   * Checks to see if there are any loads
   * @param model
   * @return
   */
  private boolean checkLoadData(ElectricPowerModel model) {       
    double totalLoad = 0.0;
    for (Load load : model.getLoads()) {
      totalLoad += load.getAttribute(Load.REACTIVE_LOAD_A_MAX_KEY, Double.class);
      totalLoad += load.getAttribute(Load.REACTIVE_LOAD_B_MAX_KEY, Double.class);
      totalLoad += load.getAttribute(Load.REACTIVE_LOAD_C_MAX_KEY, Double.class);

      totalLoad += load.getAttribute(Load.REAL_LOAD_A_MAX_KEY, Double.class);
      totalLoad += load.getAttribute(Load.REAL_LOAD_B_MAX_KEY, Double.class);
      totalLoad += load.getAttribute(Load.REAL_LOAD_C_MAX_KEY, Double.class);
    }
    
    if (totalLoad > 0) {
      System.out.println("Data Validation Checks on Load completed... success");      
    }
    else {
      System.out.println("Data Validation Checks on Load completed... fail");
      System.out.println("\tThere is no load in the system. RDT will not build or operate anything");
    }
        
    return totalLoad > 0.0;
  }

  /**
   * Check to see if any existing lines cannot be hardened
   * @param model
   * @return
   */
  private boolean checkLineHardenState(ElectricPowerModel model) {       
    HashSet<ElectricPowerFlowConnection> lines = new HashSet<ElectricPowerFlowConnection>();
    for (ElectricPowerFlowConnection connection : model.getFlowConnections()) {
      boolean canHarden = connection.getAttribute(AlgorithmConstants.CAN_HARDEN_KEY, Boolean.class);
      boolean isNewLine = connection.getAttribute(AlgorithmConstants.IS_NEW_LINE_KEY, Boolean.class);
      if (canHarden == false && isNewLine == false) {
        lines.add(connection);
      }
    }
    
    if (lines.size() == 0) {
      System.out.println("Data Validation Checks on Line Hardening options completed... success");
    }
    else {
      System.out.println("Data Validation Checks on Line Hardening options completed... fail");
      System.out.println("\tSome lines are not allowed to be hardened. RDT cannot guarantee there will be a feasible solution.");
      System.out.println("\tThe following lines cannot be hardened");
      System.out.print("\t\t");
      for (ElectricPowerFlowConnection line : lines) {
        System.out.print(" " +line);
      }
      System.out.println();
    }
    
    return lines.size() == 0;
  }
  
  /**
   * Check to see if any existing lines cannot be hardened
   * @param model
   * @return
   */
  private boolean checkLineHardenAndNewState(ElectricPowerModel model) {       
    HashSet<ElectricPowerFlowConnection> lines = new HashSet<ElectricPowerFlowConnection>();
    for (ElectricPowerFlowConnection connection : model.getFlowConnections()) {
      boolean canHarden = connection.getAttribute(AlgorithmConstants.CAN_HARDEN_KEY, Boolean.class);
      boolean isNewLine = connection.getAttribute(AlgorithmConstants.IS_NEW_LINE_KEY, Boolean.class);
      if (canHarden && isNewLine) {
        lines.add(connection);
      }
    }
    
    if (lines.size() == 0) {
      System.out.println("Data Validation Checks on Line Hardening and Construction options completed... success");
    }
    else {
      System.out.println("Data Validation Checks on Line Hardening and Construction options completed... fail");
      System.out.println("\tSome lines are labeled as new construction and can be hardened. This is not currently allowed. Hardening will be ignored for these lines:");
      System.out.print("\t\t");
      for (ElectricPowerFlowConnection line : lines) {
        System.out.print(" " +line);
        line.setAttribute(AlgorithmConstants.CAN_HARDEN_KEY, false);
        line.setAttribute(AlgorithmConstants.LINE_HARDEN_COST_KEY, null);
      }
      System.out.println();
    }
    
    return lines.size() == 0;
  }

  /**
   * Check to make sure that all cycles can be run radially
   * @param model
   * @return
   */
  private boolean checkCycles(ElectricPowerModel model) {
    CycleGenerator cycleGenerator = new CycleGenerator();
    ArrayList<Stack<Node>> cycles = cycleGenerator.getCyclesBiConnected(model);
    ArrayList<Stack<Node>> badCycles = new ArrayList<Stack<Node>>();
    
    for (Stack<Node> cycle : cycles) {
      boolean canBreak = false;
      Iterator<Node> it = cycle.iterator();
      Node node1 = it.next();

      while (it.hasNext()) {
        Node node2 = it.next();
        Collection<? extends FlowConnection> connections = model.getFlowConnections(node1, node2);
        boolean off = true;
        for (FlowConnection connection : connections) {
          off &= canSwitch((ElectricPowerFlowConnection) connection);
        }
        canBreak |= off;        
        node1 = node2;
      }
      if (!canBreak) {
        badCycles.add(cycle);
      }
    }
    
    if (badCycles.size() == 0) {
      System.out.println("Data Validation Checks on cycles completed... success");
    }
    else {
      System.out.println("Data Validation Checks on cycles... fail");
      System.out.println("\tSome cycles cannot be operated radially. RDT will not be able to find a feasible solution if radial operations is enforced");
      System.out.println("\tCycles include:");
      for (Stack<Node> cycle : badCycles) {
        System.out.print("\t\t");
        for (Node node : cycle) {
          System.out.print(" " +node);
        }
        System.out.println();
      }
    }
    
    return badCycles.size() == 0;
  }
  
  /**
   * Determine if an edge can be switched
   * @param edge
   * @return
   */
  private boolean canSwitch(ElectricPowerFlowConnection edge) {
    boolean hasSwitch = edge.getAttribute(AlgorithmConstants.HAS_SWITCH_KEY) != null && edge.getAttribute(AlgorithmConstants.HAS_SWITCH_KEY, Boolean.class);
    boolean isDisabled = !edge.getStatus();
    boolean hasCost = edge.getAttribute(AlgorithmConstants.LINE_SWITCH_COST_KEY) == null ? false : true;
    boolean buildSwitch = edge.getAttribute(LPNormIOConstants.LINE_CAN_ADD_SWITCH) == null ? false : edge.getAttribute(LPNormIOConstants.LINE_CAN_ADD_SWITCH, Boolean.class);
    boolean canBuild  = (isDisabled || (!hasCost && !buildSwitch)) ? false : true; 
    
    if (!hasSwitch && !canBuild) {
      return false;
    }
    return true;    
  }
  
  /**
   * Check for islands in the model
   * @param model
   * @return
   */
  private boolean checkIslands(ElectricPowerModel model) {
    IslandGenerator generator = new IslandGenerator();
    Collection<Collection<Node>> islands = generator.getIslands(model);
    if (islands.size() == 1) {
      System.out.println("Data Validation Checks on islands completed... success");
    }
    else {
      System.out.println("Data Validation Checks on islands... fail");
      System.out.println("\tThe network model has islands. RDT can handle islands, but infeasible cases can occur if load is isolated from generators.");
      System.out.println("\tIslands include:");
      for (Collection<Node> island : islands) {
        System.out.print("\t\t");
        for (Node node : island) {
          System.out.print(" " +node);
        }
        System.out.println();
      }
    }    
    return islands.size() == 1;
  }

  /**
   * Checks to see if there are any loads
   * @param model
   * @return
   */
  private boolean checkLoadBalance(ElectricPowerModel model) {       
    double totalRealLoadA = 0.0;
    double totalRealLoadB = 0.0;
    double totalRealLoadC = 0.0;
       
    double totalReactiveLoadA = 0.0;
    double totalReactiveLoadB = 0.0;
    double totalReactiveLoadC = 0.0;

    for (Load load : model.getLoads()) {
      totalReactiveLoadA += load.getAttribute(Load.REACTIVE_LOAD_A_MAX_KEY, Double.class);
      totalReactiveLoadB += load.getAttribute(Load.REACTIVE_LOAD_B_MAX_KEY, Double.class);
      totalReactiveLoadC += load.getAttribute(Load.REACTIVE_LOAD_C_MAX_KEY, Double.class);

      totalRealLoadA += load.getAttribute(Load.REAL_LOAD_A_MAX_KEY, Double.class);
      totalRealLoadB += load.getAttribute(Load.REAL_LOAD_B_MAX_KEY, Double.class);
      totalRealLoadC += load.getAttribute(Load.REAL_LOAD_C_MAX_KEY, Double.class);
    }
    
    double totalRealCapacityA = 0.0;
    double totalRealCapacityB = 0.0;
    double totalRealCapacityC = 0.0;
    
    double totalReactiveCapacityA = 0.0;
    double totalReactiveCapacityB = 0.0;
    double totalReactiveCapacityC = 0.0;
    
    
    for (Generator generator : model.getGenerators()) {
      double maxMicrogrid = generator.getAttribute(AlgorithmConstants.MAX_MICROGRID_KEY) == null ? 0.0 : generator.getAttribute(AlgorithmConstants.MAX_MICROGRID_KEY, Number.class).doubleValue();  
      
      totalRealCapacityA += generator.getRealGenerationMax() + maxMicrogrid;
      totalRealCapacityB += generator.getRealGenerationMax() + maxMicrogrid;
      totalRealCapacityC += generator.getRealGenerationMax() + maxMicrogrid;

      totalReactiveCapacityA += generator.getReactiveGenerationMax() + maxMicrogrid;
      totalReactiveCapacityB += generator.getReactiveGenerationMax() + maxMicrogrid;
      totalReactiveCapacityC += generator.getReactiveGenerationMax() + maxMicrogrid;      
    }
    
    if (totalRealCapacityA >= totalRealLoadA && totalRealCapacityB >= totalRealLoadB && totalRealCapacityC >= totalRealLoadC 
        && totalReactiveCapacityA >= totalReactiveLoadA && totalReactiveCapacityB >= totalReactiveLoadB 
        && totalReactiveCapacityC >= totalReactiveLoadC) {
      System.out.println("Data Validation Checks on available generation completed... success");      
      return true;
    }
    
    System.out.println("Data Validation Checks on available generation completed... fail");
    if (totalRealCapacityA < totalRealLoadA) {
      System.out.println("\tAvailable existing and buildable real power capacity on phase A is smaller than phase A real power: " + totalRealCapacityA + " " + totalRealLoadA);      
    }
    if (totalRealCapacityB < totalRealLoadB) {
      System.out.println("\tAvailable existing and buildable real power capacity on phase B is smaller than phase B real power: " + totalRealCapacityB + " " + totalRealLoadB);      
    }
    if (totalRealCapacityC < totalRealLoadC) {
      System.out.println("\tAvailable existing and buildable real power capacity on phase C is smaller than phase C real power: " + totalRealCapacityC + " " + totalRealLoadC);      
    }


    if (totalReactiveCapacityA < totalReactiveLoadA) {
      System.out.println("\tAvailable existing and buildable reactive power capacity on phase A is smaller than phase A reactive power: " + totalReactiveCapacityA + " " + totalReactiveLoadA);      
    }
    if (totalReactiveCapacityB < totalReactiveLoadB) {
      System.out.println("\tAvailable existing and buildable reactive power capacity on phase B is smaller than phase B reactive power: " + totalReactiveCapacityB + " " + totalReactiveLoadB);      
    }
    if (totalReactiveCapacityC < totalReactiveLoadC) {
      System.out.println("\tAvailable existing and buildable reactive power capacity on phase C is smaller than phase C reactive power: " + totalReactiveCapacityC + " " + totalReactiveLoadC);      
    }
    
    return false;
  }

  
}
