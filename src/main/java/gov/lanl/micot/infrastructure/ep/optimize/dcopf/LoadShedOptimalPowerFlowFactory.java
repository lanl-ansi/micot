package gov.lanl.micot.infrastructure.ep.optimize.dcopf;

import java.io.IOException;

import gov.lanl.micot.infrastructure.ep.optimize.ElectricPowerMathProgramOptimizer;
import gov.lanl.micot.infrastructure.ep.optimize.ElectricPowerMathProgramOptimizerFactory;
import gov.lanl.micot.infrastructure.optimize.OptimizerFlags;

/**
 * A factory for creating Optimal power flow linear DC Simulations with load shedding
 * @author Russell Bent
 */
public class LoadShedOptimalPowerFlowFactory extends ElectricPowerMathProgramOptimizerFactory {
	
	/**
	 *  Constructor
	 */
	public LoadShedOptimalPowerFlowFactory() {		
	}
	
	@Override
	public ElectricPowerMathProgramOptimizer createOptimizer(OptimizerFlags flags) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
	  return super.createOptimizer(new LoadShedOptimalPowerFlowFlags(flags));
	}	
}
