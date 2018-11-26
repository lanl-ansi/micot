package gov.lanl.micot.application.rdt.algorithm.ep.bound.scenario;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.Generator;
import gov.lanl.micot.infrastructure.ep.optimize.ConstraintFactory;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.application.rdt.algorithm.ep.variable.scenario.GeneratorVariableFactory;
import gov.lanl.micot.util.math.solver.Variable;
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
        Variable gp_a = variableFactory.getRealVariable(problem, gen, GeneratorVariableFactory.PHASE_A);
        Variable gq_a = variableFactory.getReactiveVariable(problem, gen, GeneratorVariableFactory.PHASE_A);        
        problem.addBounds(gp_a, 0.0, gen.getRealGenerationMax() / mvaBase);
        problem.addBounds(gq_a, 0.0, gen.getReactiveGenerationMax() / mvaBase); 
      }
      
      if (gen.getAttribute(Generator.HAS_PHASE_B_KEY, Boolean.class)) {        
        Variable gp_b = variableFactory.getRealVariable(problem, gen, GeneratorVariableFactory.PHASE_B);
        Variable gq_b = variableFactory.getReactiveVariable(problem, gen, GeneratorVariableFactory.PHASE_B);                
        problem.addBounds(gp_b, 0.0, gen.getRealGenerationMax() / mvaBase);
        problem.addBounds(gq_b, 0.0, gen.getReactiveGenerationMax() / mvaBase); 
      }

      if (gen.getAttribute(Generator.HAS_PHASE_C_KEY, Boolean.class)) {        
        Variable gp_c = variableFactory.getRealVariable(problem, gen, GeneratorVariableFactory.PHASE_C);
        Variable gq_c = variableFactory.getReactiveVariable(problem, gen, GeneratorVariableFactory.PHASE_C);                        
        problem.addBounds(gp_c, 0.0, gen.getRealGenerationMax() / mvaBase);
        problem.addBounds(gq_c, 0.0, gen.getReactiveGenerationMax() / mvaBase); 
      }
    }
  }
  
}
