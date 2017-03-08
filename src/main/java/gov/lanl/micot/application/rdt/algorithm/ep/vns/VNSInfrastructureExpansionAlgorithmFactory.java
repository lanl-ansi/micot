package gov.lanl.micot.application.rdt.algorithm.ep.vns;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.optimize.ElectricPowerMathProgramOptimizer;
import gov.lanl.micot.infrastructure.ep.optimize.ElectricPowerMathProgramOptimizerFactory;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.optimize.OptimizerFlags;
import gov.lanl.micot.infrastructure.optimize.mathprogram.MathProgramOptimizerFlags;
import gov.lanl.micot.infrastructure.project.AlgorithmConfiguration;
import gov.lanl.micot.infrastructure.project.ProjectConfiguration;
import gov.lanl.micot.infrastructure.project.ScenarioConfiguration;

/**
 * A factory for creating variable neighborhood search expansion planning mathematical programs
 * @author Russell Bent
 */
public class VNSInfrastructureExpansionAlgorithmFactory extends ElectricPowerMathProgramOptimizerFactory  {
	
	/**
	 *  Constructor
	 */
	public VNSInfrastructureExpansionAlgorithmFactory() {		
	}
	
	@Override
	public VNSInfrastructureExpansionAlgorithm createOptimizer(OptimizerFlags flags) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
	  VNSInfrastructureExpansionAlgorithmFlags opfFlags = new VNSInfrastructureExpansionAlgorithmFlags(flags);
    String factory = opfFlags.getString(VNSInfrastructureExpansionAlgorithmFlags.MATH_PROGRAM_FACTORY_KEY);   
    VNSInfrastructureExpansionAlgorithm algorithm = new VNSInfrastructureExpansionAlgorithm();
    algorithm.setFactoryName(factory);
    
    addVariableFactories(flags, algorithm);
    addConstraints(flags,algorithm);
    addVariableAssignments(flags,algorithm);        
    addObjectiveFunctions(flags,algorithm);
            
    double maxTime = opfFlags.getDouble(VNSInfrastructureExpansionAlgorithmFlags.MAX_TIME_KEY);
    int maxRestarts = opfFlags.getInt(VNSInfrastructureExpansionAlgorithmFlags.MAX_RESTARTS_KEY);
    int maxIterations = opfFlags.getInt(VNSInfrastructureExpansionAlgorithmFlags.MAX_ITERATIONS_KEY);
    double neighborhoodNormalize = opfFlags.getDouble(VNSInfrastructureExpansionAlgorithmFlags.NEIGHBORHOOD_NORMALIZE_KEY);
    String linearRelaxation = opfFlags.getString(VNSInfrastructureExpansionAlgorithmFlags.LINEAR_RELAXATION_MATH_PROGRAM_FACTORY_KEY);
        
    algorithm.setMaxTime(maxTime);
    algorithm.setMaxRestarts(maxRestarts);
    algorithm.setMaxIterations(maxIterations);
    algorithm.setNeighborhoodNormalize(neighborhoodNormalize);
    algorithm.setLinearRelaxationFactoryName(linearRelaxation);
    
    return algorithm;
	}

  /*@Override
  public ElectricPowerMathProgramOptimizer constructOptimizer(ProjectConfiguration configuration, ElectricPowerModel model) {
    OptimizerFlags flags = new OptimizerFlags();
    flags.fill(configuration.getAlgorithmFlags());
    try {
      return createOptimizer(flags);
    }
    catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }*/

  @Override
  public ElectricPowerMathProgramOptimizer constructOptimizer(ProjectConfiguration projectConfiguration, AlgorithmConfiguration configuration, ElectricPowerModel model) {
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
