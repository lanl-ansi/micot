package gov.lanl.micot.deprecated.rdt.algorithm.ep.mip.constraint.scenario;

import java.util.Collection;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.ep.model.Generator;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.optimize.mathprogram.constraint.ScenarioConstraintFactory;
import gov.lanl.micot.application.rdt.algorithm.AlgorithmConstants;
import gov.lanl.micot.deprecated.rdt.algorithm.ep.mip.variable.MicrogridVariableFactory;
import gov.lanl.micot.deprecated.rdt.algorithm.ep.mip.variable.scenario.ScenarioMicrogridCapacityVariableFactory;
import gov.lanl.micot.util.math.solver.LinearConstraint;
import gov.lanl.micot.util.math.solver.LinearConstraintLessEq;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.InvalidConstraintException;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

/**
 * Limits the scenario microgrid capacity by the unit commitment cost
 * 
 * This is constraint 9 in the AAAI 2015 paper
 * 
 * 
 * @author Russell Bent
 */
public class ScenarioMicrogridCapacityConstraint extends ScenarioConstraintFactory<ElectricPowerNode, ElectricPowerModel> {

  /**
   * Constructor
   * 
   * @param scenarios
   */
  public ScenarioMicrogridCapacityConstraint(Collection<Scenario> scenarios) {
    super(scenarios);
  }

  /**
   * Get the constraint name
   * @param edge
   * @param scenario
   * @return
   */
  private String getConstraintName(Generator generator, String phase, Scenario scenario) {
    return "MicrogridScenarioCapacity-" + generator.toString() + "-" + phase + "." + scenario.getIndex();
  }
  
  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException, InvalidConstraintException {
    ScenarioMicrogridCapacityVariableFactory variableFactory = new ScenarioMicrogridCapacityVariableFactory(getScenarios());
    MicrogridVariableFactory mfactory = new MicrogridVariableFactory();
    double mvaBase = model.getMVABase();

    for (Scenario scenario : getScenarios()) {
      for (Generator generator : model.getGenerators()) {
        double upperBound = 10e6;
        if (generator.getAttribute(AlgorithmConstants.MAX_MICROGRID_KEY) != null) {
          upperBound = generator.getAttribute(AlgorithmConstants.MAX_MICROGRID_KEY, Number.class).doubleValue() / mvaBase;
        }
                
        Variable sAvariable = variableFactory.getVariable(problem, generator,  ScenarioMicrogridCapacityVariableFactory.PHASE_A,scenario);
        Variable sBvariable = variableFactory.getVariable(problem, generator,  ScenarioMicrogridCapacityVariableFactory.PHASE_B,scenario);
        Variable sCvariable = variableFactory.getVariable(problem, generator,  ScenarioMicrogridCapacityVariableFactory.PHASE_C,scenario);
        Variable discrete = mfactory.getVariable(problem, generator);
        boolean isNew = generator.getAttribute(AlgorithmConstants.IS_NEW_MICROGRID_KEY) != null && generator.getAttribute(AlgorithmConstants.IS_NEW_MICROGRID_KEY, Boolean.class);                

        if (sAvariable != null) {
          problem.addBounds(sAvariable,0,upperBound);          
          LinearConstraint constraint = new LinearConstraintLessEq(getConstraintName(generator, ScenarioMicrogridCapacityVariableFactory.PHASE_A, scenario));
          constraint.addVariable(sAvariable, 1.0);
          if (isNew) {
            constraint.setRightHandSide(discrete, upperBound);
          }
          problem.addLinearConstraint(constraint);
        }
        
        if (sBvariable != null) {
          problem.addBounds(sBvariable,0,upperBound);

          LinearConstraint constraint = new LinearConstraintLessEq(getConstraintName(generator, ScenarioMicrogridCapacityVariableFactory.PHASE_B, scenario));
          constraint.addVariable(sBvariable, 1.0);
          if (isNew) {
            constraint.setRightHandSide(discrete, upperBound);
          }
          problem.addLinearConstraint(constraint);
        }
        
        if (sCvariable != null) {
          problem.addBounds(sCvariable,0,upperBound);

          LinearConstraint constraint = new LinearConstraintLessEq(getConstraintName(generator, ScenarioMicrogridCapacityVariableFactory.PHASE_C, scenario));
          constraint.addVariable(sCvariable, 1.0);
          if (isNew) {
            constraint.setRightHandSide(discrete, upperBound);
          }
          problem.addLinearConstraint(constraint);
        }


      }
    }
  }
  
}
