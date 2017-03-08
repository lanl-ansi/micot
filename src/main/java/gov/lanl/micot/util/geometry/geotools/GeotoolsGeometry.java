package gov.lanl.micot.util.geometry.geotools;

import gov.lanl.micot.util.geometry.Geometry;

/**
 * Interface for a geotools geometry
 * @author Russell Bent
 *
 */
public interface GeotoolsGeometry extends Geometry {

	/**
	 * Get the geotools geometry
	 * @return
	 */
	public com.vividsolutions.jts.geom.Geometry getGeotoolsGeometry();

	/**
	 * Sometimes the precision is to great in the geometry to do what it needs to do
	 * @return
	 */
	public GeotoolsGeometry reducePrecision();
	
}
