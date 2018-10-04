package gov.lanl.micot.application.rdt.algorithm.ep.bp.constraint;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.optimize.ConstraintFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.bp.variable.LineSwitchVariableFactory;
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

  /**
   * Constraint
   */
  public LineSwitchBoundConstraint() {    
  }
 
  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException, InvalidConstraintException {
    LineSwitchVariableFactory lineSwitchVariableFactory = new LineSwitchVariableFactory();
    
    for (ElectricPowerFlowConnection edge : model.getFlowConnections()) {
      if (lineSwitchVariableFactory.hasVariable(edge)) {      
      	Variable switchVar = lineSwitchVariableFactory.getVariable(problem, edge);  
    	  problem.addBounds(switchVar, 0.0, Double.MAX_VALUE);
      }    	
    }
  }

  
  
}
