package gov.lanl.micot.application.rdt.algorithm.ep.constraint.scenario.cycle;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.optimize.ConstraintFactory;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.application.rdt.algorithm.ep.variable.scenario.LineActiveVariableFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.variable.scenario.cycle.EdgeActiveVariable;
import gov.lanl.micot.util.math.solver.LinearConstraint;
import gov.lanl.micot.util.math.solver.LinearConstraintEquals;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.InvalidConstraintException;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

/**
 * This constraint ensures that choices of edges in the flow network are linked to 
 * the scenario variables about line construction and switches
 * 
 * @author Russell Bent
 */
public class LinkingConstraint implements ConstraintFactory {

  private Scenario scenario = null;
  
  /**
   * Constraint
   */
  public LinkingConstraint(Scenario scenario) {
    this.scenario = scenario;
  }

  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException, InvalidConstraintException {
    LineActiveVariableFactory activeFactory = new LineActiveVariableFactory(scenario);
    EdgeActiveVariable edgeChoiceFactory = new EdgeActiveVariable(scenario);
    
    for (ElectricPowerFlowConnection connection : model.getFlowConnections()) {                
      Variable z_s = activeFactory.getVariable(problem, connection);
      Variable edgeVar = edgeChoiceFactory.getVariable(problem, model.getFirstNode(connection), model.getSecondNode(connection));
      LinearConstraint constraint = new LinearConstraintEquals(getConstraintName(connection));
      constraint.setRightHandSide(0.0);

      if (activeFactory.hasVariable(connection)) {
        constraint.addVariable(z_s, 1.0);
      }                
      else {
        constraint.setRightHandSide(connection.getStatus() ? 1 : 0);
      }
        
      constraint.addVariable(edgeVar, -1.0);
      problem.addLinearConstraint(constraint);        
    }
  }

  /**
   * Get the ge name
   * @param connection
   * @param scenario
   * @return
   */
  private String getConstraintName(ElectricPowerFlowConnection connection) {
    String constraintName = "TreeLink-" + connection + "-" + scenario;
    return constraintName;
  }

}
