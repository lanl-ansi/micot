package gov.lanl.micot.util.math.solver.quadraticprogram.ipopt;

import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgramFlags;
import gov.lanl.micot.util.math.solver.quadraticprogram.QuadraticProgramFactory;


/**
 * Factory for creating IpOpt quadratic programs
 * @author Russell Bent
 */
public class IpOptQuadraticProgramFactory implements QuadraticProgramFactory {

	/**
	 * Constructor
	 */
	public IpOptQuadraticProgramFactory() {		
	}

	@Override
	public MathematicalProgram constructQuadraticProgram(MathematicalProgramFlags flags) {
		IpOptQuadraticProgramFlags newFlags = new IpOptQuadraticProgramFlags();
		newFlags.fill(flags);		
		return new IpOptQuadraticProgram(newFlags);
	}

	@Override
	public MathematicalProgram constructMathematicalProgram(MathematicalProgramFlags flags) {
		return constructQuadraticProgram(flags);
	}
	
}
