package gov.lanl.micot.infrastructure.coupled.simulate;

import java.util.ArrayList;
import java.util.Collection;

import gov.lanl.micot.infrastructure.coupled.model.CoupledModel;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.project.ProjectConfiguration;
import gov.lanl.micot.infrastructure.project.ScenarioConfiguration;
import gov.lanl.micot.infrastructure.project.SimulatorConfiguration;
import gov.lanl.micot.infrastructure.simulate.Simulator;
import gov.lanl.micot.infrastructure.simulate.SimulatorFactory;
import gov.lanl.micot.infrastructure.simulate.SimulatorFlags;

/**
 * A factory for creating coupled Simulations
 * @author Russell Bent
 */
public abstract class CoupledSimulatorFactoryImpl implements SimulatorFactory<CoupledModel> {
	
	/**
	 *  Constructor
	 */
	public CoupledSimulatorFactoryImpl() {		
	}
	
	@Override
  public Simulator<CoupledModel> constructSimulator(ProjectConfiguration projectConfiguration, SimulatorConfiguration configuration, CoupledModel model) {
    SimulatorFlags flags = new SimulatorFlags();
    flags.fill(configuration.getSimulatorFlags());
    
    Collection<Scenario> scenarios = new ArrayList<Scenario>();
    for (ScenarioConfiguration sc : projectConfiguration.getScenarioConfigurations()) {
      scenarios.add(sc.getScenario());
    }    
    flags.put(SimulatorFlags.SCENARIOS_KEY, scenarios);
    
    try {
      return createSimulator(flags);
    }
    catch (Exception e) {
      e.printStackTrace();
      return null;
    } 
  }	
}
