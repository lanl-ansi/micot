package gov.lanl.micot.util.math.solver.linearprogram;

import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgramFactory;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgramFlags;

/**
 * Interface for linear program factories
 * @author Russell Bent
 */
public interface LinearProgramFactory extends MathematicalProgramFactory {
	
	/**
	 * Function for constructing a linear program
	 * @return
	 */
	public MathematicalProgram constructLinearProgram(MathematicalProgramFlags flags);
	
}
