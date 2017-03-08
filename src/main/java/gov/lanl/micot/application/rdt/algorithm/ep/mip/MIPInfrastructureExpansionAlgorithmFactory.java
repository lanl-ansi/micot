package gov.lanl.micot.application.rdt.algorithm.ep.mip;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.ep.optimize.ElectricPowerMathProgramOptimizer;
import gov.lanl.micot.infrastructure.ep.optimize.ElectricPowerMathProgramOptimizerFactory;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.optimize.Optimizer;
import gov.lanl.micot.infrastructure.optimize.OptimizerFlags;
import gov.lanl.micot.infrastructure.optimize.mathprogram.MathProgramOptimizerFlags;
import gov.lanl.micot.infrastructure.project.AlgorithmConfiguration;
import gov.lanl.micot.infrastructure.project.ProjectConfiguration;
import gov.lanl.micot.infrastructure.project.ScenarioConfiguration;

/**
 * A factory for creating Optimal expansion planning mathematical programs
 * @author Russell Bent
 */
public class MIPInfrastructureExpansionAlgorithmFactory extends ElectricPowerMathProgramOptimizerFactory {
	
	/**
	 *  Constructor
	 */
	public MIPInfrastructureExpansionAlgorithmFactory() {		
	}
	
	@Override
	public ElectricPowerMathProgramOptimizer createOptimizer(OptimizerFlags flags) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
	  MIPInfrastructureExpansionAlgorithmFlags opfFlags = new MIPInfrastructureExpansionAlgorithmFlags(flags);
    String factory = opfFlags.getString(MIPInfrastructureExpansionAlgorithmFlags.MATH_PROGRAM_FACTORY_KEY);   
    ElectricPowerMathProgramOptimizer algorithm = new ElectricPowerMathProgramOptimizerFactory().createOptimizer(opfFlags);
    algorithm.setFactoryName(factory);
    
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
