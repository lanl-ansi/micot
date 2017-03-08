package gov.lanl.micot.infrastructure.model;

import java.util.Collection;

/**
 * Interface for node elements
 * @author Russell Bent
 */
public interface Node extends Comparable<Node> {

	/**
	 * Adds a component to the node
	 * @param component
	 */
	public abstract void addComponent(Component component);

	/**
	 * Removes a component from the node
	 * @param component
	 */
	public abstract void removeComponent(Component component);

	/**
	 * Get all the components associated with a node
	 * @param cls
	 * @return
	 */
	public <E extends Component> Collection<E> getComponents(Class<E> cls);

	/**
	 * Get the primary component of a node
	 * @return
	 */
  public abstract Component getPrimaryComponent();

}