package gov.lanl.micot.infrastructure.naturalgas.model.gaslib;

//import org.dom4j.Element;

import gov.lanl.micot.infrastructure.naturalgas.model.Compressor;
import gov.lanl.micot.infrastructure.naturalgas.model.Junction;
import gov.lanl.micot.infrastructure.naturalgas.model.JunctionFactory;
import gov.lanl.micot.infrastructure.naturalgas.model.PressureUnitEnum;
import gov.lanl.micot.util.geometry.PointImpl;
import gov.lanl.micot.util.io.xml.XMLElement;

/**
 * Factory for create junction related information
 * 
 * @author Russell Bent
 */
public class GaslibJunctionFactory extends JunctionFactory {

  private static final String LEGACY_TAG = "GASLIB";

  /**
   * Singleton constructor
   */
  protected GaslibJunctionFactory() {
  }

  /**
   * Creates a junction
   * 
   * @param line
   * @return
   */
  public Junction createJunction(XMLElement element) {
    
    String legacyid = element.getValue(GaslibModelConstants.NODE_ID_TAG);    
    Junction junction = registerJunction(legacyid);
    junction.setAttribute(Compressor.NAME_KEY, legacyid);
    
    initializeJunction(junction);

//    String longitude = element.attributeValue(GaslibModelConstants.NODE_LONGITUDE_TAG);
  //  String latitude = element.attributeValue(GaslibModelConstants.NODE_LATITUDE_TAG);
    String longitude = element.getValue(GaslibModelConstants.NODE_LONGITUDE_TAG);
    String latitude = element.getValue(GaslibModelConstants.NODE_LATITUDE_TAG);
    
    XMLElement pressureMinElement = element.getElement(GaslibModelConstants.NODE_PRESSURE_MIN_TAG);
    String pressureMin = pressureMinElement.getValue(GaslibModelConstants.VALUE_TAG);
    String pressureUnit = pressureMinElement.getValue(GaslibModelConstants.UNIT_TAG);
    
    XMLElement pressureMaxElement = element.getElement(GaslibModelConstants.NODE_PRESSURE_MAX_TAG);
    String pressureMax = pressureMaxElement.getValue(GaslibModelConstants.VALUE_TAG);

    XMLElement heightElement = element.getElement(GaslibModelConstants.NODE_HEIGHT_TAG);
    String height = heightElement.getValue(GaslibModelConstants.VALUE_TAG);
        
    double y = Double.parseDouble(latitude);
    double x = Double.parseDouble(longitude);
    double pMin = Double.parseDouble(pressureMin);
    double pMax = Double.parseDouble(pressureMax);

    junction.setPressure(0.0);
    junction.setStatus(true);
    junction.setCoordinate(new PointImpl(x, y));
    junction.setInitialPressure(0.0);
    junction.setMaximumPressure(pMax);
    junction.setMinimumPressure(pMin);
    junction.setAttribute(Junction.HEIGHT_KEY, Double.parseDouble(height));
    
    if (pressureUnit.equals(GaslibModelConstants.BAR_PRESSURE_CONSTANT)) {
      junction.setAttribute(Junction.PRESSURE_UNIT_KEY, PressureUnitEnum.BAR_TYPE);
    }
    else {
      System.err.println("Error: unknown pressure constant " + pressureUnit);
    }

    return junction;
  }

  /**
   * Creates a junction from another junction
   * 
   * @param bus
   * @return
   */
  public void updateJunction(Junction junction) {
    if (junction.getAttribute(GaslibModelConstants.GASLIB_LEGACY_ID_KEY) == null) {
      String legacyId = findUnusedId();
      junction.setAttribute(GaslibModelConstants.GASLIB_LEGACY_ID_KEY, legacyId);
      registerLegacy(LEGACY_TAG, legacyId, junction);
    }
  }

  /**
   * Register the junction
   * 
   * @param legacyId
   * @param bus
   * @return
   */
  private Junction registerJunction(String legacyId) {
    Junction junction = getLegacy(LEGACY_TAG, legacyId);
    if (junction == null) {
      junction = createNewJunction();
      junction.addOutputKey(GaslibModelConstants.GASLIB_LEGACY_ID_KEY);
      junction.setAttribute(GaslibModelConstants.GASLIB_LEGACY_ID_KEY, legacyId);
      registerLegacy(LEGACY_TAG, legacyId, junction);
    }
    return junction;
  }

  /**
   * Find an unused id number
   * 
   * @return
   */
  private String findUnusedId() {
    for (int i = 0; i < Integer.MAX_VALUE; ++i) {
      String id = i+"";
      if (getLegacy(LEGACY_TAG, id) == null) {
        return id;
      }
    }
    throw new RuntimeException("Error: Cannot find an unused id");
  }
  
}
