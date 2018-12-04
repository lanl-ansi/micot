package gov.lanl.micot.application.rdt.algorithm.ep.mip;

import gov.lanl.micot.application.rdt.algorithm.ep.ResilienceExpansionFlags;
import gov.lanl.micot.infrastructure.optimize.OptimizerFlags;

/**
 * Flags used for expansion planning algorithms base on a full MIP formulation
 * 
 * @author Russell Bent
 */
public class MIPResilienceFlags extends OptimizerFlags {

	private static final long serialVersionUID = 1L;
  
	/**
	 * Constructor
	 */
	public MIPResilienceFlags() {
		super();
		init(new ResilienceExpansionFlags());
	}
	
	/**
	 * Constructor
	 * 
	 * @param flags
	 */
	public MIPResilienceFlags(OptimizerFlags flags) {
		super(flags);
		init(new ResilienceExpansionFlags(flags));
	}

	/**
	 * Initialize everything we need to run the inner and outer problem
	 * @param defaults
	 */
  private void init(ResilienceExpansionFlags defaults) {
	      
  }
	
	  
}
