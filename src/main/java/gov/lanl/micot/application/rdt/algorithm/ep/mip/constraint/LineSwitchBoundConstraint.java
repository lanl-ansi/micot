package gov.lanl.micot.application.rdt.algorithm.ep.mip.constraint;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.optimize.ConstraintFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.variable.LineSwitchVariableFactory;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;


/**
 * Bounds on the line switch variables
 * 
 * This is part of constraint 26 in AAAI 2015
 * 
 * @author Russell Bent
 */
public class LineSwitchBoundConstraint implements ConstraintFactory {


  /**
   * Constraint
   */
  public LineSwitchBoundConstraint() {    
  }
    
  @Override
  public void constructConstraint(MathematicalProgram problem,  ElectricPowerModel model) throws VariableExistsException, NoVariableException {
    LineSwitchVariableFactory lineVariableFactory = new LineSwitchVariableFactory();
        
    for (ElectricPowerFlowConnection edge : model.getFlowConnections()) {
      Variable variable = lineVariableFactory.getVariable(problem, edge);
      if (variable != null) {
        problem.addBounds(variable, 0.0, 1.0);        
      }
    }
  }

  
  
}
