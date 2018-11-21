package gov.lanl.micot.application.rdt.algorithm.ep.assignment;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.optimize.AssignmentFactory;
import gov.lanl.micot.application.rdt.algorithm.AlgorithmConstants;
import gov.lanl.micot.application.rdt.algorithm.ep.variable.LineSwitchVariableFactory;
import gov.lanl.micot.util.math.solver.Solution;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

/**
 * Assignments the status of line switches
 * @author Russell Bent
 */
public class LineSwitchAssignment implements AssignmentFactory {
    
  /**
   * Constructor
   */
  public LineSwitchAssignment() {
    super();
  }
  
  @Override
  public void performAssignment(ElectricPowerModel model, MathematicalProgram problem, Solution solution) throws VariableExistsException, NoVariableException {
    LineSwitchVariableFactory variableFactory = new LineSwitchVariableFactory();
    
    for (ElectricPowerFlowConnection edge : model.getFlowConnections()) {
      Variable t = variableFactory.getVariable(problem, edge);
      if (t != null) {
        boolean lineBuilt = solution.getValueInt(t) == 1 ? true : false;
        edge.setAttribute(AlgorithmConstants.IS_SWITCH_CONSTRUCTED_KEY, lineBuilt);
      }
    }
  }
}
