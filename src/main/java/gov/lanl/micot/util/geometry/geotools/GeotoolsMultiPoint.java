package gov.lanl.micot.util.geometry.geotools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

import gov.lanl.micot.util.geometry.LineImpl;
import gov.lanl.micot.util.geometry.MultiPoint;
import gov.lanl.micot.util.geometry.MultiPointImpl;
import gov.lanl.micot.util.geometry.Point;
import gov.lanl.micot.util.geometry.PointImpl;
import gov.lanl.micot.util.math.MathUtils;

/**
 * Implementation of a point
 * @author Russell Bent
 */
public class GeotoolsMultiPoint implements MultiPoint, GeotoolsGeometry {

	private Set<Point> points = null;
	private com.vividsolutions.jts.geom.MultiPoint geotoolsPoint = null;
		
	/**
	 * Constructor
	 * @param point
	 */
	public GeotoolsMultiPoint(com.vividsolutions.jts.geom.MultiPoint p) {
		super();
		this.points = null;
		geotoolsPoint = p;
	}

	/**
	 * Constructor
	 * @param p
	 */
	public GeotoolsMultiPoint(MultiPoint point) {
		points = new LinkedHashSet<Point>();
		points.addAll(point.getPoints());
		Coordinate coordinates[] = new Coordinate[points.size()];
		Iterator<Point> it = points.iterator();
		for (int i = 0; i < points.size(); ++i) {
			Point temp = it.next();
			coordinates[i] = new Coordinate(temp.getX(), temp.getY());
		}
		
		GeometryFactory factory = new GeometryFactory();
		geotoolsPoint = factory.createMultiPoint(coordinates);		
	}
	
	@Override
	public Set<Point> getPoints() {
		if (points == null) {
			points = new LinkedHashSet<Point>();
			Coordinate coords[] = geotoolsPoint.getCoordinates();
			for (int i = 0; i < coords.length; ++i) {
				points.add(new PointImpl(coords[i].x, coords[i].y));
			}
		}
		return points;
	}

	/**
	 * Get the geotools point
	 * @return
	 */
	protected com.vividsolutions.jts.geom.MultiPoint getGeotoolsPoint() {
		return geotoolsPoint;
	}

	@Override
	public Geometry getGeotoolsGeometry() {
		return getGeotoolsPoint();
	}
	
	/* The first point
	 * @return
	 */
	private Point firstPoint() {
		return points.iterator().next();
	}
	@Override
	public double getX() {
		return firstPoint().getX();
	}

	@Override
	public double getY() {
		return firstPoint().getY();
	}
	
	@Override
	public GeotoolsMultiPoint clone() {
		return new GeotoolsMultiPoint(this);
	}
	
	@Override
	public Set<LinkedList<Point>> getGeometryBoundaries() {
		HashSet<LinkedList<Point>> set = new HashSet<LinkedList<Point>>();
		for (Point point : getPoints()) {
			LinkedList<Point> array = new LinkedList<Point>();
			array.add(point);
			set.add(array);
		}
		return set;
	}

	@Override
	public int getNumGeometries() {
		return getPoints().size();
	}

	@Override
	public double distance(Point point) {
		return firstPoint().distance(point);
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
		if (!(obj instanceof MultiPoint)) {
			return false;
		}
		
		MultiPoint mPoint = (MultiPoint)obj;
		
		if (getPoints().size() != mPoint.getPoints().size()) {
			return false;
		}
		
		Iterator<Point> thisIterator = getPoints().iterator();
		Iterator<Point> thatIterator = mPoint.getPoints().iterator();
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
		Set<Point> newPoints = new HashSet<Point>();
		for (Point point : points) {
			newPoints.add(new PointImpl(MathUtils.REDUCE_PRECISION(point.getX()), MathUtils.REDUCE_PRECISION(point.getY())));
		}
		return new GeotoolsMultiPoint(new MultiPointImpl(newPoints));
	}
}
