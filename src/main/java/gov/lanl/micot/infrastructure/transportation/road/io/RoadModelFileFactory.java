package gov.lanl.micot.infrastructure.transportation.road.io;

import java.util.HashMap;
import java.util.Map;

import gov.lanl.micot.infrastructure.io.ModelFileFactory;
import gov.lanl.micot.infrastructure.transportation.road.model.RoadModel;


/**
 * Factory for creating model files
 * @author Russell Bent
 */
public class RoadModelFileFactory implements ModelFileFactory<RoadModel, RoadModelFile> {

  private static final Map<Class<?>,Class<?>> classMapRegistry = new HashMap<Class<?>,Class<?>>();
  private static final Map<String,Class<?>> extensionMapRegistry = new HashMap<String,Class<?>>();
  
  /**
   * register a model class to a model file
   * @param modelClass
   * @param fileClass
   */
  public synchronized static void registerClass(Class<?> modelClass, Class<?> fileClass) {
    classMapRegistry.put(modelClass, fileClass);
  }
  
  /**
   * register a model extension to a model file
   * @param extension
   * @param fileClass
   */
  public synchronized static void registerExtension(String extension, Class<?> fileClass) {
    extensionMapRegistry.put(extension, fileClass);
  }
  
  static {
    try {
      if (Class.forName("gov.lanl.micot.infrastructure.transportation.road.model.shapefile.ShapefileModel") != null) {
        registerClass(Class.forName("gov.lanl.micot.infrastructure.transportation.road.model.shapefile.ShapefileModel"), Class.forName("gov.lanl.micot.infrastructure.transportation.road.io.shapefile.ShapefileModelFile"));
        registerExtension("prj",Class.forName("gov.lanl.micot.infrastructure.transportation.road.io.shapefile.ShapefileModelFile"));
      }
    }
    catch (Exception e) {
    
    }
  }
  
  
	/**
	 * Constructor
	 */
	public RoadModelFileFactory() {		
	}
	
	/**
	 * Creates an appropriate model file
	 * @param <M>
	 * @param model
	 * @return
	 */
	public RoadModelFile createModelFile(RoadModel model) {
	   for (Class<?> cls : classMapRegistry.keySet()) {
	      if (cls.isInstance(model)) {
	        try {
	          return (RoadModelFile) classMapRegistry.get(cls).newInstance();
	        }
	        catch (InstantiationException | IllegalAccessException e) {
	          e.printStackTrace();
	        }
	      }
	    }

	  
//	  try {
  //    return (RoadModelFile) Class.forName("gov.lanl.micot.infrastructure.transportation.road.io.shapefile.ShapefileModelFile").newInstance();
    //}
    //catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
      //e.printStackTrace();
    //}
	  return null;
		//return new ShapefileModelFile();
	}
	
	/**
	 * Infer a model based upon the file name extension
	 * @param fileExtension
	 * @return
	 */
	public RoadModelFile createModelFileFromExtension(String fileExtension) {
/*		if (fileExtension.equals("prj")) {
			try {
		     //return new ShapefileModelFile();
        return (RoadModelFile) Class.forName("gov.lanl.micot.infrastructure.transportation.road.io.shapefile.ShapefileModelFile").newInstance();
      }
      catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
        e.printStackTrace();
      }
		}*/
		
		 try {
	      return (RoadModelFile) extensionMapRegistry.get(fileExtension).newInstance();
	    }
	    catch (InstantiationException | IllegalAccessException e) {
	      e.printStackTrace();
	    }
		
		
		return null;
	}

  @Override
  public RoadModelFile createModelFile(String filename) {
    int idx = filename.lastIndexOf(".");
    String ext = filename.substring(idx+1,filename.length());    
    return createModelFileFromExtension(ext);
  }
	
}
