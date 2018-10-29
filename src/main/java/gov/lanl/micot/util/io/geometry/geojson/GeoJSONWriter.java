package gov.lanl.micot.util.io.geometry.geojson;

import java.util.Collection;

import gov.lanl.micot.util.io.geometry.GeometryEntryData;

/**
 * An interface for writing geo json files
 * @author Russell Bent
 */
public interface GeoJSONWriter {
	
	/**
	 * Function for writing the shapefile
	 * @param filename
	 * @param entries
	 * @param data
	 */
	public void write(String filename, Collection<GeoJSONEntry> entries, Collection<GeometryEntryData> data);
	
}
