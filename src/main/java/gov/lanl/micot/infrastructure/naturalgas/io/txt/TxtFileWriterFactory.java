package gov.lanl.micot.infrastructure.naturalgas.io.txt;

import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasModel;
import gov.lanl.micot.infrastructure.naturalgas.model.txt.TxtModel;

/**
 * Factory class for determining what kind of txt file writer
 * class is required
 * @author Russell Bent
 */
public class TxtFileWriterFactory {

	private static TxtFileWriterFactory instance = null;
	
	/**
	 * Get the instance of the txt file writer
	 * @return
	 */
	public static TxtFileWriterFactory getInstance() {
		if (instance == null) {
			instance = new TxtFileWriterFactory();
		}
		return instance;
	}
	
	/**
	 * Singleton constructor
	 */
	private TxtFileWriterFactory() {		
	}
	
	/**
	 * Intelligently create the correct pfw file writer
	 * @param model
	 * @return
	 */
	public TxtFileWriter getTxtFileWriter(NaturalGasModel model) {
		if (model instanceof TxtModel) {
			return new TxtComponentFileWriter();
		}
		return new GeneralTxtFileWriter();
	}
}
