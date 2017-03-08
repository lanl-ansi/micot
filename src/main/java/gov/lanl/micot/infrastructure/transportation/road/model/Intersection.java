package gov.lanl.micot.infrastructure.transportation.road.model;

import gov.lanl.micot.infrastructure.model.Component;

/**
 * Interface for natural gas junctions
 * @author Russell Bent
 */
public interface Intersection extends Component {
	
  public static final String SHORTEST_DISTANCE_KEY  = "SHORTEST_DISTANCE";
  public static final String SHORTEST_TIME_KEY  = "SHORTEST_TIME";
  
	/**
	 * Make sure equals is implemented
	 */
	public boolean equals(Object obj);
	
	/**
	 * Make sure hashCode is implemented
	 */
	public int hashCode();
		
  /**
   * Add a bus data listener
   * @param listener
   */
  public void addIntersectionChangeListener(IntersectionChangeListener listener);
  
  /**
   * Remove a bus data listener
   * @param listener
   */
  public void removeIntersectionChangeListener(IntersectionChangeListener listener);

  /**
   * Set the shortest distance to another intersection
   * @param intersection
   * @param distance
   */
	public void setShortestDistance(Intersection intersection, double distance);

	/**
	 * Get the shortest distance to an intersection
	 * @param intersection
	 * @return
	 */
	public Double getShortestDistance(Intersection intersection);

  /**
   * Set the shortest distance to another intersection
   * @param intersection
   * @param distance
   */
	public void setShortestTime(Intersection intersection, double time);

	/**
	 * Get the shortest distance to an intersection
	 * @param intersection
	 * @return
	 */
	public Double getShortestTime(Intersection intersection);

	/**
	 * Intersection is a clone
	 * @return
	 */
	public Intersection clone();
	
}
