package gov.lanl.micot.util.io.geometry.shapefile;

import java.io.File;
import java.io.Serializable;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.geotools.data.FeatureWriter;
import org.geotools.data.Transaction;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.referencing.crs.DefaultGeographicCRS;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;

import gov.lanl.micot.util.geometry.Line;
import gov.lanl.micot.util.geometry.MultiLine;
import gov.lanl.micot.util.geometry.MultiPoint;
import gov.lanl.micot.util.geometry.MultiPolygon;
import gov.lanl.micot.util.geometry.Point;
import gov.lanl.micot.util.geometry.Polygon;
import gov.lanl.micot.util.io.geometry.GeometryConstants;
import gov.lanl.micot.util.io.geometry.GeometryEntryData;

/**
 * Shapefile writer based upon geotools
 * 
 * @author Russell Bent
 */
public class GeotoolsShapefileWriter implements ShapefileWriter {

  /**
   * Constructor... only for the factory methods
   */
  protected GeotoolsShapefileWriter() {
  }

  @Override
  public void write(String filename, Collection<ShapefileEntry> entries, Collection<GeometryEntryData> data) {

    try {
      File file = new File(filename);
      URL saURL = file.toURI().toURL();
      ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();

      Map<String, Serializable> params = new HashMap<String, Serializable>();
      params.put("url", saURL);
      params.put("create spatial index", Boolean.TRUE);
      ShapefileDataStore dataStore = (ShapefileDataStore) dataStoreFactory.createNewDataStore(params);

      // build the feature type
      SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
      builder.setName(GeometryConstants.GEOMETRY_FIELD_NAME);
      builder.setCRS(DefaultGeographicCRS.WGS84);
      builder.setDefaultGeometry(GeometryConstants.GEOMETRY_FIELD_NAME);
      for (ShapefileEntry entry : entries) {
        String name = entry.getEntryName();
        if (name.equals(GeometryConstants.GEOMETRY_FIELD_NAME)) {
          builder.add(name, getObjectType(entry.getEntryType()));
        }
        else {
          builder.length(18).add(name, getObjectType(entry.getEntryType()));
        }
      }
      builder.setDefaultGeometry(GeometryConstants.GEOMETRY_FIELD_NAME);
      SimpleFeatureType featureType = builder.buildFeatureType();

      // create the schema to write to
      dataStore.createSchema(featureType);
      Transaction transaction = Transaction.AUTO_COMMIT;

      // get a feature writer and write the data
      FeatureWriter<SimpleFeatureType, SimpleFeature> featureWriter = dataStore.getFeatureWriter(transaction);
      for (GeometryEntryData entryData : data) {
        SimpleFeature feature = featureWriter.next();
        for (ShapefileEntry entry : entries) {
          feature.setAttribute(entry.getEntryName(), getObject(entryData.get(entry)));
          if (entry.getEntryName().equals(GeometryConstants.GEOMETRY_FIELD_NAME)) {
            feature.setDefaultGeometry(getObject(entryData.get(entry)));
          }
        }
      }

      transaction.commit();
      transaction.close();
      featureWriter.close();
    }
    catch (Exception e) {
      e.printStackTrace();
    }

  }

  /**
   * Create an appropriate object
   * 
   * @param entryType
   * @param object
   * @return
   */
  private Object getObject(Object object) {
    if (object == null) {
      return object;
    }
    
    if (Integer.class.isAssignableFrom(object.getClass())) {
      return object;
    }

    if (Double.class.isAssignableFrom(object.getClass())) {
      return object;
    }

    if (Float.class.isAssignableFrom(object.getClass())) {
      return object;
    }

    if (com.vividsolutions.jts.geom.MultiPolygon.class.isAssignableFrom(object.getClass())) {
      return object;
    }

    if (com.vividsolutions.jts.geom.Polygon.class.isAssignableFrom(object.getClass())) {
      return object;
    }

    if (com.vividsolutions.jts.geom.LineString.class.isAssignableFrom(object.getClass())) {
      return object;
    }

    if (com.vividsolutions.jts.geom.Point.class.isAssignableFrom(object.getClass())) {
      return object;
    }

    if (MultiPoint.class.isAssignableFrom(object.getClass())) {
      return createMultiPoint((MultiPoint) object);
    }

    if (MultiLine.class.isAssignableFrom(object.getClass())) {
      return createMultiLine((MultiLine) object);
    }

    if (MultiPolygon.class.isAssignableFrom(object.getClass())) {
      return createMultiPolygon((MultiPolygon) object);
    }

    if (Point.class.isAssignableFrom(object.getClass())) {
      return createPoint((Point) object);
    }

    if (Line.class.isAssignableFrom(object.getClass())) {
      return createLine((Line) object);
    }

    if (Polygon.class.isAssignableFrom(object.getClass())) {
      return createPolygon((Polygon) object);
    }

    return object.toString();

  }

  /**
   * Create a jts point
   * 
   * @param point
   * @return
   */
  private com.vividsolutions.jts.geom.Point createPoint(Point point) {
    Coordinate coordinate = new Coordinate(point.getX(), point.getY());
    GeometryFactory factory = new GeometryFactory();
    return factory.createPoint(coordinate);
  }

  /**
   * Create a jts point
   * 
   * @param point
   * @return
   */
  private com.vividsolutions.jts.geom.MultiPoint createMultiPoint(MultiPoint point) {
    Set<Point> p = point.getPoints();
    Coordinate coordinates[] = new Coordinate[p.size()];
    Iterator<Point> it = p.iterator();
    for (int i = 0; i < p.size(); ++i) {
      Point temp = it.next();
      coordinates[i] = new Coordinate(temp.getX(), temp.getY());
    }

    GeometryFactory factory = new GeometryFactory();
    return factory.createMultiPoint(coordinates);
  }

  /**
   * Create a jts point
   * 
   * @param point
   * @return
   */
  private com.vividsolutions.jts.geom.LineString createLine(Line line) {
    Point[] p = line.getPoints().toArray(new Point[0]);
    int length = p.length;
    if (p.length == 1) {
      length = 2;
    }
    
    Coordinate[] coordinates = new Coordinate[length];
    for (int i = 0; i < p.length; ++i) {
      coordinates[i] = new Coordinate(p[i].getX(), p[i].getY());
    }
    
    // force there to be two points
    if (p.length == 1) {
      coordinates[1] = new Coordinate(p[0].getX(), p[0].getY());      
    }
    
    GeometryFactory factory = new GeometryFactory();
    return factory.createLineString(coordinates);
  }

  /**
   * Create a jts point
   * 
   * @param point
   * @return
   */
  private com.vividsolutions.jts.geom.MultiLineString createMultiLine(MultiLine line) {
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
    return factory.createMultiLineString(lineStrings);
  }

  /**
   * Create a jts point
   * 
   * @param point
   * @return
   */
  private com.vividsolutions.jts.geom.Polygon createPolygon(Polygon polygon) {
    Point[] p = polygon.getPoints().toArray(new Point[0]);
    Coordinate[] coordinates = new Coordinate[p.length];
    for (int i = 0; i < coordinates.length; ++i) {
      coordinates[i] = new Coordinate(p[i].getX(), p[i].getY());
    }

    if (coordinates[0].x != coordinates[coordinates.length - 1].x || coordinates[0].y != coordinates[coordinates.length - 1].y) {
      Coordinate newCoordinates[] = new Coordinate[p.length + 1];
      for (int i = 0; i < coordinates.length; ++i) {
        newCoordinates[i] = coordinates[i];
      }
      newCoordinates[p.length] = newCoordinates[0];
      coordinates = newCoordinates;
    }

    GeometryFactory factory = new GeometryFactory();
    LinearRing ring = factory.createLinearRing(coordinates);
    return factory.createPolygon(ring, new LinearRing[0]);
  }

  /**
   * Create a jts point
   * 
   * @param point
   * @return
   */
  private com.vividsolutions.jts.geom.MultiPolygon createMultiPolygon(MultiPolygon polygon) {
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

      if (coordinates[0].x != coordinates[coordinates.length - 1].x || coordinates[0].y != coordinates[coordinates.length - 1].y) {
        Coordinate newCoordinates[] = new Coordinate[p.length + 1];
        for (int i = 0; i < coordinates.length; ++i) {
          newCoordinates[i] = coordinates[i];
        }
        newCoordinates[p.length] = newCoordinates[0];
        coordinates = newCoordinates;
      }

      LinearRing ring = factory.createLinearRing(coordinates);
      lineStrings[j] = factory.createPolygon(ring, new LinearRing[0]);
    }
    return factory.createMultiPolygon(lineStrings);
  }

  /**
   * Return the geotools mapping of objects
   * 
   * @param cls
   * @return
   */
  private Class<?> getObjectType(Class<?> cls) {
    if (Integer.class.isAssignableFrom(cls)) {
      return Integer.class;
    }

    if (Double.class.isAssignableFrom(cls)) {
      return Double.class;
    }

    if (Float.class.isAssignableFrom(cls)) {
      return Float.class;
    }

    if (com.vividsolutions.jts.geom.MultiPolygon.class.isAssignableFrom(cls)) {
      return com.vividsolutions.jts.geom.MultiPolygon.class;
    }

    if (com.vividsolutions.jts.geom.Polygon.class.isAssignableFrom(cls)) {
      return com.vividsolutions.jts.geom.Polygon.class;
    }

    if (com.vividsolutions.jts.geom.MultiLineString.class.isAssignableFrom(cls)) {
      return com.vividsolutions.jts.geom.MultiLineString.class;
    }

    if (com.vividsolutions.jts.geom.LineString.class.isAssignableFrom(cls)) {
      return com.vividsolutions.jts.geom.LineString.class;
    }

    if (com.vividsolutions.jts.geom.Point.class.isAssignableFrom(cls)) {
      return com.vividsolutions.jts.geom.Point.class;
    }

    if (com.vividsolutions.jts.geom.MultiPoint.class.isAssignableFrom(cls)) {
      return com.vividsolutions.jts.geom.MultiPoint.class;
    }

    if (MultiPoint.class.isAssignableFrom(cls)) {
      return com.vividsolutions.jts.geom.MultiPoint.class;
    }

    if (MultiLine.class.isAssignableFrom(cls)) {
      return com.vividsolutions.jts.geom.MultiLineString.class;
    }

    if (MultiPolygon.class.isAssignableFrom(cls)) {
      return com.vividsolutions.jts.geom.MultiPolygon.class;
    }

    if (Point.class.isAssignableFrom(cls)) {
      return com.vividsolutions.jts.geom.Point.class;
    }

    if (Line.class.isAssignableFrom(cls)) {
      return com.vividsolutions.jts.geom.LineString.class;
    }

    if (Polygon.class.isAssignableFrom(cls)) {
      return com.vividsolutions.jts.geom.Polygon.class;
    }

    return String.class;
  }

}
