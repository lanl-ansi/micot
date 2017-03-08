package gov.lanl.micot.infrastructure.naturalgas.optimize.ogf.initialsolution.scenario;

import gov.lanl.micot.infrastructure.model.FlowConnection;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.model.ScenarioAttribute;
import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasModel;
import gov.lanl.micot.infrastructure.naturalgas.optimize.InitialSolutionFactory;
import gov.lanl.micot.infrastructure.naturalgas.optimize.ogf.variable.scenario.FlowScenarioVariableFactory;
import gov.lanl.micot.util.math.solver.Solution;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.NoConstraintException;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

import java.util.ArrayList;
import java.util.Collection;

/**
 * This class assigns initial solutions to flow scenario variables
 */

public class FlowScenarioInitialSolutionFactory implements InitialSolutionFactory {
	private Collection<Scenario> scenarios = new ArrayList<Scenario>();
  
	// Constructor: @param models
	public FlowScenarioInitialSolutionFactory(Collection<Scenario> scenarios) {
		this.scenarios = scenarios;
	}
	
  @Override
  public void updateInitialSolution(NaturalGasModel model, MathematicalProgram problem) throws VariableExistsException, NoVariableException, NoConstraintException {
    Solution initialSolution = problem.getInitialSolution();
    FlowScenarioVariableFactory factory = new FlowScenarioVariableFactory(scenarios);
    
    // conrado, just to double check, in your assignment factories, you are storing things like pressure and flow by scenario? 
    // if not, see how the power assignment factories do it and modify it, since this is assuming that is how flows are stored... I am assuming you were able to maintain the scenario architecture
    for (Scenario scenario : scenarios) {        
      for (FlowConnection connection : model.getFlowConnections()) {
        ScenarioAttribute attribute = (ScenarioAttribute) connection.getFlow();
        Variable variable = factory.getVariable(problem, connection, scenario);
        initialSolution.addValue(variable, attribute.getValue(scenario));
      }
    }
    
  }
}
