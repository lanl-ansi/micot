package gov.lanl.micot.application.rdt.algorithm.ep.constraint.scenario;

import gov.lanl.micot.application.rdt.algorithm.ep.variable.scenario.LineExistVariableFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.variable.scenario.LineHardenVariableFactory;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.optimize.ConstraintFactory;
import gov.lanl.micot.infrastructure.model.Asset;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.util.math.solver.LinearConstraint;
import gov.lanl.micot.util.math.solver.LinearConstraintEquals;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.InvalidConstraintException;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;


/**
 * This constraint ties the hardening variable with the existence variable
 * 
 * x_e = h_e
 * 
 * @author Russell Bent
 */
public class LineHardenExistTieConstraint implements ConstraintFactory {

  private Scenario scenario = null;

  /**
   * Constraint
   */
  public LineHardenExistTieConstraint(Scenario scenario) {
    this.scenario = scenario;
  }
  
  /**
   * Get the constraint name
   * @param edge
   * @param scenario
   * @return
   */
  private String getConstraintName(ElectricPowerFlowConnection edge, Scenario scenario) {
    return "Line Harden And Exist Tie-" + edge + "." + scenario;
  }
  
  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException, InvalidConstraintException {
    LineExistVariableFactory existFactory = new LineExistVariableFactory(scenario);
    LineHardenVariableFactory harden = new LineHardenVariableFactory(scenario);
        
    for (ElectricPowerFlowConnection edge : model.getFlowConnections()) {
      boolean isDamaged = scenario.getModification(edge, Asset.IS_FAILED_KEY, Boolean.class) != null ? scenario.getModification(edge, Asset.IS_FAILED_KEY, Boolean.class) : false;
            
      if (isDamaged) {      
        Variable x_s = existFactory.getVariable(problem, edge, scenario); 
        Variable h_s  = harden.getVariable(problem, edge, scenario);            
        LinearConstraint constraint = new LinearConstraintEquals(getConstraintName(edge, scenario));
        constraint.addVariable(h_s, -1.0);
        constraint.addVariable(x_s, 1.0);
        constraint.setRightHandSide(0.0);        
        problem.addLinearConstraint(constraint);
      }
    }    
  }

  
  
}
