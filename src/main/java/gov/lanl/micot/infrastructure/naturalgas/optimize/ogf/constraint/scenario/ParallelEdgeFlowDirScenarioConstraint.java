package gov.lanl.micot.infrastructure.naturalgas.optimize.ogf.constraint.scenario;

import gov.lanl.micot.infrastructure.model.Edge;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasConnection;
import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasModel;
import gov.lanl.micot.infrastructure.naturalgas.optimize.ConstraintFactory;
import gov.lanl.micot.infrastructure.naturalgas.optimize.ogf.variable.scenario.FlowDirectionScenarioVariableFactory;
import gov.lanl.micot.util.math.solver.LinearConstraint;
import gov.lanl.micot.util.math.solver.LinearConstraintEquals;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.InvalidConstraintException;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A cut that indicates that all edges in parallel must have linked directionality
 * 
 * @author Russell Bent
 */
public class ParallelEdgeFlowDirScenarioConstraint implements ConstraintFactory {

  private Collection<Scenario> scenarios = new ArrayList<Scenario>();

  private static final String FORWARD_PREFIX = "";
  private static final String REVERSE_PREFIX = ".R";

  /**
   * Constructor
   * 
   * @param scenarios
   */
  public ParallelEdgeFlowDirScenarioConstraint(Collection<Scenario> scenarios) {
    this.scenarios = scenarios;
  }

  /**
   * Get the constraint name
   */
  public static String getConstraintName(Scenario k, NaturalGasConnection c, String prefix) {
    return "Parallel." + c + "." + k.getIndex() + prefix;
  }

  @Override
  public void constructConstraint(MathematicalProgram problem, NaturalGasModel model) throws VariableExistsException, NoVariableException, InvalidConstraintException {
    FlowDirectionScenarioVariableFactory flowDirVarFactory = new FlowDirectionScenarioVariableFactory(scenarios);

    for (Scenario scenario : scenarios) {
      for (Edge edge : model.getEdges()) {
        if (edge.getConnections(NaturalGasConnection.class).size() <= 1) {
          continue;
        }

        // post a constraint for each connection
        for (NaturalGasConnection c1 : edge.getConnections(NaturalGasConnection.class)) {
          int count = 0;
          String name1 = getConstraintName(scenario, c1, FORWARD_PREFIX);
          String name2 = getConstraintName(scenario, c1, REVERSE_PREFIX);

          LinearConstraint constraint1 = new LinearConstraintEquals(name1);
          LinearConstraint constraint2 = new LinearConstraintEquals(name2);
          constraint1.setRightHandSide(0.0);
          constraint2.setRightHandSide(0.0);

          for (NaturalGasConnection c2 : edge.getConnections(NaturalGasConnection.class)) {
            Variable betaij = flowDirVarFactory.getVariable(problem, c2, scenario, FlowDirectionScenarioVariableFactory.FORWARD_PREFIX);
            Variable betaji = flowDirVarFactory.getVariable(problem, c2, scenario, FlowDirectionScenarioVariableFactory.REVERSE_PREFIX);

            if (betaij == null) {
              continue; // disabled edge
            }

            constraint1.addVariable(betaij, 1.0);
            constraint2.addVariable(betaji, 1.0);
            ++count;
          }

          if (constraint1.getNumberOfVariables() == 1) {
            continue;
          }

          Variable betaij = flowDirVarFactory.getVariable(problem, c1, scenario, FlowDirectionScenarioVariableFactory.FORWARD_PREFIX);
          Variable betaji = flowDirVarFactory.getVariable(problem, c1, scenario, FlowDirectionScenarioVariableFactory.REVERSE_PREFIX);

          constraint1.addVariable(betaij, -(count - 1));
          constraint2.addVariable(betaji, -(count - 1));

          problem.addLinearConstraint(constraint1);
          problem.addLinearConstraint(constraint2);
        }
      }
    }
  }
}
