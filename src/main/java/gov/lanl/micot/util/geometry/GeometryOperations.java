
package gov.lanl.micot.util.geometry;

import java.awt.geom.Rectangle2D;

/**
 * An interface to some standard geometry operations
 * @author Russell Bent
 */
public interface GeometryOperations {
	
	/**
	 * Function that returns whether or not enlosingGeometry contains containedGeometry
	 * @param enclosingGeometry
	 * @param containedGeometry
	 * @return
	 */
	public boolean isContained(Geometry enclosingGeometry, Geometry containedGeometry);
	
	/**
	 * Determine if the two geometries intersect
	 * @param firstGeomerty
	 * @param secondGeometry
	 * @return
	 */
	public boolean doesIntersect(Geometry firstGeomerty, Geometry secondGeometry);
	
	/**
	 * calculate the intersection between two geometries
	 * @param geometry1
	 * @param geometry2
	 * @return
	 */
	public Geometry calculateIntersection(Geometry geometry1, Geometry geometry2);
	
	/**
	 * Calculate the length
	 * @param geometry
	 * @return
	 */
	public double calculateLength(Geometry geometry);
	
	/**
	 * Convert a geometry to a single point
	 * @param geometry
	 * @return
	 */
	public Point calculateSinglePoint(Geometry geometry);
	
	/**
	 * calculate the intersection between two geometries
	 * @param geometry1
	 * @param geometry2
	 * @return
	 */
	public Geometry calculateUnion(Geometry geometry1, Geometry geometry2);
	
	/**
	 * calculate the bounding box of a geometry
	 * @param geometry
	 * @return
	 */
	public Rectangle2D calculateBoundingBox(Geometry geometry);
	
	/**
	 * Calculate the distance between two geometries
	 * @param geometry1
	 * @param geometry2
	 * @return
	 */
	public double calculateDistance(Geometry geometry1, Geometry geometry2);
	
	/**
	 * Does a transformation of a point from one projection system to another
	 * @param point
	 * @param sourceEPSG
	 * @param destinationEPSG
	 * @return
	 */
	public Point transformProjection(Point point, String sourceEPSG, String destinationEPSG);
	
	 /**
   * Does a transformation of a point from one projection system to another
   * @param point
   * @param sourceEPSG
   * @param destinationEPSG
   * @return
   */
  public Line transformProjection(Line line, String sourceEPSG, String destinationEPSG);

}
