package gov.lanl.micot.util.math.solver.quadraticprogram.bonmin;

import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgramFlags;
import gov.lanl.micot.util.math.solver.quadraticprogram.QuadraticProgramFactory;


/**
 * Factory for creating Bonmin quadratic programs
 * @author Russell Bent
 */
public class BonminQuadraticProgramFactory implements QuadraticProgramFactory {

	/**
	 * Constructor
	 */
	public BonminQuadraticProgramFactory() {		
	}

	@Override
	public MathematicalProgram constructQuadraticProgram(MathematicalProgramFlags flags) {
		BonminQuadraticProgramFlags newFlags = new BonminQuadraticProgramFlags();
		newFlags.fill(flags);		
		return new BonminQuadraticProgram(newFlags);
	}

	@Override
	public MathematicalProgram constructMathematicalProgram(MathematicalProgramFlags flags) {
		return constructQuadraticProgram(flags);
	}
	
}
