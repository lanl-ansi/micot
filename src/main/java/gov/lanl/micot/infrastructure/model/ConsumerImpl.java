package gov.lanl.micot.infrastructure.model;


/**
 * Some common code for components that can be used
 * @author Russell Bent
 */
public abstract class ConsumerImpl extends ComponentImpl implements Consumer  {

  /**
   * Constructor
   */
  public ConsumerImpl() {
    super();
  }
  
  @Override
  public Number getConsumption() {
    return getAttribute(CONSUMPTION_KEY,Double.class);
  }

  @Override
  public void setConsumption(Number consumption) {
    setAttribute(CONSUMPTION_KEY, consumption);
  }

  @Override
  public Number getMaximumConsumption() {
    return getAttribute(MAXIMUM_CONSUMPTION_KEY,Double.class);
  }

  @Override
  public void setMaximumConsumption(Number consumption) {
    setAttribute(MAXIMUM_CONSUMPTION_KEY, consumption);
  }

  @Override
  public Number getMinimumConsumption() {
    return getAttribute(MINIMUM_CONSUMPTION_KEY,Double.class);
  }

  @Override
  public void setMinimumConsumption(Number consumption) {
    setAttribute(MINIMUM_CONSUMPTION_KEY, consumption);
  }

  public abstract ConsumerImpl clone();
  
  @Override
  public void setAttribute(Object key, Object object) {
    if (key.equals("DESIRED_CONSUMPTION")) {
      System.err.println("Warning: DESIRED_CONSUMPTION is a deprecated attribute.  Using CONSUMPTION instead");
      key = CONSUMPTION_KEY;
    }
    if (key.equals("ACTUAL_CONSUMPTION")) {
      System.err.println("Warning: ACTUAL_CONSUMPTION is a deprecated attribute.  Using CONSUMPTION instead");
      key = CONSUMPTION_KEY;
    }
    super.setAttribute(key,object);
  }
  
  @Override
  public Object getAttribute(Object key) {
    if (key.equals("DESIRED_CONSUMPTION")) {
      System.err.println("Warning: DESIRED_CONSUMPTION is a deprecated attribute.  Using CONSUMPTION instead");
      key = CONSUMPTION_KEY;
    }
    if (key.equals("ACTUAL_CONSUMPTION")) {
      System.err.println("Warning: ACTUAL_CONSUMPTION is a deprecated attribute.  Using CONSUMPTION instead");
      key = CONSUMPTION_KEY;
    }    
    return super.getAttribute(key);
  }
  
}
