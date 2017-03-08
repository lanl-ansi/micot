package gov.lanl.micot.util.math.matrix;

//import gov.lanl.micot.util.math.matrix.colt.ColtMatrixMathFactory;

/**
 * Interface for matrix operations
 * @author Russell Bent
 */
public class MatrixMathFactory {

  public static final String MATRIX_MATH_CLASS          = "gov.lanl.micot.util.math.matrix.colt.ColtMatrixMath";
  
	private static MatrixMathFactory INSTANCE  = null;
	
	public static MatrixMathFactory getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new MatrixMathFactory();
		}
		return INSTANCE;
	}

	/**
	 * Constructor
	 */
	protected MatrixMathFactory() {		
	}
	
	/**
	 * Constructs the matrix math
	 * @return
	 */
	public MatrixMath constructMatrixMath() {
		try {
      return (MatrixMath) Class.forName(MATRIX_MATH_CLASS).newInstance();
    }
    catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
      e.printStackTrace();
    }
		return null;
	}
	
	
}
