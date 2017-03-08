package gov.lanl.micot.infrastructure.naturalgas.model.gaslib;

import java.util.Vector;

import gov.lanl.micot.infrastructure.naturalgas.model.ControlValve;
import gov.lanl.micot.infrastructure.naturalgas.model.ControlValveFactory;
import gov.lanl.micot.infrastructure.naturalgas.model.FlowUnitEnum;
import gov.lanl.micot.infrastructure.naturalgas.model.Junction;
import gov.lanl.micot.infrastructure.naturalgas.model.PressureUnitEnum;
import gov.lanl.micot.util.geometry.LineImpl;
import gov.lanl.micot.util.geometry.Point;
import gov.lanl.micot.util.io.xml.XMLElement;

/**
 * Factory for creating valves
 * 
 * @author Russell Bent
 */
public class GaslibControlValveFactory extends ControlValveFactory {

  private static final String LEGACY_TAG               = "GASLIB";
  
  /**
   * Constructor
   */
  protected GaslibControlValveFactory() {
  }
    
  /**
   * Register the valve
   * @param legacyId
   * @param bus
   * @return
   */
  private ControlValve registerValve(String legacyId) {
    ControlValve valve = getLegacy(LEGACY_TAG, legacyId);
    if (valve == null) {
      valve = createNewControlValve();
      valve.addOutputKey(GaslibModelConstants.GASLIB_LEGACY_ID_KEY);
      valve.setAttribute(GaslibModelConstants.GASLIB_LEGACY_ID_KEY,legacyId);
      registerLegacy(LEGACY_TAG, legacyId, valve);
    }
    return valve;
  }

  /**
   * create the control valve
   * @param element
   * @param fromJcn
   * @param toJcn
   * @return
   */
  public ControlValve createControlValve(XMLElement element, Junction fromJcn, Junction toJcn) {
    String legacyid = element.getValue(GaslibModelConstants.CONNECTION_ID_TAG);
    ControlValve valve = registerValve(legacyid);
    valve.setAttribute(ControlValve.NAME_KEY, legacyid);
    
    // flow min and maxes
    XMLElement flowMinElement = element.getElement(GaslibModelConstants.CONNECTION_FLOW_MIN_TAG);
    String flowMin = flowMinElement.getValue(GaslibModelConstants.VALUE_TAG);
    String flowUnit = flowMinElement.getValue(GaslibModelConstants.UNIT_TAG);
    valve.setAttribute(ControlValve.FLOW_MIN_KEY, Double.parseDouble(flowMin));    
    XMLElement flowMaxElement = element.getElement(GaslibModelConstants.CONNECTION_FLOW_MAX_TAG);
    String flowMax = flowMaxElement.getValue(GaslibModelConstants.VALUE_TAG);
    valve.setAttribute(ControlValve.FLOW_MAX_KEY, Double.parseDouble(flowMax));   
    if (flowUnit.equals(GaslibModelConstants.FLOW_M_CUBED_PER_HOUR_CONSTANT)) {
      valve.setAttribute(ControlValve.FLOW_UNIT_KEY, FlowUnitEnum.M_CUBED_PER_HOUR_TYPE);
    }
    else {
      System.err.println("Error: flow constant " + flowUnit);
    }

    valve.setLength(0.0);
    valve.setDiameter(0.0);
    valve.setResistance(-Double.MAX_VALUE);
    valve.setFlow(0.0);
    valve.setCapacity(Double.MAX_VALUE);
    valve.setActualStatus(true);
    valve.setDesiredStatus(true);
    
    Vector<Point> points = new Vector<Point>();
    points.add(fromJcn.getCoordinate());
    points.add(toJcn.getCoordinate());
    valve.setCoordinates(new LineImpl(points));
        
    // pressure differential max
    XMLElement maxPressureDifferentialMaxElement = element.getElement(GaslibModelConstants.CONNECTION_FPRESSURE_DIFFERENTIAL_MAX_TAG);
    String maxPressureDifferential = maxPressureDifferentialMaxElement.getValue(GaslibModelConstants.VALUE_TAG);
    String pressureDifferentialUnit = maxPressureDifferentialMaxElement.getValue(GaslibModelConstants.UNIT_TAG);
    valve.setAttribute(ControlValve.PRESSURE_DIFFERENTIAL_MAX_KEY, Double.parseDouble(maxPressureDifferential));    
    if (pressureDifferentialUnit.equals(GaslibModelConstants.BAR_PRESSURE_CONSTANT)) {
      valve.setAttribute(ControlValve.PRESSURE_DIFFERENTIAL_UNIT_KEY, PressureUnitEnum.BAR_TYPE);
    }
    else {
      System.err.println("Error: pressure differential " + pressureDifferentialUnit);
    }
    
    // pressure differential min
    XMLElement pressureDifferentialMinElement = element.getElement(GaslibModelConstants.CONNECTION_FPRESSURE_DIFFERENTIAL_MIN_TAG);
    String minPressureDifferential = pressureDifferentialMinElement.getValue(GaslibModelConstants.VALUE_TAG);
    valve.setAttribute(ControlValve.PRESSURE_DIFFERENTIAL_MIN_KEY, Double.parseDouble(minPressureDifferential));    

    // internal bypass required
    String internalBypassRequired = element.getValue(GaslibModelConstants.INTERNAL_BYPASS_REQUIRED_TAG);
    valve.setAttribute(ControlValve.INTERNAL_BYPASS_REQUIRED_KEY, internalBypassRequired);

    // gas preheater existing required
    String gasPreheaterExisting = element.getValue(GaslibModelConstants.GAS_PREHEATER_EXISTING_TAG);
    valve.setAttribute(ControlValve.GAS_PREHEATER_EXISTING_KEY, gasPreheaterExisting);
    
    // pressure attribute
    XMLElement pressureInElement = element.getElement(GaslibModelConstants.PRESSURE_IN_MIN_TAG);
    XMLElement pressureOutElement = element.getElement(GaslibModelConstants.PRESSURE_OUT_MAX_TAG);
    String pressureIn = pressureInElement.getValue(GaslibModelConstants.VALUE_TAG);
    String pressureOut = pressureOutElement.getValue(GaslibModelConstants.VALUE_TAG);
    
    // pressure loss 
    XMLElement pressureLossInElement = element.getElement(GaslibModelConstants.VALVE_PRESSURE_LOSS_IN_TAG);
    String pressureLossIn = pressureLossInElement.getValue(GaslibModelConstants.VALUE_TAG);
    String pressureLossUnit = pressureLossInElement.getValue(GaslibModelConstants.UNIT_TAG);
    valve.setAttribute(ControlValve.PRESSURE_LOSS_IN_KEY, Double.parseDouble(pressureLossIn));    
    if (pressureLossUnit.equals(GaslibModelConstants.BAR_PRESSURE_CONSTANT)) {
      valve.setAttribute(ControlValve.PRESSURE_LOSS_UNIT_KEY, PressureUnitEnum.BAR_TYPE);
    }
    else {
      System.err.println("Error: pressure loss " + pressureLossUnit);
    }

    // pressure loss in
    XMLElement pressureLossOutElement = element.getElement(GaslibModelConstants.VALVE_PRESSURE_LOSS_OUT_TAG);
    String pressureLossOut = pressureLossOutElement.getValue(GaslibModelConstants.VALUE_TAG);
    valve.setAttribute(ControlValve.PRESSURE_LOSS_OUT_KEY, Double.parseDouble(pressureLossOut));    

    // somewhat of a hack to get the pressure min and maxes into the system
    fromJcn.setMinimumPressure(Math.max(fromJcn.getMinimumPressure(), Double.parseDouble(pressureIn)));
    toJcn.setMaximumPressure(Math.min(toJcn.getMaximumPressure(), Double.parseDouble(pressureOut)));

    valve.setAttribute(ControlValve.MINIMUM_COMPRESSION_RATIO_KEY, 0.0);
    valve.setAttribute(ControlValve.MAXIMUM_COMPRESSION_RATIO_KEY, 1.0);
    
    return valve;
  }

  
  
}
