package gov.lanl.micot.application.rdt.algorithm.ep.bp.bound;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.optimize.ConstraintFactory;
import gov.lanl.micot.infrastructure.model.Scenario;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import gov.lanl.micot.application.rdt.algorithm.ep.bp.variable.LambdaVariableFactory;
import gov.lanl.micot.util.math.solver.Solution;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;


/** 
 * Bounds on the lambda variable
 * @author Russell Bent
 */
public class LambdaBoundConstraint implements ConstraintFactory {

  private Collection<Scenario> scenarios = null;
  HashMap<Scenario, ArrayList<Solution>> columns = null;
  
  
  /**
   * Constraint
   */
  public LambdaBoundConstraint(Collection<Scenario> scenarios, HashMap<Scenario, ArrayList<Solution>> columns) {    
      this.scenarios = scenarios;
      this.columns = columns;
  }
    
  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException {
    LambdaVariableFactory variableFactory = new LambdaVariableFactory(scenarios, columns);
   
    for (Scenario scenario : scenarios) {
      ArrayList<Solution> c = columns.get(scenario);
      for (int i = 0; i < c.size(); ++i) {
        Variable variable = variableFactory.getVariable(problem, scenario, i);
        problem.addBounds(variable, 0.0, Double.MAX_VALUE);
      }
    }
  }
  
  
}
