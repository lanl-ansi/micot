package gov.lanl.micot.infrastructure.naturalgas.optimize.ogf.constraint.scenario;

import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.naturalgas.model.CityGate;
import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasConnection;
import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasModel;
import gov.lanl.micot.infrastructure.naturalgas.model.Pipe;
import gov.lanl.micot.infrastructure.naturalgas.model.Resistor;
import gov.lanl.micot.infrastructure.naturalgas.optimize.ConstraintFactory;
import gov.lanl.micot.infrastructure.naturalgas.optimize.ogf.constraint.ConstraintUtility;
import gov.lanl.micot.infrastructure.naturalgas.optimize.ogf.variable.scenario.FlowDirectionScenarioVariableFactory;
import gov.lanl.micot.infrastructure.naturalgas.optimize.ogf.variable.scenario.FlowScenarioVariableFactory;
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

/**	Bounds on the scenario-based flow variables
 * that is tied to the directionality variables
*/
public class FlowScenarioDirectionConstraint implements ConstraintFactory {

	private Collection<Scenario> scenarios = new ArrayList<Scenario>();

	// Constructor: @param scenario collection
	public FlowScenarioDirectionConstraint(Collection<Scenario> scenarios) {
		this.scenarios = scenarios;
	}
	
	/**
	 * Get the LE Name
	 * @param scenario
	 * @param edge
	 * @return
	 */
	private String getLEName(Scenario scenario, NaturalGasConnection edge) {
	  return "FlowDirLE." + scenario.getIndex() + "." + edge.toString();
	}

	/**
	 * Get the GE Name
	 * @param scenario
	 * @param edge
	 * @return
	 */
	private String getGEName(Scenario scenario, NaturalGasConnection edge) {
	  return "FlowDirGE." + scenario.getIndex() + "." + edge.toString();
	}
	
	// Constraint
	@Override
	public void constructConstraint(MathematicalProgram problem, NaturalGasModel model) throws VariableExistsException, NoVariableException, InvalidConstraintException {
		// Step 1: Instantiate all the variables being used in this constraint
		FlowScenarioVariableFactory NG_flowVarFactory =  new FlowScenarioVariableFactory(scenarios);
		FlowDirectionScenarioVariableFactory dirFactory = new FlowDirectionScenarioVariableFactory(scenarios);
		
		double totalGasDemand = 0.0;
		for (CityGate n1: model.getCityGates()) {
			totalGasDemand += n1.getMaximumConsumption().doubleValue();
		}

		// Step 2: Add the scenario-based constraint by looping over the set of specific scenarios 
		for (Scenario scenario: scenarios) {
			for (NaturalGasConnection edge : model.getFlowConnections()) {
				// Get flow variable
				Variable xij = NG_flowVarFactory.getVariable(problem, edge, scenario);
				Variable betaij = dirFactory.getVariable(problem, edge, scenario, FlowDirectionScenarioVariableFactory.FORWARD_PREFIX);
        Variable betaji = dirFactory.getVariable(problem, edge, scenario, FlowDirectionScenarioVariableFactory.REVERSE_PREFIX);
				
				// in case the edge is disabled
				if (betaij != null) {
				  double ub = totalGasDemand;
				  double lb = -totalGasDemand;
				      
				  if (edge instanceof Pipe || edge instanceof Resistor) {
				    ub = ConstraintUtility.computeflowUpperBound(model, edge, totalGasDemand);
            lb = ConstraintUtility.computeflowLowerBound(model, edge, totalGasDemand);				    				    
  			  }
				  
				  // x_ij <= (1 - beta_ji) * ub
				  LinearConstraint le = new LinearConstraintLessEq(getLEName(scenario, edge));
				  le.addVariable(xij, 1.0);
				  le.addVariable(betaji, ub);
				  le.setRightHandSide(ub);
				  problem.addLinearConstraint(le);

          // x_ij >= (1 - beta_ij) * lb
          LinearConstraint ge = new LinearConstraintGreaterEq(getGEName(scenario, edge));
          ge.addVariable(xij, 1.0);
          ge.addVariable(betaij, lb);
          ge.setRightHandSide(lb);
          problem.addLinearConstraint(ge);
				}
			}				
		}
	}
}
