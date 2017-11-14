package gov.lanl.micot.infrastructure.ep.model.matpower;

import gov.lanl.micot.infrastructure.ep.model.Bus;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.Transformer;
import gov.lanl.micot.infrastructure.ep.model.TransformerFactory;
import gov.lanl.micot.util.geometry.LineImpl;
import gov.lanl.micot.util.geometry.Point;
import gov.lanl.micot.util.geometry.PointImpl;

import java.util.Collection;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * Factory class for creating MatPower Transformers and ensuring their uniqueness
 * @author Russell Bent
 */
public class MatPowerTransformerFactory extends TransformerFactory {

  protected static final double DEFAULT_TAP_ANGLE = 0;
  protected static final double DEFAULT_TAP_RATIO = 1.0;
  private static final double DEFAULT_MIN_ANGLE = -360;
  private static final double DEFAULT_MAX_ANGLE = 360;
  
	private static final String LEGACY_TAG = "MATPOWER";
	
	/**
	 * Constructor
	 */
	protected MatPowerTransformerFactory() {
	}
	
	/**
	 * Creates a transformer and its state from a PFW file line
	 * @param line
	 * @return
	 * @throws MatPowerModelException 
	 */
	public Transformer createTransformer(String line, Bus fromBus, Bus toBus, String circuit, Collection<Point> points) throws MatPowerModelException {	
		Transformer pair = constructTransformer(line, fromBus, toBus, circuit, points);		
		String legacyid = getKey(fromBus, toBus, circuit);
    if (getLegacy(LEGACY_TAG,legacyid) != null) {
    	if (points == null) {
  			pair.setCoordinates(getLegacy(LEGACY_TAG,legacyid).getCoordinates());
  		}  	
    }
    return pair;
	}
	
	/**
	 * Construct a transformer
	 * @param string
	 * @return
	 */
	private Transformer constructTransformer(String line, Bus fromBus, Bus toBus, String circuit, Collection<Point> points) {
	  StringTokenizer tokenizer = new StringTokenizer(line, "\t");
		/*int from =*/ Integer.parseInt(tokenizer.nextToken());
		/*int to = */Integer.parseInt(tokenizer.nextToken());
		double resistance = Double.parseDouble(tokenizer.nextToken());
		double reactance = Double.parseDouble(tokenizer.nextToken());
		double charging = Double.parseDouble(tokenizer.nextToken());
		double normalrating = Double.parseDouble(tokenizer.nextToken());
		double shortrating = Double.parseDouble(tokenizer.nextToken());
		double emergencyrating = Double.parseDouble(tokenizer.nextToken());
		double tapRatio = Double.parseDouble(tokenizer.nextToken());
		double tapAngle = Double.parseDouble(tokenizer.nextToken());
		int status = Integer.parseInt(tokenizer.nextToken());
		double minAngleDifference = Double.parseDouble(tokenizer.nextToken());
		String temp = tokenizer.nextToken();
		double maxAngleDifference = Double.parseDouble(temp.substring(0,temp.length()-1));
				
  	// check to see if the area already exists
		Transformer transformer = registerTransformer(fromBus, toBus, circuit);
    transformer.setAttribute(Transformer.CIRCUIT_KEY, circuit);
    transformer.setResistance(resistance);
    transformer.setReactance(reactance);
    transformer.setLineCharging(charging);
    transformer.setCapacityRating(normalrating);
    transformer.setShortTermEmergencyCapacityRating(emergencyrating);
    transformer.setLongTermEmergencyCapacityRating(shortrating);
    transformer.setStatus(status == 1 ? true : false);
    transformer.setMWFlow(0.0);
    transformer.setMVarFlow(0.0);
    transformer.setAttribute(Transformer.TAP_RATIO_KEY, tapRatio);
    transformer.setAttribute(Transformer.TAP_ANGLE_KEY, tapAngle);
		if (points == null) {
			points = new Vector<Point>();
    	points.add(new PointImpl(0, 0));
    	points.add(new PointImpl(0, 0));
		}
		transformer.setCoordinates(new LineImpl(points.toArray(new Point[0])));
	  transformer.setAttribute(MatPowerModelConstants.MATPOWER_MIN_ANGLE_DIFFERENCE_KEY, minAngleDifference);
	  transformer.setAttribute(MatPowerModelConstants.MATPOWER_MAX_ANGLE_DIFFERENCE_KEY, maxAngleDifference);
				
	  transformer.setAttribute(Transformer.HAS_PHASE_A_KEY, true);
	  transformer.setAttribute(Transformer.HAS_PHASE_B_KEY, false);
	  transformer.setAttribute(Transformer.HAS_PHASE_C_KEY, false);
	  transformer.setAttribute(Transformer.CAPACITY_RATING_A_KEY, normalrating);
	  transformer.setAttribute(Transformer.CAPACITY_RATING_B_KEY, 0.0);
	  transformer.setAttribute(Transformer.CAPACITY_RATING_C_KEY, 0.0);
	  transformer.setAttribute(ElectricPowerFlowConnection.NUMBER_OF_PHASES_KEY, 1);

  	return transformer;
	}
	
	/**
	 * Creates/gets a copy of a line with a new circuit
	 * @param line
	 * @param state
	 * @param circuit
	 * @return
	 */
	public Transformer createTransformerWithCircuit(Transformer line, Bus fromBus, Bus toBus, String circuit) {
	  Transformer transformer = registerTransformer(fromBus, toBus, circuit);
    transformer.setAttribute(Transformer.CIRCUIT_KEY, circuit);
    transformer.setResistance(line.getResistance());
    transformer.setReactance(line.getReactance());
    transformer.setLineCharging(line.getLineCharging());
    transformer.setCapacityRating(line.getCapacityRating().intValue());
    transformer.setShortTermEmergencyCapacityRating(line.getShortTermEmergencyCapacityRating().intValue());
    transformer.setLongTermEmergencyCapacityRating(line.getLongTermEmergencyCapacityRating().intValue());
    transformer.setStatus(line.getStatus());
    transformer.setAttribute(Transformer.TYPE_KEY, line.getAttribute(Transformer.TYPE_KEY));
    transformer.setAttribute(Transformer.TAP_RATIO_KEY, line.getAttribute(Transformer.TAP_RATIO_KEY));
    transformer.setAttribute(Transformer.TAP_ANGLE_KEY, line.getAttribute(Transformer.TAP_ANGLE_KEY));
    transformer.setAttribute(Transformer.MIN_TAP_ANGLE_KEY, line.getAttribute(Transformer.MIN_TAP_ANGLE_KEY));
    transformer.setAttribute(Transformer.MAX_TAP_ANGLE_KEY, line.getAttribute(Transformer.MAX_TAP_ANGLE_KEY));
    transformer.setAttribute(Transformer.STEP_SIZE_KEY, line.getAttribute(Transformer.STEP_SIZE_KEY));
    transformer.setAttribute(Transformer.CONTROL_MIN_KEY, line.getAttribute(Transformer.CONTROL_MIN_KEY));
    transformer.setAttribute(Transformer.CONTROL_MAX_KEY, line.getAttribute(Transformer.CONTROL_MAX_KEY));
    transformer.setAttribute(Transformer.CONTROL_SIDE_KEY, line.getAttribute(Transformer.CONTROL_SIDE_KEY)); 
    transformer.setAttribute(MatPowerModelConstants.MATPOWER_MIN_ANGLE_DIFFERENCE_KEY, line.getAttribute(MatPowerModelConstants.MATPOWER_MIN_ANGLE_DIFFERENCE_KEY));
	  transformer.setAttribute(MatPowerModelConstants.MATPOWER_MAX_ANGLE_DIFFERENCE_KEY, line.getAttribute(MatPowerModelConstants.MATPOWER_MAX_ANGLE_DIFFERENCE_KEY));

		transformer.setCoordinates(line.getCoordinates());
		return transformer;		
	}
	
	@Override
	protected Transformer createEmptyTransformer(Bus bus1, Bus bus2, Object circuit) {
	  Transformer transformer = registerTransformer(bus1, bus2, circuit.toString());   
	  transformer.setAttribute(MatPowerModelConstants.MATPOWER_MIN_ANGLE_DIFFERENCE_KEY, DEFAULT_MIN_ANGLE);
	  transformer.setAttribute(MatPowerModelConstants.MATPOWER_MAX_ANGLE_DIFFERENCE_KEY, DEFAULT_MAX_ANGLE);
	  return transformer;
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
   * Augment a transformers data
   * @param bus
   * @return
   */
  public void updateTransformer(Transformer line, Bus id1, Bus id2) {
    if (line.getAttribute(MatPowerModelConstants.MATPOWER_MAX_ANGLE_DIFFERENCE_KEY) == null) {
    	line.setAttribute(MatPowerModelConstants.MATPOWER_MAX_ANGLE_DIFFERENCE_KEY, DEFAULT_MAX_ANGLE);
    }

    if (line.getAttribute(MatPowerModelConstants.MATPOWER_MIN_ANGLE_DIFFERENCE_KEY) == null) {
    	line.setAttribute(MatPowerModelConstants.MATPOWER_MIN_ANGLE_DIFFERENCE_KEY, DEFAULT_MIN_ANGLE);
    }
    
    
    String circuit = line.getCircuit() == null ? "" : line.getCircuit().toString(); 
    String legacyid = getKey(id1, id2, circuit);
    if (getLegacy(LEGACY_TAG,legacyid) == null) {
      registerLegacy(LEGACY_TAG, legacyid, line);
      line.setAttribute(MatPowerModelConstants.MATPOWER_LEGACY_ID_KEY, legacyid);
    }
    
    if (line.getAttribute(Transformer.TAP_RATIO_KEY) == null) {
      line.setAttribute(Transformer.TAP_RATIO_KEY, DEFAULT_TAP_RATIO);
    }

    if (line.getAttribute(Transformer.TAP_ANGLE_KEY) == null) {
      line.setAttribute(Transformer.TAP_ANGLE_KEY, DEFAULT_TAP_ANGLE);
    }    
  }

  /**
   * Register the line
   * 
   * @param legacyId
   * @param bus
   * @return
   */
  private Transformer registerTransformer(Bus fromBus, Bus toBus, String circuit) {
    String legacyId = getKey(fromBus, toBus, circuit);
    Transformer transformer = getLegacy(LEGACY_TAG, legacyId);
    if (transformer == null) {
      transformer = createNewTransformer();
      transformer.addOutputKey(MatPowerModelConstants.MATPOWER_LEGACY_ID_KEY);
      transformer.setAttribute(MatPowerModelConstants.MATPOWER_LEGACY_ID_KEY, legacyId);
      registerLegacy(LEGACY_TAG, legacyId, transformer);
    }
    return transformer;
  }
  

}
