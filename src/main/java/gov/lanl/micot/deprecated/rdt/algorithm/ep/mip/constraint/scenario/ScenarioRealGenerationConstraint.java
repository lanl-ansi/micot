package gov.lanl.micot.deprecated.rdt.algorithm.ep.mip.constraint.scenario;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.ep.model.Generator;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.optimize.mathprogram.constraint.ScenarioConstraintFactory;
import gov.lanl.micot.application.rdt.algorithm.AlgorithmConstants;
import gov.lanl.micot.deprecated.rdt.algorithm.ep.mip.variable.scenario.ScenarioGeneratorRealPhaseVariableFactory;
import gov.lanl.micot.deprecated.rdt.algorithm.ep.mip.variable.scenario.ScenarioMicrogridCapacityVariableFactory;
import gov.lanl.micot.util.math.solver.LinearConstraint;
import gov.lanl.micot.util.math.solver.LinearConstraintGreaterEq;
import gov.lanl.micot.util.math.solver.LinearConstraintLessEq;
import gov.lanl.micot.util.math.solver.exception.InvalidConstraintException;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

import java.util.Collection;

/**
 * Real generation constraint is smaller than micro grid size and existing generation
 * 
 * This is constraint 7 in the AAAI 15 Paper
 * 
 * @author Russell Bent
 */
public class ScenarioRealGenerationConstraint extends ScenarioConstraintFactory<ElectricPowerNode, ElectricPowerModel> {

  /**
   * Constraint
   */
  public ScenarioRealGenerationConstraint(Collection<Scenario> scenarios) {
    super(scenarios);
  }

  /**
   * Get the constraint name
   * 
   * @param load
   * @param phase
   * @return
   */
  private String getConstraintLEName(Generator generator, String phase, Scenario scenario) {
    return "GenerationLE(p)-" + generator + "_" + scenario + "." + phase;
  }

  /**
   * Get the constraint name
   * 
   * @param load
   * @param phase
   * @return
   */
  private String getConstraintGEName(Generator generator, String phase, Scenario scenario) {
    return "GenerationGE(p)-" + generator + "_" + scenario + "." + phase;
  }

  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException, InvalidConstraintException {
    ScenarioMicrogridCapacityVariableFactory microgridVariableFactory = new ScenarioMicrogridCapacityVariableFactory(getScenarios());
    ScenarioGeneratorRealPhaseVariableFactory generatorVariableFactory = new ScenarioGeneratorRealPhaseVariableFactory(getScenarios());
    double mvaBase = model.getMVABase();

    for (Scenario scenario : getScenarios()) {
      for (ElectricPowerNode node : model.getNodes()) {
        for (Generator generator : node.getComponents(Generator.class)) {
          boolean hasCost = generator.getAttribute(AlgorithmConstants.MICROGRID_COST_KEY) == null ? false : true;
          if (!generator.getStatus() || !hasCost) {
            continue;
          }

          if (generator.getAttribute(Generator.HAS_PHASE_A_KEY, Boolean.class)) {
            LinearConstraint constraint = new LinearConstraintLessEq(getConstraintLEName(generator, ScenarioGeneratorRealPhaseVariableFactory.PHASE_A, scenario));
            constraint.addVariable(generatorVariableFactory.getVariable(problem, generator, ScenarioGeneratorRealPhaseVariableFactory.PHASE_A, scenario));
            constraint.setRightHandSide(microgridVariableFactory.getVariable(problem, generator, ScenarioMicrogridCapacityVariableFactory.PHASE_A, scenario), 1.0);
            constraint.setRightHandSide(generator.getRealGenerationMax() / mvaBase);
            problem.addLinearConstraint(constraint);

            constraint = new LinearConstraintGreaterEq(getConstraintGEName(generator, ScenarioGeneratorRealPhaseVariableFactory.PHASE_A, scenario));
            constraint.addVariable(generatorVariableFactory.getVariable(problem, generator, ScenarioGeneratorRealPhaseVariableFactory.PHASE_A, scenario));
            constraint.setRightHandSide(0.0);
            problem.addLinearConstraint(constraint);
          }

          if (generator.getAttribute(Generator.HAS_PHASE_B_KEY, Boolean.class)) {
            LinearConstraint constraint = new LinearConstraintLessEq(getConstraintLEName(generator, ScenarioGeneratorRealPhaseVariableFactory.PHASE_B, scenario));
            constraint.addVariable(generatorVariableFactory.getVariable(problem, generator, ScenarioGeneratorRealPhaseVariableFactory.PHASE_B, scenario));
            constraint.setRightHandSide(microgridVariableFactory.getVariable(problem, generator, ScenarioMicrogridCapacityVariableFactory.PHASE_B, scenario), 1.0);
            constraint.setRightHandSide(generator.getRealGenerationMax() / mvaBase);
            problem.addLinearConstraint(constraint);

            constraint = new LinearConstraintGreaterEq(getConstraintGEName(generator, ScenarioGeneratorRealPhaseVariableFactory.PHASE_B, scenario));
            constraint.addVariable(generatorVariableFactory.getVariable(problem, generator, ScenarioGeneratorRealPhaseVariableFactory.PHASE_B, scenario));
            constraint.setRightHandSide(0.0);
            problem.addLinearConstraint(constraint);
          }

          if (generator.getAttribute(Generator.HAS_PHASE_C_KEY, Boolean.class)) {
            LinearConstraint constraint = new LinearConstraintLessEq(getConstraintLEName(generator, ScenarioGeneratorRealPhaseVariableFactory.PHASE_C, scenario));
            constraint.addVariable(generatorVariableFactory.getVariable(problem, generator, ScenarioGeneratorRealPhaseVariableFactory.PHASE_C, scenario));
            constraint.setRightHandSide(microgridVariableFactory.getVariable(problem, generator, ScenarioMicrogridCapacityVariableFactory.PHASE_C, scenario), 1.0);
            constraint.setRightHandSide(generator.getRealGenerationMax() / mvaBase);
            problem.addLinearConstraint(constraint);

            constraint = new LinearConstraintGreaterEq(getConstraintGEName(generator, ScenarioGeneratorRealPhaseVariableFactory.PHASE_C, scenario));
            constraint.addVariable(generatorVariableFactory.getVariable(problem, generator, ScenarioGeneratorRealPhaseVariableFactory.PHASE_C, scenario));
            constraint.setRightHandSide(0.0);
            problem.addLinearConstraint(constraint);
          }
        }
      }
    }
  }
  
}
