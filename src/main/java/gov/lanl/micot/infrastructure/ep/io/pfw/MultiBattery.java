package gov.lanl.micot.infrastructure.ep.io.pfw;

import java.util.Collection;

import gov.lanl.micot.infrastructure.ep.model.Battery;
import gov.lanl.micot.infrastructure.ep.model.BatteryImpl;
import gov.lanl.micot.infrastructure.model.Asset;
import gov.lanl.micot.util.geometry.Point;

/**
 * Class for turning a multi battery data into a single one, when necessary
 * @author Russell Bent
 *
 */
public class MultiBattery extends BatteryImpl {

	private Collection<Battery> data = null;
	
	/**
	 * Constructor
	 * @param data
	 */
	public MultiBattery(Collection<Battery> data) {
	  super((long) data.iterator().next().getAttribute(Battery.ASSET_ID_KEY,Integer.class));
		this.data = data;
	}

	@Override
	public boolean getStatus() {
	  boolean status = false;
	  for (Battery d : data) {
	    status |= d.getStatus();
	  }
	  return status;
	}

	@Override
	public void setStatus(boolean b) {
		for (Battery d : data) {
			d.setStatus(b);
		}
	}

  @Override
  public Point getCoordinate() {
    return data.iterator().next().getCoordinate();
  }

  @Override
  public void setCoordinate(Point point) {
    for (Battery d : data) {
      d.setCoordinate(point);
    }
  }

  @Override
  public int compareTo(Asset arg0) {
    return data.iterator().next().compareTo(arg0);
  }

  @Override
  public Object getAttribute(Object key) {
    return data.iterator().next().getAttribute(key);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <E> E getAttribute(Object key, Class<E> cls) {
    return (E)getAttribute(key);
  }
  
  @Override
  public void setAttribute(Object key, Object value) {
    for (Battery generator : data) {
      generator.setAttribute(key,value);
    }
  }
  
}
