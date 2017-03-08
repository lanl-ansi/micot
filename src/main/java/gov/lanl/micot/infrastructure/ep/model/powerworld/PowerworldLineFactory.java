package gov.lanl.micot.infrastructure.ep.model.powerworld;

import java.util.ArrayList;
import java.util.Vector;

import gov.lanl.micot.infrastructure.ep.io.powerworld.PowerworldIOConstants;
import gov.lanl.micot.infrastructure.ep.model.Bus;
import gov.lanl.micot.infrastructure.ep.model.Line;
import gov.lanl.micot.infrastructure.ep.model.LineFactory;
import gov.lanl.micot.infrastructure.ep.model.Transformer;
import gov.lanl.micot.util.collection.Triple;
import gov.lanl.micot.util.geometry.LineImpl;
import gov.lanl.micot.util.geometry.Point;
import gov.lanl.micot.util.io.dcom.ComDataObject;
import gov.lanl.micot.util.io.dcom.ComObject;

/**
 * A factory for generating lines
 * @author Russell Bent
 *
 */
public class PowerworldLineFactory extends LineFactory {

  private static final String LEGACY_TAG = "POWERWORLD";
	  
  
  /**
   * Singleton constructor
   */
  protected PowerworldLineFactory() {  
  }
  
  @Override
  protected Line createEmptyLine(Bus fromBus, Bus toBus, Transformer transformer) {
    Line line = registerLine(new Triple<Integer,Integer,String>(fromBus.getAttribute(PowerworldModelConstants.POWERWORLD_LEGACY_ID_KEY, Integer.class), toBus.getAttribute(PowerworldModelConstants.POWERWORLD_LEGACY_ID_KEY, Integer.class), findUnusedId()));   
    return line;
  }

  @Override
  protected Line createEmptyLine(Bus fromBus, Bus toBus, Object circuit) {
    Line line = registerLine(new Triple<Integer,Integer,String>(fromBus.getAttribute(PowerworldModelConstants.POWERWORLD_LEGACY_ID_KEY, Integer.class), toBus.getAttribute(PowerworldModelConstants.POWERWORLD_LEGACY_ID_KEY, Integer.class), findUnusedId()));   
    return line;
  }  
		
	/**
   * Creates a line and data from a line
   * @param line
   * @return
   */
  public Line createLine(ComObject powerworld, Bus fromBus, Bus toBus, Triple<Integer,Integer,String> legacyid)  {
    Line line = registerLine(legacyid);   
    
    String fields[] = new String[]{PowerworldIOConstants.BRANCH_BUS_FROM_NUM, PowerworldIOConstants.BRANCH_BUS_TO_NUM, PowerworldIOConstants.BRANCH_NUM, 
        PowerworldIOConstants.BRANCH_RESISTANCE, PowerworldIOConstants.BRANCH_REACTANCE, PowerworldIOConstants.BRANCH_CHARGING, PowerworldIOConstants.BRANCH_THERMAL_LIMIT_A, 
        PowerworldIOConstants.BRANCH_THERMAL_LIMIT_B, PowerworldIOConstants.BRANCH_THERMAL_LIMIT_C, PowerworldIOConstants.BRANCH_STATUS, PowerworldIOConstants.BRANCH_LENGTH,
        PowerworldIOConstants.BRANCH_REACTIVE_LOSS, PowerworldIOConstants.BRANCH_REAL_LOSS, PowerworldIOConstants.BRANCH_FROM_REACTIVE_FLOW, PowerworldIOConstants.BRANCH_TO_REACTIVE_FLOW, 
        PowerworldIOConstants.BRANCH_FROM_REAL_FLOW, PowerworldIOConstants.BRANCH_TO_REAL_FLOW}; 

    String values[] = new String[] {legacyid.getOne()+"", legacyid.getTwo()+"", legacyid.getThree()+"", "", "", "", "", "", "", "", "", "", "", "", "", "", ""};
        
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
    
    if (rating1 <= 0) {
      System.err.println("Warning: Line rating 1 of " + line + " + is " + rating1 + ". Changing to " + PowerworldModelConstants.DEFAULT_MAX_RATING);
      rating1 = PowerworldModelConstants.DEFAULT_MAX_RATING;
    }
    if (rating2 <= 0) {
      System.err.println("Warning: Line rating 2 of " + line + " + is " + rating2 + ". Changing to " + PowerworldModelConstants.DEFAULT_MAX_RATING);
      rating2 = PowerworldModelConstants.DEFAULT_MAX_RATING;
    }
    if (rating3 <= 0) {
      System.err.println("Warning: Line rating 3 of " + line + " + is " + rating3 + ". Changing to " + PowerworldModelConstants.DEFAULT_MAX_RATING);
      rating3 = PowerworldModelConstants.DEFAULT_MAX_RATING;
    }

    line.setResistance(resistance);
    line.setReactance(reactance);
    line.setLineCharging(charging);
    line.setCapacityRating(rating1);
    line.setShortTermEmergencyCapacityRating(rating2);
    line.setLongTermEmergencyCapacityRating(rating3);
    line.setDesiredStatus(status);
    line.setActualStatus(status);
    line.setMWFlow(mwFlow);
    line.setMVarFlow(mVarFlow);
    line.setAttribute(Line.LENGTH_KEY, length);
    line.setRealLoss(realLoss);
    line.setReactiveLoss(reactiveLoss);
    line.setAttribute(Line.MVAR_FLOW_SIDE1_KEY, reactiveFrom);
    line.setAttribute(Line.MVAR_FLOW_SIDE2_KEY, reactiveTo);
    line.setAttribute(Line.MW_FLOW_SIDE1_KEY, realFrom);
    line.setAttribute(Line.MW_FLOW_SIDE2_KEY, realTo);
        
    Vector<Point> points = new Vector<Point>();
    points.add(fromBus.getCoordinate());
    points.add(toBus.getCoordinate());
    line.setCoordinates(new LineImpl(points));    
    return line;
  }
  
  /**
   * Register the line
   * @param legacyId
   * @param bus
   * @return
   */
  private Line registerLine(Triple<Integer,Integer,String> legacyId) {
    Line line = getLegacy(LEGACY_TAG, legacyId);
    if (line == null) {
      line = createNewLine();
      line.setAttribute(PowerworldModelConstants.POWERWORLD_LEGACY_ID_KEY,legacyId);
      line.addOutputKey(PowerworldModelConstants.POWERWORLD_LEGACY_ID_KEY);
      registerLegacy(LEGACY_TAG, legacyId, line);
    }
    return line;
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
    throw new RuntimeException("Error: Ran out of OpenDSS ids");    
  }

  
}
