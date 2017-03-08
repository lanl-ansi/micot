package gov.lanl.micot.util.geometry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

import gov.lanl.micot.util.math.MathUtils;

/**
 * Get all the points associated with a line
 * @author Russell Bent
 */
public class MultiLineImpl implements MultiLine {

	private Set<Collection<Point>> points = null;
		
	/**
	 * Constructor
	 * @param points
	 */
	public MultiLineImpl(Set<Collection<Point>> points) {
		super();
		this.points = points;
	}

	@Override
	public Collection<Point> getPoints() {
		return points.iterator().next();
	}

	@Override
	public Set<Collection<Point>> getMultiPoints() {
		return points;
	}

	@Override
	public MultiLineImpl clone() {
		LinkedHashSet<Collection<Point>> newPoints = new LinkedHashSet<Collection<Point>>();
		for (Collection<Point> c : points) {
			ArrayList<Point> list = new ArrayList<Point>();
			for (Point p : c) {
				list.add((Point)p.clone());
			}
			newPoints.add(list);
		}
		return new MultiLineImpl(newPoints);
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
	
}
