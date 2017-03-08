package gov.lanl.micot.infrastructure.transportation.road.model;

/**
 * This is an interface for listening to changes in a model's
 * structure
 * @author Russell Bent
 */
public interface RoadModelListener {

	/**
	 * Notification that a link has been added to the model
	 * @param link
	 */
	public void roadAdded(Road link);
	
	/**
	 * Notification that a link has been removed from the model
	 * @param link
	 */
	public void roadRemoved(Road link);
		
	/**
   * Notification that road data has changed
   * @param line
   * @param newLineData
   */
  public void roadDataChange(Road road);
	
	/**
	 * Notification that an intersection has been added
	 * @param bus
	 */
	public void intersectionAdded(Intersection intersection);

	/**
	 * Notification that an intersection has been removed
	 * @param bus
	 */
	public void intersectionRemoved(Intersection intersection);

  /**
   * Notification that intersection data has changed
   * @param bus
   * @param newBusData
   */
  public void intersectionDataChange(Intersection intersection);
	
  /**
	 * Notification that an intersection has been added
	 * @param bus
	 */
	public void destinationAdded(Destination destination);

	/**
	 * Notification that an intersection has been removed
	 * @param bus
	 */
	public void destinationRemoved(Destination destination);

  /**
   * Notification that intersection data has changed
   * @param bus
   * @param newBusData
   */
  public void destinationDataChange(Destination destination);

  /**
	 * Notification that an intersection has been added
	 * @param bus
	 */
	public void originAdded(Origin origin);

	/**
	 * Notification that an intersection has been removed
	 * @param bus
	 */
	public void originRemoved(Origin origin);

  /**
   * Notification that intersection data has changed
   * @param bus
   * @param newBusData
   */
  public void originDataChange(Origin origin);

  
 
}
