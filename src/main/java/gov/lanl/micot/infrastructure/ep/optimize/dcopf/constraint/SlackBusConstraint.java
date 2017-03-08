package gov.lanl.micot.infrastructure.ep.optimize.dcopf.constraint;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.ep.optimize.ConstraintFactory;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.variable.PhaseAngleVariableFactory;
import gov.lanl.micot.util.math.solver.LinearConstraint;
import gov.lanl.micot.util.math.solver.LinearConstraintEquals;
import gov.lanl.micot.util.math.solver.exception.InvalidConstraintException;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

import java.util.Collection;

/**
 * This constraint adds constraints related to the slack bus
 * @author Russell Bent
 */
public class SlackBusConstraint implements ConstraintFactory {

  /**
   * Constructor
   */
  public SlackBusConstraint() {    
  }
  
  /**
   * Get the slack bus phase angle name
   * 
   * @return
   */
  protected static String getSlackBusPhaseAngleConstraintName() {
    return "SlackBus";
  }
  
  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException, InvalidConstraintException {
    PhaseAngleVariableFactory phaseAngleVariableFactory = new PhaseAngleVariableFactory();    
    for (ElectricPowerNode node : model.getNodes()) {
      LinearConstraint constraint = new LinearConstraintEquals(getSlackBusPhaseAngleConstraintName());
      constraint.addVariable(phaseAngleVariableFactory.getVariable(problem, node),1.0);
      constraint.setRightHandSide(0.0);       
      problem.addLinearConstraint(constraint);
      break;
    }
  }

}
