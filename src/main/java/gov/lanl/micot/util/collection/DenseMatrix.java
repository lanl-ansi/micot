package gov.lanl.micot.util.collection;

import java.util.HashSet;
import java.util.Set;

/**
 * An implementation of matrix that is dense
 * 
 * @author Russell Bent
 * 
 * @param <E>
 */
public class DenseMatrix<E> implements Matrix<E> {

  private E matrix[][] = null;

  /**
   * Constructor
   * 
   * @param rows
   * @param columns
   */
  @SuppressWarnings("unchecked")
  public DenseMatrix(int rows, int columns) {
    matrix = (E[][]) new Object[rows][columns];
  }

  @Override
  public E get(int i, int j) {
    return matrix[i][j];
  }

  @Override
  public void set(int i, int j, E value) {
    matrix[i][j] = value;
  }

  @Override
  public int getRows() {
    return matrix.length;
  }

  @Override
  public int getColumns() {
    return matrix[0].length;
  }

  @Override
  public Set<Pair<Integer, E>> getNonEmptyEntries(int row) {
    Set<Pair<Integer, E>> entries = new HashSet<Pair<Integer, E>>();
    for (int j = 0; j < getColumns(); ++j) {
      if (matrix[row][j] != null) {
        entries.add(new Pair<Integer, E>(j, matrix[row][j]));
      }
    }
    return entries;
  }

  @Override
  public String toString() {
    String str = "";
    for (int i = 0; i < getRows(); ++i) {
      for (int j = 0; j < getColumns(); ++j) {
        str += get(i, j) + " ";
      }
      str += System.getProperty("line.separator");
    }
    return str;
  }

}
