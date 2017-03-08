package gov.lanl.micot.infrastructure.naturalgas.optimize.ogf.constraint.scenario;

import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.naturalgas.model.CityGate;
import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasConnection;
import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasModel;
import gov.lanl.micot.infrastructure.naturalgas.model.Pipe;
import gov.lanl.micot.infrastructure.naturalgas.model.Resistor;
import gov.lanl.micot.infrastructure.naturalgas.optimize.ConstraintFactory;
import gov.lanl.micot.infrastructure.naturalgas.optimize.ogf.constraint.ConstraintUtility;
import gov.lanl.micot.infrastructure.naturalgas.optimize.ogf.variable.scenario.FlowScenarioVariableFactory;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

import java.util.ArrayList;
import java.util.Collection;

	/**	Bounds on the scenario-based flow variables: [-totalGasDemand, totalGasDemand]
	 *	@author Conrado, extending Russell's factory
	 *  Note: A 'FlowScenarioVariableFactory.java' file already exists for the 
	 *        power grid problem implementations. 
	 *        -- Similarly this can be observed with many other files. 
	 *        -- Hence, a 'NG_' prefix is appended to those java files 
	 *           pertaining to the gas system project.
	 */

public class FlowScenarioBoundConstraint implements ConstraintFactory {

	private Collection<Scenario> scenarios = new ArrayList<Scenario>();

	// Constructor: @param scenario collection
	public FlowScenarioBoundConstraint(Collection<Scenario> scenarios) {
		this.scenarios = scenarios;
	}
	
	// Constraint
	@Override
	public void constructConstraint(MathematicalProgram problem, NaturalGasModel model) throws VariableExistsException, NoVariableException {
		// Step 1: Instantiate all the variables being used in this constraint
		FlowScenarioVariableFactory NG_flowVarFactory =  new FlowScenarioVariableFactory(scenarios);
		
		double totalGasDemand = 0.0;
		for (CityGate n1: model.getCityGates()) {
			totalGasDemand += n1.getMaximumConsumption().doubleValue();
		}

		// Step 2: Add the scenario-based constraint by looping over the set of specific scenarios 
		for (Scenario scenario: scenarios) {
			for (NaturalGasConnection edge : model.getFlowConnections()) {
				// Get flow variable
				Variable xij = NG_flowVarFactory.getVariable(problem, edge, scenario);
				
				// in case the edge is disabled
				if (xij != null) {
				  if (edge instanceof Pipe || edge instanceof Resistor) {
				    double ub = ConstraintUtility.computeflowUpperBound(model, edge, totalGasDemand);
            double lb = ConstraintUtility.computeflowLowerBound(model, edge, totalGasDemand);				    				    
            problem.addBounds(xij, lb, ub);				    
  			  }
				  else {
				    problem.addBounds(xij, -totalGasDemand, totalGasDemand);
				  }
				}
			}				
		}
	}
}
