package gov.lanl.micot.infrastructure.naturalgas.optimize.ogf.constraint.scenario;

import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasModel;
import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasNode;
import gov.lanl.micot.infrastructure.naturalgas.optimize.ConstraintFactory;
import gov.lanl.micot.infrastructure.naturalgas.optimize.ogf.variable.scenario.PressureScenarioVariableFactory;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Bounds on the scenario-based pressure variables: [200, 1000] psig
 *
 * @author Conrado, extending Russell's factory
 */

public class PressureScenarioBoundConstraint implements ConstraintFactory {

  private Collection<Scenario> scenarios = new ArrayList<Scenario>();

  // Constructor: @param scenario collection
  public PressureScenarioBoundConstraint(Collection<Scenario> scenarios) {
    this.scenarios = scenarios;
  }

  // Constraint
  @Override
  public void constructConstraint(MathematicalProgram problem, NaturalGasModel model) throws VariableExistsException, NoVariableException {
    // Step 1: Instantiate all the variables being used in this constraint
    PressureScenarioVariableFactory NG_pressureVariable = new PressureScenarioVariableFactory(scenarios);

    // Step 2: Add the scenario-based constraint by looping over the set of specific scenarios
    for (Scenario scenario : scenarios)
      for (NaturalGasNode i : model.getNodes()) {
        Variable pi = NG_pressureVariable.getVariable(problem, i, scenario);
        if (pi != null) {
          double Pmin = Math.pow(i.getJunction().getMinimumPressure(), 2);
          double Pmax = Math.pow(i.getJunction().getMaximumPressure(), 2);
          problem.addBounds(pi, Pmin, Pmax);
        }
      }
  }
}
