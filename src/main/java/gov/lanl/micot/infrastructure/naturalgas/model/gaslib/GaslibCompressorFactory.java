package gov.lanl.micot.infrastructure.naturalgas.model.gaslib;

import gov.lanl.micot.infrastructure.naturalgas.model.Compressor;
import gov.lanl.micot.infrastructure.naturalgas.model.CompressorFactory;
import gov.lanl.micot.infrastructure.naturalgas.model.FlowUnitEnum;
import gov.lanl.micot.infrastructure.naturalgas.model.Junction;
import gov.lanl.micot.infrastructure.naturalgas.model.LengthUnitEnum;
import gov.lanl.micot.util.geometry.LineImpl;
import gov.lanl.micot.util.geometry.Point;
import gov.lanl.micot.util.io.xml.XMLElement;

import java.util.Vector;

/**
 * Factory for creating a compressor 
 * 
 * @author Russell Bent
 */
public class GaslibCompressorFactory extends CompressorFactory {

  private static final String LEGACY_TAG = "GASLIB";
  
  /**
   * Constructor
   */
  protected GaslibCompressorFactory() {
  }

  /**
   * Creates a capacitor and data from a capacitor
   * 
   * @param compressor
   * @return
   */
  public Compressor createCompressor(XMLElement element, Junction fromJunction, Junction toJunction) {
    String legacyid = element.getValue(GaslibModelConstants.CONNECTION_ID_TAG);
    Compressor compressor = registerCompressor(legacyid);    
    compressor.setAttribute(Compressor.NAME_KEY, legacyid);
   
    // flow min and maxes
    XMLElement flowMinElement = element.getElement(GaslibModelConstants.CONNECTION_FLOW_MIN_TAG);
    String flowMin = flowMinElement.getValue(GaslibModelConstants.VALUE_TAG);
    String flowUnit = flowMinElement.getValue(GaslibModelConstants.UNIT_TAG);
    compressor.setAttribute(Compressor.FLOW_MIN_KEY, Double.parseDouble(flowMin));    
    XMLElement flowMaxElement = element.getElement(GaslibModelConstants.CONNECTION_FLOW_MAX_TAG);
    String flowMax = flowMaxElement.getValue(GaslibModelConstants.VALUE_TAG);
    compressor.setAttribute(Compressor.FLOW_MAX_KEY, Double.parseDouble(flowMax));   
    if (flowUnit.equals(GaslibModelConstants.FLOW_M_CUBED_PER_HOUR_CONSTANT)) {
      compressor.setAttribute(Compressor.FLOW_UNIT_KEY, FlowUnitEnum.M_CUBED_PER_HOUR_TYPE);
    }
    else {
      System.err.println("Error: flow constant " + flowUnit);
    }

    // diameter attribute
    XMLElement diameterElement = element.getElement(GaslibModelConstants.COMPRESSOR_DIAMETER_IN_TAG);
    if (diameterElement != null) {
      String diameter = diameterElement.getValue(GaslibModelConstants.VALUE_TAG);
      String diameterUnit = diameterElement.getValue(GaslibModelConstants.UNIT_TAG);
      compressor.setDiameter(Double.parseDouble(diameter));
      if (diameterUnit.equals(GaslibModelConstants.MM_CONSTANT)) {
        compressor.setAttribute(Compressor.DIAMETER_UNIT_KEY, LengthUnitEnum.MM_TYPE);
      }
      else {
        System.err.println("Error: diameter constant " + diameterUnit);
        }
      }
    else {
        compressor.setDiameter(0);
    }

    // diameter out attribute
    XMLElement diameterOutElement = element.getElement(GaslibModelConstants.COMPRESSOR_DIAMETER_OUT_TAG);
    if (diameterOutElement != null) {
      String diameterOut = diameterOutElement.getValue(GaslibModelConstants.VALUE_TAG);
      String diameterOutUnit = diameterElement.getValue(GaslibModelConstants.UNIT_TAG);
      compressor.setAttribute(Compressor.DIAMETER_OUT_KEY, Double.parseDouble(diameterOut));   
      if (diameterOutUnit.equals(GaslibModelConstants.MM_CONSTANT)) {
        compressor.setAttribute(Compressor.DIAMETER_OUT_UNIT_KEY, LengthUnitEnum.MM_TYPE);
      }
      else {
        System.err.println("Error: diameter constant " + diameterOutUnit);
      }
    }

    // drag factor out attribute
    XMLElement dragFactorOutElement = element.getElement(GaslibModelConstants.COMPRESSOR_DRAG_FACTOR_OUT_TAG);
    if (dragFactorOutElement != null) {
      String dragFactorOut = dragFactorOutElement.getValue(GaslibModelConstants.VALUE_TAG);
      compressor.setAttribute(Compressor.DRAG_FACTOR_OUT_KEY, Double.parseDouble(dragFactorOut));   
    }
    
    // drag factor in attribute
    XMLElement dragFactorInElement = element.getElement(GaslibModelConstants.COMPRESSOR_DRAG_FACTOR_IN_TAG);
    if (dragFactorInElement != null) {
      String dragFactorIn = dragFactorInElement.getValue(GaslibModelConstants.VALUE_TAG);
      compressor.setAttribute(Compressor.DRAG_FACTOR_IN_KEY, Double.parseDouble(dragFactorIn));
    }
    
    // gas cooler existing
    String gasCoolerExisting = element.getValue(GaslibModelConstants.COMPRESSOR_COOLER_EXISTING_TAG);
    compressor.setAttribute(Compressor.GAS_COOLER_EXISTING_KEY, gasCoolerExisting);
    
    // internal bypass required
    String internalBypassRequired = element.getValue(GaslibModelConstants.INTERNAL_BYPASS_REQUIRED_TAG);
    compressor.setAttribute(Compressor.INTERNAL_BYPASS_REQUIRED_KEY, internalBypassRequired);
    
    
    double length = 0.0;
    double resistance = 0.0;
    double minRatio = 1.0;
    double maxRatio = 100.0;
        
    compressor.setLength(length);
    compressor.setResistance(resistance);
    compressor.setCompressionRatio(maxRatio); 		
    compressor.setMaximumCompressionRatio(maxRatio);
    compressor.setMinimumCompressionRatio(minRatio);
    compressor.setInitialCompressionRatio(maxRatio); 
    compressor.setFlow(0.0);						 
    compressor.setCapacity(Double.MAX_VALUE);
    compressor.setStatus(true);
    
    Vector<Point> points = new Vector<Point>();
    points.add(fromJunction.getCoordinate());
    points.add(toJunction.getCoordinate());
    compressor.setCoordinates(new LineImpl(points));
     
    // pressure attribute
    XMLElement pressureInElement = element.getElement(GaslibModelConstants.PRESSURE_IN_MIN_TAG);
    XMLElement pressureOutElement = element.getElement(GaslibModelConstants.PRESSURE_OUT_MAX_TAG);
    String pressureIn = pressureInElement.getValue(GaslibModelConstants.VALUE_TAG);
    String pressureOut = pressureOutElement.getValue(GaslibModelConstants.VALUE_TAG);
      
    // somewhat of a hack to get the pressure min and maxes into the system
    fromJunction.setMinimumPressure(Math.max(fromJunction.getMinimumPressure(), Double.parseDouble(pressureIn)));
    toJunction.setMaximumPressure(Math.min(toJunction.getMaximumPressure(), Double.parseDouble(pressureOut)));
        
    return compressor;
  }

  /**
   * Create an ieiss compressor based upon another compressor
   * 
   * @param compressor
   * @param shuntCapacitorData
   * @param busId
   * @return
   */
  public void updateCompressor(Compressor compressor, Junction fromJunction, Junction toJunction, String legacyid) {
    if (compressor.getAttribute(GaslibModelConstants.GASLIB_LEGACY_ID_KEY) == null) {
//      String legacyid = findUnusedId();
      compressor.setAttribute(GaslibModelConstants.GASLIB_LEGACY_ID_KEY, legacyid);
      registerLegacy(LEGACY_TAG, legacyid, compressor);
    }
  }

 /**
  * Register the compressor
  * @param legacyId
  * @param bus
  * @return
  */
 private Compressor registerCompressor(String legacyId) {
   Compressor compressor = getLegacy(LEGACY_TAG, legacyId);
   if (compressor == null) {
     compressor = createNewCompressor();
     compressor.addOutputKey(GaslibModelConstants.GASLIB_LEGACY_ID_KEY);
     compressor.setAttribute(GaslibModelConstants.GASLIB_LEGACY_ID_KEY,legacyId);
     registerLegacy(LEGACY_TAG, legacyId, compressor);
   }
   return compressor;
 }
 
 @Override
 protected Compressor getLegacy(String legacyTag, Object key) {
   return super.getLegacy(legacyTag, key);
 }
 
}
