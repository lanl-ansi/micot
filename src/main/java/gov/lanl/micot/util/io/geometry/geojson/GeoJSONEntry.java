package gov.lanl.micot.util.io.geometry.geojson;

import gov.lanl.micot.util.io.data.Entry;

/**
 * This is an interface for writing geo json entries
 * @author Russell Bent
 */
public interface GeoJSONEntry extends Entry {
		
	/**
	 * Get the index number of the entry
	 * @return
	 */
	public int getEntryIndex();
	
}
