package gov.lanl.micot.util.geometry;


/**
 * A factory method for creating geometry operations
 * @author Russell Bent
 *
 */
public class GeometryOperationsFactory {

  public static final String GEOMETRY_CLASS          = "gov.lanl.micot.util.geometry.geotools.GeotoolsGeometryOperations";
  
  
	private static GeometryOperationsFactory INSTANCE = null;
	
	public synchronized static GeometryOperationsFactory getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new GeometryOperationsFactory();
		}
		return INSTANCE;
	}
	
	/**
	 * Singleton constructor
	 */
	private GeometryOperationsFactory() {		
	}
	
	public GeometryOperations getDefaultGeometryOperations() {
	  try {
      return (GeometryOperations) Class.forName(GEOMETRY_CLASS).newInstance();
    }
    catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
      e.printStackTrace();
    }
	  return null;
	}	
}
