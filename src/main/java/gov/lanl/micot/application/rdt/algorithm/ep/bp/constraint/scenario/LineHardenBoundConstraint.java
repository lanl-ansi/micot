package gov.lanl.micot.application.rdt.algorithm.ep.bp.constraint.scenario;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.optimize.ConstraintFactory;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.application.rdt.algorithm.ep.bp.variable.scenario.LineHardenVariableFactory;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;


/**
 * Bounds on the line hardening variables are 0,1
 * 
 * This is part of constraint 26 in the AAAI 2015 paper
 * 
 * @author Russell Bent
 */
public class LineHardenBoundConstraint implements ConstraintFactory {

  private Scenario scenario = null;
  
  /**
   * Constraint
   */
  public LineHardenBoundConstraint(Scenario scenario) { 
    this.scenario = scenario;
  }
    
  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException {
    LineHardenVariableFactory lineVariableFactory = new LineHardenVariableFactory(scenario);
        
    for (ElectricPowerFlowConnection edge : model.getFlowConnections()) {
      if (lineVariableFactory.hasVariable(edge)) {
        Variable variable = lineVariableFactory.getVariable(problem, edge, scenario);
        problem.addBounds(variable, 0.0, 1.0);
      }      
    }
  }
  
}
