package gov.lanl.micot.application.rdt.algorithm.ep.assignment.scenario;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.Generator;
import gov.lanl.micot.infrastructure.ep.optimize.AssignmentFactory;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.model.ScenarioAttribute;
import gov.lanl.micot.application.rdt.algorithm.ep.variable.scenario.GeneratorVariableFactory;
import gov.lanl.micot.util.math.solver.Solution;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

/**
 * Assignments of generator outputs by phase
 * 
 * @author Russell Bent
 */
public class GeneratorAssignment implements AssignmentFactory {

  private Scenario scenario = null;
  
  /**
   * Constructor
   */
  public GeneratorAssignment(Scenario scenario) {
    this.scenario = scenario;
  }

  @Override
  public void performAssignment(ElectricPowerModel model, MathematicalProgram problem, Solution solution) throws VariableExistsException, NoVariableException {
    GeneratorVariableFactory generatorVariableFactory = new GeneratorVariableFactory(scenario);
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

      if (generator.getAttribute(Generator.REAL_GENERATION_A_KEY) == null || !(generator.getAttribute(Generator.REAL_GENERATION_A_KEY) instanceof ScenarioAttribute)) {
        generator.setAttribute(Generator.REAL_GENERATION_A_KEY, new ScenarioAttribute());
      }
      
      if (generator.getAttribute(Generator.REAL_GENERATION_B_KEY) == null || !(generator.getAttribute(Generator.REAL_GENERATION_B_KEY) instanceof ScenarioAttribute)) {         
        generator.setAttribute(Generator.REAL_GENERATION_B_KEY, new ScenarioAttribute());
      }
      
      if (generator.getAttribute(Generator.REAL_GENERATION_C_KEY) == null || !(generator.getAttribute(Generator.REAL_GENERATION_C_KEY) instanceof ScenarioAttribute)) {         
        generator.setAttribute(Generator.REAL_GENERATION_C_KEY, new ScenarioAttribute());
      }
      
      
      Variable gq_a = generatorVariableFactory.getReactiveVariable(problem, generator, GeneratorVariableFactory.PHASE_A);
      Variable gq_b = generatorVariableFactory.getReactiveVariable(problem, generator, GeneratorVariableFactory.PHASE_B);
      Variable gq_c = generatorVariableFactory.getReactiveVariable(problem, generator, GeneratorVariableFactory.PHASE_C);

      Variable gp_a = generatorVariableFactory.getRealVariable(problem, generator, GeneratorVariableFactory.PHASE_A);
      Variable gp_b = generatorVariableFactory.getRealVariable(problem, generator, GeneratorVariableFactory.PHASE_B);
      Variable gp_c = generatorVariableFactory.getRealVariable(problem, generator, GeneratorVariableFactory.PHASE_C);

      if (gq_a != null) {
        double gen = solution.getValueDouble(gq_a) * mvaBase;
        generator.getAttribute(Generator.REACTIVE_GENERATION_A_KEY, ScenarioAttribute.class).addEntry(scenario, gen);
      }

      if (gq_b != null) {
        double gen = solution.getValueDouble(gq_b) * mvaBase;
        generator.getAttribute(Generator.REACTIVE_GENERATION_B_KEY, ScenarioAttribute.class).addEntry(scenario, gen);
      }

      if (gq_c != null) {
        double gen = solution.getValueDouble(gq_c) * mvaBase;
        generator.getAttribute(Generator.REACTIVE_GENERATION_C_KEY, ScenarioAttribute.class).addEntry(scenario, gen);
      }
      
      if (gp_a != null) {
        double gen = solution.getValueDouble(gp_a) * mvaBase;
        generator.getAttribute(Generator.REAL_GENERATION_A_KEY, ScenarioAttribute.class).addEntry(scenario, gen);
      }

      if (gp_b != null) {
        double gen = solution.getValueDouble(gp_b) * mvaBase;
        generator.getAttribute(Generator.REAL_GENERATION_B_KEY, ScenarioAttribute.class).addEntry(scenario, gen);
      }

      if (gp_c != null) {
        double gen = solution.getValueDouble(gp_c) * mvaBase;
        generator.getAttribute(Generator.REAL_GENERATION_C_KEY, ScenarioAttribute.class).addEntry(scenario, gen);
      }
    }
  }
  
}
