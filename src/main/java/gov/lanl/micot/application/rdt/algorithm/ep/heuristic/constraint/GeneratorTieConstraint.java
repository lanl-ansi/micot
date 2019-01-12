package gov.lanl.micot.application.rdt.algorithm.ep.heuristic.constraint;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.Generator;
import gov.lanl.micot.infrastructure.ep.optimize.ConstraintFactory;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.application.rdt.algorithm.ep.variable.GeneratorConstructionVariableFactory;
import gov.lanl.micot.util.math.solver.LinearConstraint;
import gov.lanl.micot.util.math.solver.LinearConstraintGreaterEq;
import gov.lanl.micot.util.math.solver.Solution;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.InvalidConstraintException;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

/**
 * This constraint ties the outer problem solution to an inner problem solution
 * 
 * u >= u
 * 
 * @author Russell Bent
 */
public class GeneratorTieConstraint implements ConstraintFactory {

  private Scenario scenario = null;
  private Solution solution = null;

  /**
   * Constraint
   */
  public GeneratorTieConstraint(Scenario scenario, Solution solution) {
    this.scenario = scenario;
    this.solution = solution;
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
    GeneratorConstructionVariableFactory genFactory = new GeneratorConstructionVariableFactory();
        
    for (Generator generator : model.getGenerators()) {
      if (genFactory.hasVariable(generator)) {
        Variable u = genFactory.getVariable(problem, generator);
        
        LinearConstraint constraint = new LinearConstraintGreaterEq(getConstraintName(generator, scenario));
        constraint.addVariable(u, 1.0);
        constraint.setRightHandSide(solution.getValue(u).doubleValue());
       
        problem.addLinearConstraint(constraint);
      }
    }    
  }

  
  
}
