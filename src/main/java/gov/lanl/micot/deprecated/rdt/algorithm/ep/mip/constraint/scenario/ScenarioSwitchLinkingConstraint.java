package gov.lanl.micot.deprecated.rdt.algorithm.ep.mip.constraint.scenario;

import java.util.Collection;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.optimize.mathprogram.constraint.ScenarioConstraintFactory;
import gov.lanl.micot.application.rdt.algorithm.AlgorithmConstants;
import gov.lanl.micot.deprecated.rdt.algorithm.ep.mip.variable.LineSwitchVariableFactory;
import gov.lanl.micot.deprecated.rdt.algorithm.ep.mip.variable.scenario.ScenarioSwitchVariableFactory;
import gov.lanl.micot.deprecated.rdt.algorithm.ep.mip.variable.scenario.ScenarioVariableFactoryUtility;
import gov.lanl.micot.util.math.solver.LinearConstraint;
import gov.lanl.micot.util.math.solver.LinearConstraintGreaterEq;
import gov.lanl.micot.util.math.solver.LinearConstraintLessEq;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.InvalidConstraintException;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

/**
 * Links first stage variables with second stage variables
 * 
 * This is constraint 20 from the AAAI 2015 paper
 * 
 * @author Russell Bent
 */
public class ScenarioSwitchLinkingConstraint extends ScenarioConstraintFactory<ElectricPowerNode, ElectricPowerModel> {

  /**
   * Constructor
   * 
   * @param scenarios
   */
  public ScenarioSwitchLinkingConstraint(Collection<Scenario> scenarios) {
    super(scenarios);
  }

  /**
   * Get the constraint name
   * @param edge
   * @param scenario
   * @return
   */
  private String getConstraintName(ElectricPowerFlowConnection edge, Scenario scenario) {
    return "SwitchLinking-" + edge.toString() + "." + scenario.getIndex();
  }
  
  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException, InvalidConstraintException {
    ScenarioSwitchVariableFactory lineVariableFactory = new ScenarioSwitchVariableFactory(getScenarios());
    LineSwitchVariableFactory factory = new LineSwitchVariableFactory();
    for (Scenario scenario : getScenarios()) {
      for (ElectricPowerFlowConnection edge : model.getFlowConnections()) {
        Variable svariable = lineVariableFactory.getVariable(problem, edge, scenario);
        Variable fvariable = factory.getVariable(problem, edge);
        
        boolean hasVariable = ScenarioVariableFactoryUtility.doCreateSwitchScenarioVariable(edge, scenario);
        boolean canBuild  = edge.getAttribute(AlgorithmConstants.LINE_SWITCH_COST_KEY) != null;
        
        if (hasVariable && canBuild) {
          LinearConstraint constraint = new LinearConstraintGreaterEq(getConstraintName(edge,scenario));
          constraint.addVariable(svariable, 1.0);
          constraint.addVariable(fvariable, 1.0);
          constraint.setRightHandSide(1.0);
          problem.addLinearConstraint(constraint);          
        }
      }
    }
  }

}
