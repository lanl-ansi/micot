package gov.lanl.micot.application.lpnorm.model;

import gov.lanl.micot.infrastructure.ep.model.BatteryFactory;

/**
 * Factory class for creating LP NORM batteries
 * @author Russell Bent
 */
public class LPNormBatteryFactory extends BatteryFactory {

  private static final String LEGACY_TAG = "LPNORM";
	
	/**
	 * Constructor
	 */
	protected LPNormBatteryFactory() {
	}		
}
