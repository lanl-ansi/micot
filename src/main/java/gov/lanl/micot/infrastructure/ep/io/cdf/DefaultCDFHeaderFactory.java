package gov.lanl.micot.infrastructure.ep.io.cdf;

import gov.lanl.micot.infrastructure.ep.model.cdf.CDFHeader;

/**
 * This is a factory for creating default cdf file headers
 * 
 * @author Russell Bent
 */
public class DefaultCDFHeaderFactory {

	private static DefaultCDFHeaderFactory	instance	= null;

	/**
	 * Singleton
	 * @return
	 */
	public static DefaultCDFHeaderFactory getInstance() {
		if (instance == null) {
			instance = new DefaultCDFHeaderFactory();
		}
		return instance;
	}

	/**
	 * Constructor
	 */
	private DefaultCDFHeaderFactory() {
	}

	/**
	 * Create the default header
	 * @return
	 */
	public CDFHeader createDefaultCDFFileHeader(double mvaBase) {
    String header = " 99/99/99 LANL                100.0  9999 W 1                    ";
    CDFHeader cHeader = new CDFHeader(header);
    cHeader.setMVABase(mvaBase);
    return cHeader;
	}

}
