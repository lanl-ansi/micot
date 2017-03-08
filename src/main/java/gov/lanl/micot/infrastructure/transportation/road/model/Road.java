package gov.lanl.micot.infrastructure.transportation.road.model;

import gov.lanl.micot.infrastructure.model.Connection;
import gov.lanl.micot.infrastructure.model.FlowConnection;

/**
 * Interface for power lines
 * @author Russell Bent
 */
public interface Road extends Connection, FlowConnection {	
    
  public static final String SPEED_LIMIT_KEY     = "SPEED_LIMIT";
  public static final String NUMBER_OF_LANES_KEY = "NUMBER_OF_LANES";
  
  /**
   * Adds the line data listener
   * @param listener
   */
  public void addRoadChangeListener(RoadChangeListener listener);
  
  /**
   * Remove the line data listener
   * @param listener
   */
  public void removeRoadChangeListener(RoadChangeListener listener);

  /**
   * set the speed limit
   * @param pressure
   */
  public void setSpeedLimit(double speedLimit);
  
  /**
   * get the speed limit
   * @return
   */
  public double getSpeedLimit();

  /**
   * set the number of lanes
   * @param pressure
   */
  public void setNumberOfLanes(int lanes);
  
  /**
   * get the number of lanes
   * @return
   */
  public int getNumberOfLanes();

  /**
   * Set the length
   * @param pressure
   */
  public void setLength(double length);
  
  /**
   * Get the length
   * @return
   */
  public double getLength();

  /**
   * Clone a road
   * @return
   */
  public Road clone();
  
}
