package gov.lanl.micot.util.collection;

/**
 * An implementation of matrix that is dense
 * @author Russell Bent
 *
 * @param <E>
 */
public class IntegerSparseMatrix extends SparseMatrix<Integer> {

	/**
	 * Constructor
	 * @param rows
	 * @param columns
	 */
	public IntegerSparseMatrix() {
		super();
	}
	
	@Override
	public Integer get(int i, int j) {
		Integer value = super.get(i, j);
		if (value == null) {
			return 0;
		}
		return value;
	}
}
