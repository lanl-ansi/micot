package gov.lanl.micot.infrastructure.ep.model.opendss;

import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Vector;

import gov.lanl.micot.infrastructure.ep.io.opendss.OpenDSSIOConstants;
import gov.lanl.micot.infrastructure.ep.model.Bus;
import gov.lanl.micot.infrastructure.ep.model.Line;
import gov.lanl.micot.infrastructure.ep.model.LineFactory;
import gov.lanl.micot.infrastructure.ep.model.Transformer;
import gov.lanl.micot.util.geometry.LineImpl;
import gov.lanl.micot.util.geometry.Point;
import gov.lanl.micot.util.io.dcom.ComObject;

/**
 * A factory for generating lines
 * @author Russell Bent
 *
 */
public class OpenDSSLineFactory extends LineFactory {

  private static final String LEGACY_TAG = "OPENDSS";
	  
  /**
   * Singleton constructor
   */
  protected OpenDSSLineFactory() {  
  }
  
  @Override
  protected Line createEmptyLine(Bus fromBus, Bus toBus, Transformer transformer) {
    Line line = registerLine(findUnusedId());   
    return line;
  }

  @Override
  protected Line createEmptyLine(Bus fromBus, Bus toBus, Object circuit) {
    Line line = registerLine(findUnusedId());   
    return line;
  }  
  /**
   * Create a line with a particular id
   * @param link
   * @param data
   * @param makeCircuitId
   * @return
   */
	protected Line createLineWithId(Line link, Bus fromBus, Bus toBus, String makeCircuitId) {
	  throw new RuntimeException("OpenDSSLineFactory::createLineWithId");
	  // TODO	  
	}
		
	/**
   * Creates a line and data from a line
   * @param line
   * @return
   */
  public Line createLine(ComObject objLine, Bus fromBus, Bus toBus, ComObject activeLine)  {
    String legacyid = objLine.getString(OpenDSSIOConstants.LINE_NAME);    
    Line line = registerLine(legacyid);   

    // TODO ... get the capacity calculations correct... some dicrepancies about what baseKV means in a 3 phase system., e.e. a 24.9 kv
    // system somehow is 14.4 in a 3 phase system.... we need to get the 3 phase terms of the resistance/reactance as well
    
    double baseKV = fromBus.getSystemVoltageKV();
    double ampsRating = objLine.getDouble(OpenDSSIOConstants.LINE_CAPACITY);
    double emergAmpsRating = objLine.getDouble(OpenDSSIOConstants.LINE_CAPACITY);
    double capacity = ampsRating * 14.4 / 1000.0; // HACK!!! baseKV; TODO Fix this... need to figure out where to get the data
    double emergencyCapacity = emergAmpsRating * baseKV;
    boolean status = activeLine.getBoolean(OpenDSSIOConstants.LINE_STATUS);
    double length = objLine.getDouble(OpenDSSIOConstants.LINE_LENGTH);

    ArrayList<Double> rmatrix = objLine.getDoubleArray(OpenDSSIOConstants.LINE_RMATRIX);
    ArrayList<Double> xmatrix = objLine.getDoubleArray(OpenDSSIOConstants.LINE_XMATRIX);
    
    int numberOfPhases = 3;
    String bus1PhaseName = objLine.getString(OpenDSSIOConstants.LINE_FROM_BUS);
    boolean carriesPhaseA = false;
    boolean carriesPhaseB = false;
    boolean carriesPhaseC = false;
    int idx = bus1PhaseName.indexOf(".");
    if (idx == -1) {
      carriesPhaseA = carriesPhaseB = carriesPhaseC = true;
    }
    else {
      String tmp = bus1PhaseName.substring(idx,bus1PhaseName.length());
      if (tmp.contains("1")) {
        carriesPhaseA = true;
        ++numberOfPhases;
      }
      if (tmp.contains("2")) {
        carriesPhaseB = true;
        ++numberOfPhases;
      }
      if (tmp.contains("3")) {
        carriesPhaseC = true;
        ++numberOfPhases;
      }
    }
    
   // Resistance = R * (MVA*10^6) / (V_LL * 10^3)^2
    double ohmsConversion = (100000.0) / (230.0 * 230.0 * 1000.0);    
    
    // TODO set other attributes, and missing attributes    
    line.setCapacityRating(capacity);
    line.setLongTermEmergencyCapacityRating(emergencyCapacity);    
    line.setAttribute(Line.RESISTANCE_PHASE_A_KEY,rmatrix.get(0) * length  * ohmsConversion);
    line.setAttribute(Line.RESISTANCE_PHASE_AB_KEY,rmatrix.get(1) * length * ohmsConversion );
    line.setAttribute(Line.RESISTANCE_PHASE_AC_KEY,rmatrix.get(2) * length * ohmsConversion);
    line.setAttribute(Line.RESISTANCE_PHASE_BA_KEY,rmatrix.get(3) * length * ohmsConversion);
    line.setAttribute(Line.RESISTANCE_PHASE_B_KEY,rmatrix.get(4) * length  * ohmsConversion);
    line.setAttribute(Line.RESISTANCE_PHASE_BC_KEY,rmatrix.get(5) * length * ohmsConversion);
    line.setAttribute(Line.RESISTANCE_PHASE_CA_KEY,rmatrix.get(6) * length * ohmsConversion);
    line.setAttribute(Line.RESISTANCE_PHASE_CB_KEY,rmatrix.get(7) * length * ohmsConversion);
    line.setAttribute(Line.RESISTANCE_PHASE_C_KEY,rmatrix.get(8) * length  * ohmsConversion);

    line.setAttribute(Line.REACTANCE_PHASE_A_KEY,xmatrix.get(0) * length  * ohmsConversion);
    line.setAttribute(Line.REACTANCE_PHASE_AB_KEY,xmatrix.get(1) * length * ohmsConversion);
    line.setAttribute(Line.REACTANCE_PHASE_AC_KEY,xmatrix.get(2) * length * ohmsConversion);
    line.setAttribute(Line.REACTANCE_PHASE_BA_KEY,xmatrix.get(3) * length * ohmsConversion);
    line.setAttribute(Line.REACTANCE_PHASE_B_KEY,xmatrix.get(4) * length  * ohmsConversion);
    line.setAttribute(Line.REACTANCE_PHASE_BC_KEY,xmatrix.get(5) * length * ohmsConversion);
    line.setAttribute(Line.REACTANCE_PHASE_CA_KEY,xmatrix.get(6) * length * ohmsConversion);
    line.setAttribute(Line.REACTANCE_PHASE_CB_KEY,xmatrix.get(7) * length * ohmsConversion);
    line.setAttribute(Line.REACTANCE_PHASE_C_KEY,xmatrix.get(8) * length  * ohmsConversion);
        
    line.setShortTermEmergencyCapacityRating(emergencyCapacity);
    line.setDesiredStatus(status);
    line.setActualStatus(status);
    line.setAttribute(Line.LENGTH_KEY, length);
    line.setAttribute(Line.NUMBER_OF_PHASES_KEY,numberOfPhases);
    line.setAttribute(Line.HAS_PHASE_A_KEY,carriesPhaseA);
    line.setAttribute(Line.HAS_PHASE_B_KEY,carriesPhaseB);
    line.setAttribute(Line.HAS_PHASE_C_KEY,carriesPhaseC);
           
    Vector<Point> points = new Vector<Point>();
    points.add(fromBus.getCoordinate());
    points.add(toBus.getCoordinate());
    line.setCoordinates(new LineImpl(points));    
    return line;
  }
  
  /**
   * Creates an ieiss line from another line
   * @param line
   * @param id
   * @param id2
   * @return
   */
  public Line createLine(Line line, Bus id1, Bus id2) {
    throw new RuntimeException("OpenDSSLineFactory::createLine");
    // TODO
  }

  /**
   * Register the line
   * @param legacyId
   * @param bus
   * @return
   */
  private Line registerLine(String legacyId) {
    Line line = getLegacy(LEGACY_TAG, legacyId);
    if (line == null) {
      line = createNewLine();
      line.setAttribute(OpenDSSModelConstants.OPENDSS_LEGACY_ID_KEY,legacyId);
      line.addOutputKey(OpenDSSModelConstants.OPENDSS_LEGACY_ID_KEY);
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

  /**
   * Create a line from text data
   * @param lineData
   * @param frombus
   * @param tobus
   * @param linecode
   * @return
   */
  public Line createLine(String lineData, Bus fromBus, Bus toBus, String linecode) {
	lineData = lineData.replace("\"", "");
	String legacyid = OpenDSSIOConstants.getData(lineData,"Line", ".");
	Line line = registerLine(legacyid); 
	
    double baseKV = fromBus.getSystemVoltageKV();
    double ampsRating = 0.0;
    double emergAmpsRating = 0.0;
    double length = 0.0;
    int numberOfPhases = 3;
    if (lineData.contains("normamps")) {
    	ampsRating = Double.parseDouble(OpenDSSIOConstants.getData(lineData,"normamps", "="));
    }
    if (lineData.contains("emergamps")) {
    	emergAmpsRating = Double.parseDouble(OpenDSSIOConstants.getData(lineData,"emergamps", "="));
    }
    double capacity = ampsRating * 14.4 / 1000.0; // HACK!!! baseKV; TODO Fix this... need to figure out where to get the data
    double emergencyCapacity = emergAmpsRating * baseKV;
    boolean status = true; // HACK!!! TODO pull it from somewhere
    if (lineData.contains("length")) {
    	length = Double.parseDouble(OpenDSSIOConstants.getData(lineData,"length", "="));
    }
    Vector<Vector<String>> rmat = new Vector<Vector<String>>();
    Vector<Vector<String>> xmat = new Vector<Vector<String>>();    
    if (lineData.contains("rmatrix")) {
	    String rmatrix = OpenDSSIOConstants.getData(lineData, "rmatrix", "=");
	    String xmatrix = OpenDSSIOConstants.getData(lineData, "xmatrix", "=");
	    rmatrix = rmatrix.substring(1, rmatrix.length()-1);
	    xmatrix = xmatrix.substring(1, xmatrix.length()-1);
	    // Multiple phases
	    if (rmatrix.contains("|")) {
	    	StringTokenizer Rtokenizer = new StringTokenizer(rmatrix, "|");
	    	StringTokenizer Xtokenizer = new StringTokenizer(rmatrix, "|");
	  	  	while (Rtokenizer.hasMoreTokens()) {
	  	  		String token = Rtokenizer.nextToken();
	  	  		StringTokenizer subTokenizer = new StringTokenizer(token);
	  	  		Vector<String> Rrow = new Vector<String>();
	  	  		while (subTokenizer.hasMoreTokens()) {
	  	  			Rrow.add(subTokenizer.nextToken());
	  	  		}
	  	  		rmat.add(Rrow);
	  	  	}
	  	  	while (Xtokenizer.hasMoreTokens()) {
	  	  		String token = Xtokenizer.nextToken();
	  	  		StringTokenizer subTokenizer = new StringTokenizer(token);
	  	  		Vector<String> Xrow = new Vector<String>();
	  	  		while (subTokenizer.hasMoreTokens()) {
	  	  			Xrow.add(subTokenizer.nextToken());
	  	  		}
	  	  		xmat.add(Xrow);
	  	  	}
	    // Single phase
	    } else {
	    	Vector<String> Rrow = new Vector<String>();
	    	Vector<String> Xrow = new Vector<String>();
	    	Rrow.add(rmatrix);
	    	Xrow.add(xmatrix);
	    	rmat.add(Rrow);
	    	xmat.add(Xrow);
	    }
    }
    if (lineData.contains("phases")) { 
    	numberOfPhases = Integer.parseInt(OpenDSSIOConstants.getData(lineData,"phases", "="));
    }
    
    double ohmsConversion = (100000.0) / (230.0 * 230.0 * 1000.0);    
    double resistancePerLength = 0.0;
    double reactancePerLength = 0.0;
    if (lineData.contains("rmatrix")) {
	    for (int i = 0; i < numberOfPhases; ++i) {
	    	resistancePerLength += Double.parseDouble(rmat.get(i).get(i));
	    	reactancePerLength += Double.parseDouble(xmat.get(i).get(i));
	    }
	    resistancePerLength = resistancePerLength/numberOfPhases;
	    reactancePerLength = reactancePerLength/numberOfPhases;
    }
    double resistance = resistancePerLength * length * ohmsConversion;
    double reactance = reactancePerLength * length * ohmsConversion;    
    String bus1PhaseName = fromBus.getAttribute("OPENDSS_LEGACY_ID", String.class);
    boolean carriesPhaseA = false;
    boolean carriesPhaseB = false;
    boolean carriesPhaseC = false;
    
    int idx = bus1PhaseName.indexOf(".");
    if (idx == -1) {
      carriesPhaseA = carriesPhaseB = carriesPhaseC = true;
    }
    else {
      String tmp = bus1PhaseName.substring(idx,bus1PhaseName.length());
      if (tmp.contains("1")) {
        carriesPhaseA = true;
      }
      if (tmp.contains("2")) {
        carriesPhaseB = true;
      }
      if (tmp.contains("3")) {
        carriesPhaseC = true;
      }
    }
        
    
    // TODO set other attributes, and missing attributes
    
    line.setCapacityRating(capacity);
    line.setLongTermEmergencyCapacityRating(emergencyCapacity);    
    line.setReactance(reactance); 
    line.setResistance(resistance);
    line.setShortTermEmergencyCapacityRating(emergencyCapacity);
    line.setDesiredStatus(status);
    line.setActualStatus(status);
    line.setAttribute(Line.LENGTH_KEY, length);
    line.setAttribute(Line.NUMBER_OF_PHASES_KEY,numberOfPhases);
    line.setAttribute(Line.HAS_PHASE_A_KEY,carriesPhaseA);
    line.setAttribute(Line.HAS_PHASE_B_KEY,carriesPhaseB);
    line.setAttribute(Line.HAS_PHASE_C_KEY,carriesPhaseC);
           
    Vector<Point> points = new Vector<Point>();
    points.add(fromBus.getCoordinate());
    points.add(toBus.getCoordinate());
    line.setCoordinates(new LineImpl(points));    
    return line;
  }

}
