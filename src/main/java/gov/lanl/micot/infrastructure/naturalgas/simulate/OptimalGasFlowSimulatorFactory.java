package gov.lanl.micot.infrastructure.naturalgas.simulate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasModel;
import gov.lanl.micot.infrastructure.naturalgas.optimize.NaturalGasMathProgramOptimizer;
import gov.lanl.micot.infrastructure.naturalgas.optimize.NaturalGasMathProgramOptimizerFactory;
import gov.lanl.micot.infrastructure.optimize.OptimizerFlags;
import gov.lanl.micot.infrastructure.project.ProjectConfiguration;
import gov.lanl.micot.infrastructure.project.ScenarioConfiguration;
import gov.lanl.micot.infrastructure.project.SimulatorConfiguration;
import gov.lanl.micot.infrastructure.simulate.Simulator;
import gov.lanl.micot.infrastructure.simulate.SimulatorFactory;
import gov.lanl.micot.infrastructure.simulate.SimulatorFlags;

/**
 * A factory for creating Optimal power flow linear DC Simulations
 * @author Russell Bent
 */
public class OptimalGasFlowSimulatorFactory implements SimulatorFactory<NaturalGasModel> {
	
	/**
	 *  Constructor
	 */
	public OptimalGasFlowSimulatorFactory() {		
	}
		
	@Override
	public OptimalGasFlowSimulator createSimulator(SimulatorFlags flags) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
	  OptimizerFlags opfFlags = new OptimizerFlags(flags);
	  String factoryName = flags.getString(OptimalGasFlowSimulatorFlags.OPTIMIZER_FACTORY_KEY);
	  NaturalGasMathProgramOptimizerFactory factory = ( NaturalGasMathProgramOptimizerFactory) Class.forName(factoryName).newInstance();
	  NaturalGasMathProgramOptimizer optimizer = factory.createOptimizer(opfFlags);	  
	  OptimalGasFlowSimulator simulator = new OptimalGasFlowSimulator(optimizer);
		return simulator;
	}

  @Override
  public Simulator<NaturalGasModel> constructSimulator(ProjectConfiguration projectConfiguration, SimulatorConfiguration configuration, NaturalGasModel model) {
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
