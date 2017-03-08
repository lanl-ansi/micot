package gov.lanl.micot.infrastructure.naturalgas.optimize.ogf.constraint.scenario;

import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasModel;
import gov.lanl.micot.infrastructure.naturalgas.model.Valve;
import gov.lanl.micot.infrastructure.naturalgas.optimize.ConstraintFactory;
import gov.lanl.micot.infrastructure.naturalgas.optimize.ogf.variable.scenario.ValveScenarioVariableFactory;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

import java.util.ArrayList;
import java.util.Collection;

	/**	Bounds on thevalve variables: [0, 1]
	 *	@author Conrado, extending Russell's factory
	 */

public class ValveScenarioBoundConstraint implements ConstraintFactory {

	private Collection<Scenario> scenarios = new ArrayList<Scenario>();

	// Constructor: @param scenario collection
	public ValveScenarioBoundConstraint(Collection<Scenario> scenarios) {
		this.scenarios = scenarios;
	}
	
	// Constraint
	@Override
	public void constructConstraint(MathematicalProgram problem, NaturalGasModel model) throws VariableExistsException, NoVariableException {
		// Step 1: Instantiate all the variables being used in this constraint
		ValveScenarioVariableFactory factory = new ValveScenarioVariableFactory(scenarios);					

		// Step 2: Add the scenario-based constraint by looping over the set of specific scenarios 
		for (Scenario scenario : scenarios) {
		  for (Valve valve : model.getValves()) {
        Variable variable = factory.getVariable(problem, valve, scenario);  
        if (variable == null) {
          continue; // in case the edge is disabled
        }      
        problem.addBounds(variable, 0, 1);
			}
		}
	}
	
}


