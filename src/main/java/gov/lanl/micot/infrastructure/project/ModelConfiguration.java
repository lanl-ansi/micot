package gov.lanl.micot.infrastructure.project;

import java.util.ArrayList;
import java.util.Collection;

import gov.lanl.micot.infrastructure.config.AssetModification;
import gov.lanl.micot.util.io.Flags;
import gov.lanl.micot.util.io.FlagsImpl;

/**
 * Model Configuration
 * @author Russell Bent
 */
public class ModelConfiguration {

  private Collection<AssetModification> componentModifications        = null;
  private String modelFileFactoryClass                                = null;
  private String modelFile                                            = null;    
  /**
   * Constructor
   */
  public ModelConfiguration() { 
    componentModifications = new ArrayList<AssetModification>();
  }

	/**
	 * @return the algorithmFactoryClass
	 */
	public String getModelFileFactoryClass() {
		return modelFileFactoryClass;
	}

	/**
	 * @param algorithmFactoryClass the algorithmFactoryClass to set
	 */
	public void setModelFileFactoryClass(String modelFileFactoryClass) {
		this.modelFileFactoryClass = modelFileFactoryClass;
	}

	/**
	 * @return the algorithmFlags
	 */
	public String getModelFile() {
		return modelFile;
	}

	/**
	 * @param algorithmFlags the algorithmFlags to set
	 */
	public void setModelFile(String modelFile) {
		this.modelFile = modelFile;
	}

  /**
   * @return the componentModifications
   */
  public Collection<AssetModification> getComponentModifications() {
    return componentModifications;
  }

  /**
   * @param componentModifications the componentModifications to set
   */
  public void setComponentModifications(Collection<AssetModification> componentModifications) {
    this.componentModifications = componentModifications;
  }
  
  /**
   * Add the component modifications
   * @param config
   */
  public void addComponentModification(AssetModification config) {
    componentModifications.add(config);
  }
}
