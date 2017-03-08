package gov.lanl.micot.infrastructure.coupled.model;

import java.util.HashSet;
import java.util.Set;

import gov.lanl.micot.infrastructure.model.FlowConnectionImpl;

/**
 * Interface for lines
 * @author Russell Bent
 *
 */
public class CouplingImpl extends FlowConnectionImpl implements Coupling {

  private Set<CouplingChangeListener> listeners                        = null;
  
  /**
   * Constructor
   */
  protected CouplingImpl(long assetId) {
    super();
    listeners = new HashSet<CouplingChangeListener>();
    setAttribute(CouplingImpl.ASSET_ID_KEY, assetId);
  }

  /**
   * Constructor
   * @param line
   */
//  public CouplingImpl(Coupling coupling) {
  //  super(coupling);
   // listeners = new HashSet<CouplingChangeListener>();    
  //}
  
  /**
   * Add a coupling listener
   * @param listener
   */
  public void addCouplingChangeListener(CouplingChangeListener listener) {
    listeners.add(listener);
  }

  /**
   * Remove the line data listener
   * 
   * @param listener
   */
  public void removeCouplingChangeListener(CouplingChangeListener listener) {
    listeners.remove(listener);
  }

  /**
   * Notification that line data has changed
   */
  private void fireDataChangeEvent(Object attribute) {
    for (CouplingChangeListener listener : listeners) {
      listener.couplingDataChanged(this, attribute);
    }
  }

  @Override
  public void setAttribute(Object key, Object object) {
    super.setAttribute(key,object);
    fireDataChangeEvent(key);
  }

  @Override
  public CouplingImpl clone() {
//    CouplingImpl newCoupling = new CouplingImpl((CouplingImpl)getBaseData());
    CouplingImpl newCoupling = new CouplingImpl(getAttribute(ASSET_ID_KEY,Long.class));

    try {
      deepCopy(newCoupling);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return newCoupling;
  }

  
 
}
