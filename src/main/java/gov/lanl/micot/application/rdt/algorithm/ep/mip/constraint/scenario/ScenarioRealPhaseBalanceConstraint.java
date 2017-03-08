package gov.lanl.micot.application.rdt.algorithm.ep.mip.constraint.scenario;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.ep.model.Generator;
import gov.lanl.micot.infrastructure.ep.model.Load;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.optimize.mathprogram.constraint.ScenarioConstraintFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.variable.scenario.ScenarioRealFlowPhaseVariableFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.variable.scenario.ScenarioGeneratorRealPhaseVariableFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.variable.scenario.ScenarioRealLoadPhaseVariableFactory;
import gov.lanl.micot.util.math.solver.LinearConstraint;
import gov.lanl.micot.util.math.solver.LinearConstraintEquals;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.InvalidConstraintException;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

import java.util.Collection;

/**
 * Balances the real phase flows at each node for each scenario
 * 
 * Constraint 8 in the AAAI 2015 paper
 * 
 * @author Russell Bent
 */
public class ScenarioRealPhaseBalanceConstraint extends ScenarioConstraintFactory<ElectricPowerNode, ElectricPowerModel> {

  /**
   * Constraint
   */
  public ScenarioRealPhaseBalanceConstraint(Collection<Scenario> scenarios) {
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
    return "Balance(p)-" + node + "_" + scenario + "." + phase;
  }

  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException, InvalidConstraintException {
    ScenarioGeneratorRealPhaseVariableFactory generatorVariableFactory = new ScenarioGeneratorRealPhaseVariableFactory(getScenarios());
    ScenarioRealLoadPhaseVariableFactory loadVariableFactory = new ScenarioRealLoadPhaseVariableFactory(getScenarios());
    ScenarioRealFlowPhaseVariableFactory flowVariableFactory = new ScenarioRealFlowPhaseVariableFactory(getScenarios());

    for (Scenario scenario : getScenarios()) {
      for (ElectricPowerNode node : model.getNodes()) {

        // Phase A
        LinearConstraint constraint = new LinearConstraintEquals(getConstraintName(node, ScenarioGeneratorRealPhaseVariableFactory.PHASE_A, scenario));
        constraint.setRightHandSide(0);
        for (ElectricPowerFlowConnection edge : model.getFlowEdges(node)) {
          Variable variable = flowVariableFactory.getVariable(problem, edge, ScenarioRealFlowPhaseVariableFactory.PHASE_A, scenario);
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
          Variable variable = loadVariableFactory.getVariable(problem, load, ScenarioRealLoadPhaseVariableFactory.PHASE_A, scenario);
          if (variable != null) {
            constraint.addVariable(variable, -1.0);
          }
        }

        for (Generator generator : node.getComponents(Generator.class)) {
          Variable variable = generatorVariableFactory.getVariable(problem, generator, ScenarioGeneratorRealPhaseVariableFactory.PHASE_A, scenario);
          if (variable != null) {
            constraint.addVariable(variable, 1.0);
          }
        }
        if (constraint.getNumberOfVariables() > 0) {
          problem.addLinearConstraint(constraint);
        }

        // Phase B
        constraint = new LinearConstraintEquals(getConstraintName(node, ScenarioGeneratorRealPhaseVariableFactory.PHASE_B, scenario));
        constraint.setRightHandSide(0);
        for (ElectricPowerFlowConnection edge : model.getFlowEdges(node)) {
          Variable variable = flowVariableFactory.getVariable(problem, edge, ScenarioRealFlowPhaseVariableFactory.PHASE_B, scenario);
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
          Variable variable = loadVariableFactory.getVariable(problem, load, ScenarioRealLoadPhaseVariableFactory.PHASE_B, scenario);
          if (variable != null) {
            constraint.addVariable(variable, -1.0);
          }
        }

        for (Generator generator : node.getComponents(Generator.class)) {
          Variable variable = generatorVariableFactory.getVariable(problem, generator, ScenarioGeneratorRealPhaseVariableFactory.PHASE_B, scenario);
          if (variable != null) {
            constraint.addVariable(variable, 1.0);
          }
        }
        if (constraint.getNumberOfVariables() > 0) {
          problem.addLinearConstraint(constraint);
        }

        // Phase C
        constraint = new LinearConstraintEquals(getConstraintName(node, ScenarioGeneratorRealPhaseVariableFactory.PHASE_C, scenario));
        constraint.setRightHandSide(0);
        for (ElectricPowerFlowConnection edge : model.getFlowEdges(node)) {
          Variable variable = flowVariableFactory.getVariable(problem, edge, ScenarioRealFlowPhaseVariableFactory.PHASE_C, scenario);
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
          Variable variable = loadVariableFactory.getVariable(problem, load, ScenarioRealLoadPhaseVariableFactory.PHASE_C, scenario);
          if (variable != null) {
            constraint.addVariable(variable, -1.0);
          }
        }

        for (Generator generator : node.getComponents(Generator.class)) {
          Variable variable = generatorVariableFactory.getVariable(problem, generator, ScenarioGeneratorRealPhaseVariableFactory.PHASE_C, scenario);
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
