package gov.lanl.micot.infrastructure.naturalgas.optimize.ogf.variable.scenario;

import gov.lanl.micot.infrastructure.model.FlowConnection;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasConnection;
import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasModel;
import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasNode;
import gov.lanl.micot.infrastructure.naturalgas.optimize.VariableFactory;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.InvalidVariableException;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

import java.util.ArrayList;
import java.util.Collection;

/**
 * General class for creating flow variables associated with
 * scenarios. In the papers this shows up as the x variable
 * 
 * @author Russell Bent Slightly modified by Conrado -  */

public class FlowScenarioVariableFactory implements VariableFactory {
  private Collection<Scenario> scenarios = new ArrayList<Scenario>();

  // Constructor: @param models
  public FlowScenarioVariableFactory(Collection<Scenario> scenarios) {
    this.scenarios = scenarios;
  }

  // Get the phase angle variable name: @param edge, @return
  public String getVariableName(Scenario k, FlowConnection edge) {
    return "X." + k.getIndex() + "." + edge.toString();
  }

  @Override
  public Collection<Variable> createVariables(MathematicalProgram program, NaturalGasModel model) throws VariableExistsException, InvalidVariableException {
    ArrayList<Variable> variables = new ArrayList<Variable>();
    for (Scenario scenario : scenarios) {
//      SystemLogger.getSystemLogger().algorithmLogger.println("\n-- Create FlowScenario variables: " + "\n\t # \t Pipe  \t Flow  \t    VARIABLE " + "\n\t---\t-------\t-------\t---------------");
      for (FlowConnection edge : model.getFlowConnections()) {
        NaturalGasNode node1 = model.getFirstNode(edge);
        NaturalGasNode node2 = model.getSecondNode(edge);
        if (scenario.computeActualStatus(edge, true)) { 
          String VarName = getVariableName(scenario, edge);
          variables.add(program.makeContinuousVariable(VarName));
  //        SystemLogger.getSystemLogger().algorithmLogger.println("\t" + edge + "\t(" + node1 + "," + node2 + ")\t " + edge.getFlow() + "\t '" + VarName + "' ADDED!");
        }
      }
    }
    //SystemLogger.getSystemLogger().algorithmLogger.println("");
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
