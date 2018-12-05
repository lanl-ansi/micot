package gov.lanl.micot.deprecated.rdt.algorithm.ep.mip.constraint.scenario;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.ep.model.Load;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.optimize.mathprogram.constraint.ScenarioConstraintFactory;
import gov.lanl.micot.deprecated.rdt.algorithm.ep.mip.variable.scenario.ScenarioRealLoadPhaseVariableFactory;
import gov.lanl.micot.deprecated.rdt.algorithm.ep.mip.variable.scenario.ScenarioLoadServeVariableFactory;
import gov.lanl.micot.util.math.solver.LinearConstraintEquals;
import gov.lanl.micot.util.math.solver.exception.InvalidConstraintException;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

import java.util.Collection;

/**
 * Real Load served = the decision variable
 * 
 * This is constraint 6 in AAAI 2015 paper
 * 
 * @author Russell Bent
 */
public class ScenarioRealLoadServeConstraint extends ScenarioConstraintFactory<ElectricPowerNode, ElectricPowerModel> {

  /**
   * Constraint
   */
  public ScenarioRealLoadServeConstraint(Collection<Scenario> scenarios) {
    super(scenarios);
  }

  /**
   * Get the constraint name
   * 
   * @param load
   * @param phase
   * @return
   */
  private String getConstraintName(Load load, String phase, Scenario scenario) {
    return "RealLoadServe-" + load + "-" + scenario + "." + phase;
  }

  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException, InvalidConstraintException {
    ScenarioLoadServeVariableFactory loadServeVariableFactory = new ScenarioLoadServeVariableFactory(getScenarios());
    ScenarioRealLoadPhaseVariableFactory loadVariableFactory = new ScenarioRealLoadPhaseVariableFactory(getScenarios());
    double mvaBase = model.getMVABase();
    for (Scenario scenario : getScenarios()) {
      for (ElectricPowerNode node : model.getNodes()) {
        for (Load load : node.getComponents(Load.class)) {
          if (load.getAttribute(Load.HAS_PHASE_A_KEY, Boolean.class)) {
            LinearConstraintEquals constraint = new LinearConstraintEquals(getConstraintName(load, ScenarioRealLoadPhaseVariableFactory.PHASE_A, scenario));
            constraint.addVariable(loadVariableFactory.getVariable(problem, load, ScenarioRealLoadPhaseVariableFactory.PHASE_A, scenario));
            constraint.setRightHandSide(loadServeVariableFactory.getVariable(problem, node, scenario), load.getAttribute(Load.REAL_LOAD_A_MAX_KEY, Number.class).doubleValue() / mvaBase);
            problem.addLinearConstraint(constraint);
          }

          if (load.getAttribute(Load.HAS_PHASE_B_KEY, Boolean.class)) {
            LinearConstraintEquals constraint = new LinearConstraintEquals(getConstraintName(load, ScenarioRealLoadPhaseVariableFactory.PHASE_B, scenario));
            constraint.addVariable(loadVariableFactory.getVariable(problem, load, ScenarioRealLoadPhaseVariableFactory.PHASE_B, scenario));
            constraint.setRightHandSide(loadServeVariableFactory.getVariable(problem, node, scenario), load.getAttribute(Load.REAL_LOAD_B_MAX_KEY, Number.class).doubleValue() / mvaBase);
            problem.addLinearConstraint(constraint);
          }

          if (load.getAttribute(Load.HAS_PHASE_C_KEY, Boolean.class)) {
            LinearConstraintEquals constraint = new LinearConstraintEquals(getConstraintName(load, ScenarioRealLoadPhaseVariableFactory.PHASE_C, scenario));
            constraint.addVariable(loadVariableFactory.getVariable(problem, load, ScenarioRealLoadPhaseVariableFactory.PHASE_C, scenario));
            constraint.setRightHandSide(loadServeVariableFactory.getVariable(problem, node, scenario), load.getAttribute(Load.REAL_LOAD_C_MAX_KEY, Number.class).doubleValue() / mvaBase);
            problem.addLinearConstraint(constraint);
          }
        }
      }
    }
  }
  
}
