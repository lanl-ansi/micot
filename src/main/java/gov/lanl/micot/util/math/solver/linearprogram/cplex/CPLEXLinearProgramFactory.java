package gov.lanl.micot.util.math.solver.linearprogram.cplex;

import gov.lanl.micot.util.math.solver.linearprogram.LinearProgramFactory;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgramFlags;

/**
 * Factory for creating CPlex linear programs
 * @author Russell Bent
 */
public class CPLEXLinearProgramFactory implements LinearProgramFactory {

	/**
	 * Constructor
	 */
	public CPLEXLinearProgramFactory() {		
	}

	@Override
	public MathematicalProgram constructLinearProgram(MathematicalProgramFlags flags) {
		CPLEXLinearProgramFlags newFlags = new CPLEXLinearProgramFlags();
		newFlags.fill(flags);		
		return new CPLEXLinearProgram(newFlags);
	}

	@Override
	public MathematicalProgram constructMathematicalProgram(MathematicalProgramFlags flags) {
		return constructLinearProgram(flags);
	}
	
}
