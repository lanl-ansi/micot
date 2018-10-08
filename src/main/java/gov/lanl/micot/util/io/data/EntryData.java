package gov.lanl.micot.util.io.data;

import java.util.Set;

import gov.lanl.micot.util.io.data.Entry;

/**
 * An interface for associating actual data with an entry
 * @author Russell Bent
 */
public interface EntryData {

	/**
	 * Put the data
	 * @param entry
	 * @param data
	 */
	public Object put(Entry entry, Object data);
	
	/**
	 * Get the data
	 * @param entry
	 * @return 
	 */
	public Object get(Entry entry);

	/**
	 * Get the data in a particular format
	 * @param entry
	 * @return 
	 */
	public<E> E get(Entry entry, Class<E> cls);

	/**
	 * Get the data
	 * @param entry
	 * @return 
	 */
	public Object get(String entryName);

	/**
	 * Get the data in a particular format
	 * @param entry
	 * @return 
	 */
	public<E> E get(String entryName, Class<E> cls);
		
	/**
	 * Get all the entries
	 * @return
	 */
	public Set<Entry> getEntries();
	
}
