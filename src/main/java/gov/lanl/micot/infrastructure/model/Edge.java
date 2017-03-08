package gov.lanl.micot.infrastructure.model;

import java.util.Collection;

/**
 * Interface for edge elements
 * @author Russell Bent
 */
public interface Edge extends Comparable<Edge> {

	/**
	 * Adds a connection to the edge
	 * @param connection
	 */
	public abstract void addConnection(Connection connection);

	/**
	 * Removes a connection from the edge
	 * @param connection
	 */
	public abstract void removeConnection(Connection connection);

	/**
	 * Get all the connections associated with an edge
	 * @param cls
	 * @return
	 */
	public <E extends Connection> Collection<E> getConnections(Class<E> cls);

	/**
   * Get the primary connection of the edge
   * @return
   */
  public Connection getPrimaryConnection();

	
	
}