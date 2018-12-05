package gov.lanl.micot.deprecated.sbd;

import gov.lanl.micot.infrastructure.optimize.OptimizerFlags;

/**
 * Flags used for solvers that do scenario based decomposition
 * 
 * @author Russell Bent
 */
public class ScenarioBasedDecompositionOptimizerFlags extends OptimizerFlags {

	private static final long serialVersionUID = 1L;

	public static final String OUTER_OPTIMIZER_FLAGS_KEY = "OuterOptimizerFlags";
	public static final String INNER_OPTIMIZER_FLAGS_KEY = "InnerOptimizerFlags";

	/**
	 * Constructor
	 */
	public ScenarioBasedDecompositionOptimizerFlags() {
		super();
		put(OUTER_OPTIMIZER_FLAGS_KEY, new OptimizerFlags());
		put(INNER_OPTIMIZER_FLAGS_KEY, new OptimizerFlags());
	}

	/**
	 * Constructor
	 * 
	 * @param flags
	 */
	public ScenarioBasedDecompositionOptimizerFlags(OptimizerFlags flags) {
		super(flags);
		if (get(OUTER_OPTIMIZER_FLAGS_KEY) == null) {
			put(OUTER_OPTIMIZER_FLAGS_KEY, new OptimizerFlags());
		}
		if (get(INNER_OPTIMIZER_FLAGS_KEY) == null) {
			put(INNER_OPTIMIZER_FLAGS_KEY, new OptimizerFlags());
		}
	}

	/**
	 * Add keys to the outer optimization
	 * 
	 * @param key
	 * @param value
	 */
	public void addOuterOptimizationKey(String key, Object value) {
		get(OUTER_OPTIMIZER_FLAGS_KEY, OptimizerFlags.class).put(key, value);
	}

	/**
	 * Add keys to the inner optimization
	 * 
	 * @param key
	 * @param value
	 */
	public void addInnerOptimizationKey(String key, Object value) {
		get(INNER_OPTIMIZER_FLAGS_KEY, OptimizerFlags.class).put(key, value);
	}

}
