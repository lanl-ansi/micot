package gov.lanl.micot.infrastructure.ep.io.cdf;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.cdf.CDFModel;

/**
 * Factory class for determining what kind of cdf file writer
 * class is required
 * @author Russell Bent
 */
public class CDFFileWriterFactory {

	private static CDFFileWriterFactory instance = null;
	
	/**
	 * Get the instance of the pfw file writer
	 * @return
	 */
	public static CDFFileWriterFactory getInstance() {
		if (instance == null) {
			instance = new CDFFileWriterFactory();
		}
		return instance;
	}
	
	/**
	 * Singleton constructor
	 */
	private CDFFileWriterFactory() {		
	}
	
	/**
	 * Intelligently create the correct pfw file writer
	 * @param model
	 * @return
	 */
	public CDFFileWriter getCDFFileWriter(ElectricPowerModel model) {
	  if (model instanceof CDFModel) {
      return new CDFComponentCDFFileWriter();
    }
    return new GeneralCDFFileWriter();
	}
}
