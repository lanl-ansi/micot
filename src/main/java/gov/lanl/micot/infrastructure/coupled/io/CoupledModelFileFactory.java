package gov.lanl.micot.infrastructure.coupled.io;

//import gov.lanl.micot.infrastructure.coupled.io.impl.CoupledModelFileImpl;
import gov.lanl.micot.infrastructure.coupled.model.CoupledModel;
//import gov.lanl.micot.infrastructure.coupled.model.impl.DefaultCoupledModelImpl;
import gov.lanl.micot.infrastructure.io.ModelFileFactory;


/**
 * Factory for creating model files
 * @author Russell Bent
 */
public class CoupledModelFileFactory implements ModelFileFactory<CoupledModel, CoupledModelFile> {

	/**
	 * Constructor
	 */
	public CoupledModelFileFactory() {		
	}
	
	/**
	 * Creates an appropriate model file
	 * @param <M>
	 * @param model
	 * @return
	 */
	public CoupledModelFile createModelFile(CoupledModel model) {
//		if (model instanceof DefaultCoupledModelImpl) {
	//		return new CoupledModelFileImpl();
	//	}
		return null;
	}
	
	/**
	 * Infer a model based upon the file name extension
	 * @param fileExtension
	 * @return
	 */
	public CoupledModelFile createModelFileFromExtension(String fileExtension) {
		//if (fileExtension.equalsIgnoreCase("xml")) {
			//return new CoupledModelFileImpl();
		//}
		return null;
	}
	
  /**
   * Infer a model based upon the file name extension
   * @param fileExtension
   * @return
   */
  public CoupledModelFile createModelFile(String filename) {
    int idx = filename.lastIndexOf(".");
    String ext = filename.substring(idx+1,filename.length());    
    return createModelFileFromExtension(ext);
  }

	
}
