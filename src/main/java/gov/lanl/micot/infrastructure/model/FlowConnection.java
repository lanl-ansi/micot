package gov.lanl.micot.infrastructure.model;

/**
 * Abstract class for power lines
 * @author Russell Bent
 */
public interface FlowConnection extends Connection {
  
  public static final String CAPACITY_KEY = "CAPACITY";
  public static final String FLOW_KEY     = "FLOW";
  public static final String LENGTH_KEY   = "LENGTH";
  
  /**
   * Set the capacity of a flow edge
   * @param capacity
   */
  public void setCapacity(Number capacity);

  /**
   * Get the capacity of an edge
   * @return
   */
  public Number getCapacity();

  /**
   * gets the total flow
   * @return
   */
  public Number getFlow();

  /**
   * Set the flow
   * @param flow
   */
  public void setFlow(Number flow);

  
}
