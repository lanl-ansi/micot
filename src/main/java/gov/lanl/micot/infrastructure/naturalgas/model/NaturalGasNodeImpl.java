package gov.lanl.micot.infrastructure.naturalgas.model;

import java.io.Serializable;
import java.util.Collection;

import gov.lanl.micot.infrastructure.model.Component;
import gov.lanl.micot.infrastructure.model.NodeImpl;

/**
 * Abstract class for representing nodes
 * @author Russell Bent
 */
public class NaturalGasNodeImpl extends NodeImpl implements Serializable, NaturalGasNode  {

	protected static final long serialVersionUID = 0;
	
	/**
	 * Constructor
	 * @param root
	 */
	public NaturalGasNodeImpl(Component root) {
		super(root);		
	}

	/**
	 * Gets the proper return value of the root
	 * @return
	 */
	public Junction getJunction() {
		return (Junction)getNodeObject();
	}
		
	@Override
	public CityGate getCityGate() {
	  Collection<CityGate> gates = (Collection<CityGate>) getComponents(CityGate.class);
	  if (gates.size() == 0) {
	    return null;
	  }
	  return gates.iterator().next();
	}
	
	@Override
  public Well getWell() {
    Collection<Well> wells = (Collection<Well>) getComponents(Well.class);
    if (wells.size() == 0) {
      return null;
    }
    return wells.iterator().next();
  }
	
	@Override
	public Reservoir getReservoir() {
	  Collection<Reservoir> reservoirs = (Collection<Reservoir>) getComponents(Reservoir.class);
	  if (reservoirs.size() == 0) {
	    return null;
	  }
	  return reservoirs.iterator().next();
	}

  @Override
  public Component getPrimaryComponent() {
    return getJunction();
  }
}
