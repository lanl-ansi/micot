package gov.lanl.micot.infrastructure.naturalgas.optimize.ogf.variable.scenario;

import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasConnection;
import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasModel;
import gov.lanl.micot.infrastructure.naturalgas.optimize.VariableFactory;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.InvalidVariableException;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * General class for creating valve on/off variables
 * 
 * 0 means the valve is closed and there is no flow
 * 
 * @author Russell Bent  
 * 
 * */

public class ValveScenarioVariableFactory implements VariableFactory {
  private Collection<Scenario> scenarios = new ArrayList<Scenario>();

  // Constructor: @param models
  public ValveScenarioVariableFactory(Collection<Scenario> scenarios) {
    this.scenarios = scenarios;
  }

  // Get the phase angle variable name: @param edge, @return
  public String getVariableName(Scenario k, NaturalGasConnection edge) {
    return "V." + k.getIndex() + "." + edge.toString();
  }

  @Override
  public Collection<Variable> createVariables(MathematicalProgram program, NaturalGasModel model) throws VariableExistsException, InvalidVariableException {
    ArrayList<Variable> variables = new ArrayList<Variable>();
    
    Set<NaturalGasConnection> connections = new HashSet<NaturalGasConnection>();
    connections.addAll(model.getValves());
    connections.addAll(model.getControlValves());
    
    for (Scenario scenario : scenarios) {
      for (NaturalGasConnection valve : connections) {
        if (scenario.computeActualStatus(valve, true)) { 
          String name = getVariableName(scenario, valve);
          variables.add(program.makeDiscreteVariable(name));
        }
      }
    }
    return variables;
  }

  @Override
  public Variable getVariable(MathematicalProgram program, Object asset) throws NoVariableException {
    throw new NoVariableException(asset.toString());
  }

  /**
   * Get a variable
   * 
   * @param program
   * @param asset
   * @param scenario
   * @return
   * @throws NoVariableException
   */
  public Variable getVariable(MathematicalProgram program, Object asset, Scenario scenario) throws NoVariableException {
    if (asset instanceof NaturalGasConnection) {
      return program.getVariable(getVariableName(scenario, (NaturalGasConnection) asset));
    }
    return null;
  }
}
