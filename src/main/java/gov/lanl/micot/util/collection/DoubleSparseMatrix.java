package gov.lanl.micot.util.collection;

/**
 * An implementation of matrix that is dense
 * @author Russell Bent
 *
 * @param <E>
 */
public class DoubleSparseMatrix extends SparseMatrix<Double> {

	/**
	 * Constructor
	 * @param rows
	 * @param columns
	 */
	public DoubleSparseMatrix() {
		super();
	}
	
	@Override
	public Double get(int i, int j) {
		Double value = super.get(i, j);
		if (value == null) {
			return 0.0;
		}
		return value;
	}
}
