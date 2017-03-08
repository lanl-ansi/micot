package gov.lanl.micot.infrastructure.ep.io.pfw;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.pfw.PFWModel;

/**
 * Factory class for determining what kind of pfw file writer
 * class is required
 * @author Russell Bent
 */
public class PFWFileWriterFactory {

	private static PFWFileWriterFactory instance = null;
	
	/**
	 * Get the instance of the pfw file writer
	 * @return
	 */
	public static PFWFileWriterFactory getInstance() {
		if (instance == null) {
			instance = new PFWFileWriterFactory();
		}
		return instance;
	}
	
	/**
	 * Singleton constructor
	 */
	private PFWFileWriterFactory() {		
	}
	
	/**
	 * Intelligently create the correct pfw file writer
	 * @param model
	 * @return
	 */
	public PFWFileWriter getPFWFileWriter(ElectricPowerModel model) {
		if (model instanceof PFWModel) {
			return new PFWComponentPFWFileWriter();
		}
		return new GeneralPFWFileWriter();
	}
}
