package gov.lanl.micot.deprecated.rdt.algorithm.ep.mip.constraint.scenario.tree;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.deprecated.rdt.algorithm.ep.mip.variable.scenario.ScenarioLineUseVariableFactory;
import gov.lanl.micot.deprecated.rdt.algorithm.ep.mip.variable.scenario.ScenarioVariableFactoryUtility;
import gov.lanl.micot.deprecated.rdt.algorithm.ep.mip.variable.scenario.tree.ScenarioTreeFlowChoiceVariableFactory;
import gov.lanl.micot.util.math.solver.LinearConstraint;
import gov.lanl.micot.util.math.solver.LinearConstraintLessEq;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.InvalidConstraintException;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;
import gov.lanl.micot.infrastructure.optimize.mathprogram.constraint.ScenarioConstraintFactory;
import java.util.Collection;

/**
 * This constraint ensures that choices of edges in the flow network are linked to 
 * the scenario variables about line construction and switches
 * 
 * @author Russell Bent
 */
public class ScenarioTreeLinkingConstraint extends ScenarioConstraintFactory<ElectricPowerNode, ElectricPowerModel> {


  /**
   * Constraint
   */
  public ScenarioTreeLinkingConstraint(Collection<Scenario> scenarios) {
    super(scenarios);
  }

  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException, InvalidConstraintException {
    ScenarioLineUseVariableFactory useFactory = new ScenarioLineUseVariableFactory(getScenarios());
    ScenarioTreeFlowChoiceVariableFactory edgeChoiceFactory = new ScenarioTreeFlowChoiceVariableFactory(getScenarios());
    
    for (Scenario scenario : getScenarios()) {
      for (ElectricPowerFlowConnection connection : model.getFlowConnections()) {                
        Variable useVar = useFactory.getVariable(problem, connection, scenario);
        Variable edgeVar = edgeChoiceFactory.getVariable(problem, model.getFirstNode(connection), model.getSecondNode(connection), scenario);
        LinearConstraint constraint = new LinearConstraintLessEq(getConstraintName(connection, scenario));
        constraint.setRightHandSide(0.0);

        if (ScenarioVariableFactoryUtility.doCreateLineUseScenarioVariable(connection, scenario)) {
          constraint.addVariable(useVar, 1.0);
        }                
        else {
          int constant = ScenarioVariableFactoryUtility.getLineUseScenarioConstant(connection, scenario);
          constraint.setRightHandSide(-constant);
        }
        
        constraint.addVariable(edgeVar, -1.0);
        problem.addLinearConstraint(constraint);        
      }
    }

  }

  /**
   * Get the ge name
   * @param connection
   * @param scenario
   * @return
   */
  private String getConstraintName(ElectricPowerFlowConnection connection, Scenario scenario) {
    String constraintName = "TreeLink-" + connection + "-" + scenario;
    return constraintName;
  }

}
