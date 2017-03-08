package gov.lanl.micot.infrastructure.transportation.road.model;

import java.util.HashSet;
import java.util.Set;

import gov.lanl.micot.infrastructure.model.FlowConnectionImpl;

/**
 * Implementation of a road
 * 
 * @author Russell Bent
 */
public class RoadImpl extends FlowConnectionImpl implements Road {

  protected static final long       serialVersionUID                  = 1L;
  
  private Set<RoadChangeListener> listeners                        = null;

  /**
   * Constructor
   */
  protected RoadImpl(long assetId) {
    super();
    listeners = new HashSet<RoadChangeListener>();
    setAttribute(Road.ASSET_ID_KEY, assetId);
  }

  /**
   * Constructor
   * @param destination
   */
//  public RoadImpl(Road road) {
  //  super(road);
   // listeners = new HashSet<RoadChangeListener>();    
  //}
          

  
  @Override
  public void addRoadChangeListener(RoadChangeListener listener) {
    listeners.add(listener);
  }

  /**
   * Remove the line data listener
   * 
   * @param listener
   */
  public void removeRoadChangeListener(RoadChangeListener listener) {
    listeners.remove(listener);
  }

  /**
   * Notification that line data has changed
   */
  private void fireDataChangeEvent(Object attribute) {
    for (RoadChangeListener listener : listeners) {
      listener.roadDataChanged(this, attribute);
    }
  }

  @Override
  public void setAttribute(Object key, Object object) {
    super.setAttribute(key,object);
    fireDataChangeEvent(key);
  }

  @Override
  public void setSpeedLimit(double speedLimit) {
    setAttribute(SPEED_LIMIT_KEY, speedLimit);
  }

  @Override
  public double getSpeedLimit() {
    return getAttribute(SPEED_LIMIT_KEY, Double.class);
  }

  @Override
  public void setNumberOfLanes(int numberOfLanes) {
    setAttribute(NUMBER_OF_LANES_KEY, numberOfLanes);
  }

  @Override
  public int getNumberOfLanes() {
    return getAttribute(NUMBER_OF_LANES_KEY, Integer.class);
  }

  @Override
  public void setLength(double length) {
    setAttribute(LENGTH_KEY, length);
  }

  @Override
  public double getLength() {
    return getAttribute(LENGTH_KEY, Double.class);
  }

  @Override
  public RoadImpl clone() {
    //RoadImpl newRoad = new RoadImpl((Road)getBaseData());
    RoadImpl newRoad = new RoadImpl(getAttribute(ASSET_ID_KEY,Long.class));
    
    try {
      deepCopy(newRoad);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return newRoad;
  }


}
