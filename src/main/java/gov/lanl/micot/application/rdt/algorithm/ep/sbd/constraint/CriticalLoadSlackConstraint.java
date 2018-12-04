package gov.lanl.micot.application.rdt.algorithm.ep.sbd.constraint;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.Load;
import gov.lanl.micot.infrastructure.ep.optimize.ConstraintFactory;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.application.rdt.algorithm.AlgorithmConstants;
import gov.lanl.micot.application.rdt.algorithm.ep.sbd.variable.LoadSlackVariableFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.variable.scenario.LoadVariableFactory;
import gov.lanl.micot.util.math.solver.LinearConstraint;
import gov.lanl.micot.util.math.solver.LinearConstraintGreaterEq;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.InvalidConstraintException;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

/**
 * The amount of critical real load needs to be at least a certain amount,
 * but for an objective function criteria
 * 
 * d <= \eta_c * total load
 * 
 * @author Russell Bent
 */
public class CriticalLoadSlackConstraint implements ConstraintFactory {

  private double percentage = 0;
  private Scenario scenario = null;
  
  
  /**
   * Constraint
   */
  public CriticalLoadSlackConstraint(double percentage, Scenario scenario) {
    this.scenario = scenario;
    this.percentage = percentage;
  }

  /**
   * Get the real constraint name
   * 
   * @param load
   * @param phase
   * @return
   */
  private String getRealName(Scenario scenario) {
    return "Critical Real Load Met-" + scenario;
  }

  /**
   * Get the reactive constraint name
   * 
   * @param load
   * @param phase
   * @return
   */
  private String getReactiveName(Scenario scenario) {
    return "Critical Reactive Load Met-" + scenario;
  }
  
  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws NoVariableException, InvalidConstraintException  {
    LoadVariableFactory loadVariable = new LoadVariableFactory(scenario);
    LoadSlackVariableFactory slackVariable = new LoadSlackVariableFactory(scenario);

    double mvaBase = model.getMVABase();

    LinearConstraint real = new LinearConstraintGreaterEq(getRealName(scenario));
    LinearConstraint reactive = new LinearConstraintGreaterEq(getReactiveName(scenario));
    
    double totalRealLoad = 0;
    double totalReactiveLoad = 0;

    for (Load load : model.getLoads()) {
      boolean isCritical = load.getAttribute(AlgorithmConstants.IS_CRITICAL_LOAD_KEY) == null || !load.getAttribute(AlgorithmConstants.IS_CRITICAL_LOAD_KEY, Boolean.class) ? false : true;
      if (isCritical) {
        Variable dp_a = loadVariable.getRealVariable(problem, load, LoadVariableFactory.PHASE_A);
        Variable dq_a = loadVariable.getReactiveVariable(problem, load, LoadVariableFactory.PHASE_A);
        Variable dp_b = loadVariable.getRealVariable(problem, load, LoadVariableFactory.PHASE_B);
        Variable dq_b = loadVariable.getReactiveVariable(problem, load, LoadVariableFactory.PHASE_B);
        Variable dp_c = loadVariable.getRealVariable(problem, load, LoadVariableFactory.PHASE_C);
        Variable dq_c = loadVariable.getReactiveVariable(problem, load, LoadVariableFactory.PHASE_C);
        
        if (dp_a != null) {
          real.addVariable(dp_a, 1.0);
          reactive.addVariable(dq_a, 1.0);
          totalRealLoad += load.getAttribute(Load.REAL_LOAD_A_MAX_KEY, Number.class).doubleValue();
          totalReactiveLoad += load.getAttribute(Load.REACTIVE_LOAD_A_MAX_KEY, Number.class).doubleValue();
        }

        if (dp_b != null) {
          real.addVariable(dp_b, 1.0);
          reactive.addVariable(dq_b, 1.0);
          totalRealLoad += load.getAttribute(Load.REAL_LOAD_B_MAX_KEY, Number.class).doubleValue();
          totalReactiveLoad += load.getAttribute(Load.REACTIVE_LOAD_B_MAX_KEY, Number.class).doubleValue();
        }
        
        if (dp_c != null) {
          real.addVariable(dp_c, 1.0);
          reactive.addVariable(dq_c, 1.0);
          totalRealLoad += load.getAttribute(Load.REAL_LOAD_C_MAX_KEY, Number.class).doubleValue();
          totalReactiveLoad += load.getAttribute(Load.REACTIVE_LOAD_C_MAX_KEY, Number.class).doubleValue();
        }        
      }
     }
        
    totalRealLoad /= mvaBase;
    totalReactiveLoad /= mvaBase;
    
    real.setRightHandSide(totalRealLoad * percentage);
    reactive.setRightHandSide(totalReactiveLoad * percentage);

    real.addVariable(slackVariable.getVariable(problem), 1.0);
    real.addVariable(slackVariable.getVariable(problem), 1.0);
    real.addVariable(slackVariable.getVariable(problem), 1.0);
    reactive.addVariable(slackVariable.getVariable(problem), 1.0);
    reactive.addVariable(slackVariable.getVariable(problem), 1.0);
    reactive.addVariable(slackVariable.getVariable(problem), 1.0);
    
    problem.addLinearConstraint(real);
    problem.addLinearConstraint(reactive);    
  }
  
}
