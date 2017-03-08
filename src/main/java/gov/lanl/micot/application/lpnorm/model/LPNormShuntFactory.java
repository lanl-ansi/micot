package gov.lanl.micot.application.lpnorm.model;

import gov.lanl.micot.infrastructure.ep.model.ShuntCapacitorFactory;

/**
 * Factory class for creating shunts
 * @author Russell Bent
 */
public class LPNormShuntFactory extends ShuntCapacitorFactory {

	private static final String LEGACY_TAG = "LPNORM";
	
	/**
	 * Constructor
	 */
	protected LPNormShuntFactory() {
	}
  
}
