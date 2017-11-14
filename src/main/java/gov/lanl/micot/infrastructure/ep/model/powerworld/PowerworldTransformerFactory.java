package gov.lanl.micot.infrastructure.ep.model.powerworld;

import java.util.ArrayList;
import java.util.Vector;

import gov.lanl.micot.infrastructure.ep.io.powerworld.PowerworldIOConstants;
import gov.lanl.micot.infrastructure.ep.model.Bus;
import gov.lanl.micot.infrastructure.ep.model.Transformer;
import gov.lanl.micot.infrastructure.ep.model.TransformerFactory;
import gov.lanl.micot.infrastructure.ep.model.TransformerTypeEnum;
import gov.lanl.micot.util.collection.Triple;
import gov.lanl.micot.util.geometry.LineImpl;
import gov.lanl.micot.util.geometry.Point;
import gov.lanl.micot.util.io.dcom.ComDataObject;
import gov.lanl.micot.util.io.dcom.ComObject;

/**
 * Factory for creating power world transformers
 * @author Russell Bent
 */
public class PowerworldTransformerFactory extends TransformerFactory {

  private static final String LEGACY_TAG = "POWERWORLD";

  /**
   * Singleton constructor
   */
  protected PowerworldTransformerFactory() {    
  }
  
  @Override
  protected Transformer createEmptyTransformer(Bus bus1, Bus bus2, Object circuit) {
    Transformer transformer = registerTransformer(new Triple<Integer,Integer,String>(bus1.getAttribute(PowerworldModelConstants.POWERWORLD_LEGACY_ID_KEY, Integer.class), bus2.getAttribute(PowerworldModelConstants.POWERWORLD_LEGACY_ID_KEY, Integer.class), findUnusedId()));   
    return transformer;
  }

  /**
   * Find an unused id number
   * @return
   */
  public String findUnusedId() {
    for (int i = 0; i < Integer.MAX_VALUE; ++i) {
      if (!doesLegacyExist(LEGACY_TAG,i+"")) {
        return i+"";
      }
    }
    throw new RuntimeException("Error: Ran out of powerworld ids");    
  }
	
	/**
   * Creates a transformer and data from a transformer
   * @param line
   * @return
   */
  public Transformer createTransformer(ComObject powerworld, Bus fromBus, Bus toBus, Triple<Integer,Integer,String> legacyid)  {    
    Transformer line = registerTransformer(legacyid);   
    
    String fields[] = new String[]{PowerworldIOConstants.BRANCH_BUS_FROM_NUM, PowerworldIOConstants.BRANCH_BUS_TO_NUM, PowerworldIOConstants.BRANCH_NUM, 
        PowerworldIOConstants.BRANCH_RESISTANCE, PowerworldIOConstants.BRANCH_REACTANCE, PowerworldIOConstants.BRANCH_CHARGING, PowerworldIOConstants.BRANCH_THERMAL_LIMIT_A, 
        PowerworldIOConstants.BRANCH_THERMAL_LIMIT_B, PowerworldIOConstants.BRANCH_THERMAL_LIMIT_C, PowerworldIOConstants.BRANCH_STATUS, PowerworldIOConstants.BRANCH_LENGTH,
        PowerworldIOConstants.BRANCH_REACTIVE_LOSS, PowerworldIOConstants.BRANCH_REAL_LOSS, PowerworldIOConstants.BRANCH_FROM_REACTIVE_FLOW, PowerworldIOConstants.BRANCH_TO_REACTIVE_FLOW, 
        PowerworldIOConstants.BRANCH_FROM_REAL_FLOW, PowerworldIOConstants.BRANCH_TO_REAL_FLOW, PowerworldIOConstants.BRANCH_TAP_RATIO, PowerworldIOConstants.BRANCH_TAP_PHASE,
        PowerworldIOConstants.BRANCH_MAX_TAP_RATIO, PowerworldIOConstants.BRANCH_MIN_TAP_RATIO, PowerworldIOConstants.BRANCH_TAP_RATIO_STEPS, PowerworldIOConstants.BRANCH_TRANSFORMER_TYPE,
        PowerworldIOConstants.BRANCH_REGULATED_MAX, PowerworldIOConstants.BRANCH_REGULATED_MIN}; 

    String values[] = new String[] {legacyid.getOne()+"", legacyid.getTwo()+"", legacyid.getThree()+"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "","","", "",""};
        
    ComDataObject dataObject = powerworld.callData(PowerworldIOConstants.GET_PARAMETERS_SINGLE_ELEMENT, PowerworldIOConstants.BRANCH, fields, values);
    ArrayList<ComDataObject> branchData = dataObject.getArrayValue();
    String errorString = branchData.get(0).getStringValue();
    if (!errorString.equals("")) {
      System.err.println("Error getting powerworld branch data: " + errorString);                
    }
        
    ArrayList<ComDataObject> bData = branchData.get(1).getArrayValue();                       
    String resistanceStr = bData.get(3).getStringValue();
    String reactanceStr = bData.get(4).getStringValue();
    String chargingStr = bData.get(5).getStringValue();
    String rating1Str = bData.get(6).getStringValue();
    String rating2Str = bData.get(7).getStringValue();
    String rating3Str = bData.get(8).getStringValue();
    String statusStr = bData.get(9).getStringValue();
    String lengthStr = bData.get(10).getStringValue();
    String reactiveLossStr = bData.get(11).getStringValue();
    String realLossStr = bData.get(12).getStringValue();
    String reactiveFromStr = bData.get(13).getStringValue();
    String reactiveToStr = bData.get(14).getStringValue();    
    String realFromStr = bData.get(15).getStringValue();
    String realToStr = bData.get(16).getStringValue();
    String ratioStr = bData.get(17).getStringValue();
    String phaseStr = bData.get(18).getStringValue();
    String maxTapStr = bData.get(19).getStringValue();
    String minTapStr = bData.get(20).getStringValue();
    String stepsStr = bData.get(21).getStringValue();
    String typeStr = bData.get(22).getStringValue();
    String regulatedMaxStr = bData.get(23).getStringValue();
    String regulatedMinStr = bData.get(24).getStringValue();
    
           
    double resistance = Double.parseDouble(resistanceStr);
    double reactance = Double.parseDouble(reactanceStr);
    double charging = Double.parseDouble(chargingStr);
    double rating1 = Double.parseDouble(rating1Str);
    double rating2 = Double.parseDouble(rating2Str);
    double rating3 = Double.parseDouble(rating3Str);
    boolean status = statusStr.equalsIgnoreCase(PowerworldIOConstants.BRANCH_CLOSED);
    double length = Double.parseDouble(lengthStr);
    double reactiveLoss = Double.parseDouble(reactiveLossStr);
    double realLoss = Double.parseDouble(realLossStr);
    double reactiveFrom = Double.parseDouble(reactiveFromStr);
    double reactiveTo = Double.parseDouble(reactiveToStr);
    double realFrom = Double.parseDouble(realFromStr);
    double realTo = Double.parseDouble(realToStr);
    double mwFlow = Math.max(realFrom, realTo);
    double mVarFlow = Math.max(reactiveFrom, reactiveTo);
    double ratio = Double.parseDouble(ratioStr);
    double phase = Double.parseDouble(phaseStr);
    double maxTapAngle = Double.parseDouble(maxTapStr);
    double minTapAngle = Double.parseDouble(minTapStr);
    double maxTapRatio = Double.parseDouble(maxTapStr);
    double minTapRatio = Double.parseDouble(minTapStr);
    double regularMax = regulatedMaxStr != null ? Double.parseDouble(regulatedMaxStr) : 2.0; 
    double regularMin = regulatedMinStr != null ? Double.parseDouble(regulatedMinStr) : 0.0; 
    
    double steps = Double.parseDouble(stepsStr);
    TransformerTypeEnum type = TransformerTypeEnum.TRANSMISSION_FIXED_ANGLE_FIXED_TAP_TYPE;
    if (typeStr.equalsIgnoreCase(PowerworldIOConstants.BRANCH_TRANSFORMER_LTC)) {
      type = TransformerTypeEnum.TRANSMISSION_FIXED_ANGLE_VARIABLE_TAP_VOLTAGE_CONTROL_TYPE;
    }
    if (typeStr.equalsIgnoreCase(PowerworldIOConstants.BRANCH_TRANSFORMER_MVAR)) {
      type = TransformerTypeEnum.TRANSMISSION_FIXED_ANGLE_VARIABLE_TAP_REACTIVE_FLOW_CONTROL_TYPE;
    }
    if (typeStr.equalsIgnoreCase(PowerworldIOConstants.BRANCH_TRANSFORMER_PHASE)) {
      type = TransformerTypeEnum.TRANSMISSION_FIXED_ANGLE_VARIABLE_TAP_REAL_FLOW_CONTROL_TYPE;
    }
        
    if (rating1 <= 0) {
      System.err.println("Warning: Transformer rating 1 of " + line + " + is " + rating1 + ". Changing to " + PowerworldModelConstants.DEFAULT_MAX_RATING);
      rating1 = PowerworldModelConstants.DEFAULT_MAX_RATING;
    }
    if (rating2 <= 0) {
      System.err.println("Warning: Transformer rating 2 of " + line + " + is " + rating2 + ". Changing to " + PowerworldModelConstants.DEFAULT_MAX_RATING);
      rating2 = PowerworldModelConstants.DEFAULT_MAX_RATING;
    }
    if (rating3 <= 0) {
      System.err.println("Warning: Transformer rating 3 of " + line + " + is " + rating3 + ". Changing to " + PowerworldModelConstants.DEFAULT_MAX_RATING);
      rating3 = PowerworldModelConstants.DEFAULT_MAX_RATING;
    }
    
    line.setResistance(resistance);
    line.setReactance(reactance);
    line.setLineCharging(charging);
    line.setCapacityRating(rating1);
    line.setShortTermEmergencyCapacityRating(rating2);
    line.setLongTermEmergencyCapacityRating(rating3);
    line.setStatus(status);
    line.setMWFlow(mwFlow);
    line.setMVarFlow(mVarFlow);
    line.setAttribute(Transformer.LENGTH_KEY, length);
    line.setRealLoss(realLoss);
    line.setReactiveLoss(reactiveLoss);
    line.setAttribute(Transformer.MVAR_FLOW_SIDE1_KEY, reactiveFrom);
    line.setAttribute(Transformer.MVAR_FLOW_SIDE2_KEY, reactiveTo);
    line.setAttribute(Transformer.MW_FLOW_SIDE1_KEY, realFrom);
    line.setAttribute(Transformer.MW_FLOW_SIDE2_KEY, realTo);
    line.setAttribute(Transformer.STEP_SIZE_KEY, steps);

    if (typeStr.equalsIgnoreCase(PowerworldIOConstants.BRANCH_TRANSFORMER_PHASE)) {
      ratio = 1.0;
      minTapRatio = 1.0;
      maxTapRatio = 1.0;      
    }
    else {
      phase = 0;
      maxTapAngle = 0;
      minTapAngle = 0;
    }
    phase = Math.toRadians(phase);
    maxTapAngle = Math.toRadians(phase);
    minTapAngle = Math.toRadians(phase);
    
    phase = 0; // turning off phase shifters for now.
    ratio = 1.0;
    
    //System.out.println(ratio);
    
    
    line.setAttribute(Transformer.TAP_RATIO_KEY, ratio);
    line.setAttribute(Transformer.TAP_ANGLE_KEY, phase);
    line.setAttribute(Transformer.TYPE_KEY, type);
    line.setAttribute(Transformer.MAX_TAP_ANGLE_KEY, maxTapAngle);
    line.setAttribute(Transformer.MIN_TAP_ANGLE_KEY, minTapAngle);
    line.setAttribute(Transformer.MAX_TAP_RATIO_KEY, maxTapRatio);
    line.setAttribute(Transformer.MIN_TAP_RATIO_KEY, minTapRatio);
    line.setAttribute(Transformer.CONTROL_MAX_KEY, regularMax);
    line.setAttribute(Transformer.CONTROL_MIN_KEY, regularMin);
    
    Vector<Point> points = new Vector<Point>();
    points.add(fromBus.getCoordinate());
    points.add(toBus.getCoordinate());
    line.setCoordinates(new LineImpl(points));    
    return line;
  }
  
  /**
   * Register the transformer
   * @param legacyId
   * @param bus
   * @return
   */
  private Transformer registerTransformer(Triple<Integer,Integer,String> legacyId) {
    Transformer transformer = getLegacy(LEGACY_TAG, legacyId);
    if (transformer == null) {
      transformer = createNewTransformer();
      transformer.setAttribute(PowerworldModelConstants.POWERWORLD_LEGACY_ID_KEY,legacyId);
      transformer.addOutputKey(PowerworldModelConstants.POWERWORLD_LEGACY_ID_KEY);
      registerLegacy(LEGACY_TAG, legacyId, transformer);
    }
    return transformer;
  }


}
