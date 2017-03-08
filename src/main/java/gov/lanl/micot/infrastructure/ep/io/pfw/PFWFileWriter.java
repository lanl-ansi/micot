package gov.lanl.micot.infrastructure.ep.io.pfw;

import java.io.IOException;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;

/**
 * Interface for writing PFW File writers
 * @author Russell Bent
 */
public interface PFWFileWriter {
	
	/**
	 * 
	 * @param model
	 * @param filename
	 * @return a registry to the pfw components that get written for "model"
	 * @throws IOException
	 */
	public void saveFile(ElectricPowerModel model, String filename) throws IOException;	
}
