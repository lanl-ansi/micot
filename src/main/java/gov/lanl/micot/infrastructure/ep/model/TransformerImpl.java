package gov.lanl.micot.infrastructure.ep.model;

import java.util.HashSet;
import java.util.Set;

/**
 * Implemenation of a transformer
 * @author Russell Bent
 */
public class TransformerImpl extends ElectricPowerFlowConnectionImpl implements Transformer {

  protected static final long serialVersionUID = 1L;
  
  private Set<TransformerChangeListener> listeners = null;

  /**
   * Constructor
   */
  protected TransformerImpl(long assetId) {
    super();
    listeners = new HashSet<TransformerChangeListener>();
    setAttribute(Transformer.ASSET_ID_KEY, assetId);
  }
  
  /**
   * Constructor
   * @param line
   */
//  public TransformerImpl(Transformer transformer) {
  //  super(transformer);
   // listeners = new HashSet<TransformerChangeListener>();    
  //}
  
  @Override
  public void addTransformerChangeListener(TransformerChangeListener listener) {
    listeners.add(listener);
  }
  
  /**
   * Remove the line data listener
   * @param listener
   */
  public void removeTransformerChangeListener(TransformerChangeListener listener) {
    listeners.remove(listener);
  }
  
  @Override
  public void setAttribute(Object key, Object object) {
    super.setAttribute(key,object);
    fireDataChangeEvent(key);
  }
  
  /**
   * Indication that transformer data has changed
   */
  private void fireDataChangeEvent(Object attribute) {
    for (TransformerChangeListener listener : listeners) {
      listener.transformerDataChanged(this,attribute);
    }
  }

  @Override
  public TransformerImpl clone() {
//    TransformerImpl newTransformer = new TransformerImpl((Transformer)getBaseData());
    TransformerImpl newTransformer = new TransformerImpl(getAttribute(ASSET_ID_KEY,Long.class));

    try {
      deepCopy(newTransformer);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return newTransformer;
  }

}
