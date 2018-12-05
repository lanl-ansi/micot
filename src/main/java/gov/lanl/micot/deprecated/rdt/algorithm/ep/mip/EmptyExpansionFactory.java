package gov.lanl.micot.deprecated.rdt.algorithm.ep.mip;

import java.io.IOException;

import gov.lanl.micot.infrastructure.ep.optimize.ElectricPowerMathProgramOptimizer;
import gov.lanl.micot.infrastructure.optimize.OptimizerFlags;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgramFlags;

/**
 * A factory for creating for the no optimization option 
 * @author Russell Bent
 */
public class EmptyExpansionFactory extends MIPInfrastructureExpansionAlgorithmFactory {
	
	/**
	 *  Constructor
	 */
	public EmptyExpansionFactory() {		
	}
	
	@Override
	public ElectricPowerMathProgramOptimizer createOptimizer(OptimizerFlags flags) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
	  ElectricPowerMathProgramOptimizer algorithm = super.createOptimizer(new EmptyExpansionFlags(flags));
	  algorithm.addMathProgramFlag(MathematicalProgramFlags.DEBUG_ON_FLAG, false);
    algorithm.addMathProgramFlag(MathematicalProgramFlags.MIP_GAP_TOLERANCE_FLAG, 1e-3);
	  return algorithm;
	}
 
}
