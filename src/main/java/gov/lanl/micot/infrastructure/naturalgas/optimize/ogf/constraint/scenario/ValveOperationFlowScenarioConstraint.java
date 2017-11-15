package gov.lanl.micot.infrastructure.naturalgas.optimize.ogf.constraint.scenario;

import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.naturalgas.model.CityGate;
import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasConnection;
import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasModel;
import gov.lanl.micot.infrastructure.naturalgas.optimize.ConstraintFactory;
import gov.lanl.micot.infrastructure.naturalgas.optimize.ogf.variable.scenario.FlowScenarioVariableFactory;
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
public class ValveOperationFlowScenarioConstraint implements ConstraintFactory {

  private Collection<Scenario> scenarios = new ArrayList<Scenario>();

  // Constructor
  public ValveOperationFlowScenarioConstraint(Collection<Scenario> scenarios) {
    this.scenarios = scenarios;
  }

  /**
   * Flow >= constraint
   * @param k
   * @param edge
   * @return
   */
  private String getFlowGEConstraintName(Scenario k, NaturalGasConnection edge) {
    return "ValveOperationGE" + "." + k.getIndex() + "." + edge;
  }

  /**
   * Flow <= constraint
   * @param k
   * @param edge
   * @return
   */
  private String getFlowLEConstraintName(Scenario k, NaturalGasConnection edge) {
    return "ValveOperationLE" + "." + k.getIndex() + "." + edge;
  }

  @Override
  public void constructConstraint(MathematicalProgram problem, NaturalGasModel model) throws VariableExistsException, NoVariableException, InvalidConstraintException {
    ValveScenarioVariableFactory valveFactory = new ValveScenarioVariableFactory(scenarios);
    FlowScenarioVariableFactory flowFactory = new FlowScenarioVariableFactory(scenarios);
    
    double maxFlow = 0;
    for (CityGate gate : model.getCityGates()) {
      maxFlow += gate.getMaximumConsumption().doubleValue();
    }

    Set<NaturalGasConnection> connections = new HashSet<NaturalGasConnection>();
    connections.addAll(model.getValves());
    connections.addAll(model.getControlValves());
    
    for (Scenario scenario : scenarios) {
      for (NaturalGasConnection valve : connections) {
        Variable valveOperations = valveFactory.getVariable(problem, valve, scenario);
        Variable flow = flowFactory.getVariable(problem, valve, scenario);                
        
        // f_ij <= maxFlow * valveOpertions
        LinearConstraint leconstraint = new LinearConstraintLessEq(getFlowLEConstraintName(scenario, valve));
        leconstraint.setRightHandSide(0.0);
        leconstraint.addVariable(flow, 1.0);
        leconstraint.addVariable(valveOperations, -maxFlow);        
        problem.addLinearConstraint(leconstraint);
               
        // f_ij >= -maxFlow * valveOpertions
        LinearConstraint geconstraint = new LinearConstraintGreaterEq(getFlowGEConstraintName(scenario, valve));
        geconstraint.setRightHandSide(0.0);
        geconstraint.addVariable(flow, 1.0);
        geconstraint.addVariable(valveOperations, maxFlow);        
        problem.addLinearConstraint(geconstraint);        
      }      
    }
  }
}




            
