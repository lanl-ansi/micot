package gov.lanl.micot.util.io.geometry;

import java.util.HashMap;
import java.util.Set;

import gov.lanl.micot.util.geometry.Geometry;

/**
 * An implementation of shapefile entry data
 * @author Russell Bent
 */
public class GeometryEntryDataImpl extends HashMap<gov.lanl.micot.util.io.data.Entry, Object> implements GeometryEntryData {

	private static final long	serialVersionUID	= 1L;
	
	@Override
	public Object put(gov.lanl.micot.util.io.data.Entry entry, Object data) {
		return super.put(entry, data);
	}

	@Override
	public Object get(gov.lanl.micot.util.io.data.Entry entry) {
		return super.get(entry);
	}

	@Override
	public Set<gov.lanl.micot.util.io.data.Entry> getEntries() {
		return keySet();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <E> E get(gov.lanl.micot.util.io.data.Entry entry, Class<E> cls) {
		return (E) super.get(entry);
	}

	@Override
	public Object get(String entryName) {
		for (gov.lanl.micot.util.io.data.Entry entry : keySet()) {
			if (entry.getEntryName().equals(entryName)) {
				return get(entry);
			}
		}
		return null;
	}

	@Override
	public <E> E get(String entryName, Class<E> cls) {
		for (gov.lanl.micot.util.io.data.Entry entry : keySet()) {
			if (entry.getEntryName().equals(entryName)) {
				return get(entry, cls);
			}
		}
		return null;
	}

	@Override
	public Geometry getGeometry() {
		for (gov.lanl.micot.util.io.data.Entry entry : keySet()) {
			if (Geometry.class.isAssignableFrom(entry.getEntryType())) {
				return get(entry, Geometry.class);
			}
		}
		return null;
	}

}
