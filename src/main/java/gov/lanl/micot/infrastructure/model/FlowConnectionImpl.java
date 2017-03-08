package gov.lanl.micot.infrastructure.model;


/**
 * An implementation of flow edge
 * @author Russell Bent
 */
public abstract class FlowConnectionImpl extends ConnectionImpl implements FlowConnection {
  
  /**
   * Constructor
   */
  public FlowConnectionImpl() {
    super();
  }

  /**
   * Constructor
   * @param component
   * @param identifierKeys
   * @param oredKeys
   * @param additiveKeys
   * @param subtractiveKeys
   */
//  public FlowConnectionImpl(Connection edge) {
  //  super(edge);
  //}
     
  @Override
  public void setCapacity(Number capacity) {
    setAttribute(CAPACITY_KEY,capacity);
  }

  @Override
  public Number getCapacity() {
    return getAttribute(CAPACITY_KEY,Double.class);
  }

  @Override
  public Number getFlow() {
    return getAttribute(FLOW_KEY,Double.class);
  }

  @Override
  public void setFlow(Number flow) {
    setAttribute(FLOW_KEY,flow);
  }

}
