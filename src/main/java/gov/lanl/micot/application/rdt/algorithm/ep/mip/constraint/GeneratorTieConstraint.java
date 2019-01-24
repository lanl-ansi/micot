package gov.lanl.micot.application.rdt.algorithm.ep.mip.constraint;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.Generator;
import gov.lanl.micot.infrastructure.ep.optimize.ConstraintFactory;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.application.rdt.algorithm.ep.variable.GeneratorConstructionVariableFactory;
import gov.lanl.micot.util.math.solver.LinearConstraint;
import gov.lanl.micot.util.math.solver.LinearConstraintEquals;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.InvalidConstraintException;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;


/**
 * This constraint ties the outer problem generator variable with the inner problem generator variable
 * 
 * u = u_s
 * 
 * @author Russell Bent
 */
public class GeneratorTieConstraint implements ConstraintFactory {

  private Scenario scenario = null;

  /**
   * Constraint
   */
  public GeneratorTieConstraint(Scenario scenario) {
    this.scenario = scenario;
  }
  
  /**
   * Get the constraint name
   * @param generator
   * @param scenario
   * @return
   */
  private String getConstraintName(Generator generator, Scenario scenario) {
    return "Generator Tie-" + generator + "." + scenario;
  }
  
  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException, InvalidConstraintException {
    gov.lanl.micot.application.rdt.algorithm.ep.variable.scenario.GeneratorConstructionVariableFactory scenarioFactory = new gov.lanl.micot.application.rdt.algorithm.ep.variable.scenario.GeneratorConstructionVariableFactory(scenario,null);
    GeneratorConstructionVariableFactory genFactory = new GeneratorConstructionVariableFactory();
        
    for (Generator generator : model.getGenerators()) {
      if (genFactory.hasVariable(generator)) {
        Variable u = genFactory.getVariable(problem, generator);
        Variable u_s = scenarioFactory.getVariable(problem, generator, scenario);        
        
        LinearConstraint constraint = new LinearConstraintEquals(getConstraintName(generator, scenario));
        constraint.addVariable(u, 1.0);
        constraint.addVariable(u_s, -1.0);
        constraint.setRightHandSide(0.0);
        
        problem.addLinearConstraint(constraint);
      }
    }    
  }

  
  
}
