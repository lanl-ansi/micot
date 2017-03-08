package gov.lanl.micot.util.graph.jung;

/**
 * A simple wrapper for edges in the jung format to have
 * a robust edge factory for JUNG max flow calculations
 * @author Russell Bent
 */
public class JUNGMaxFlowEdge<E> {

	private E wrappedEdge = null;
	
	/**
	 * Constructor
	 * @param edge
	 */
	protected JUNGMaxFlowEdge(E edge) {
		wrappedEdge = edge;
	}
	
	/**
	 * Constructor needed for JUNG 
	 */
	public JUNGMaxFlowEdge() {
		wrappedEdge = null;
	}
	
	/**
	 * Gets the wrapped edge
	 * @return
	 */
	protected E getWrappedEdge() {
		return wrappedEdge;
	}
	
}
