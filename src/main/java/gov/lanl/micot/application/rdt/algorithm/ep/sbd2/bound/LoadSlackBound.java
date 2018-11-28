package gov.lanl.micot.application.rdt.algorithm.ep.sbd2.bound;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.Load;
import gov.lanl.micot.infrastructure.ep.optimize.ConstraintFactory;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.application.rdt.algorithm.ep.sbd2.variable.LoadSlackVariableFactory;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

/**
 * Bounds on load slack
 * 
 * @author Russell Bent
 */
public class LoadSlackBound implements ConstraintFactory {

  private Scenario scenario = null;
  
  /**
   * Constraint
   */
  public LoadSlackBound(Scenario scenario) {
    this.scenario = scenario;
  }

  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws NoVariableException {
    LoadSlackVariableFactory variableFactory = new LoadSlackVariableFactory(scenario);
    double mvaBase = model.getMVABase();
    double dp = 0;
    
    for (Load load : model.getLoads()) {
      if (!scenario.computeActualStatus(load, true)) {
        continue;
      }
            
      if (load.getAttribute(Load.HAS_PHASE_A_KEY, Boolean.class)) {   
        dp += load.getAttribute(Load.REAL_LOAD_A_MAX_KEY, Number.class).doubleValue() / mvaBase; 
        dp += load.getAttribute(Load.REACTIVE_LOAD_A_MAX_KEY, Number.class).doubleValue() / mvaBase;            
      }
      
      if (load.getAttribute(Load.HAS_PHASE_B_KEY, Boolean.class)) {   
        dp += load.getAttribute(Load.REAL_LOAD_B_MAX_KEY, Number.class).doubleValue() / mvaBase; 
        dp += load.getAttribute(Load.REACTIVE_LOAD_B_MAX_KEY, Number.class).doubleValue() / mvaBase;            
      }

      if (load.getAttribute(Load.HAS_PHASE_C_KEY, Boolean.class)) {   
        dp += load.getAttribute(Load.REAL_LOAD_C_MAX_KEY, Number.class).doubleValue() / mvaBase; 
        dp += load.getAttribute(Load.REACTIVE_LOAD_C_MAX_KEY, Number.class).doubleValue() / mvaBase;            
      }
    }
    
    Variable v = variableFactory.getVariable(problem);
    problem.addBounds(v,0.0,dp);
  }
  
}
