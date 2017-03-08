package gov.lanl.micot.infrastructure.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

/**
 * Class for representing edges
 * @author Russell Bent
 */
public class EdgeImpl implements Serializable, Edge {

	protected static final long serialVersionUID = 0;
	
	private Set<Connection> connections = null;
	private Connection primaryConnection = null;
	
	/**
	 * Constructor
	 * @param root
	 */
	public EdgeImpl(Connection primaryConnection) {
	  connections = new HashSet<Connection>();	  
	  connections.add(primaryConnection);
	  this.primaryConnection = primaryConnection;
	}
	
	@Override
	public void addConnection(Connection connection) {
	  connections.add(connection);
	}
	
	@Override
	public void removeConnection(Connection connection) {
	  connections.remove(connection);
	  if (primaryConnection.equals(connection)) {
	    primaryConnection = null;
	  }
	}
	
	@Override
	public Connection getPrimaryConnection() {
	  return primaryConnection;
	}
	
	@Override
	public String toString() {
	  return getPrimaryConnection().toString();
	}

	@Override
	public int compareTo(Edge arg0) {
	  return getPrimaryConnection().compareTo(arg0.getPrimaryConnection());
	}

	
	@Override
	public int hashCode() {
		return getPrimaryConnection().hashCode();
	}

	@Override
	public boolean equals(Object obj) {	
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Edge other = (Edge) obj;
		return getPrimaryConnection().equals(other.getPrimaryConnection());
	}

	@Override
  @SuppressWarnings("unchecked")
  public <E extends Connection> Collection<E> getConnections(Class<E> cls) {
    Vector<E> vector = new Vector<E>();
    for (Connection connection : connections) {
      if (cls.isAssignableFrom(connection.getClass())) {
        vector.add((E)connection);
      }
    }
    return vector;
  }
}
