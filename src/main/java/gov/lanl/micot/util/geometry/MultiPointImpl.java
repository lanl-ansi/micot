package gov.lanl.micot.util.geometry;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

import gov.lanl.micot.util.math.MathUtils;

/**
 * Implementation of a multi point
 * @author Russell Bent
 */
public class MultiPointImpl implements MultiPoint {

	private Set<Point> points = null;
		
	/**
	 * Constructor
	 * @param point
	 */
	public MultiPointImpl(Set<Point> points) {
		super();
		this.points = points;
	}

	/**
	 * The first point
	 * @return
	 */
	private Point firstPoint() {
		return points.iterator().next();
	}

	@Override
	public Set<Point> getPoints() {
		return points;
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
	public MultiPointImpl clone() {
		LinkedHashSet<Point> newPoints = new LinkedHashSet<Point>();
		for (Point p : points) {
			newPoints.add((Point)p.clone());
		}
		return new MultiPointImpl(newPoints);
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
}
