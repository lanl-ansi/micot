package gov.lanl.micot.util.io.data;

/**
 * Simple implementation of an entry
 * @author Russell Bent
 */
public class EntryImpl implements Entry {

	private String entryName = null;
	private Class<?> entryType = null;
	
	/**
	 * Constructor
	 * @param entryName
	 * @param entryType
	 */
	public EntryImpl(String entryName, Class<?> entryType) {
		super();
		this.entryName = entryName;
		this.entryType = entryType;
	}

	@Override
	public String getEntryName() {
		return entryName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((entryName == null) ? 0 : entryName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EntryImpl other = (EntryImpl) obj;
		if (entryName == null) {
			if (other.entryName != null)
				return false;
		}
		else if (!entryName.equals(other.entryName))
			return false;
		return true;
	}

	@Override
	public Class<?> getEntryType() {
		return entryType;
	}

}
