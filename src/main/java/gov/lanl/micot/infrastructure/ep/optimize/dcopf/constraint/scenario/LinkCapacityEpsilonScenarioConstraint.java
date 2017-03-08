package gov.lanl.micot.infrastructure.ep.optimize.dcopf.constraint.scenario;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.ep.optimize.ConstraintFactory;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.variable.scenario.PhaseAngleScenarioVariableFactory;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.util.math.solver.LinearConstraint;
import gov.lanl.micot.util.math.solver.LinearConstraintGreaterEq;
import gov.lanl.micot.util.math.solver.LinearConstraintLessEq;
import gov.lanl.micot.util.math.solver.exception.InvalidConstraintException;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

import java.util.ArrayList;
import java.util.Collection;

/**
 * The link capacity constraint for scenarios as long as the probability of a scenario is bigger than some epsilon
 * 
 * @author Russell Bent
 */
public class LinkCapacityEpsilonScenarioConstraint implements ConstraintFactory {

  private Collection<Scenario> scenarios = null;
  private double epsilon = 1;

  /**
   * Constructor
   * 
   * @param scenarios
   */
  public LinkCapacityEpsilonScenarioConstraint(Collection<Scenario> scenarios, double epsilon) {
    this.scenarios = scenarios;
    this.epsilon = epsilon;
  }

  /**
   * Get the flow constraint name
   * 
   * @param edge
   * @return
   */
  private String getFlowLessThanConstraintName(ElectricPowerFlowConnection edge, int k) {
    return "\"FlowL-" + k + "-" + edge.toString() + "\"";
  }

  /**
   * Get the flow constraint name
   * 
   * @param edge
   * @return
   */
  private String getFlowGreaterThanConstraintName(ElectricPowerFlowConnection edge, int k) {
    return "\"FlowG-" + k + "-" + edge.toString() + "\"";
  }
  
  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException, InvalidConstraintException {
    for (Scenario scenario : scenarios) {
      if (scenario.getWeight() <= epsilon) {
        continue;
      }
      
      for (ElectricPowerFlowConnection connection : model.getFlowConnections()) {
        boolean status = scenario.computeActualStatus(connection, true);

        if (!status) {
          continue;
        }
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
    double mva = model.getMVABase();
    int k = scenario.getIndex();
    PhaseAngleScenarioVariableFactory phaseAngleVariableFactory = new PhaseAngleScenarioVariableFactory(scenarios);
    ElectricPowerNode fNode = model.getFirstNode(connection);
    ElectricPowerNode sNode = model.getSecondNode(connection);
    
    double impedance = connection.getSusceptance();

    LinearConstraint constraint = new LinearConstraintLessEq(getFlowLessThanConstraintName(connection, k));
    constraint.setRightHandSide(connection.getCapacityRating() / mva);
    constraint.addVariable(phaseAngleVariableFactory.getVariable(problem, fNode, scenario), impedance);
    constraint.addVariable(phaseAngleVariableFactory.getVariable(problem, sNode, scenario), -impedance);
    problem.addLinearConstraint(constraint);
    constraints.add(constraint);

    constraint = new LinearConstraintGreaterEq(getFlowGreaterThanConstraintName(connection, k));
    constraint.setRightHandSide(-connection.getCapacityRating() / mva);
    constraint.addVariable(phaseAngleVariableFactory.getVariable(problem, fNode, scenario), impedance);
    constraint.addVariable(phaseAngleVariableFactory.getVariable(problem, sNode, scenario), -impedance);
    problem.addLinearConstraint(constraint);
    constraints.add(constraint);
    
    return constraints;
  }

}
