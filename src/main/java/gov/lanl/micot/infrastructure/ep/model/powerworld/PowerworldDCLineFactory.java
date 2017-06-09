package gov.lanl.micot.infrastructure.ep.model.powerworld;

import java.util.ArrayList;
import java.util.Vector;

import gov.lanl.micot.infrastructure.ep.io.powerworld.PowerworldIOConstants;
import gov.lanl.micot.infrastructure.ep.model.Bus;
import gov.lanl.micot.infrastructure.ep.model.DCLine;
import gov.lanl.micot.infrastructure.ep.model.DCTwoTerminalLineControlModeEnum;
import gov.lanl.micot.infrastructure.ep.model.DCLineFactory;
import gov.lanl.micot.infrastructure.ep.model.DCMultiTerminalLine;
import gov.lanl.micot.infrastructure.ep.model.DCMultiTerminalLineTerminalTypeEnum;
import gov.lanl.micot.infrastructure.ep.model.DCTwoTerminalLine;
import gov.lanl.micot.infrastructure.ep.model.DCVoltageSourceLine;
import gov.lanl.micot.infrastructure.ep.model.DCVoltageSourceLineACControlModeEnum;
import gov.lanl.micot.infrastructure.ep.model.DCVoltageSourceLineDCControlModeEnum;
import gov.lanl.micot.util.collection.Triple;
import gov.lanl.micot.util.geometry.LineImpl;
import gov.lanl.micot.util.geometry.Point;
import gov.lanl.micot.util.io.dcom.ComDataObject;
import gov.lanl.micot.util.io.dcom.ComObject;

/**
 * Factory for creating power world dc lines
 * @author Russell Bent
 */
public class PowerworldDCLineFactory extends DCLineFactory {

  private static final String LEGACY_TAG = "POWERWORLD";
  
  /**
   * Singleton constructor
   */
  protected PowerworldDCLineFactory() {    
  }
  
  /**
   * Register the dc line
   * @param legacyId
   * @return
   */
  private DCTwoTerminalLine registerTwoTerminalLine(Triple<Integer,Integer,String> legacyId) {
    DCLine line = getLegacy(LEGACY_TAG, legacyId);
    if (line == null) {
      line = createNewTwoTerminalLine();
      line.setAttribute(PowerworldModelConstants.POWERWORLD_LEGACY_ID_KEY,legacyId);
      line.addOutputKey(PowerworldModelConstants.POWERWORLD_LEGACY_ID_KEY);
      registerLegacy(LEGACY_TAG, legacyId, line);
    }
    return (DCTwoTerminalLine)line;
  }

  /**
   * Register the dc line
   * @param legacyId
   * @return
   */
  private DCMultiTerminalLine registerMultiTerminalLine(Triple<Integer,Integer,String> legacyId) {
    DCLine line = getLegacy(LEGACY_TAG, legacyId);
    if (line == null) {
      line = createNewTwoTerminalLine();
      line.setAttribute(PowerworldModelConstants.POWERWORLD_LEGACY_ID_KEY,legacyId);
      line.addOutputKey(PowerworldModelConstants.POWERWORLD_LEGACY_ID_KEY);
      registerLegacy(LEGACY_TAG, legacyId, line);
    }
    return (DCMultiTerminalLine)line;
  }

  
  /**
   * Register the dc line
   * @param legacyId
   * @return
   */
  private DCVoltageSourceLine registerVoltageSourceLine(Triple<Integer,Integer,String> legacyId) {
    DCLine line = getLegacy(LEGACY_TAG, legacyId);
    if (line == null) {
      line = createNewVoltageSourceLine();
      line.setAttribute(PowerworldModelConstants.POWERWORLD_LEGACY_ID_KEY,legacyId);
      line.addOutputKey(PowerworldModelConstants.POWERWORLD_LEGACY_ID_KEY);
      registerLegacy(LEGACY_TAG, legacyId, line);
    }
    return (DCVoltageSourceLine)line;
  }

  
  /**
   * Creates a two terminal dc line and data
   * @param line
   * @return
   */
  public DCTwoTerminalLine createTwoTerminalDCLine(ComObject powerworld, Bus fromBus, Bus toBus, Triple<Integer,Integer,String> legacyid)  {    
    DCTwoTerminalLine line = registerTwoTerminalLine(legacyid);   
    
    String fields[] = new String[]{PowerworldIOConstants.TWO_TERMINAL_BUS_FROM_NUM, PowerworldIOConstants.TWO_TERMINAL_BUS_TO_NUM, PowerworldIOConstants.TWO_TERMINAL_NUM,
        PowerworldIOConstants.TWO_TERMINAL_INVERTER_TAP,         PowerworldIOConstants.TWO_TERMINAL_INVERTER_MIN_TAP,        PowerworldIOConstants.TWO_TERMINAL_INVERTER_MAX_TAP,
        PowerworldIOConstants.TWO_TERMINAL_INVERTER_TAP_RATIO,   PowerworldIOConstants.TWO_TERMINAL_RECTIFIER_REACTIVE_FLOW, PowerworldIOConstants.TWO_TERMINAL_INVERTER_REACTIVE_FLOW,
        PowerworldIOConstants.TWO_TERMINAL_RECTIFIER_REAL_FLOW,  PowerworldIOConstants.TWO_TERMINAL_INVERTER_REAL_FLOW,      PowerworldIOConstants.TWO_TERMINAL_CONTROL_MODE,
        PowerworldIOConstants.TWO_TERMINAL_RECTIFIER_TAP,        PowerworldIOConstants.TWO_TERMINAL_RECTIFIER_TAP_MIN,       PowerworldIOConstants.TWO_TERMINAL_RECTIFIER_TAP_MAX, 
        PowerworldIOConstants.TWO_TERMINAL_RECTIFIER_TAP_RATIO,  PowerworldIOConstants.TWO_TERMINAL_DC_VOLTAGE,              PowerworldIOConstants.TWO_TERMINAL_NAME, 
        PowerworldIOConstants.TWO_TERMINAL_DC_RECTIFIER_VOLTAGE, PowerworldIOConstants.TWO_TERMINAL_DC_INVERTER_VOLTAGE,     PowerworldIOConstants.TWO_TERMINAL_STATUS,
        PowerworldIOConstants.TWO_TERMINAL_RESISTANCE};
        
    String values[] = new String[] {legacyid.getOne()+"", legacyid.getTwo()+"", legacyid.getThree()+"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""};
        
    ComDataObject dataObject = powerworld.callData(PowerworldIOConstants.GET_PARAMETERS_SINGLE_ELEMENT, PowerworldIOConstants.DC_TWO_TERMINAL, fields, values);
    ArrayList<ComDataObject> branchData = dataObject.getArrayValue();
    String errorString = branchData.get(0).getStringValue();
    if (!errorString.equals("")) {
      System.err.println("Error getting powerworld two terminal data: " + errorString);                
    }
        
    ArrayList<ComDataObject> bData = branchData.get(1).getArrayValue();                       
    String inverterTapStr = bData.get(3).getStringValue();
    String inverterMinStr = bData.get(4).getStringValue();
    String inverterMaxStr = bData.get(5).getStringValue();
    String inverterTapRatioStr = bData.get(6).getStringValue();
    String rectifierReactiveFlowStr = bData.get(7).getStringValue();
    String inverterReactiveFlowStr = bData.get(8).getStringValue();
    String rectifierRealFlowStr = bData.get(9).getStringValue();
    String inverterRealFlowStr = bData.get(10).getStringValue();
    String controlModeStr = bData.get(11).getStringValue();
    String rectifierTapStr = bData.get(12).getStringValue();
    String rectifierTapMinStr = bData.get(13).getStringValue();
    String rectifierTapMaxStr = bData.get(14).getStringValue();    
    String rectifierTapRatioStr = bData.get(15).getStringValue();
    String dCVoltageStr = bData.get(16).getStringValue();
    String name = bData.get(17).getStringValue();
    String rectifierDCVoltageStr = bData.get(18).getStringValue();
    String inverterDCVoltageStr = bData.get(19).getStringValue();
    String statusStr = bData.get(20).getStringValue();
    String resistanceStr = bData.get(21).getStringValue();
           
    double inverterTap = Double.parseDouble(inverterTapStr);
    double inverterMin = Double.parseDouble(inverterMinStr);
    double inverterMax = Double.parseDouble(inverterMaxStr);
    double inverterTapRatio = Double.parseDouble(inverterTapRatioStr);
    double rectifierReactiveFlow = Double.parseDouble(rectifierReactiveFlowStr);
    double inverterReactiveFlow = Double.parseDouble(inverterReactiveFlowStr);
    double rectifierRealFlow = Double.parseDouble(rectifierRealFlowStr);
    double inverterRealFlow = Double.parseDouble(inverterRealFlowStr);
    DCTwoTerminalLineControlModeEnum controlMode = controlModeStr.equalsIgnoreCase(PowerworldIOConstants.TWO_TERMINAL_CONTROL_MW) ? DCTwoTerminalLineControlModeEnum.CONTROL_IS_MW : DCTwoTerminalLineControlModeEnum.CONTROL_IS_AMP;
    double rectifierTap = Double.parseDouble(rectifierTapStr);
    double rectifierTapMin = Double.parseDouble(rectifierTapMinStr);
    double rectifierTapMax = Double.parseDouble(rectifierTapMaxStr);
    Double rectifierTapRatio = rectifierTapRatioStr != null ? Double.parseDouble(rectifierTapRatioStr) : null;
    double rectifierDCVoltage = Double.parseDouble(rectifierDCVoltageStr);
    double inverterDCVoltage = Double.parseDouble(inverterDCVoltageStr);
    double dCVoltage = Double.parseDouble(dCVoltageStr);    
    boolean status = statusStr.equalsIgnoreCase(PowerworldIOConstants.TWO_TERMINAL_CLOSED);
    double mwFlow = Math.max(inverterReactiveFlow, rectifierReactiveFlow);
    double mVarFlow = Math.max(inverterRealFlow, rectifierRealFlow);
    double resistance = Double.parseDouble(resistanceStr);
    
    line.setDesiredStatus(status);
    line.setActualStatus(status);
    line.setAttribute(DCTwoTerminalLine.INVERTER_TAP_KEY, inverterTap);
    line.setAttribute(DCTwoTerminalLine.INVERTER_MIN_TAP_KEY, inverterMin);
    line.setAttribute(DCTwoTerminalLine.INVERTER_MAX_TAP_KEY, inverterMax);
    line.setAttribute(DCTwoTerminalLine.INVERTER_TAP_RATIO_KEY, inverterTapRatio);    
    line.setMWFlow(mwFlow);
    line.setMVarFlow(mVarFlow);
    line.setAttribute(DCTwoTerminalLine.MVAR_FLOW_SIDE1_KEY, inverterReactiveFlow);
    line.setAttribute(DCTwoTerminalLine.MVAR_FLOW_SIDE2_KEY, rectifierReactiveFlow);
    line.setAttribute(DCTwoTerminalLine.MW_FLOW_SIDE1_KEY, inverterRealFlow);
    line.setAttribute(DCTwoTerminalLine.MW_FLOW_SIDE2_KEY, rectifierRealFlow);
    line.setAttribute(DCTwoTerminalLine.CONTROL_MODE_KEY, controlMode);    
    line.setAttribute(DCTwoTerminalLine.RECTIFIER_TAP_KEY, rectifierTap);
    line.setAttribute(DCTwoTerminalLine.RECTIFIER_MIN_TAP_KEY, rectifierTapMin);
    line.setAttribute(DCTwoTerminalLine.RECTIFIER_MAX_TAP_KEY, rectifierTapMax);
    line.setAttribute(DCTwoTerminalLine.RECTIFIER_TAP_RATIO_KEY, rectifierTapRatio);    
    line.setAttribute(DCTwoTerminalLine.DC_VOLTAGE_KEY, dCVoltage);    
    line.setAttribute(DCTwoTerminalLine.DC_INVERTER_VOLTAGE_KEY, inverterDCVoltage);    
    line.setAttribute(DCTwoTerminalLine.DC_RECTIFIER_VOLTAGE_KEY, rectifierDCVoltage);
    line.setAttribute(DCTwoTerminalLine.NAME_KEY, name);
    line.setResistance(resistance);
    
    Vector<Point> points = new Vector<Point>();
    points.add(fromBus.getCoordinate());
    points.add(toBus.getCoordinate());
    line.setCoordinates(new LineImpl(points));    
    return line;
  }
  
  
  
  
  
  
  
  /**
   * Creates a two terminal dc line and data
   * @param line
   * @return
   */
  public DCMultiTerminalLine createMultiTerminalDCLine(ComObject powerworld, Bus fromBus, Bus toBus, Triple<Integer,Integer,String> legacyid)  {    
    DCMultiTerminalLine line = registerMultiTerminalLine(legacyid);   
    
    String fields[] = new String[]{PowerworldIOConstants.MULTI_TERMINAL_BUS_FROM_NUM, PowerworldIOConstants.MULTI_TERMINAL_BUS_TO_NUM, PowerworldIOConstants.MULTI_TERMINAL_NUM,
        PowerworldIOConstants.MULTI_TERMINAL_BASE_WINDING, PowerworldIOConstants.MULTI_TERMINAL_FIRING_ANGLE,
        PowerworldIOConstants.MULTI_TERMINAL_FIRING_ANGLE_MAX, PowerworldIOConstants.MULTI_TERMINAL_FIRING_ANGLE_MIN,
        PowerworldIOConstants.MULTI_TERMINAL_CURRENT_ANGLE, PowerworldIOConstants.MULTI_TERMINAL_MW,
        PowerworldIOConstants.MULTI_TERMINAL_MVAR, PowerworldIOConstants.MULTI_TERMINAL_SETPOINT,
        PowerworldIOConstants.MULTI_TERMINAL_STATUS, PowerworldIOConstants.MULTI_TERMINAL_TAP,
        PowerworldIOConstants.MULTI_TERMINAL_TAP_MAX, PowerworldIOConstants.MULTI_TERMINAL_TAP_MIN,
        PowerworldIOConstants.MULTI_TERMINAL_TAP_STEP_SIZE, PowerworldIOConstants. MULTI_TERMINAL_TYPE,
        PowerworldIOConstants.MULTI_TERMINAL_TRANSFORMER_TAP, PowerworldIOConstants.MULTI_TERMINAL_CURRENT_RATING,
        PowerworldIOConstants.MULTI_TERMINAL_DC_VOLTAGE };
                
    String values[] = new String[] {legacyid.getOne()+"", legacyid.getTwo()+"", legacyid.getThree()+"", "","","","","","","","","","","","","","","","",""};
        
    ComDataObject dataObject = powerworld.callData(PowerworldIOConstants.GET_PARAMETERS_SINGLE_ELEMENT, PowerworldIOConstants.DC_MULTI_TERMINAL, fields, values);
    ArrayList<ComDataObject> branchData = dataObject.getArrayValue();
    String errorString = branchData.get(0).getStringValue();
    if (!errorString.equals("")) {
      System.err.println("Error getting powerworld multi terminal data: " + errorString);                
    }
        
    ArrayList<ComDataObject> bData = branchData.get(1).getArrayValue();                       
    
    String baseWindingStr    = bData.get(3).getStringValue();
    String firingAngleStr    = bData.get(4).getStringValue();
    String firingAngleMaxStr = bData.get(5).getStringValue();
    String firingAngleMinStr = bData.get(6).getStringValue();
    String currentAngleStr   = bData.get(7).getStringValue();
    String mwStr             = bData.get(8).getStringValue();
    String mvarStr           = bData.get(9).getStringValue();
    String setPointStr       = bData.get(10).getStringValue();
    String statusStr         = bData.get(11).getStringValue();
    String tapStr            = bData.get(12).getStringValue();
    String tapMaxStr         = bData.get(13).getStringValue();
    String tapMinStr         = bData.get(14).getStringValue();
    String tapStepSizeStr    = bData.get(15).getStringValue();
    String terminalTypeStr   = bData.get(16).getStringValue();
    String transformerTapStr = bData.get(17).getStringValue();
    String currentRatingStr  =  bData.get(18).getStringValue();
    String dcVoltageStr      =  bData.get(19).getStringValue();
    
    double baseWinding    = Double.parseDouble(baseWindingStr);
    double firingAngle    = Double.parseDouble(firingAngleStr);
    double firingAngleMax = Double.parseDouble(firingAngleMaxStr);
    double firingAngleMin = Double.parseDouble(firingAngleMinStr);
    double currentAngle   = Double.parseDouble(currentAngleStr);
    double mw             = Double.parseDouble(mwStr);
    double mvar           = Double.parseDouble(mvarStr);
    double setPoint       = Double.parseDouble(setPointStr);
    boolean status        = statusStr.equalsIgnoreCase(PowerworldIOConstants.MULTI_TERMINAL_CLOSED);
    double tap            = Double.parseDouble(tapStr);
    double tapMax         = Double.parseDouble(tapMaxStr);
    double tapMin         = Double.parseDouble(tapMinStr);
    double tapStepSize    = Double.parseDouble(tapStepSizeStr);
    double transformerTap = Double.parseDouble(transformerTapStr);
    double currentRating  = Double.parseDouble(currentRatingStr);
    double dcVoltage      = Double.parseDouble(dcVoltageStr);
    DCMultiTerminalLineTerminalTypeEnum terminalType = terminalTypeStr.equalsIgnoreCase(PowerworldIOConstants.MULTI_TERMINAL_TYPE_RECT) ?  DCMultiTerminalLineTerminalTypeEnum.TYPE_IS_RECT : DCMultiTerminalLineTerminalTypeEnum.TYPE_IS_INV;

    line.setDesiredStatus(status);
    line.setActualStatus(status);
    line.setAttribute(DCMultiTerminalLine.BASE_WINDING_KEY, baseWinding);
    line.setAttribute(DCMultiTerminalLine.FIRING_ANGLE_KEY, firingAngle);
    line.setAttribute(DCMultiTerminalLine.FIRING_ANGLE_MAX_KEY, firingAngleMax);
    line.setAttribute(DCMultiTerminalLine.FIRING_ANGLE_MIN_KEY, firingAngleMin);
    line.setAttribute(DCMultiTerminalLine.CURRENT_ANGLE_KEY, currentAngle);
    line.setMWFlow(mw);
    line.setMVarFlow(mvar);
    line.setAttribute(DCMultiTerminalLine.SETPOINT_KEY, setPoint);
    line.setAttribute(DCMultiTerminalLine.TAP_KEY, tap);
    line.setAttribute(DCMultiTerminalLine.TAP_MAX_KEY, tapMax);
    line.setAttribute(DCMultiTerminalLine.TAP_MIN_KEY, tapMin);
    line.setAttribute(DCMultiTerminalLine.TAP_STEP_SIZE_KEY, tapStepSize);
    line.setAttribute(DCMultiTerminalLine.TRANSFORMER_TAP_KEY, transformerTap);
    line.setAttribute(DCMultiTerminalLine.CURRENT_RATING_KEY, currentRating);
    line.setAttribute(DCMultiTerminalLine.DC_VOLTAGE_KEY, dcVoltage);
    line.setAttribute(DCMultiTerminalLine.TYPE_KEY, terminalType);
    
    
    Vector<Point> points = new Vector<Point>();
    points.add(fromBus.getCoordinate());
    points.add(toBus.getCoordinate());
    line.setCoordinates(new LineImpl(points));    
    return line;
  }
  
  
 
  /**
   * Creates a voltage source dc line and data
   * @param line
   * @return
   */
  public DCVoltageSourceLine createVoltageSourceDCLine(ComObject powerworld, Bus fromBus, Bus toBus, String id)  {
    Triple<Integer,Integer,String> legacyId = new Triple<Integer,Integer,String>(Integer.parseInt(fromBus.toString()),Integer.parseInt(toBus.toString()),id);
    
    DCVoltageSourceLine line = registerVoltageSourceLine(legacyId);   
    
    String fields[] = new String[]{ PowerworldIOConstants.VOLTAGE_SOURCE_NAME,  
        PowerworldIOConstants.VOLTAGE_SOURCE_FROM_AC_CONTROL_MODE, PowerworldIOConstants.VOLTAGE_SOURCE_TO_AC_CONTROL_MODE,
        PowerworldIOConstants.VOLTAGE_SOURCE_FROM_AC_SET_POINT, PowerworldIOConstants.VOLTAGE_SOURCE_TO_AC_SET_POINT,
        PowerworldIOConstants.VOLTAGE_SOURCE_FROM_MW_INPUT, PowerworldIOConstants.VOLTAGE_SOURCE_TO_MW_INPUT,
        PowerworldIOConstants.VOLTAGE_SOURCE_RESISTANCE, PowerworldIOConstants.VOLTAGE_SOURCE_FROM_DC_CONTROL_MODE,
        PowerworldIOConstants.VOLTAGE_SOURCE_TO_DC_CONTROL_MODE, PowerworldIOConstants.VOLTAGE_SOURCE_FROM_DC_SET_POINT,
        PowerworldIOConstants.VOLTAGE_SOURCE_TO_DC_SET_POINT,
        PowerworldIOConstants.VOLTAGE_SOURCE_FROM_MAX_MVAR, PowerworldIOConstants.VOLTAGE_SOURCE_TO_MAX_MVAR,
        PowerworldIOConstants.VOLTAGE_SOURCE_FROM_MIN_MVAR, PowerworldIOConstants.VOLTAGE_SOURCE_TO_MIN_MVAR,
        PowerworldIOConstants.VOLTAGE_SOURCE_FROM_CURRENT_MAX, PowerworldIOConstants.VOLTAGE_SOURCE_TO_CURRENT_MAX,
        PowerworldIOConstants.VOLTAGE_SOURCE_FROM_FLOW_MVAR, PowerworldIOConstants.VOLTAGE_SOURCE_TO_FLOW_MVAR,
        PowerworldIOConstants.VOLTAGE_SOURCE_FROM_FLOW_MW, PowerworldIOConstants.VOLTAGE_SOURCE_TO_FLOW_MW, 
        PowerworldIOConstants.VOLTAGE_SOURCE_STATUS, PowerworldIOConstants.VOLTAGE_SOURCE_FROM_DC_VOLTAGE,
        PowerworldIOConstants.VOLTAGE_SOURCE_TO_DC_VOLTAGE, PowerworldIOConstants.VOLTAGE_SOURCE_FROM_MVA_RATING,
        PowerworldIOConstants.VOLTAGE_SOURCE_TO_MVA_RATING };
        
    String values[] = new String[] {id, "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""};
        
    ComDataObject dataObject = powerworld.callData(PowerworldIOConstants.GET_PARAMETERS_SINGLE_ELEMENT, PowerworldIOConstants.DC_VOLTAGE_SOURCE, fields, values);
    ArrayList<ComDataObject> branchData = dataObject.getArrayValue();
    String errorString = branchData.get(0).getStringValue();
    if (!errorString.equals("")) {
      System.err.println("Error getting powerworld voltage source data: " + errorString);                
    }
        
    ArrayList<ComDataObject> bData = branchData.get(1).getArrayValue();                       
    String fromACControlModeStr = bData.get(1).getStringValue();
    String toACControlModeStr = bData.get(2).getStringValue();
    String fromACSetPointStr = bData.get(3).getStringValue();
    String toACSetPointStr = bData.get(4).getStringValue();
    String fromMWInputStr = bData.get(5).getStringValue();
    String toMWInputStr = bData.get(6).getStringValue();
    String resistanceStr = bData.get(7).getStringValue();
    String fromDCControlModeStr = bData.get(8).getStringValue();
    String toDCControlModeStr = bData.get(9).getStringValue();
    String fromDCSetPointStr = bData.get(10).getStringValue();
    String toDCSetPointStr = bData.get(11).getStringValue();
    String fromMaxMVARStr = bData.get(12).getStringValue();
    String toMaxMVARStr = bData.get(13).getStringValue();
    String fromMinMVARStr = bData.get(14).getStringValue();
    String toMinMVARStr = bData.get(15).getStringValue();
    String fromCurrentStr = bData.get(16).getStringValue();
    String toCurrentStr = bData.get(17).getStringValue();
    String fromFlowMVARStr = bData.get(18).getStringValue();
    String toFlowMVARStr = bData.get(19).getStringValue();
    String fromFlowMWStr = bData.get(20).getStringValue();
    String toFlowMWStr = bData.get(21).getStringValue();
    String statusStr = bData.get(22).getStringValue();
    String fromDCVoltageStr = bData.get(23).getStringValue();
    String toDCVoltageStr = bData.get(24).getStringValue();
    String fromMVARatingStr = bData.get(23).getStringValue();
    String toMVARatingStr = bData.get(24).getStringValue();

    boolean status = statusStr.equalsIgnoreCase(PowerworldIOConstants.VOLTAGE_SOURCE_CLOSED);
    DCVoltageSourceLineACControlModeEnum fromACControlMode = fromACControlModeStr.equals(PowerworldIOConstants.VOLTAGE_SOURCE_AC_CONTROL_MODE_VOLTAGE) ? DCVoltageSourceLineACControlModeEnum.CONTROL_IS_VOLTAGE : DCVoltageSourceLineACControlModeEnum.CONTROL_IS_POWER_FACTOR;
    DCVoltageSourceLineACControlModeEnum toACControlMode = toACControlModeStr.equals(PowerworldIOConstants.VOLTAGE_SOURCE_AC_CONTROL_MODE_VOLTAGE) ? DCVoltageSourceLineACControlModeEnum.CONTROL_IS_VOLTAGE : DCVoltageSourceLineACControlModeEnum.CONTROL_IS_POWER_FACTOR;
    double fromACSetPoint = Double.parseDouble(fromACSetPointStr);
    double toACSetPoint = Double.parseDouble(toACSetPointStr);
    double fromMWInput = Double.parseDouble(fromMWInputStr);
    double toMWInput = Double.parseDouble(toMWInputStr);
    double resistance = Double.parseDouble(resistanceStr);
    DCVoltageSourceLineDCControlModeEnum fromDCControlMode = fromDCControlModeStr.equals(PowerworldIOConstants.VOLTAGE_SOURCE_DC_CONTROL_MODE_OUT_OF_SERVICE) ? DCVoltageSourceLineDCControlModeEnum.CONTROL_IS_OUT_OF_SERVICE :  fromDCControlModeStr.equals(PowerworldIOConstants.VOLTAGE_SOURCE_DC_CONTROL_MODE_VOLTAGE) ? DCVoltageSourceLineDCControlModeEnum.CONTROL_IS_VOLTAGE : DCVoltageSourceLineDCControlModeEnum.CONTROL_IS_POWER;
    DCVoltageSourceLineDCControlModeEnum toDCControlMode = toDCControlModeStr.equals(PowerworldIOConstants.VOLTAGE_SOURCE_DC_CONTROL_MODE_OUT_OF_SERVICE) ? DCVoltageSourceLineDCControlModeEnum.CONTROL_IS_OUT_OF_SERVICE :  toDCControlModeStr.equals(PowerworldIOConstants.VOLTAGE_SOURCE_DC_CONTROL_MODE_VOLTAGE) ? DCVoltageSourceLineDCControlModeEnum.CONTROL_IS_VOLTAGE : DCVoltageSourceLineDCControlModeEnum.CONTROL_IS_POWER;
    double fromDCSetPoint = Double.parseDouble(fromDCSetPointStr);
    double toDCSetPoint = Double.parseDouble(toDCSetPointStr);
    double fromMaxMVAR = Double.parseDouble(fromMaxMVARStr);
    double toMaxMVAR = Double.parseDouble(toMaxMVARStr);
    double fromMinMVAR = Double.parseDouble(fromMinMVARStr);
    double toMinMVAR = Double.parseDouble(toMinMVARStr);
    double fromCurrent = Double.parseDouble(fromCurrentStr);
    double toCurrent = Double.parseDouble(toCurrentStr);
    double fromFlowMVAR = Double.parseDouble(fromFlowMVARStr);
    double toFlowMVAR = Double.parseDouble(toFlowMVARStr);
    double fromFlowMW = Double.parseDouble(fromFlowMWStr);
    double toFlowMW = Double.parseDouble(toFlowMWStr);
    double fromDCVoltage = Double.parseDouble(fromDCVoltageStr);
    double toDCVoltage = Double.parseDouble(toDCVoltageStr);
    double fromMVARating = Double.parseDouble(fromMVARatingStr);
    double toMVARating = Double.parseDouble(toMVARatingStr);
    
    line.setDesiredStatus(status);
    line.setActualStatus(status);
    line.setAttribute(DCVoltageSourceLine.FROM_AC_CONTROL_MODE_KEY, fromACControlMode);
    line.setAttribute(DCVoltageSourceLine.TO_AC_CONTROL_MODE_KEY, toACControlMode);
    line.setAttribute(DCVoltageSourceLine.FROM_DC_CONTROL_MODE_KEY, fromDCControlMode);
    line.setAttribute(DCVoltageSourceLine.TO_DC_CONTROL_MODE_KEY, toDCControlMode);
    line.setAttribute(DCVoltageSourceLine.FROM_AC_SET_POINT_KEY, fromACSetPoint);
    line.setAttribute(DCVoltageSourceLine.TO_AC_SET_POINT_KEY, toACSetPoint);
    line.setAttribute(DCVoltageSourceLine.FROM_MW_INPUT_KEY, fromMWInput);
    line.setAttribute(DCVoltageSourceLine.TO_MW_INPUT_KEY, toMWInput);
    line.setResistance(resistance);
    line.setAttribute(DCVoltageSourceLine.FROM_DC_SET_POINT_KEY, fromDCSetPoint);
    line.setAttribute(DCVoltageSourceLine.TO_DC_SET_POINT_KEY, toDCSetPoint);
    line.setAttribute(DCVoltageSourceLine.FROM_MAX_MVAR_KEY, fromMaxMVAR);
    line.setAttribute(DCVoltageSourceLine.TO_MAX_MVAR_KEY, toMaxMVAR);
    line.setAttribute(DCVoltageSourceLine.FROM_MIN_MVAR_KEY, fromMinMVAR);
    line.setAttribute(DCVoltageSourceLine.TO_MIN_MVAR_KEY, toMinMVAR);
    line.setAttribute(DCVoltageSourceLine.FROM_MAX_CURRENT_KEY, fromCurrent);
    line.setAttribute(DCVoltageSourceLine.TO_MAX_CURRENT_KEY, toCurrent);
    line.setAttribute(DCVoltageSourceLine.MVAR_FLOW_SIDE1_KEY, fromFlowMVAR);
    line.setAttribute(DCVoltageSourceLine.MVAR_FLOW_SIDE2_KEY, toFlowMVAR);
    line.setAttribute(DCVoltageSourceLine.MW_FLOW_SIDE1_KEY, fromFlowMW);
    line.setAttribute(DCVoltageSourceLine.MW_FLOW_SIDE2_KEY, toFlowMW);
    line.setAttribute(DCVoltageSourceLine.FROM_DC_VOLTAGE_KEY, fromDCVoltage);
    line.setAttribute(DCVoltageSourceLine.TO_DC_VOLTAGE_KEY, toDCVoltage);
    line.setAttribute(DCVoltageSourceLine.FROM_MVA_RATING_KEY, fromMVARating);
    line.setAttribute(DCVoltageSourceLine.TO_MVA_RATING_KEY, toMVARating);
    line.setAttribute(DCVoltageSourceLine.NAME_KEY, id);

    Vector<Point> points = new Vector<Point>();
    points.add(fromBus.getCoordinate());
    points.add(toBus.getCoordinate());
    line.setCoordinates(new LineImpl(points));    
    return line;
  }

  
  
  
  
  
  
  
  
  
  
  
  
  
  

}
