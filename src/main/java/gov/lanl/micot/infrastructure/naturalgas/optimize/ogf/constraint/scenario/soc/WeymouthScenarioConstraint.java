package gov.lanl.micot.infrastructure.naturalgas.optimize.ogf.constraint.scenario.soc;

import gov.lanl.micot.infrastructure.model.FlowConnection;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasConnection;
import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasModel;
import gov.lanl.micot.infrastructure.naturalgas.optimize.ConstraintFactory;
import gov.lanl.micot.infrastructure.naturalgas.optimize.ogf.variable.scenario.FlowScenarioVariableFactory;
import gov.lanl.micot.infrastructure.naturalgas.optimize.ogf.variable.scenario.soc.RelaxedFlowScenarioVariableFactory;
import gov.lanl.micot.util.math.solver.QuadraticConstraint;
import gov.lanl.micot.util.math.solver.QuadraticConstraintGreaterEq;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.InvalidConstraintException;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

/**
 * @author Conrado Borraz - Extending Russell's factories This file adds the SOC-type Weymouth equation constraints to the gas problem
 */
public class WeymouthScenarioConstraint implements ConstraintFactory {

  private Collection<Scenario> scenarios = new ArrayList<Scenario>();

  // Constructor
  public WeymouthScenarioConstraint(Collection<Scenario> scenarios) {
    this.scenarios = scenarios;
  }

  // Get the name for the mass flow balance constraint: @param scenario number, @param node, @return
  public static String getConstraintName(Scenario k, FlowConnection edge) {
    return "SOC_WE" + "." + k.getIndex() + "." + edge.toString();
  }

  @Override
  public void constructConstraint(MathematicalProgram problem, NaturalGasModel model) throws VariableExistsException, NoVariableException, InvalidConstraintException {
    // Step 1: Instantiate all the variables being used in this constraint

    // Get the connections where the Weymouth constraint is applied
    HashSet<NaturalGasConnection> weymouthConnections = new HashSet<NaturalGasConnection>();
    weymouthConnections.addAll(model.getPipes());
    weymouthConnections.addAll(model.getResistors());

    for (Scenario scenario : scenarios) {

      for (NaturalGasConnection edge : weymouthConnections) {
        QuadraticConstraint WE = createConstraint(scenario, edge, problem);
        problem.addQuadraticConstraint(WE);
      }
    }
  }

  protected QuadraticConstraint createConstraint(Scenario scenario, NaturalGasConnection edge, MathematicalProgram problem) throws NoVariableException {
    FlowScenarioVariableFactory flowVarFactory = new FlowScenarioVariableFactory(scenarios);
    RelaxedFlowScenarioVariableFactory relaxedVarFactory = new RelaxedFlowScenarioVariableFactory(scenarios);
    String name = getConstraintName(scenario, edge);

    QuadraticConstraint WE = new QuadraticConstraintGreaterEq(name);

    // Eq. a: l_ij \geq w_hat*x_ij^2

    // Get flow variable
    Variable xij = flowVarFactory.getVariable(problem, edge, scenario);
    // double w_hat = 1 / edge.getResistance(); // Inverse of the resistanceFactor
    double w = edge.getResistance(); // Inverse of the resistanceFactor

    // Get SOC-flow variable
    Variable lij = relaxedVarFactory.getVariable(problem, edge, scenario);

    // The convex WE is based on a SOC approximation
    // Testing:
    // Eq. a: lambdaP_ij * l_ij - w_hat*x_ij^2 \geq 0 WE.addVariables(lambdaPij, Lij, 1.0);
    // Adding a bilinear term for the SOC-flows
    
    // this conditional statement is an attempt to manage numerical issues.
    if (w >= 1e8) {    
      WE.addVariable(lij, 1.0); // Adding a linear term of SOC-flows
      WE.addVariables(xij, xij, -1.0 / w); // Adding a quadratic term of flows of this constraint
    }
    else {
      WE.addVariable(lij, w); // Adding a linear term of SOC-flows
      WE.addVariables(xij, xij, -1.0); // Adding a quadratic term of flows of this constraint
    }
    WE.setRightHandSide(0);

//    System.out.println(w);

    
    return WE;
  }

}
