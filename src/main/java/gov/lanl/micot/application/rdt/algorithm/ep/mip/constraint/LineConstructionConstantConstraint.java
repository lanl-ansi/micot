package gov.lanl.micot.application.rdt.algorithm.ep.mip.constraint;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.optimize.ConstraintFactory;
import gov.lanl.micot.application.rdt.algorithm.AlgorithmConstants;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.variable.LineConstructionVariableFactory;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

/**
 * Bounds on the line construction variables are set to a specific value.
 * Typically used in a decomposition type approach
 * @author Russell Bent
 */
public class LineConstructionConstantConstraint implements ConstraintFactory {


  /**
   * Constraint
   */
  public LineConstructionConstantConstraint() {    
  }
    
  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException {
    LineConstructionVariableFactory lineVariableFactory = new LineConstructionVariableFactory();
        
    for (ElectricPowerFlowConnection edge : model.getFlowConnections()) {
      Variable variable = lineVariableFactory.getVariable(problem, edge);
      if (variable != null) {
        int value = edge.getAttribute(AlgorithmConstants.IS_CONSTRUCTED_KEY, Boolean.class) ? 1 : 0;        
        problem.addBounds(variable, value, value);
      }      
    }
  }

  
  
}
