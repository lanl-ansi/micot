package gov.lanl.micot.infrastructure.naturalgas.model;

import java.util.HashSet;
import java.util.Set;

import gov.lanl.micot.infrastructure.model.ConsumerImpl;

/**
 * An implementation of the city gate interface
 * @author Russell Bent
 */
public class CityGateImpl extends ConsumerImpl implements CityGate {

  protected static final long serialVersionUID = 1L;
  
  private Set<CityGateChangeListener> listeners = null;
  
  /**
   * Constructor
   */
  protected CityGateImpl(long assetId) {
    super();
    listeners = new HashSet<CityGateChangeListener>();
    setAttribute(CityGate.ASSET_ID_KEY, assetId);
  }
     
  /**
   * Constructor
   * @param battery
   */
//  public CityGateImpl(CityGate gate) {
  //  super(gate);
   // listeners = new HashSet<CityGateChangeListener>();    
  //}
  
  @Override
  public void addCityGateChangeListener(CityGateChangeListener listener) {
    listeners.add(listener);
  }
  
  @Override
  public void removeCityGateChangeListener(CityGateChangeListener listener) {
    listeners.remove(listener);
  }

  /**
   * Fire the data change event
   */
  private void fireDataChangeEvent(Object attribute) {
    for (CityGateChangeListener listener : listeners) {
      listener.cityGateDataChanged(this, attribute);
    }
  }

  
  @Override
  public void setAttribute(Object key, Object object) {
    super.setAttribute(key,object);
    fireDataChangeEvent(key);
  }  
  
  @Override
  public CityGateImpl clone() {
//    CityGateImpl newGate = new CityGateImpl((CityGate)getBaseData());
    CityGateImpl newGate = new CityGateImpl(getAttribute(ASSET_ID_KEY,Long.class));

    try {
      deepCopy(newGate);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return newGate;
  }
}
