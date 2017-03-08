package gov.lanl.micot.infrastructure.ep.model.cdf;

import gov.lanl.micot.infrastructure.ep.model.Bus;
import gov.lanl.micot.infrastructure.ep.model.Line;
import gov.lanl.micot.infrastructure.ep.model.LineFactory;
import gov.lanl.micot.infrastructure.ep.model.Transformer;
import gov.lanl.micot.util.geometry.LineImpl;
import gov.lanl.micot.util.geometry.Point;
import gov.lanl.micot.util.geometry.PointImpl;

import java.util.Collection;
import java.util.Vector;

/**
 * Factory class for creating PFWLines and ensuring their uniqueness
 * @author Russell Bent
 */
public class CDFLineFactory extends LineFactory {

	//private static CDFLineFactory instance = null;
	private static final String LEGACY_TAG = "CDF";
	
	//public static CDFLineFactory getInstance() {
	//	if (instance == null) {
		//	instance = new CDFLineFactory();
	//	}
	//	return instance;
	//}
	
	/**
	 * Constructor
	 */
	protected CDFLineFactory() {
	}

	/**
	 * Determine the unique key
	 * @param fromBus
	 * @param toBus
	 * @param circuit
	 * @return
	 */
	 private String getKey(Bus fromBus, Bus toBus, String circuit) {
	   int from = fromBus.getAttribute(CDFModelConstants.CDF_LEGACY_ID_KEY,Integer.class);
	   int to = toBus.getAttribute(CDFModelConstants.CDF_LEGACY_ID_KEY,Integer.class);    
	   return from + "," + to + "," + circuit;    
	 }
	
	/**
	 * Creates a line and its state from a PFW file line
	 * @param line
	 * @return
	 * @throws PFWModelException 
	 */
	public Line createLine(String line, Bus fromBus, Bus toBus, Collection<Point> points) throws CDFModelException {	
		Line pair = constructLine(line, fromBus, toBus, points);		
  	if (getLegacy(LEGACY_TAG, getKey(fromBus, toBus, pair.getCircuit().toString())) != null) {
  		if (points == null) {
  			pair.setCoordinates(getLegacy(LEGACY_TAG, getKey(fromBus, toBus, pair.getCircuit().toString())).getCoordinates());
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
	  
		String circuit = line.substring(16,17);	
		double resistance = Double.parseDouble(line.substring(20,29).trim());
		double reactance = Double.parseDouble(line.substring(30,40).trim());
		double charging = Double.parseDouble(line.substring(41,50).trim());
		int rating1 = Integer.parseInt(line.substring(51,55).trim());
		int rating2 = Integer.parseInt(line.substring(57,61).trim());
		int rating3 = Integer.parseInt(line.substring(63,67).trim());
		boolean status = true;

  	// check to see if the area already exists
		Line pfwLine = registerLine(fromBus, toBus, circuit);
    pfwLine.setAttribute(Line.CIRCUIT_KEY, circuit);
    pfwLine.setResistance(resistance);
    pfwLine.setReactance(reactance);
    pfwLine.setLineCharging(charging);
    pfwLine.setCapacityRating(rating1);
    pfwLine.setShortTermEmergencyCapacityRating(rating2);
    pfwLine.setLongTermEmergencyCapacityRating(rating3);
    pfwLine.setDesiredStatus(status);
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
	
	/*@Override
	public Line createLine(Transformer transformer, Bus fromBus, Bus toBus) {		
	  String circuit = transformer.getCircuit().toString();
    Line pfwLine = registerLine(fromBus, toBus, circuit);	  
    pfwLine.setAttribute(Line.CIRCUIT_KEY, transformer.getCircuit());
    pfwLine.setResistance(transformer.getResistance());
    pfwLine.setReactance(transformer.getReactance());
    pfwLine.setLineCharging(transformer.getLineCharging());
    pfwLine.setCapacityRating(transformer.getCapacityRating());
    pfwLine.setShortTermEmergencyCapacityRating(transformer.getShortTermEmergencyCapacityRating());
    pfwLine.setLongTermEmergencyCapacityRating(transformer.getLongTermEmergencyCapacityRating());
    pfwLine.setDesiredStatus(transformer.getDesiredStatus());
    pfwLine.setActualStatus(pfwLine.getDesiredStatus());
		pfwLine.setCoordinates(transformer.getCoordinates());
		
		return pfwLine;
	}*/
	
	@Override
	protected Line createEmptyLine(Bus fromBus, Bus toBus, Transformer transformer) {
    Line line = registerLine(fromBus, toBus, transformer.getCircuit().toString());   
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
    pfwLine.setActualStatus(pfwLine.getDesiredStatus());
		
	  pfwLine.setCoordinates(((Line)line).getCoordinates());
	  return pfwLine;	  
	}

	/*@Override
	public Line createLine(ElectricPowerNode node1, ElectricPowerNode node2, double reactance, double resistance, double charging, double capacity, Object c) {						
		Bus from = node1.getBus();
    Bus to = node2.getBus();    
		String circuit = c.toString(); 
		
		int rating1 = (int)capacity; 
		int rating2 = (int)capacity; 
		int rating3 = (int)capacity; 
		boolean status = true;
		
		Line pfwLine = registerLine(from, to, circuit);
    pfwLine.setAttribute(Line.CIRCUIT_KEY, circuit);
    pfwLine.setResistance(resistance);
    pfwLine.setReactance(reactance);
    pfwLine.setLineCharging(charging);
    pfwLine.setCapacityRating(rating1);
    pfwLine.setShortTermEmergencyCapacityRating(rating2);
    pfwLine.setLongTermEmergencyCapacityRating(rating3);
    pfwLine.setDesiredStatus(status);
    pfwLine.setActualStatus(status);
        
    Vector<Point> points = new Vector<Point>();
    points.add(node1.getBus().getCoordinate());
    points.add(node2.getBus().getCoordinate());
    pfwLine.setCoordinates(new LineImpl(points.toArray(new Point[0])));

    return pfwLine;    
	}*/

	@Override
  protected Line createEmptyLine(Bus fromBus, Bus toBus, Object circuit) {
    Line line = registerLine(fromBus, toBus, circuit.toString());   
    return line;
  }
	
  /**
   * Creates a bus from another bus
   * @param bus
   * @return
   */
  public void updateLine(Line line, Bus fromBus, Bus toBus, String circuit) {
    if (line.getCircuit() == null) {
      line.setAttribute(Line.CIRCUIT_KEY,circuit);
    }

    if (getLegacy(LEGACY_TAG, getKey(fromBus, toBus, circuit)) == null) {
      line.setAttribute(CDFModelConstants.CDF_LEGACY_ID_KEY, getKey(fromBus, toBus, circuit));
      registerLegacy(LEGACY_TAG, getKey(fromBus, toBus, circuit), line);
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
      line.addOutputKey(CDFModelConstants.CDF_LEGACY_ID_KEY);
      line.setAttribute(CDFModelConstants.CDF_LEGACY_ID_KEY, legacyId);
      registerLegacy(LEGACY_TAG, legacyId, line);
    }
    return line;
  }

  
}
