package gov.lanl.micot.infrastructure.ep.optimize.dcopf;

import java.io.IOException;

import gov.lanl.micot.infrastructure.ep.optimize.ElectricPowerMathProgramOptimizer;
import gov.lanl.micot.infrastructure.ep.optimize.ElectricPowerMathProgramOptimizerFactory;
import gov.lanl.micot.infrastructure.optimize.OptimizerFlags;

/**
 * A factory for creating Optimal power flow linear DC Simulations
 * @author Russell Bent
 */
public class ShiftingFactorOptimalPowerFlowFactory extends ElectricPowerMathProgramOptimizerFactory {
	
	/**
	 *  Constructor
	 */
	public ShiftingFactorOptimalPowerFlowFactory() {		
	}
	
	@Override
	public ElectricPowerMathProgramOptimizer createOptimizer(OptimizerFlags flags) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
	  return super.createOptimizer(new ShiftingFactorOptimalPowerFlowFlags(flags));
	}	
}
