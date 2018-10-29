package gov.lanl.micot.util.io.geometry.geojson;

import gov.lanl.micot.util.io.data.EntryImpl;

/**
 * Simple implementation of a geo json entry
 * @author Russell Bent
 */
public class GeoJSONEntryImpl extends EntryImpl implements GeoJSONEntry {

	private int    entryIndex = -1;
	
	/**
	 * Constructor
	 * @param entryName
	 * @param entryIndex
	 * @param entryType
	 */
	public GeoJSONEntryImpl(String entryName, int entryIndex, Class<?> entryType) {
		super(entryName,entryType);
		this.entryIndex = entryIndex;
	}

	@Override
	public int getEntryIndex() {
		return entryIndex;
	}

}
