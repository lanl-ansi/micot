package gov.lanl.micot.application.rdt.algorithm.ep.sbd;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.optimize.sbd.ScenarioBasedDecompositionOptimizerImpl;
import gov.lanl.micot.util.math.MathUtils;
import gov.lanl.micot.util.math.solver.Solution;

import java.util.Collection;
import java.util.HashMap;

/**
 * Abstract implementation of a scenario based decomposition algorithm 
 * for optimizing infrastructure systems
 * @author Russell Bent
 */
public class SBDResilienceAlgorithm extends ScenarioBasedDecompositionOptimizerImpl<ElectricPowerNode, ElectricPowerModel> {

  
  /**
   * Constructor
   */
  public SBDResilienceAlgorithm(Collection<Scenario> scenarios) {
    super();
    setScenarios(scenarios);
    
    System.err.println("Are the chance constraints set correctly?  Does SBD check for that in a reasonable way?");
    System.err.println("Make sure the empty scenario is included in the model");
  }

  @Override
  protected boolean notConverged(HashMap<Scenario, Solution> innerSolutions) {
    System.err.println("Not converged needs to be dependent on whether or not we have chance constraints. Then we have a counting argument here between how many were satisfied in the original along with this one");
    
    for (Scenario scenario : innerSolutions.keySet()) {
      Solution solution = innerSolutions.get(scenario);
      if (!solution.isFeasible() || MathUtils.DOUBLE_GREATER_THAN(Math.abs(solution.getObjectiveValue()), 0.0)) {
        return true;
      }
    }    
    return false;
  }

  
}
