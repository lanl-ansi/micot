package gov.lanl.micot.infrastructure.naturalgas.io.txt;

import java.io.IOException;

import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasModel;

/**
 * Interface for writing Txt File writers
 * @author Russell Bent
 */
public interface TxtFileWriter {
	
	/**
	 * 
	 * @param model
	 * @param filename
	 * @return a registry to the txt components that get written for "model"
	 * @throws IOException
	 */
	public void saveFile(NaturalGasModel model, String filename) throws IOException;	
}
