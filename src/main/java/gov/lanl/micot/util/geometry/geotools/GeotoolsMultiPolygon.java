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

import gov.lanl.micot.util.geometry.MultiLineImpl;
import gov.lanl.micot.util.geometry.MultiPolygon;
import gov.lanl.micot.util.geometry.MultiPolygonImpl;
import gov.lanl.micot.util.geometry.Point;
import gov.lanl.micot.util.geometry.PointImpl;
import gov.lanl.micot.util.math.MathUtils;

/**
 * Get all the points associated with a line
 * @author Russell Bent
 */
public class GeotoolsMultiPolygon implements MultiPolygon, GeotoolsGeometry {

	private Set<Collection<Point>> points = null;
	private com.vividsolutions.jts.geom.MultiPolygon polygon = null;	
	
	/**
	 * Constructor
	 * @param points
	 */
	public GeotoolsMultiPolygon(com.vividsolutions.jts.geom.MultiPolygon polygon) {
		super();
		points = null;
		this.polygon = polygon;
	}

	/**
	 * Constructor
	 * @param polygon
	 */
	public GeotoolsMultiPolygon(MultiPolygon polygon) {
		com.vividsolutions.jts.geom.Polygon[] lineStrings = new com.vividsolutions.jts.geom.Polygon[polygon.getMultiPoints().size()];
		GeometryFactory factory = new GeometryFactory();
		Iterator<Collection<Point>> it = polygon.getMultiPoints().iterator();
		for (int j = 0; j < lineStrings.length; ++j) {
			Collection<Point> points = it.next();
			Point[] p = points.toArray(new Point[0]);
			Coordinate[] coordinates = new Coordinate[p.length];
			for (int i = 0; i < coordinates.length; ++i) {
				coordinates[i] = new Coordinate(p[i].getX(), p[i].getY());
			}
			
			if (p[0].getX() != p[p.length-1].getX() || p[0].getY() != p[p.length-1].getY()) {
				Coordinate[] newCoordinates = new Coordinate[p.length+1];
				for (int i = 0; i < coordinates.length; ++i) {
					newCoordinates[i] = coordinates[i];
				}
				newCoordinates[p.length] = new Coordinate(p[0].getX(), p[0].getY());				
				coordinates = newCoordinates;
			}
			
			LinearRing ring = factory.createLinearRing(coordinates);
			lineStrings[j] = factory.createPolygon(ring, new LinearRing[0]);		
		}
		this.polygon = factory.createMultiPolygon(lineStrings);
	}
	
	@Override
	public Collection<Point> getPoints() {
		if (points == null) {
			points = new HashSet<Collection<Point>>();
			int size = polygon.getNumGeometries();
			for (int j = 0; j < size; ++j) {
				com.vividsolutions.jts.geom.Geometry geometry = polygon.getGeometryN(j);
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
			int size = polygon.getNumGeometries();
			for (int j = 0; j < size; ++j) {
				com.vividsolutions.jts.geom.Geometry geometry = polygon.getGeometryN(j);
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
	 * Get the geotools poylgon
	 * @return
	 */
	protected com.vividsolutions.jts.geom.MultiPolygon getGeotoolsMultiPolygon() {
		return polygon;
	}

	@Override
	public Geometry getGeotoolsGeometry() {
		return getGeotoolsMultiPolygon();
	}
	
	@Override
	public GeotoolsMultiPolygon clone() {
		return new GeotoolsMultiPolygon(this);
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
		if (!(obj instanceof MultiPolygon)) {
			return false;
		}
		
		MultiPolygon polygon = (MultiPolygon)obj;
		
		if (getMultiPoints().size() != polygon.getMultiPoints().size()) {
			return false;
		}
		
		Iterator<Collection<Point>> thisCollectionIterator = getMultiPoints().iterator();
		Iterator<Collection<Point>> thatCollectionIterator = polygon.getMultiPoints().iterator();
		
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
		return new GeotoolsMultiPolygon(new MultiPolygonImpl(newPoints));
	}
}
