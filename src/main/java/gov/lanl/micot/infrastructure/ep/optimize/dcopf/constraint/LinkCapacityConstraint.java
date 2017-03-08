package gov.lanl.micot.infrastructure.ep.optimize.dcopf.constraint;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.ep.optimize.ConstraintFactory;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.variable.PhaseAngleVariableFactory;
import gov.lanl.micot.util.math.solver.LinearConstraint;
import gov.lanl.micot.util.math.solver.LinearConstraintGreaterEq;
import gov.lanl.micot.util.math.solver.LinearConstraintLessEq;
import gov.lanl.micot.util.math.solver.exception.InvalidConstraintException;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

/**
 * The link capacity constraint
 * @author Russell Bent
 */
public class LinkCapacityConstraint implements ConstraintFactory {

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
  public LinkCapacityConstraint() {    
  }
    
  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException, InvalidConstraintException {
    double mva = model.getMVABase();
    PhaseAngleVariableFactory phaseAngleVariableFactory = new PhaseAngleVariableFactory();
    
    for (ElectricPowerFlowConnection link : model.getFlowConnections()) {
      if (link.getActualStatus() == false) {
        continue;
      }

      ElectricPowerNode firstNode = model.getFirstNode(link);
      ElectricPowerNode secondNode = model.getSecondNode(link);

      double impedance = link.getSusceptance();

      LinearConstraint constraint = new LinearConstraintLessEq(getFlowLessThanConstraintName(link));
      constraint.setRightHandSide(link.getCapacityRating() / mva);
      constraint.addVariable(phaseAngleVariableFactory.getVariable(problem, firstNode), impedance);
      constraint.addVariable(phaseAngleVariableFactory.getVariable(problem, secondNode), -impedance);
      problem.addLinearConstraint(constraint);

      constraint = new LinearConstraintGreaterEq(getFlowGreaterThanConstraintName(link));
      constraint.setRightHandSide(-link.getCapacityRating() / mva);
      constraint.addVariable(phaseAngleVariableFactory.getVariable(problem, firstNode), impedance);
      constraint.addVariable(phaseAngleVariableFactory.getVariable(problem, secondNode), -impedance);
      problem.addLinearConstraint(constraint);
    }
  }

  
  
}
