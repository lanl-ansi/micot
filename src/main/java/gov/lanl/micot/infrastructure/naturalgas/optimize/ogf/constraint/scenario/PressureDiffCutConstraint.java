package gov.lanl.micot.infrastructure.naturalgas.optimize.ogf.constraint.scenario;

import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.naturalgas.model.Junction;
import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasConnection;
import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasModel;
import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasNode;
import gov.lanl.micot.infrastructure.naturalgas.optimize.ConstraintFactory;
import gov.lanl.micot.infrastructure.naturalgas.optimize.ogf.variable.scenario.FlowDirectionScenarioVariableFactory;
import gov.lanl.micot.infrastructure.naturalgas.optimize.ogf.variable.scenario.PressureScenarioVariableFactory;
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

	/**	
	 * Adds a cut on the pressure difference based on the flow direction
	 * variables  
	 */

public class PressureDiffCutConstraint implements ConstraintFactory {

	private Collection<Scenario> scenarios = new ArrayList<Scenario>();

	// Constructor: @param scenario collection
	public PressureDiffCutConstraint(Collection<Scenario> scenarios) {
		this.scenarios = scenarios;
	}
	
	private String getLEName(Scenario scenario, NaturalGasConnection connection) {
	  return "PressDiffCutLE." + scenario.getIndex() + "." + connection.toString();
	}

	private String getGEName(Scenario scenario, NaturalGasConnection connection) {
	  return "PressDiffCutGE." + scenario.getIndex() + "." + connection.toString();
	}
	
	// Constraint
	@Override
	public void constructConstraint(MathematicalProgram problem, NaturalGasModel model) throws VariableExistsException, NoVariableException, InvalidConstraintException {
		// Step 1: Instantiate all the variables being used in this constraint
		FlowDirectionScenarioVariableFactory dirFactory =  new FlowDirectionScenarioVariableFactory(scenarios);
		PressureScenarioVariableFactory pressureFactory = new PressureScenarioVariableFactory(scenarios);

		Collection<NaturalGasConnection> connections = new HashSet<NaturalGasConnection>();
		connections.addAll(model.getPipes());
    connections.addAll(model.getResistors());
		
		// Step 2: Add the scenario-based constraint by looping over the set of specific scenarios 
		for (Scenario scenario: scenarios) {
			for (NaturalGasConnection edge : connections) {
				Variable betaij = dirFactory.getVariable(problem, edge, scenario, FlowDirectionScenarioVariableFactory.FORWARD_PREFIX);
        Variable betaji = dirFactory.getVariable(problem, edge, scenario, FlowDirectionScenarioVariableFactory.REVERSE_PREFIX);

        NaturalGasNode ni = model.getFirstNode(edge);
        NaturalGasNode nj = model.getSecondNode(edge);
        Junction ji = ni.getJunction();
        Junction jj = nj.getJunction();
        
        double pdMax = ji.getMaximumPressure() * ji.getMaximumPressure() - jj.getMinimumPressure() * jj.getMinimumPressure();
        double pdMin = ji.getMinimumPressure() * ji.getMinimumPressure() - jj.getMaximumPressure() * jj.getMaximumPressure();
        
        
        Variable pi = pressureFactory.getVariable(problem, ni, scenario);
        Variable pj = pressureFactory.getVariable(problem, nj, scenario);
               
        if (betaij == null) {
          continue; // edge is disabled
        }
        
        LinearConstraint ge = new LinearConstraintGreaterEq(getGEName(scenario,edge));
        ge.addVariable(pi, 1.0);
        ge.addVariable(pj, -1.0);
        ge.addVariable(betaij, pdMin);
        ge.setRightHandSide(pdMin);
        problem.addLinearConstraint(ge);

        LinearConstraint le = new LinearConstraintLessEq(getLEName(scenario,edge));
        le.addVariable(pi, 1.0);
        le.addVariable(pj, -1.0);
        le.addVariable(betaji, pdMax);
        le.setRightHandSide(pdMax);
        problem.addLinearConstraint(le);
			}				
		}
	}
}
