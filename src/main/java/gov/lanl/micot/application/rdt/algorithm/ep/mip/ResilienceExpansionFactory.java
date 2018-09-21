package gov.lanl.micot.application.rdt.algorithm.ep.mip;

import java.io.IOException;

import gov.lanl.micot.infrastructure.ep.optimize.ElectricPowerMathProgramOptimizer;
import gov.lanl.micot.infrastructure.optimize.OptimizerFlags;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgramFlags;

/**
 * A factory for creating Optimal expansion planning mathematical programs for resilience of 
 * @author Russell Bent
 */
public class ResilienceExpansionFactory extends MIPInfrastructureExpansionAlgorithmFactory {
	
	/**
	 *  Constructor
	 */
	public ResilienceExpansionFactory() {		
	}
	
	@Override
	public ElectricPowerMathProgramOptimizer createOptimizer(OptimizerFlags flags) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
	  ElectricPowerMathProgramOptimizer algorithm = super.createOptimizer(new ResilienceExpansionFlags(flags));
	  algorithm.addMathProgramFlag(MathematicalProgramFlags.DEBUG_ON_FLAG, false);
    algorithm.addMathProgramFlag(MathematicalProgramFlags.MIP_GAP_TOLERANCE_FLAG, 1e-3);
    if (flags.containsKey(MathematicalProgramFlags.TIMEOUT_FLAG)) {
      algorithm.addMathProgramFlag(MathematicalProgramFlags.TIMEOUT_FLAG, flags.getDouble(MathematicalProgramFlags.TIMEOUT_FLAG));
    }
	  return algorithm;
	}
 
}
