package gov.lanl.micot.util.io.geometry.geojson;

import java.io.PrintStream;
import java.util.Collection;

import gov.lanl.micot.util.geometry.Geometry;
import gov.lanl.micot.util.geometry.Line;
import gov.lanl.micot.util.geometry.Point;
import gov.lanl.micot.util.io.geometry.GeometryConstants;
import gov.lanl.micot.util.io.geometry.GeometryEntryData;
import gov.lanl.micot.util.io.json.JSON;
import gov.lanl.micot.util.io.json.JSONArrayBuilder;
import gov.lanl.micot.util.io.json.JSONObjectBuilder;
import gov.lanl.micot.util.io.json.JSONWriter;

/**
 * Geo JSON writer based upon our json implementation....
 * 
 * @author Russell Bent
 */
public class JSONGeoJSONWriter implements GeoJSONWriter {
  
  private static final String TYPE_TAG = "type";
  private static final String TYPE_VALUE_TAG = "FeatureCollection";
  
  private static final String FEATURE_TYPE_TAG = "type";
  private static final String FEATURE_TYPE_VALUE_TAG = "Feature";
  
  private static final String GEOMETRY_TYPE_TAG = "type";
  private static final String COORDINATE_TYPE_TAG = "coordinates";
  private static final String POINT_VALUE_TAG = "Point";
  private static final String LINESTRING_VALUE_TAG = "LineString";
  
  private static final String GEOMETRY_TAG = "geometry";
  private static final String PROPERTIES_TAG = "properties";
  private static final String FEATURES_TAG = "features";
  
  /**
   * Constructor... only for the factory methods
   */
  protected JSONGeoJSONWriter() {
  }

  @Override
  public void write(String filename, Collection<GeoJSONEntry> entries, Collection<GeometryEntryData> data) {

    try {
      PrintStream out = new PrintStream(filename);
      JSONWriter writer = JSON.getDefaultJSON().createWriter(out);
            
      JSONObjectBuilder builder = JSON.getDefaultJSON().createObjectBuilder();
      builder = builder.add(TYPE_TAG, TYPE_VALUE_TAG);
                  
      JSONArrayBuilder arrayBuilder = JSON.getDefaultJSON().createArrayBuilder();  
      for (GeometryEntryData info : data) {      
        JSONObjectBuilder dataBuilder = JSON.getDefaultJSON().createObjectBuilder();
        dataBuilder = dataBuilder.add(FEATURE_TYPE_TAG, FEATURE_TYPE_VALUE_TAG);
      
        JSONObjectBuilder geoBuilder = JSON.getDefaultJSON().createObjectBuilder();
        JSONObjectBuilder propertiesBuilder = JSON.getDefaultJSON().createObjectBuilder();
        
        for (GeoJSONEntry entry : entries) {        
          String name = entry.getEntryName();
          if (name.equals(GeometryConstants.GEOMETRY_FIELD_NAME)) {
            geoBuilder = geoBuilder.add(GEOMETRY_TYPE_TAG, getGeometryType(entry.getEntryType()));
            Geometry geometry = info.getGeometry();
            Point[] points = geometry.getCoordinates();
            JSONArrayBuilder coords = JSON.getDefaultJSON().createArrayBuilder(); 
            if (geometry instanceof Point) {
              coords.add(points[0].getX());
              coords.add(points[0].getY());
              geoBuilder = geoBuilder.add(COORDINATE_TYPE_TAG, coords);
            }
            else {
              for (int i = 0; i < points.length; ++i) {
                JSONArrayBuilder coord = JSON.getDefaultJSON().createArrayBuilder();
                coord.add(points[i].getX());
                coord.add(points[i].getY());
                coords.add(coord);
              }              
              geoBuilder = geoBuilder.add(COORDINATE_TYPE_TAG, coords);
            }
          }
          else {
            Object value = info.get(entry);
            if (value == null) {
              value = "";
            }
            propertiesBuilder = propertiesBuilder.add(name, value);
          }
        }
        
        dataBuilder = dataBuilder.add(GEOMETRY_TAG, geoBuilder);
        dataBuilder = dataBuilder.add(PROPERTIES_TAG, propertiesBuilder);
        arrayBuilder = arrayBuilder.add(dataBuilder);
      }
      builder = builder.add(FEATURES_TAG, arrayBuilder);
      writer.write(builder.build()); 
      writer.close();
      out.close();
    }
    catch (Exception e) {
      e.printStackTrace();
    }

  }

  /**
   * Return the geotools mapping of objects
   * 
   * @param cls
   * @return
   */
  private String getGeometryType(Class<?> cls) {

   if (Point.class.isAssignableFrom(cls)) {
      return POINT_VALUE_TAG;
    }

    if (Line.class.isAssignableFrom(cls)) {
      return LINESTRING_VALUE_TAG;
    }

    return null;
  }

}
