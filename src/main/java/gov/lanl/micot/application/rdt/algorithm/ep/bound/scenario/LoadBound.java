package gov.lanl.micot.application.rdt.algorithm.ep.bound.scenario;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.Load;
import gov.lanl.micot.infrastructure.ep.optimize.ConstraintFactory;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.application.rdt.algorithm.ep.variable.scenario.LoadVariableFactory;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

/**
 * Bounds on loads
 * 
 * @author Russell Bent
 */
public class LoadBound implements ConstraintFactory {

  private Scenario scenario = null;
  
  /**
   * Constraint
   */
  public LoadBound(Scenario scenario) {
    this.scenario = scenario;
  }

  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws NoVariableException {
    LoadVariableFactory variableFactory = new LoadVariableFactory(scenario);
    double mvaBase = model.getMVABase();
    for (Load load : model.getLoads()) {
      if (!scenario.computeActualStatus(load, true)) {
        continue;
      }
            
      if (load.getAttribute(Load.HAS_PHASE_A_KEY, Boolean.class)) {   
        Variable dp_a = variableFactory.getRealVariable(problem, load, LoadVariableFactory.PHASE_A);
        Variable dq_a = variableFactory.getReactiveVariable(problem, load, LoadVariableFactory.PHASE_A);        
        problem.addBounds(dp_a, 0.0, load.getAttribute(Load.REAL_LOAD_A_MAX_KEY, Number.class).doubleValue() / mvaBase);
        problem.addBounds(dq_a, 0.0, load.getAttribute(Load.REACTIVE_LOAD_A_MAX_KEY, Number.class).doubleValue() / mvaBase); 
      }
      
      if (load.getAttribute(Load.HAS_PHASE_B_KEY, Boolean.class)) {        
        Variable dp_b = variableFactory.getRealVariable(problem, load, LoadVariableFactory.PHASE_B);
        Variable dq_b = variableFactory.getReactiveVariable(problem, load, LoadVariableFactory.PHASE_B);        
        problem.addBounds(dp_b, 0.0, load.getAttribute(Load.REAL_LOAD_B_MAX_KEY, Number.class).doubleValue() / mvaBase);
        problem.addBounds(dq_b, 0.0, load.getAttribute(Load.REACTIVE_LOAD_B_MAX_KEY, Number.class).doubleValue() / mvaBase); 
      }
      
      if (load.getAttribute(Load.HAS_PHASE_C_KEY, Boolean.class)) {        
        Variable dp_c = variableFactory.getRealVariable(problem, load, LoadVariableFactory.PHASE_C);
        Variable dq_c = variableFactory.getReactiveVariable(problem, load, LoadVariableFactory.PHASE_C);        
        problem.addBounds(dp_c, 0.0, load.getAttribute(Load.REAL_LOAD_C_MAX_KEY, Number.class).doubleValue() / mvaBase);
        problem.addBounds(dq_c, 0.0, load.getAttribute(Load.REACTIVE_LOAD_C_MAX_KEY, Number.class).doubleValue() / mvaBase); 
      }
    }
  }
  
}
