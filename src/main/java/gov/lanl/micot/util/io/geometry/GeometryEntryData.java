package gov.lanl.micot.util.io.geometry;

import gov.lanl.micot.util.geometry.Geometry;
import gov.lanl.micot.util.io.data.EntryData;

/**
 * An interface for associating actual data with a geometry entries
 * @author Russell Bent
 */
public interface GeometryEntryData extends EntryData {

	/**
	 * Get the geometry of the shapefile
	 * @return
	 */
	public Geometry getGeometry();
	
}
