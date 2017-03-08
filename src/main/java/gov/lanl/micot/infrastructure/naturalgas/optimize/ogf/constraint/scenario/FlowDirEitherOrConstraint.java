package gov.lanl.micot.infrastructure.naturalgas.optimize.ogf.constraint.scenario;

import gov.lanl.micot.infrastructure.model.FlowConnection;
import gov.lanl.micot.infrastructure.model.Scenario;
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
 * This file adds the Flow-Direction either or constraint
 * 
 * @author Conrado Borraz - Extending Russell's factories
 */
public class FlowDirEitherOrConstraint implements ConstraintFactory {

  private Collection<Scenario> scenarios = new ArrayList<Scenario>();

  // Constructor
  public FlowDirEitherOrConstraint(Collection<Scenario> scenarios) {
    this.scenarios = scenarios;
  }

  // Get the constraint's name: @param scenario number, @param node, @return
  public static String getActiveGammaConstraintName(Scenario k, FlowConnection edge) {
    return "BetaLambda." + k.getIndex() + "." + edge.toString();
  }

  @Override
  public void constructConstraint(MathematicalProgram problem, NaturalGasModel model) throws VariableExistsException, NoVariableException, InvalidConstraintException {
    // Step 1: Instantiate all the variables being used in this constraint
    FlowDirectionScenarioVariableFactory NG_flowDirVarFactory = new FlowDirectionScenarioVariableFactory(scenarios);

    for (Scenario scenario : scenarios) {
//      SystemLogger.getSystemLogger().systemLogger.println("\n\n-- Adding Flow-Dir and Lambda variables CONSTRAINTS to the MODEL (Scenario [" + scenario.getIndex() + "]): " + "\n\t      \tActual \tDesired\t       \tFlowDir\tedge-Dir1\tedge-Dir2" + "\n\tEdgeID\tStatus \tStatus \t (i,j) \tVarName\tVarName\tVarName\t Status" + "\n\t------\t-------\t-------\t-------\t-------\t-------\t-------\t--------");

      // Add the constraint
      // create the non zero entries of the flow matrix
      for (FlowConnection edge : model.getFlowConnections()) {
        // Retrieve variables
        Variable betaij = NG_flowDirVarFactory.getVariable(problem, edge, scenario, FlowDirectionScenarioVariableFactory.FORWARD_PREFIX); // betaPij
        Variable betaji = NG_flowDirVarFactory.getVariable(problem, edge, scenario, FlowDirectionScenarioVariableFactory.REVERSE_PREFIX); // betaPji

        if (betaij == null) {
          continue; // edge disabled
        }

        // Linking FLOW DIRECTION AND CONSTRUCTION VARIABLES
        String ConstraintName = getActiveGammaConstraintName(scenario, edge);
        LinearConstraint C1 = new LinearConstraintEquals(ConstraintName);

        // Eq. 1: betaij + betaji = lambda
        C1.addVariable(betaij, 1.0);
        C1.addVariable(betaji, 1.0);
        C1.setRightHandSide(1.0);
        problem.addLinearConstraint(C1);

  //      SystemLogger.getSystemLogger().systemLogger.println("\t " + edge + "\t" + edge.getActualStatus() + "\t" + edge.getDesiredStatus() + "\t(" + model.getFirstNode(edge) + "," + model.getSecondNode(edge) + ")" + "\t" + betaij + "\t" + betaji + "\t" + "Added!");
      }
    }
  }

}
