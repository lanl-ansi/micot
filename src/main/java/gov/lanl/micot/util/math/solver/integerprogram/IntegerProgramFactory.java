package gov.lanl.micot.util.math.solver.integerprogram;

import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgramFactory;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgramFlags;

/**
 * Interface for integer program factories
 * @author Russell Bent
 */
public interface IntegerProgramFactory extends MathematicalProgramFactory {
	
	/**
	 * Function for constructing a linear program
	 * @return
	 */
	public MathematicalProgram constructIntegerProgram(MathematicalProgramFlags flags);
	
}
