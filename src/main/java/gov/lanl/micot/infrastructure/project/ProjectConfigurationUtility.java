package gov.lanl.micot.infrastructure.project;

import gov.lanl.micot.infrastructure.application.Application;
import gov.lanl.micot.infrastructure.application.ApplicationFactory;
import gov.lanl.micot.infrastructure.config.AssetModification;
import gov.lanl.micot.infrastructure.io.ModelFile;
import gov.lanl.micot.infrastructure.io.ModelFileFactory;
import gov.lanl.micot.infrastructure.model.Asset;
import gov.lanl.micot.infrastructure.model.AssetModifierImpl;
import gov.lanl.micot.infrastructure.model.Model;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.optimize.Optimizer;
import gov.lanl.micot.infrastructure.optimize.OptimizerFactory;
import gov.lanl.micot.infrastructure.project.AlgorithmConfiguration;
import gov.lanl.micot.infrastructure.project.ModelConfiguration;
import gov.lanl.micot.infrastructure.project.ProjectConfiguration;
import gov.lanl.micot.infrastructure.project.ScenarioConfiguration;
import gov.lanl.micot.infrastructure.simulate.Simulator;
import gov.lanl.micot.infrastructure.simulate.SimulatorFactory;

import java.io.IOException;
import java.util.Set;

/**
 * General set of static methods to convert data in the configuration
 * into appropriate data structures
 * 
 * @author Russell Bent
 *
 */
public class ProjectConfigurationUtility {
  
  /**
   * No constructor
   */
  private ProjectConfigurationUtility() {    
  }
  
  /**
   * Modify the model according to the instructions in the configuration
   * @param configuration
   * @param model
   * @throws ClassNotFoundException
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public static void modifyModel(Model model, ProjectConfiguration projectConfiguration, ModelConfiguration modelConfiguration) throws ClassNotFoundException {

    // set the master data
    for (AssetModification config : modelConfiguration.getComponentModifications()) {
      String componentClass = config.getComponentClass();
      Class cls = Class.forName(componentClass);
      Set<Asset> assets = model.getAssets(cls);
      
      boolean found = false;
      for (Asset asset : assets) {
        boolean ok = true;
        for (String key : config.getKeys().keySet()) {
          Object value = config.getKeys().get(key);
          if (!asset.getAttribute(key).equals(value) && !asset.getAttribute(key).toString().equals(value.toString())) {
            ok = false;
          }
        }
        if (ok) {
          found = true;
          for (String key2 : config.getAttributes().keySet()) {
            asset.setAttribute(key2, config.getAttributes().get(key2));
          }
        }
      }

      if (!found) {
        System.err.println("Could not find asset " + config.getKeys() + " to modify attribute " + config.getAttributes().keySet());
      }
    }
    

    // set the scenario data
    for (ScenarioConfiguration s : projectConfiguration.getScenarioConfigurations()) {
      Scenario scenario = s.getScenario();
      for (AssetModification config : s.getComponentModifications()) {
        String componentClass = config.getComponentClass();       
        Class cls = Class.forName(componentClass);
        Set<Asset> assets = model.getAssets(cls);
        
        boolean found = false;
        for (Asset asset : assets) {
          boolean ok = true;
          for (String key : config.getKeys().keySet()) {
            Object value = config.getKeys().get(key);
            if (!asset.getAttribute(key).equals(value) && !asset.getAttribute(key).toString().equals(value.toString())) {
              ok = false;
            }
          }

          // found the asset
          if (ok) {
            found = true;
            for (String key2 : config.getAttributes().keySet()) {
              AssetModifierImpl impl = new AssetModifierImpl(key2, config.getAttributes().get(key2));
              scenario.addModification(asset, impl);              
            }
          }
        }

        if (!found) {
          System.err.println("Could not find asset " + config.getKeys()  + " to modify attribute " + config.getAttributes().keySet());
        }
      }
    }
  }
  
  /**
   * Create a model from the configuration
   * @return
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws ClassNotFoundException
   * @throws IOException
   */
  @SuppressWarnings("rawtypes")
  public static Model createModel(ModelConfiguration configuration) throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException {
    String inputFile = configuration.getModelFile();
    String extension = inputFile.substring(inputFile.lastIndexOf(".") + 1);
    ModelFileFactory modelFileFactory = (ModelFileFactory) Class.forName(configuration.getModelFileFactoryClass()).newInstance();
    ModelFile modelFile = modelFileFactory.createModelFileFromExtension(extension);
    return modelFile.readModel(inputFile);
  }

  /**
   * Create the optimizer
   * @param configuration
   * @param model
   * @return
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws ClassNotFoundException
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public static Optimizer createOptimizer(ProjectConfiguration projectConfiguration, AlgorithmConfiguration configuration, Model model) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
    if (configuration == null) {
      return null;
    }
    
    OptimizerFactory factory = (OptimizerFactory) Class.forName(configuration.getAlgorithmFactoryClass()).newInstance();
    Optimizer algorithm = factory.constructOptimizer(projectConfiguration, configuration, model);
    return algorithm;
  }

  /**
   * Create the application
   * @param configuration
   * @param model
   * @return
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws ClassNotFoundException
   * @throws IOException 
   */
  public static Application createApplication(ProjectConfiguration configuration) throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException {
    if (configuration == null) {
      return null;
    }
    
    ApplicationFactory factory = (ApplicationFactory) Class.forName(configuration.getApplicationConfiguration().getApplicationFactoryClass()).newInstance();
    Application application = factory.createApplication(configuration);
    return application;
  }
  
  /**
   * Create the optimizaer
   * @param configuration
   * @param model
   * @return
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws ClassNotFoundException
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public static Simulator createSimulator(ProjectConfiguration projectConfiguration, SimulatorConfiguration configuration, Model model) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
    SimulatorFactory factory = (SimulatorFactory) Class.forName(configuration.getSimulatorFactoryClass()).newInstance();
    Simulator simulator = factory.constructSimulator(projectConfiguration, configuration, model);
    return simulator;
  }
  
}
