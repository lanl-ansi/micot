package gov.lanl.micot.util.geometry.geotools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;

import gov.lanl.micot.util.geometry.LineImpl;
import gov.lanl.micot.util.geometry.Point;
import gov.lanl.micot.util.geometry.PointImpl;
import gov.lanl.micot.util.geometry.Polygon;
import gov.lanl.micot.util.geometry.PolygonImpl;
import gov.lanl.micot.util.math.MathUtils;

/**
 * Get all the points associated with a polygon
 * @author Russell Bent
 */
public class GeotoolsPolygon implements Polygon, GeotoolsGeometry {

	private Collection<Point> points = null;
	private com.vividsolutions.jts.geom.Polygon polygon = null;
	
	/**
	 * Constructor
	 * @param points
	 */
	public GeotoolsPolygon(com.vividsolutions.jts.geom.Polygon polygon) {
		super();
		this.points = null;
		this.polygon = polygon;
	}

	/**
	 * Constructor
	 * @param polygon
	 */
	public GeotoolsPolygon(Polygon polygon) {
		Point[] p = polygon.getPoints().toArray(new Point[0]);
		Coordinate[] coordinates = new Coordinate[p.length];
		for (int i = 0; i < coordinates.length; ++i) {
			coordinates[i] = new Coordinate(p[i].getX(), p[i].getY());
		}		
		GeometryFactory factory = new GeometryFactory();
		LinearRing ring = factory.createLinearRing(coordinates);
		this.polygon = factory.createPolygon(ring, new LinearRing[0]);
	}
	
	@Override
	public Collection<Point> getPoints() {
		if (points == null) {
			points = new ArrayList<Point>();
			Coordinate[] coordinates = polygon.getCoordinates();
			for (int i = 0; i < coordinates.length; ++i) {
				points.add(new PointImpl(coordinates[i].x, coordinates[i].y));
			}
		}
		return points;
	}
	
	/**
	 * Get the geotools polyon
	 * @return
	 */
	protected com.vividsolutions.jts.geom.Polygon getGeotoolsPolygon() {
		return polygon;
	}

	@Override
	public Geometry getGeotoolsGeometry() {
		return getGeotoolsPolygon();
	}

	@Override
	public GeotoolsPolygon clone() {
		return new GeotoolsPolygon(this);
	}

	@Override
	public int getNumGeometries() {
		return polygon.getNumGeometries();
	}

	@Override
	public Set<LinkedList<Point>> getGeometryBoundaries() {
		HashSet<LinkedList<Point>> set = new HashSet<LinkedList<Point>>();
		LinkedList<Point> array = new LinkedList<Point>();
		array.addAll(getPoints());
		set.add(array);
		return set;
	}
	
	@Override
	public String toString() {
		String str = "[";
		for (Point point : getPoints()) {
			str += " (" + point.getX() + "," + point.getY() + ")";
		}
		str += " ]";
		return str;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Polygon)) {
			return false;
		}
		
		Polygon polygon = (Polygon)obj;
		
		if (getPoints().size() != polygon.getPoints().size()) {
			return false;
		}
		
		Iterator<Point> thisIterator = getPoints().iterator();
		Iterator<Point> thatIterator = polygon.getPoints().iterator();
		while (thisIterator.hasNext()) {
			Point thisPoint = thisIterator.next();
			Point thatPoint = thatIterator.next();

			if (!MathUtils.DOUBLE_EQUAL(thisPoint.getX(), thatPoint.getX()) || !MathUtils.DOUBLE_EQUAL(thisPoint.getY(), thatPoint.getY())) {
				return false;
			}
		}
		return true;
	}

	@Override
	public Point[] getCoordinates() {
		return getPoints().toArray(new Point[0]);
	}
	
	@Override
	public GeotoolsGeometry reducePrecision() {
		Collection<Point> points = getPoints();
		ArrayList<Point> newPoints = new ArrayList<Point>();
		for (Point point : points) {
			newPoints.add(new PointImpl(MathUtils.REDUCE_PRECISION(point.getX()), MathUtils.REDUCE_PRECISION(point.getY())));
		}
		return new GeotoolsPolygon(new PolygonImpl(newPoints));
	}
}
