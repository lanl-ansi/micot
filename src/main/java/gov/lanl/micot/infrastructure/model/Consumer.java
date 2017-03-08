package gov.lanl.micot.infrastructure.model;

/**
 * An interface for consumption components
 * @author Russell Bent
 */
public interface Consumer extends Component {

  public static final String DESIRED_CONSUMPTION_KEY = "DESIRED_CONSUMPTION";
  public static final String ACTUAL_CONSUMPTION_KEY  = "ACTUAL_CONSUMPTION";
  public static final String MAXIMUM_CONSUMPTION_KEY  = "MAXIMUM_CONSUMPTION";
  public static final String MINIMUM_CONSUMPTION_KEY  = "MINIMUM_CONSUMPTION";

  /**
   * Get desired consumption rate
   * @return
   */
  public Number getDesiredConsumption();

  /**
   * Set the desired comsumption rate
   * @param consumption
   */
  public void setDesiredConsumption(Number consumption);

  /**
   * Get the actual consumption rate
   * @return
   */
  public Number getActualConsumption();
  
  /**
   * Sets the actual consumption rate
   * @param consumption
   */
  public void setActualConsumption(Number consumption);

  /**
   * Get the actual consumption rate
   * @return
   */
  public Number getMaximumConsumption();
  
  /**
   * Sets the actual consumption rate
   * @param consumption
   */
  public void setMaximumConsumption(Number consumption);

  /**
   * Get the actual consumption rate
   * @return
   */
  public Number getMinimumConsumption();
  
  /**
   * Sets the actual consumption rate
   * @param consumption
   */
  public void setMinimumConsumption(Number consumption);

  
}
