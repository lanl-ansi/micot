package gov.lanl.micot.application.rdt.algorithm.ep.constraint.scenario;

import gov.lanl.micot.application.rdt.algorithm.ep.variable.scenario.LineActiveVariableFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.variable.scenario.LineExistVariableFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.variable.scenario.LineSwitchVariableFactory;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.optimize.ConstraintFactory;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.util.math.solver.LinearConstraint;
import gov.lanl.micot.util.math.solver.LinearConstraintEquals;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.InvalidConstraintException;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;


/**
 * This constraint ties line active variable with the existence and switching constraint
 * 
 * z_s = x_e - t_e
 * 
 * @author Russell Bent
 */
public class LineActiveTieConstraint implements ConstraintFactory {

  private Scenario scenario = null;

  /**
   * Constraint
   */
  public LineActiveTieConstraint(Scenario scenario) {
    this.scenario = scenario;
  }
  
  /**
   * Get the constraint name
   * @param edge
   * @param scenario
   * @return
   */
  private String getConstraintName(ElectricPowerFlowConnection edge, Scenario scenario) {
    return "Line Active Tie-" + edge + "." + scenario;
  }
  
  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException, InvalidConstraintException {
    LineSwitchVariableFactory switchFactory = new LineSwitchVariableFactory(scenario);
    LineExistVariableFactory existFactory = new LineExistVariableFactory(scenario);
    LineActiveVariableFactory active = new LineActiveVariableFactory(scenario);
        
    for (ElectricPowerFlowConnection edge : model.getFlowConnections()) {
        Variable t_s = switchFactory.getVariable(problem, edge);
        Variable x_s = existFactory.getVariable(problem, edge); 
        Variable z_s  = active.getVariable(problem, edge);
        double rightHandSide = 0;
            
        LinearConstraint constraint = new LinearConstraintEquals(getConstraintName(edge, scenario));
        constraint.addVariable(z_s, 1.0);

        if (t_s != null) {
          constraint.addVariable(t_s, 1.0);
        }
        
        if (x_s != null) {
          constraint.addVariable(x_s, -1.0);
        }
        else {
          rightHandSide = 1;
        }
                
        constraint.setRightHandSide(rightHandSide);        
        problem.addLinearConstraint(constraint);
    }    
  }

  
  
}
