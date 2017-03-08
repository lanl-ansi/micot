package gov.lanl.micot.infrastructure.ep.model.cdf;

import gov.lanl.micot.infrastructure.ep.model.Bus;
import gov.lanl.micot.infrastructure.ep.model.Transformer;
import gov.lanl.micot.infrastructure.ep.model.TransformerControlModeEnum;
import gov.lanl.micot.infrastructure.ep.model.TransformerFactory;
import gov.lanl.micot.infrastructure.ep.model.TransformerTypeEnum;
import gov.lanl.micot.util.geometry.LineImpl;
import gov.lanl.micot.util.geometry.Point;
import gov.lanl.micot.util.geometry.PointImpl;

import java.util.Collection;
import java.util.Vector;

/**
 * Factory class for creating PFWLines and ensuring their uniqueness
 * @author Russell Bent
 */
public class CDFTransformerFactory extends TransformerFactory {

//	private static CDFTransformerFactory instance = null;
	private static final String LEGACY_TAG = "CDF";
	
	/**
	 * Get the instance of the transformer factory
	 * @return
	 */
	//public static CDFTransformerFactory getInstance() {
		//if (instance == null) {
			//instance = new CDFTransformerFactory();
	//	}
	//	return instance;
	//}
	
	/**
	 * Constructor
	 */
	protected CDFTransformerFactory() {
	}
	
	/**
	 * Creates a transformer and its state from a PFW file line
	 * @param line
	 * @return
	 * @throws PFWModelException 
	 */
	public Transformer createTransformer(String line, Bus fromBus, Bus toBus, Collection<Point> points) throws CDFModelException {	
		Transformer pair = constructTransformer(line, fromBus, toBus, points);	
		String legacyid = getKey(fromBus, toBus, pair.getCircuit().toString());
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
	private Transformer constructTransformer(String line, Bus fromBus, Bus toBus, Collection<Point> points) {
    String circuit = line.substring(16,17); 
    double resistance = Double.parseDouble(line.substring(20,29).trim());
    double reactance = Double.parseDouble(line.substring(30,40).trim());
    double charging = Double.parseDouble(line.substring(41,50).trim());
    int rating1 = Integer.parseInt(line.substring(51,55).trim());
    int rating2 = Integer.parseInt(line.substring(57,61).trim());
    int rating3 = Integer.parseInt(line.substring(63,67).trim());
    boolean status = true;
    int type = Integer.parseInt(line.substring(18,19).trim());
    int controlSide = Integer.parseInt(line.substring(73,75).trim());
    double tapRatio = Double.parseDouble(line.substring(76,82).trim());
    double tapAngle = Double.parseDouble(line.substring(83,90).trim());
    double minTapAngle = Double.parseDouble(line.substring(90,97).trim());
    double maxTapAngle = Double.parseDouble(line.substring(97,104).trim());
    double stepSize = Double.parseDouble(line.substring(105,111).trim());
    double controlMin = Double.parseDouble(line.substring(112,118).trim());
    double controlMax = Double.parseDouble(line.substring(120,Math.min(126,line.length())).trim());

  	// check to see if the area already exists
		TransformerControlModeEnum gControlSide = TransformerControlModeEnum.getEnum(controlSide);
		TransformerTypeEnum transformerType = TransformerTypeEnum.getEnum(type);

		Transformer transformer = registerTransformer(fromBus, toBus, circuit);
    transformer.setAttribute(Transformer.CIRCUIT_KEY, circuit);
    transformer.setResistance(resistance);
    transformer.setReactance(reactance);
    transformer.setLineCharging(charging);
    transformer.setCapacityRating(rating1);
    transformer.setShortTermEmergencyCapacityRating(rating2);
    transformer.setLongTermEmergencyCapacityRating(rating3);
    transformer.setDesiredStatus(status);
    transformer.setAttribute(Transformer.TYPE_KEY, transformerType);
    transformer.setAttribute(Transformer.TAP_RATIO_KEY, tapRatio);
    transformer.setAttribute(Transformer.TAP_ANGLE_KEY, tapAngle);
    transformer.setAttribute(Transformer.MIN_TAP_ANGLE_KEY, minTapAngle);
    transformer.setAttribute(Transformer.MAX_TAP_ANGLE_KEY, maxTapAngle);
    transformer.setAttribute(Transformer.STEP_SIZE_KEY, stepSize);
    transformer.setAttribute(Transformer.CONTROL_MIN_KEY, controlMin);
    transformer.setAttribute(Transformer.CONTROL_MAX_KEY, controlMax);
    transformer.setAttribute(Transformer.CONTROL_SIDE_KEY, gControlSide);   
    transformer.setActualStatus(transformer.getDesiredStatus());
    transformer.setMWFlow(0.0);
    transformer.setMVarFlow(0.0);
		if (points == null) {
			points = new Vector<Point>();
    	points.add(new PointImpl(0, 0));
    	points.add(new PointImpl(0, 0));
		}
		transformer.setCoordinates(new LineImpl(points.toArray(new Point[0])));
		
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
    transformer.setDesiredStatus(line.getDesiredStatus());
    transformer.setAttribute(Transformer.TYPE_KEY, line.getAttribute(Transformer.TYPE_KEY));
    transformer.setAttribute(Transformer.TAP_RATIO_KEY, line.getAttribute(Transformer.TAP_RATIO_KEY));
    transformer.setAttribute(Transformer.TAP_ANGLE_KEY, line.getAttribute(Transformer.TAP_ANGLE_KEY));
    transformer.setAttribute(Transformer.MIN_TAP_ANGLE_KEY, line.getAttribute(Transformer.MIN_TAP_ANGLE_KEY));
    transformer.setAttribute(Transformer.MAX_TAP_ANGLE_KEY, line.getAttribute(Transformer.MAX_TAP_ANGLE_KEY));
    transformer.setAttribute(Transformer.STEP_SIZE_KEY, line.getAttribute(Transformer.STEP_SIZE_KEY));
    transformer.setAttribute(Transformer.CONTROL_MIN_KEY, line.getAttribute(Transformer.CONTROL_MIN_KEY));
    transformer.setAttribute(Transformer.CONTROL_MAX_KEY, line.getAttribute(Transformer.CONTROL_MAX_KEY));
    transformer.setAttribute(Transformer.CONTROL_SIDE_KEY, line.getAttribute(Transformer.CONTROL_SIDE_KEY));   
    transformer.setActualStatus(transformer.getDesiredStatus());  		
		transformer.setCoordinates(line.getCoordinates());
		return transformer;
		
	}

	@Override
	protected Transformer createEmptyTransformer(Bus bus1, Bus bus2, Object circuit) {
	  Transformer transformer = registerTransformer(bus1, bus2, circuit.toString());   
	  return transformer;
	}
	
  /**
   * Create the unique key
   * @param line
   * @return
   */
  private String getKey(Bus fromBus, Bus toBus, String circuit) {
    return fromBus.getAttribute(CDFModelConstants.CDF_LEGACY_ID_KEY) + "," + toBus.getAttribute(CDFModelConstants.CDF_LEGACY_ID_KEY) + "," + circuit;
  }
  
  /**
   * Augment a transformers data
   * @param bus
   * @return
   */
  public void updateTransformer(Transformer line, Bus id1, Bus id2, String circuit) {
    if (line.getCircuit() == null) {
      line.setAttribute(Transformer.CIRCUIT_KEY,circuit);
    }

    String legacyid = getKey(id1, id2, circuit);
    if (getLegacy(LEGACY_TAG,legacyid) == null) {
      registerLegacy(LEGACY_TAG, legacyid,line);
      line.setAttribute(CDFModelConstants.CDF_LEGACY_ID_KEY, legacyid);
    }
    
    if (line.getAttribute(Transformer.TYPE_KEY) == null) {
      line.setAttribute(Transformer.TYPE_KEY, DEFAULT_TYPE);
    }

    if (line.getAttribute(Transformer.TAP_RATIO_KEY) == null) {
      line.setAttribute(Transformer.TAP_RATIO_KEY, DEFAULT_TAP_RATIO);
    }

    if (line.getAttribute(Transformer.TAP_ANGLE_KEY) == null) {
      line.setAttribute(Transformer.TAP_ANGLE_KEY, DEFAULT_TAP_ANGLE);
    }

    if (line.getAttribute(Transformer.MIN_TAP_ANGLE_KEY) == null) {
      line.setAttribute(Transformer.MIN_TAP_ANGLE_KEY, DEFAULT_MIN_TAP_ANGLE);
    }

    if (line.getAttribute(Transformer.MAX_TAP_ANGLE_KEY) == null) {
      line.setAttribute(Transformer.MAX_TAP_ANGLE_KEY, DEFAULT_MAX_TAP_ANGLE);
    }

    if (line.getAttribute(Transformer.STEP_SIZE_KEY) == null) {
      line.setAttribute(Transformer.STEP_SIZE_KEY, DEFAULT_STEP_SIZE);
    }

    if (line.getAttribute(Transformer.CONTROL_MIN_KEY) == null) {
      line.setAttribute(Transformer.CONTROL_MIN_KEY, DEFAULT_CONTROL_MIN);
    }

    if (line.getAttribute(Transformer.CONTROL_MAX_KEY) == null) {
      line.setAttribute(Transformer.CONTROL_MAX_KEY, DEFAULT_CONTROL_MAX);
    }

    if (line.getAttribute(Transformer.CONTROL_SIDE_KEY) == null) {
      line.setAttribute(Transformer.CONTROL_SIDE_KEY, DEFAULT_CONTROL_SIDE);
    }
  }

  /**
   * Register the transformer
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
      transformer.addOutputKey(CDFModelConstants.CDF_LEGACY_ID_KEY);
      transformer.setAttribute(CDFModelConstants.CDF_LEGACY_ID_KEY, legacyId);
      registerLegacy(LEGACY_TAG, legacyId, transformer);
    }
    return transformer;
  }

  

}
