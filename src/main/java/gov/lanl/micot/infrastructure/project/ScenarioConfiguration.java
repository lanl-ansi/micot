package gov.lanl.micot.infrastructure.project;

import java.util.ArrayList;
import java.util.Collection;

import gov.lanl.micot.infrastructure.config.AssetModification;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.util.io.Flags;
import gov.lanl.micot.util.io.FlagsImpl;

/**
 * Scenario Configuration
 * @author Russell Bent
 */
public class ScenarioConfiguration {

  private Collection<AssetModification> componentModifications        = null;
  private String scenarioBuilderFactoryClass                          = null;
  private Flags scenarioBuilderFlags                                  = null;
  private Scenario scenario                                           = null;

    
  /**
   * Constructor
   */
  public ScenarioConfiguration(int idx, double probability) { 
    componentModifications = new ArrayList<AssetModification>();
    scenarioBuilderFlags = new FlagsImpl();
    scenario = new Scenario(idx, probability);
  }

	/**
	 * @return the algorithmFactoryClass
	 */
	public String getScenarioBuilderFactoryClass() {
		return scenarioBuilderFactoryClass;
	}

	/**
	 * @param algorithmFactoryClass the algorithmFactoryClass to set
	 */
	public void setScenarioBuilderFactoryClass(String scenarioBuilderFactoryClass) {
		this.scenarioBuilderFactoryClass = scenarioBuilderFactoryClass;
	}

	/**
	 * @return the algorithmFlags
	 */
	public Flags getScenarioBuilderFlags() {
		return scenarioBuilderFlags;
	}

	/**
	 * @param algorithmFlags the algorithmFlags to set
	 */
	public void setScenarioBuilderFlags(Flags scenarioBuilderFlags) {
		this.scenarioBuilderFlags = scenarioBuilderFlags;
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

  /**
   * Adds an algorithm flag
   * @param key
   * @param value
   */
  public void addScenarioBuilderFlag(String key, Object value) {
    scenarioBuilderFlags.put(key, value);
  }

  /**
   * Gets the scenario object
   * @return
   */
  public Scenario getScenario() {
    return scenario;
  }
}
