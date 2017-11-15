package gov.lanl.micot.infrastructure.transportation.road.model;

import java.util.HashSet;
import java.util.Set;

import gov.lanl.micot.infrastructure.model.ProducerImpl;

/**
 * Implementation of generators
 * 
 * @author Russell Bent
 */
public class OriginImpl extends ProducerImpl implements Origin {

  protected static final long            serialVersionUID = 1L;
  private Set<OriginChangeListener> listeners        = null;

  /**
   * Constructor
   */
  protected OriginImpl(long assetId) {
    super();
    listeners = new HashSet<OriginChangeListener>();
    setAttribute(Origin.ASSET_ID_KEY, assetId);
  }
 
  @Override
  public void setProduction(Set<Origin> producers) {
    double producerMax      = 0;
    double actualProduction = 0;
    
    for (Origin state : producers) {
      if (state.getStatus() == true) {
        producerMax += state.getMaximumProduction().doubleValue();
        actualProduction += state.getProduction().doubleValue();
      }
    }

    double percentage = actualProduction / producerMax; 
    setProduction(percentage * getMaximumProduction().doubleValue());
  }

  @Override
  public void addOriginDataListener(OriginChangeListener listener) {
    listeners.add(listener);
  }

  @Override
  public void removeOriginDataListener(OriginChangeListener listener) {
    listeners.remove(listener);
  }

  /**
   * Fire the data change event
   */
  private void fireDataChangeEvent(Object attribute) {
    for (OriginChangeListener listener : listeners) {
      listener.originDataChanged(this, attribute);
    }
  }

  @Override
  public void setAttribute(Object key, Object object) {
    super.setAttribute(key, object);
    fireDataChangeEvent(key);
  }
  
  @Override
  public OriginImpl clone() {
    OriginImpl newOrigin = new OriginImpl(getAttribute(ASSET_ID_KEY,Long.class));
    
    try {
      deepCopy(newOrigin);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return newOrigin;
  }


}
