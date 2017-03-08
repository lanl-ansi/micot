package gov.lanl.micot.infrastructure.io;

import java.io.IOException;

import gov.lanl.micot.infrastructure.model.Model;

/**
 * Interface for interacting with a model file
 * @author Russell Bent
 */
public interface ModelFile<T1 extends Model> {

	/**
	 * Read a model
	 * @param filename
	 * @return
	 * @throws IOException
	 */
	public T1 readModel(String filename) throws IOException;
	
	/**
	 * Save the model
	 * @param filename
	 * @param model
	 * @throws IOException
	 */
	public void saveFile(String filename, Model model) throws IOException;
	
}
