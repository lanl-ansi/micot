package gov.lanl.micot.application.rdt.algorithm.ep.bound.scenario;

import gov.lanl.micot.infrastructure.ep.model.Bus;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.optimize.ConstraintFactory;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.application.rdt.algorithm.ep.variable.scenario.VoltageVariableFactory;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

/**
 * Voltage binary variables (modeled as the square of voltage) 
 * overbar{v}_i <= v_i underbar{v}_i 
 *
 * @author Russell Bent
 */
public class VoltageBound implements ConstraintFactory {

  public Scenario scenario = null;
  
  /**
   * Constraint
   */
  public VoltageBound(Scenario scenario) {
    this.scenario = scenario;
  }

  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException {
    VoltageVariableFactory variableFactory = new VoltageVariableFactory(scenario);
    
    for (Bus bus : model.getBuses()) {        
      Variable v_a = variableFactory.getVariable(problem, bus, VoltageVariableFactory.PHASE_A);
      Variable v_b = variableFactory.getVariable(problem, bus, VoltageVariableFactory.PHASE_B);
      Variable v_c = variableFactory.getVariable(problem, bus, VoltageVariableFactory.PHASE_C);

      if (v_a != null) {
        problem.addBounds(v_a, bus.getMinimumVoltagePU() * bus.getMinimumVoltagePU(), bus.getMaximumVoltagePU() * bus.getMaximumVoltagePU());
      }
      if (v_b != null) {
        problem.addBounds(v_b, bus.getMinimumVoltagePU() * bus.getMinimumVoltagePU(), bus.getMaximumVoltagePU() * bus.getMaximumVoltagePU());
      }
      if (v_c != null) {
        problem.addBounds(v_c, bus.getMinimumVoltagePU() * bus.getMinimumVoltagePU(), bus.getMaximumVoltagePU() * bus.getMaximumVoltagePU());
      }    
    }
  }
  
}
