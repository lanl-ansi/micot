package gov.lanl.micot.infrastructure.ep.model.pfw;

import gov.lanl.micot.infrastructure.ep.model.Bus;
import gov.lanl.micot.infrastructure.ep.model.Intertie;
import gov.lanl.micot.infrastructure.ep.model.IntertieFactory;
import gov.lanl.micot.util.geometry.LineImpl;
import gov.lanl.micot.util.geometry.Point;
import gov.lanl.micot.util.geometry.PointImpl;

import java.util.Collection;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * A factory class for creating factories
 * @author Russell Bent
 */
public class PFWIntertieFactory extends IntertieFactory {

	private static final String LEGACY_TAG = "PFW";
	
	/**
	 * Constructor
	 */
	protected PFWIntertieFactory() {
	}
	
	/**
	 * Creates an intertie and its state from a PFW file line
	 * @param line
	 * @return
	 * @throws PFWModelException 
	 */
	public Intertie createIntertie(String line, Bus meterBus, Bus nonMeterBus, Collection<Point> points) throws PFWModelException {		
  	Intertie intertie = constructIntertie(line, meterBus, nonMeterBus, points);
  	String legacyid = getKey(meterBus, nonMeterBus, intertie.getCircuit().toString());
  	if (getLegacy(LEGACY_TAG,legacyid) != null) {
  		if (points == null) {
  			intertie.setCoordinates(getLegacy(LEGACY_TAG,legacyid).getCoordinates());
  		}  	
  	}
  	return intertie;
	}
	
	/**
	 * Constructs the intertie
	 * @param line
	 * @return
	 */
	private Intertie constructIntertie(String line, Bus meterBus, Bus nonMeterBus, Collection<Point> points) {
		// parse the information
		StringTokenizer tokenizer = new StringTokenizer(line,",");
  	/*int meteredBus = */Integer.parseInt(tokenizer.nextToken().trim());
  	/*int meteredArea = */Integer.parseInt(tokenizer.nextToken().trim());
  	/*int nonMeteredBus = */Integer.parseInt(tokenizer.nextToken().trim());
  	/*int nonMeteredArea = */Integer.parseInt(tokenizer.nextToken().trim());
  	String circuit = tokenizer.nextToken();
  	while (!circuit.endsWith("\"")) {
  		circuit = circuit + "," + tokenizer.nextToken();
  	}
		
  	// check to see if the area already exists
  	Intertie intertie = registerIntertie(meterBus, nonMeterBus, circuit);
    intertie.setAttribute(Intertie.CIRCUIT_KEY, circuit);
    intertie.setStatus(true);
  	
  	if (points == null) {
			points = new Vector<Point>();
    	points.add(new PointImpl(0, 0));
    	points.add(new PointImpl(0, 0));
		}
		intertie.setCoordinates(new LineImpl(points.toArray(new Point[0])));
  	return intertie;
	}
	
	 /**
   * Create the unique key
   * @param line
   * @return
   */
  private String getKey(Bus meterBus, Bus nonMeterBus, String circuit) {
    return meterBus.getAttribute(PFWModelConstants.PFW_LEGACY_ID_KEY) + "," + meterBus.getAttribute(PFWModelConstants.PFW_LEGACY_ID_KEY) + "," + circuit;
  }


  /**
   * Create an intertie filled with the apprprioate information
   * @param intertie
   * @param id1
   * @param id2
   * @param circuit
   * @return
   */
  public void updateIntertie(Intertie intertie, Bus id1, Bus id2, String circuit) {
    if (intertie.getCircuit() == null) {
      intertie.setAttribute(Intertie.CIRCUIT_KEY,circuit);
    }

    String legacyid = getKey(id1, id2, circuit);
    if (getLegacy(LEGACY_TAG, legacyid) == null) {
      registerLegacy(LEGACY_TAG, legacyid, intertie);
      intertie.setAttribute(PFWModelConstants.PFW_LEGACY_ID_KEY, legacyid);
    }
  }
 

  /**
   * Register the intertie
   * 
   * @param legacyId
   * @param bus
   * @return
   */
  private Intertie registerIntertie(Bus meterBus, Bus nonMeterBus, String circuit) {
    String legacyId = getKey(meterBus, nonMeterBus, circuit);
    Intertie intertie = getLegacy(LEGACY_TAG, legacyId);
    if (intertie == null) {
      intertie = createNewIntertie();
      intertie.addOutputKey(PFWModelConstants.PFW_LEGACY_ID_KEY);
      intertie.setAttribute(PFWModelConstants.PFW_LEGACY_ID_KEY, legacyId);
      registerLegacy(LEGACY_TAG, legacyId, intertie);
    }
    return intertie;
  }

  
}
