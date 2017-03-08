package gov.lanl.micot.infrastructure.ep.optimize.dcopf.constraint.scenario;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.optimize.ConstraintFactory;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.variable.scenario.LinkCapacityOverloadScenarioVariableFactory;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.util.math.solver.LinearConstraint;
import gov.lanl.micot.util.math.solver.LinearConstraintGreaterEq;
import gov.lanl.micot.util.math.solver.LinearConstraintLessEq;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.InvalidConstraintException;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

import java.util.ArrayList;
import java.util.Collection;

/**
 * This constraint adds constraints to the 0-1 variables corresponding to line overload constraints
 * Those variables have to be more than 0
 * @author Russell Bent
 */
public class LinkCapacityOverloadScenarioVariableConstraint implements ConstraintFactory {

  private Collection<Scenario> scenarios = new ArrayList<Scenario>();
  private double epsilon = 0;
  
  /**
   * Constraint
   */
  public LinkCapacityOverloadScenarioVariableConstraint(Collection<Scenario> scenarios, double epsilon) {    
    this.scenarios = scenarios;
    this.epsilon = epsilon;
  }
  
  /**
   * Get the name for the greater than zero overload constraint
   * 
   * @param k scenario number
   * @param edge
   * @return
   */
  private static String getOverloadGreaterEqConstraintName(int k, ElectricPowerFlowConnection edge) {
    return "OG" + k + "." + edge.toString();
  }
  
  private static String getOverloadLessEqConstraintName(int k, ElectricPowerFlowConnection edge) {
    return "OL" + k + "." + edge.toString();
  }
  
  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException, InvalidConstraintException {
    LinkCapacityOverloadScenarioVariableFactory factory = new LinkCapacityOverloadScenarioVariableFactory(scenarios, epsilon);
        
    for (Scenario scenario : scenarios) {
      if (scenario.getWeight() > epsilon) {
        continue;
      }
      
      for (ElectricPowerFlowConnection connection : model.getFlowConnections()) {
        boolean status = scenario.computeActualStatus(connection, true);
        
        if (status) {
          Variable variable = factory.getVariable(problem, connection, scenario);          
          createGEConstraint(problem, variable, scenario, connection);  
          createLEConstraint(problem, variable, scenario, connection);  
        }
      }
    }
  }

  /**
   * Create the constraint
   * @param problem
   * @param variable
   * @param scenario
   * @param linK
   * @throws InvalidConstraintException 
   */
  public void createGEConstraint(MathematicalProgram problem, Variable variable, Scenario scenario, ElectricPowerFlowConnection connection) throws InvalidConstraintException {
    LinearConstraint constraint = new LinearConstraintGreaterEq(getOverloadGreaterEqConstraintName(scenario.getIndex(), connection));
    constraint.addVariable(variable, 1.0);
    constraint.setRightHandSide(0.0);
    problem.addLinearConstraint(constraint);       
  }
 
  /**
   * Create the constraint
   * @param problem
   * @param variable
   * @param scenario
   * @param linK
   * @throws InvalidConstraintException 
   */
  public void createLEConstraint(MathematicalProgram problem, Variable variable, Scenario scenario, ElectricPowerFlowConnection connection) throws InvalidConstraintException {
    LinearConstraint constraint = new LinearConstraintLessEq(getOverloadLessEqConstraintName(scenario.getIndex(), connection));
    constraint.addVariable(variable, 1.0);
    constraint.setRightHandSide(1.0);
    problem.addLinearConstraint(constraint);       
  }
}
