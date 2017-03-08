package gov.lanl.micot.util.math.matrix;

import gov.lanl.micot.util.collection.Matrix;

/**
 * An interface for Matrix Math
 * @author Russell Bent
 */
public interface MatrixMath {

	/**
	 * Add an equation
	 * @param constraint
	 */
	public Matrix<Number> invert(Matrix<Number> matrix);
	
	/**
	 * multiply two matricies
	 * @param matrix1
	 * @param matrix2
	 * @return
	 */
	public Matrix<Number> multiply(Matrix<Number> matrix1, Matrix<Number> matrix2);
	
	/**
	 * Compute the rank of a matrix
	 * @param matrix
	 * @return
	 */
	public int rank(Matrix<Number> matrix);
	
	/**
	 * Compute the determinant of a matrix
	 * @param matrix
	 * @return
	 */
	public double determinant(Matrix<Number> matrix);
	
}
