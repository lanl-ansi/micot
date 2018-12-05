package gov.lanl.micot.application.rdt.algorithm.ep.sbd.bound;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.optimize.ConstraintFactory;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.application.rdt.algorithm.ep.variable.scenario.LineSwitchVariableFactory;
import gov.lanl.micot.util.math.solver.Solution;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;


/**
 * Switches can only be built if the line is built
 * @author Russell Bent
 */
public class LineSwitchBound implements ConstraintFactory {

  private Scenario scenario = null;
  private Solution values = null;
  private MathematicalProgram outerProblem = null;

  
  /**
   * Constraint
   */
  public LineSwitchBound(Scenario scenario, Solution values, MathematicalProgram outerProblem) {
    this.scenario = scenario;
    this.values = values;
    this.outerProblem = outerProblem;
  }
    
  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException {
    LineSwitchVariableFactory lineSwitchVariableFactory = new LineSwitchVariableFactory(scenario);
    gov.lanl.micot.application.rdt.algorithm.ep.variable.LineSwitchVariableFactory outerFactory = new gov.lanl.micot.application.rdt.algorithm.ep.variable.LineSwitchVariableFactory();
        
    for (ElectricPowerFlowConnection edge : model.getFlowConnections()) {      
      if (lineSwitchVariableFactory.hasVariable(edge)) {      
      	Variable t_s = lineSwitchVariableFactory.getVariable(problem, edge);
        Variable t = outerFactory.getVariable(outerProblem, edge);                
    	  problem.addBounds(t_s, values.getValueInt(t), values.getValueInt(t));
      }    	
    }
  }

  
  
}
