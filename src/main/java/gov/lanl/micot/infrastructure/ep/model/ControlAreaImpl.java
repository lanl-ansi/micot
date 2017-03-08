package gov.lanl.micot.infrastructure.ep.model;

import gov.lanl.micot.infrastructure.model.ComponentImpl;


/**
 * Some common bus implementation information
 * 
 * @author Russell Bent
 */
public class ControlAreaImpl extends ComponentImpl implements ControlArea {

  protected static final long      serialVersionUID    = 1L;

  /**
   * Constructor
   * @param identifierKeys
   * @param oredKeys
   * @param additiveKeys
   * @param subtractiveKeys
   */
  protected ControlAreaImpl(long assetId) {
    super();
    setAttribute(ControlArea.ASSET_ID_KEY, assetId);
  }
  
  /**
   * Constructor
   * @param bus
   */
//  public ControlAreaImpl(ControlArea area) {
  //  super(area);
  //}
  
  @Override
  public void setAttribute(Object key, Object object) {
    super.setAttribute(key,object);
  }
  
  @Override
  public ControlAreaImpl clone() {
//    ControlAreaImpl newArea = new ControlAreaImpl((ControlArea)getBaseData());
    ControlAreaImpl newArea = new ControlAreaImpl(getAttribute(ASSET_ID_KEY,Long.class));

    try {
      deepCopy(newArea);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return newArea;
  }

}
