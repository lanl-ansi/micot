package gov.lanl.micot.infrastructure.model;

/**
 * Some common code for components that can be used
 * @author Russell Bent
 */
public abstract class ProducerImpl extends ComponentImpl implements Producer  {

  /**
   * Constructor
   */
  public ProducerImpl() {
    super();
  }
  
  @Override
  public Number getProduction() {
    return getAttribute(PRODUCTION_KEY, Double.class);
  }

  @Override
  public void setProduction(Number consumption) {
    setAttribute(PRODUCTION_KEY,consumption);
  }

  @Override
  public Number getMinimumProduction() {
    return getAttribute(MINIMUM_PRODUCTION_KEY, Double.class);
  }

  @Override
  public void setMinimumProduction(Number production) {
    setAttribute(MINIMUM_PRODUCTION_KEY, production);
  }

  @Override
  public Number getMaximumProduction() {
    return getAttribute(MAXIMUM_PRODUCTION_KEY, Double.class);
  }

  @Override
  public void setMaximumProduction(Number production) {
    setAttribute(MAXIMUM_PRODUCTION_KEY, production);
  }
  
  @Override
  public void setAttribute(Object key, Object object) {
    if (key.equals("DESIRED_PRODUCTION")) {
      System.err.println("Warning: DESIRED_PRODUCTION is a deprecated attribute.  Using PRODUCTION instead");
      key = PRODUCTION_KEY;
    }
    if (key.equals("ACTUAL_PRODUCTION")) {
      System.err.println("Warning: ACTUAL_PRODUCTION is a deprecated attribute.  Using PRODUCTION instead");
      key = PRODUCTION_KEY;
    }
    super.setAttribute(key,object);
  }
  
  @Override
  public Object getAttribute(Object key) {
    if (key.equals("DESIRED_PRODUCTION")) {
      System.err.println("Warning: DESIRED_PRODUCTION is a deprecated attribute.  Using PRODUCTION instead");
      key = PRODUCTION_KEY;
    }
    if (key.equals("ACTUAL_PRODUCTION")) {
      System.err.println("Warning: ACTUAL_PRODUCTION is a deprecated attribute.  Using PRODUCTION instead");
      key = PRODUCTION_KEY;
    }
    
    return super.getAttribute(key);
  }

  
}
