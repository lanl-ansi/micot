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
 * The link capacity constraint for scenarios
 * 
 * @author Russell Bent
 */
public class LinkCapacityScenarioConstraint implements ConstraintFactory {

  private Collection<Scenario> scenarios = null;

  /**
   * Constructor
   * 
   * @param scenarios
   */
  public LinkCapacityScenarioConstraint(Collection<Scenario> scenarios) {
    this.scenarios = scenarios;
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
      for (ElectricPowerFlowConnection link : model.getFlowConnections()) {
        boolean status = link.getActualStatus();
        boolean isDisabled = !scenario.computeActualStatus(link, status);

        // no capacity constraint if the link is disabled (not present)
        if (isDisabled) {
          continue;
        }

        createConstraint(problem, scenario, link, model);
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
  public Collection<LinearConstraint> createConstraint(MathematicalProgram problem, Scenario scenario, ElectricPowerFlowConnection link, ElectricPowerModel model) throws NoVariableException, InvalidConstraintException {
    ArrayList<LinearConstraint> constraints = new ArrayList<LinearConstraint>();

    double mva = model.getMVABase();
    int k = scenario.getIndex();
    PhaseAngleScenarioVariableFactory phaseAngleVariableFactory = new PhaseAngleScenarioVariableFactory(scenarios);
    ElectricPowerNode firstNode = model.getFirstNode(link);
    ElectricPowerNode secondNode = model.getSecondNode(link);

    double impedance = link.getSusceptance();

    LinearConstraint constraint = new LinearConstraintLessEq(getFlowLessThanConstraintName(link, k));
    constraint.setRightHandSide(link.getCapacityRating() / mva);
    constraint.addVariable(phaseAngleVariableFactory.getVariable(problem, firstNode, scenario), impedance);
    constraint.addVariable(phaseAngleVariableFactory.getVariable(problem, secondNode, scenario), -impedance);
    problem.addLinearConstraint(constraint);
    constraints.add(constraint);

    constraint = new LinearConstraintGreaterEq(getFlowGreaterThanConstraintName(link, k));
    constraint.setRightHandSide(-link.getCapacityRating() / mva);
    constraint.addVariable(phaseAngleVariableFactory.getVariable(problem, firstNode, scenario), impedance);
    constraint.addVariable(phaseAngleVariableFactory.getVariable(problem, secondNode, scenario), -impedance);
    problem.addLinearConstraint(constraint);
    constraints.add(constraint);

    return constraints;
  }

}
