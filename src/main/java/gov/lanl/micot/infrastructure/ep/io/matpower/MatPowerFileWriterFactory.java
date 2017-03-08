package gov.lanl.micot.infrastructure.ep.io.matpower;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.matpower.MatPowerModel;

/**
 * Factory class for determining what kind of mat power file writer
 * class is required
 * @author Russell Bent
 */
public class MatPowerFileWriterFactory {

	private static MatPowerFileWriterFactory instance = null;
	
	/**
	 * Get the instance of the mat power file writer
	 * @return
	 */
	public static MatPowerFileWriterFactory getInstance() {
		if (instance == null) {
			instance = new MatPowerFileWriterFactory();
		}
		return instance;
	}
	
	/**
	 * Singleton constructor
	 */
	private MatPowerFileWriterFactory() {		
	}
	
	/**
	 * Intelligently create the correct mat power file writer
	 * @param model
	 * @return
	 */
	public MatPowerFileWriter getMatPowerFileWriter(ElectricPowerModel model) {
	  if (model instanceof MatPowerModel) {
      return new MatPowerComponentMatPowerFileWriter();
    }
    return new GeneralMatPowerFileWriter();
	}
}
