package gov.lanl.micot.util.geometry.geotools;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.TopologyException;

import gov.lanl.micot.util.geometry.Geometry;
import gov.lanl.micot.util.geometry.GeometryOperations;
import gov.lanl.micot.util.geometry.Line;
import gov.lanl.micot.util.geometry.LineImpl;
import gov.lanl.micot.util.geometry.Point;
import gov.lanl.micot.util.geometry.PointImpl;
import gov.lanl.micot.util.geometry.Polygon;

/**
 * An implementation of geometry operations that uses geotools
 * @author Russell Bent
 */
public class GeotoolsGeometryOperations implements GeometryOperations {

	@Override
	public boolean isContained(Geometry enclosingGeometry, Geometry containedGeometry) {
		GeotoolsGeometryFactory factory = GeotoolsGeometryFactory.getInstance();
		GeotoolsGeometry eg = factory.createGeometry(enclosingGeometry);
		GeotoolsGeometry cg = factory.createGeometry(containedGeometry);
		return eg.getGeotoolsGeometry().contains(cg.getGeotoolsGeometry());
	}

	@Override
	public boolean doesIntersect(Geometry firstGeometry, Geometry secondGeometry) {
		GeotoolsGeometryFactory factory = GeotoolsGeometryFactory.getInstance();
		GeotoolsGeometry first = factory.createGeometry(firstGeometry);
		GeotoolsGeometry second = factory.createGeometry(secondGeometry);
		return first.getGeotoolsGeometry().intersects(second.getGeotoolsGeometry());
	}

	@Override
	public Geometry calculateIntersection(Geometry geometry1, Geometry geometry2) {
		GeotoolsGeometryFactory factory = GeotoolsGeometryFactory.getInstance();
		GeotoolsGeometry first = geometry1 instanceof GeotoolsGeometry ? (GeotoolsGeometry)geometry1 : factory.createGeometry(geometry1);
		GeotoolsGeometry second = geometry2 instanceof GeotoolsGeometry ? (GeotoolsGeometry)geometry2 : factory.createGeometry(geometry2);
		try {
			return factory.createGeometry(first.getGeotoolsGeometry().intersection(second.getGeotoolsGeometry()));		
		}
		catch (TopologyException e) {							
		}
		return factory.createGeometry(second.getGeotoolsGeometry().intersection(first.getGeotoolsGeometry()));		
	}

	@Override
	public Geometry calculateUnion(Geometry geometry1, Geometry geometry2) {
		GeotoolsGeometryFactory factory = GeotoolsGeometryFactory.getInstance();
		GeotoolsGeometry first = geometry1 instanceof GeotoolsGeometry ? (GeotoolsGeometry)geometry1 : factory.createGeometry(geometry1);
		GeotoolsGeometry second = geometry2 instanceof GeotoolsGeometry ? (GeotoolsGeometry)geometry2 : factory.createGeometry(geometry2);

		// geotools sometimes flakes out
		try {
			return factory.createGeometry(first.getGeotoolsGeometry().union(second.getGeotoolsGeometry()));		
		}
		catch (TopologyException e) {				
		}
		
		try {
			return factory.createGeometry(second.getGeotoolsGeometry().union(first.getGeotoolsGeometry()));
		}
		catch (TopologyException e) {
			// a hack...
			return calculateUnion(first.reducePrecision(), second.reducePrecision());
		}
	}
	
	@Override
	public double calculateLength(Geometry geometry) {
		GeotoolsGeometryFactory factory = GeotoolsGeometryFactory.getInstance();
		GeotoolsGeometry g = factory.createGeometry(geometry);
		return g.getGeotoolsGeometry().getLength();
	}

	@Override
	public Point calculateSinglePoint(Geometry geometry) {
		if (geometry instanceof Point) {
			return (Point) geometry;
		}
		if (geometry instanceof Line) {
			return ((Line) geometry).getPoints().iterator().next();
		}
		if (geometry instanceof Polygon) {
			return ((Polygon) geometry).getPoints().iterator().next();
		}
		return null;
	}

  @Override
  public Rectangle2D calculateBoundingBox(Geometry geometry) {
    GeotoolsGeometryFactory factory = GeotoolsGeometryFactory.getInstance();
    GeotoolsGeometry g = factory.createGeometry(geometry);
    
    com.vividsolutions.jts.geom.Geometry envelope = g.getGeotoolsGeometry().getEnvelope();
    Coordinate[] coordinates = envelope.getCoordinates();
    double minY = coordinates[0].y;
    double maxY = coordinates[0].y;
    double minX = coordinates[0].x;
    double maxX = coordinates[0].x;
    for (int i = 1; i < coordinates.length; ++i) {
      minY = Math.min(minY, coordinates[i].y);
      maxY = Math.max(maxY, coordinates[i].y);
      minX = Math.min(minX, coordinates[i].x);
      maxX = Math.max(maxX, coordinates[i].x);
    }
    Rectangle2D.Double rect = new Rectangle2D.Double(minX, minY, maxX-minX, maxY-minY);
    return rect;
  }

  @Override
  public double calculateDistance(Geometry geometry1, Geometry geometry2) {
    GeotoolsGeometryFactory factory = GeotoolsGeometryFactory.getInstance();
    GeotoolsGeometry first = geometry1 instanceof GeotoolsGeometry ? (GeotoolsGeometry)geometry1 : factory.createGeometry(geometry1);
    GeotoolsGeometry second = geometry2 instanceof GeotoolsGeometry ? (GeotoolsGeometry)geometry2 : factory.createGeometry(geometry2);
    return first.getGeotoolsGeometry().distance(second.getGeotoolsGeometry());    
  }

  @Override
  public Point transformProjection(Point point, String sourceEPSG, String destinationEPSG) {    
    com.vividsolutions.jts.geom.GeometryFactory factory = new com.vividsolutions.jts.geom.GeometryFactory(new com.vividsolutions.jts.geom.PrecisionModel(),32105);
    com.vividsolutions.jts.geom.Geometry geo = factory.createPoint(new com.vividsolutions.jts.geom.Coordinate(point.getX(), point.getY()));
    
    try {
      org.opengis.referencing.crs.CoordinateReferenceSystem sourcecrs = org.geotools.referencing.CRS.decode(sourceEPSG);
      org.opengis.referencing.crs.CoordinateReferenceSystem defaultcrs = org.geotools.referencing.CRS.decode(destinationEPSG);
      boolean lenient = true; // allow for some error due to different datums
      org.opengis.referencing.operation.MathTransform transform =  org.geotools.referencing.CRS.findMathTransform(sourcecrs, defaultcrs, lenient);
      com.vividsolutions.jts.geom.Geometry geo2 = org.geotools.geometry.jts.JTS.transform(geo, transform);
 
      return new PointImpl(geo2.getCoordinate().y, geo2.getCoordinate().x);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
  
  @Override
  public Line transformProjection(Line line, String sourceEPSG, String destinationEPSG) {    
    ArrayList<Point> points = new ArrayList<Point>();
    for (Point point : line.getCoordinates()) {
      points.add(transformProjection(point,sourceEPSG, destinationEPSG));
    }
    return new LineImpl(points);
  }


}
