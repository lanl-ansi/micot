package gov.lanl.micot.application.rdt.algorithm.ep.mip.constraint.scenario;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.ep.model.Generator;
import gov.lanl.micot.infrastructure.ep.model.Load;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.optimize.mathprogram.constraint.ScenarioConstraintFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.variable.scenario.ScenarioReactiveFlowPhaseVariableFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.variable.scenario.ScenarioGeneratorReactivePhaseVariableFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.variable.scenario.ScenarioReactiveLoadPhaseVariableFactory;
import gov.lanl.micot.util.math.solver.LinearConstraint;
import gov.lanl.micot.util.math.solver.LinearConstraintEquals;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.InvalidConstraintException;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

import java.util.Collection;

/**
 * Balances the reactive phase flows at each node for each scenario
 * 
 * @author Russell Bent
 */
public class ScenarioReactivePhaseBalanceConstraint extends ScenarioConstraintFactory<ElectricPowerNode, ElectricPowerModel> {

  /**
   * Constraint
   */
  public ScenarioReactivePhaseBalanceConstraint(Collection<Scenario> scenarios) {
    super(scenarios);
  }

  /**
   * Get the constraint name
   * 
   * @param load
   * @param phase
   * @return
   */
  private String getConstraintName(ElectricPowerNode node, String phase, Scenario scenario) {
    return "Balance(q)-" + node + "_" + scenario + "." + phase;
  }

  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException, InvalidConstraintException {
    ScenarioGeneratorReactivePhaseVariableFactory generatorVariableFactory = new ScenarioGeneratorReactivePhaseVariableFactory(getScenarios());
    ScenarioReactiveLoadPhaseVariableFactory loadVariableFactory = new ScenarioReactiveLoadPhaseVariableFactory(getScenarios());
    ScenarioReactiveFlowPhaseVariableFactory flowVariableFactory = new ScenarioReactiveFlowPhaseVariableFactory(getScenarios());

    for (Scenario scenario : getScenarios()) {
      for (ElectricPowerNode node : model.getNodes()) {

        // Phase A
        LinearConstraint constraint = new LinearConstraintEquals(getConstraintName(node, ScenarioGeneratorReactivePhaseVariableFactory.PHASE_A, scenario));
        constraint.setRightHandSide(0);
        for (ElectricPowerFlowConnection edge : model.getFlowEdges(node)) {
          Variable variable = flowVariableFactory.getVariable(problem, edge, ScenarioReactiveFlowPhaseVariableFactory.PHASE_A, scenario);
          if (variable != null) {
            if (node.equals(model.getFirstNode(edge))) {
              constraint.addVariable(variable, -1.0);
            }
            else {
              constraint.addVariable(variable, 1.0);
            }
          }
        }

        for (Load load : node.getComponents(Load.class)) {
          Variable variable = loadVariableFactory.getVariable(problem, load, ScenarioReactiveLoadPhaseVariableFactory.PHASE_A, scenario);
          if (variable != null) {
            constraint.addVariable(variable, -1.0);
          }
        }

        for (Generator generator : node.getComponents(Generator.class)) {
          Variable variable = generatorVariableFactory.getVariable(problem, generator, ScenarioGeneratorReactivePhaseVariableFactory.PHASE_A, scenario);
          if (variable != null) {
            constraint.addVariable(variable, 1.0);
          }
        }
        if (constraint.getNumberOfVariables() > 0) {
          problem.addLinearConstraint(constraint);
        }

        // Phase B
        constraint = new LinearConstraintEquals(getConstraintName(node, ScenarioGeneratorReactivePhaseVariableFactory.PHASE_B, scenario));
        constraint.setRightHandSide(0);
        for (ElectricPowerFlowConnection edge : model.getFlowEdges(node)) {
          Variable variable = flowVariableFactory.getVariable(problem, edge, ScenarioReactiveFlowPhaseVariableFactory.PHASE_B, scenario);
          if (variable != null) {
            if (node.equals(model.getFirstNode(edge))) {
              constraint.addVariable(variable, -1.0);
            }
            else {
              constraint.addVariable(variable, 1.0);
            }
          }
        }

        for (Load load : node.getComponents(Load.class)) {
          Variable variable = loadVariableFactory.getVariable(problem, load, ScenarioReactiveLoadPhaseVariableFactory.PHASE_B, scenario);
          if (variable != null) {
            constraint.addVariable(variable, -1.0);
          }
        }

        for (Generator generator : node.getComponents(Generator.class)) {
          Variable variable = generatorVariableFactory.getVariable(problem, generator, ScenarioGeneratorReactivePhaseVariableFactory.PHASE_B, scenario);
          if (variable != null) {
            constraint.addVariable(variable, 1.0);
          }
        }
        if (constraint.getNumberOfVariables() > 0) {
          problem.addLinearConstraint(constraint);
        }

        // Phase C
        constraint = new LinearConstraintEquals(getConstraintName(node, ScenarioGeneratorReactivePhaseVariableFactory.PHASE_C, scenario));
        constraint.setRightHandSide(0);
        for (ElectricPowerFlowConnection edge : model.getFlowEdges(node)) {
          Variable variable = flowVariableFactory.getVariable(problem, edge, ScenarioReactiveFlowPhaseVariableFactory.PHASE_C, scenario);
          if (variable != null) {
            if (node.equals(model.getFirstNode(edge))) {
              constraint.addVariable(variable, -1.0);
            }
            else {
              constraint.addVariable(variable, 1.0);
            }
          }
        }

        for (Load load : node.getComponents(Load.class)) {
          Variable variable = loadVariableFactory.getVariable(problem, load, ScenarioReactiveLoadPhaseVariableFactory.PHASE_C, scenario);
          if (variable != null) {
            constraint.addVariable(variable, -1.0);
          }
        }

        for (Generator generator : node.getComponents(Generator.class)) {
          Variable variable = generatorVariableFactory.getVariable(problem, generator, ScenarioGeneratorReactivePhaseVariableFactory.PHASE_C, scenario);
          if (variable != null) {
            constraint.addVariable(variable, 1.0);
          }
        }

        if (constraint.getNumberOfVariables() > 0) {
          problem.addLinearConstraint(constraint);
        }

      }
    }
  }
  
}
