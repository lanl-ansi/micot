package gov.lanl.micot.infrastructure.ep.simulate.powerworld;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.project.ProjectConfiguration;
import gov.lanl.micot.infrastructure.project.ScenarioConfiguration;
import gov.lanl.micot.infrastructure.project.SimulatorConfiguration;
import gov.lanl.micot.infrastructure.simulate.Simulator;
import gov.lanl.micot.infrastructure.simulate.SimulatorFactory;
import gov.lanl.micot.infrastructure.simulate.SimulatorFlags;

/**
 * A factory for creating Ieiss Simulations
 * @author Russell Bent
 */
public class PowerworldSimulatorFactory implements SimulatorFactory<ElectricPowerModel> {
	
	/**
	 *  Constructor
	 */
	public PowerworldSimulatorFactory() {		
	}
	
	@Override
	public PowerworldSimulator createSimulator(SimulatorFlags flags) throws IOException {
	  PowerworldSimulatorFlags powerworldFlags = new PowerworldSimulatorFlags(flags);
	  PowerworldSimulator simulator = new PowerworldSimulator();
	  simulator.setCheckArea(powerworldFlags.getBoolean(PowerworldSimulatorFlags.CHECK_AREA_FLAG));
    simulator.setCheckMVarLimits(powerworldFlags.getBoolean(PowerworldSimulatorFlags.CHECK_MVAR_LIMITS_FLAG));
    simulator.setCheckMWLimits(powerworldFlags.getBoolean(PowerworldSimulatorFlags.CHECK_MW_LIMITS_FLAG));
    simulator.setCheckPhaseShifters(powerworldFlags.getBoolean(PowerworldSimulatorFlags.CHECK_PHASE_SHIFTERS_FLAG));
    simulator.setCheckShunts(powerworldFlags.getBoolean(PowerworldSimulatorFlags.CHECK_SHUNTS_FLAG));
    simulator.setCheckSVC(powerworldFlags.getBoolean(PowerworldSimulatorFlags.CHECK_SVC_FLAG));
    simulator.setCheckTaps(powerworldFlags.getBoolean(PowerworldSimulatorFlags.CHECK_TAPS_FLAG));
		return simulator;
	}
	
  @Override
  public Simulator<ElectricPowerModel> constructSimulator(ProjectConfiguration projectConfiguration, SimulatorConfiguration configuration, ElectricPowerModel model) {
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
