package gov.lanl.micot.infrastructure.naturalgas.optimize.ogf.constraint.scenario.soc;

import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.naturalgas.model.CityGate;
import gov.lanl.micot.infrastructure.naturalgas.model.Junction;
import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasConnection;
import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasModel;
import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasNode;
import gov.lanl.micot.infrastructure.naturalgas.model.Pipe;
import gov.lanl.micot.infrastructure.naturalgas.optimize.ConstraintFactory;
import gov.lanl.micot.infrastructure.naturalgas.optimize.ogf.variable.scenario.soc.RelaxedFlowScenarioVariableFactory;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Bounds on the relaxed flow variables
 * 
 * @author Russell Bent
 * 
 */

public class RelaxedFlowScenarioBoundConstraint implements ConstraintFactory {

  private Collection<Scenario> scenarios = new ArrayList<Scenario>();

  // Constructor: @param scenario collection
  public RelaxedFlowScenarioBoundConstraint(Collection<Scenario> scenarios) {
    this.scenarios = scenarios;
  }

  // Constraint
  @Override
  public void constructConstraint(MathematicalProgram problem, NaturalGasModel model) throws VariableExistsException, NoVariableException {
    // Step 1: Instantiate all the variables being used in this constraint
    RelaxedFlowScenarioVariableFactory factory = new RelaxedFlowScenarioVariableFactory(scenarios);
    Set<NaturalGasConnection> weymouthConnections = new HashSet<NaturalGasConnection>();
    weymouthConnections.addAll(model.getPipes());
    weymouthConnections.addAll(model.getResistors());
    
    double maxFlow = 0;
    for (CityGate gate : model.getCityGates()) {
      maxFlow += gate.getMaximumConsumption().doubleValue();
    }
    maxFlow = maxFlow * maxFlow;

    // Step 2: Add the scenario-based constraint by looping over the set of specific scenarios
    for (Scenario scenario : scenarios) {
      for (NaturalGasConnection edge : weymouthConnections) {
        // Get flow variable
        Variable lij = factory.getVariable(problem, edge, scenario);
        if (lij == null) {
          continue; // could be disabled
        }

        double resistance = edge.getResistance();
        
        NaturalGasNode firstNode = model.getFirstNode(edge);
        NaturalGasNode secondNode = model.getSecondNode(edge);
        Junction i = firstNode.getJunction();
        Junction j = secondNode.getJunction();

        double iMinPressure = i.getMinimumPressure() * i.getMinimumPressure();
        double jMinPressure = j.getMinimumPressure() * j.getMinimumPressure();
        double iMaxPressure = i.getMaximumPressure() * i.getMaximumPressure();
        double jMaxPressure = j.getMaximumPressure() * j.getMaximumPressure();

        double lb = Math.min(jMinPressure - iMaxPressure, iMinPressure - jMaxPressure);
        lb = Math.max(0, lb);

        double ub = Math.max(iMaxPressure - jMinPressure, jMaxPressure - iMinPressure);
        if (edge instanceof Pipe) { // otherwise resistance gets too small, and the numerics get really gnarly
          ub = Math.min(ub, maxFlow / resistance);
        }

        
        problem.addBounds(lij, lb, ub);
      }
    }
  }
}
