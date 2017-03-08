package gov.lanl.micot.util.geometry;

import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import gov.lanl.micot.util.math.MathUtils;

/**
 * Implementation of a point
 * @author Russell Bent
 */
public class PointImpl implements Point {

	private Point2D point = null;
		
	/**
	 * Constructor
	 * @param point
	 */
	public PointImpl(Point2D point) {
		super();
		this.point = point;
	}
	
	public PointImpl(double x, double y) {
		this(new Point2D.Double(x,y));
	}

	/**
	 * Get the point 2d of this point
	 * @return
	 */
	private Point2D getPoint() {
		return point;
	}

	@Override
	public double getX() {
		return getPoint().getX();
	}

	@Override
	public double getY() {
		return getPoint().getY();
	}

	@Override
	public PointImpl clone() {
		return new PointImpl(point);
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
	public int getNumGeometries() {
		return 1;
	}

	@Override
	public double distance(Point point) {
		Point2D.Double thatPoint = new Point2D.Double(point.getX(), point.getY());
		return getPoint().distance(thatPoint);
	}
	
	@Override
	public String toString() {
		return "(" + getPoint().getX() + "," + getPoint().getY() + ")";
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
}
