package gov.lanl.micot.infrastructure.ep.optimize;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.optimize.Optimizer;
import gov.lanl.micot.infrastructure.optimize.OptimizerFactoryImpl;
import gov.lanl.micot.infrastructure.optimize.OptimizerFlags;
import gov.lanl.micot.infrastructure.optimize.mathprogram.MathProgramOptimizerFlags;
import gov.lanl.micot.infrastructure.project.AlgorithmConfiguration;
import gov.lanl.micot.infrastructure.project.ProjectConfiguration;
import gov.lanl.micot.infrastructure.project.ScenarioConfiguration;

/**
 * A factory for creating Optimal power flow linear DC Simulations
 * @author Russell Bent
 */
public class ElectricPowerMathProgramOptimizerFactory extends OptimizerFactoryImpl<ElectricPowerNode, ElectricPowerModel> {
	
	/**
	 *  Constructor
	 */
	public ElectricPowerMathProgramOptimizerFactory() {		
	}
	
	
	@Override
	public ElectricPowerMathProgramOptimizer createOptimizer(OptimizerFlags flags) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
	  ElectricPowerMathProgramOptimizerFlags opfFlags = new ElectricPowerMathProgramOptimizerFlags(flags);
	  String factory = opfFlags.getString(ElectricPowerMathProgramOptimizerFlags.MATH_PROGRAM_FACTORY_KEY);	  
	  ElectricPowerMathProgramOptimizer simulator = new ElectricPowerMathProgramOptimizer();
	  simulator.setFactoryName(factory);
	  
	  // pass in the flags so the internal optimizer can use it..
	  for (String key : flags.keySet()) {
	    simulator.addMathProgramFlag(key, flags.get(key));
	  }
	  
	  
	  addVariableFactories(flags, simulator);
	  addConstraints(flags,simulator);
	  addVariableAssignments(flags,simulator);	  	  
	  addObjectiveFunctions(flags,simulator);
    	  
		return simulator;
	}

  @Override
  public Optimizer<ElectricPowerNode, ElectricPowerModel> constructOptimizer(ProjectConfiguration projectConfiguration, AlgorithmConfiguration configuration, ElectricPowerModel model) {
    OptimizerFlags flags = new OptimizerFlags();
    flags.fill(configuration.getAlgorithmFlags());
    
    Collection<Scenario> scenarios = new ArrayList<Scenario>();
    for (ScenarioConfiguration sc : projectConfiguration.getScenarioConfigurations()) {
      scenarios.add(sc.getScenario());
    }    
    flags.put(MathProgramOptimizerFlags.SCENARIOS_KEY, scenarios);

    
    try {
      return createOptimizer(flags);
    }
    catch (Exception e) {
      e.printStackTrace();
      return null;
    } 
  }
 


}
