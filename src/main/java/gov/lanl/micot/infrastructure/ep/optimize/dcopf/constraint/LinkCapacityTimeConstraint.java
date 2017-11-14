package gov.lanl.micot.infrastructure.ep.optimize.dcopf.constraint;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.ep.optimize.ConstraintFactory;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.variable.PhaseAngleTimeVariableFactory;
import gov.lanl.micot.util.math.solver.LinearConstraint;
import gov.lanl.micot.util.math.solver.LinearConstraintGreaterEq;
import gov.lanl.micot.util.math.solver.LinearConstraintLessEq;
import gov.lanl.micot.util.math.solver.exception.InvalidConstraintException;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

/**
 * This constraint for enforcing link capacity constraints
 * @author Russell Bent
 */
public class LinkCapacityTimeConstraint implements ConstraintFactory {

  private int numberOfIncrements = -1;
  private double incrementSize = -1;
    
  /**
   * Constructor
   * @param numberOfIncrements
   * @param incrementSize
   */
  public LinkCapacityTimeConstraint(int numberOfIncrements, double incrementSize) {
    super();
    this.numberOfIncrements = numberOfIncrements;
    this.incrementSize = incrementSize;
  }
  
  /**
   * Get the flow constraint name
   * @param edge
   * @return
   */
  private String getFlowLessThanConstraintName(ElectricPowerFlowConnection edge, double time) {
    return "FlowL" + edge.toString()+"-"+time;
  }

  /**
   * Get the flow constraint name
   * @param edge
   * @return
   */
  private String getFlowGreaterThanConstraintName(ElectricPowerFlowConnection edge, double time) {
    return "FlowG" + edge.toString()+"-"+time;
  }
  
  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException, InvalidConstraintException {
    PhaseAngleTimeVariableFactory phaseAngleVariableFactory = new PhaseAngleTimeVariableFactory(numberOfIncrements, incrementSize);    
    double mva = model.getMVABase();    
    
    for (ElectricPowerFlowConnection link : model.getFlowConnections()) {
      if (link.getStatus() == false) {
        continue;
      }

      ElectricPowerNode firstNode = model.getFirstNode(link);
      ElectricPowerNode secondNode = model.getSecondNode(link);

      for (int i = 0; i < numberOfIncrements; ++i) {
        double time = i * incrementSize;
      
        double impedance = link.getSusceptance();

        LinearConstraint constraint = new LinearConstraintLessEq(getFlowLessThanConstraintName(link,time));
        constraint.setRightHandSide(link.getCapacityRating() / mva);
        constraint.addVariable(phaseAngleVariableFactory.getVariable(problem, firstNode, time), impedance);
        constraint.addVariable(phaseAngleVariableFactory.getVariable(problem, secondNode, time), -impedance);
        problem.addLinearConstraint(constraint);

        constraint = new LinearConstraintGreaterEq(getFlowGreaterThanConstraintName(link,time));
        constraint.setRightHandSide(-link.getCapacityRating() / mva);
        constraint.addVariable(phaseAngleVariableFactory.getVariable(problem, firstNode, time), impedance);
        constraint.addVariable(phaseAngleVariableFactory.getVariable(problem, secondNode, time), -impedance);
        problem.addLinearConstraint(constraint);
      }
    }
    
  }

  
}
