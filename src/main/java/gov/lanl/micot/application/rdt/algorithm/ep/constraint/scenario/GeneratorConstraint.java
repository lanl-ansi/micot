package gov.lanl.micot.application.rdt.algorithm.ep.constraint.scenario;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.Generator;
import gov.lanl.micot.infrastructure.ep.optimize.ConstraintFactory;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.application.rdt.algorithm.ep.variable.scenario.GeneratorConstructionVariableFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.variable.scenario.GeneratorVariableFactory;
import gov.lanl.micot.util.math.solver.LinearConstraintLessEq;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.InvalidConstraintException;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;


/**
 * On/off generator constraints
 * 
 * @author Russell Bent
 */
public class GeneratorConstraint implements ConstraintFactory {

  private Scenario scenario = null;

  /**
   * Constraint
   */
  public GeneratorConstraint(Scenario scenario) {    
    this.scenario = scenario;
  }
  
  /**
   * Get the real constraint name
   * 
   * @param load
   * @param phase
   * @return
   */
  private String getRealName(Generator generator, String phase) {
    return "Generator Real Capacity Constraint-" + generator + "_" + scenario + "." + phase;
  }

  /**
   * Get the reactive constraint name
   * 
   * @param load
   * @param phase
   * @return
   */
  private String getReactiveName(Generator generator, String phase) {
    return "Generator Reactive Capacity Constraint-" + generator + "_" + scenario + "." + phase;
  }
  
  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException, InvalidConstraintException {
    GeneratorConstructionVariableFactory constructionFactory = new GeneratorConstructionVariableFactory(scenario);
    GeneratorVariableFactory genFactory = new GeneratorVariableFactory(scenario);
    double mvaBase = model.getMVABase();
    
    for (Generator generator : model.getGenerators()) {
      if (constructionFactory.hasVariable(generator)) {
        Variable u_s = constructionFactory.getVariable(problem, generator, scenario);

        Variable gp_a = genFactory.getRealVariable(problem, generator, GeneratorVariableFactory.PHASE_A);
        Variable gp_b = genFactory.getRealVariable(problem, generator, GeneratorVariableFactory.PHASE_B);
        Variable gp_c = genFactory.getRealVariable(problem, generator, GeneratorVariableFactory.PHASE_C);

        Variable gq_a = genFactory.getReactiveVariable(problem, generator, GeneratorVariableFactory.PHASE_A);
        Variable gq_b = genFactory.getReactiveVariable(problem, generator, GeneratorVariableFactory.PHASE_B);
        Variable gq_c = genFactory.getReactiveVariable(problem, generator, GeneratorVariableFactory.PHASE_C);
            
        LinearConstraintLessEq constraint = new LinearConstraintLessEq(getRealName(generator, GeneratorVariableFactory.PHASE_A));
        constraint.addVariable(gp_a, 1.0);
        constraint.addVariable(u_s,-generator.getRealGenerationMax() / mvaBase);
        constraint.setRightHandSide(0.0);
        problem.addLinearConstraint(constraint);
        
        constraint = new LinearConstraintLessEq(getRealName(generator, GeneratorVariableFactory.PHASE_B));
        constraint.addVariable(gp_b, 1.0);
        constraint.addVariable(u_s,-generator.getRealGenerationMax() / mvaBase);
        constraint.setRightHandSide(0.0);
        problem.addLinearConstraint(constraint);
        
        constraint = new LinearConstraintLessEq(getRealName(generator, GeneratorVariableFactory.PHASE_C));
        constraint.addVariable(gp_c, 1.0);
        constraint.addVariable(u_s,-generator.getRealGenerationMax() / mvaBase);
        constraint.setRightHandSide(0.0);
        problem.addLinearConstraint(constraint);
        
        constraint = new LinearConstraintLessEq(getReactiveName(generator, GeneratorVariableFactory.PHASE_A));
        constraint.addVariable(gq_a, 1.0);
        constraint.addVariable(u_s,-generator.getReactiveGenerationMax() / mvaBase);
        constraint.setRightHandSide(0.0);
        problem.addLinearConstraint(constraint);

        constraint = new LinearConstraintLessEq(getReactiveName(generator, GeneratorVariableFactory.PHASE_B));
        constraint.addVariable(gq_b, 1.0);
        constraint.addVariable(u_s,-generator.getReactiveGenerationMax() / mvaBase);
        constraint.setRightHandSide(0.0);
        problem.addLinearConstraint(constraint);

        constraint = new LinearConstraintLessEq(getReactiveName(generator, GeneratorVariableFactory.PHASE_C));
        constraint.addVariable(gq_c, 1.0);
        constraint.addVariable(u_s,-generator.getReactiveGenerationMax() / mvaBase);
        constraint.setRightHandSide(0.0);
        problem.addLinearConstraint(constraint);
      }
    }    
  }

}
