package gov.lanl.micot.infrastructure.ep.model.cdf;

import gov.lanl.micot.infrastructure.ep.model.Bus;
import gov.lanl.micot.infrastructure.ep.model.Intertie;
import gov.lanl.micot.infrastructure.ep.model.IntertieFactory;
import gov.lanl.micot.util.geometry.LineImpl;
import gov.lanl.micot.util.geometry.Point;
import gov.lanl.micot.util.geometry.PointImpl;

import java.util.Collection;
import java.util.Vector;

/**
 * A factory class for creating factories
 * @author Russell Bent
 */
public class CDFIntertieFactory extends IntertieFactory {

	private static final String LEGACY_TAG = "CDF";
	
	/**
	 * Constructor
	 */
	protected CDFIntertieFactory() {
	}
	
	/**
	 * Creates an intertie and its state from a PFW file line
	 * @param line
	 * @return
	 * @throws PFWModelException 
	 */
	public Intertie createIntertie(String line, Bus meterBus, Bus nonMeterBus, Collection<Point> points) throws CDFModelException {		
  	Intertie intertie = constructIntertie(line, meterBus, nonMeterBus, points);
  	String legacyid = getKey(meterBus, nonMeterBus, intertie.getCircuit().toString());
  	if (getLegacy(LEGACY_TAG, legacyid) != null) {
  		if (points == null) {
  			intertie.setCoordinates(getLegacy(LEGACY_TAG, legacyid).getCoordinates());
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
  	String circuit = line.substring(20,21);
		
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
    return meterBus.getAttribute(CDFModelConstants.CDF_LEGACY_ID_KEY) + "," + nonMeterBus.getAttribute(CDFModelConstants.CDF_LEGACY_ID_KEY) + "," + circuit;
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
    if (getLegacy(LEGACY_TAG,legacyid) == null) {
      registerLegacy(LEGACY_TAG, legacyid, intertie);
      intertie.setAttribute(CDFModelConstants.CDF_LEGACY_ID_KEY, legacyid);
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
      intertie.addOutputKey(CDFModelConstants.CDF_LEGACY_ID_KEY);
      intertie.setAttribute(CDFModelConstants.CDF_LEGACY_ID_KEY, legacyId);
      registerLegacy(LEGACY_TAG, legacyId, intertie);
    }
    return intertie;
  }

  
}
