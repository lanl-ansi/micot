package gov.lanl.micot.application.rdt.algorithm.ep;


import gov.lanl.micot.infrastructure.ep.optimize.ElectricPowerMathProgramOptimizerFlags;
import gov.lanl.micot.infrastructure.optimize.OptimizerFlags;
import gov.lanl.micot.application.rdt.algorithm.AlgorithmConstants;
import gov.lanl.micot.util.math.solver.quadraticprogram.scip.ScipQuadraticProgramFactory;

/**
 * Flags used for expansion planning algorithms that take into account the full optimization model
 * when computing the optimal expansion plan
 * 
 * @author Russell Bent
 */
public class ResilienceExpansionFlags extends ElectricPowerMathProgramOptimizerFlags {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 */
	public ResilienceExpansionFlags() {
		super();
		put(MATH_PROGRAM_FACTORY_KEY, getDefaultMathProgram());
		put(AlgorithmConstants.LOAD_MET_KEY, AlgorithmConstants.DEFAULT_LOAD_MET);
		put(AlgorithmConstants.CRITICAL_LOAD_MET_KEY, AlgorithmConstants.DEFAULT_CRITICAL_LOAD_MET);
	//	put(AlgorithmConstants.IS_DISCRETE_GENERATION_KEY, AlgorithmConstants.DEFAULT_IS_DISCRETE_GENERATION);
   // put(AlgorithmConstants.IS_CHANCE_CONSTRAINT_KEY, AlgorithmConstants.DEFAULT_IS_CHANCE_CONSTRAINT);
    put(AlgorithmConstants.CYCLE_MODEL_CONSTRAINT_KEY, AlgorithmConstants.DEFAULT_CYCLE_CONSTRAINT);
    put(AlgorithmConstants.POWER_FLOW_MODEL_KEY, AlgorithmConstants.DEFAULT_POWER_FLOW_MODEL);
	}

	@Override
  protected String getDefaultMathProgram() {
    return ScipQuadraticProgramFactory.class.getCanonicalName();
  }
	
	/**
	 * Constructor
	 * 
	 * @param flags
	 */
	public ResilienceExpansionFlags(OptimizerFlags flags) {
		super(flags);		
		if (get(MATH_PROGRAM_FACTORY_KEY) == null) {
		  put(MATH_PROGRAM_FACTORY_KEY, getDefaultMathProgram());
		}
		if (get(AlgorithmConstants.LOAD_MET_KEY) == null) {
			put(AlgorithmConstants.LOAD_MET_KEY, AlgorithmConstants.DEFAULT_LOAD_MET);
		}
		if (get(AlgorithmConstants.CRITICAL_LOAD_MET_KEY) == null) {
			put(AlgorithmConstants.CRITICAL_LOAD_MET_KEY, AlgorithmConstants.DEFAULT_CRITICAL_LOAD_MET);
		}
    //if (get(AlgorithmConstants.IS_DISCRETE_GENERATION_KEY) == null) {
     // put(AlgorithmConstants.IS_DISCRETE_GENERATION_KEY, AlgorithmConstants.DEFAULT_IS_DISCRETE_GENERATION);
    //}
    //if (get(AlgorithmConstants.IS_CHANCE_CONSTRAINT_KEY) == null) {
      //put(AlgorithmConstants.IS_CHANCE_CONSTRAINT_KEY, AlgorithmConstants.DEFAULT_IS_CHANCE_CONSTRAINT);
   // }
    if (get(AlgorithmConstants.CYCLE_MODEL_CONSTRAINT_KEY) == null) {
      put(AlgorithmConstants.CYCLE_MODEL_CONSTRAINT_KEY, AlgorithmConstants.DEFAULT_CYCLE_CONSTRAINT);
    }
    if (get(AlgorithmConstants.POWER_FLOW_MODEL_KEY) == null) {
      put(AlgorithmConstants.POWER_FLOW_MODEL_KEY, AlgorithmConstants.DEFAULT_POWER_FLOW_MODEL);
    }
    if (get(AlgorithmConstants.IS_DISCRETE_MODEL_KEY) == null) {
      put(AlgorithmConstants.IS_DISCRETE_MODEL_KEY, AlgorithmConstants.DEFAULT_IS_DISCRETE_MODEL);
    }

	}

  
}
