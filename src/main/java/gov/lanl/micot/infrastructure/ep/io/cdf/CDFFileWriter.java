package gov.lanl.micot.infrastructure.ep.io.cdf;

import java.io.IOException;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;

/**
 * Interface for writing CDF File writers
 * @author Russell Bent
 */
public interface CDFFileWriter {
	
	/**
	 * 
	 * @param model
	 * @param filename
	 * @return a registry to the cdf components that get written for "model"
	 * @throws IOException
	 */
	public void saveFile(ElectricPowerModel model, String filename) throws IOException;	
}
