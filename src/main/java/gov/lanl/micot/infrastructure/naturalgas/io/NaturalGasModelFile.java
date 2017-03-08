package gov.lanl.micot.infrastructure.naturalgas.io;

import java.io.IOException;

import gov.lanl.micot.infrastructure.io.ModelFile;
import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasModel;

/**
 * Interface for interacting with a model file
 * @author Russell Bent
 */
public interface NaturalGasModelFile extends ModelFile<NaturalGasModel> {

	/**
	 * Save the model
	 * @param filename
	 * @param model
	 * @throws IOException
	 */
	public void saveFile(String filename, NaturalGasModel model) throws IOException;
	
}
