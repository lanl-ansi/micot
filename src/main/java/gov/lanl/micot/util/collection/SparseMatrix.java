package gov.lanl.micot.util.collection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * An implementation of matrix that is sparse
 * @author Russell Bent
 *
 * @param <E>
 */
public class SparseMatrix<E> implements Matrix<E> {

	private ArrayList<Map<Integer,E>> map = null;
	private int rows = 0;
	private int columns = 0;
	
	/**
	 * Constructor
	 * @param rows
	 * @param columns
	 */
	public SparseMatrix() {
		map = new ArrayList<Map<Integer,E>>();
		rows = 0;
		columns = 0;
	}
	
	/**
	 * Constructor
	 * @param rows
	 * @param columns
	 */
	public SparseMatrix(int rows, int columns) {
    map = new ArrayList<Map<Integer,E>>();
    this.rows = rows;
    this.columns = columns;	 
    for (int i = 0; i < rows; ++i) {
      map.add(new HashMap<Integer,E>());
    }
	}
	
	@Override
	public E get(int i, int j) {
		if (map.get(i) == null) {
			return null;
		}
		return map.get(i).get(j);
	}

	@Override
	public void set(int i, int j, E value) {
		while (map.size() <= i) {
			map.add(null);
		}		
		if (map.get(i) == null) {
			map.set(i, new HashMap<Integer,E>());
		}
		map.get(i).put(j,value);
		columns = Math.max(j+1, columns);
		rows = Math.max(i+1, rows);
	}

	@Override
	public int getRows() {
		return rows;
	}
	
	@Override
	public int getColumns() {
		return columns;
	}

	@Override
	public Set<Pair<Integer,E>> getNonEmptyEntries(int row) {
		Set<Pair<Integer,E>> entries = new HashSet<Pair<Integer,E>>();
		Map<Integer,E> r = map.get(row);
		if (r != null) {		
		  for (int key : r.keySet()) {
			  entries.add(new Pair<Integer,E>(key,r.get(key)));
		  }		
		}
		return entries;
	}
	
	@Override
	public String toString() {
	  String str = "";
	  for (int i = 0; i < getRows(); ++i) {
	    for (int j = 0; j < getColumns(); ++j) {
	      str += get(i,j) + " ";
	    }
	    str += System.getProperty("line.separator");
	  }	  
	  return str;
	}
	
}
