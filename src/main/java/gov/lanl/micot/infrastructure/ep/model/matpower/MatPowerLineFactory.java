package gov.lanl.micot.infrastructure.ep.model.matpower;

import gov.lanl.micot.infrastructure.ep.model.Bus;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.Line;
import gov.lanl.micot.infrastructure.ep.model.LineFactory;
import gov.lanl.micot.infrastructure.ep.model.Transformer;
import gov.lanl.micot.util.geometry.LineImpl;
import gov.lanl.micot.util.geometry.Point;
import gov.lanl.micot.util.geometry.PointImpl;

import java.util.Collection;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * Factory class for creating MatPowerLines and ensuring their uniqueness
 * @author Russell Bent
 */
public class MatPowerLineFactory extends LineFactory {

  private static final double DEFAULT_MIN_ANGLE = -360;
  private static final double DEFAULT_MAX_ANGLE = 360;
   
  private static final String LEGACY_TAG = "MATPOWER";

	/**
	 * Constructor
	 */
	protected MatPowerLineFactory() {
	}

	/**
	 * Create the unique key
	 * @param line
	 * @return
	 */
  private String getKey(Bus fromBus, Bus toBus, String circuit) {
    return fromBus.getAttribute(MatPowerModelConstants.MATPOWER_LEGACY_ID_KEY) + "," + toBus.getAttribute(MatPowerModelConstants.MATPOWER_LEGACY_ID_KEY) + "," + circuit;
  }

	
	/**
	 * Creates a line and its state from a PFW file line
	 * @param line
	 * @return
	 * @throws MatPowerModelException 
	 */
	public Line createLine(String line, Bus fromBus, Bus toBus, String circuit, Collection<Point> points) throws MatPowerModelException {	
		Line pair = constructLine(line, fromBus, toBus, circuit, points);		
		String legacyid = getKey(fromBus, toBus, circuit);
		
  	if (getLegacy(LEGACY_TAG, legacyid) != null) {
  		if (points == null) {
  			pair.setCoordinates(getLegacy(LEGACY_TAG, legacyid).getCoordinates());
  		}  		
  	}
		return pair;
	}
		
	/**
	 * Constructs a line from a pfw text line
	 * @param line
	 * @return
	 */
	private Line constructLine(String line, Bus fromBus, Bus toBus, String circuit, Collection<Point> points) {
	  StringTokenizer tokenizer = new StringTokenizer(line, "\t");
		/*int from = */Integer.parseInt(tokenizer.nextToken());
		/*int to = */Integer.parseInt(tokenizer.nextToken().trim());
		double resistance = Double.parseDouble(tokenizer.nextToken());
		double reactance = Double.parseDouble(tokenizer.nextToken());
		double charging = Double.parseDouble(tokenizer.nextToken());
		double normalrating = Double.parseDouble(tokenizer.nextToken());
		double shortrating = Double.parseDouble(tokenizer.nextToken());
		double emergencyrating = Double.parseDouble(tokenizer.nextToken());
		tokenizer.nextToken(); // tap ratio
		tokenizer.nextToken(); // phase shift angle
		int status = Integer.parseInt(tokenizer.nextToken().trim());
		double minAngleDifference = Double.parseDouble(tokenizer.nextToken());
		String temp = tokenizer.nextToken();
		double maxAngleDifference = Double.parseDouble(temp.substring(0,temp.length()-1));
				
  	// check to see if the area already exists
		Line pfwLine = registerLine(fromBus, toBus, circuit);
	  pfwLine.setAttribute(Line.CIRCUIT_KEY, circuit);
    pfwLine.setResistance(resistance);
    pfwLine.setReactance(reactance);
    pfwLine.setLineCharging(charging);
    pfwLine.setCapacityRating(normalrating);
    pfwLine.setShortTermEmergencyCapacityRating(emergencyrating);
    pfwLine.setLongTermEmergencyCapacityRating(shortrating);
    pfwLine.setStatus(status == 1 ? true : false);
    pfwLine.setMWFlow(0.0);
    pfwLine.setMVarFlow(0.0);
		if (points == null) {
			points = new Vector<Point>();
    	points.add(new PointImpl(0, 0));
    	points.add(new PointImpl(0, 0));
		}
		pfwLine.setCoordinates(new LineImpl(points.toArray(new Point[0])));
	  pfwLine.setAttribute(MatPowerModelConstants.MATPOWER_MIN_ANGLE_DIFFERENCE_KEY, minAngleDifference);
	  pfwLine.setAttribute(MatPowerModelConstants.MATPOWER_MAX_ANGLE_DIFFERENCE_KEY, maxAngleDifference);
	 
	  pfwLine.setAttribute(Line.HAS_PHASE_A_KEY, true);
	  pfwLine.setAttribute(Line.HAS_PHASE_B_KEY, false);
	  pfwLine.setAttribute(Line.HAS_PHASE_C_KEY, false);
	  pfwLine.setAttribute(Line.CAPACITY_RATING_A_KEY, normalrating);
	  pfwLine.setAttribute(Line.CAPACITY_RATING_B_KEY, 0.0);
	  pfwLine.setAttribute(Line.CAPACITY_RATING_C_KEY, 0.0);
	  pfwLine.setAttribute(ElectricPowerFlowConnection.NUMBER_OF_PHASES_KEY, 1);

  	return pfwLine;
	}
	
	 @Override
	  protected Line createEmptyLine(Bus fromBus, Bus toBus, Transformer transformer) {
	    Line line = registerLine(fromBus, toBus, transformer.getCircuit().toString());   
	    line.setAttribute(MatPowerModelConstants.MATPOWER_MIN_ANGLE_DIFFERENCE_KEY, transformer.getAttribute(MatPowerModelConstants.MATPOWER_MIN_ANGLE_DIFFERENCE_KEY));
	    line.setAttribute(MatPowerModelConstants.MATPOWER_MAX_ANGLE_DIFFERENCE_KEY, transformer.getAttribute(MatPowerModelConstants.MATPOWER_MAX_ANGLE_DIFFERENCE_KEY));
	    return line;
	  }

	
	/**
	 * Creates/gets a copy of a line with a new circuit
	 * @param line
	 * @param state
	 * @param circuit
	 * @return
	 */
	public Line getLineWithCircuit(Line line, Bus fromBus, Bus toBus, String circuit) {
	  Line pfwLine = registerLine(fromBus, toBus, circuit);
    pfwLine.setAttribute(Line.CIRCUIT_KEY, circuit);
    pfwLine.setResistance(line.getResistance());
    pfwLine.setReactance(line.getReactance());
    pfwLine.setLineCharging(line.getLineCharging());
    pfwLine.setCapacityRating(line.getCapacityRating());
    pfwLine.setShortTermEmergencyCapacityRating(line.getShortTermEmergencyCapacityRating());
    pfwLine.setLongTermEmergencyCapacityRating(line.getLongTermEmergencyCapacityRating());
    pfwLine.setStatus(line.getStatus());
    pfwLine.setAttribute(MatPowerModelConstants.MATPOWER_MIN_ANGLE_DIFFERENCE_KEY, line.getAttribute(MatPowerModelConstants.MATPOWER_MIN_ANGLE_DIFFERENCE_KEY));
	  pfwLine.setAttribute(MatPowerModelConstants.MATPOWER_MAX_ANGLE_DIFFERENCE_KEY, line.getAttribute(MatPowerModelConstants.MATPOWER_MAX_ANGLE_DIFFERENCE_KEY));
	  return pfwLine;	  
	}


	 @Override
	  protected Line createEmptyLine(Bus fromBus, Bus toBus, Object circuit) {
	    Line line = registerLine(fromBus, toBus, circuit.toString());   
	    line.setAttribute(MatPowerModelConstants.MATPOWER_MIN_ANGLE_DIFFERENCE_KEY, DEFAULT_MIN_ANGLE);
	    line.setAttribute(MatPowerModelConstants.MATPOWER_MAX_ANGLE_DIFFERENCE_KEY, DEFAULT_MAX_ANGLE);
	    return line;
	  }

	
  /**
   * Creates a line from another line
   * @param bus
   * @return
   */
  public void updateLine(Line line, Bus id1, Bus id2) {
    if (line.getAttribute(MatPowerModelConstants.MATPOWER_MAX_ANGLE_DIFFERENCE_KEY) == null) {
    	line.setAttribute(MatPowerModelConstants.MATPOWER_MAX_ANGLE_DIFFERENCE_KEY, DEFAULT_MAX_ANGLE);
    }

    if (line.getAttribute(MatPowerModelConstants.MATPOWER_MIN_ANGLE_DIFFERENCE_KEY) == null) {
    	line.setAttribute(MatPowerModelConstants.MATPOWER_MIN_ANGLE_DIFFERENCE_KEY, DEFAULT_MIN_ANGLE);
    }
    
    String circuit = line.getCircuit() == null ? "" : line.getCircuit().toString();
    String legacyid = getKey(id1, id2, circuit);
    if (getLegacy(LEGACY_TAG, legacyid) == null) {
      registerLegacy(LEGACY_TAG, legacyid, line);
      line.setAttribute(MatPowerModelConstants.MATPOWER_LEGACY_ID_KEY, legacyid);
    }
  }

  /**
   * Register the line
   * 
   * @param legacyId
   * @param bus
   * @return
   */
  private Line registerLine(Bus fromBus, Bus toBus, String circuit) {    
    String legacyId = getKey(fromBus, toBus, circuit);
    Line line = getLegacy(LEGACY_TAG, legacyId);
    if (line == null) {
      line = createNewLine();
      line.addOutputKey(MatPowerModelConstants.MATPOWER_LEGACY_ID_KEY);
      line.setAttribute(MatPowerModelConstants.MATPOWER_LEGACY_ID_KEY, legacyId);
      registerLegacy(LEGACY_TAG, legacyId, line);
    }
    return line;
  }
  
}
