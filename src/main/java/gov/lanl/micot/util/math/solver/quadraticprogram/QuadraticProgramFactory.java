package gov.lanl.micot.util.math.solver.quadraticprogram;

import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgramFactory;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgramFlags;

/**
 * Interface for quadratic program factories
 * @author Russell Bent
 */
public interface QuadraticProgramFactory extends MathematicalProgramFactory {
	
	/**
	 * Function for constructing a linear program
	 * @return
	 */
	public MathematicalProgram constructQuadraticProgram(MathematicalProgramFlags flags);
	
}
