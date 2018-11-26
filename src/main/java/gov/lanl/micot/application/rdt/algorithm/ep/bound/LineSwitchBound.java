package gov.lanl.micot.application.rdt.algorithm.ep.bound;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.optimize.ConstraintFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.variable.LineSwitchVariableFactory;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;


/**
 * Switches can only be built if the line is built
 * @author Russell Bent
 */
public class LineSwitchBound implements ConstraintFactory {
  
  private double upperBound = 0;
  
  /**
   * Constraint
   */
  public LineSwitchBound(double upperBound) {    
    this.upperBound = upperBound;
  }
 
  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException {
    LineSwitchVariableFactory lineSwitchVariableFactory = new LineSwitchVariableFactory();
    
    for (ElectricPowerFlowConnection edge : model.getFlowConnections()) {
      if (lineSwitchVariableFactory.hasVariable(edge)) {      
      	Variable t = lineSwitchVariableFactory.getVariable(problem, edge);  
    	  problem.addBounds(t, 0.0, upperBound);
      }    	
    }
  }

  
  
}
