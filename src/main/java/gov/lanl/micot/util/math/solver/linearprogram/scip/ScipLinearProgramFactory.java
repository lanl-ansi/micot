package gov.lanl.micot.util.math.solver.linearprogram.scip;

import gov.lanl.micot.util.math.solver.linearprogram.LinearProgramFactory;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgramFlags;

/**
 * Factory for creating SCIP linear programs
 * @author Russell Bent
 */
public class ScipLinearProgramFactory implements LinearProgramFactory {

	/**
	 * Constructor
	 */
	public ScipLinearProgramFactory() {		
	}

	@Override
	public MathematicalProgram constructLinearProgram(MathematicalProgramFlags flags) {
		ScipLinearProgramFlags newFlags = new ScipLinearProgramFlags();
		newFlags.fill(flags);		
		return new ScipLinearProgram(newFlags);
	}

	@Override
	public MathematicalProgram constructMathematicalProgram(MathematicalProgramFlags flags) {
		return constructLinearProgram(flags);
	}
	
}
