package gov.lanl.micot.infrastructure.transportation.road.io;

import java.io.IOException;

import gov.lanl.micot.infrastructure.io.ModelFile;
import gov.lanl.micot.infrastructure.transportation.road.model.RoadModel;

/**
 * Interface for interacting with a model file
 * @author Russell Bent
 */
public interface RoadModelFile extends ModelFile<RoadModel> {

	/**
	 * Save the model
	 * @param filename
	 * @param model
	 * @throws IOException
	 */
	public void saveFile(String filename, RoadModel model) throws IOException;
	
}
