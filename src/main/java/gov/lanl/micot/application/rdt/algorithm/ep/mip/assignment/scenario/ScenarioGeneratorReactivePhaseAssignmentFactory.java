package gov.lanl.micot.application.rdt.algorithm.ep.mip.assignment.scenario;

import java.util.Collection;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.ep.model.Generator;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.model.ScenarioAttribute;
import gov.lanl.micot.infrastructure.optimize.mathprogram.assignment.ScenarioAssignmentFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.variable.scenario.ScenarioGeneratorReactivePhaseVariableFactory;
import gov.lanl.micot.util.math.solver.Solution;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

/**
 * Assignments of reactive generator phase
 * 
 * @author Russell Bent
 */
public class ScenarioGeneratorReactivePhaseAssignmentFactory extends ScenarioAssignmentFactory<ElectricPowerNode, ElectricPowerModel> {

  /**
   * Constructor
   */
  public ScenarioGeneratorReactivePhaseAssignmentFactory(Collection<Scenario> scenarios) {
    super(scenarios);
  }

  @Override
  public void performAssignment(ElectricPowerModel model, MathematicalProgram problem, Solution solution) throws VariableExistsException, NoVariableException {
    ScenarioGeneratorReactivePhaseVariableFactory generatorVariableFactory = new ScenarioGeneratorReactivePhaseVariableFactory(getScenarios());
    double mvaBase = model.getMVABase();

    for (Generator generator : model.getGenerators()) {
      
      if (generator.getAttribute(Generator.REACTIVE_GENERATION_A_KEY) == null || !(generator.getAttribute(Generator.REACTIVE_GENERATION_A_KEY) instanceof ScenarioAttribute)) {
        generator.setAttribute(Generator.REACTIVE_GENERATION_A_KEY, new ScenarioAttribute());
      }
      
      if (generator.getAttribute(Generator.REACTIVE_GENERATION_B_KEY) == null || !(generator.getAttribute(Generator.REACTIVE_GENERATION_B_KEY) instanceof ScenarioAttribute)) {         
        generator.setAttribute(Generator.REACTIVE_GENERATION_B_KEY, new ScenarioAttribute());
      }
      
      if (generator.getAttribute(Generator.REACTIVE_GENERATION_C_KEY) == null || !(generator.getAttribute(Generator.REACTIVE_GENERATION_C_KEY) instanceof ScenarioAttribute)) {         
        generator.setAttribute(Generator.REACTIVE_GENERATION_C_KEY, new ScenarioAttribute());
      }

      for (Scenario scenario : getScenarios()) {
        Variable variable = generatorVariableFactory.getVariable(problem, generator, ScenarioGeneratorReactivePhaseVariableFactory.PHASE_A, scenario);
        if (variable != null) {
          double gen = solution.getValueDouble(variable) * mvaBase;
          generator.getAttribute(Generator.REACTIVE_GENERATION_A_KEY, ScenarioAttribute.class).addEntry(scenario, gen);
        }

        variable = generatorVariableFactory.getVariable(problem, generator, ScenarioGeneratorReactivePhaseVariableFactory.PHASE_B, scenario);
        if (variable != null) {
          double gen = solution.getValueDouble(variable) * mvaBase;
          generator.getAttribute(Generator.REACTIVE_GENERATION_B_KEY, ScenarioAttribute.class).addEntry(scenario, gen);
        }

        variable = generatorVariableFactory.getVariable(problem, generator, ScenarioGeneratorReactivePhaseVariableFactory.PHASE_C, scenario);
        if (variable != null) {
          double gen = solution.getValueDouble(variable) * mvaBase;
          generator.getAttribute(Generator.REACTIVE_GENERATION_C_KEY, ScenarioAttribute.class).addEntry(scenario, gen);
        }
      }
    }
  }
  
}
