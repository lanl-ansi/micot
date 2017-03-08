package gov.lanl.micot.infrastructure.ep.model;

import gov.lanl.micot.infrastructure.model.ComponentImpl;

/**
 * Some common zone implementation information
 * 
 * @author Russell Bent
 */
public class ZoneImpl extends ComponentImpl implements Zone {

  protected static final long      serialVersionUID    = 1L;

  /**
   * Constructor
   * @param identifierKeys
   * @param oredKeys
   * @param additiveKeys
   * @param subtractiveKeys
   */
  protected ZoneImpl(long assetId) {
    super();
    setAttribute(Zone.ASSET_ID_KEY, assetId);
  }
  
  /**
   * Constructor
   * @param zone
   */
//  public ZoneImpl(Zone zone) {
  //  super(zone);
  //}
  
  @Override
  public void setAttribute(Object key, Object object) {
    super.setAttribute(key,object);
  }

  @Override
  public String getName() {
    return getAttribute(ZONE_NAME_KEY,String.class);
  }
  
  @Override
  public ZoneImpl clone() {
//    ZoneImpl newZone = new ZoneImpl((Zone)getBaseData());
    ZoneImpl newZone = new ZoneImpl(getAttribute(ASSET_ID_KEY,Long.class));

    try {
      deepCopy(newZone);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return newZone;
  }

}
