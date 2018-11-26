package gov.lanl.micot.application.rdt.algorithm.ep.bound;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.optimize.ConstraintFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.variable.LineHardenVariableFactory;
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
public class LineHardenBound implements ConstraintFactory {

  private double upperBound = 0;
  
  /**
   * Constraint
   */
  public LineHardenBound(double upperBound) {    
    this.upperBound = upperBound;
  }
    
  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException {
    LineHardenVariableFactory lineVariableFactory = new LineHardenVariableFactory();
        
    for (ElectricPowerFlowConnection edge : model.getFlowConnections()) {
      if (lineVariableFactory.hasVariable(edge)) {
        Variable h = lineVariableFactory.getVariable(problem, edge);
        problem.addBounds(h, 0.0, upperBound);
      }      
    }
  }
  
}
