package gov.lanl.micot.infrastructure.naturalgas.model.gaslib;

import java.util.Vector;

import gov.lanl.micot.infrastructure.naturalgas.model.FlowUnitEnum;
import gov.lanl.micot.infrastructure.naturalgas.model.Junction;
import gov.lanl.micot.infrastructure.naturalgas.model.LengthUnitEnum;
import gov.lanl.micot.infrastructure.naturalgas.model.Resistor;
import gov.lanl.micot.infrastructure.naturalgas.model.ResistorFactory;
import gov.lanl.micot.util.geometry.LineImpl;
import gov.lanl.micot.util.geometry.Point;
import gov.lanl.micot.util.io.xml.XMLElement;

/**
 * Factory for creating resistors
 * 
 * @author Russell Bent
 */
public class GaslibResistorFactory extends ResistorFactory {

  private static final String LEGACY_TAG               = "GASLIB";
  
  /**
   * Singleton constructor
   */
  protected GaslibResistorFactory() {
  }
    
  /**
   * Create the valve
   * @param element
   * @param fromJcn
   * @param toJcn
   * @return
   */
  public Resistor createResistor(XMLElement element, Junction fromJcn, Junction toJcn) {
    String legacyid = element.getValue(GaslibModelConstants.CONNECTION_ID_TAG);
    Resistor resistor = registerResistor(legacyid);
    resistor.setAttribute(Resistor.NAME_KEY, legacyid);

     // flow min and maxes
    XMLElement flowMinElement = element.getElement(GaslibModelConstants.CONNECTION_FLOW_MIN_TAG);
    String flowMin = flowMinElement.getValue(GaslibModelConstants.VALUE_TAG);
    String flowUnit = flowMinElement.getValue(GaslibModelConstants.UNIT_TAG);
    resistor.setAttribute(Resistor.FLOW_MIN_KEY, Double.parseDouble(flowMin));    
    XMLElement flowMaxElement = element.getElement(GaslibModelConstants.CONNECTION_FLOW_MAX_TAG);
    String flowMax = flowMaxElement.getValue(GaslibModelConstants.VALUE_TAG);
    resistor.setAttribute(Resistor.FLOW_MAX_KEY, Double.parseDouble(flowMax));   
    if (flowUnit.equals(GaslibModelConstants.FLOW_M_CUBED_PER_HOUR_CONSTANT)) {
      resistor.setAttribute(Resistor.FLOW_UNIT_KEY, FlowUnitEnum.M_CUBED_PER_HOUR_TYPE);
    }
    else {
      System.err.println("Error: flow constant " + flowUnit);
    }
    
    // diameter attribute
    XMLElement diameterElement = element.getElement(GaslibModelConstants.CONNECTION_DIAMETER_TAG);
    String diameter = diameterElement.getValue(GaslibModelConstants.VALUE_TAG);
    String diameterUnit = diameterElement.getValue(GaslibModelConstants.UNIT_TAG);
    resistor.setDiameter(Double.parseDouble(diameter));
    if (diameterUnit.equals(GaslibModelConstants.MM_CONSTANT)) {
      resistor.setAttribute(Resistor.DIAMETER_UNIT_KEY, LengthUnitEnum.MM_TYPE);
    }
    else {
      System.err.println("Error: diameter constant " + diameterUnit);
    }
    
    resistor.setLength(0.0);
    resistor.setResistance(-Double.MAX_VALUE);
    resistor.setFlow(0.0);
    resistor.setCapacity(Double.MAX_VALUE);
    resistor.setActualStatus(true);
    resistor.setDesiredStatus(true);
    
    Vector<Point> points = new Vector<Point>();
    points.add(fromJcn.getCoordinate());
    points.add(toJcn.getCoordinate());
    resistor.setCoordinates(new LineImpl(points));
    
    // pressure differential max
    XMLElement dragFactorElement = element.getElement(GaslibModelConstants.DRAG_FACTOR_TAG);
    String dragFactor = dragFactorElement.getValue(GaslibModelConstants.VALUE_TAG);
    resistor.setAttribute(Resistor.DRAG_FACTOR_KEY, Double.parseDouble(dragFactor));    
    
    return resistor;
  }

  /**
   * Register the resistor
   * @param legacyId
   * @param bus
   * @return
   */
  private Resistor registerResistor(String legacyId) {
    Resistor resistor = getLegacy(LEGACY_TAG, legacyId);
    if (resistor == null) {
      resistor = createNewResistor();
      resistor.addOutputKey(GaslibModelConstants.GASLIB_LEGACY_ID_KEY);
      resistor.setAttribute(GaslibModelConstants.GASLIB_LEGACY_ID_KEY,legacyId);
      registerLegacy(LEGACY_TAG, legacyId, resistor);
    }
    return resistor;
  }

  
  
}
