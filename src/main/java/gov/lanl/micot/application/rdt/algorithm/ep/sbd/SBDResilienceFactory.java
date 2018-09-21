package gov.lanl.micot.application.rdt.algorithm.ep.sbd;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.optimize.Optimizer;
import gov.lanl.micot.infrastructure.optimize.OptimizerFlags;
import gov.lanl.micot.infrastructure.optimize.mathprogram.MathProgramOptimizerFlags;
import gov.lanl.micot.infrastructure.optimize.sbd.ScenarioBasedDecompositionFactoryImpl;
import gov.lanl.micot.infrastructure.project.AlgorithmConfiguration;
import gov.lanl.micot.infrastructure.project.ProjectConfiguration;
import gov.lanl.micot.infrastructure.project.ScenarioConfiguration;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgramFlags;

/**
 * A factory for creating a scenario based decomposition algorithm
 * @author Russell Bent
 */
public class SBDResilienceFactory extends ScenarioBasedDecompositionFactoryImpl<ElectricPowerNode, ElectricPowerModel> {
	
	/**
	 *  Constructor
	 */
	public SBDResilienceFactory() {		
	}
	
	@Override
	public SBDResilienceAlgorithm createOptimizer(OptimizerFlags oFlags) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
	  SBDResilienceFlags flags = new SBDResilienceFlags(oFlags);
	  
	  Collection<Scenario> scenarios = flags.getCollection(SBDResilienceFlags.SCENARIOS_KEY, Scenario.class);
    
	  SBDResilienceAlgorithm algorithm = new SBDResilienceAlgorithm(scenarios);
	  
	  double innerTimeout = oFlags.containsKey(MathematicalProgramFlags.TIMEOUT_FLAG) ? oFlags.getDouble(MathematicalProgramFlags.TIMEOUT_FLAG) : Double.POSITIVE_INFINITY;
    double outerTimeout = oFlags.containsKey(MathematicalProgramFlags.TIMEOUT_FLAG) ? oFlags.getDouble(MathematicalProgramFlags.TIMEOUT_FLAG) : Double.POSITIVE_INFINITY;
	  
	  algorithm.addInnerMathProgramFlag(MathematicalProgramFlags.DEBUG_ON_FLAG, false);
	  algorithm.addInnerMathProgramFlag(MathematicalProgramFlags.MIP_GAP_TOLERANCE_FLAG, 1e-3);
    algorithm.addInnerMathProgramFlag(MathematicalProgramFlags.TIMEOUT_FLAG, innerTimeout);
	  algorithm.addOuterMathProgramFlag(MathematicalProgramFlags.DEBUG_ON_FLAG, false);
	  algorithm.addOuterMathProgramFlag(MathematicalProgramFlags.MIP_GAP_TOLERANCE_FLAG, 1e-3);
	  algorithm.addOuterMathProgramFlag(MathematicalProgramFlags.TIMEOUT_FLAG, outerTimeout);

	  // pull out and overwrite parameters
	  for (String key : flags.keySet()) {
	    if (key.startsWith(SBDResilienceFlags.OUTER_PREFIX)) {
	      algorithm.addOuterMathProgramFlag(key.substring(SBDResilienceFlags.OUTER_PREFIX.length(), key.length()), flags.get(key));
	    }
	    if (key.startsWith(SBDResilienceFlags.INNER_PREFIX)) {
	      algorithm.addInnerMathProgramFlag(key.substring(SBDResilienceFlags.INNER_PREFIX.length(), key.length()), flags.get(key));
	    }
	  }

    
    addInnerVariableFactories(flags, algorithm);
    addInnerConstraints(flags,algorithm);
    addInnerVariableAssignments(flags,algorithm);        
    addInnerObjectiveFunctions(flags,algorithm);

    addOuterVariableFactories(flags, algorithm);
    addOuterConstraints(flags,algorithm);
    addOuterVariableAssignments(flags,algorithm);        
    addOuterObjectiveFunctions(flags,algorithm);
        
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
