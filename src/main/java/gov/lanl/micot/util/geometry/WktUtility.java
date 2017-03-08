package gov.lanl.micot.util.geometry;

/** A class with Well Known Text operations on the generic geometry classes
 * @author Hari S. Khalsa
 *
 */
public class WktUtility {
	
	public static String getWkt(Line line) {
		String wkt = "LINESTRING (";
		Point[] points = line.getCoordinates();
		int i;
		for (i = 0; i < points.length-1; i++) 
		  wkt += points[i].getX() + " " + points[i].getY() + ", ";
		wkt += points[1].getX() + " " + points[1].getY() + ")";
		return wkt;
	}
	
	public static String getWkt(Point point) {
		return "POINT (" + point.getX() + " " + point.getY() + ")";
	}
	
	/** This function only works with single and filled polygons, i.e. no 
	 * doughnut shapes.
	 * @param polygon
	 * @return
	 */
	public static String getWkt(Polygon polygon) {
		String wkt = "POLYGON ((";
		Point[] points = polygon.getCoordinates();
		int i;
		for (i = 0; i < points.length-1; i++) 
		  wkt += points[i].getX() + " " + points[i].getY() + ", ";
		wkt += points[i].getX() + " " + points[i].getY() + "))";
		return wkt;
	}

}
