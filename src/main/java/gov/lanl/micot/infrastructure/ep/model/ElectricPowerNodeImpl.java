package gov.lanl.micot.infrastructure.ep.model;

import java.io.Serializable;
import java.util.Collection;

import gov.lanl.micot.infrastructure.model.Component;
import gov.lanl.micot.infrastructure.model.NodeImpl;

/**
 * Abstract class for representing nodes
 * @author Russell Bent
 */
public class ElectricPowerNodeImpl extends NodeImpl implements Serializable, ElectricPowerNode  {

	protected static final long serialVersionUID = 0;
	
	public ElectricPowerNodeImpl(Component root) {
		super(root);		
	}


	/**
	 * Gets the proper return value of the root
	 * @return
	 */
	public Bus getBus() {
		return (Bus)getNodeObject();
	}
	
	@Override
  public ShuntCapacitor getShunt() {
    Collection<ShuntCapacitor> shunts = (Collection<ShuntCapacitor>) getComponents(ShuntCapacitor.class);
    if (shunts.size() == 0) {
      return null;
    }
    return shunts.iterator().next();
  }
	
	@Override
	public Load getLoad() {
	  Collection<Load> loads = (Collection<Load>) getComponents(Load.class);
	  if (loads.size() == 0) {
	    return null;
	  }
	  return loads.iterator().next();
	}
	
	@Override
  public Generator getGenerator() {
    Collection<Generator> generators = (Collection<Generator>) getComponents(Generator.class);
    if (generators.size() == 0) {
      return null;
    }
    return generators.iterator().next();
  }


  @Override
  public Component getPrimaryComponent() {
    return getBus();
  }
}
