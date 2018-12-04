package gov.lanl.micot.deprecated.rdt.algorithm.ep.mip.constraint.scenario;

import java.util.Collection;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.ep.model.Generator;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.optimize.mathprogram.constraint.ScenarioConstraintFactory;
import gov.lanl.micot.application.rdt.algorithm.AlgorithmConstants;
import gov.lanl.micot.deprecated.rdt.algorithm.ep.mip.variable.MicrogridCapacityVariableFactory;
import gov.lanl.micot.deprecated.rdt.algorithm.ep.mip.variable.scenario.ScenarioMicrogridCapacityVariableFactory;
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
 * This is constraint 22 in the AAAI 2015 paper
 * 
 * @author Russell Bent
 */
public class ScenarioMicrogridCapacityLinkingConstraint extends ScenarioConstraintFactory<ElectricPowerNode, ElectricPowerModel> {

  /**
   * Constructor
   * 
   * @param scenarios
   */
  public ScenarioMicrogridCapacityLinkingConstraint(Collection<Scenario> scenarios) {
    super(scenarios);
  }

  /**
   * Get the constraint name
   * @param edge
   * @param scenario
   * @return
   */
  private String getConstraintName(Generator generator, String phase, Scenario scenario) {
    return "MicrogridLinking-" + generator.toString() + "-" + phase + "." + scenario.getIndex();
  }
  
  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException, InvalidConstraintException {
    ScenarioMicrogridCapacityVariableFactory microgridVariableFactory = new ScenarioMicrogridCapacityVariableFactory(getScenarios());
    MicrogridCapacityVariableFactory factory = new MicrogridCapacityVariableFactory();

    for (Scenario scenario : getScenarios()) {
      for (Generator generator : model.getGenerators()) {
        Variable sAvariable = microgridVariableFactory.getVariable(problem, generator,  ScenarioMicrogridCapacityVariableFactory.PHASE_A,scenario);
        Variable sBvariable = microgridVariableFactory.getVariable(problem, generator,  ScenarioMicrogridCapacityVariableFactory.PHASE_B,scenario);
        Variable sCvariable = microgridVariableFactory.getVariable(problem, generator,  ScenarioMicrogridCapacityVariableFactory.PHASE_C,scenario);
        Variable fAvariable = factory.getVariable(problem, generator,ScenarioMicrogridCapacityVariableFactory.PHASE_A);
        Variable fBvariable = factory.getVariable(problem, generator,ScenarioMicrogridCapacityVariableFactory.PHASE_B);
        Variable fCvariable = factory.getVariable(problem, generator,ScenarioMicrogridCapacityVariableFactory.PHASE_C);
        
        boolean isNew = generator.getAttribute(AlgorithmConstants.IS_NEW_MICROGRID_KEY) != null && generator.getAttribute(AlgorithmConstants.IS_NEW_MICROGRID_KEY, Boolean.class);                 
        if (!isNew) {
          continue;
        }
        

        if (sAvariable != null) {
          LinearConstraint constraint = new LinearConstraintLessEq(getConstraintName(generator, ScenarioMicrogridCapacityVariableFactory.PHASE_A, scenario));
          constraint.addVariable(sAvariable, 1.0);
          constraint.addVariable(fAvariable, -1.0);
          constraint.setRightHandSide(0.0);
          problem.addLinearConstraint(constraint);
        }
        
        if (sBvariable != null) {
          LinearConstraint constraint = new LinearConstraintLessEq(getConstraintName(generator, ScenarioMicrogridCapacityVariableFactory.PHASE_B, scenario));
          constraint.addVariable(sBvariable, 1.0);
          constraint.addVariable(fBvariable, -1.0);
          constraint.setRightHandSide(0.0);
          problem.addLinearConstraint(constraint);
        }
        
        if (sCvariable != null) {
          LinearConstraint constraint = new LinearConstraintLessEq(getConstraintName(generator, ScenarioMicrogridCapacityVariableFactory.PHASE_C, scenario));
          constraint.addVariable(sCvariable, 1.0);
          constraint.addVariable(fCvariable, -1.0);
          constraint.setRightHandSide(0.0);
          problem.addLinearConstraint(constraint);
        }


      }
    }
  }

}
