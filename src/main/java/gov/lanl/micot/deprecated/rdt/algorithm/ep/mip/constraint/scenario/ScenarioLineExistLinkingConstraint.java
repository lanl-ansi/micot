package gov.lanl.micot.deprecated.rdt.algorithm.ep.mip.constraint.scenario;

import java.util.Collection;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.optimize.mathprogram.constraint.ScenarioConstraintFactory;
import gov.lanl.micot.application.rdt.algorithm.AlgorithmConstants;
import gov.lanl.micot.deprecated.rdt.algorithm.ep.mip.variable.LineConstructionVariableFactory;
import gov.lanl.micot.deprecated.rdt.algorithm.ep.mip.variable.LineHardenVariableFactory;
import gov.lanl.micot.deprecated.rdt.algorithm.ep.mip.variable.scenario.ScenarioLineExistVariableFactory;
import gov.lanl.micot.deprecated.rdt.algorithm.ep.mip.variable.scenario.ScenarioVariableFactoryUtility;
import gov.lanl.micot.util.math.solver.LinearConstraint;
import gov.lanl.micot.util.math.solver.LinearConstraintEquals;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.InvalidConstraintException;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

/**
 * Links first stage variables with second stage variables
 * 
 * Constraint 21 and 27 in the AAAI 2015 paper
 * 
 * @author Russell Bent
 */
public class ScenarioLineExistLinkingConstraint extends ScenarioConstraintFactory<ElectricPowerNode, ElectricPowerModel> {

  /**
   * Constructor
   * 
   * @param scenarios
   */
  public ScenarioLineExistLinkingConstraint(Collection<Scenario> scenarios) {
    super(scenarios);
  }

  /**
   * Get the constraint name
   * @param edge
   * @param scenario
   * @return
   */
  private String getConstraintName(ElectricPowerFlowConnection edge, Scenario scenario) {
    return "LineExistLinking-" + edge.toString() + "." + scenario.getIndex();
  }
  
  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException, InvalidConstraintException {
    ScenarioLineExistVariableFactory lineVariableFactory = new ScenarioLineExistVariableFactory(getScenarios());
    LineHardenVariableFactory hardenFactory = new LineHardenVariableFactory();
    LineConstructionVariableFactory constructionFactory = new LineConstructionVariableFactory();

    for (Scenario scenario : getScenarios()) {
      for (ElectricPowerFlowConnection edge : model.getFlowConnections()) {
        Variable svariable = lineVariableFactory.getVariable(problem, edge, scenario);
        Variable hvariable = hardenFactory.getVariable(problem, edge);
        Variable cvariable = constructionFactory.getVariable(problem, edge);
        
        boolean canHarden = edge.getAttribute(AlgorithmConstants.CAN_HARDEN_KEY) != null && edge.getAttribute(AlgorithmConstants.CAN_HARDEN_KEY, Boolean.class);
        boolean exists = edge.getAttribute(AlgorithmConstants.IS_NEW_LINE_KEY) == null || edge.getAttribute(AlgorithmConstants.IS_NEW_LINE_KEY, Boolean.class) == false;
        
        if (ScenarioVariableFactoryUtility.doCreateLineExistScenarioVariable(edge, scenario)) {
          LinearConstraint constraint = new LinearConstraintEquals(getConstraintName(edge,scenario));
          if (canHarden) {
            constraint.addVariable(svariable, 1.0);
            constraint.addVariable(hvariable, -1.0);
          }
          else {
            constraint.addVariable(svariable, 1.0);
            constraint.addVariable(cvariable, -1.0);
          }
          constraint.setRightHandSide(0.0);           
          problem.addLinearConstraint(constraint);
        }        
      }
    }
  }

}
