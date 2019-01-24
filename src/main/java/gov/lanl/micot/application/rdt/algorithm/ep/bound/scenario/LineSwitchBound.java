package gov.lanl.micot.application.rdt.algorithm.ep.bound.scenario;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.optimize.ConstraintFactory;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.application.rdt.algorithm.ep.variable.scenario.LineSwitchVariableFactory;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;


/**
 * Switches can only be built if the line is built
 * @author Russell Bent
 */
public class LineSwitchBound implements ConstraintFactory {

  public Scenario scenario = null;
  
  /**
   * Constraint
   */
  public LineSwitchBound(Scenario scenario) {
    this.scenario = scenario;
  }
    
  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException {
    LineSwitchVariableFactory lineSwitchVariableFactory = new LineSwitchVariableFactory(scenario,null);
    
    for (ElectricPowerFlowConnection edge : model.getFlowConnections()) {      
      if (lineSwitchVariableFactory.hasVariable(edge)) {      
      	Variable t_s = lineSwitchVariableFactory.getVariable(problem, edge);
    	  problem.addBounds(t_s, 0.0, 1.0);
      }    	
    }
  }

  
  
}
