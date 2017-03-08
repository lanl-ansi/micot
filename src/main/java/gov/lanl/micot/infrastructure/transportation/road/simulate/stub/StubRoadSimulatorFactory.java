package gov.lanl.micot.infrastructure.transportation.road.simulate.stub;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Collection;

import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.project.ProjectConfiguration;
import gov.lanl.micot.infrastructure.project.ScenarioConfiguration;
import gov.lanl.micot.infrastructure.project.SimulatorConfiguration;
import gov.lanl.micot.infrastructure.simulate.Simulator;
import gov.lanl.micot.infrastructure.simulate.SimulatorFactory;
import gov.lanl.micot.infrastructure.simulate.SimulatorFlags;
import gov.lanl.micot.infrastructure.transportation.road.model.RoadModel;
import gov.lanl.micot.infrastructure.transportation.road.simulate.RoadSimulatorFlags;
import gov.lanl.micot.util.collection.Pair;

/**
 * A factory for creating stub road Simulations
 * @author 236322
 */
public class StubRoadSimulatorFactory implements SimulatorFactory<RoadModel> {
	
	/**
	 *  Constructor
	 */
	public StubRoadSimulatorFactory() {		
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
  @Override
	public StubRoadSimulator createSimulator(SimulatorFlags flags) throws IOException {
		RoadSimulatorFlags newFlags = new RoadSimulatorFlags(flags);
		StubRoadSimulator simulator = new StubRoadSimulator();
		Collection<Pair<String,Object>> nodes = (Collection)newFlags.getCollection(RoadSimulatorFlags.All_PAIR_NODES_TAG, Pair.class);   
    simulator.setNodes(nodes);
		return simulator;
	}

	 @SuppressWarnings("unchecked")
  @Override
	  public Simulator<RoadModel> constructSimulator(ProjectConfiguration projectConfiguration, SimulatorConfiguration configuration, RoadModel model) {
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
