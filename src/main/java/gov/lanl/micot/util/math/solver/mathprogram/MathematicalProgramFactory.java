package gov.lanl.micot.util.math.solver.mathprogram;

/**
 * Interface for Matematical Program Factories
 * @author Russell Bent
 */
public interface MathematicalProgramFactory {

	public MathematicalProgram constructMathematicalProgram(MathematicalProgramFlags flags);	
}
