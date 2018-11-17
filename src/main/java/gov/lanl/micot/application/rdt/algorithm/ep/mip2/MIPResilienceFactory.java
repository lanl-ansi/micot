package gov.lanl.micot.application.rdt.algorithm.ep.mip2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import gov.lanl.micot.application.rdt.algorithm.AlgorithmConstants;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.MIPInfrastructureExpansionAlgorithmFlags;
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
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgramFlags;

/**
 * A factory for creating a MIP algorithm
 * @author Russell Bent
 */
public class MIPResilienceFactory extends OptimizerFactoryImpl<ElectricPowerNode, ElectricPowerModel> {
	
	/**
	 *  Constructor
	 */
	public MIPResilienceFactory() {		
	}
	
	@Override
	public MIPResilienceAlgorithm createOptimizer(OptimizerFlags oFlags) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
	  MIPResilienceFlags flags = new MIPResilienceFlags(oFlags);
	  
	  Collection<Scenario> scenarios = flags.getCollection(MIPResilienceFlags.SCENARIOS_KEY, Scenario.class);        
	  MIPResilienceAlgorithm algorithm = new MIPResilienceAlgorithm(scenarios);
	  
	  double timeout = oFlags.containsKey(MathematicalProgramFlags.TIMEOUT_FLAG) ? oFlags.getDouble(MathematicalProgramFlags.TIMEOUT_FLAG) : Double.POSITIVE_INFINITY;
    double criticalLoadMet = oFlags.getDouble(AlgorithmConstants.CRITICAL_LOAD_MET_KEY);
    double nonCriticalLoadMet = oFlags.getDouble(AlgorithmConstants.LOAD_MET_KEY);

	  algorithm.addMathProgramFlag(MathematicalProgramFlags.DEBUG_ON_FLAG, false);
	  algorithm.addMathProgramFlag(MathematicalProgramFlags.MIP_GAP_TOLERANCE_FLAG, 1e-3);
    algorithm.addMathProgramFlag(MathematicalProgramFlags.TIMEOUT_FLAG, timeout);
    algorithm.addMathProgramFlag(MathematicalProgramFlags.MATHEMATICAL_PROGRAM_FACTORY_FLAG, flags.getString(MIPInfrastructureExpansionAlgorithmFlags.MATH_PROGRAM_FACTORY_KEY));

    algorithm.setCriticalLoadMet(criticalLoadMet);
    algorithm.setNonCriticalLoadMet(nonCriticalLoadMet);
    
	  return algorithm;
	}
 
  @Override
  public Optimizer<ElectricPowerNode,ElectricPowerModel> constructOptimizer(ProjectConfiguration projectConfiguration, AlgorithmConfiguration configuration, ElectricPowerModel model) {
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
