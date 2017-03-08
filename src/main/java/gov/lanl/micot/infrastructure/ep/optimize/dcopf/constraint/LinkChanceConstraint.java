package gov.lanl.micot.infrastructure.ep.optimize.dcopf.constraint;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.optimize.ConstraintFactory;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.variable.scenario.LinkCapacityOverloadScenarioVariableFactory;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.util.math.solver.LinearConstraint;
import gov.lanl.micot.util.math.solver.LinearConstraintLessEq;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.InvalidConstraintException;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

import java.util.ArrayList;
import java.util.Collection;

/**
 * This constraint adds chance constraints to the OPF
 * @author Russell Bent
 */
public class LinkChanceConstraint implements ConstraintFactory {

  private Collection<Scenario> scenarios = new ArrayList<Scenario>();
  private double epsilon                 = 0;
  
  /**
   * Constraint
   */
  public LinkChanceConstraint(Collection<Scenario> scenarios, double epsilon) {    
    this.scenarios = scenarios;
    this.epsilon = epsilon;
  }

  /**
   * Get the name for the threshold constraint
   * @param line 
   * @return
   */
  public static String getThresholdConstraintName(ElectricPowerFlowConnection line) {
    return "Threshold." + line;
  }
  
  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException, InvalidConstraintException {   
    LinkCapacityOverloadScenarioVariableFactory factory = new LinkCapacityOverloadScenarioVariableFactory(scenarios, epsilon);    
    for (Scenario scenario : scenarios) {
      if (scenario.getWeight() > epsilon) {
        continue;
      }
      
      for (ElectricPowerFlowConnection connection : model.getFlowConnections()) {        
        boolean status = scenario.computeActualStatus(connection, connection.getActualStatus());
        
        if (status ) {
          if (!constraintExists(problem, connection)) {     
            constructConstraint(problem, connection);
          }
          Variable overloadVariable = factory.getVariable(problem, connection, scenario);
          addVariable(problem,overloadVariable,scenario,connection);
        }
      }
    }
  }

  /**
   * Add a variable to problem
   * @param problem
   * @param variable
   * @param scenario
   * @param edge
   */
  public void addVariable(MathematicalProgram problem, Variable variable, Scenario scenario, ElectricPowerFlowConnection edge) {
    LinearConstraint thresholdConstraint = problem.getLinearConstraint(getThresholdConstraintName(edge));
    thresholdConstraint.addVariable(variable, scenario.getWeight());          
  }
  
  /**
   * Determine if a constraint exists or not
   * @param problem
   * @param edge
   * @return
   */
  public boolean constraintExists(MathematicalProgram problem, ElectricPowerFlowConnection edge) {
    LinearConstraint thresholdConstraint = problem.getLinearConstraint(getThresholdConstraintName(edge));
    if (thresholdConstraint == null) {
      return false;
    }
    return true;
  }

  /**
   * Construct the constraint
   * @param problem
   * @param edge
   * @return
   * @throws InvalidConstraintException 
   */
  public LinearConstraint constructConstraint(MathematicalProgram problem, ElectricPowerFlowConnection edge) throws InvalidConstraintException {
    LinearConstraint thresholdConstraint = new LinearConstraintLessEq(getThresholdConstraintName(edge));
    thresholdConstraint.setRightHandSide(epsilon);
    problem.addLinearConstraint(thresholdConstraint);
    return thresholdConstraint;
  }
 
  /**
   * Get the constraint
   * @param problem
   * @param edge
   * @return
   */
  public LinearConstraint getConstraint(MathematicalProgram problem, ElectricPowerFlowConnection edge) {
    return problem.getLinearConstraint(getThresholdConstraintName(edge));
  }
  
}
