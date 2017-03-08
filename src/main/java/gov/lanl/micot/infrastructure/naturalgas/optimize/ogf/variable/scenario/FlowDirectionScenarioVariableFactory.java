package gov.lanl.micot.infrastructure.naturalgas.optimize.ogf.variable.scenario;

import gov.lanl.micot.infrastructure.model.FlowConnection;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasConnection;
import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasModel;
import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasNode;
import gov.lanl.micot.infrastructure.naturalgas.model.ShortPipe;
import gov.lanl.micot.infrastructure.naturalgas.model.Valve;
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
 * General class for creating flow direction variables associated with scenarios
 * @author Conrado - Sep 2014 - Extending Russell's factory
 *  Note 1: To invoke the variable: (scenario, edgeID, prefix={"", ".R"})
 */

public class FlowDirectionScenarioVariableFactory implements VariableFactory {
	private Collection<Scenario> scenarios = new ArrayList<Scenario>();

	public static final String FORWARD_PREFIX = "";
  public static final String REVERSE_PREFIX = ".R";
	
	// Constructor: @param models
	public FlowDirectionScenarioVariableFactory(Collection<Scenario> scenarios) {
		this.scenarios = scenarios;
	}
	
	// Get the flow direction (beta) variable name: @param scenario, edge, @return
	public String getVariableName(Scenario k, FlowConnection edge, String prefix) {
	  if (prefix != FORWARD_PREFIX && prefix != REVERSE_PREFIX) {
	    throw new RuntimeException("Error: Incorrect prefix for FlowDirectionScenarioVariableFactory " + prefix);
	  }	  
		return "Beta." + k.getIndex() + "." + edge.toString() + prefix;
	}
    
	@Override
	public Collection<Variable> createVariables(MathematicalProgram program, NaturalGasModel model) throws VariableExistsException, InvalidVariableException {
		ArrayList<Variable> variables = new ArrayList<Variable>();
		
		Set<NaturalGasConnection> connections = new HashSet<NaturalGasConnection>();
		connections.addAll(model.getPipes());
    connections.addAll(model.getCompressors());
    connections.addAll(model.getResistors());
    connections.addAll(model.getControlValves());
    connections.addAll(model.getValves());
    connections.addAll(model.getShortPipes());
		
		for (Scenario scenario : scenarios) {
//			SystemLogger.getSystemLogger().systemLogger.println("\n-- Create FlowDirection Scenario variables: " + "\n\t # \t Pipe  \t Flow  \t    VARIABLE " + "\n\t---\t-------\t-------\t---------------");
			for (NaturalGasConnection edge : connections) {
	//			SystemLogger.getSystemLogger().systemLogger.println("\t" + edge +"\t(" + model.getFirstNode(edge) + "," + model.getSecondNode(edge) + ")\t " + edge.getFlow()); 
				if (scenario.computeActualStatus(edge, true)) { // For scenario-based variables and constraints used this.				  
					String betaPij = getVariableName(scenario, edge, FORWARD_PREFIX);
					String betaPji = getVariableName(scenario, edge, REVERSE_PREFIX);					
					variables.add(program.makeDiscreteVariable(betaPij));
					variables.add(program.makeDiscreteVariable(betaPji));
		//			SystemLogger.getSystemLogger().systemLogger.println("\t '" + betaPij + " & " + betaPji + "' ADDED!");
				}
			}
		}
		//SystemLogger.getSystemLogger().systemLogger.println("");
		return variables;
	}

	@Override
	public Variable getVariable(MathematicalProgram program, Object asset) throws NoVariableException {
		throw new NoVariableException(asset.toString());
	}

	// Get a variable: @param program, @param asset, @param scenario, @return @throws NoVariableException
	public Variable getVariable(MathematicalProgram program, Object asset, Scenario scenario, String prefix) throws NoVariableException {
		if (asset instanceof FlowConnection) {
			return program.getVariable(getVariableName(scenario, (FlowConnection) asset, prefix));
		}     
		return null;
	}
}
