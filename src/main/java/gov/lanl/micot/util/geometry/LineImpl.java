package gov.lanl.micot.util.geometry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import gov.lanl.micot.util.math.MathUtils;

/**
 * Get all the points associated with a line
 * @author Russell Bent
 */
public class LineImpl implements Line {

	private Collection<Point> points = null;
		
	/**
	 * Constructor
	 * @param points
	 */
	public LineImpl(Collection<Point> points) {
		super();
		this.points = points;
	}
	
	/**
	 * Constructor
	 * @param points
	 */
	public LineImpl(Point[] points) {
		super();
		this.points = new ArrayList<Point>();
		for (int i = 0; i < points.length; ++i) {
			this.points.add(points[i]);
		}
	}


	@Override
	public Collection<Point> getPoints() {
		return points;
	}

	@Override
	public LineImpl clone() {		
		ArrayList<Point> newPoints = new ArrayList<Point>();
		for (Point point : points) {
			newPoints.add((Point)point.clone());
		}
		return new LineImpl(newPoints);
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
	public int getNumGeometries() {
		return 1;
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
}
