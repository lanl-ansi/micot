package gov.lanl.micot.infrastructure.ep.model.pfw;

import gov.lanl.micot.infrastructure.ep.model.Bus;
import gov.lanl.micot.infrastructure.ep.model.Transformer;
import gov.lanl.micot.infrastructure.ep.model.TransformerControlModeEnum;
import gov.lanl.micot.infrastructure.ep.model.TransformerFactory;
import gov.lanl.micot.infrastructure.ep.model.TransformerTypeEnum;
import gov.lanl.micot.util.geometry.LineImpl;
import gov.lanl.micot.util.geometry.Point;
import gov.lanl.micot.util.geometry.PointImpl;

import java.util.Collection;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * Factory class for creating PFWLines and ensuring their uniqueness
 * @author Russell Bent
 */
public class PFWTransformerFactory extends TransformerFactory {

  protected static final double DEFAULT_CONTROL_MAX = 1.02;
  protected static final double DEFAULT_CONTROL_MIN = 0.97;
  protected static final TransformerControlModeEnum  DEFAULT_CONTROL_SIDE = TransformerControlModeEnum.CONTROL_BUS_IS_TERMINAL ;
  protected static final double DEFAULT_MAX_TAP_ANGLE = 1.1;
  protected static final double DEFAULT_MIN_TAP_ANGLE = 0.9;
  protected static final double DEFAULT_TAP_ANGLE = 0;
  protected static final double DEFAULT_TAP_RATIO = 1.01;
  protected static final TransformerTypeEnum DEFAULT_TYPE = TransformerTypeEnum.TRANSMISSION_FIXED_ANGLE_FIXED_TAP_TYPE;
  protected static final double DEFAULT_STEP_SIZE = 0.00625;
  protected static final String DEFAULT_BRANCH_RATING = "M";

//	private static PFWTransformerFactory instance = null;
	private static final String LEGACY_TAG = "PFW";
	
	/**
	 * Get the instance of the transformer factory
	 * @return
	 */
	//public static PFWTransformerFactory getInstance() {
		//if (instance == null) {
			//instance = new PFWTransformerFactory();
		//}
		//return instance;
	//}

	/**
	 * Constructor
	 */
	protected PFWTransformerFactory() {
	}
	
	/**
	 * Creates a transformer and its state from a PFW file line
	 * @param line
	 * @return
	 * @throws PFWModelException 
	 */
	public Transformer createTransformer(String line, Bus fromBus, Bus toBus, Collection<Point> points) throws PFWModelException {	
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
		// parse the information
		StringTokenizer tokenizer = new StringTokenizer(line, ",");

		/*int from =*/ Integer.parseInt(tokenizer.nextToken().trim());
		/*int to = */Integer.parseInt(tokenizer.nextToken().trim());
		String circuit = tokenizer.nextToken();
		if (circuit.startsWith("\"")) {
			while (!circuit.endsWith("\"")) {
				circuit = circuit + "," + tokenizer.nextToken();
			}
		}
		// strip off the quotes
		circuit = circuit.substring(1,circuit.length()-1);
		
		/*int area = */Integer.parseInt(tokenizer.nextToken().trim());
		/*int zone = */Integer.parseInt(tokenizer.nextToken().trim());
		double resistance = Double.parseDouble(tokenizer.nextToken().trim());
		double reactance = Double.parseDouble(tokenizer.nextToken().trim());
		double charging = Double.parseDouble(tokenizer.nextToken().trim());
		double rating1 = Double.parseDouble(tokenizer.nextToken().trim());
		double rating2 = Double.parseDouble(tokenizer.nextToken().trim());
		double rating3 = Double.parseDouble(tokenizer.nextToken().trim());
		int status = Integer.parseInt(tokenizer.nextToken().trim());
		String branchRatingDesginator = tokenizer.nextToken();
		int type = Integer.parseInt(tokenizer.nextToken().trim());
		double tapRatio = Double.parseDouble(tokenizer.nextToken().trim());
		double tapAngle = Double.parseDouble(tokenizer.nextToken().trim());
		double minTapAngle = Double.parseDouble(tokenizer.nextToken().trim());
		double maxTapAngle = Double.parseDouble(tokenizer.nextToken().trim());
		double stepSize = Double.parseDouble(tokenizer.nextToken().trim());
		double controlMin = Double.parseDouble(tokenizer.nextToken().trim());
		double controlMax = Double.parseDouble(tokenizer.nextToken().trim());
		/*int controlBus =*/ Integer.parseInt(tokenizer.nextToken().trim());
		int controlSide = Integer.parseInt(tokenizer.nextToken().trim());

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
    transformer.setDesiredStatus(status == 1 ? true : false);
    transformer.setAttribute(PFWModelConstants.PFW_TRANSFORMER_BRANCH_RATING_DESGINATOR_KEY,branchRatingDesginator);
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
    transformer.setAttribute(PFWModelConstants.PFW_TRANSFORMER_BRANCH_RATING_DESGINATOR_KEY,line.getAttribute(PFWModelConstants.PFW_TRANSFORMER_BRANCH_RATING_DESGINATOR_KEY, String.class));
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
	    transformer.setAttribute(PFWModelConstants.PFW_TRANSFORMER_BRANCH_RATING_DESGINATOR_KEY,PFWLineFactory.DEFAULT_BRANCH_RATING);
	    return transformer;
	  }

	
  /**
   * Create the unique key
   * @param line
   * @return
   */
  private String getKey(Bus fromBus, Bus toBus, String circuit) {
    return fromBus.getAttribute(PFWModelConstants.PFW_LEGACY_ID_KEY) + "," + toBus.getAttribute(PFWModelConstants.PFW_LEGACY_ID_KEY) + "," + circuit;
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
      registerLegacy(LEGACY_TAG, legacyid, line);
      line.setAttribute(PFWModelConstants.PFW_LEGACY_ID_KEY,legacyid);
    }
    
    if (line.getAttribute(PFWModelConstants.PFW_TRANSFORMER_BRANCH_RATING_DESGINATOR_KEY) == null) {
      line.setAttribute(PFWModelConstants.PFW_TRANSFORMER_BRANCH_RATING_DESGINATOR_KEY, DEFAULT_BRANCH_RATING);
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
      transformer.addOutputKey(PFWModelConstants.PFW_LEGACY_ID_KEY);
      transformer.setAttribute(PFWModelConstants.PFW_LEGACY_ID_KEY, legacyId);
      registerLegacy(LEGACY_TAG, legacyId, transformer);
    }
    return transformer;
  }


  

}
