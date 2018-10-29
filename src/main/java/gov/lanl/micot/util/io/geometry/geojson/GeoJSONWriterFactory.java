package gov.lanl.micot.util.io.geometry.geojson;

/**
 * A factory class for creating geo json writers
 * @author Russell Bent
 *
 */
public class GeoJSONWriterFactory {

	private static GeoJSONWriterFactory INSTANCE = null;
	
	public static GeoJSONWriterFactory getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new GeoJSONWriterFactory();
		}
		return INSTANCE;
	}
	
	/**
	 * Singleton constructor
	 */
	private GeoJSONWriterFactory() {		
	}
	
	/**
	 * Get the default geo json writer
	 * @return
	 */
	public GeoJSONWriter getDefaultWriter() {
		return getJSONWriter();
	}
	
	/**
	 * Get the text based geo json writer
	 * @return
	 */
	public JSONGeoJSONWriter getJSONWriter() {
		return new JSONGeoJSONWriter();
	}
}
