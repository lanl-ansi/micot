package gov.lanl.micot.infrastructure.naturalgas.io.gaslib;

import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasModel;
import gov.lanl.micot.infrastructure.naturalgas.model.txt.TxtModel;

/**
 * Factory class for determining what kind of txt file writer
 * class is required
 * @author Russell Bent
 */
public class GaslibFileWriterFactory {

	private static GaslibFileWriterFactory instance = null;
	
	/**
	 * Get the instance of the txt file writer
	 * @return
	 */
	public static GaslibFileWriterFactory getInstance() {
		if (instance == null) {
			instance = new GaslibFileWriterFactory();
		}
		return instance;
	}
	
	/**
	 * Singleton constructor
	 */
	private GaslibFileWriterFactory() {		
	}
	
	/**
	 * Intelligently create the correct pfw file writer
	 * @param model
	 * @return
	 */
	public GaslibFileWriter getTxtFileWriter(NaturalGasModel model) {
		if (model instanceof TxtModel) {
			return new GaslibComponentFileWriter();
		}
		return new GeneralGaslibFileWriter();
	}
}
