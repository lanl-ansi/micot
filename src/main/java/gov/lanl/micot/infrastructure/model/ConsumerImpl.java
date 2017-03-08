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
  
  /**
   * Constructor
   * @param consumer
   */
//  public ConsumerImpl(Consumer consumer) {
  //  super(consumer/*, ((ConsumerImpl)consumer).getOredKeys(), ((ConsumerImpl)consumer).getAdditiveKeys(), ((ConsumerImpl)consumer).getSubtractiveKeys()*/);
  //}
  
  @Override
  public Number getActualConsumption() {
    return getAttribute(ACTUAL_CONSUMPTION_KEY,Double.class);
  }

  @Override
  public void setActualConsumption(Number consumption) {
    setAttribute(ACTUAL_CONSUMPTION_KEY, consumption);
  }

  @Override
  public Number getDesiredConsumption() {
    return getAttribute(DESIRED_CONSUMPTION_KEY,Double.class);
  }

  @Override
  public void setDesiredConsumption(Number consumption) {
    setAttribute(DESIRED_CONSUMPTION_KEY, consumption);
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
  
}
