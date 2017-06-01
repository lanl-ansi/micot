package gov.lanl.micot.infrastructure.io.csv;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import gov.lanl.micot.infrastructure.model.Asset;
import gov.lanl.micot.infrastructure.model.Component;
import gov.lanl.micot.infrastructure.model.Connection;
import gov.lanl.micot.infrastructure.model.Model;
// import gov.lanl.micot.util.geometry.Line;
// import gov.lanl.micot.util.geometry.Point;
// import gov.lanl.micot.util.io.geometry.GeometryConstants;
// import gov.lanl.micot.util.io.geometry.GeometryEntryData;
// import gov.lanl.micot.util.io.geometry.shapefile.ShapefileEntry;
// import gov.lanl.micot.util.io.geometry.shapefile.ShapefileEntryDataImpl;
// import gov.lanl.micot.util.io.geometry.shapefile.ShapefileEntryImpl;
// import gov.lanl.micot.util.io.geometry.shapefile.ShapefileWriter;

/**
 * Class for exporting csv files
 * @author Russell Bent
 */
public class CSVModelExporter {

  /**
   * Constructor
   */
  public CSVModelExporter() {    
  }
    
  /**
   * Function for exporting a model in csv format
   * @param model
   * @throws FileNotFoundException 
   */
  public void exportModel(Model model, String directoryName) throws FileNotFoundException {
    
    Map<Class<?>, Collection<Component>> components = new HashMap<Class<?>, Collection<Component>>();
    Map<Class<?>, Collection<Connection>> edges = new HashMap<Class<?>, Collection<Connection>>();
    
    // get the components
    for (Component component : model.getComponents()) {
      if (components.get(component.getClass()) == null) {
        components.put(component.getClass(), new ArrayList<Component>());
      }
      components.get(component.getClass()).add(component);
    }

    // get the edges
    for (Connection edge : model.getConnections()) {
      if (edges.get(edge.getClass()) == null) {
        edges.put(edge.getClass(), new ArrayList<Connection>());
      }
      edges.get(edge.getClass()).add(edge);
    }

    // print out the component data
    for (Class<?> cls : components.keySet()) {
      String filename = directoryName + File.separatorChar + cls.getSimpleName() + ".csv";
      PrintStream ps = new PrintStream(filename);      
      Collection<Component> assetCollection = components.get(cls);

      // make the list of possible features
      Asset temp = assetCollection.iterator().next();
      ArrayList<Object> attributes = new ArrayList<Object>();
      attributes.addAll(temp.getAttributeKeys());
      
      for (Object key : attributes) {
        ps.print(key + ",");
      }
      ps.println("NODE");
            
      for (Component asset : assetCollection) {
        for (Object key : attributes) {
          String value = asset.getAttribute(key) == null ? "" : asset.getAttribute(key).toString();
          value = value.replace(',', ' ');
          ps.print(value + ",");
        }
        ps.println(model.getNode(asset).toString().replaceAll(",", " "));
      }
     ps.close();   
    }   
    
    
    // print out the component data
    for (Class<?> cls : edges.keySet()) {
      String filename = directoryName + File.separatorChar + cls.getSimpleName() + ".csv";
      PrintStream ps = new PrintStream(filename);      
      Collection<Connection> assetCollection = edges.get(cls);

      // make the list of possible features
      Asset temp = assetCollection.iterator().next();
      ArrayList<Object> attributes = new ArrayList<Object>();
      attributes.addAll(temp.getAttributeKeys());
      
      for (Object key : attributes) {
        ps.print(key + ",");
      }
      ps.println("NODE1,NODE2");
            
      for (Connection asset : assetCollection) {
        for (Object key : attributes) {
          String value = asset.getAttribute(key) == null ? "" : asset.getAttribute(key).toString();
          value = value.replace(',', ' ');
          ps.print(value + ",");
        }
        ps.println(model.getFirstNode(asset).toString().replaceAll(",", " ") + "," + model.getSecondNode(asset).toString().replaceAll(",", " "));
      }
     ps.close();   
    }
    
  }
  
}
