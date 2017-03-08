package gov.lanl.micot.infrastructure.transportation.road.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import gov.lanl.micot.infrastructure.model.ComponentImpl;

/**
 * Some common bus implementation information
 * 
 * @author Russell Bent
 */
public class IntersectionImpl extends ComponentImpl implements Intersection {

  protected static final long      serialVersionUID    = 1L;
  private Set<IntersectionChangeListener> listeners           = null;

  /**
   * Constructor
   */
  protected IntersectionImpl(long assetId) {
    super();
    listeners = new HashSet<IntersectionChangeListener>();
    setAttribute(Origin.ASSET_ID_KEY, assetId);
    setAttribute(SHORTEST_DISTANCE_KEY, new HashMap<Intersection,Double>());
    setAttribute(SHORTEST_TIME_KEY, new HashMap<Intersection,Double>());
  }

  /**
   * Constructor
   * @param battery
   */
//  public IntersectionImpl(Intersection intersection) {
 //   super(intersection);
  //  listeners = new HashSet<IntersectionChangeListener>();    
  //}
  
  @Override
  public void addIntersectionChangeListener(IntersectionChangeListener listener) {
    listeners.add(listener);
  }

  @Override
  public void removeIntersectionChangeListener(IntersectionChangeListener listener) {
    listeners.remove(listener);
  }

  /**
   * Fires a change in bus data
   */
  private void fireIntersectionDataChangeEvent(Object attribute) {
    for (IntersectionChangeListener listener : listeners) {
      listener.intersectionDataChanged(this, attribute);
    }
  }
  
  @Override
  public void setAttribute(Object key, Object object) {
    super.setAttribute(key,object);
    fireIntersectionDataChangeEvent(key);
  }

	@Override
	@SuppressWarnings("unchecked")
	public void setShortestDistance(Intersection intersection, double distance) {
		Map<Intersection,Double> map = getAttribute(SHORTEST_DISTANCE_KEY,Map.class);
		map.put(intersection, distance);	
	}

	@Override
	@SuppressWarnings("unchecked")
	public Double getShortestDistance(Intersection intersection) {
		Map<Intersection,Double> map = getAttribute(SHORTEST_DISTANCE_KEY,Map.class);
		return map.get(intersection);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void setShortestTime(Intersection intersection, double distance) {
		Map<Intersection,Double> map = getAttribute(SHORTEST_TIME_KEY,Map.class);
		map.put(intersection, distance);	
	}

	@Override
	@SuppressWarnings("unchecked")
	public Double getShortestTime(Intersection intersection) {
		Map<Intersection,Double> map = getAttribute(SHORTEST_TIME_KEY,Map.class);
		return map.get(intersection);
	}
	
  @Override
  public IntersectionImpl clone() {
    //IntersectionImpl newIntersection = new IntersectionImpl((Intersection)getBaseData());
    IntersectionImpl newIntersection = new IntersectionImpl(getAttribute(ASSET_ID_KEY,Long.class));
    
    try {
      deepCopy(newIntersection);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return newIntersection;
  }

}
