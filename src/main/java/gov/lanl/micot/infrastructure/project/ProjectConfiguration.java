package gov.lanl.micot.infrastructure.project;

import java.util.ArrayList;
import java.util.Collection;

import gov.lanl.micot.infrastructure.model.Scenario;

/**
 * General configuration information
 * @author Russell Bent
 */
public class ProjectConfiguration {

  private OutputConfiguration outputConfiguration                     = null;
  private ApplicationConfiguration applicationConfiguration           = null;  
  private Collection<AlgorithmConfiguration> algorithms               = null;
  private Collection<SimulatorConfiguration> simulators               = null;
  private Collection<ModelConfiguration> models                       = null;
  private Collection<ScenarioConfiguration> scenarios                 = null;
    
  /**
   * Constructor
   */
  public ProjectConfiguration() { 
    models = new ArrayList<ModelConfiguration>();
    algorithms = new ArrayList<AlgorithmConfiguration>();
    simulators = new ArrayList<SimulatorConfiguration>();
    scenarios = new ArrayList<ScenarioConfiguration>();
    outputConfiguration = new OutputConfiguration();
  }

	/**
   * @return the output configuration
   */
  public OutputConfiguration getOutputConfiguration() {
    return outputConfiguration;
  }

  /**
   * @param statusLog the statusLog to set
   */
  public void setOutputConfiguration(OutputConfiguration outputConfiguration) {
    this.outputConfiguration = outputConfiguration;
  }

	/**
	 * @return the algorithmFactoryClass
	 */
	public Collection<AlgorithmConfiguration> getAlgorithmConfigurations() {
		return algorithms;
	}

	/**
	 * @param algorithmFactoryClass the algorithmFactoryClass to set
	 */
	public void setAlgorithmConfigurations(Collection<AlgorithmConfiguration> algorithms) {
		this.algorithms = algorithms;
	}
	
	 /**
   * @return the algorithmFactoryClass
   */
  public Collection<ScenarioConfiguration> getScenarioConfigurations() {
    return scenarios;
  }

  /**
   * @param algorithmFactoryClass the algorithmFactoryClass to set
   */
  public void setScenarioConfigurations(Collection<ScenarioConfiguration> scenarios) {
    this.scenarios = scenarios;
  }

	
	 /**
   * @return the algorithmFactoryClass
   */
  public Collection<SimulatorConfiguration> getSimulatorConfigurations() {
    return simulators;
  }

  /**
   * @param algorithmFactoryClass the algorithmFactoryClass to set
   */
  public void setSimulatorConfigurations(Collection<SimulatorConfiguration> simulators) {
    this.simulators = simulators;
  }
	
  /**
   * Adds an algorithm flag
   * @param key
   * @param value
   */
  public void addAlgorithmConfiguration(AlgorithmConfiguration configuration) {
    algorithms.add(configuration);
  }

  
  /**
   * Adds an algorithm flag
   * @param key
   * @param value
   */
  public void addSimulatorConfiguration(SimulatorConfiguration configuration) {
    simulators.add(configuration);
  }

  /**
   * Adds an algorithm flag
   * @param key
   * @param value
   */
  public void addScenarioConfiguration(ScenarioConfiguration configuration) {
    scenarios.add(configuration);
  }
  
  /**
   * Adds a model configuration
   * @param configuration
   */
  public void addModelConfiguration(ModelConfiguration configuration) {
    models.add(configuration);
  }

  /**
   * @return the models
   */
  public Collection<ModelConfiguration> getModels() {
    return models;
  }

  /**
   * @param models the models to set
   */
  public void setModels(Collection<ModelConfiguration> models) {
    this.models = models;
  }

  /**
   * Get first model
   * @return
   */
  public ModelConfiguration getFirstModel() {
    if (models.size() > 0) {
      return models.iterator().next();
    }
    return null;
  }

  /**
   * Get the first algorithm configuration
   * @return
   */
  public AlgorithmConfiguration getFirstAlgorithm() {
    if (algorithms.size() > 0) {
      return algorithms.iterator().next();
    }
    return null;
  }

  /**
   * Get rid of all scenarios
   */
  public void removeScenarios() {
    scenarios.clear();
  }

  public SimulatorConfiguration getFirstSimulator() {
    if (simulators.size() > 0) {
      return simulators.iterator().next();
    }
    return null;
  }

  public Scenario getFirstScenario() {
    if (scenarios.size() > 0) {
      return scenarios.iterator().next().getScenario();
    }
    return null;
  }
  
  /**
   * Gets the application configuration
   * @param fig
   */
  public void setApplicationConfiguration(ApplicationConfiguration fig) {
    applicationConfiguration = fig;
  }
  
  /**
   * Gets the application configuration
   * @return
   */
  public ApplicationConfiguration getApplicationConfiguration() {
    return applicationConfiguration;
  }
  
}
