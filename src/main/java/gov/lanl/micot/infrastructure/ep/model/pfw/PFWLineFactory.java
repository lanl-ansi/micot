package gov.lanl.micot.infrastructure.ep.model.pfw;

import gov.lanl.micot.infrastructure.ep.model.Bus;
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
 * Factory class for creating PFWLines and ensuring their uniqueness
 * @author Russell Bent
 */
public class PFWLineFactory extends LineFactory {

	protected static final String DEFAULT_CIRCUIT = "\"00\"";
	protected static final int    DEFAULT_STATUS = 1;
	protected static final String DEFAULT_BRANCH_RATING = "M";
	protected static final int    DEFAULT_UNKNOWN = 0;
	
//	private static PFWLineFactory instance = null;
	private static final String LEGACY_TAG = "PFW";
	
	//public static PFWLineFactory getInstance() {
		//if (instance == null) {
			//instance = new PFWLineFactory();
	//	}
	//	return instance;
	//}
	
	/**
	 * Constructor
	 */
	protected PFWLineFactory() {
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
	 * Creates a line and its state from a PFW file line
	 * @param line
	 * @return
	 * @throws PFWModelException 
	 */
	public Line createLine(String line, Bus fromBus, Bus toBus, Collection<Point> points) throws PFWModelException {	
		Line pair = constructLine(line, fromBus, toBus, points);		
		String legacyid = getKey(fromBus, toBus, pair.getCircuit().toString());		
  	if (getLegacy(LEGACY_TAG,legacyid) != null) {
  		if (points == null) {
  			pair.setCoordinates(getLegacy(LEGACY_TAG,legacyid).getCoordinates());
  		}  		
  	}
  	return pair;
	}
		
	/**
	 * Constructs a line from a pfw text line
	 * @param line
	 * @return
	 */
	private Line constructLine(String line, Bus fromBus, Bus toBus, Collection<Point> points) {
		StringTokenizer tokenizer = new StringTokenizer(line, ",");

		/*int from = */Integer.parseInt(tokenizer.nextToken().trim());
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
		int unknown = Integer.parseInt(tokenizer.nextToken().trim());
		
  	// check to see if the area already exists
		Line pfwLine = registerLine(fromBus, toBus, circuit);
    pfwLine.setAttribute(Line.CIRCUIT_KEY, circuit);
    pfwLine.setResistance(resistance);
    pfwLine.setReactance(reactance);
    pfwLine.setLineCharging(charging);
    pfwLine.setCapacityRating(rating1);
    pfwLine.setShortTermEmergencyCapacityRating(rating2);
    pfwLine.setLongTermEmergencyCapacityRating(rating3);
    pfwLine.setDesiredStatus(status == 1 ? true : false);
    pfwLine.setAttribute(PFWModelConstants.PFW_LINE_BRANCH_RATING_DESGINATOR_KEY,branchRatingDesginator);
    pfwLine.setAttribute(PFWModelConstants.PFW_LINE_UNKNOWN_KEY,unknown);
    pfwLine.setActualStatus(pfwLine.getDesiredStatus());
    pfwLine.setMWFlow(0.0);
    pfwLine.setMVarFlow(0.0);
		if (points == null) {
			points = new Vector<Point>();
    	points.add(new PointImpl(0, 0));
    	points.add(new PointImpl(0, 0));
		}
		pfwLine.setCoordinates(new LineImpl(points.toArray(new Point[0])));
		
  	return pfwLine;
	}
	
	@Override
  protected Line createEmptyLine(Bus fromBus, Bus toBus, Transformer transformer) {
    Line line = registerLine(fromBus, toBus, transformer.getCircuit().toString());   
    line.setAttribute(PFWModelConstants.PFW_LINE_UNKNOWN_KEY,DEFAULT_UNKNOWN);    
    line.setAttribute(PFWModelConstants.PFW_LINE_BRANCH_RATING_DESGINATOR_KEY,transformer.getAttribute(PFWModelConstants.PFW_TRANSFORMER_BRANCH_RATING_DESGINATOR_KEY));
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
    pfwLine.setDesiredStatus(line.getDesiredStatus());
    pfwLine.setAttribute(PFWModelConstants.PFW_LINE_BRANCH_RATING_DESGINATOR_KEY,line.getAttribute(PFWModelConstants.PFW_LINE_BRANCH_RATING_DESGINATOR_KEY));
    pfwLine.setAttribute(PFWModelConstants.PFW_LINE_UNKNOWN_KEY,line.getAttribute(PFWModelConstants.PFW_LINE_UNKNOWN_KEY));
    pfwLine.setActualStatus(pfwLine.getDesiredStatus());
		
	  pfwLine.setCoordinates(((Line)line).getCoordinates());
	  return pfwLine;  
	}
	
	 @Override
	  protected Line createEmptyLine(Bus fromBus, Bus toBus, Object circuit) {
	    Line line = registerLine(fromBus, toBus, circuit.toString());   
	    line.setAttribute(PFWModelConstants.PFW_LINE_BRANCH_RATING_DESGINATOR_KEY,DEFAULT_BRANCH_RATING);
	    line.setAttribute(PFWModelConstants.PFW_LINE_UNKNOWN_KEY,DEFAULT_UNKNOWN);
	    return line;
	  }
	
  /**
   * Creates a bus from another bus
   * @param bus
   * @return
   */
  public void updateLine(Line line, Bus id1, Bus id2,  String circuit) {
   
    if (line.getCircuit() == null) {
      line.setAttribute(Line.CIRCUIT_KEY,circuit);
    }

    String legacyid = getKey(id1, id2, circuit);
    if (getLegacy(LEGACY_TAG, legacyid) == null) {
      registerLegacy(LEGACY_TAG, legacyid, line);
      line.setAttribute(PFWModelConstants.PFW_LEGACY_ID_KEY, legacyid);
    }
        
    if (line.getAttribute(PFWModelConstants.PFW_LINE_BRANCH_RATING_DESGINATOR_KEY) == null) {
      line.setAttribute(PFWModelConstants.PFW_LINE_BRANCH_RATING_DESGINATOR_KEY, DEFAULT_BRANCH_RATING);
    }

    if (line.getAttribute(PFWModelConstants.PFW_LINE_UNKNOWN_KEY) == null) {
      line.setAttribute(PFWModelConstants.PFW_LINE_UNKNOWN_KEY, DEFAULT_UNKNOWN);
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
      line.addOutputKey(PFWModelConstants.PFW_LEGACY_ID_KEY);
      line.setAttribute(PFWModelConstants.PFW_LEGACY_ID_KEY, legacyId);
      registerLegacy(LEGACY_TAG, legacyId, line);
    }
    return line;
  }

  
}
