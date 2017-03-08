package gov.lanl.micot.infrastructure.model;

import gov.lanl.micot.infrastructure.model.Component;

/**
 * Conveys information about dependencies between components
 * @author Russell Bent
 */
public class NodeImplEdge {

	Component parent = null;
	Component child =  null;
	
	/**
	 * Constructor
	 * @param parent
	 * @param child
	 */
	public NodeImplEdge(Component parent, Component child) {
		super();
		this.parent = parent;
		this.child = child;
	}

	/**
	 * @return the parent
	 */
	public Component getParent() {
		return parent;
	}

	/**
	 * @return the child
	 */
	public Component getChild() {
		return child;
	}	
}
