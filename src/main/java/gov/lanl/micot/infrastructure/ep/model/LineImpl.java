package gov.lanl.micot.infrastructure.ep.model;

import java.util.HashSet;
import java.util.Set;

/**
 * Implementation of a line
 * 
 * @author Russell Bent
 */
public class LineImpl extends ElectricPowerFlowConnectionImpl implements Line {

  protected static final long       serialVersionUID               = 1L;
  
  private Set<LineChangeListener> listeners                        = null;

  /**
   * Constructor
   */
  protected LineImpl(long assetId) {
    super();
    listeners = new HashSet<LineChangeListener>();
    setAttribute(Line.ASSET_ID_KEY, assetId);
  }

  /**
   * Constructor
   * @param line
   */
//  public LineImpl(Line line) {
  //  super(line);
   // listeners = new HashSet<LineChangeListener>();    
  //}

  @Override
  public void addLineChangeListener(LineChangeListener listener) {
    listeners.add(listener);
  }

  /**
   * Remove the line data listener
   * 
   * @param listener
   */
  public void removeLineChangeListener(LineChangeListener listener) {
    listeners.remove(listener);
  }

  /**
   * Notification that line data has changed
   */
  private void fireDataChangeEvent(Object attribute) {
    for (LineChangeListener listener : listeners) {
      listener.lineDataChanged(this, attribute);
    }
  }

  @Override
  public void setAttribute(Object key, Object object) {
    super.setAttribute(key,object);
    fireDataChangeEvent(key);
  }

   @Override
  public LineImpl clone() {
//    LineImpl newLine = new LineImpl((Line)getBaseData());
    LineImpl newLine = new LineImpl(getAttribute(ASSET_ID_KEY,Long.class));

    try {
      deepCopy(newLine);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return newLine;
  }

  
}
