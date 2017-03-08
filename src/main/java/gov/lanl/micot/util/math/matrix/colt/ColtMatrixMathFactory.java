package gov.lanl.micot.util.math.matrix.colt;

import gov.lanl.micot.util.math.matrix.MatrixMath;
import gov.lanl.micot.util.math.matrix.MatrixMathFactory;

/**
 * Factory for creating Colt Matrix Math packages
 * @author Russell Bent
 */
public class ColtMatrixMathFactory extends MatrixMathFactory {

	/**
	 * Constructor
	 */
	public ColtMatrixMathFactory() {
	}

	@Override
	public MatrixMath constructMatrixMath() {
		return new ColtMatrixMath();
	}
	
	
}
