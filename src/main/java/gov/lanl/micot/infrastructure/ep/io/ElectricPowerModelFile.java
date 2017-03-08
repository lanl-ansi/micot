package gov.lanl.micot.infrastructure.ep.io;

import java.io.IOException;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.io.ModelFile;

/**
 * Interface for interacting with a model file
 * @author Russell Bent
 */
public interface ElectricPowerModelFile extends ModelFile<ElectricPowerModel> {

	public static final String COORDINATE_HEADER = "BEGIN HEADER COORDINATE, BUS, LATITUDE, LONGITUDE";
	public static final String COORDINATE_FOOTER = "END HEADER COORDINATE, BUS, LATITUDE, LONGITUDE";	
	public static final String EXTRA_GENERATOR_HEADER_START = "BEGIN HEADER EXTRA_GENERATOR_INFO";
  public static final String EXTRA_GENERATOR_FOOTER_START = "END HEADER EXTRA_GENERATOR_INFO";
  public static final String EXTRA_GENERATOR_HEADER_END = ", ID, COST, FUEL_TYPE, CARBON, CAPACITY_FACTOR";
  public static final String EXTRA_GENERATOR_FOOTER_END = ", ID, COST, FUEL_TYPE, CARBON, CAPACITY_FACTOR";
	public static final String BATTERY_HEADER = "BEGIN HEADER BATTERY, ID, NAME, CAPACITY, USED_CAPACITY, COST, MAX, MIN";
  public static final String BATTERY_FOOTER = "END HEADER BATTERY, ID, NAME, CAPACITY, USED_CAPACITY, COST, MAX, MIN";
	
	/**
	 * Save the model
	 * @param filename
	 * @param model
	 * @throws IOException
	 */
	public void saveFile(String filename, ElectricPowerModel model) throws IOException;
	
}
