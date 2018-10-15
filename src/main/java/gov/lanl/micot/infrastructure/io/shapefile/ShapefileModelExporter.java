package gov.lanl.micot.infrastructure.io.shapefile;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import gov.lanl.micot.infrastructure.model.Asset;
import gov.lanl.micot.infrastructure.model.Component;
import gov.lanl.micot.infrastructure.model.Connection;
import gov.lanl.micot.infrastructure.model.Model;
import gov.lanl.micot.util.geometry.Line;
import gov.lanl.micot.util.geometry.Point;
import gov.lanl.micot.util.io.geometry.GeometryConstants;
import gov.lanl.micot.util.io.geometry.GeometryEntryData;
import gov.lanl.micot.util.io.geometry.shapefile.ShapefileEntry;
import gov.lanl.micot.util.io.geometry.shapefile.ShapefileEntryDataImpl;
import gov.lanl.micot.util.io.geometry.shapefile.ShapefileEntryImpl;
import gov.lanl.micot.util.io.geometry.shapefile.ShapefileWriter;

/**
 * Class for exporting shapefiles
 * @author Russell Bent
 */
public class ShapefileModelExporter {

  // a general map to change the output of field names
  private HashMap<String,String> fieldNames = null;
  private HashMap<String,String> reverseFieldNames = null;
  
  private String NODE = "node";
  private String NODE1 = "node1";
  private String NODE2 = "node2";
  
  /**
   * Constructor
   */
  public ShapefileModelExporter() {    
    fieldNames = new HashMap<String,String>();
    reverseFieldNames = new HashMap<String,String>();
  }
  
  /**
   * Export the assets
   * @param model
   * @param cls
   * @param filename
   */
  public void exportAssets(Model model,  ShapefileWriter writer, Class<? extends Asset> cls, String filename) {
    Collection<ShapefileEntry> entries = new ArrayList<ShapefileEntry>();
    Collection<GeometryEntryData> data = new ArrayList<GeometryEntryData>();
    
    Collection<? extends Asset> assetCollection = model.getAssets(cls);

    // make the list of possible features
    Asset temp = assetCollection.iterator().next();
    int idx = 1;
    for (Object key : temp.getAttributeKeys()) {
      if (key.equals(Asset.COORDINATE_KEY)) {
        Class<?> newClass = (temp instanceof Component) ? Point.class : Line.class; 
        ShapefileEntry entry = new ShapefileEntryImpl(GeometryConstants.GEOMETRY_FIELD_NAME, 0, newClass);
        entries.add(entry);
      }        
      else {
        Object value = temp.getAttribute(key) == null ? "" : temp.getAttribute(key);
        String field = fieldNames.get(key) == null ? key.toString() : fieldNames.get(key);          
        ShapefileEntry entry = new ShapefileEntryImpl(field, idx, value.getClass());
        entries.add(entry);
        ++idx;
      }
    }
    
    if (temp instanceof Connection) {
      ShapefileEntry entry = new ShapefileEntryImpl(NODE1, idx, String.class);
      entries.add(entry);
      ++idx;
      entry = new ShapefileEntryImpl(NODE2, idx, String.class);
      entries.add(entry);
      ++idx;
    }
    else {
      ShapefileEntry entry = new ShapefileEntryImpl(NODE, idx, String.class);
      entries.add(entry);
      ++idx;        
    }
    
    // make all the data
    for (Asset asset : assetCollection) {
      GeometryEntryData assetData = new ShapefileEntryDataImpl();
      for (ShapefileEntry key: entries) {
        if (key.getEntryName().equals(GeometryConstants.GEOMETRY_FIELD_NAME)) {
          Object points = (temp instanceof Component) ? ((Component)asset).getCoordinate() : ((Connection)asset).getCoordinates();
          assetData.put(key, points);
        }
        else if (key.getEntryName().equals(NODE1)) {
          Connection c = (Connection)asset;
          assetData.put(key, model.getFirstNode(c).toString());
        }
        else if (key.getEntryName().equals(NODE2)) {
          Connection c = (Connection)asset;
          assetData.put(key, model.getSecondNode(c).toString());
        }          
        else if (key.getEntryName().equals(NODE)) {
          Component c = (Component)asset;
          assetData.put(key, model.getNode(c).toString());
        }          
        else {
          String field = key.getEntryName();
          if (reverseFieldNames.get(field) != null) {
            field = reverseFieldNames.get(field);
          }
          assetData.put(key, asset.getAttribute(field));
        }
      }
      data.add(assetData);
      
    }
    
    writer.write(filename, entries, data);   
    
    
  }
  
  /**
   * Function for exporting a model in shapefile format
   * @param model
   */
  public void exportModel(Model model, ShapefileWriter writer, String directoryName) {
    
    Map<Class<?>, Collection<Asset>> assets = new HashMap<Class<?>, Collection<Asset>>();
    
    // collect the assets
    for (Asset asset : model.getComponents()) {
      if (assets.get(asset.getClass()) == null) {
        assets.put(asset.getClass(), new ArrayList<Asset>());
      }
      assets.get(asset.getClass()).add(asset);
    }

    for (Asset asset : model.getConnections()) {
      if (assets.get(asset.getClass()) == null) {
        assets.put(asset.getClass(), new ArrayList<Asset>());
      }
      assets.get(asset.getClass()).add(asset);
    }

    
    for (Class<?> cls : assets.keySet()) {      
      String filename = directoryName + File.separatorChar + cls.getSimpleName() + ".shp";
      exportAssets(model, writer, (Class<Asset>) cls, filename);
    }
    
  }

  /**
   * Allow a field name change
   * @param oldName
   * @param newName
   */
  public void changeFieldName(String oldName, String newName) {
    fieldNames.put(oldName, newName);
    reverseFieldNames.put(newName, oldName);
  }
  
}
