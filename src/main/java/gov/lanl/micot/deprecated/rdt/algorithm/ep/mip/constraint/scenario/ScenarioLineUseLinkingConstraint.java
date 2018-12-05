package gov.lanl.micot.deprecated.rdt.algorithm.ep.mip.constraint.scenario;

import java.util.Collection;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.optimize.mathprogram.constraint.ScenarioConstraintFactory;
import gov.lanl.micot.deprecated.rdt.algorithm.ep.mip.variable.scenario.ScenarioLineExistVariableFactory;
import gov.lanl.micot.deprecated.rdt.algorithm.ep.mip.variable.scenario.ScenarioLineUseVariableFactory;
import gov.lanl.micot.deprecated.rdt.algorithm.ep.mip.variable.scenario.ScenarioSwitchVariableFactory;
import gov.lanl.micot.deprecated.rdt.algorithm.ep.mip.variable.scenario.ScenarioVariableFactoryUtility;
import gov.lanl.micot.util.math.solver.LinearConstraint;
import gov.lanl.micot.util.math.solver.LinearConstraintEquals;
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
 * Constraint 21 and 27 in the AAAI 2015 paper
 * 
 * @author Russell Bent
 */
public class ScenarioLineUseLinkingConstraint extends ScenarioConstraintFactory<ElectricPowerNode, ElectricPowerModel> {

  /**
   * Constructor
   * 
   * @param scenarios
   */
  public ScenarioLineUseLinkingConstraint(Collection<Scenario> scenarios) {
    super(scenarios);
  }

  /**
   * Get the constraint name 1
   * @param edge
   * @param scenario
   * @return
   */
  private String getConstraintName1(ElectricPowerFlowConnection edge, Scenario scenario) {
    return "LineUseLinking1-" + edge.toString() + "." + scenario.getIndex();
  }

  /**
   * Get the constraint name 2
   * @param edge
   * @param scenario
   * @return
   */
  private String getConstraintName2(ElectricPowerFlowConnection edge, Scenario scenario) {
    return "LineUseLinking2-" + edge.toString() + "." + scenario.getIndex();
  }
  
  /**
   * Get the constraint name 3
   * @param edge
   * @param scenario
   * @return
   */
  private String getConstraintName3(ElectricPowerFlowConnection edge, Scenario scenario) {
    return "LineUseLinking3-" + edge.toString() + "." + scenario.getIndex();
  }

  
  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException, InvalidConstraintException {
    ScenarioLineUseVariableFactory lineVariableFactory = new ScenarioLineUseVariableFactory(getScenarios());
    ScenarioLineExistVariableFactory existFactory = new ScenarioLineExistVariableFactory(getScenarios());
    ScenarioSwitchVariableFactory switchFactory = new ScenarioSwitchVariableFactory(getScenarios());

    for (Scenario scenario : getScenarios()) {
      for (ElectricPowerFlowConnection edge : model.getFlowConnections()) {
        Variable svariable = lineVariableFactory.getVariable(problem, edge, scenario);
        Variable existVariable = existFactory.getVariable(problem, edge, scenario);
        Variable switchVariable = switchFactory.getVariable(problem, edge, scenario);
        
        boolean hasExist = ScenarioVariableFactoryUtility.doCreateLineExistScenarioVariable(edge, scenario);
        boolean hasSwitch = ScenarioVariableFactoryUtility.doCreateSwitchScenarioVariable(edge, scenario);
        boolean hasUse = ScenarioVariableFactoryUtility.doCreateLineUseScenarioVariable(edge, scenario);
        
        if (hasUse) {
          if (hasExist && hasSwitch) {
            LinearConstraint constraint1 = new LinearConstraintGreaterEq(getConstraintName1(edge,scenario));
            constraint1.addVariable(svariable, 1.0);
            constraint1.addVariable(existVariable, -1.0);
            constraint1.addVariable(switchVariable, -1.0);
            constraint1.setRightHandSide(-1.0);
            problem.addLinearConstraint(constraint1);            
            
            LinearConstraint constraint2 = new LinearConstraintLessEq(getConstraintName2(edge,scenario));
            constraint2.addVariable(svariable, 1.0);
            constraint2.addVariable(existVariable, -1.0);
            constraint2.setRightHandSide(0.0);
            problem.addLinearConstraint(constraint2);            

            LinearConstraint constraint3 = new LinearConstraintLessEq(getConstraintName3(edge,scenario));
            constraint3.addVariable(svariable, 1.0);
            constraint3.addVariable(switchVariable, -1.0);
            constraint3.setRightHandSide(0.0);
            problem.addLinearConstraint(constraint3);            
          }
          else if (hasExist) {
            LinearConstraint constraint1 = new LinearConstraintEquals(getConstraintName1(edge,scenario));
            Integer constant = ScenarioVariableFactoryUtility.getSwitchScenarioConstant(edge, scenario);
            constraint1.addVariable(svariable, 1.0);
            constraint1.addVariable(existVariable, -constant);
            constraint1.setRightHandSide(0.0);            
            problem.addLinearConstraint(constraint1);            
          }
          else {
            LinearConstraint constraint1 = new LinearConstraintEquals(getConstraintName1(edge,scenario));
            Integer constant = ScenarioVariableFactoryUtility.getLineExistScenarioConstant(edge, scenario);
            constraint1.addVariable(svariable, 1.0);
            constraint1.addVariable(switchVariable, -constant);
            constraint1.setRightHandSide(0.0);            
            problem.addLinearConstraint(constraint1);                        
          }
        }
      }
    }
  }

}
