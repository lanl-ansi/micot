package gov.lanl.micot.application.rdt.algorithm.ep.constraint;

import gov.lanl.micot.application.rdt.algorithm.ep.variable.scenario.LineExistVariableFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.variable.scenario.LineSwitchVariableFactory;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.optimize.ConstraintFactory;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.util.math.solver.LinearConstraint;
import gov.lanl.micot.util.math.solver.LinearConstraintLessEq;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.InvalidConstraintException;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;


/**
 * This constraint ties line existing with the switch status
 * 
 * x_s >= t_s
 * 
 * @author Russell Bent
 */
public class LineSwitchExistTieConstraint implements ConstraintFactory {

  private Scenario scenario = null;

  /**
   * Constraint
   */
  public LineSwitchExistTieConstraint(Scenario scenario) {
    this.scenario = scenario;
  }
  
  private String getConstraintName(ElectricPowerFlowConnection edge, Scenario scenario) {
    return "Line Switch Exist Tie-" + edge + "." + scenario;
  }
  
  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException, InvalidConstraintException {
    LineSwitchVariableFactory switchFactory = new LineSwitchVariableFactory(scenario);
    LineExistVariableFactory existFactory = new LineExistVariableFactory(scenario);
        
    for (ElectricPowerFlowConnection edge : model.getFlowConnections()) {
      if (switchFactory.hasVariable(edge)) {
        Variable t_s = switchFactory.getVariable(problem, edge);
        Variable x_s = existFactory.getVariable(problem, edge, scenario);        
        
        LinearConstraint constraint = new LinearConstraintLessEq(getConstraintName(edge, scenario));
        constraint.addVariable(x_s, 1.0);
        constraint.addVariable(t_s, -1.0);
        constraint.setRightHandSide(0.0);
        
        problem.addLinearConstraint(constraint);
      }
    }    
  }

  
  
}
