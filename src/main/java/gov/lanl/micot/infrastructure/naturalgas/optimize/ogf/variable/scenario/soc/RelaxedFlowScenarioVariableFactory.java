package gov.lanl.micot.infrastructure.naturalgas.optimize.ogf.variable.scenario.soc;

import gov.lanl.micot.infrastructure.model.FlowConnection;
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

/**
 * @author Conrado Borraz, extending Russell's factory
 * This factory creates flow variable relaxations used in second order cone formulations 
 * 
 */
public class RelaxedFlowScenarioVariableFactory implements VariableFactory {
  private Collection<Scenario> scenarios = new ArrayList<Scenario>();

  // Constructor: @param models
  public RelaxedFlowScenarioVariableFactory(Collection<Scenario> scenarios) {
    this.scenarios = scenarios;
  }

  // Get the SOC flow variable name: @param edge, @return
  public String getVariableName(Scenario k, FlowConnection edge) {
    return "Lij." + k.getIndex() + "." + edge.toString();
  }

  @Override
  public Collection<Variable> createVariables(MathematicalProgram program, NaturalGasModel model) throws VariableExistsException, InvalidVariableException {
    ArrayList<Variable> variables = new ArrayList<Variable>();
    for (Scenario scenario : scenarios) {
      //SystemLogger.getSystemLogger().systemLogger.println("\n-- Create SOC-FlowScenario variables: \n\t # \t Pipe  \t Flow  \t    VARIABLE \n\t---\t-------\t-------\t---------------");
      for (FlowConnection edge : model.getFlowConnections()) {
        if (scenario.computeActualStatus(edge, true)) { // For scenario-based variables and constraints used this.
          String varName = getVariableName(scenario, edge);
          variables.add(program.makeContinuousVariable(varName));
        //  SystemLogger.getSystemLogger().systemLogger.println("\t" + edge + "\t(" + model.getFirstNode(edge) + "," + model.getSecondNode(edge) + ")\t " + edge.getFlow() + "\t '" + varName + "' ADDED!");
        }
      }
    }
//    SystemLogger.getSystemLogger().systemLogger.println("");
    return variables;
  }

  @Override
  public Variable getVariable(MathematicalProgram program, Object asset) throws NoVariableException {
    throw new NoVariableException(asset.toString());
  }

  // Get a variable: @param program, @param asset, @param scenario, @return, @throws NoVariableException
  public Variable getVariable(MathematicalProgram program, Object asset, Scenario scenario) throws NoVariableException {
    if (asset instanceof NaturalGasConnection) {
      return program.getVariable(getVariableName(scenario, (NaturalGasConnection) asset));
    }
    return null;
  }
}
