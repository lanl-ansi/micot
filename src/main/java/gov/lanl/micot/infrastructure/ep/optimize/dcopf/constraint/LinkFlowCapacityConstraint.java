package gov.lanl.micot.infrastructure.ep.optimize.dcopf.constraint;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.optimize.ConstraintFactory;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.variable.FlowVariableFactory;
import gov.lanl.micot.util.math.solver.LinearConstraint;
import gov.lanl.micot.util.math.solver.LinearConstraintGreaterEq;
import gov.lanl.micot.util.math.solver.LinearConstraintLessEq;
import gov.lanl.micot.util.math.solver.exception.InvalidConstraintException;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

/**
 * The link capacity constraint using flow variables directly
 * @author Russell Bent
 */
public class LinkFlowCapacityConstraint implements ConstraintFactory {

  
  /**
   * Get the flow constraint name
   * 
   * @param edge
   * @return
   */
  private String getFlowLessThanConstraintName(ElectricPowerFlowConnection edge) {
    return "\"FlowL" + edge.toString() + "\"";
  }

  /**
   * Get the flow constraint name
   * 
   * @param edge
   * @return
   */
  private String getFlowGreaterThanConstraintName(ElectricPowerFlowConnection edge) {
    return "\"FlowG" + edge.toString() + "\"";
  }
  
  /**
   * Constraint
   */
  public LinkFlowCapacityConstraint() {    
  }
    
  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException, InvalidConstraintException {
    double mva = model.getMVABase();
    FlowVariableFactory flowVariableFactory = new FlowVariableFactory();
    
    for (ElectricPowerFlowConnection link : model.getFlowConnections()) {
      if (link.getActualStatus() == false) {
        continue;
      }

      LinearConstraint constraint = new LinearConstraintLessEq(getFlowLessThanConstraintName(link));
      constraint.setRightHandSide(link.getCapacityRating() / mva);
      constraint.addVariable(flowVariableFactory.getVariable(problem, link), 1.0);
      problem.addLinearConstraint(constraint);

      constraint = new LinearConstraintGreaterEq(getFlowGreaterThanConstraintName(link));
      constraint.setRightHandSide(-link.getCapacityRating() / mva);
      constraint.addVariable(flowVariableFactory.getVariable(problem, link), 1.0);
      problem.addLinearConstraint(constraint);
    }
  }

  
  
}
