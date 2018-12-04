package gov.lanl.micot.deprecated.rdt.algorithm.ep.mip.constraint.scenario.cycle;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.optimize.mathprogram.constraint.ScenarioConstraintFactory;
import gov.lanl.micot.deprecated.rdt.algorithm.ep.mip.variable.scenario.ScenarioLineUseVariableFactory;
import gov.lanl.micot.deprecated.rdt.algorithm.ep.mip.variable.scenario.ScenarioVariableFactoryUtility;
import gov.lanl.micot.deprecated.rdt.algorithm.ep.mip.variable.scenario.cycle.ScenarioLineCycleVariableFactory;
import gov.lanl.micot.util.math.solver.LinearConstraint;
import gov.lanl.micot.util.math.solver.LinearConstraintLessEq;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.InvalidConstraintException;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

import java.util.Collection;

/**
 * This constraint ensures that edges not included in a scenario forces all lines to not be included
 * 
 * Constraint 16 in the AAAI 2015 paper
 * 
 * @author Russell Bent
 */
public class ScenarioIncludeEdgeConstraint extends ScenarioConstraintFactory<ElectricPowerNode, ElectricPowerModel> {


  /**
   * Constraint
   */
  public ScenarioIncludeEdgeConstraint(Collection<Scenario> scenarios) {
    super(scenarios);
  }

  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException, InvalidConstraintException {
    ScenarioLineCycleVariableFactory cycleFactory = new ScenarioLineCycleVariableFactory(getScenarios());
    ScenarioLineUseVariableFactory lFactory = new ScenarioLineUseVariableFactory(getScenarios());
    
    for (Scenario scenario : getScenarios()) {
      for (ElectricPowerFlowConnection connection : model.getFlowConnections()) {
        LinearConstraint constraint = new LinearConstraintLessEq(getConstraintName(connection, scenario));
        Variable xvariable = lFactory.getVariable(problem, connection, scenario);
        Variable cvariable = cycleFactory.getVariable(problem, model.getFirstNode(connection), model.getSecondNode(connection), scenario);
        constraint.setRightHandSide(0.0);
        
        if (ScenarioVariableFactoryUtility.doCreateLineUseScenarioVariable(connection, scenario)) {        
          constraint.addVariable(xvariable, 1.0);
        }
        else {
          int constant = ScenarioVariableFactoryUtility.getLineUseScenarioConstant(connection, scenario);
          constraint.setRightHandSide(-constant);
        }
      
        constraint.addVariable(cvariable, -1.0);
        problem.addLinearConstraint(constraint);
      }
    }

  }

  /**
   * Get the flow constraint name
   * 
   * @param edge
   * @return
   */
  private String getConstraintName(ElectricPowerFlowConnection connection, Scenario scenario) {
    String constraintName = "ScenarioIncludeExclude-" + connection + "-" + scenario;
    return constraintName;
  }

}
