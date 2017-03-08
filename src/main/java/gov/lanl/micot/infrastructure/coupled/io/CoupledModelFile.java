package gov.lanl.micot.infrastructure.coupled.io;

import java.io.IOException;

import gov.lanl.micot.infrastructure.coupled.model.CoupledModel;
import gov.lanl.micot.infrastructure.io.ModelFile;

/**
 * Interface for interacting with a model file
 * @author Russell Bent
 */
public interface CoupledModelFile extends ModelFile<CoupledModel> {

	/**
	 * Save the model
	 * @param filename
	 * @param model
	 * @throws IOException
	 */
	public void saveFile(String filename, CoupledModel model) throws IOException;
	
}
