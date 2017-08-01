package gov.lanl.micot.infrastructure.ep.model;

import gov.lanl.micot.infrastructure.model.ComponentImpl;


/**
 * Some common three winding information
 * 
 * @author Russell Bent
 */
public class ThreeWindingTransformerImpl extends ComponentImpl implements ThreeWindingTransformer {

  protected static final long      serialVersionUID    = 1L;

  /**
   * Constructor
   * @param identifierKeys
   * @param oredKeys
   * @param additiveKeys
   * @param subtractiveKeys
   */
  protected ThreeWindingTransformerImpl(long assetId) {
    super();
    setAttribute(ThreeWindingTransformer.ASSET_ID_KEY, assetId);
  }
  
  @Override
  public void setAttribute(Object key, Object object) {
    super.setAttribute(key,object);
  }
  
  @Override
  public ThreeWindingTransformerImpl clone() {
    ThreeWindingTransformerImpl newTransformer = new ThreeWindingTransformerImpl(getAttribute(ASSET_ID_KEY,Long.class));

    try {
      deepCopy(newTransformer);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return newTransformer;
  }

}
