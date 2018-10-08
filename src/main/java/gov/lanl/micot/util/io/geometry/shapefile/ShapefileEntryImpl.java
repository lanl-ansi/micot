package gov.lanl.micot.util.io.geometry.shapefile;

import gov.lanl.micot.util.io.data.EntryImpl;

/**
 * Simple implementation of a shapefile entry
 * @author Russell Bent
 */
public class ShapefileEntryImpl extends EntryImpl implements ShapefileEntry {

	private int    entryIndex = -1;
	
	/**
	 * Constructor
	 * @param entryName
	 * @param entryIndex
	 * @param entryType
	 */
	public ShapefileEntryImpl(String entryName, int entryIndex, Class<?> entryType) {
		super(entryName,entryType);
		this.entryIndex = entryIndex;
	}

	@Override
	public int getEntryIndex() {
		return entryIndex;
	}

}
