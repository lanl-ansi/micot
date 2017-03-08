package gov.lanl.micot.util.geometry.geotools;

import gov.lanl.micot.util.geometry.Geometry;
import gov.lanl.micot.util.geometry.Line;
import gov.lanl.micot.util.geometry.MultiLine;
import gov.lanl.micot.util.geometry.MultiPoint;
import gov.lanl.micot.util.geometry.MultiPolygon;
import gov.lanl.micot.util.geometry.Point;
import gov.lanl.micot.util.geometry.Polygon;

/**
 * Simple factory for making geometries
 * @author Russell Bent
 *
 */
public class GeotoolsGeometryFactory {

	private static GeotoolsGeometryFactory INSTANCE = null;
	
	public static GeotoolsGeometryFactory getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new GeotoolsGeometryFactory();
		}
		return INSTANCE;
	}
	
	/**
	 * Singleton constructor
	 */
	private GeotoolsGeometryFactory() {		
	}
	
	/**
	 * Creates the geometry
	 * @param geometry
	 * @return
	 */
	public Geometry createGeometry(com.vividsolutions.jts.geom.Geometry geometry) {
		if (geometry instanceof com.vividsolutions.jts.geom.MultiPolygon) {
			return new GeotoolsMultiPolygon((com.vividsolutions.jts.geom.MultiPolygon) geometry);
		}
		if (geometry instanceof com.vividsolutions.jts.geom.Polygon) {
			return new GeotoolsPolygon((com.vividsolutions.jts.geom.Polygon) geometry);
		}
		if (geometry instanceof com.vividsolutions.jts.geom.LineString) {
			return new GeotoolsLine((com.vividsolutions.jts.geom.LineString) geometry);
		}
		if (geometry instanceof com.vividsolutions.jts.geom.MultiLineString) {
			return new GeotoolsMultiLine((com.vividsolutions.jts.geom.MultiLineString) geometry);
		}
		if (geometry instanceof com.vividsolutions.jts.geom.Point) {
			return new GeotoolsPoint((com.vividsolutions.jts.geom.Point) geometry);
		}
		if (geometry instanceof com.vividsolutions.jts.geom.MultiPoint) {
			return new GeotoolsMultiPoint((com.vividsolutions.jts.geom.MultiPoint) geometry);
		}
		return null;
	}
	
	/**
	 * Create a geotools geometry
	 * @param geometry
	 * @return
	 */
	public GeotoolsGeometry createGeometry(Geometry geometry) {
		if (geometry instanceof MultiPolygon) {
			return new GeotoolsMultiPolygon((MultiPolygon)geometry);
		}
		if (geometry instanceof MultiLine) {
			return new GeotoolsMultiLine((MultiLine)geometry);
		}
		if (geometry instanceof MultiPoint) {
			return new GeotoolsMultiPoint((MultiPoint)geometry);
		}
		if (geometry instanceof Polygon) {
			return new GeotoolsPolygon((Polygon)geometry);
		}
		if (geometry instanceof Point) {
			return new GeotoolsPoint((Point)geometry);
		}
		if (geometry instanceof Line) {
			return new GeotoolsLine((Line)geometry);
		}
		return null;
	}
}
