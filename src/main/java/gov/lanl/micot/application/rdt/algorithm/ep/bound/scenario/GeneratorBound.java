package gov.lanl.micot.application.rdt.algorithm.ep.bound.scenario;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.Generator;
import gov.lanl.micot.infrastructure.ep.optimize.ConstraintFactory;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.application.rdt.algorithm.ep.variable.scenario.GeneratorVariableFactory;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

/**
 * Bounds on generators
 * 
 * @author Russell Bent
 */
public class GeneratorBound implements ConstraintFactory {

  private Scenario scenario = null;
  
  /**
   * Constraint
   */
  public GeneratorBound(Scenario scenario) {
    this.scenario = scenario;
  }

  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws NoVariableException {
    GeneratorVariableFactory variableFactory = new GeneratorVariableFactory(scenario);
    double mvaBase = model.getMVABase();
    for (Generator gen : model.getGenerators()) {
      if (!scenario.computeActualStatus(gen, true)) {
        continue;
      }
            
      if (gen.getAttribute(Generator.HAS_PHASE_A_KEY, Boolean.class)) {        
        problem.addBounds(variableFactory.getRealVariable(problem, gen, GeneratorVariableFactory.PHASE_A), 0.0, gen.getAttribute(Generator.REAL_GENERATION_MAX_KEY, Number.class).doubleValue() / mvaBase);
        problem.addBounds(variableFactory.getReactiveVariable(problem, gen, GeneratorVariableFactory.PHASE_A), 0.0, gen.getAttribute(Generator.REAL_GENERATION_MAX_KEY, Number.class).doubleValue() / mvaBase); 
      }
      
      if (gen.getAttribute(Generator.HAS_PHASE_B_KEY, Boolean.class)) {        
        problem.addBounds(variableFactory.getRealVariable(problem, gen, GeneratorVariableFactory.PHASE_B), 0.0, gen.getAttribute(Generator.REAL_GENERATION_MAX_KEY, Number.class).doubleValue() / mvaBase);
        problem.addBounds(variableFactory.getReactiveVariable(problem, gen, GeneratorVariableFactory.PHASE_B), 0.0, gen.getAttribute(Generator.REAL_GENERATION_MAX_KEY, Number.class).doubleValue() / mvaBase); 
      }

      if (gen.getAttribute(Generator.HAS_PHASE_C_KEY, Boolean.class)) {        
        problem.addBounds(variableFactory.getRealVariable(problem, gen, GeneratorVariableFactory.PHASE_C), 0.0, gen.getAttribute(Generator.REAL_GENERATION_MAX_KEY, Number.class).doubleValue() / mvaBase);
        problem.addBounds(variableFactory.getReactiveVariable(problem, gen, GeneratorVariableFactory.PHASE_C), 0.0, gen.getAttribute(Generator.REAL_GENERATION_MAX_KEY, Number.class).doubleValue() / mvaBase); 
      }
    }
  }
  
}
