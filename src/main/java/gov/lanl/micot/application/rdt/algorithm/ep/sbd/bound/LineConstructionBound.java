package gov.lanl.micot.application.rdt.algorithm.ep.sbd.bound;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.optimize.ConstraintFactory;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.application.rdt.algorithm.ep.variable.scenario.LineExistVariableFactory;
import gov.lanl.micot.util.math.solver.Solution;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;


/**
 * Bounds on the line construction variables are 0,1
 * @author Russell Bent
 */
public class LineConstructionBound implements ConstraintFactory {

  private Scenario scenario = null;
  private Solution values = null;
  private MathematicalProgram outerProblem = null;


  /**
   * Constraint
   */
  public LineConstructionBound(Scenario scenario, Solution values, MathematicalProgram outerProblem) {
    this.scenario = scenario;
    this.values = values;
    this.outerProblem = outerProblem;

  }
    
  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException {
    LineExistVariableFactory lineVariableFactory = new LineExistVariableFactory(scenario,null);
    gov.lanl.micot.application.rdt.algorithm.ep.variable.LineConstructionVariableFactory outerFactory = new gov.lanl.micot.application.rdt.algorithm.ep.variable.LineConstructionVariableFactory();
        
    for (ElectricPowerFlowConnection edge : model.getFlowConnections()) {
      if (outerFactory.hasVariable(edge)) {
        Variable x_s = lineVariableFactory.getVariable(problem, edge);
        Variable x = outerFactory.getVariable(outerProblem, edge);                
        problem.addBounds(x_s, values.getValueInt(x), values.getValueInt(x));
      }      
    }
  }

  
  
}
