package gov.lanl.micot.infrastructure.coupled.model.impl;

import gov.lanl.micot.infrastructure.coupled.model.Coupling;
import gov.lanl.micot.infrastructure.coupled.model.CouplingFactory;
import gov.lanl.micot.infrastructure.model.Asset;

/**
 * Factory class for creating default couplings
 * @author Russell Bent
 */
public class CouplingFactoryImpl extends CouplingFactory {

//	private static CouplingFactoryImpl instance = null;
	
//	public static CouplingFactoryImpl getInstance() {
	//	if (instance == null) {
		//	instance = new CouplingFactoryImpl();
		//}
		//return instance;
	//}
	
	/**
	 * Constructor
	 */
	protected CouplingFactoryImpl() {
	}


  /**
   * Creates a coupling from two components
   * @param master
   * @param dependent
   * @return
   */
  public Coupling createCoupling(Asset master, Asset dependent) {
    return createNewCoupling();
  }

  
  
  
}
