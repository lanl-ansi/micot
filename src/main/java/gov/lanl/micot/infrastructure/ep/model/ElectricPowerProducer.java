package gov.lanl.micot.infrastructure.ep.model;

import gov.lanl.micot.infrastructure.model.Component;
import gov.lanl.micot.infrastructure.model.Producer;

/**
 * Base class for generator types
 * @author Russell Bent
 */
public interface ElectricPowerProducer extends Producer, Component {

  public static final String REACTIVE_GENERATION_KEY         = "REACTIVE_GENERATION";
  public static final String REAL_GENERATION_KEY             = "REAL_GENERATION";  
  
  public static final String REACTIVE_MAX_KEY                = "REACTIVE_GENERATION_MAX";
  public static final String REAL_GENERATION_MAX_KEY         = "REAL_GENERATION_MAX";
  
  public static final String REAL_GENERATION_MIN_KEY         = "REAL_GENERATION_MIN";
  public static final String REACTIVE_GENERATION_MIN_KEY     = "REACTIVE_GENERATION_MIN";
  			
	/**
   * Get the real generation of a generator
   * @return
   */
  public Number getRealGeneration();
  
  /**
   * Set the real generation
   * @param real
   */
  public void setRealGeneration(Number real);
  
  /**
   * Get the reactive generation
   * @return
   */
  public Number getReactiveGeneration();
  
  /**
   * Set the reactive generation
   * @param reactive
   */
  public void setReactiveGeneration(Number reactive);
  
  /**
   * Get the reactive max ratio
   * @return
   */
  public double getReactiveGenerationMax();
  
  /**
   * Set the reactive max ratio
   * @param max
   */
  public void setReactiveGenerationMax(double max);
  
  /**
   * Get the min reactive ratio
   * @return
   */
  public double getReactiveGenerationMin();
  
  /**
   * Set the min reactive ratio
   * @param max
   */
  public void setReactiveGenerationMin(double max);
  
  /**
   * Get the real generation max value
   * @return
   */
  public double getRealGenerationMax();
  
  /**
   * Set the real generation max
   * @param real
   */
  public void setRealGenerationMax(double real);
  
  /**
   * Get the real generation min
   * @return
   */
  public double getRealGenerationMin();
  
  /**
   * Set the real generation min
   * @param real
   */
  public void setRealGenerationMin(double real);
      
  /**
   * Get the economic cost of a producer
   * @return
   */
  public double getEconomicCost();
  
  /**
   * Set the economic cost
   * @param ec
   */
  public void setEconomicCost(double ec);
  
}
