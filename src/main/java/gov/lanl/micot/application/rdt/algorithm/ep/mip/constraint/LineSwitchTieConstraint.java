package gov.lanl.micot.application.rdt.algorithm.ep.mip.constraint;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.optimize.ConstraintFactory;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.application.rdt.algorithm.ep.variable.LineSwitchVariableFactory;
import gov.lanl.micot.util.math.solver.LinearConstraint;
import gov.lanl.micot.util.math.solver.LinearConstraintLessEq;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.InvalidConstraintException;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;


/**
 * This constraint ties the outer problem line switch variable with the inner problem line switch variable
 * 
 * t_s <= t
 * 
 * @author Russell Bent
 */
public class LineSwitchTieConstraint implements ConstraintFactory {

  private Scenario scenario = null;

  /**
   * Constraint
   */
  public LineSwitchTieConstraint(Scenario scenario) {
    this.scenario = scenario;
  }
  
  private String getConstraintName(ElectricPowerFlowConnection edge, Scenario scenario) {
    return "Line Switch Tie-" + edge + "." + scenario;
  }
  
  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException, InvalidConstraintException {
    gov.lanl.micot.application.rdt.algorithm.ep.variable.scenario.LineSwitchVariableFactory scenarioFactory = new gov.lanl.micot.application.rdt.algorithm.ep.variable.scenario.LineSwitchVariableFactory(scenario);
    LineSwitchVariableFactory switchFactory = new LineSwitchVariableFactory();
        
    for (ElectricPowerFlowConnection edge : model.getFlowConnections()) {
      if (switchFactory.hasVariable(edge)) {
        Variable t = switchFactory.getVariable(problem, edge);
        Variable t_s = scenarioFactory.getVariable(problem, edge);        
        
        LinearConstraint constraint = new LinearConstraintLessEq(getConstraintName(edge, scenario));
        constraint.addVariable(t, -1.0);
        constraint.addVariable(t_s, 1.0);
        constraint.setRightHandSide(0.0);
        
        problem.addLinearConstraint(constraint);
      }
    }    
  }

  
  
}
