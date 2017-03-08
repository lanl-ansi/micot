package gov.lanl.micot.infrastructure.ep.optimize.dcopf.assignment;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.ep.optimize.AssignmentFactory;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.variable.PhaseAngleVariableFactory;
import gov.lanl.micot.util.math.solver.Solution;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

/**
 * General class for creating phase angle variable assignments
 * @author Russell Bent
 */
public class PhaseAngleAssignmentFactory implements AssignmentFactory {

  @Override
  public void performAssignment(ElectricPowerModel model, MathematicalProgram problem, Solution solution) throws VariableExistsException, NoVariableException {
    PhaseAngleVariableFactory phaseAngleVariableFactory = new PhaseAngleVariableFactory();
    for (ElectricPowerNode node : model.getNodes()) {
      Variable variable = phaseAngleVariableFactory.getVariable(problem, node);
      node.getBus().setPhaseAngle(Math.toDegrees(solution.getValueDouble(variable)));
    }    
  }

 
}
