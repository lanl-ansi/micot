package gov.lanl.micot.application.rdt.algorithm.ep.bound.scenario;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.optimize.ConstraintFactory;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.application.rdt.algorithm.ep.variable.scenario.LineActiveVariableFactory;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;


/**
 * Bounds on the line active variables are 0,1
 * @author Russell Bent
 */
public class LineActiveBound implements ConstraintFactory {

  private Scenario scenario = null;

  /**
   * Constraint
   */
  public LineActiveBound(Scenario scenario) {
    this.scenario = scenario;
  }
    
  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException {
    LineActiveVariableFactory lineVariableFactory = new LineActiveVariableFactory(scenario);        
    for (ElectricPowerFlowConnection edge : model.getFlowConnections()) {
      if (lineVariableFactory.hasVariable(edge)) {
        Variable z_s = lineVariableFactory.getVariable(problem, edge);
        problem.addBounds(z_s, 0.0, 1.0);
      }      
    }
  }

  
  
}
