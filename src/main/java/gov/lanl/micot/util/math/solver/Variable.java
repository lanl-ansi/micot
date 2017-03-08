package gov.lanl.micot.util.math.solver;

/**
 * Definition for a math variable
 * @author Russell Bent, Carleton Coffrin
 *
 */
public interface Variable extends Comparable<Variable> {

	/**
	 * All a visitor of the variable
	 * @param v
	 */
	public void visit(VariableVisitor v);
	
	/**
	 * Get the name of the variable
	 * @return
	 */
	public String getName();
	
}
