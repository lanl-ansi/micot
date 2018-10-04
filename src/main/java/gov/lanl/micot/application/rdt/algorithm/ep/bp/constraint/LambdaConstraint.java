package gov.lanl.micot.application.rdt.algorithm.ep.bp.constraint;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.optimize.ConstraintFactory;
import gov.lanl.micot.infrastructure.model.Scenario;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import gov.lanl.micot.application.rdt.algorithm.ep.bp.variable.LambdaVariableFactory;
import gov.lanl.micot.util.math.solver.LinearConstraint;
import gov.lanl.micot.util.math.solver.LinearConstraintEquals;
import gov.lanl.micot.util.math.solver.Solution;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.InvalidConstraintException;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;


/** 
 * Each scenario's lambdas must sum to 1
 * @author Russell Bent
 */
public class LambdaConstraint implements ConstraintFactory {

  private Collection<Scenario> scenarios = null;
  HashMap<Scenario, ArrayList<Solution>> columns = null;
  
  
  /**
   * Constraint
   */
  public LambdaConstraint(Collection<Scenario> scenarios, HashMap<Scenario, ArrayList<Solution>> columns) {    
      this.scenarios = scenarios;
      this.columns = columns;
  }
    
  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException, InvalidConstraintException {
    LambdaVariableFactory variableFactory = new LambdaVariableFactory(scenarios, columns);
   
    for (Scenario scenario : scenarios) {
      ArrayList<Solution> c = columns.get(scenario);
      if (c.size() <= 0) {
        continue;
      }
             
      LinearConstraint constraint = new LinearConstraintEquals(getName(scenario));
      constraint.setRightHandSide(1.0);
      for (int i = 0; i < c.size(); ++i) {
        Variable variable = variableFactory.getVariable(problem, scenario, i);
        constraint.addVariable(variable, 1.0);
      }
      problem.addLinearConstraint(constraint);
    }
  }
  
  /**
   * Get the flow constraint name
   * 
   * @param edge
   * @return
   */
  private String getName(Scenario scenario) {
    return "Lambda." + scenario.getIndex();
  }

  
}
