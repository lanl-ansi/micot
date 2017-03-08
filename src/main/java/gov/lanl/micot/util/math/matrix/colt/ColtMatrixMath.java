package gov.lanl.micot.util.math.matrix.colt;

import java.util.Set;

import cern.colt.list.DoubleArrayList;
import cern.colt.list.IntArrayList;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;
import cern.colt.matrix.linalg.Algebra;
import gov.lanl.micot.util.collection.DenseMatrix;
import gov.lanl.micot.util.collection.Matrix;
import gov.lanl.micot.util.collection.Pair;
import gov.lanl.micot.util.collection.SparseMatrix;
import gov.lanl.micot.util.math.matrix.MatrixMath;

/**
 * An implementation of linear algebra in the colt program
 * @author Russell Bent
 *
 */
public class ColtMatrixMath implements MatrixMath {

	/**
	 * Protected constructor
	 */
	public ColtMatrixMath() {
		super();
	}

	/**
	 * Creates a colt specific matrix
	 * @param matrix
	 * @return
	 */
	private DoubleMatrix2D createColtMatrix(Matrix<Number> matrix) {
		DoubleMatrix2D coltMatrix = (matrix instanceof DenseMatrix) ? new DenseDoubleMatrix2D(matrix.getRows(), matrix.getColumns()) :  new SparseDoubleMatrix2D(matrix.getRows(), matrix.getColumns());
	
		if (matrix instanceof DenseMatrix) {
			for (int i = 0; i < matrix.getRows(); ++i) {
				for (int j = 0; j < matrix.getColumns(); ++j) {
					coltMatrix.set(i, j, matrix.get(i,j).doubleValue());
				}
			}
		}
		else {
			for (int i = 0; i < matrix.getRows(); ++i) {
				Set<Pair<Integer,Number>> values = matrix.getNonEmptyEntries(i);
				for (Pair<Integer,Number> value : values) {
					coltMatrix.set(i, value.getOne(), value.getTwo().doubleValue());
				}
			}
		}
		
		return coltMatrix;
	}
	
	/**
	 * Convert a colt matrix into a generic one
	 * @param matrix
	 * @return
	 */
	private Matrix<Number> createMatrix(DoubleMatrix2D matrix) {
		Matrix<Number> newMatrix = matrix instanceof DenseDoubleMatrix2D ? new DenseMatrix<Number>(matrix.rows(), matrix.columns()) : new SparseMatrix<Number>();
		
		if (matrix instanceof DenseDoubleMatrix2D) {
			for (int i = 0; i < matrix.rows(); ++i) {
				for (int j = 0; j < matrix.columns(); ++j) {
					newMatrix.set(i, j, matrix.get(i,j));
				}
			}
		}
		else {
			IntArrayList rows = new IntArrayList();
			IntArrayList columns = new IntArrayList();
			DoubleArrayList values = new DoubleArrayList();
			matrix.getNonZeros(rows, columns, values);
			
			for (int i = 0; i < rows.size(); ++i) {
				newMatrix.set(rows.get(i), columns.get(i), values.get(i));
			}
		}					
		return newMatrix;
	}

	@Override
	public Matrix<Number> invert(Matrix<Number> matrix) {
		Algebra algebra = new Algebra();
		return createMatrix(algebra.inverse(createColtMatrix(matrix)));
	}

	@Override
	public Matrix<Number> multiply(Matrix<Number> matrix1, Matrix<Number> matrix2) {
		Algebra algebra = new Algebra();
		return createMatrix(algebra.mult(createColtMatrix(matrix1), createColtMatrix(matrix2)));
	}

	@Override
	public int rank(Matrix<Number> matrix) {
		Algebra algebra = new Algebra();
		return algebra.rank(createColtMatrix(matrix));
	}

	@Override
	public double determinant(Matrix<Number> matrix) {
		Algebra algebra = new Algebra();
		return algebra.det(createColtMatrix(matrix));
	}
	
	
}
