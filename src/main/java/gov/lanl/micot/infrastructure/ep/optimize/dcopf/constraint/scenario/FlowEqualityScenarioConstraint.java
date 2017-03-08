package gov.lanl.micot.infrastructure.ep.optimize.dcopf.constraint.scenario;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.ep.optimize.ConstraintFactory;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.variable.scenario.FlowScenarioVariableFactory;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.variable.scenario.PhaseAngleScenarioVariableFactory;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.util.math.solver.LinearConstraint;
import gov.lanl.micot.util.math.solver.LinearConstraintEquals;
import gov.lanl.micot.util.math.solver.exception.InvalidConstraintException;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

import java.util.ArrayList;
import java.util.Collection;

/**
 * This constraints says, on a per scenario basis, the flow has to be equal to the difference in phase angles * susceptance.
 * 
 * @author Russell Bent
 */
public class FlowEqualityScenarioConstraint implements ConstraintFactory {

  private Collection<Scenario> scenarios = null;

  /**
   * Constructor
   * 
   * @param scenarios
   */
  public FlowEqualityScenarioConstraint(Collection<Scenario> scenarios) {
    this.scenarios = scenarios;
  }

  /**
   * Get the flow constraint name
   * 
   * @param edge
   * @return
   */
  private String getFlowConstraintName(ElectricPowerFlowConnection edge, int k) {
    return "\"Flow-" + k + "-" + edge.toString() + "\"";
  }

  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException, InvalidConstraintException {
    for (Scenario scenario : scenarios) {
      for (ElectricPowerFlowConnection connection : model.getFlowConnections()) {
        boolean status = scenario.computeActualStatus(connection, true);

        if (!status) {
          continue;
        }

//        ElectricPowerNode firstNode = model.getFirstNode(connection);
  //      ElectricPowerNode secondNode = model.getSecondNode(connection);

    //    if (!nodes.contains(firstNode) || !nodes.contains(secondNode)) {
      //    continue;
       // }

        createConstraint(problem, scenario, connection, model);        
      }
    }
  }

  /**
   * Create a specific constraint
   * 
   * @param scenario
   * @param edge
   * @return
   * @throws NoVariableException 
   * @throws InvalidConstraintException 
   */
  public Collection<LinearConstraint> createConstraint(MathematicalProgram problem, Scenario scenario, ElectricPowerFlowConnection connection, ElectricPowerModel model) throws NoVariableException, InvalidConstraintException {
    ArrayList<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
//    ElectricPowerModel modelK = (ElectricPowerModel)scenario.getModel();
    int k = scenario.getIndex();
    FlowScenarioVariableFactory flowVariableFactory = new FlowScenarioVariableFactory(scenarios);
    PhaseAngleScenarioVariableFactory phaseAngleVariableFactory = new PhaseAngleScenarioVariableFactory(scenarios);

    ElectricPowerNode fNode = model.getFirstNode(connection);
    ElectricPowerNode sNode = model.getSecondNode(connection);
    
    double impedance = connection.getSusceptance();
    LinearConstraint constraint = new LinearConstraintEquals(getFlowConstraintName(connection, k));
    constraint.setRightHandSide(0.0);
    constraint.addVariable(flowVariableFactory.getVariable(problem, connection, scenario), -1.0);    
    constraint.addVariable(phaseAngleVariableFactory.getVariable(problem, fNode, scenario), impedance);
    constraint.addVariable(phaseAngleVariableFactory.getVariable(problem, sNode, scenario), -impedance);
    problem.addLinearConstraint(constraint);
    constraints.add(constraint);

   
    
    return constraints;
  }

}
