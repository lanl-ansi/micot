package gov.lanl.micot.infrastructure.transportation.road.model;

import java.io.Serializable;

import gov.lanl.micot.infrastructure.model.Component;
import gov.lanl.micot.infrastructure.model.NodeImpl;

/**
 * Abstract class for representing nodes
 * @author Russell Bent
 */
public class RoadNodeImpl extends NodeImpl implements Serializable, RoadNode  {

	protected static final long serialVersionUID = 0;
	
	/**
	 * Constructor
	 * @param root
	 */
	public RoadNodeImpl(Component root) {
		super(root);		
	}

	/**
	 * Gets the proper return value of the root
	 * @return
	 */
	public Intersection getIntersection() {
		return (Intersection)getNodeObject();
	}

  @Override
  public Component getPrimaryComponent() {
    return getIntersection();
  }
	
}
