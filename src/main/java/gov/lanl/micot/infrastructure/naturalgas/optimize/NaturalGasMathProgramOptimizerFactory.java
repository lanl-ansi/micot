package gov.lanl.micot.infrastructure.naturalgas.optimize;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasModel;
import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasNode;
import gov.lanl.micot.infrastructure.optimize.Optimizer;
import gov.lanl.micot.infrastructure.optimize.OptimizerFactoryImpl;
import gov.lanl.micot.infrastructure.optimize.OptimizerFlags;
import gov.lanl.micot.infrastructure.optimize.mathprogram.MathProgramOptimizerFlags;
import gov.lanl.micot.infrastructure.project.AlgorithmConfiguration;
import gov.lanl.micot.infrastructure.project.ProjectConfiguration;
import gov.lanl.micot.infrastructure.project.ScenarioConfiguration;

/**
 * A factory for creating optimal gas flow math programs
 * @author Russell Bent
 */
public class NaturalGasMathProgramOptimizerFactory extends OptimizerFactoryImpl<NaturalGasNode, NaturalGasModel> {
	
	/**
	 *  Constructor
	 */
	public NaturalGasMathProgramOptimizerFactory() {
	  super();
	}
	
	
	@Override
	public NaturalGasMathProgramOptimizer createOptimizer(OptimizerFlags flags) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
	  NaturalGasMathProgramOptimizerFlags opfFlags = new NaturalGasMathProgramOptimizerFlags(flags);
	  String factory = opfFlags.getString(NaturalGasMathProgramOptimizerFlags.MATH_PROGRAM_FACTORY_KEY);	  
	  NaturalGasMathProgramOptimizer simulator = new NaturalGasMathProgramOptimizer();
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
  public Optimizer<NaturalGasNode, NaturalGasModel> constructOptimizer(ProjectConfiguration projectConfiguration, AlgorithmConfiguration configuration, NaturalGasModel model) {
    OptimizerFlags flags = new OptimizerFlags();
    flags.fill(configuration.getAlgorithmFlags());
    
    Collection<Scenario> scenarios = new ArrayList<Scenario>();
    for (ScenarioConfiguration sc : projectConfiguration.getScenarioConfigurations()) {
      scenarios.add(sc.getScenario());
    }
    
    if (scenarios.size() == 0) {
      scenarios.add(new Scenario(1,1.0));
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
