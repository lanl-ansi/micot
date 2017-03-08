package gov.lanl.micot.util.math.solver.integerprogram.cplex;

import gov.lanl.micot.util.math.solver.linearprogram.LinearProgramFactory;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgramFlags;


/**
 * Factory for creating CPlex integer programs
 * @author Russell Bent
 */
public class CPLEXIntegerProgramFactory implements LinearProgramFactory {

	/**
	 * Constructor
	 */
	public CPLEXIntegerProgramFactory() {		
	}

	@Override
	public MathematicalProgram constructLinearProgram(MathematicalProgramFlags flags) {
		CPLEXIntegerProgramFlags newFlags = new CPLEXIntegerProgramFlags();
		newFlags.fill(flags);		
		return new CPLEXIntegerProgram(newFlags);
	}

	@Override
	public MathematicalProgram constructMathematicalProgram(MathematicalProgramFlags flags) {
		return constructLinearProgram(flags);
	}
	
}
