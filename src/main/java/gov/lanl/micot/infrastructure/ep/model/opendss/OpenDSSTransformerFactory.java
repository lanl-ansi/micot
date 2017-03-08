package gov.lanl.micot.infrastructure.ep.model.opendss;

import java.util.StringTokenizer;
import java.util.Vector;

import gov.lanl.micot.infrastructure.ep.io.opendss.OpenDSSIOConstants;
import gov.lanl.micot.infrastructure.ep.model.Bus;
import gov.lanl.micot.infrastructure.ep.model.Line;
import gov.lanl.micot.infrastructure.ep.model.Transformer;
import gov.lanl.micot.infrastructure.ep.model.TransformerFactory;
import gov.lanl.micot.util.geometry.LineImpl;
import gov.lanl.micot.util.geometry.Point;
import gov.lanl.micot.util.io.dcom.ComObject;

/**
 * Factory for creating open dss transformers
 * @author Russell Bent
 */
public class OpenDSSTransformerFactory extends TransformerFactory {

  private static final String LEGACY_TAG = "OPENDSS";
  
  /**
   * Singleton constructor
   */
  protected OpenDSSTransformerFactory() {    
  }
  
  @Override
  protected Transformer createEmptyTransformer(Bus bus1, Bus bus2, Object circuit) {
    Transformer transformer = registerTransformer(findUnusedId());   
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
    throw new RuntimeException("Error: Ran out of OpenDSS ids");    
  }

  

  /**
   * Create a transformer with a specific id
   * @param transformer
   * @param data
   * @param makeCircuitId
   * @return
   */
	protected Transformer createLineWithId(Transformer transformer, Bus fromBus, Bus toBus, String makeCircuitId) {
	  throw new RuntimeException("OpenDSSTransformerFactory::createLineWithID");
	  // TODO
	}
	
	/**
   * Creates a transformer and data from a transformer
   * @param line
   * @return
   */
  public Transformer createTransformer(ComObject objTransformer, Bus fromBus, Bus toBus, ComObject activeTransformer)  {    
    String legacyid = objTransformer.getString(OpenDSSIOConstants.TRANSFORMER_NAME);    
    Transformer transformer = registerTransformer(legacyid);   
    
    boolean status = activeTransformer.getBoolean(OpenDSSIOConstants.TRANSFORMER_STATUS);
    double rating = objTransformer.getDouble(OpenDSSIOConstants.TRANSFORMER_CAPACITY);
    double resistance = objTransformer.getDouble(OpenDSSIOConstants.TRANSFORMER_RESISTANCE);
    double reactance = 0.0; 
    
    
    // TODO get extra fields in, and add the missing fields, also like off diagnol resistance terms
    
    transformer.setCapacityRating(rating);
    transformer.setLongTermEmergencyCapacityRating(rating);    
    transformer.setReactance(reactance); 
    transformer.setResistance(resistance);
    transformer.setShortTermEmergencyCapacityRating(rating);
    transformer.setDesiredStatus(status);
    transformer.setActualStatus(status);

    
    ComObject property = activeTransformer.call(OpenDSSIOConstants.PROPERTIES, OpenDSSIOConstants.TRANSFORMER_BUSES);     
    String busNames = property.getString(OpenDSSIOConstants.PROPERTY_VALUE);
    busNames = busNames.substring(1,busNames.length()-2);    
    StringTokenizer tokenizer = new StringTokenizer(busNames, ",");
    String fromBusName = tokenizer.nextToken().trim();
    int numberOfPhases = 0;
    boolean carriesPhaseA = false;
    boolean carriesPhaseB = false;
    boolean carriesPhaseC = false;
    int idx = fromBusName.indexOf(".");
    if (idx == -1) {
      carriesPhaseA = carriesPhaseB = carriesPhaseC = true;
      numberOfPhases = 3;
    }
    else {
      String tmp = fromBusName.substring(idx,fromBusName.length());
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
    transformer.setAttribute(Transformer.IS_PHASE_A_KEY,carriesPhaseA);
    transformer.setAttribute(Transformer.IS_PHASE_B_KEY,carriesPhaseB);
    transformer.setAttribute(Transformer.IS_PHASE_C_KEY,carriesPhaseC);
    transformer.setAttribute(Transformer.NUMBER_OF_PHASES_KEY,numberOfPhases);
    transformer.setAttribute(Transformer.LENGTH_KEY, 1.0);
    
    
    transformer.setAttribute(Line.RESISTANCE_PHASE_A_KEY,resistance);
    transformer.setAttribute(Line.RESISTANCE_PHASE_AB_KEY,0.0);
    transformer.setAttribute(Line.RESISTANCE_PHASE_AC_KEY,0.0);
    transformer.setAttribute(Line.RESISTANCE_PHASE_BA_KEY,0.0);
    transformer.setAttribute(Line.RESISTANCE_PHASE_B_KEY,resistance);
    transformer.setAttribute(Line.RESISTANCE_PHASE_BC_KEY,0.0);
    transformer.setAttribute(Line.RESISTANCE_PHASE_CA_KEY,0.0);
    transformer.setAttribute(Line.RESISTANCE_PHASE_CB_KEY,0.0);
    transformer.setAttribute(Line.RESISTANCE_PHASE_C_KEY,resistance);

    transformer.setAttribute(Line.REACTANCE_PHASE_A_KEY,reactance);
    transformer.setAttribute(Line.REACTANCE_PHASE_AB_KEY,0.0);
    transformer.setAttribute(Line.REACTANCE_PHASE_AC_KEY,0.0);
    transformer.setAttribute(Line.REACTANCE_PHASE_BA_KEY,0.0);
    transformer.setAttribute(Line.REACTANCE_PHASE_B_KEY,reactance);
    transformer.setAttribute(Line.REACTANCE_PHASE_BC_KEY,0.0);
    transformer.setAttribute(Line.REACTANCE_PHASE_CA_KEY,0.0);
    transformer.setAttribute(Line.REACTANCE_PHASE_CB_KEY,0.0);
    transformer.setAttribute(Line.REACTANCE_PHASE_C_KEY,reactance);

    
    
    Vector<Point> points = new Vector<Point>();
    points.add(fromBus.getCoordinate());
    points.add(toBus.getCoordinate());
    transformer.setCoordinates(new LineImpl(points));    
    return transformer;
  }
  
  /**
   * Creates an ieiss transformer from another transformer
   * @param transformer
   * @param transformerData
   * @param id
   * @param id2
   * @return
   */
	public Transformer createTransformer(Transformer transformer, Bus id1, Bus id2) {	  
	  throw new RuntimeException("OpenDSSTransformerFactory::createTransformer");
	  // TODO
	  
	}

  /**
   * Register the transformer
   * @param legacyId
   * @param bus
   * @return
   */
  private Transformer registerTransformer(String legacyId) {
    Transformer transformer = getLegacy(LEGACY_TAG, legacyId);
    if (transformer == null) {
      transformer = createNewTransformer();
      transformer.setAttribute(OpenDSSModelConstants.OPENDSS_LEGACY_ID_KEY,legacyId);
      transformer.addOutputKey(OpenDSSModelConstants.OPENDSS_LEGACY_ID_KEY);
      registerLegacy(LEGACY_TAG, legacyId, transformer);
    }
    return transformer;
  }

  /**
   * Create a transformer from a piece of text
   * @param transformerData
   * @param frombus
   * @param tobus
   * @param linecode
   * @return
   */
  public Transformer createTransformer(String transformerData, Bus frombus, Bus tobus, String linecode) {
	transformerData = transformerData.replace("\"", "");
    String legacyid = OpenDSSIOConstants.getData(transformerData,"Transformer", ".");  
    Transformer transformer = registerTransformer(legacyid);   

    // TODO hacks in the following!
    boolean status = true;
    double rating = 1e+5;
    double resistance = .001;
    double reactance = .001;
    
    // TODO get extra fields in, and add the missing fields, also like off diagnol resistance terms
    
    transformer.setCapacityRating(rating);
    transformer.setLongTermEmergencyCapacityRating(rating);    
    transformer.setReactance(reactance); 
    transformer.setResistance(resistance);
    transformer.setShortTermEmergencyCapacityRating(rating);
    transformer.setDesiredStatus(status);
    transformer.setActualStatus(status);

    String fromBusName = frombus.getAttribute(Bus.NAME_KEY, String.class);
    int numberOfPhases = 0;
    boolean carriesPhaseA = false;
    boolean carriesPhaseB = false;
    boolean carriesPhaseC = false;
    int idx = fromBusName.indexOf(".");
    if (idx == -1) {
      carriesPhaseA = carriesPhaseB = carriesPhaseC = true;
      numberOfPhases = 3;
    }
    else {
      String tmp = fromBusName.substring(idx,fromBusName.length());
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
    transformer.setAttribute(Transformer.IS_PHASE_A_KEY,carriesPhaseA);
    transformer.setAttribute(Transformer.IS_PHASE_B_KEY,carriesPhaseB);
    transformer.setAttribute(Transformer.IS_PHASE_C_KEY,carriesPhaseC);
    transformer.setAttribute(Transformer.NUMBER_OF_PHASES_KEY,numberOfPhases);
        
    Vector<Point> points = new Vector<Point>();
    points.add(frombus.getCoordinate());
    points.add(tobus.getCoordinate());
    transformer.setCoordinates(new LineImpl(points));    
    return transformer;	  
  }

  /**
   * Returns the value of field after "=" until " "
   * @param str
   * @param field
   * @return
   */  
  public String getField(String str, String field) {
	int idx_field = str.indexOf(field);
	String sub1 = str.substring(idx_field+1, str.length());
	int idx_equals = idx_field + sub1.indexOf("=");
	String sub2 = str.substring(idx_equals+1, str.length());
	int idx_space = idx_equals + (sub2.indexOf(" ") >= 0 ? sub2.indexOf(" ") : sub2.length());
	return str.substring(idx_equals+2, idx_space+1);
  }  


}
