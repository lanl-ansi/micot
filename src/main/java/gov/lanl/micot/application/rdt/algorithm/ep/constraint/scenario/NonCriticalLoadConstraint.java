package gov.lanl.micot.application.rdt.algorithm.ep.constraint.scenario;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.Load;
import gov.lanl.micot.infrastructure.ep.optimize.ConstraintFactory;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.application.rdt.algorithm.AlgorithmConstants;
import gov.lanl.micot.application.rdt.algorithm.ep.variable.scenario.LoadVariableFactory;
import gov.lanl.micot.util.math.solver.LinearConstraint;
import gov.lanl.micot.util.math.solver.LinearConstraintGreaterEq;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.InvalidConstraintException;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;


/**
 * The amount of critical real load needs to be at least a certain amount
 * 
 * d <= \eta_c * total load
 * 
 * @author Russell Bent
 */
public class NonCriticalLoadConstraint implements ConstraintFactory {

  private double percentage = 0;
  private Scenario scenario = null;
  
  
  /**
   * Constraint
   */
  public NonCriticalLoadConstraint(double percentage, Scenario scenario) {
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
  private String getRealName(Scenario scenario, String phase) {
    return "Non Critical Real Load Met-" + scenario + "." + phase;
  }

  /**
   * Get the reactive constraint name
   * 
   * @param load
   * @param phase
   * @return
   */
  private String getReactiveName(Scenario scenario, String phase) {
    return "Non Critical Reactive Load Met-" + scenario + "." + phase;
  }
  
  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws NoVariableException, InvalidConstraintException  {
    LoadVariableFactory factory = new LoadVariableFactory(scenario);
    double mvaBase = model.getMVABase();

    LinearConstraint realA = new LinearConstraintGreaterEq(getRealName(scenario, LoadVariableFactory.PHASE_A));
    LinearConstraint reactiveA = new LinearConstraintGreaterEq(getReactiveName(scenario, LoadVariableFactory.PHASE_A));
    LinearConstraint realB = new LinearConstraintGreaterEq(getRealName(scenario, LoadVariableFactory.PHASE_B));
    LinearConstraint reactiveB = new LinearConstraintGreaterEq(getReactiveName(scenario, LoadVariableFactory.PHASE_B));
    LinearConstraint realC = new LinearConstraintGreaterEq(getRealName(scenario, LoadVariableFactory.PHASE_C));
    LinearConstraint reactiveC = new LinearConstraintGreaterEq(getReactiveName(scenario, LoadVariableFactory.PHASE_C));
    
    double totalRealLoadA = 0;
    double totalReactiveLoadA = 0;
    double totalRealLoadB = 0;
    double totalReactiveLoadB = 0;
    double totalRealLoadC = 0;
    double totalReactiveLoadC = 0;

    for (Load load : model.getLoads()) {
      boolean isCritical = load.getAttribute(AlgorithmConstants.IS_CRITICAL_LOAD_KEY) == null || !load.getAttribute(AlgorithmConstants.IS_CRITICAL_LOAD_KEY, Boolean.class) ? false : true;
      if (!isCritical) {
        Variable realAVariable = factory.getRealVariable(problem, load, LoadVariableFactory.PHASE_A);
        Variable reactiveAVariable = factory.getReactiveVariable(problem, load, LoadVariableFactory.PHASE_A);
        Variable realBVariable = factory.getRealVariable(problem, load, LoadVariableFactory.PHASE_B);
        Variable reactiveBVariable = factory.getReactiveVariable(problem, load, LoadVariableFactory.PHASE_B);
        Variable realCVariable = factory.getRealVariable(problem, load, LoadVariableFactory.PHASE_C);
        Variable reactiveCVariable = factory.getReactiveVariable(problem, load, LoadVariableFactory.PHASE_C);
        
        if (realAVariable != null) {
          realA.addVariable(realAVariable, 1.0);
          reactiveA.addVariable(reactiveAVariable, 1.0);
          totalRealLoadA += load.getAttribute(Load.REAL_LOAD_A_MAX_KEY, Number.class).doubleValue();
          totalReactiveLoadA += load.getAttribute(Load.REACTIVE_LOAD_A_MAX_KEY, Number.class).doubleValue();
        }

        if (realBVariable != null) {
          realB.addVariable(realBVariable, 1.0);
          reactiveB.addVariable(reactiveBVariable, 1.0);
          totalRealLoadB += load.getAttribute(Load.REAL_LOAD_B_MAX_KEY, Number.class).doubleValue();
          totalReactiveLoadB += load.getAttribute(Load.REACTIVE_LOAD_B_MAX_KEY, Number.class).doubleValue();
        }
        
        if (realCVariable != null) {
          realC.addVariable(realCVariable, 1.0);
          reactiveC.addVariable(reactiveCVariable, 1.0);
          totalRealLoadC += load.getAttribute(Load.REAL_LOAD_C_MAX_KEY, Number.class).doubleValue();
          totalReactiveLoadC += load.getAttribute(Load.REACTIVE_LOAD_C_MAX_KEY, Number.class).doubleValue();
        }        
      }
     }
        
    totalRealLoadA /= mvaBase;
    totalReactiveLoadA /= mvaBase;
    totalRealLoadB /= mvaBase;
    totalReactiveLoadB /= mvaBase;
    totalRealLoadC /= mvaBase;
    totalReactiveLoadC /= mvaBase;
    
    realA.setRightHandSide(totalRealLoadA * percentage);
    reactiveA.setRightHandSide(totalReactiveLoadA * percentage);
    realB.setRightHandSide(totalRealLoadB * percentage);
    reactiveB.setRightHandSide(totalReactiveLoadB * percentage);
    realC.setRightHandSide(totalRealLoadC * percentage);
    reactiveC.setRightHandSide(totalReactiveLoadC * percentage);

    if (realA.getNumberOfVariables() > 0) {
      problem.addLinearConstraint(realA);
    }      
    if (reactiveA.getNumberOfVariables() > 0) {
      problem.addLinearConstraint(reactiveA);
    }
    
    if (realB.getNumberOfVariables() > 0) {
      problem.addLinearConstraint(realB);
    }      
    if (reactiveB.getNumberOfVariables() > 0) {
      problem.addLinearConstraint(reactiveB);
    }
    
    if (realC.getNumberOfVariables() > 0) {
      problem.addLinearConstraint(realC);
    }      
    if (reactiveC.getNumberOfVariables() > 0) {
      problem.addLinearConstraint(reactiveC);
    }    
  }
  
}
