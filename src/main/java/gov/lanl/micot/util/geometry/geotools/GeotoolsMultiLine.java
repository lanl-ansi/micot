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
import com.vividsolutions.jts.geom.MultiLineString;

import gov.lanl.micot.util.geometry.LineImpl;
import gov.lanl.micot.util.geometry.MultiLine;
import gov.lanl.micot.util.geometry.MultiLineImpl;
import gov.lanl.micot.util.geometry.Point;
import gov.lanl.micot.util.geometry.PointImpl;
import gov.lanl.micot.util.math.MathUtils;

/**
 * Get all the points associated with a line
 * @author Russell Bent
 */
public class GeotoolsMultiLine implements MultiLine, GeotoolsGeometry {

	private Set<Collection<Point>> points = null;
	private MultiLineString line = null;	
	
	/**
	 * Constructor
	 * @param points
	 */
	public GeotoolsMultiLine(MultiLineString line) {
		super();
		points = null;
		this.line = line;
	}

	public GeotoolsMultiLine(MultiLine line) {
		com.vividsolutions.jts.geom.LineString[] lineStrings = new com.vividsolutions.jts.geom.LineString[line.getMultiPoints().size()];
		GeometryFactory factory = new GeometryFactory();
		Iterator<Collection<Point>> it = line.getMultiPoints().iterator();
		for (int j = 0; j < lineStrings.length; ++j) {
			Collection<Point> points = it.next();
			Point[] p = points.toArray(new Point[0]);
			Coordinate[] coordinates = new Coordinate[p.length];
			for (int i = 0; i < coordinates.length; ++i) {
				coordinates[i] = new Coordinate(p[i].getX(), p[i].getY());
			}		
			lineStrings[j] = factory.createLineString(coordinates);			
		}
		this.line = factory.createMultiLineString(lineStrings);
	}
	
	@Override
	public Collection<Point> getPoints() {
		if (points == null) {
			points = new HashSet<Collection<Point>>();
			int size = line.getNumGeometries();
			for (int j = 0; j < size; ++j) {
				com.vividsolutions.jts.geom.Geometry geometry = line.getGeometryN(j);
				Coordinate[] coordinates = geometry.getCoordinates();
				ArrayList<Point> localPoints = new ArrayList<Point>();
				for (int i = 0; i < coordinates.length; ++i) {
					localPoints.add(new PointImpl(coordinates[i].x, coordinates[i].y));
				}
				points.add(localPoints);				
			}
		}
		return points.iterator().next();
	}

	@Override
	public Set<Collection<Point>> getMultiPoints() {
		if (points == null) {
			points = new HashSet<Collection<Point>>();
			int size = line.getNumGeometries();
			for (int j = 0; j < size; ++j) {
				com.vividsolutions.jts.geom.Geometry geometry = line.getGeometryN(j);
				Coordinate[] coordinates = geometry.getCoordinates();
				ArrayList<Point> localPoints = new ArrayList<Point>();
				for (int i = 0; i < coordinates.length; ++i) {
					localPoints.add(new PointImpl(coordinates[i].x, coordinates[i].y));
				}
				points.add(localPoints);				
			}
		}
		return points;
	}
	
	/**
	 * Get the multi line string
	 * @return
	 */
	protected MultiLineString getMultiLineString() {
		return line;
	}

	@Override
	public Geometry getGeotoolsGeometry() {
		return getMultiLineString();
	}
	
	@Override
	public GeotoolsMultiLine clone() {
		return new GeotoolsMultiLine(this);
	}
	
	@Override
	public Set<LinkedList<Point>> getGeometryBoundaries() {
		HashSet<LinkedList<Point>> set = new HashSet<LinkedList<Point>>();
		for (Collection<Point> points : getMultiPoints()) {
			LinkedList<Point> array = new LinkedList<Point>();
			array.addAll(points);
			set.add(array);
		}
		return set;
	}

	@Override
	public int getNumGeometries() {
		return getMultiPoints().size();
	}

	@Override
	public String toString() {
		String str = "{";
		
		for (Collection<Point> collection : getMultiPoints()) {
			str += " [";
			for (Point point : collection) {
				str += " (" + point.getX() + "," + point.getY() + ")";
			}
			str += " ]";
		}
		
		str += " }";
		return str;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof MultiLine)) {
			return false;
		}
		
		MultiLine line = (MultiLine)obj;
		
		if (getMultiPoints().size() != line.getMultiPoints().size()) {
			return false;
		}
		
		Iterator<Collection<Point>> thisCollectionIterator = getMultiPoints().iterator();
		Iterator<Collection<Point>> thatCollectionIterator = line.getMultiPoints().iterator();
		
		while (thisCollectionIterator.hasNext()) {
			Collection<Point> thisCollection = thisCollectionIterator.next();
			Collection<Point> thatCollection = thatCollectionIterator.next();
			if (thisCollection.size() != thatCollection.size()) {
				return false;
			}
		
			Iterator<Point> thisIterator = thisCollection.iterator();
			Iterator<Point> thatIterator = thatCollection.iterator();
			while (thisIterator.hasNext()) {
				Point thisPoint = thisIterator.next();
				Point thatPoint = thatIterator.next();

				if (!MathUtils.DOUBLE_EQUAL(thisPoint.getX(), thatPoint.getX()) || !MathUtils.DOUBLE_EQUAL(thisPoint.getY(), thatPoint.getY())) {
					return false;
				}
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
		Set<Collection<Point>> points = getMultiPoints();
		Set<Collection<Point>> newPoints = new HashSet<Collection<Point>>();
		for (Collection<Point> pt : points) {
			ArrayList<Point> list = new ArrayList<Point>();			
			for (Point point : pt) {
				list.add(new PointImpl(MathUtils.REDUCE_PRECISION(point.getX()), MathUtils.REDUCE_PRECISION(point.getY())));
			}
			newPoints.add(list);
		}
		return new GeotoolsMultiLine(new MultiLineImpl(newPoints));
	}
}
