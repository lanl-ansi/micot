package gov.lanl.micot.infrastructure.naturalgas.model.impl;

import gov.lanl.micot.infrastructure.naturalgas.model.Junction;
import gov.lanl.micot.infrastructure.naturalgas.model.Reservoir;
import gov.lanl.micot.infrastructure.naturalgas.model.ReservoirFactory;

/**
 * Factory class for creating creating unique reservoirs
 * @author Russell Bent
 */
public class ReservoirFactoryImpl extends ReservoirFactory {

	//private static ReservoirFactoryImpl instance = null;
	
	//public static ReservoirFactoryImpl getInstance() {
		//if (instance == null) {
			//instance = new ReservoirFactoryImpl();
	//	}
	//	return instance;
//	}
	
	/**
	 * Constructor
	 */
	public ReservoirFactoryImpl() {
	}
	
  /**
   * Register the reservoir
   * @param legacyId
   * @param bus
   * @return
   */
  private Reservoir registerReservoir() {
    Reservoir reservoir = createNewReservoir();
    return reservoir;
  }
	
  /**
   * Create the reservoir from scratch
   * @param junction
   * @param capacity
   * @return
   */
  public Reservoir createReservoir(Junction junction, double capacity) {
    Reservoir reservoir = registerReservoir();    
    initializeReservoir(reservoir, junction, capacity);
    return reservoir;
  }
}
