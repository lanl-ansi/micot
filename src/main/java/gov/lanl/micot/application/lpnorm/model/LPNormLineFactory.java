package gov.lanl.micot.application.lpnorm.model;

import gov.lanl.micot.infrastructure.ep.model.Bus;
import gov.lanl.micot.infrastructure.ep.model.Line;
import gov.lanl.micot.infrastructure.ep.model.LineFactory;
import gov.lanl.micot.infrastructure.ep.model.Transformer;
import gov.lanl.micot.application.lpnorm.io.LPNormIOConstants;
import gov.lanl.micot.application.rdt.algorithm.AlgorithmConstants;
import gov.lanl.micot.util.geometry.LineImpl;
import gov.lanl.micot.util.geometry.Point;
import gov.lanl.micot.util.io.json.JSONArray;
import gov.lanl.micot.util.io.json.JSONObject;

import java.util.Vector;

/**
 * Factory class for creating MatPowerLines and ensuring their uniqueness
 * @author Russell Bent
 */
public class LPNormLineFactory extends LineFactory {

  private static final String LEGACY_TAG = "LPNORM";
  
  private static final double DEFAULT_CAPACITY = 1e8;
  private static final double DEFAULT_LENGTH = 1.0;
  private static final int DEFAULT_NUM_PHASES = 3;
  
	/**
	 * Constructor
	 */
	protected LPNormLineFactory() {
	}
	
	/**
	 * Creates a line and its state from a PFW file line
	 * @param line
	 * @return
	 * @throws MatPowerModelException 
	 */
	public Line createLine(JSONObject object, JSONObject lineCode, Bus fromBus, Bus toBus)  {	
		Line line = constructLine(object, lineCode, fromBus, toBus);		
		return line;
	}
		
	/**
	 * Constructs a line from a pfw text line
	 * @param line
	 * @return
	 */
	private Line constructLine(JSONObject object, JSONObject lineCode, Bus fromBus, Bus toBus) {
    String legacyid = object.getString(LPNormIOConstants.LINE_ID_TAG);
    
    boolean status = true;
    double capacity = object.containsKey(LPNormIOConstants.LINE_CAPACITY_TAG) ? object.getDouble(LPNormIOConstants.LINE_CAPACITY_TAG) : DEFAULT_CAPACITY;    
		double length = object.containsKey(LPNormIOConstants.LINE_LENGTH_TAG) ? object.getDouble(LPNormIOConstants.LINE_LENGTH_TAG) : DEFAULT_LENGTH;
		JSONArray rmatrix = lineCode.getArray(LPNormIOConstants.LINE_CODE_RMATRIX_TAG);
    JSONArray xmatrix = lineCode.getArray(LPNormIOConstants.LINE_CODE_XMATRIX_TAG);
    int numberOfPhases = object.containsKey(LPNormIOConstants.LINE_NUM_PHASES_TAG) ? object.getInt(LPNormIOConstants.LINE_NUM_PHASES_TAG) : DEFAULT_NUM_PHASES;
    JSONArray hasPhase = object.getArray(LPNormIOConstants.LINE_HAS_PHASE_TAG);
    Double constructionCost =  object.containsKey(LPNormIOConstants.LINE_CONSTRUCTION_COST_TAG) ? object.getDouble(LPNormIOConstants.LINE_CONSTRUCTION_COST_TAG) : 1e10;
    Double hardenCost =  object.containsKey(LPNormIOConstants.LINE_HARDEN_COST_TAG) ? object.getDouble(LPNormIOConstants.LINE_HARDEN_COST_TAG) : 1e10;
    Double switchCost =  object.containsKey(LPNormIOConstants.LINE_SWITCH_COST_TAG) ? object.getDouble(LPNormIOConstants.LINE_SWITCH_COST_TAG) : null;
    boolean isNewLine = object.containsKey(LPNormIOConstants.LINE_IS_NEW_TAG) ? object.getBoolean(LPNormIOConstants.LINE_IS_NEW_TAG) : false;
    boolean hasSwitch = object.containsKey(LPNormIOConstants.LINE_HAS_SWITCH_TAG) ? object.getBoolean(LPNormIOConstants.LINE_HAS_SWITCH_TAG) : false;
    boolean canHarden = object.containsKey(LPNormIOConstants.LINE_CAN_HARDEN_TAG) ? object.getBoolean(LPNormIOConstants.LINE_CAN_HARDEN_TAG) : !isNewLine;
        
//    length = length / 529000013.001;
    
  	// check to see if the area already exists
		Line line = registerLine(legacyid);
		
    line.setCapacityRating(capacity);
    line.setLongTermEmergencyCapacityRating(capacity);    
    line.setAttribute(Line.RESISTANCE_PHASE_A_KEY,rmatrix.getArray(0).getDouble(0) * length);
    line.setAttribute(Line.RESISTANCE_PHASE_AB_KEY,rmatrix.getArray(0).getDouble(1) * length);
    line.setAttribute(Line.RESISTANCE_PHASE_AC_KEY,rmatrix.getArray(0).getDouble(2) * length);
    line.setAttribute(Line.RESISTANCE_PHASE_BA_KEY,rmatrix.getArray(1).getDouble(0) * length);
    line.setAttribute(Line.RESISTANCE_PHASE_B_KEY,rmatrix.getArray(1).getDouble(1) * length);
    line.setAttribute(Line.RESISTANCE_PHASE_BC_KEY,rmatrix.getArray(1).getDouble(2) * length);
    line.setAttribute(Line.RESISTANCE_PHASE_CA_KEY,rmatrix.getArray(2).getDouble(0) * length);
    line.setAttribute(Line.RESISTANCE_PHASE_CB_KEY,rmatrix.getArray(2).getDouble(1) * length);
    line.setAttribute(Line.RESISTANCE_PHASE_C_KEY,rmatrix.getArray(2).getDouble(2) * length);

    line.setAttribute(Line.REACTANCE_PHASE_A_KEY,xmatrix.getArray(0).getDouble(0) * length);
    line.setAttribute(Line.REACTANCE_PHASE_AB_KEY,xmatrix.getArray(0).getDouble(1) * length);
    line.setAttribute(Line.REACTANCE_PHASE_AC_KEY,xmatrix.getArray(0).getDouble(2) * length);
    line.setAttribute(Line.REACTANCE_PHASE_BA_KEY,xmatrix.getArray(1).getDouble(0) * length);
    line.setAttribute(Line.REACTANCE_PHASE_B_KEY,xmatrix.getArray(1).getDouble(1) * length);
    line.setAttribute(Line.REACTANCE_PHASE_BC_KEY,xmatrix.getArray(1).getDouble(2) * length);
    line.setAttribute(Line.REACTANCE_PHASE_CA_KEY,xmatrix.getArray(2).getDouble(0) * length);
    line.setAttribute(Line.REACTANCE_PHASE_CB_KEY,xmatrix.getArray(2).getDouble(1) * length);
    line.setAttribute(Line.REACTANCE_PHASE_C_KEY,xmatrix.getArray(2).getDouble(2) * length);
    
    
    line.setShortTermEmergencyCapacityRating(capacity);
    line.setDesiredStatus(status);
    line.setActualStatus(status);
    line.setAttribute(Line.LENGTH_KEY, length);
    line.setAttribute(Line.NUMBER_OF_PHASES_KEY,numberOfPhases);
    line.setAttribute(Line.HAS_PHASE_A_KEY,hasPhase.getBoolean(0));
    line.setAttribute(Line.HAS_PHASE_B_KEY,hasPhase.getBoolean(1));
    line.setAttribute(Line.HAS_PHASE_C_KEY,hasPhase.getBoolean(2));

    if (isNewLine) {    
      line.setAttribute(AlgorithmConstants.LINE_CONSTRUCTION_COST_KEY,constructionCost);
    }
    if (!isNewLine) {
      line.setAttribute(AlgorithmConstants.LINE_HARDEN_COST_KEY,hardenCost);
    }
    line.setAttribute(AlgorithmConstants.LINE_SWITCH_COST_KEY,switchCost);
    line.setAttribute(AlgorithmConstants.IS_NEW_LINE_KEY,isNewLine);
    line.setAttribute(AlgorithmConstants.HAS_SWITCH_KEY,hasSwitch);
    line.setAttribute(AlgorithmConstants.CAN_HARDEN_KEY, canHarden);


    Vector<Point> points = new Vector<Point>();
    points.add(fromBus.getCoordinate());
    points.add(toBus.getCoordinate());
    line.setCoordinates(new LineImpl(points));    
    return line;  	
	}
	
	@Override
	protected Line createEmptyLine(Bus fromBus, Bus toBus, Transformer transformer) {
	  return null;
	}

	@Override
	protected Line createEmptyLine(Bus fromBus, Bus toBus, Object circuit) {
	  return null;
	}
	
  /**
   * Register the line
   * 
   * @param legacyId
   * @param bus
   * @return
   */
  private Line registerLine(String legacyId) {    
    Line line = getLegacy(LEGACY_TAG, legacyId);
    if (line == null) {
      line = createNewLine();
      line.addOutputKey(LPNormModelConstants.LPNORM_LEGACY_ID_KEY);
      line.setAttribute(LPNormModelConstants.LPNORM_LEGACY_ID_KEY, legacyId);
      registerLegacy(LEGACY_TAG, legacyId, line);
    }
    return line;
  }
  
}
