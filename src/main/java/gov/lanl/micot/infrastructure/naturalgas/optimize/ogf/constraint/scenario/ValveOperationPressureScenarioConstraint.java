package gov.lanl.micot.infrastructure.naturalgas.optimize.ogf.constraint.scenario;

import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.naturalgas.model.Junction;
import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasConnection;
import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasModel;
import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasNode;
import gov.lanl.micot.infrastructure.naturalgas.optimize.ConstraintFactory;
import gov.lanl.micot.infrastructure.naturalgas.optimize.ogf.variable.scenario.PressureScenarioVariableFactory;
import gov.lanl.micot.infrastructure.naturalgas.optimize.ogf.variable.scenario.ValveScenarioVariableFactory;
import gov.lanl.micot.util.math.solver.LinearConstraint;
import gov.lanl.micot.util.math.solver.LinearConstraintGreaterEq;
import gov.lanl.micot.util.math.solver.LinearConstraintLessEq;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.InvalidConstraintException;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * This is the constraint for operating a valve
 * 
 * @author Russell Bent
 */
public class ValveOperationPressureScenarioConstraint implements ConstraintFactory {

  private Collection<Scenario> scenarios = new ArrayList<Scenario>();

  // Constructor
  public ValveOperationPressureScenarioConstraint(Collection<Scenario> scenarios) {
    this.scenarios = scenarios;
  }

  /**
   * Pressure <= constraint
   * @param k
   * @param edge
   * @return
   */
  private String getPressureGEConstraintName(Scenario k, NaturalGasConnection edge) {
    return "PressureGE" + "." + k.getIndex() + "." + edge;
  }

  /**
   * Pressure >= constraint
   * @param k
   * @param edge
   * @return
   */
  private String getPressureLEConstraintName(Scenario k, NaturalGasConnection edge) {
    return "PressureLE" + "." + k.getIndex() + "." + edge;
  }

  @Override
  public void constructConstraint(MathematicalProgram problem, NaturalGasModel model) throws VariableExistsException, NoVariableException, InvalidConstraintException {
    ValveScenarioVariableFactory valveFactory = new ValveScenarioVariableFactory(scenarios);
    PressureScenarioVariableFactory pressureFactory = new PressureScenarioVariableFactory(scenarios);
    
    Set<NaturalGasConnection> connections = new HashSet<NaturalGasConnection>();
    connections.addAll(model.getValves());
    
    for (Scenario scenario : scenarios) {
      for (NaturalGasConnection valve : connections) {
        NaturalGasNode ni = model.getFirstNode(valve);
        NaturalGasNode nj = model.getSecondNode(valve);
        Junction i = ni.getJunction();
        Junction j = nj.getJunction();
        
        Variable valveOperations = valveFactory.getVariable(problem, valve, scenario);
        Variable pi = pressureFactory.getVariable(problem, ni, scenario);
        Variable pj = pressureFactory.getVariable(problem, nj, scenario);
                
        double maxPressureJ = j.getMaximumPressure() * j.getMaximumPressure();
        double maxPressureI = i.getMaximumPressure() * i.getMaximumPressure();

        // pj <= pi + (1 - valve) * maxPressureJ
        LinearConstraint leConstraint2 = new LinearConstraintLessEq(getPressureLEConstraintName(scenario, valve));
        leConstraint2.addVariable(pj, 1.0);
        leConstraint2.addVariable(pi, -1.0);
        leConstraint2.addVariable(valveOperations, maxPressureJ);
        leConstraint2.setRightHandSide(maxPressureJ);
        problem.addLinearConstraint(leConstraint2);

        // pj >= pi - (1-valve)maxPressureI
        LinearConstraint geConstraint = new LinearConstraintGreaterEq( getPressureGEConstraintName(scenario, valve));
        geConstraint.addVariable(pj, 1.0);
        geConstraint.addVariable(pi, -1.0);
        geConstraint.addVariable(valveOperations, -maxPressureI);
        geConstraint.setRightHandSide(-maxPressureI);
        problem.addLinearConstraint(geConstraint);                
      }      
    }
  }
}




            
