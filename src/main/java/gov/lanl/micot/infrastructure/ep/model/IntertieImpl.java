package gov.lanl.micot.infrastructure.ep.model;

import java.util.HashSet;
import java.util.Set;

/**
 * The implementation of an intertie
 * @author Russell Bent
 */
public class IntertieImpl extends ElectricPowerConnectionImpl implements Intertie {
  
  protected static final long serialVersionUID = 1L;

  private Set<IntertieChangeListener> listeners = null;

  /**
   * Constructor
   */
  protected IntertieImpl(long assetId) {
    super();
    listeners = new HashSet<IntertieChangeListener>();
    setAttribute(Intertie.ASSET_ID_KEY, assetId);
  }
   
  /**
   * Constructor
   * @param line
   */
//  public IntertieImpl(Intertie line) {
  //  super(line);
   // listeners = new HashSet<IntertieChangeListener>();    
 // }

  @Override
  public void addIntertieChangeListener(IntertieChangeListener listener) {
    listeners.add(listener);
  }
  
  @Override
  public void removeIntertieChangeListener(IntertieChangeListener listener) {
    listeners.remove(listener);
  }

  /**
   * Fire an interties data change event    
   */
  private void fireIntertieDataChangeEvent(Object attribute) {
    for (IntertieChangeListener listener : listeners) {
      listener.intertieDataChanged(this,attribute);
    }
  }  
  @Override
  public void setAttribute(Object key, Object object) {
    super.setAttribute(key,object);
    fireIntertieDataChangeEvent(key);
  }
  
  @Override
  public IntertieImpl clone() {
//    IntertieImpl newIntertie = new IntertieImpl((Intertie)getBaseData());
    IntertieImpl newIntertie = new IntertieImpl(getAttribute(ASSET_ID_KEY,Long.class));

    try {
      deepCopy(newIntertie);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return newIntertie;
  }

}
