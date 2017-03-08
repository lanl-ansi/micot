package gov.lanl.micot.util.math.solver.integerprogram.scip;

import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgramFlags;


/**
 * Flags and parameters for setting up mathematical programs 
 * @author Russell Bent
 */
public class ScipIntegerProgramFlags extends MathematicalProgramFlags {
	
	private static final long serialVersionUID = 1L;
  	
	/**
	 * Constructor
	 */
	public ScipIntegerProgramFlags() {
	  super();
	  put(MATHEMATICAL_PROGRAM_FACTORY_FLAG,"gov.lanl.util.math.solver.integerprogram.scip.ScipIntegerProgramFactory");
	}
	
}
