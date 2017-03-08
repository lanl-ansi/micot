package gov.lanl.micot.application.rdt.algorithm.ep.mip.assignment;


import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.optimize.AssignmentFactory;
import gov.lanl.micot.application.rdt.algorithm.AlgorithmConstants;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.variable.LineHardenVariableFactory;
import gov.lanl.micot.util.math.solver.Solution;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

/**
 * Assignments the status of line hardening
 * @author Russell Bent
 */
public class LineHardenAssignmentFactory implements AssignmentFactory {
    
  /**
   * Constructor
   * @param numberOfIncrements
   * @param incrementSize
   */
  public LineHardenAssignmentFactory() {
    super();
  }
  
  @Override
  public void performAssignment(ElectricPowerModel model, MathematicalProgram problem, Solution solution) throws VariableExistsException, NoVariableException {
    LineHardenVariableFactory variableFactory = new LineHardenVariableFactory();
    
    for (ElectricPowerFlowConnection edge : model.getFlowConnections()) {
      Variable variable = variableFactory.getVariable(problem, edge);
      if (variable != null) {
        boolean lineBuilt = solution.getValueInt(variable) == 1 ? true : false;
        edge.setAttribute(AlgorithmConstants.IS_HARDENED_KEY, lineBuilt);
      }
    }
  }
}
