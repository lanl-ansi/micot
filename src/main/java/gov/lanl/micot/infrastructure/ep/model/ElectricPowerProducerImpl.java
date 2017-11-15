package gov.lanl.micot.infrastructure.ep.model;

import gov.lanl.micot.infrastructure.model.ProducerImpl;
import gov.lanl.micot.util.math.MathUtils;

/**
 * Implementation of electric power producers
 * 
 * @author Russell Bent
 */
public abstract class ElectricPowerProducerImpl extends ProducerImpl implements ElectricPowerProducer {

  /**
   * Constructor
   */
  protected ElectricPowerProducerImpl() {
    super();   
  }
     
  @Override
  public Number getRealGeneration() {
    return getAttribute(REAL_GENERATION_KEY, Number.class);
  }

  @Override
  public void setRealGeneration(Number realGeneration) {
    setAttribute(REAL_GENERATION_KEY, realGeneration);
  }

//  @Override
  //public Number getActualRealGeneration() {
    //return getAttribute(ACTUAL_REAL_GENERATION_KEY, Number.class);
  //}

  //@Override
  //public void setActualRealGeneration(Number realGeneration) {
    //setAttribute(ACTUAL_REAL_GENERATION_KEY, realGeneration);
  //}

  @Override
  public Number getReactiveGeneration() {
    return getAttribute(REACTIVE_GENERATION_KEY, Number.class);
  }

  @Override
  public void setReactiveGeneration(Number reactiveGeneration) {
    setAttribute(REACTIVE_GENERATION_KEY, reactiveGeneration);
  }

//  @Override
  //public Number getActualReactiveGeneration() {
    //return getAttribute(ACTUAL_REACTIVE_GENERATION_KEY, Number.class);
 // }

  //@Override
 // public void setActualReactiveGeneration(Number reactiveGeneration) {
   // setAttribute(ACTUAL_REACTIVE_GENERATION_KEY, reactiveGeneration);
  //}

  @Override
  public double getDesiredReactiveMax() {
    return getAttribute(DESIRED_REACTIVE_MAX_KEY, Double.class);
  }

  @Override
  public void setDesiredReactiveMax(double reactiveMaxRatio) {
    setAttribute(DESIRED_REACTIVE_MAX_KEY, reactiveMaxRatio);
  }

  @Override
  public double getReactiveMin() {
    return getAttribute(REACTIVE_MIN_KEY, Double.class);
  }

  @Override
  public void setReactiveMin(double reactiveMinRatio) {
    setAttribute(REACTIVE_MIN_KEY, reactiveMinRatio);
  }

  @Override
  public double getDesiredRealGenerationMax() {
    return getAttribute(DESIRED_REAL_GENERATION_MAX_KEY, Double.class);
  }

  @Override
  public void setDesiredRealGenerationMax(double realGenerationMax) {
    setAttribute(DESIRED_REAL_GENERATION_MAX_KEY, realGenerationMax);
  }

  @Override
  public double getRealGenerationMin() {
    return getAttribute(REAL_GENERATION_MIN_KEY, Double.class);
  }

  @Override
  public void setRealGenerationMin(double realGenerationMin) {
    setAttribute(REAL_GENERATION_MIN_KEY, realGenerationMin);
  }

  @Override
  public Number getProduction() {
    return MathUtils.SIGNED_NORM(getRealGeneration(), getReactiveGeneration());    
  }

  @Override
  public void setProduction(Number consumption) {
    setRealGeneration(consumption.doubleValue());
    setReactiveGeneration(0.0);
  }

  @Override
  public Number getMinimumProduction() {
  	double value = Math.sqrt(getRealGenerationMin() * getRealGenerationMin() + getReactiveMin() * getReactiveMin()); 
  	if (getRealGenerationMin() < 0 && getRealGenerationMin() < getReactiveMin()) {
    	value = -value;
    }
    else if (getReactiveMin() < 0 && getRealGenerationMin() > getReactiveMin()) {
    	value = -value;
    }  	
    return value; 
  }

  @Override
  public void setMinimumProduction(Number generation) {
    setRealGenerationMin(generation.doubleValue());
    setReactiveMin(0.0);
  }

  @Override
  public Number getMaximumProduction() {
    double value =  Math.sqrt(getDesiredRealGenerationMax() * getDesiredRealGenerationMax() + getDesiredReactiveMax() * getDesiredReactiveMax());
    if (getDesiredRealGenerationMax() < 0 && getDesiredRealGenerationMax() < getDesiredReactiveMax()) {
    	value = -value;
    }
    else if (getDesiredReactiveMax() < 0 && getDesiredRealGenerationMax() > getDesiredReactiveMax()) {
    	value = -value;
    }    
    return value;
  }

  @Override
  public void setMaximumProduction(Number production) {
    setDesiredRealGenerationMax(production.doubleValue());
    setDesiredReactiveMax(0.0);    
  }

  /**
   * Get economic cost
   * @return
   */
  public double getEconomicCost() {
    return getAttribute(ECONOMIC_COST_KEY,Number.class).doubleValue();
  }

  /**
   * Set the economic cost
   * @param cost
   */
  public void setEconomicCost(double cost) {
    setAttribute(ECONOMIC_COST_KEY,cost);    
  }
  
  
  @Override
  public void setAttribute(Object key, Object object) {
    if (key.equals("ACTUAL_REACTIVE_GENERATION")) {
      System.err.println("Warning: ACTUAL_REACTIVE_GENERATION is a deprecated attribute.  Using REACTIVE_GENERATION instead");
      key = REACTIVE_GENERATION_KEY;
    }
    if (key.equals("ACTUAL_REAL_GENERATION")) {
      System.err.println("Warning: ACTUAL_REAL_GENERATION is a deprecated attribute.  Using REAL_GENERATION instead");
      key = REAL_GENERATION_KEY;
    }
    
    if (key.equals("DESIRED_REACTIVE_GENERATION")) {
      System.err.println("Warning: DESIRED_REACTIVE_GENERATION is a deprecated attribute.  Using REACTIVE_GENERATION instead");
      key = REACTIVE_GENERATION_KEY;
    }
    if (key.equals("DESIRED_REAL_GENERATION")) {
      System.err.println("Warning: DESIRED_REAL_GENERATION is a deprecated attribute.  Using REAL_GENERATION instead");
      key = REAL_GENERATION_KEY;
    }
    
    super.setAttribute(key,object);
  }
  
  @Override
  public Object getAttribute(Object key) {
    if (key.equals("ACTUAL_REACTIVE_GENERATION")) {
      System.err.println("Warning: ACTUAL_REACTIVE_GENERATION is a deprecated attribute.  Using REACTIVE_GENERATION instead");
      key = REACTIVE_GENERATION_KEY;
    }
    if (key.equals("ACTUAL_REAL_GENERATION")) {
      System.err.println("Warning: ACTUAL_REAL_GENERATION is a deprecated attribute.  Using REAL_GENERATION instead");
      key = REAL_GENERATION_KEY;
    }
    
    if (key.equals("DESIRED_REACTIVE_GENERATION")) {
      System.err.println("Warning: DESIRED_REACTIVE_GENERATION is a deprecated attribute.  Using REACTIVE_GENERATION instead");
      key = REACTIVE_GENERATION_KEY;
    }
    if (key.equals("DESIRED_REAL_GENERATION")) {
      System.err.println("Warning: DESIRED_REAL_GENERATION is a deprecated attribute.  Using REAL_GENERATION instead");
      key = REAL_GENERATION_KEY;
    }
    
    return super.getAttribute(key);
  }
  
}
