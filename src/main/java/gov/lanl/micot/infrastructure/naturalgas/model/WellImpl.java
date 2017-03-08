package gov.lanl.micot.infrastructure.naturalgas.model;

import java.util.HashSet;
import java.util.Set;

import gov.lanl.micot.infrastructure.model.ProducerImpl;

/**
 * Implementation of generators
 * 
 * @author Russell Bent
 */
public class WellImpl extends ProducerImpl implements Well {

  private Set<WellChangeListener> listeners        = null;

  /**
   * Constructor
   */
  protected WellImpl(long assetId) {
    super();
    listeners = new HashSet<WellChangeListener>();
    setAttribute(Well.ASSET_ID_KEY, assetId);
  }

  /**
   * Constructor
   * @param battery
   */
//  public WellImpl(Well well) {
  //  super(well);
    //listeners = new HashSet<WellChangeListener>();    
 // }
  
  @Override
  public void setActualProduction(Set<Well> producers) {
    double producerMax      = 0;
    double actualProduction = 0;
    
    for (Well state : producers) {
      if (state.getDesiredStatus() == true) {
        producerMax += state.getMaximumProduction().doubleValue();
        actualProduction += state.getActualProduction().doubleValue();
      }
    }

    double percentage = actualProduction / producerMax; 
    setActualProduction(percentage * getMaximumProduction().doubleValue());
  }

  @Override
  public void addWellDataListener(WellChangeListener listener) {
    listeners.add(listener);
  }

  @Override
  public void removeWellDataListener(WellChangeListener listener) {
    listeners.remove(listener);
  }

  /**
   * Fire the data change event
   */
  private void fireDataChangeEvent(Object attribute) {
    for (WellChangeListener listener : listeners) {
      listener.wellDataChanged(this, attribute);
    }
  }

  @Override
  public void setAttribute(Object key, Object object) {
    super.setAttribute(key, object);
    fireDataChangeEvent(key);
  }

  @Override
  public WellImpl clone() {
    //WellImpl newWell = new WellImpl((Well)getBaseData());
    WellImpl newWell = new WellImpl(getAttribute(ASSET_ID_KEY,Long.class));
    
    try {
      deepCopy(newWell);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return newWell;
  }

}
