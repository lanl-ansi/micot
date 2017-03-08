package gov.lanl.micot.util.math.solver.integerprogram.scip;

import gov.lanl.micot.util.math.solver.integerprogram.IntegerProgramFactory;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgramFlags;

/**
 * Factory for creating SCIP linear programs
 * @author Russell Bent
 */
public class ScipIntegerProgramFactory implements IntegerProgramFactory {

	/**
	 * Constructor
	 */
	public ScipIntegerProgramFactory() {		
	}

	@Override
	public MathematicalProgram constructIntegerProgram(MathematicalProgramFlags flags) {
		ScipIntegerProgramFlags newFlags = new ScipIntegerProgramFlags();
		newFlags.fill(flags);		
		return new ScipIntegerProgram(newFlags);
	}

	@Override
	public MathematicalProgram constructMathematicalProgram(MathematicalProgramFlags flags) {
		return constructIntegerProgram(flags);
	}
	
}
