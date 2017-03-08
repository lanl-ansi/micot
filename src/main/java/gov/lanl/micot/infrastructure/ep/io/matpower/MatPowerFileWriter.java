package gov.lanl.micot.infrastructure.ep.io.matpower;

import java.io.IOException;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;

/**
 * Interface for writing MatPower File writers
 * @author Russell Bent
 */
public interface MatPowerFileWriter {
	
	/**
	 * 
	 * @param model
	 * @param filename
	 * @return a registry to the mat power components that get written for "model"
	 * @throws IOException
	 */
	public void saveFile(ElectricPowerModel model, String filename) throws IOException;	
}
