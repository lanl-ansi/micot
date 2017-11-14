package gov.lanl.micot.infrastructure.naturalgas.model.gaslib;

import gov.lanl.micot.infrastructure.naturalgas.model.FlowUnitEnum;
import gov.lanl.micot.infrastructure.naturalgas.model.Junction;
import gov.lanl.micot.infrastructure.naturalgas.model.ShortPipe;
import gov.lanl.micot.infrastructure.naturalgas.model.ShortPipeFactory;
import gov.lanl.micot.util.geometry.LineImpl;
import gov.lanl.micot.util.geometry.Point;
import gov.lanl.micot.util.io.xml.XMLElement;

import java.util.Vector;

/**
 * Factory for creating a compressor 
 * 
 * @author Russell Bent
 */
public class GaslibShortPipeFactory extends ShortPipeFactory {

  private static final String LEGACY_TAG = "GASLIB";
  
  /**
   * Constructor
   */
  protected GaslibShortPipeFactory() {
  }

  /**
   * Create the short pipe
   * @param element
   * @param fromJcn
   * @param toJcn
   * @return
   */
  public ShortPipe createShortPipe(XMLElement element, Junction fromJcn, Junction toJcn) {
    String legacyid = element.getValue(GaslibModelConstants.CONNECTION_ID_TAG);
    ShortPipe pipe = registerShortPipe(legacyid);
    pipe.setAttribute(ShortPipe.NAME_KEY, legacyid);
    
    // flow min and maxes
    XMLElement flowMinElement = element.getElement(GaslibModelConstants.CONNECTION_FLOW_MIN_TAG);
    String flowMin = flowMinElement.getValue(GaslibModelConstants.VALUE_TAG);
    String flowUnit = flowMinElement.getValue(GaslibModelConstants.UNIT_TAG);
    pipe.setAttribute(ShortPipe.FLOW_MIN_KEY, Double.parseDouble(flowMin));    
    XMLElement flowMaxElement = element.getElement(GaslibModelConstants.CONNECTION_FLOW_MAX_TAG);
    String flowMax = flowMaxElement.getValue(GaslibModelConstants.VALUE_TAG);
    pipe.setAttribute(ShortPipe.FLOW_MAX_KEY, Double.parseDouble(flowMax));   
    if (flowUnit.equals(GaslibModelConstants.FLOW_M_CUBED_PER_HOUR_CONSTANT)) {
      pipe.setAttribute(ShortPipe.FLOW_UNIT_KEY, FlowUnitEnum.M_CUBED_PER_HOUR_TYPE);
    }
    else {
      System.err.println("Error: flow constant " + flowUnit);
    }

    pipe.setResistance(-Double.MAX_VALUE);
    pipe.setFlow(0.0);
    pipe.setCapacity(Double.MAX_VALUE);
    pipe.setStatus(true);
    
    Vector<Point> points = new Vector<Point>();
    points.add(fromJcn.getCoordinate());
    points.add(toJcn.getCoordinate());
    pipe.setCoordinates(new LineImpl(points));

    return pipe;
  }

  /**
   * Create an short pipe based upon another compressor
   * 
   * @param compressor
   * @param shuntCapacitorData
   * @param busId
   * @return
   */
  public void updateShortPipe(ShortPipe pipe, Junction fromJunction, Junction toJunction, String legacyid) {
    if (pipe.getAttribute(GaslibModelConstants.GASLIB_LEGACY_ID_KEY) == null) {
//      String legacyid = findUnusedId();
      pipe.setAttribute(GaslibModelConstants.GASLIB_LEGACY_ID_KEY, legacyid);
      registerLegacy(LEGACY_TAG, legacyid, pipe);
    }
  }

 /**
  * Register the compressor
  * @param legacyId
  * @param bus
  * @return
  */
 private ShortPipe registerShortPipe(String legacyId) {
   ShortPipe pipe = getLegacy(LEGACY_TAG, legacyId);
   if (pipe == null) {
     pipe = createNewShortPipe();
     pipe.addOutputKey(GaslibModelConstants.GASLIB_LEGACY_ID_KEY);
     pipe.setAttribute(GaslibModelConstants.GASLIB_LEGACY_ID_KEY,legacyId);
     registerLegacy(LEGACY_TAG, legacyId, pipe);
   }
   return pipe;
 }
 
 /**
  * Find an unused id number
  * 
  * @return
  */
 //private String findUnusedId() {
   //GaslibPipeFactory factory = GaslibPipeFactory.getInstance();   
   //for (int i = 0; i < Integer.MAX_VALUE; ++i) {
     //if (getLegacy(LEGACY_TAG, i+"") == null && factory.getLegacy(LEGACY_TAG, i+"") == null) {
       //return i+"";
    // }
   //}
   //throw new RuntimeException("Error: Cannot find an unused id");
 //}
 
 @Override
 protected ShortPipe getLegacy(String legacyTag, Object key) {
   return super.getLegacy(legacyTag, key);
 }
 
}
