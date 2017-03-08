package gov.lanl.micot.infrastructure.naturalgas.model.impl;

import gov.lanl.micot.infrastructure.naturalgas.model.Junction;
import gov.lanl.micot.infrastructure.naturalgas.model.Well;
import gov.lanl.micot.infrastructure.naturalgas.model.WellFactory;

/**
 * Factory class for creating wells
 * @author Russell Bent
 */
public class WellFactoryImpl extends WellFactory {

//	private static WellFactoryImpl instance = null;
	
	//public static WellFactoryImpl getInstance() {
	//	if (instance == null) {
		//	instance = new WellFactoryImpl();
		//}
		//return instance;
	//}
	
	/**
	 * Constructor
	 */
	public WellFactoryImpl() {
	}
	
  /**
   * Register the well
   * @param legacyId
   * @param bus
   * @return
   */
  private Well registerWell() {
    Well well = createNewWell();
    return well;
  }
	
  /**
   * Create the wells
   * @param junction
   * @param minProduction
   * @param maxProduction
   * @return
   */
  public Well createWell(Junction junction, double minProduction, double maxProduction) {
    Well well = registerWell();    
    initializeWell(well, junction, maxProduction);
    well.setMaximumProduction(maxProduction);
    well.setMinimumProduction(minProduction);
    return well;
  }

}
