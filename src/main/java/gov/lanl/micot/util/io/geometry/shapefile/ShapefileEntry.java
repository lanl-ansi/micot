package gov.lanl.micot.util.io.geometry.shapefile;

import gov.lanl.micot.util.io.data.Entry;

/**
 * This is an interface for writing shapefile entries
 * @author Russell Bent
 */
public interface ShapefileEntry extends Entry {
		
	/**
	 * Get the index number of the entry
	 * @return
	 */
	public int getEntryIndex();
	
}
