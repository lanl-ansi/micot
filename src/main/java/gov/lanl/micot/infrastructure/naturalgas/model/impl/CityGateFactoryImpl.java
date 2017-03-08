package gov.lanl.micot.infrastructure.naturalgas.model.impl;

import gov.lanl.micot.infrastructure.naturalgas.model.CityGate;
import gov.lanl.micot.infrastructure.naturalgas.model.CityGateFactory;
import gov.lanl.micot.infrastructure.naturalgas.model.Junction;

/**
 * Factory class for creating creating unique city gates
 * @author Russell Bent
 */
public class CityGateFactoryImpl extends CityGateFactory {

//	private static CityGateFactoryImpl instance = null;
	
//	public static CityGateFactoryImpl getInstance() {
	//	if (instance == null) {
		//	instance = new CityGateFactoryImpl();
		//}
		//return instance;
	//}
	
	/**
	 * Constructor
	 */
	public CityGateFactoryImpl() {
	}
	
  /**
   * Register the battery
   * @param legacyId
   * @param bus
   * @return
   */
  private CityGate registerCityGate() {
    CityGate gate = createNewCityGate();
    return gate;
  }
	

  /**
   * Create a city gate from scratch
   * @param junction
   * @param cfd
   * @return
   */
  public CityGate createCityGate(Junction junction, double cfd) {
    CityGate gate = registerCityGate();
    initializeGate(gate, junction, cfd);
    return gate;
  }
}
