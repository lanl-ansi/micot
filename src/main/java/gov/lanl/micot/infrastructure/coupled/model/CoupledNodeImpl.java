package gov.lanl.micot.infrastructure.coupled.model;

import java.io.Serializable;

import gov.lanl.micot.infrastructure.model.Component;
import gov.lanl.micot.infrastructure.model.NodeImpl;

/**
 * Abstract class for representing nodes
 * @author Russell Bent
 */
public class CoupledNodeImpl extends NodeImpl implements Serializable, CoupledNode  {

	protected static final long serialVersionUID = 0;
	
	public CoupledNodeImpl(CoupledComponent root) {
		super(root);		
	}

  @Override
  public Component getPrimaryComponent() {
    return getNodeObject();
  }


	



}
