package gov.lanl.micot.deprecated.rdt.algorithm.ep.mip.constraint.scenario;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.optimize.mathprogram.constraint.ScenarioConstraintFactory;
import gov.lanl.micot.deprecated.rdt.algorithm.ep.mip.variable.scenario.ScenarioChanceConstrainedVariableFactory;
import gov.lanl.micot.util.math.solver.LinearConstraint;
import gov.lanl.micot.util.math.solver.LinearConstraintLessEq;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.InvalidConstraintException;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

import java.util.Collection;

/**
 * The scenario constraint
 * 
 * @author Russell Bent
 */
public class ScenarioChanceConstraint extends ScenarioConstraintFactory<ElectricPowerNode, ElectricPowerModel> {

  private double epsilon = 0;

  /**
   * Constraint
   */
  public ScenarioChanceConstraint(double epsilon, Collection<Scenario> scenarios) {
    super(scenarios);
    this.epsilon = epsilon;
  }

  /**
   * Get the constraint name
   * 
   * @param load
   * @param phase
   * @return
   */
  private String getConstraintName() {
    return "Chance Constraint";
  }

  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException, InvalidConstraintException {
    ScenarioChanceConstrainedVariableFactory chanceFactory = new ScenarioChanceConstrainedVariableFactory(getScenarios());
    LinearConstraint constraint = new LinearConstraintLessEq(getConstraintName());
    
    for (Scenario scenario : getScenarios()) {
      Variable chanceVariable = chanceFactory.getVariable(problem, scenario);
      constraint.addVariable(chanceVariable, 1.0);
    }
    constraint.setRightHandSide(epsilon*((double)getScenarios().size()));    
    problem.addLinearConstraint(constraint);    
  }
  
}
