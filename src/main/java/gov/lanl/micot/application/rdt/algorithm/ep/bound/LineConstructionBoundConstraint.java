package gov.lanl.micot.application.rdt.algorithm.ep.bound;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.optimize.ConstraintFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.variable.LineConstructionVariableFactory;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;


/**
 * Bounds on the line construction variables are 0,1
 * @author Russell Bent
 */
public class LineConstructionBoundConstraint implements ConstraintFactory {

  private double upperBound = 0;
  
  /**
   * Constraint
   */
  public LineConstructionBoundConstraint(double upperBound) {    
    this.upperBound = upperBound;
  }
    
  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException {
    LineConstructionVariableFactory lineVariableFactory = new LineConstructionVariableFactory();
        
    for (ElectricPowerFlowConnection edge : model.getFlowConnections()) {
      if (lineVariableFactory.hasVariable(edge)) {      
        Variable variable = lineVariableFactory.getVariable(problem, edge);
        problem.addBounds(variable, 0.0, upperBound);
      }      
    }
  }
}
