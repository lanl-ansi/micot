package gov.lanl.micot.infrastructure.naturalgas.model.gaslib;

import java.util.Vector;

import gov.lanl.micot.infrastructure.naturalgas.model.FlowUnitEnum;
import gov.lanl.micot.infrastructure.naturalgas.model.Junction;
import gov.lanl.micot.infrastructure.naturalgas.model.PressureUnitEnum;
import gov.lanl.micot.infrastructure.naturalgas.model.Valve;
import gov.lanl.micot.infrastructure.naturalgas.model.ValveFactory;
import gov.lanl.micot.util.geometry.LineImpl;
import gov.lanl.micot.util.geometry.Point;
import gov.lanl.micot.util.io.xml.XMLElement;

/**
 * Factory for creating valves
 * 
 * @author Russell Bent
 */
public class GaslibValveFactory extends ValveFactory {

  private static final String LEGACY_TAG               = "GASLIB";
  
  /**
   * Singleton constructor
   */
  protected GaslibValveFactory() {
  }
  
  
  /**
   * Create the valve
   * @param element
   * @param fromJcn
   * @param toJcn
   * @return
   */
  public Valve createValve(XMLElement element, Junction fromJcn, Junction toJcn) {
    String legacyid = element.getValue(GaslibModelConstants.CONNECTION_ID_TAG);
    Valve valve = registerValve(legacyid);
    valve.setAttribute(Valve.NAME_KEY, legacyid);
    
    // flow min and maxes
    XMLElement flowMinElement = element.getElement(GaslibModelConstants.CONNECTION_FLOW_MIN_TAG);
    String flowMin = flowMinElement.getValue(GaslibModelConstants.VALUE_TAG);
    String flowUnit = flowMinElement.getValue(GaslibModelConstants.UNIT_TAG);
    valve.setAttribute(Valve.FLOW_MIN_KEY, Double.parseDouble(flowMin));    
    XMLElement flowMaxElement = element.getElement(GaslibModelConstants.CONNECTION_FLOW_MAX_TAG);
    String flowMax = flowMaxElement.getValue(GaslibModelConstants.VALUE_TAG);
    valve.setAttribute(Valve.FLOW_MAX_KEY, Double.parseDouble(flowMax));   
    if (flowUnit.equals(GaslibModelConstants.FLOW_M_CUBED_PER_HOUR_CONSTANT)) {
      valve.setAttribute(Valve.FLOW_UNIT_KEY, FlowUnitEnum.M_CUBED_PER_HOUR_TYPE);
    }
    else {
      System.err.println("Error: flow constant " + flowUnit);
    }

    valve.setLength(0.0);
    valve.setDiameter(0.0);
    valve.setResistance(-Double.MAX_VALUE);
    valve.setFlow(0.0);
    valve.setCapacity(Double.MAX_VALUE);
    valve.setStatus(true);
    valve.setAttribute(Valve.PRESSURE_LOSS_OUT_KEY, 0.0);    
    valve.setAttribute(Valve.PRESSURE_LOSS_IN_KEY, 0.0);    
    
    Vector<Point> points = new Vector<Point>();
    points.add(fromJcn.getCoordinate());
    points.add(toJcn.getCoordinate());
    valve.setCoordinates(new LineImpl(points));
        
    // pressure differential max
    XMLElement pressureDifferentialMaxElement = element.getElement(GaslibModelConstants.CONNECTION_FPRESSURE_DIFFERENTIAL_MAX_TAG);
    String pressureDifferential = pressureDifferentialMaxElement.getValue(GaslibModelConstants.VALUE_TAG);
    String pressureDifferentialUnit = pressureDifferentialMaxElement.getValue(GaslibModelConstants.UNIT_TAG);
    valve.setAttribute(Valve.PRESSURE_DIFFERENTIAL_MAX_KEY, Double.parseDouble(pressureDifferential));    
    if (pressureDifferentialUnit.equals(GaslibModelConstants.BAR_PRESSURE_CONSTANT)) {
      valve.setAttribute(Valve.PRESSURE_DIFFERENTIAL_UNIT_KEY, PressureUnitEnum.BAR_TYPE);
    }
    else {
      System.err.println("Error: pressure differential " + pressureDifferentialUnit);
    }
    
    return valve;
  }

  /**
   * Register the valve
   * @param legacyId
   * @param bus
   * @return
   */
  private Valve registerValve(String legacyId) {
    Valve valve = getLegacy(LEGACY_TAG, legacyId);
    if (valve == null) {
      valve = createNewValve();
      valve.addOutputKey(GaslibModelConstants.GASLIB_LEGACY_ID_KEY);
      valve.setAttribute(GaslibModelConstants.GASLIB_LEGACY_ID_KEY,legacyId);
      registerLegacy(LEGACY_TAG, legacyId, valve);
    }
    return valve;
  }

  
  
}
