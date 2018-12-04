package gov.lanl.micot.deprecated.rdt.algorithm.ep.mip.constraint.scenario;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.ep.model.Transformer;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.optimize.mathprogram.constraint.ScenarioConstraintFactory;
import gov.lanl.micot.deprecated.rdt.algorithm.ep.mip.variable.scenario.ScenarioReactiveFlowPhaseVariableFactory;
import gov.lanl.micot.util.math.solver.LinearConstraint;
import gov.lanl.micot.util.math.solver.LinearConstraintGreaterEq;
import gov.lanl.micot.util.math.solver.LinearConstraintLessEq;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.InvalidConstraintException;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

import java.util.Collection;

/**
 * Limit the reactive power variations between phase on a line to be small than some constant
 * 
 * 
 * @author Russell Bent
 */
public class ScenarioPhaseReactiveFlowVariationConstraint extends ScenarioConstraintFactory<ElectricPowerNode, ElectricPowerModel> {

  private double threshold = 0;

  /**
   * Constraint
   */
  public ScenarioPhaseReactiveFlowVariationConstraint(double threshold, Collection<Scenario> scenarios) {
    super(scenarios);
    this.threshold = threshold;
  }

  /**
   * Get the flow constraint name
   * 
   * @param edge
   * @return
   */
  private String getFlowLessThanConstraintName(ElectricPowerFlowConnection edge, String phase, Scenario scenario) {
    return "FlowPhaseL(q)" + edge.toString() + "_" + scenario + "." + phase + "";
  }

  /**
   * Get the flow constraint name
   * 
   * @param edge
   * @return
   */
  private String getFlowGreaterThanConstraintName(ElectricPowerFlowConnection edge, String phase, Scenario scenario) {
    return "FlowSwitchG(q)" + edge.toString() + "_" + scenario + "." + phase + "";
  }

  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException, InvalidConstraintException {
    ScenarioReactiveFlowPhaseVariableFactory flowFactory = new ScenarioReactiveFlowPhaseVariableFactory(getScenarios());

    for (Scenario scenario : getScenarios()) {
      for (Transformer link : model.getTransformers()) {
        double numberOfPhases = (double) link.getAttribute(ElectricPowerFlowConnection.NUMBER_OF_PHASES_KEY, Integer.class);
        if (numberOfPhases <= 1) {
          continue;
        }

        Variable variableA = flowFactory.getVariable(problem, link, ScenarioReactiveFlowPhaseVariableFactory.PHASE_A, scenario);
        Variable variableB = flowFactory.getVariable(problem, link, ScenarioReactiveFlowPhaseVariableFactory.PHASE_B, scenario);
        Variable variableC = flowFactory.getVariable(problem, link, ScenarioReactiveFlowPhaseVariableFactory.PHASE_C, scenario);

        // phaseA
        if (variableA != null) {
          // <=
          LinearConstraint constraint = new LinearConstraintLessEq(getFlowLessThanConstraintName(link, ScenarioReactiveFlowPhaseVariableFactory.PHASE_A, scenario));
          constraint.setRightHandSide(0);
          constraint.sumVariable(variableA, 1.0);
          constraint.sumVariable(variableA, -1.0 / numberOfPhases);
          constraint.sumVariable(variableA, -threshold / numberOfPhases);

          if (variableB != null) {
            constraint.sumVariable(variableB, -1.0 / numberOfPhases);
            constraint.sumVariable(variableB, -threshold / numberOfPhases);
          }

          if (variableC != null) {
            constraint.sumVariable(variableC, -1.0 / numberOfPhases);
            constraint.sumVariable(variableC, -threshold / numberOfPhases);
          }
          problem.addLinearConstraint(constraint);

          // >=
          constraint = new LinearConstraintGreaterEq(getFlowGreaterThanConstraintName(link, ScenarioReactiveFlowPhaseVariableFactory.PHASE_A, scenario));
          constraint.setRightHandSide(0);
          constraint.sumVariable(variableA, 1.0);
          constraint.sumVariable(variableA, -1.0 / numberOfPhases);
          constraint.sumVariable(variableA, threshold / numberOfPhases);

          if (variableB != null) {
            constraint.sumVariable(variableB, -1.0 / numberOfPhases);
            constraint.sumVariable(variableB, threshold / numberOfPhases);
          }

          if (variableC != null) {
            constraint.sumVariable(variableC, -1.0 / numberOfPhases);
            constraint.sumVariable(variableC, threshold / numberOfPhases);
          }
          problem.addLinearConstraint(constraint);
        }

        // phaseB
        if (variableB != null) {
          // <=
          LinearConstraint constraint = new LinearConstraintLessEq(getFlowLessThanConstraintName(link, ScenarioReactiveFlowPhaseVariableFactory.PHASE_B, scenario));
          constraint.setRightHandSide(0);
          constraint.sumVariable(variableB, 1.0);
          constraint.sumVariable(variableB, -1.0 / numberOfPhases);
          constraint.sumVariable(variableB, -threshold / numberOfPhases);

          if (variableA != null) {
            constraint.sumVariable(variableA, -1.0 / numberOfPhases);
            constraint.sumVariable(variableA, -threshold / numberOfPhases);
          }

          if (variableC != null) {
            constraint.sumVariable(variableC, -1.0 / numberOfPhases);
            constraint.sumVariable(variableC, -threshold / numberOfPhases);
          }
          problem.addLinearConstraint(constraint);

          // >=
          constraint = new LinearConstraintGreaterEq(getFlowGreaterThanConstraintName(link, ScenarioReactiveFlowPhaseVariableFactory.PHASE_B, scenario));
          constraint.setRightHandSide(0);
          constraint.sumVariable(variableB, 1.0);
          constraint.sumVariable(variableB, -1.0 / numberOfPhases);
          constraint.sumVariable(variableB, threshold / numberOfPhases);

          if (variableA != null) {
            constraint.sumVariable(variableA, -1.0 / numberOfPhases);
            constraint.sumVariable(variableA, threshold / numberOfPhases);
          }

          if (variableC != null) {
            constraint.sumVariable(variableC, -1.0 / numberOfPhases);
            constraint.sumVariable(variableC, threshold / numberOfPhases);
          }
          problem.addLinearConstraint(constraint);
        }

        // phaseC
        if (variableC != null) {
          // <=
          LinearConstraint constraint = new LinearConstraintLessEq(getFlowLessThanConstraintName(link, ScenarioReactiveFlowPhaseVariableFactory.PHASE_C, scenario));
          constraint.setRightHandSide(0);
          constraint.sumVariable(variableC, 1.0);
          constraint.sumVariable(variableC, -1.0 / numberOfPhases);
          constraint.sumVariable(variableC, -threshold / numberOfPhases);

          if (variableB != null) {
            constraint.sumVariable(variableB, -1.0 / numberOfPhases);
            constraint.sumVariable(variableB, -threshold / numberOfPhases);
          }

          if (variableA != null) {
            constraint.sumVariable(variableA, -1.0 / numberOfPhases);
            constraint.sumVariable(variableA, -threshold / numberOfPhases);
          }
          problem.addLinearConstraint(constraint);

          // >=
          constraint = new LinearConstraintGreaterEq(getFlowGreaterThanConstraintName(link, ScenarioReactiveFlowPhaseVariableFactory.PHASE_C, scenario));
          constraint.setRightHandSide(0);
          constraint.sumVariable(variableC, 1.0);
          constraint.sumVariable(variableC, -1.0 / numberOfPhases);
          constraint.sumVariable(variableC, threshold / numberOfPhases);

          if (variableB != null) {
            constraint.sumVariable(variableB, -1.0 / numberOfPhases);
            constraint.sumVariable(variableB, threshold / numberOfPhases);
          }

          if (variableA != null) {
            constraint.sumVariable(variableA, -1.0 / numberOfPhases);
            constraint.sumVariable(variableA, threshold / numberOfPhases);
          }
          problem.addLinearConstraint(constraint);
        }

      }
    }
  }

  
}
