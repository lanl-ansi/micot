package gov.lanl.micot.infrastructure.naturalgas.model;

import java.util.HashSet;
import java.util.Set;

import gov.lanl.micot.infrastructure.model.ProducerImpl;

/**
 * Implementation of generators
 * 
 * @author Russell Bent
 */
public class ReservoirImpl extends ProducerImpl implements Reservoir {

  protected static final long            serialVersionUID = 1L;
  
  private Set<ReservoirChangeListener> listeners        = null;

  /**
   * Constructor
   */
  protected ReservoirImpl(long assetId) {
    super();
    listeners = new HashSet<ReservoirChangeListener>();
    setAttribute(Reservoir.ASSET_ID_KEY, assetId);
  }

  /**
   * Constructor
   * @param battery
   */
//  public ReservoirImpl(Reservoir reservoir) {
  //  super(reservoir);
   // listeners = new HashSet<ReservoirChangeListener>();    
  //}
  
  @Override
  public void setActualProduction(Set<Reservoir> producers) {
    double producerMax      = 0;
    double actualProduction = 0;
    
    for (Reservoir state : producers) {
      if (state.getDesiredStatus() == true) {
        producerMax += state.getMaximumProduction().doubleValue();
        actualProduction += state.getActualProduction().doubleValue();
      }
    }

    double percentage = actualProduction / producerMax; 
    setActualProduction(percentage * getMaximumProduction().doubleValue());
  }

  @Override
  public void addReservoirChangeListener(ReservoirChangeListener listener) {
    listeners.add(listener);
  }

  @Override
  public void removeReservoirChangeListener(ReservoirChangeListener listener) {
    listeners.remove(listener);
  }

  /**
   * Fire the data change event
   */
  private void fireDataChangeEvent(Object attribute) {
    for (ReservoirChangeListener listener : listeners) {
      listener.reservoirDataChanged(this,attribute);
    }
  }


  @Override
  public void setAttribute(Object key, Object object) {
    super.setAttribute(key, object);
    fireDataChangeEvent(key);
  }

  @Override
  public void setCapacity(double capacity) {
    setAttribute(CAPACITY_KEY, capacity);
  }

  @Override
  public double getCapacity() {
    return getAttribute(CAPACITY_KEY, Double.class);
  }

  @Override
  public ReservoirImpl clone() {
    //ReservoirImpl newReservoir = new ReservoirImpl((Reservoir)getBaseData());
    ReservoirImpl newReservoir = new ReservoirImpl(getAttribute(ASSET_ID_KEY,Long.class));
    
    try {
      deepCopy(newReservoir);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return newReservoir;
  }

}
