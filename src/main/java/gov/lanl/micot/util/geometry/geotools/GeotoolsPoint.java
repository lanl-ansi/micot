package gov.lanl.micot.util.geometry.geotools;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

import gov.lanl.micot.util.geometry.LineImpl;
import gov.lanl.micot.util.geometry.Point;
import gov.lanl.micot.util.geometry.PointImpl;
import gov.lanl.micot.util.math.MathUtils;

/**
 * Implementation of a point
 * @author Russell Bent
 */
public class GeotoolsPoint implements Point, GeotoolsGeometry {

	private com.vividsolutions.jts.geom.Point geotoolsPoint = null;
		
	/**
	 * Constructor
	 * @param point
	 */
	public GeotoolsPoint(com.vividsolutions.jts.geom.Point p) {
		super();
		geotoolsPoint = p;
	}

	/**
	 * Constructor
	 * @param point
	 */
	public GeotoolsPoint(Point point) {
		Coordinate coordinate = new Coordinate(point.getX(), point.getY());
		GeometryFactory factory = new GeometryFactory();
		geotoolsPoint = factory.createPoint(coordinate);
	}
	
	/**
	 * Get the geotools point
	 * @return
	 */
	protected com.vividsolutions.jts.geom.Point getGeotoolsPoint() {
		return geotoolsPoint;
	}

	@Override
	public Geometry getGeotoolsGeometry() {
		return getGeotoolsPoint();
	}

	@Override
	public double getX() {
		return geotoolsPoint.getX();
	}

	@Override
	public double getY() {
		return geotoolsPoint.getY();
	}
	
	@Override
	public GeotoolsPoint clone() {
		return new GeotoolsPoint(this);
	}

	@Override
	public int getNumGeometries() {
		return 1;
	}

	@Override
	public Set<LinkedList<Point>> getGeometryBoundaries() {
		HashSet<LinkedList<Point>> set = new HashSet<LinkedList<Point>>();
		LinkedList<Point> array = new LinkedList<Point>();
		array.add(this);
		set.add(array);
		return set;
	}

	@Override
	public double distance(Point point) {
		Point2D thisPoint = new Point2D.Double(getX(), getY());
		Point2D thatPoint = new Point2D.Double(point.getX(), point.getY());
		return thisPoint.distance(thatPoint);
	}
	
	@Override
	public String toString() {
		return "(" + geotoolsPoint.getX() + "," + geotoolsPoint.getY() + ")";
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Point)) {
			return false;
		}
		
		Point tPoint = (Point)obj;
				
		if (!MathUtils.DOUBLE_EQUAL(getX(), tPoint.getX()) || !MathUtils.DOUBLE_EQUAL(getY(), tPoint.getY())) {
			return false;
		}
		return true;
	}
	
	@Override
	public Point[] getCoordinates() {
		Point[] array = new Point[1];
		array[0] = this;
		return array;
	}
	
	@Override
	public GeotoolsGeometry reducePrecision() {
		return new GeotoolsPoint(new PointImpl(MathUtils.REDUCE_PRECISION(getX()), MathUtils.REDUCE_PRECISION(getY())));
	}
}
