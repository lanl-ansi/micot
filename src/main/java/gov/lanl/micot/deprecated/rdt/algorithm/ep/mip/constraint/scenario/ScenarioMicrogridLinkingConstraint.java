package gov.lanl.micot.deprecated.rdt.algorithm.ep.mip.constraint.scenario;

import java.util.Collection;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.ep.model.Generator;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.optimize.mathprogram.constraint.ScenarioConstraintFactory;
import gov.lanl.micot.application.rdt.algorithm.AlgorithmConstants;
import gov.lanl.micot.deprecated.rdt.algorithm.ep.mip.variable.MicrogridVariableFactory;
import gov.lanl.micot.deprecated.rdt.algorithm.ep.mip.variable.scenario.ScenarioMicrogridVariableFactory;
import gov.lanl.micot.util.math.solver.LinearConstraint;
import gov.lanl.micot.util.math.solver.LinearConstraintLessEq;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.InvalidConstraintException;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

/**
 * Links first stage variables with second stage variables
 * 
 * This is constraint 23 is in AAAI paper
 * 
 * @author Russell Bent
 */
public class ScenarioMicrogridLinkingConstraint extends ScenarioConstraintFactory<ElectricPowerNode, ElectricPowerModel> {

  /**
   * Constructor
   * 
   * @param scenarios
   */
  public ScenarioMicrogridLinkingConstraint(Collection<Scenario> scenarios) {
    super(scenarios);
  }

  /**
   * Get the constraint name
   * @param edge
   * @param scenario
   * @return
   */
  private String getConstraintName(Generator generator, Scenario scenario) {
    return "MicrogridLinking-" + generator.toString() + "." + scenario.getIndex();
  }
  
  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException, InvalidConstraintException {
    ScenarioMicrogridVariableFactory lineVariableFactory = new ScenarioMicrogridVariableFactory(getScenarios());
    MicrogridVariableFactory factory = new MicrogridVariableFactory();

    for (Scenario scenario : getScenarios()) {
      for (Generator generator : model.getGenerators()) {
        Variable svariable = lineVariableFactory.getVariable(problem, generator, scenario);
        Variable fvariable = factory.getVariable(problem, generator);
        if (svariable != null) {
          LinearConstraint constraint = new LinearConstraintLessEq(getConstraintName(generator,scenario));
          constraint.addVariable(svariable, 1.0);
          boolean isNew = generator.getAttribute(AlgorithmConstants.IS_NEW_MICROGRID_KEY) != null && generator.getAttribute(AlgorithmConstants.IS_NEW_MICROGRID_KEY, Boolean.class);                
          if (isNew) {
            constraint.addVariable(fvariable, -1.0);
          }
          
          constraint.setRightHandSide(0.0);
          problem.addLinearConstraint(constraint);
        }
      }
    }
  }

}
