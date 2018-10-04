package gov.lanl.micot.application.rdt.algorithm.ep.bp.constraint.dual;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.optimize.mathprogram.constraint.ScenarioConstraintFactory;

import java.util.Collection;

import gov.lanl.micot.application.rdt.algorithm.AlgorithmConstants;
import gov.lanl.micot.application.rdt.algorithm.ep.bp.variable.dual.YLineSwitchVariableFactory;
import gov.lanl.micot.util.math.solver.LinearConstraint;
import gov.lanl.micot.util.math.solver.LinearConstraintGreaterEq;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.InvalidConstraintException;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;


/**
 * Constraints associated with the columns of the line harden variables
 * 
 * @author Russell Bent
 */
public class LineSwitchConstraint extends ScenarioConstraintFactory<ElectricPowerNode, ElectricPowerModel> {

  /**
   * Constructor
   */
  public LineSwitchConstraint(Collection<Scenario> scenarios) {
    super(scenarios);
  }
    
  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException, InvalidConstraintException {
    YLineSwitchVariableFactory variableFactory = new YLineSwitchVariableFactory(getScenarios());
        
    for (ElectricPowerFlowConnection edge : model.getFlowConnections()) {
      LinearConstraint constraint = new LinearConstraintGreaterEq(getName(edge));  
      double cost = edge.getAttribute(AlgorithmConstants.LINE_SWITCH_COST_KEY) != null ? -edge.getAttribute(AlgorithmConstants.LINE_SWITCH_COST_KEY, Number.class).doubleValue() : 0.0;
      constraint.setRightHandSide(cost);
          
      for (Scenario scenario : getScenarios()) {
        if (variableFactory.hasVariable(edge)) {
          Variable variable = variableFactory.getVariable(problem, edge, scenario);
          constraint.addVariable(variable, -1);
        }
      }
      problem.addLinearConstraint(constraint);

    }
  }
    
  /**
   * Get the constraint name
   * 
   * @param edge
   * @return
   */
  private String getName(ElectricPowerFlowConnection edge) {
    return "LineSwitchDual." + edge.toString();
  }
  
}
