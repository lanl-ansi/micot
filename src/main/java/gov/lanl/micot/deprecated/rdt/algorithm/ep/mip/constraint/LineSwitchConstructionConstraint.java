package gov.lanl.micot.deprecated.rdt.algorithm.ep.mip.constraint;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.optimize.ConstraintFactory;
import gov.lanl.micot.deprecated.rdt.algorithm.ep.mip.variable.LineConstructionVariableFactory;
import gov.lanl.micot.deprecated.rdt.algorithm.ep.mip.variable.LineSwitchVariableFactory;
import gov.lanl.micot.util.math.solver.LinearConstraint;
import gov.lanl.micot.util.math.solver.LinearConstraintLessEq;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.InvalidConstraintException;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;


/**
 * Switches can only be built if the line is built
 * @author Russell Bent
 */
public class LineSwitchConstructionConstraint implements ConstraintFactory {

  /**
   * Constraint
   */
  public LineSwitchConstructionConstraint() {    
  }
    
  private String getConstraintName(ElectricPowerFlowConnection edge) {
  	return "CoupledBuildConstraint-" + edge.toString();
  }
    
  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException, InvalidConstraintException {
    LineSwitchVariableFactory lineSwitchVariableFactory = new LineSwitchVariableFactory();
    LineConstructionVariableFactory lineConstructionVariableFactory = new LineConstructionVariableFactory();    
    
    for (ElectricPowerFlowConnection edge : model.getFlowConnections()) {
    	Variable switchVar = lineSwitchVariableFactory.getVariable(problem, edge);
    	Variable constructionVar = lineConstructionVariableFactory.getVariable(problem, edge);

    	if (switchVar != null && constructionVar != null) {
    		LinearConstraint constraint = new LinearConstraintLessEq(getConstraintName(edge));
    		constraint.setRightHandSide(0);
    		constraint.addVariable(switchVar, 1.0);
    		constraint.addVariable(constructionVar, -1.0);
    		problem.addLinearConstraint(constraint);
    	}
    }
  }

  
  
}
