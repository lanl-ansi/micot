package gov.lanl.micot.util.collection;

/**
 * An implementation of matrix that is dense
 * @author Russell Bent
 *
 * @param <E>
 */
public class IntegerDenseMatrix extends DenseMatrix<Integer> {

	/**
	 * Constructor
	 * @param rows
	 * @param columns
	 */
	public IntegerDenseMatrix(int rows, int columns) {
		super(rows,columns);
	}
	
}
