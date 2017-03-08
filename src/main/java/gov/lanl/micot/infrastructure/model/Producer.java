package gov.lanl.micot.infrastructure.model;

/**
 * Interface for producers
 * @author Russell Bent
 */
public interface Producer extends Component {

  public static final String DESIRED_PRODUCTION_KEY = "DESIRED_PRODUCTION_KEY";
  public static final String ACTUAL_PRODUCTION_KEY  = "ACTUAL_PRODUCTION_KEY";
  public static final String MAXIMUM_PRODUCTION_KEY = "MAXIMUM_PRODUCTION_KEY";
  public static final String MINIMUM_PRODUCTION_KEY = "MINIMUM_PRODUCTION_KEY";
  public static final String CARBON_OUTPUT_KEY      = "CARBON_OUTPUT_KEY";
  public static final String ECONOMIC_COST_KEY      = "ECONOMIC_COST_KEY";
  
  /**
   * Get the desired production
   * @return
   */
  public Number getDesiredProduction();
  
  /**
   * Set the desired production
   * @param production
   */
  public void setDesiredProduction(Number production);

  /**
   * Get the actual production
   * @return
   */
  public Number getActualProduction();
  
  /**
   * Set the actual production
   * @param production
   */
  public void setActualProduction(Number production);


  /**
   * Get the minimum production
   * @return
   */
  public Number getMinimumProduction();
  
  /**
   * Set the minimum production
   * @param real
   */
  public void setMinimumProduction(Number generation);
    
  /**
   * Get the maximum production
   * @return
   */
  public Number getMaximumProduction();
  
  /**
   * Set the maximum production
   * @param real
   */
  public void setMaximumProduction(Number production);

}
