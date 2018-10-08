package gov.lanl.micot.util.io.geometry.shapefile;

/**
 * A factory class for creating shapefile writers
 * @author Russell Bent
 *
 */
public class ShapefileWriterFactory {

	private static ShapefileWriterFactory INSTANCE = null;
	
	public static ShapefileWriterFactory getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new ShapefileWriterFactory();
		}
		return INSTANCE;
	}
	
	/**
	 * Singleton constructor
	 */
	private ShapefileWriterFactory() {		
	}
	
	/**
	 * Get the default shapefile writer
	 * @return
	 */
	public ShapefileWriter getDefaultWriter() {
		return getGeotoolsWriter();
	}
	
	/**
	 * Get the geotools shapefile writer
	 * @return
	 */
	public GeotoolsShapefileWriter getGeotoolsWriter() {
		return new GeotoolsShapefileWriter();
	}
}
