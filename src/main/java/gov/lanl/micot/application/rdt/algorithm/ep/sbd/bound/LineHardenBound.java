package gov.lanl.micot.application.rdt.algorithm.ep.sbd.bound;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.optimize.ConstraintFactory;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.application.rdt.algorithm.ep.variable.scenario.LineHardenVariableFactory;
import gov.lanl.micot.util.math.solver.Solution;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;


/**
 * Bounds on the line hardening variables are 0,1
 * 
 * This is part of constraint 26 in the AAAI 2015 paper
 * 
 * @author Russell Bent
 */
public class LineHardenBound implements ConstraintFactory {

  private Scenario scenario = null;
  private Solution values = null;
  private MathematicalProgram outerProblem = null;
  
  /**
   * Constraint
   */
  public LineHardenBound(Scenario scenario, Solution values, MathematicalProgram outerProblem) { 
    this.scenario = scenario;
    this.values = values;
    this.outerProblem = outerProblem;
  }
    
  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException {
    LineHardenVariableFactory lineVariableFactory = new LineHardenVariableFactory(scenario,null);
    gov.lanl.micot.application.rdt.algorithm.ep.variable.LineHardenVariableFactory outerFactory = new gov.lanl.micot.application.rdt.algorithm.ep.variable.LineHardenVariableFactory();
    
    for (ElectricPowerFlowConnection edge : model.getFlowConnections()) {
      if (lineVariableFactory.hasVariable(edge)) {
        Variable h_s = lineVariableFactory.getVariable(problem, edge);
        Variable h = outerFactory.getVariable(outerProblem, edge);                
        problem.addBounds(h_s, values.getValueInt(h), values.getValueInt(h));
      }      
    }
  }
  
}
