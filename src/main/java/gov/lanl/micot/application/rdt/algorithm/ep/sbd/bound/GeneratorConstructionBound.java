package gov.lanl.micot.application.rdt.algorithm.ep.sbd.bound;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.Generator;
import gov.lanl.micot.infrastructure.ep.optimize.ConstraintFactory;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.application.rdt.algorithm.ep.variable.scenario.GeneratorConstructionVariableFactory;
import gov.lanl.micot.util.math.solver.Solution;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;


/**
 * Bounds on the generator scenario variables
 * 
 * @author Russell Bent
 */
public class GeneratorConstructionBound implements ConstraintFactory {

  private Scenario scenario = null;
  private Solution values = null;
  private MathematicalProgram outerProblem = null;


  /**
   * Constraint
   */
  public GeneratorConstructionBound(Scenario scenario, Solution values,  MathematicalProgram outerProblem) {    
    this.scenario = scenario;
    this.values = values;
    this.outerProblem = outerProblem;

  }
    
  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException {
    GeneratorConstructionVariableFactory generatorVariableFactory = new GeneratorConstructionVariableFactory(scenario,null);
    gov.lanl.micot.application.rdt.algorithm.ep.variable.GeneratorConstructionVariableFactory outerFactory = new gov.lanl.micot.application.rdt.algorithm.ep.variable.GeneratorConstructionVariableFactory();
        
    for (Generator generator : model.getGenerators()) {
      if (generatorVariableFactory.hasVariable(generator)) {
        Variable u_s = generatorVariableFactory.getVariable(problem, generator, scenario);
        Variable u = outerFactory.getVariable(outerProblem, generator);                
        problem.addBounds(u_s, values.getValueInt(u), values.getValueInt(u));        
      }
    }    
  }

}
