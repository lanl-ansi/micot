package gov.lanl.micot.infrastructure.naturalgas.optimize.ogf.constraint.scenario;

import gov.lanl.micot.infrastructure.model.FlowConnection;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasModel;
import gov.lanl.micot.infrastructure.naturalgas.optimize.ConstraintFactory;
import gov.lanl.micot.infrastructure.naturalgas.optimize.ogf.variable.scenario.FlowDirectionScenarioVariableFactory;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

import java.util.ArrayList;
import java.util.Collection;

	/**	Bounds on the flow direction scenario variables: [0, 1]
	 *	@author Conrado, extending Russell's factory
	 *  Note: Since similarly java file names might exist for the power grid problem implementations. 
	 *        -- A 'NG_' prefix is appended to those java files pertaining to the gas system project.
	 */

public class FlowDirScenarioBoundConstraint implements ConstraintFactory {

	private Collection<Scenario> scenarios = new ArrayList<Scenario>();

	// Constructor: @param scenario collection
	public FlowDirScenarioBoundConstraint(Collection<Scenario> scenarios) {
		this.scenarios = scenarios;
	}
	
	// Constraint
	@Override
	public void constructConstraint(MathematicalProgram problem, NaturalGasModel model) throws VariableExistsException, NoVariableException {
		// Step 1: Instantiate all the variables being used in this constraint
		FlowDirectionScenarioVariableFactory nG_flowDirVarFactory = new FlowDirectionScenarioVariableFactory(scenarios);					

		// Step 2: Add the scenario-based constraint by looping over the set of specific scenarios 
		for (Scenario scenario : scenarios) {
		  for (FlowConnection edge: model.getFlowConnections()) {
        Variable betaij = nG_flowDirVarFactory.getVariable(problem, edge, scenario, FlowDirectionScenarioVariableFactory.FORWARD_PREFIX);    // betaPij 
        Variable betaji = nG_flowDirVarFactory.getVariable(problem, edge, scenario, FlowDirectionScenarioVariableFactory.REVERSE_PREFIX);  // betaPji
        
        if (betaij == null) {
          continue; // in case the edge is disabled
        }
        
        problem.addBounds(betaij, 0, 1);
        problem.addBounds(betaji, 0, 1);
			}
		}
	}
	
}


