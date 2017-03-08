package gov.lanl.micot.infrastructure.naturalgas.optimize.ogf.constraint.scenario;

import gov.lanl.micot.infrastructure.model.FlowConnection;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.naturalgas.model.CityGate;
import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasModel;
import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasNode;
import gov.lanl.micot.infrastructure.naturalgas.model.Well;
import gov.lanl.micot.infrastructure.naturalgas.optimize.ConstraintFactory;
import gov.lanl.micot.infrastructure.naturalgas.optimize.ogf.variable.scenario.FlowScenarioVariableFactory;
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
 * This file adds mass flow balance constraints to the GTNEP model -- GTNEP = Natural Gas Transmission Network Expansion Planning
 * 
 * This assumes that load and generation are constants
 * 
 * @author Conrado Borraz - Extending Russell's factories
 */
public class FlowBalanceScenarioConstraint implements ConstraintFactory {

  private Collection<Scenario> scenarios = new ArrayList<Scenario>();

  // Constructor
  public FlowBalanceScenarioConstraint(Collection<Scenario> scenarios) {
    this.scenarios = scenarios;
  }

  // Get the name for the mass flow balance constraint: @param scenario number, @param node, @return
  public static String getMassFlowBalanceConstraintName(Scenario k, NaturalGasNode node) {
    return "BAL." + k.getIndex() + "." + node.toString();
  }

  // Function for adding the mass flow balance constraint: @param program, @param model, @throws NoVariableException
  @Override
  public void constructConstraint(MathematicalProgram problem, NaturalGasModel model) throws VariableExistsException, NoVariableException, InvalidConstraintException {
    // Step 1: Instantiate all the variables being used in this constraint
    FlowScenarioVariableFactory NG_flowVariable = new FlowScenarioVariableFactory(scenarios);

    double totalLoad = 0.0;
    for (Scenario scenario : scenarios) {
//      SystemLogger.getSystemLogger().systemLogger.println("\n\n-- Adding MassFlowBalanceScenario CONSTRAINTS to the MODEL (Scenario [" + scenario.getIndex() + "]): " + "\n\t       \t Node  \t Const.\t Node  \t       \t      \tActual \tDesired\t       \t       \t    Flow    \t VAR  " + "\n\t Node  \t Name  \t Name  \t Type  \t Load  \tEdgeID\tStatus \tStatus \t (i,j) \t Match \t  Variable  \tSTATUS" + "\n\t-------\t-------\t-------\t-------\t-------\t------\t-------\t-------\t-------\t-------\t------------\t------");

      // Add the constraint
      for (NaturalGasNode node : model.getNodes()) {
        String name = getMassFlowBalanceConstraintName(scenario, node);
        String nodetype = "InterCo";
        LinearConstraint constraint = new LinearConstraintEquals(name);
        double load = 0.0;

        Collection<CityGate> gates = node.getComponents(CityGate.class);
        Collection<Well> wells = node.getComponents(Well.class);
        if (gates.size() == 0 && wells.size() > 0) {
          nodetype = "Source";
        }
        if (gates.size() > 0 && wells.size() == 0) {
          nodetype = "Sink";
        }

        for (Well well : wells) {
          load += well.getActualProduction().doubleValue();
        }

        for (CityGate gate : gates) {
          load -= gate.getActualConsumption().doubleValue();
        }

        totalLoad += load;

        // create the non zero entries of the flow matrix
        // LeftHandSide variables: Sum of incoming and outgoing flows towards and from current node
        for (FlowConnection edge : model.getFlowConnections(node)) {
          NaturalGasNode firstNode = model.getFirstNode(edge);
          NaturalGasNode secondNode = model.getSecondNode(edge);

          double sign = 1.0;
          if (secondNode.equals(node)) {
            sign = sign * (-1.0);
          }

          Variable xij = NG_flowVariable.getVariable(problem, edge, scenario);
          constraint.addVariable(xij, sign);
  //        SystemLogger.getSystemLogger().systemLogger.print("\n\t" + node + "\t");
    //      if (node.getJunction().getName().length() > 7) {
      //      SystemLogger.getSystemLogger().systemLogger.print(node.getJunction().getName().substring(0, 7));
        //  } else {
          //  SystemLogger.getSystemLogger().systemLogger.print(node.getJunction().getName());
         // }
          //SystemLogger.getSystemLogger().systemLogger.println("\t" + name + "\t" + nodetype + "\t" + load + "\n\t\t\t\t\t\t" + edge + "\t" + edge.getActualStatus() + "\t" + edge.getDesiredStatus() + "\t(" + firstNode + "," + secondNode + ")" + "\t j=[" + secondNode + "]\t" + "(" + sign + ")X." + scenario.getIndex() + "." + edge.toString() + "\tAdded!");
        }

        constraint.setRightHandSide(load); // RightHandSide: Load
        problem.addLinearConstraint(constraint);
      }
//      SystemLogger.getSystemLogger().systemLogger.println("\n\t-- TOTAL LOAD = [" + totalLoad + "]\n");
    }
  }
}
