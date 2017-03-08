package gov.lanl.micot.infrastructure.transportation.road.model;

import java.util.HashSet;
import java.util.Set;

import gov.lanl.micot.infrastructure.model.ConsumerImpl;

/**
 * An implementation of the destubatuib interface
 * @author Russell Bent
 */
public class DestinationImpl extends ConsumerImpl implements Destination {

  protected static final long serialVersionUID = 1L;
  private Set<DestinationChangeListener> listeners = null;
    
  /**
   * Constructor
   */
  protected DestinationImpl(long assetId) {
    super();
    listeners = new HashSet<DestinationChangeListener>();
    setAttribute(Destination.ASSET_ID_KEY, assetId);
  }
  
  /**
   * Constructor
   * @param destination
   */
//  public DestinationImpl(Destination destination) {
  //  super(destination);
   // listeners = new HashSet<DestinationChangeListener>();    
 // }
          
  @Override
  public void addDestinationChangeListener(DestinationChangeListener listener) {
    listeners.add(listener);
  }
  
  @Override
  public void removeDestinationChangeListener(DestinationChangeListener listener) {
    listeners.remove(listener);
  }

  /**
   * Fire the data change event
   */
  private void fireDataChangeEvent(Object attribute) {
    for (DestinationChangeListener listener : listeners) {
      listener.destinationDataChanged(this, attribute);
    }
  }
  
  @Override
  public void setAttribute(Object key, Object object) {
    super.setAttribute(key,object);
    fireDataChangeEvent(key);
  } 
  
  @Override
  public DestinationImpl clone() {
    //DestinationImpl newDestination = new DestinationImpl((Destination)getBaseData());
    DestinationImpl newDestination = new DestinationImpl(getAttribute(ASSET_ID_KEY,Long.class));
    
    try {
      deepCopy(newDestination);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return newDestination;
  }

}
