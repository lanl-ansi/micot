package gov.lanl.micot.infrastructure.ep.io;

import java.util.HashMap;
import java.util.Map;

//import gov.lanl.micot.infrastructure.ep.io.cdf.CDFFile;
//import gov.lanl.micot.infrastructure.ep.io.dat.DatFile;
//import gov.lanl.micot.infrastructure.ep.io.dew.DewFile;
//import gov.lanl.micot.infrastructure.ep.io.ieiss.IeissModelFile;
//import gov.lanl.micot.infrastructure.ep.io.lacounty.LACountyModelFile;
//import gov.lanl.micot.infrastructure.ep.io.matpower.MatPowerFile;
//import gov.lanl.micot.infrastructure.ep.io.opendss.OpenDSSModelFile;
//import gov.lanl.micot.infrastructure.ep.io.pfw.PFWFile;
//import gov.lanl.micot.infrastructure.ep.io.powerworld.PowerworldModelFile;
//import gov.lanl.micot.infrastructure.ep.io.rsl.RSLFile;
//import gov.lanl.micot.infrastructure.ep.io.shapefile.ShapefileModelFile;
//import gov.lanl.micot.infrastructure.ep.io.uk.UKFile;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
//import gov.lanl.micot.infrastructure.ep.model.cdf.CDFModel;
//import gov.lanl.micot.infrastructure.ep.model.dat.DatModel;
//import gov.lanl.micot.infrastructure.ep.model.dew.DewModel;
//import gov.lanl.micot.infrastructure.ep.model.ieiss.IeissModel;
//import gov.lanl.micot.infrastructure.ep.model.lacounty.LACountyModel;
//import gov.lanl.micot.infrastructure.ep.model.matpower.MatPowerModel;
//import gov.lanl.micot.infrastructure.ep.model.opendss.OpenDSSModel;
//import gov.lanl.micot.infrastructure.ep.model.pfw.PFWModel;
//import gov.lanl.micot.infrastructure.ep.model.powerworld.PowerworldModel;
//import gov.lanl.micot.infrastructure.ep.model.rsl.RSLModel;
//import gov.lanl.micot.infrastructure.ep.model.shapefile.ShapefileModel;
import gov.lanl.micot.infrastructure.io.ModelFileFactory;

/**
 * Factory for creating model files
 * @author Russell Bent
 */
public class ElectricPowerModelFileFactory implements ModelFileFactory<ElectricPowerModel, ElectricPowerModelFile> {

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
      if (Class.forName("gov.lanl.micot.infrastructure.ep.model.pfw.PFWModel") != null) {
        registerClass(Class.forName("gov.lanl.micot.infrastructure.ep.model.pfw.PFWModel"), Class.forName("gov.lanl.micot.infrastructure.ep.io.pfw.PFWFile"));
        registerExtension("pfw",Class.forName("gov.lanl.micot.infrastructure.ep.io.pfw.PFWFile"));
      }
    }
    catch (Exception e) {      
    }
    
    try {      
      if (Class.forName("gov.lanl.micot.infrastructure.ep.model.cdf.CDFModel") != null) {
        registerClass(Class.forName("gov.lanl.micot.infrastructure.ep.model.cdf.CDFModel"), Class.forName("gov.lanl.micot.infrastructure.ep.io.cdf.CDFFile"));
        registerExtension("cdf",Class.forName("gov.lanl.micot.infrastructure.ep.io.cdf.CDFFile"));
      }
    }
    catch (Exception e) {      
    }
    
    try {
      if (Class.forName("gov.lanl.micot.infrastructure.ep.model.matpower.MatPowerModel") != null) {
        registerClass(Class.forName("gov.lanl.micot.infrastructure.ep.model.matpower.MatPowerModel"), Class.forName("gov.lanl.micot.infrastructure.ep.io.matpower.MatPowerFile"));
        registerExtension("m",Class.forName("gov.lanl.micot.infrastructure.ep.io.matpower.MatPowerFile"));
      }
    }
    catch (Exception e) {      
    }
    
    try {
      if (Class.forName("gov.lanl.micot.infrastructure.ep.model.opendss.OpenDSSModel") != null) {
        registerClass(Class.forName("gov.lanl.micot.infrastructure.ep.model.opendss.OpenDSSModel"), Class.forName("gov.lanl.micot.infrastructure.ep.io.opendss.OpenDSSModelFile"));
        registerExtension("dss",Class.forName("gov.lanl.micot.infrastructure.ep.io.opendss.OpenDSSModelFile"));
      }
    }
    catch (Exception e) {      
    }
    
    try {
      if (Class.forName("gov.lanl.micot.infrastructure.ep.model.powerworld.PowerworldModel") != null) {
        registerClass(Class.forName("gov.lanl.micot.infrastructure.ep.model.powerworld.PowerworldModel"), Class.forName("gov.lanl.micot.infrastructure.ep.io.powerworld.PowerworldModelFile"));
        registerExtension("pwb",Class.forName("gov.lanl.micot.infrastructure.ep.io.powerworld.PowerworldModelFile"));
      }
    }
    catch (Exception e) {     
    }
    
    try {
      if (Class.forName("gov.lanl.micot.infrastructure.ep.model.dew.DewModel") != null) {
        registerClass(Class.forName("gov.lanl.micot.infrastructure.ep.model.dew.DewModel"), Class.forName("gov.lanl.micot.infrastructure.ep.io.dew.DewFile"));
        registerExtension("dewc",Class.forName("gov.lanl.micot.infrastructure.ep.io.dew.DewFile"));
      }
    }
    catch (Exception e) {
      
    }

    
  }
  
  /**
	 * Constructor
	 */
	public ElectricPowerModelFileFactory() {		
	}
	
	/**
	 * Creates an appropriate model file
	 * @param <M>
	 * @param model
	 * @return
	 */
	public ElectricPowerModelFile createModelFile(ElectricPowerModel model) {
	  for (Class<?> cls : classMapRegistry.keySet()) {
	    if (cls.isInstance(model)) {
	      try {
          return (ElectricPowerModelFile) classMapRegistry.get(cls).newInstance();
        }
        catch (InstantiationException | IllegalAccessException e) {
          e.printStackTrace();
        }
	    }
	  }
	  
		return null;
	}
	
	/**
	 * Infer a model based upon the file name extension
	 * @param fileExtension
	 * @return
	 */
	public ElectricPowerModelFile createModelFileFromExtension(String fileExtension) {
	  try {
	    Class<?> cls = extensionMapRegistry.get(fileExtension.toLowerCase());
	    if (cls == null) {
	      System.err.println("File extension: " + fileExtension + " has not been registered");	      
	    }	    
      return (ElectricPowerModelFile) cls.newInstance();
    }
    catch (InstantiationException | IllegalAccessException e) {
      e.printStackTrace();
    }	  
		return null;
	}
	
  /**
   * Infer a model based upon the file name extension
   * @param fileExtension
   * @return
   */
  public ElectricPowerModelFile createModelFile(String filename) {
    int idx = filename.lastIndexOf(".");
    String ext = filename.substring(idx+1,filename.length());    
    return createModelFileFromExtension(ext);
  }
}
