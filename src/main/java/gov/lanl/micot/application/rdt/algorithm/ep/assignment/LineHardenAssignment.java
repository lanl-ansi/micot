package gov.lanl.micot.application.rdt.algorithm.ep.assignment;


import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.optimize.AssignmentFactory;
import gov.lanl.micot.application.rdt.algorithm.AlgorithmConstants;
import gov.lanl.micot.application.rdt.algorithm.ep.variable.LineHardenVariableFactory;
import gov.lanl.micot.util.math.solver.ContinuousVariable;
import gov.lanl.micot.util.math.solver.Solution;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

/**
 * Assignments the status of line hardening
 * @author Russell Bent
 */
public class LineHardenAssignment implements AssignmentFactory {
    
  /**
   * Constructor
   * @param numberOfIncrements
   * @param incrementSize
   */
  public LineHardenAssignment() {
    super();
  }
  
  @Override
  public void performAssignment(ElectricPowerModel model, MathematicalProgram problem, Solution solution) throws VariableExistsException, NoVariableException {
    LineHardenVariableFactory variableFactory = new LineHardenVariableFactory();
    
    for (ElectricPowerFlowConnection edge : model.getFlowConnections()) {
      Variable h = variableFactory.getVariable(problem, edge);
      if (h != null) {
        boolean lineBuilt = false;
        if (h instanceof ContinuousVariable) {
          lineBuilt = solution.getValueDouble(h) >= 1e-4 ? true : false;          
        }
        else {
          lineBuilt = solution.getValueInt(h) == 1 ? true : false;
        }
        
        edge.setAttribute(AlgorithmConstants.IS_HARDENED_KEY, lineBuilt);
      }
    }
  }
}
