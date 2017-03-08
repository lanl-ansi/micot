package gov.lanl.micot.util.math.solver.quadraticprogram.cplex;

import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgramFlags;
import gov.lanl.micot.util.math.solver.quadraticprogram.QuadraticProgramFactory;


/**
 * Factory for creating CPlex quadratic programs
 * @author Russell Bent
 */
public class CPLEXQuadraticProgramFactory implements QuadraticProgramFactory {

	/**
	 * Constructor
	 */
	public CPLEXQuadraticProgramFactory() {		
	}

	@Override
	public MathematicalProgram constructQuadraticProgram(MathematicalProgramFlags flags) {
		CPLEXQuadraticProgramFlags newFlags = new CPLEXQuadraticProgramFlags();
		newFlags.fill(flags);		
		return new CPLEXQuadraticProgram(newFlags);
	}

	@Override
	public MathematicalProgram constructMathematicalProgram(MathematicalProgramFlags flags) {
		return constructQuadraticProgram(flags);
	}
	
}
