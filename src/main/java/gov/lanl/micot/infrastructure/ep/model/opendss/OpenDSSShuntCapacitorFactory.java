package gov.lanl.micot.infrastructure.ep.model.opendss;

import gov.lanl.micot.infrastructure.ep.model.Bus;
import gov.lanl.micot.infrastructure.ep.model.ShuntCapacitor;
import gov.lanl.micot.infrastructure.ep.model.ShuntCapacitorFactory;
import gov.lanl.micot.util.io.dcom.ComObject;

/**
 * Factory for creating a shunt capacitor
 * @author Russell Bent
 */
public class OpenDSSShuntCapacitorFactory extends ShuntCapacitorFactory {

//	private static OpenDSSShuntCapacitorFactory INSTANCE = null;
	private static final String LEGACY_TAG = "OPENDSS";
	
	//public static OpenDSSShuntCapacitorFactory getInstance() {
		//if (INSTANCE == null) {
			//INSTANCE = new OpenDSSShuntCapacitorFactory();
		//}
		//return INSTANCE;
	//}
		
	/**
	 * Constructor
	 */
	protected OpenDSSShuntCapacitorFactory() {	
	}
	
  /**
   * Creates a capacitor and data from a capacitor
   * @param shunt
   * @return
   */
  public ShuntCapacitor createShuntCapacitor(ComObject iShunt, Bus bus, ComObject activeShunt)  {
    throw new RuntimeException("OpenDSSShuntCapacitorFactory::createShuntCapacitor");
    // TODO    
  }
  
  /**
  * Register the shunt capacitor
  * @param legacyId
  * @param bus
  * @return
  */
  private ShuntCapacitor registerCapacitor(String legacyId, Bus bus) {
    ShuntCapacitor capacitor = getLegacy(LEGACY_TAG, legacyId);

    if (capacitor == null) {
      capacitor = createNewShuntCapacitor();
      capacitor.setAttribute(OpenDSSModelConstants.OPENDSS_LEGACY_ID_KEY,legacyId);
      capacitor.addOutputKey(OpenDSSModelConstants.OPENDSS_LEGACY_ID_KEY);      
      registerLegacy(LEGACY_TAG, legacyId, capacitor);
    }
   return capacitor;
 }

}
