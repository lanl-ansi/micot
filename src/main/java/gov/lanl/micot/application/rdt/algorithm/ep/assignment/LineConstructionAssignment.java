package gov.lanl.micot.application.rdt.algorithm.ep.assignment;


import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.optimize.AssignmentFactory;
import gov.lanl.micot.application.rdt.algorithm.AlgorithmConstants;
import gov.lanl.micot.application.rdt.algorithm.ep.variable.LineConstructionVariableFactory;
import gov.lanl.micot.util.math.solver.Solution;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

/**
 * Assignments the status of line construction
 * @author Russell Bent
 */
public class LineConstructionAssignment implements AssignmentFactory {
    
  /**
   * Constructor
   */
  public LineConstructionAssignment() {
    super();
  }
  
  @Override
  public void performAssignment(ElectricPowerModel model, MathematicalProgram problem, Solution solution) throws VariableExistsException, NoVariableException {
    LineConstructionVariableFactory variableFactory = new LineConstructionVariableFactory();
    
    for (ElectricPowerFlowConnection edge : model.getFlowConnections()) {
      Variable x = variableFactory.getVariable(problem, edge);
      if (x != null) {
        boolean lineBuilt = solution.getValueInt(x) == 1 ? true : false;
        edge.setAttribute(AlgorithmConstants.IS_CONSTRUCTED_KEY, lineBuilt);
      }
    }
  }
}
