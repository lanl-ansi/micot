package gov.lanl.micot.util.collection;

/**
 * An implementation of matrix that is dense
 * @author Russell Bent
 *
 * @param <E>
 */
public class DoubleDenseMatrix extends DenseMatrix<Double> {

	/**
	 * Constructor
	 * @param rows
	 * @param columns
	 */
	public DoubleDenseMatrix(int rows, int columns) {
		super(rows,columns);
	}
	
}
