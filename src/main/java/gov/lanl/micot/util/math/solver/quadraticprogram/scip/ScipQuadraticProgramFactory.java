package gov.lanl.micot.util.math.solver.quadraticprogram.scip;

import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgramFlags;
import gov.lanl.micot.util.math.solver.quadraticprogram.QuadraticProgramFactory;


/**
 * Factory for creating CPlex quadratic programs
 * @author Russell Bent
 */
public class ScipQuadraticProgramFactory implements QuadraticProgramFactory {

	/**
	 * Constructor
	 */
	public ScipQuadraticProgramFactory() {		
	}

	@Override
	public MathematicalProgram constructQuadraticProgram(MathematicalProgramFlags flags) {
		ScipQuadraticProgramFlags newFlags = new ScipQuadraticProgramFlags();
		newFlags.fill(flags);		
		return new ScipQuadraticProgram(newFlags);
	}

	@Override
	public MathematicalProgram constructMathematicalProgram(MathematicalProgramFlags flags) {
		return constructQuadraticProgram(flags);
	}
	
}
