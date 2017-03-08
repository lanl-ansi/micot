package gov.lanl.micot.infrastructure.naturalgas.io;

import java.util.HashMap;
import java.util.Map;

import gov.lanl.micot.infrastructure.io.ModelFileFactory;
import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasModel;

/**
 * Factory for creating model files
 * @author Russell Bent
 */
public class NaturalGasModelFileFactory implements ModelFileFactory<NaturalGasModel, NaturalGasModelFile> {

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
      if (Class.forName("gov.lanl.micot.infrastructure.naturalgas.model.txt.TxtModel") != null) {
        registerClass(Class.forName("gov.lanl.micot.infrastructure.naturalgas.model.txt.TxtModel"), Class.forName("gov.lanl.micot.infrastructure.naturalgas.io.txt.TxtModelFile"));
        registerExtension("txt",Class.forName("gov.lanl.micot.infrastructure.naturalgas.io.txt.TxtModelFile"));
      }
    }
    catch (Exception e) {      
    }
    
    try {      
      if (Class.forName("gov.lanl.micot.infrastructure.naturalgas.model.gaslib.GaslibModel") != null) {
        registerClass(Class.forName("gov.lanl.micot.infrastructure.naturalgas.model.gaslib.GaslibModel"), Class.forName("gov.lanl.micot.infrastructure.naturalgas.io.gaslib.GaslibModelFile"));
        registerExtension("gaslib",Class.forName("gov.lanl.micot.infrastructure.naturalgas.io.gaslib.GaslibModelFile"));
      }
    }
    catch (Exception e) {      
    }
  }

  
	/**
	 * Constructor
	 */
	public NaturalGasModelFileFactory() {		
	}
	
	/**
	 * Creates an appropriate model file
	 * @param <M>
	 * @param model
	 * @return
	 */
	public NaturalGasModelFile createModelFile(NaturalGasModel model) {
    for (Class<?> cls : classMapRegistry.keySet()) {
      if (cls.isInstance(model)) {
        try {
          return (NaturalGasModelFile) classMapRegistry.get(cls).newInstance();
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
	public NaturalGasModelFile createModelFileFromExtension(String fileExtension) {
    try {
      Class<?> cls = extensionMapRegistry.get(fileExtension.toLowerCase());
      if (cls == null) {
        System.err.println("File extension: " + fileExtension + " has not been registered");        
      }     
      return (NaturalGasModelFile) cls.newInstance();
    }
    catch (InstantiationException | IllegalAccessException e) {
      e.printStackTrace();
    }   
    return null;
	}

  @Override
  public NaturalGasModelFile createModelFile(String filename) {
    int idx = filename.lastIndexOf(".");
    String ext = filename.substring(idx+1,filename.length());    
    return createModelFileFromExtension(ext);
  }

	
}
