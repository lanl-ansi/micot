package gov.lanl.micot.infrastructure.ep.optimize.dcopf.constraint.scenario;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.optimize.ConstraintFactory;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.variable.scenario.PhaseAngleScenarioVariableFactory;
import gov.lanl.micot.infrastructure.model.Node;
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
 * The phase angle constraint bounds... essentially here for big M on some of the dual variables
 * @author Russell Bent
 */
public class PhaseAngleBoundScenarioConstraint implements ConstraintFactory {
  
  private Collection<Scenario> scenarios = new ArrayList<Scenario>();

  /**
   * Constructor
   * @param scenarios
   */
  public PhaseAngleBoundScenarioConstraint(Collection<Scenario> scenarios) {
    this.scenarios = scenarios;
  }
  
  /**
   * Get the upper bound constraint name
   * 
   * @param edge
   * @return
   */
  private String getPhaseAngleUpperConstraintName(Node node, Scenario kappa) {
    return "\"PBU" + node.toString() + "\"-" + kappa.getIndex();
  }
  
  /**
   * Get the lower bound constraint name
   * @param node
   * @return
   */
  private String getPhaseAngleLowerConstraintName(Node node, Scenario kappa) {
    return "\"PBL" + node.toString() + "\"-" + kappa.getIndex();
  }
      
  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException, InvalidConstraintException {
    PhaseAngleScenarioVariableFactory phaseAngleVariableFactory = new PhaseAngleScenarioVariableFactory(scenarios);
  
    for (Scenario scenario : scenarios) {
      for (Node node : model.getNodes()) {      
        LinearConstraint constraint = new LinearConstraintLessEq(getPhaseAngleUpperConstraintName(node, scenario));
        constraint.setRightHandSide(model.getNodes().size());
        constraint.addVariable(phaseAngleVariableFactory.getVariable(problem, node, scenario), 1);
        problem.addLinearConstraint(constraint);
      
        constraint = new LinearConstraintGreaterEq(getPhaseAngleLowerConstraintName(node, scenario));
        constraint.setRightHandSide(-model.getNodes().size());
        constraint.addVariable(phaseAngleVariableFactory.getVariable(problem, node, scenario), 1);
        problem.addLinearConstraint(constraint);      
      }
    }
  }

  
  
}
