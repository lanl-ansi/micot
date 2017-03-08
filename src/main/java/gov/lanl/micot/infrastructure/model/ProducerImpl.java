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
  
  /**
   * Constructor
   * @param battery
   */
//  public ProducerImpl(Producer producer) {
  //  super(producer/*, ((ProducerImpl)producer).getOredKeys(), ((ProducerImpl)producer).getAdditiveKeys(), ((ProducerImpl)producer).getSubtractiveKeys()*/);
 // }

  @Override
  public Number getDesiredProduction() {
    return getAttribute(DESIRED_PRODUCTION_KEY, Double.class);
  }

  @Override
  public void setDesiredProduction(Number consumption) {
    setAttribute(DESIRED_PRODUCTION_KEY,consumption);
  }

  @Override
  public Number getActualProduction() {
    return getAttribute(ACTUAL_PRODUCTION_KEY, Double.class);
  }

  @Override
  public void setActualProduction(Number consumption) {
    setAttribute(ACTUAL_PRODUCTION_KEY,consumption);
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

  
  
}
