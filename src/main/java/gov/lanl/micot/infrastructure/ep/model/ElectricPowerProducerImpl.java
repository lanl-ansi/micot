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
  public double getReactiveGenerationMax() {
    return getAttribute(REACTIVE_MAX_KEY, Double.class);
  }

  @Override
  public void setReactiveGenerationMax(double reactiveMaxRatio) {
    setAttribute(REACTIVE_MAX_KEY, reactiveMaxRatio);
  }

  @Override
  public double getReactiveGenerationMin() {
    return getAttribute(REACTIVE_GENERATION_MIN_KEY, Double.class);
  }

  @Override
  public void setReactiveGenerationMin(double reactiveMinRatio) {
    setAttribute(REACTIVE_GENERATION_MIN_KEY, reactiveMinRatio);
  }

  @Override
  public double getRealGenerationMax() {
    return getAttribute(REAL_GENERATION_MAX_KEY, Double.class);
  }

  @Override
  public void setRealGenerationMax(double realGenerationMax) {
    setAttribute(REAL_GENERATION_MAX_KEY, realGenerationMax);
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
  	double value = Math.sqrt(getRealGenerationMin() * getRealGenerationMin() + getReactiveGenerationMin() * getReactiveGenerationMin()); 
  	if (getRealGenerationMin() < 0 && getRealGenerationMin() < getReactiveGenerationMin()) {
    	value = -value;
    }
    else if (getReactiveGenerationMin() < 0 && getRealGenerationMin() > getReactiveGenerationMin()) {
    	value = -value;
    }  	
    return value; 
  }

  @Override
  public void setMinimumProduction(Number generation) {
    setRealGenerationMin(generation.doubleValue());
    setReactiveGenerationMin(0.0);
  }

  @Override
  public Number getMaximumProduction() {
    double value =  Math.sqrt(getRealGenerationMax() * getRealGenerationMax() + getReactiveGenerationMax() * getReactiveGenerationMax());
    if (getRealGenerationMax() < 0 && getRealGenerationMax() < getReactiveGenerationMax()) {
    	value = -value;
    }
    else if (getReactiveGenerationMax() < 0 && getRealGenerationMax() > getReactiveGenerationMax()) {
    	value = -value;
    }    
    return value;
  }

  @Override
  public void setMaximumProduction(Number production) {
    setRealGenerationMax(production.doubleValue());
    setReactiveGenerationMax(0.0);    
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
    if (key.equals("REACTIVE_MAX")) {
      System.err.println("Warning: REACTIVE_MAX is a deprecated attribute.  Using REACTIVE_GENERATION_MAX instead");
      key = REACTIVE_MAX_KEY;
    }
    if (key.equals("REACTIVE_MIN")) {
      System.err.println("Warning: REACTIVE_MIN is a deprecated attribute.  Using REACTIVE_GENERATION_MIN instead");
      key = REACTIVE_GENERATION_MIN_KEY;
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
    if (key.equals("REACTIVE_MAX")) {
      System.err.println("Warning: REACTIVE_MAX is a deprecated attribute.  Using REACTIVE_GENERATION_MAX instead");
      key = REACTIVE_MAX_KEY;
    }
    if (key.equals("REACTIVE_MIN")) {
      System.err.println("Warning: REACTIVE_MIN is a deprecated attribute.  Using REACTIVE_GENERATION_MIN instead");
      key = REACTIVE_GENERATION_MIN_KEY;
    }
        
    return super.getAttribute(key);
  }
  
}
