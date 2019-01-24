package gov.lanl.micot.application.rdt.algorithm.ep.mip.constraint;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.optimize.ConstraintFactory;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.application.rdt.algorithm.ep.variable.LineHardenVariableFactory;
import gov.lanl.micot.util.math.solver.LinearConstraint;
import gov.lanl.micot.util.math.solver.LinearConstraintEquals;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.InvalidConstraintException;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;


/**
 * This constraint ties the outer problem line harden variable with the inner problem line harden variable
 * 
 * h = h_s
 * 
 * @author Russell Bent
 */
public class LineHardenTieConstraint implements ConstraintFactory {

  private Scenario scenario = null;

  /**
   * Constraint
   */
  public LineHardenTieConstraint(Scenario scenario) {
    this.scenario = scenario;
  }
  
  private String getConstraintName(ElectricPowerFlowConnection edge, Scenario scenario) {
    return "Line Harden Tie-" + edge + "." + scenario;
  }
  
  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException, InvalidConstraintException {
    gov.lanl.micot.application.rdt.algorithm.ep.variable.scenario.LineHardenVariableFactory scenarioFactory = new gov.lanl.micot.application.rdt.algorithm.ep.variable.scenario.LineHardenVariableFactory(scenario,null);
    LineHardenVariableFactory hardenFactory = new LineHardenVariableFactory();
        
    for (ElectricPowerFlowConnection edge : model.getFlowConnections()) {
      if (hardenFactory.hasVariable(edge)) {
        Variable h = hardenFactory.getVariable(problem, edge);
        Variable h_s = scenarioFactory.getVariable(problem, edge);        
        
        LinearConstraint constraint = new LinearConstraintEquals(getConstraintName(edge, scenario));
        constraint.addVariable(h, 1.0);
        constraint.addVariable(h_s, -1.0);
        constraint.setRightHandSide(0.0);
        
        problem.addLinearConstraint(constraint);
      }
    }    
  }

  
  
}
