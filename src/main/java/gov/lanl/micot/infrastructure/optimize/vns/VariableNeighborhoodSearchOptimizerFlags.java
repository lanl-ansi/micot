package gov.lanl.micot.infrastructure.optimize.vns;

import gov.lanl.micot.infrastructure.optimize.OptimizerFlags;

/**
 * Flags used for solvers that do variable neighborhood search
 * 
 * @author Russell Bent
 */
public class VariableNeighborhoodSearchOptimizerFlags extends OptimizerFlags {

	private static final long serialVersionUID = 1L;

	public static final String RELAXED_OPTIMIZER_FLAGS_KEY  = "RelaxedOptimizerFlags";
	public static final String OPTIMIZER_FLAGS_KEY          = "OptimizerFlags";
	
  public static final String VARIABLE_FACTORY_KEY         = "VARIABLE_FACTORY";
  public static final String RELAXED_VARIABLE_FACTORY_KEY = "RELAXED_VARIABLE_FACTORY";
  public static final String IS_DISCRETE_KEY = "IS_DISCRETE";

	/**
	 * Constructor
	 */
	public VariableNeighborhoodSearchOptimizerFlags() {
		super();
		put(OPTIMIZER_FLAGS_KEY, new OptimizerFlags());
		put(RELAXED_OPTIMIZER_FLAGS_KEY, new OptimizerFlags());
	}

	/**
	 * Constructor
	 * 
	 * @param flags
	 */
	public VariableNeighborhoodSearchOptimizerFlags(OptimizerFlags flags) {
		super(flags);
		if (get(OPTIMIZER_FLAGS_KEY) == null) {
			put(OPTIMIZER_FLAGS_KEY, new OptimizerFlags());
		}
		if (get(RELAXED_OPTIMIZER_FLAGS_KEY) == null) {
			put(RELAXED_OPTIMIZER_FLAGS_KEY, new OptimizerFlags());
		}
	}

	/**
	 * Add keys to the outer optimization
	 * 
	 * @param key
	 * @param value
	 */
	public void addOptimizationKey(String key, Object value) {
		get(OPTIMIZER_FLAGS_KEY, OptimizerFlags.class).put(key, value);
	}

	/**
	 * Add keys to the inner optimization
	 * 
	 * @param key
	 * @param value
	 */
	public void addRelaxedOptimizationKey(String key, Object value) {
		get(RELAXED_OPTIMIZER_FLAGS_KEY, OptimizerFlags.class).put(key, value);
	}

}
