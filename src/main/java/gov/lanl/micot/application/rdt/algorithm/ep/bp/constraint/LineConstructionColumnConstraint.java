package gov.lanl.micot.application.rdt.algorithm.ep.bp.constraint;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.optimize.ConstraintFactory;
import gov.lanl.micot.infrastructure.model.Scenario;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import gov.lanl.micot.application.rdt.algorithm.ep.bp.variable.LambdaVariableFactory;
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
 * Forces the master problem solution to be a combination of columns for lines
 * @author Russell Bent
 */
public class LineConstructionColumnConstraint implements ConstraintFactory {

  private Collection<Scenario> scenarios = null;
  HashMap<Scenario, ArrayList<Solution>> columns = null;
    
  /**
   * Constraint
   */
  public LineConstructionColumnConstraint(Collection<Scenario> scenarios, HashMap<Scenario, ArrayList<Solution>> columns) {    
      this.scenarios = scenarios;
      this.columns = columns;
  }
    
  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException, InvalidConstraintException {
    LambdaVariableFactory lambdaFactory = new LambdaVariableFactory(scenarios, columns);
    LineConstructionVariableFactory lineFactory = new LineConstructionVariableFactory();
        
    // generator columns
    for (ElectricPowerFlowConnection edge : model.getFlowConnections()) {
      if (lineFactory.hasVariable(edge)) {
        Variable variable = lineFactory.getVariable(problem, edge);
     
        for (Scenario scenario : scenarios) {
          ArrayList<Solution> c = columns.get(scenario);
          LinearConstraint constraint = new LinearConstraintGreaterEq(getName(scenario, edge));
          constraint.setRightHandSide(0);
          constraint.addVariable(variable, 1.0);
        
          for (int i = 0; i < c.size(); ++i) {
            Solution solution = c.get(i);
            Variable lambda = lambdaFactory.getVariable(problem, scenario, i);          
            constraint.addVariable(lambda,-solution.getValueDouble(null));          
          }
          problem.addLinearConstraint(constraint);
        }
      }
    }
        
  }
  
  /**
   * Get the flow constraint name
   * 
   * @param edge
   * @return
   */
  private String getName(Scenario scenario, ElectricPowerFlowConnection edge) {
    return "Column-Line." + edge.toString() + "-" + scenario.getIndex();
  }

  
}
