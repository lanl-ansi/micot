package gov.lanl.micot.application.rdt.algorithm.ep.bp.constraint.scenario;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.optimize.ConstraintFactory;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.application.rdt.algorithm.ep.bp.variable.scenario.LineSwitchVariableFactory;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.InvalidConstraintException;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;


/**
 * Switches can only be built if the line is built
 * @author Russell Bent
 */
public class LineSwitchBoundConstraint implements ConstraintFactory {

  public Scenario scenario = null;
  
  /**
   * Constraint
   */
  public LineSwitchBoundConstraint(Scenario scenario) {
    this.scenario = scenario;
  }
    
  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException, InvalidConstraintException {
    LineSwitchVariableFactory lineSwitchVariableFactory = new LineSwitchVariableFactory(scenario);
    
    for (ElectricPowerFlowConnection edge : model.getFlowConnections()) {      
      if (lineSwitchVariableFactory.hasVariable(edge)) {      
      	Variable switchVar = lineSwitchVariableFactory.getVariable(problem, edge, scenario);
    	  problem.addBounds(switchVar, 0.0, 1.0);
      }    	
    }
  }

  
  
}
