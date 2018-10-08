package gov.lanl.micot.util.io.data;

/**
 * This is an interface for writing shapefile entries
 * @author Russell Bent
 */
public interface Entry {
	
	/**
	 * Get the name of the entry
	 * @return
	 */
	public String getEntryName();
		
	/**
	 * Get the type stored in the entry
	 * @return
	 */
	public Class<?> getEntryType();
	
	

}
