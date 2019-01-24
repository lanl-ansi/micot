package gov.lanl.micot.application.rdt.algorithm.ep.heuristic.constraint;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.optimize.ConstraintFactory;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.application.rdt.algorithm.ep.variable.LineConstructionVariableFactory;
import gov.lanl.micot.util.math.solver.LinearConstraint;
import gov.lanl.micot.util.math.solver.LinearConstraintGreaterEq;
import gov.lanl.micot.util.math.solver.Solution;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.InvalidConstraintException;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;


/**
 * This constraint ties the outer problem line construction variable with inner problem line exists variable
 * 
 * x >= x
 * 
 * @author Russell Bent
 */
public class LineConstructionTieConstraint implements ConstraintFactory {

  private Scenario scenario = null;
  private Solution solution = null;

  /**
   * Constraint
   */
  public LineConstructionTieConstraint(Scenario scenario, Solution solution) {
    this.scenario = scenario;
    this.solution = solution;
  }
  
  private String getConstraintName(ElectricPowerFlowConnection edge, Scenario scenario) {
    return "Line Construction Tie-" + edge.toString() + "." + scenario;
  }
  
  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException, InvalidConstraintException {
    LineConstructionVariableFactory lineVariableFactory = new LineConstructionVariableFactory();
        
    for (ElectricPowerFlowConnection edge : model.getFlowConnections()) {
      if (lineVariableFactory.hasVariable(edge)) {
        Variable x = lineVariableFactory.getVariable(problem, edge);
        
        LinearConstraint constraint = new LinearConstraintGreaterEq(getConstraintName(edge, scenario));
        constraint.addVariable(x, 1.0);
        constraint.setRightHandSide(solution.getValueDouble(x));
        
        problem.addLinearConstraint(constraint);
      }
    }    
  }

  
  
}
