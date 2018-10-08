package gov.lanl.micot.util.io.geometry.shapefile;

import java.util.Collection;

import gov.lanl.micot.util.io.geometry.GeometryEntryData;

/**
 * An interface for writing shapefiles
 * @author Russell Bent
 */
public interface ShapefileWriter {
	
	/**
	 * Function for writing the shapefile
	 * @param filename
	 * @param entries
	 * @param data
	 */
	public void write(String filename, Collection<ShapefileEntry> entries, Collection<GeometryEntryData> data);
	
}
