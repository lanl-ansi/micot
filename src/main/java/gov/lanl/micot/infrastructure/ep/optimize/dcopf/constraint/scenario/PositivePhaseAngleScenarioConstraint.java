package gov.lanl.micot.infrastructure.ep.optimize.dcopf.constraint.scenario;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.ep.optimize.ConstraintFactory;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.variable.scenario.PhaseAngleScenarioVariableFactory;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.util.math.solver.LinearConstraint;
import gov.lanl.micot.util.math.solver.LinearConstraintGreaterEq;
import gov.lanl.micot.util.math.solver.exception.InvalidConstraintException;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

import java.util.ArrayList;
import java.util.Collection;

/**
 * This constraint forces phase angles to be positive
 * @author Russell Bent
 */
public class PositivePhaseAngleScenarioConstraint implements ConstraintFactory {

  private Collection<Scenario> scenarios = new ArrayList<Scenario>();
  
  /**
   * Constraint
   */
  public PositivePhaseAngleScenarioConstraint(Collection<Scenario> scenarios) {    
    this.scenarios = scenarios;
  }

  /**
   * Get the slack bus phase angle name
   * 
   * @return
   */
  private static String getPhaseAngleConstraintName(int k, ElectricPowerNode node) {
    return "Positive-" + node.toString() + "-" + k;
  }

  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException, InvalidConstraintException {   
    PhaseAngleScenarioVariableFactory factory = new PhaseAngleScenarioVariableFactory(scenarios);
    
    for (Scenario scenario : scenarios) {
      int k = scenario.getIndex();
      for (ElectricPowerNode node : model.getNodes()) {
        LinearConstraint constraint = new LinearConstraintGreaterEq(getPhaseAngleConstraintName(k, node));
        constraint.addVariable(factory.getVariable(problem, node, scenario),1.0);
        constraint.setRightHandSide(0.0);
        problem.addLinearConstraint(constraint);
      }
    }
  }

 
}
