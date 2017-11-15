package gov.lanl.micot.infrastructure.naturalgas.model.gaslib;

import gov.lanl.micot.infrastructure.naturalgas.model.CityGate;
import gov.lanl.micot.infrastructure.naturalgas.model.CityGateFactory;
import gov.lanl.micot.infrastructure.naturalgas.model.Compressor;
import gov.lanl.micot.infrastructure.naturalgas.model.FlowUnitEnum;
import gov.lanl.micot.infrastructure.naturalgas.model.Junction;
import gov.lanl.micot.infrastructure.naturalgas.model.Well;
import gov.lanl.micot.util.geometry.PointImpl;
import gov.lanl.micot.util.io.xml.XMLElement;

/**
 * Factory for creating city gates
 * 
 * @author Russell Bent
 */
public class GaslibCityGateFactory extends CityGateFactory {

  private static final String LEGACY_TAG = "GASLIB";
  
  /**
   * Singleton constructor
   */
  protected GaslibCityGateFactory() {
  }

  /**
   * Creates a bus and data from a bus
   * 
   * @param line
   * @return
   */
  public CityGate createCityGate(XMLElement element, Junction junction) {

    String legacyid = element.getValue(GaslibModelConstants.NODE_ID_TAG);

    String longitude = element.getValue(GaslibModelConstants.NODE_LONGITUDE_TAG);
    String latitude = element.getValue(GaslibModelConstants.NODE_LATITUDE_TAG);
    double y = Double.parseDouble(latitude);
    double x = Double.parseDouble(longitude);

    XMLElement flowMinElement = element.getElement(GaslibModelConstants.NODE_FLOW_MIN_TAG);
    String flowMin = flowMinElement.getValue(GaslibModelConstants.VALUE_TAG);
    String flowUnit = flowMinElement.getValue(GaslibModelConstants.UNIT_TAG);

    XMLElement flowMaxElement = element.getElement(GaslibModelConstants.NODE_FLOW_MAX_TAG);
    String flowMax = flowMaxElement.getValue(GaslibModelConstants.VALUE_TAG);
       
    double maxConsumption = Double.parseDouble(flowMax);
    double minConsumption = Double.parseDouble(flowMin);
    double consumption = maxConsumption;
    
    CityGate gate = registerGate(legacyid);
    initializeGate(gate, junction, consumption);
    gate.setAttribute(Compressor.NAME_KEY, legacyid);
        
    gate.setConsumption(consumption);
    gate.setStatus(true);
    gate.setCoordinate(new PointImpl(x,y));
    gate.setMaximumConsumption(maxConsumption);
    gate.setMinimumConsumption(minConsumption);

    if (flowUnit.equals(GaslibModelConstants.FLOW_M_CUBED_PER_HOUR_CONSTANT)) {
      gate.setAttribute(Well.FLOW_UNIT_KEY, FlowUnitEnum.M_CUBED_PER_HOUR_TYPE);
    }
    else {
      System.err.println("Error: unknown flow unit constant " + flowUnit);
    }

    
    return gate;
  }

  /**
   * Creates an ieiss load from another load object
   * 
   * @param load
   * @param loadData
   * @param busId
   * @return
   */
  protected void updateCityGate(CityGate gate, Junction junction) {
    if (gate.getAttribute(GaslibModelConstants.GASLIB_LEGACY_ID_KEY) == null) {
      int legacyid = junction.getAttribute(GaslibModelConstants.GASLIB_LEGACY_ID_KEY, Integer.class);
      gate.setAttribute(GaslibModelConstants.GASLIB_LEGACY_ID_KEY, legacyid);
      registerLegacy(LEGACY_TAG, legacyid, gate);
    }
  }

  /**
   * Register the city gate
   * 
   * @param legacyId
   * @param bus
   * @return
   */
  private CityGate registerGate(String legacyId) {
    CityGate gate = getLegacy(LEGACY_TAG, legacyId);
    if (gate == null) {
      gate = createNewCityGate();
      gate.setAttribute(GaslibModelConstants.GASLIB_LEGACY_ID_KEY, legacyId);
      gate.addOutputKey(GaslibModelConstants.GASLIB_LEGACY_ID_KEY);
      registerLegacy(LEGACY_TAG, legacyId, gate);      
    }
    return gate;
  }


}
