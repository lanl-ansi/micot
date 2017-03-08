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
import com.vividsolutions.jts.geom.LineString;

import gov.lanl.micot.util.geometry.Line;
import gov.lanl.micot.util.geometry.LineImpl;
import gov.lanl.micot.util.geometry.Point;
import gov.lanl.micot.util.geometry.PointImpl;
import gov.lanl.micot.util.math.MathUtils;

/**
 * Get all the points associated with a line
 * @author Russell Bent
 */
public class GeotoolsLine implements Line, GeotoolsGeometry {

	private Collection<Point> points = null;
	private LineString line = null;	
	
	/**
	 * Constructor
	 * @param points
	 */
	public GeotoolsLine(LineString line) {
		super();
		points = null;
		this.line = line;
	}

	/**
	 * Constructor
	 * @param line
	 */
	public GeotoolsLine(Line line) {
		points = new ArrayList<Point>(); 
		points.addAll(line.getPoints());
		Point[] p = points.toArray(new Point[0]);
		Coordinate[] coordinates = new Coordinate[p.length];
		for (int i = 0; i < coordinates.length; ++i) {
			coordinates[i] = new Coordinate(p[i].getX(), p[i].getY());
		}		
		GeometryFactory factory = new GeometryFactory();
		this.line = factory.createLineString(coordinates);
	}
	
	@Override
	public Collection<Point> getPoints() {
		if (points == null) {
			points = new ArrayList<Point>();
			Coordinate[] coordinates = line.getCoordinates();
			for (int i = 0; i < coordinates.length; ++i) {
				points.add(new PointImpl(coordinates[i].x, coordinates[i].y));
			}
		}
		return points;
	}

	/**
	 * Get the line string
	 * @return
	 */
	protected LineString getLineString() {
		return line;
	}

	@Override
	public Geometry getGeotoolsGeometry() {
		return getLineString();
	}
	
	@Override
	public GeotoolsLine clone() {
		return new GeotoolsLine(this);
	}

	@Override
	public int getNumGeometries() {
		return 1;
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
		if (!(obj instanceof Line)) {
			return false;
		}
		
		Line line = (Line)obj;
		
		if (getPoints().size() != line.getPoints().size()) {
			return false;
		}
		
		Iterator<Point> thisIterator = getPoints().iterator();
		Iterator<Point> thatIterator = line.getPoints().iterator();
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
		return new GeotoolsLine(new LineImpl(newPoints));
	}
	
}
