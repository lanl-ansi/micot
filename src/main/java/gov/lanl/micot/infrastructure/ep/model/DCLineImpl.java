package gov.lanl.micot.infrastructure.ep.model;

import java.util.HashSet;
import java.util.Set;

/**
 * Implementation of a dc line
 * 
 * @author Russell Bent
 */
public abstract class DCLineImpl extends ElectricPowerFlowConnectionImpl implements DCLine {

  protected static final long       serialVersionUID               = 1L;
  
  private Set<DCLineChangeListener> listeners                        = null;

  /**
   * Constructor
   */
  protected DCLineImpl(long assetId) {
    super();
    listeners = new HashSet<DCLineChangeListener>();
    setAttribute(Line.ASSET_ID_KEY, assetId);
  }

  @Override
  public void addDCLineChangeListener(DCLineChangeListener listener) {
    listeners.add(listener);
  }

  @Override
  public void removeDCLineChangeListener(DCLineChangeListener listener) {
    listeners.remove(listener);
  }

  /**
   * Notification that line data has changed
   */
  private void fireDataChangeEvent(Object attribute) {
    for (DCLineChangeListener listener : listeners) {
      listener.dcLineDataChanged(this, attribute);
    }
  }

  @Override
  public void setAttribute(Object key, Object object) {
    super.setAttribute(key,object);
    fireDataChangeEvent(key);
  }

  @Override
  public abstract DCLineImpl clone();
}
