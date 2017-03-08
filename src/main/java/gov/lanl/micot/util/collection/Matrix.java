package gov.lanl.micot.util.collection;

import java.util.Set;

/**
 * An interface for matrices
 * @author Russell Bent
 *
 */
public interface Matrix<E> {

	/**
	 * Get the entry
	 * @param i
	 * @param j
	 * @return
	 */
	public E get(int i, int j);
	
	/**
	 * Set the entry
	 * @param i
	 * @param j
	 * @param value
	 */
	public void set(int i, int j, E value);
	
	/**
	 * Get the number of rows
	 * @return
	 */
	public int getRows();
	
	/**
	 * get the number of columns
	 * @return
	 */
	public int getColumns();
	

	/**
	 * Get the set of non empty entries
	 * @param row
	 * @return
	 */
	public Set<Pair<Integer,E>> getNonEmptyEntries(int row);

}
