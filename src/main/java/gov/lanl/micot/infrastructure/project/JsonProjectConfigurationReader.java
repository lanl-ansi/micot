package gov.lanl.micot.infrastructure.project;

import gov.lanl.micot.infrastructure.config.AssetModification;
import gov.lanl.micot.util.io.Flags;
import gov.lanl.micot.util.io.FlagsImpl;
import gov.lanl.micot.util.io.json.JSON;
import gov.lanl.micot.util.io.json.JSONArray;
import gov.lanl.micot.util.io.json.JSONObject;
import gov.lanl.micot.util.io.json.JSONReader;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Class for reading in configurations from Json
 * 
 * @author Russell Bent
 */
public class JsonProjectConfigurationReader {

  /**
   * Reads a configuration from a master filename and a single scenario filename
   * @param masterFilename
   * @param xmlScenarioFilename
   * @return
   * @throws IOException
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws ClassNotFoundException
   */
  public ProjectConfiguration readConfiguration(String masterFilename, String scenarioFilename) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
    ArrayList<String> algorithmFilenames = new ArrayList<String>();
    algorithmFilenames.add(masterFilename);

    ArrayList<String> modelFilenames = new ArrayList<String>();
    modelFilenames.add(masterFilename);

    ArrayList<String> simulatorFilenames = new ArrayList<String>();
    simulatorFilenames.add(masterFilename);
    
    ArrayList<String> scenarioFilenames = new ArrayList<String>();
    scenarioFilenames.add(scenarioFilename);
    
    return readConfiguration(masterFilename, algorithmFilenames, modelFilenames, scenarioFilenames, simulatorFilenames);
  }

  /**
   * read the configuration from master file and a collection of scenario files
   * @param masterFilename
   * @param scenarioFilenames
   * @return
   * @throws IOException
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws ClassNotFoundException
   */
  public ProjectConfiguration readConfiguration(String masterFilename, Collection<String> scenarioFilenames) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
    ArrayList<String> algorithmFilenames = new ArrayList<String>();
    algorithmFilenames.add(masterFilename);

    ArrayList<String> modelFilenames = new ArrayList<String>();
    modelFilenames.add(masterFilename);

    ArrayList<String> simulatorFilenames = new ArrayList<String>();
    simulatorFilenames.add(masterFilename);
    
    return readConfiguration(masterFilename, algorithmFilenames, modelFilenames, scenarioFilenames, simulatorFilenames);
  }

  /**
   * Read the configuration from a set of files
   * @param applicationFilename
   * @param algorithmFilename
   * @param modelFilename
   * @param scenarioFilenames
   * @param xmlSimulatorFilename
   * @return
   * @throws IOException
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws ClassNotFoundException
   */
  public ProjectConfiguration readConfiguration(String applicationFilename, String algorithmFilename, String modelFilename, Collection<String> scenarioFilenames, String simulatorFilename) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
    ArrayList<String> algorithmFilenames = new ArrayList<String>();
    algorithmFilenames.add(algorithmFilename);

    ArrayList<String> modelFilenames = new ArrayList<String>();
    modelFilenames.add(modelFilename);

    ArrayList<String> simulatorFilenames = new ArrayList<String>();
    simulatorFilenames.add(simulatorFilename);

    return readConfiguration(applicationFilename, algorithmFilenames, modelFilenames, scenarioFilenames, simulatorFilenames);
  }
 
  /**
   * Read in the configuration files
   * @param applicationFilename
   * @param algorithmFilenames
   * @param modelFilenames
   * @param scenarioFilenames
   * @param simulatorFilenames
   * @return
   * @throws IOException
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws ClassNotFoundException
   */
  public ProjectConfiguration readConfiguration(String applicationFilename, Collection<String> algorithmFilenames, Collection<String> modelFilenames, Collection<String> scenarioFilenames, Collection<String> simulatorFilenames) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {    
    try {
      Collection<InputStream> algorithms = new ArrayList<InputStream>();
      Collection<InputStream> models = new ArrayList<InputStream>();
      Collection<InputStream> scenarios = new ArrayList<InputStream>();
      Collection<InputStream> simulators = new ArrayList<InputStream>();

      // get the algorithms
      for (String filename : algorithmFilenames) {
        FileInputStream fstream = new FileInputStream(filename);
        algorithms.add(fstream);
      }
      
      // get the models
      for (String filename : modelFilenames) {
        FileInputStream fstream = new FileInputStream(filename);
        models.add(fstream);
      }

      // get the scenarios
      for (String filename : scenarioFilenames) {
        FileInputStream fstream = new FileInputStream(filename);
        scenarios.add(fstream);
      }

      // get the simulators
      for (String filename : simulatorFilenames) {
        FileInputStream fstream = new FileInputStream(filename);
        simulators.add(fstream);
      }
      
      FileInputStream fstream = new FileInputStream(applicationFilename);
      return readConfiguration(fstream, algorithms, models, scenarios, simulators);
    }
    catch (FileNotFoundException e) {
      throw new RuntimeException(e.getMessage());
    }
  }
    
  /**
   * Read data from streams
   * @param applicationFilename
   * @param algorithmFilenames
   * @param modelFilenames
   * @param scenarioFilenames
   * @param simulatorFilenames
   * @return
   * @throws IOException
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws ClassNotFoundException
   */
  public ProjectConfiguration readConfiguration(InputStream applicationFilename, Collection<InputStream> algorithmFilenames, Collection<InputStream> modelFilenames, Collection<InputStream> scenarioFilenames, Collection<InputStream> simulatorFilenames) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {    
    ProjectConfiguration configuration = null;
      Collection<JSONObject> algorithms = new ArrayList<JSONObject>();
      Collection<JSONObject> models = new ArrayList<JSONObject>();
      Collection<JSONObject> scenarios = new ArrayList<JSONObject>();
      Collection<JSONObject> simulators = new ArrayList<JSONObject>();
      JSONObject application = null;

      // get the algorithms
      for (InputStream fstream : algorithmFilenames) {
        JSONReader reader = JSON.getDefaultJSON().createReader(fstream);
        JSONObject object = reader.readObject();
        algorithms.add(object);
      }
      
      // get the models
      for (InputStream fstream : modelFilenames) {
        JSONReader reader = JSON.getDefaultJSON().createReader(fstream);
        JSONObject object = reader.readObject();
        models.add(object);
      }

      // get the scenarios
      for (InputStream fstream : scenarioFilenames) {
        JSONReader reader = JSON.getDefaultJSON().createReader(fstream);
        JSONObject object = reader.readObject();
        scenarios.add(object);
      }

      // get the simulators
      for (InputStream fstream : simulatorFilenames) {
        JSONReader reader = JSON.getDefaultJSON().createReader(fstream);
        JSONObject object = reader.readObject();
        simulators.add(object);
      }
      
      InputStream fstream = applicationFilename;
      JSONReader reader = JSON.getDefaultJSON().createReader(fstream);
      application = reader.readObject();      
      configuration = createConfigurationFrom(application, algorithms, models, scenarios, simulators);
    return configuration;
  }


  /**
   * 
   * @param content
   * @return
   * @throws ClassNotFoundException
   * @throws IllegalAccessException
   * @throws InstantiationException
   * @throws ConfigXMLIOException
   */
  public ProjectConfiguration readConfigurationFromContent(String content) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
    Collection<String> algorithmContents = new ArrayList<String>();
    algorithmContents.add(content);
    
    Collection<String> modelContents = new ArrayList<String>();
    modelContents.add(content);

    Collection<String> scenarioContents = new ArrayList<String>();
    scenarioContents.add(content);

    Collection<String> simulatorContents = new ArrayList<String>();
    simulatorContents.add(content);
    
    return readConfigurationFromContent(content, algorithmContents, modelContents, scenarioContents, simulatorContents);    
  }

  /**
   * Splits the content into a scenario and a master file
   * @param xmlMasterContent
   * @param xmlScenarioContent
   * @param properties
   * @return
   * @throws ClassNotFoundException
   * @throws IllegalAccessException
   * @throws InstantiationException
   * @throws ConfigXMLIOException
   */
  public ProjectConfiguration readConfigurationFromContent(String applicationContent, Collection<String> algorithmContents, Collection<String> modelContents, Collection<String> scenarioContents, Collection<String> simulatorContents) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
    Collection<JSONObject> algorithms = new ArrayList<JSONObject>();
    Collection<JSONObject> models = new ArrayList<JSONObject>();
    Collection<JSONObject> scenarios = new ArrayList<JSONObject>();
    Collection<JSONObject> simulators = new ArrayList<JSONObject>();
    JSONObject application = null;

    for (String content : algorithmContents) {
      JSONObject document = parse(content);
      algorithms.add(document);
    }
                
    for (String content : modelContents) {
      JSONObject document = parse(content);
      models.add(document);
    }

    for (String content : scenarioContents) {
      JSONObject document = parse(content);
      scenarios.add(document);
    }

    for (String content : simulatorContents) {
      JSONObject document = parse(content);
      simulators.add(document);
    }
    
    application = parse(applicationContent);    
    return createConfigurationFrom(application,algorithms, models, scenarios, simulators);
  }


  /**
   * 
   * Pull the data from the JSON objects
   * 
   * @param applicationObject
   * @param algorithmObjects
   * @param modelObjects
   * @param scenarioObjects
   * @param simulatorObjects
   * @return
   * @throws ConfigXMLIOException
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws ClassNotFoundException
   */
  private ProjectConfiguration createConfigurationFrom(JSONObject applicationObject, Collection<JSONObject> algorithmObjects, Collection<JSONObject> modelObjects, Collection<JSONObject> scenarioObjects, Collection<JSONObject> simulatorObjects) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
    ProjectConfiguration configuration = new ProjectConfiguration();

    // get the application data
    JSONObject application = applicationObject.getObject(JsonConfigurationUtilities.APPLICATION_TAG);
    readApplication(configuration, application);
    
    // get the algorithm data
    for (JSONObject object : algorithmObjects) {
      JSONArray algorithms = object.getArray(JsonConfigurationUtilities.ALGORITHM_TAG);
      for (int i = 0; i < algorithms.size(); ++i) {
        readAlgorithm(configuration, algorithms.getObject(i));
      }
    }
        
    // get the model data
    for (JSONObject object : modelObjects) {
      JSONArray models = object.getArray(JsonConfigurationUtilities.MODEL_TAG);
      for (int i = 0; i < models.size(); ++i) {
        JSONObject model = models.getObject(i);
        readModel(configuration, model);
      }
    }

    // get the simulator data
    for (JSONObject object : simulatorObjects) {
      JSONArray simulators = object.getArray(JsonConfigurationUtilities.SIMULATOR_TAG);
      for (int i = 0; i < simulators.size(); ++i) {
        JSONObject simulator = simulators.getObject(i);
        readSimulator(configuration,simulator);
      }
    }

    // count number of scenarios
    double numScenarios = 0;
    for (JSONObject object : scenarioObjects) {
      JSONArray scenarios = object.getArray(JsonConfigurationUtilities.SCENARIOS_TAG);
      numScenarios += scenarios.size();
    }
    
    // get the scenario data
    int idx = 1;
    for (JSONObject object : scenarioObjects) {
      JSONArray scenarios = object.getArray(JsonConfigurationUtilities.SCENARIOS_TAG);
      for (int i = 0; i < scenarios.size(); ++i) {
        JSONObject scenario = scenarios.getObject(i);
        readScenario(configuration, scenario, idx, 1.0 / numScenarios);
        ++idx;
      }      
    }
    return configuration;
  }

  /**
   * Fill the configuration data
   * @param configuration
   * @param scenarioDocument
   * @throws ConfigXMLIOException 
   */
  private void readScenario(ProjectConfiguration configuration, JSONObject scenario, int defaultIdx, double defaultProb) {
    double probability = scenario.containsKey(JsonConfigurationUtilities.SCENARIO_PROBABILITY_TAG) ? scenario.getDouble(JsonConfigurationUtilities.SCENARIO_PROBABILITY_TAG) : defaultProb;    
    int idx = scenario.containsKey(JsonConfigurationUtilities.SCENARIO_INDEX_TAG) ? scenario.getInt(JsonConfigurationUtilities.SCENARIO_INDEX_TAG) : defaultIdx;    
    ScenarioConfiguration scenarioConfiguration = new ScenarioConfiguration(idx, probability);

    String build = scenario.containsKey(JsonConfigurationUtilities.SCENARIO_BUILDER_TAG) ? scenario.getString(JsonConfigurationUtilities.SCENARIO_BUILDER_TAG) : "";    
    scenarioConfiguration.setScenarioBuilderFactoryClass(build);

    Flags flags = new FlagsImpl();
    for (String key : scenario.getKeys()) {
      if (!key.equals(JsonConfigurationUtilities.SCENARIO_BUILDER_TAG) && 
          !key.equals(JsonConfigurationUtilities.SCENARIO_PROBABILITY_TAG) && 
          !key.equals(JsonConfigurationUtilities.SCENARIO_INDEX_TAG) && 
          !key.equals(JsonConfigurationUtilities.MODIFICATIONS_TAG)) {
        flags.put(key, scenario.getString(key));
      }
    }
    scenarioConfiguration.setScenarioBuilderFlags(flags);
    
    JSONArray emodifications = scenario.getArray(JsonConfigurationUtilities.MODIFICATIONS_TAG);
    Collection<AssetModification> modifications = readModifications(emodifications);    
    scenarioConfiguration.setComponentModifications(modifications);
    configuration.addScenarioConfiguration(scenarioConfiguration);    
  }
  
  /**
   * Read the algorithm
   * @param configuration
   * @param log
   * @param path
   * @throws ConfigXMLIOException
   */
  private void readAlgorithm(ProjectConfiguration projectConfiguration, JSONObject algorithm)  {    
    AlgorithmConfiguration configuration = new AlgorithmConfiguration();    
    configuration.setAlgorithmFactoryClass(algorithm.getString(JsonConfigurationUtilities.ALGORITHM_FACTORY_TAG));
    Flags flags = new FlagsImpl();
    for (String key : algorithm.getKeys()) {
      if (!key.equals(JsonConfigurationUtilities.ALGORITHM_FACTORY_TAG)) {
        flags.put(key, algorithm.getString(key));
      }
    }
    configuration.setAlgorithmFlags(flags);    
    projectConfiguration.addAlgorithmConfiguration(configuration);
  }

  /**
   * Read the application data
   * @param projectConfiguration
   * @param application
   * @throws ConfigXMLIOException
   */
  private void readApplication(ProjectConfiguration projectConfiguration, JSONObject application)  {
    ApplicationConfiguration configuration = new ApplicationConfiguration();    
    configuration.setApplicationFactoryClass(application.getString(JsonConfigurationUtilities.APPLICATION_FACTORY_TAG));
    Flags flags = new FlagsImpl();
    for (String key : application.getKeys()) {
      if (!key.equals(JsonConfigurationUtilities.APPLICATION_FACTORY_TAG)) {
        flags.put(key, application.getString(key));
      }
    }
    configuration.setApplicationFlags(flags);    
    projectConfiguration.setApplicationConfiguration(configuration);
  }

  /**
   * Read the algorithm
   * @param configuration
   * @param log
   * @param path
   * @throws ConfigXMLIOException
   */
  private void readSimulator(ProjectConfiguration projectConfiguration, JSONObject simulator) {    
    SimulatorConfiguration configuration = new SimulatorConfiguration();    
    configuration.setSimulatorFactoryClass(simulator.getString(JsonConfigurationUtilities.SIMULATOR_FACTORY_TAG));
    Flags flags = new FlagsImpl();
    for (String key : simulator.getKeys()) {
      if (!key.equals(JsonConfigurationUtilities.SIMULATOR_FACTORY_TAG)) {
        flags.put(key, simulator.getString(key));
      }
    }
    configuration.setSimulatorFlags(flags);    
    projectConfiguration.addSimulatorConfiguration(configuration);
  }
  
  /**
   * Reads in the model data
   * @param configuration
   * @param model
   * @throws ConfigXMLIOException
   */
  private void readModel(ProjectConfiguration projectConfiguration, JSONObject model)  {
    ModelConfiguration configuration = new ModelConfiguration();
    String modelFileFactoryClass = model.getString(JsonConfigurationUtilities.MODEL_FILE_FACTORY_TAG);
    configuration.setModelFileFactoryClass(modelFileFactoryClass);
    String modelFile = model.getString(JsonConfigurationUtilities.MODEL_FILE_TAG);
    configuration.setModelFile(modelFile);

    JSONArray modifications = model.getArray(JsonConfigurationUtilities.MODIFICATIONS_TAG);
    Collection<AssetModification> mods = readModifications(modifications); 
    configuration.setComponentModifications(mods);
    projectConfiguration.addModelConfiguration(configuration);    
  }
  
  /**
   * Reads in the model modification data
   * @param configuration
   * @param modifications
   * @throws ConfigXMLIOException 
   */
  public Collection<AssetModification> readModifications(JSONArray modifications)  {
    ArrayList<AssetModification> mods = new ArrayList<AssetModification>();
    // get the modifications
    if (modifications != null) { 
      for (int i = 0; i < modifications.size(); ++i) {
        JSONObject element = modifications.getObject(i);
        AssetModification config = new AssetModification();
        String type = element.getString(JsonConfigurationUtilities.MODIFICATION_COMPONENT_TAG);
        config.setComponentClass(type);
        
        JSONArray ids = element.getArray(JsonConfigurationUtilities.ID_TAG);
        for (int j = 0; j < ids.size(); ++j) {
          JSONObject id = ids.getObject(j);          
          String key = id.getString(JsonConfigurationUtilities.MODIFICATION_IDENTIFIER_KEY_TAG);
          String eType = id.getString(JsonConfigurationUtilities.MODIFICATION_IDENTIFIER_TYPE_TAG);                    
                  
          if (JsonConfigurationUtilities.INT_TAG.equals(eType)) {
            config.addKey(key, id.getInt(JsonConfigurationUtilities.MODIFICATION_IDENTIFIER_VALUE_TAG));
          }
          else if (JsonConfigurationUtilities.DOUBLE_TAG.equals(eType)) {
            config.addKey(key, id.getDouble(JsonConfigurationUtilities.MODIFICATION_IDENTIFIER_VALUE_TAG));
          }
          else if (JsonConfigurationUtilities.BOOLEAN_TAG.equals(eType)) {
            config.addKey(key, id.getBoolean(JsonConfigurationUtilities.MODIFICATION_IDENTIFIER_VALUE_TAG));
          }
          else {
            config.addKey(key, id.getString(JsonConfigurationUtilities.MODIFICATION_IDENTIFIER_VALUE_TAG));
          }
        }
        
        JSONArray attributes = element.getArray(JsonConfigurationUtilities.MODIFICATION_ATTRIBUTE_TAG);
        for (int j = 0; j < attributes.size(); ++j) {
          JSONObject a = attributes.getObject(j);          
          String key = a.getString(JsonConfigurationUtilities.MODIFICATION_ATTRIBUTE_KEY_TAG);
          String eType = a.getString(JsonConfigurationUtilities.MODIFICATION_ATTRIBUTE_TYPE_TAG);                              
          if (JsonConfigurationUtilities.INT_TAG.equals(eType)) {
            config.addAttribute(key, a.getInt(JsonConfigurationUtilities.MODIFICATION_ATTRIBUTE_VALUE_TAG));
          }
          else if (JsonConfigurationUtilities.DOUBLE_TAG.equals(eType)) {
            config.addAttribute(key, a.getDouble(JsonConfigurationUtilities.MODIFICATION_ATTRIBUTE_VALUE_TAG));
          }
          else if (JsonConfigurationUtilities.FLOAT_TAG.equals(eType)) {
            config.addAttribute(key, a.getDouble(JsonConfigurationUtilities.MODIFICATION_ATTRIBUTE_VALUE_TAG));
          }
          else if (JsonConfigurationUtilities.BOOLEAN_TAG.equals(eType)) {
            config.addAttribute(key, a.getBoolean(JsonConfigurationUtilities.MODIFICATION_ATTRIBUTE_VALUE_TAG));
          }
          else {
            config.addAttribute(key, a.getString(JsonConfigurationUtilities.MODIFICATION_ATTRIBUTE_VALUE_TAG));
          }
        }
 
        mods.add(config);
      }
    }
      return mods;
  }
    
  /**
   * Parse the provided string content into a JsonObject.
   * @return
   */
  private JSONObject parse(String content) {
    InputStream stream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));    
    JSONReader reader = JSON.getDefaultJSON().createReader(stream);
    JSONObject object = reader.readObject();
    return object;
  }
  

  /**
   * Everything glommed together
   * @param projectFile
   * @param properties
   * @throws IOException 
   * @throws ClassNotFoundException 
   * @throws IllegalAccessException 
   * @throws InstantiationException 
   */
  public ProjectConfiguration readConfiguration(String projectFile) throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException {
    ArrayList<String> xmlAlgorithmFilenames = new ArrayList<String>();
    xmlAlgorithmFilenames.add(projectFile);

    ArrayList<String> xmlModelFilenames = new ArrayList<String>();
    xmlModelFilenames.add(projectFile);

    ArrayList<String> xmlSimulatorFilenames = new ArrayList<String>();
    xmlSimulatorFilenames.add(projectFile);

    ArrayList<String> xmlScenarioFilenames = new ArrayList<String>();
    xmlScenarioFilenames.add(projectFile);
    
    return readConfiguration(projectFile, xmlAlgorithmFilenames, xmlModelFilenames, xmlScenarioFilenames, xmlSimulatorFilenames);
    
  }
  
  /**
   * Everything glommed together
   * @param projectFile
   * @param properties
   * @throws IOException 
   * @throws ClassNotFoundException 
   * @throws IllegalAccessException 
   * @throws InstantiationException 
   */
  public ProjectConfiguration readConfiguration(InputStream masterStream, InputStream algorithmStream, InputStream modelStream, InputStream scenarioStream, InputStream simulatorStream) throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException {
    ArrayList<InputStream> xmlAlgorithmFilenames = new ArrayList<InputStream>();
    xmlAlgorithmFilenames.add(algorithmStream);

    ArrayList<InputStream> xmlModelFilenames = new ArrayList<InputStream>();
    xmlModelFilenames.add(modelStream);

    ArrayList<InputStream> xmlSimulatorFilenames = new ArrayList<InputStream>();
    xmlSimulatorFilenames.add(simulatorStream);

    ArrayList<InputStream> xmlScenarioFilenames = new ArrayList<InputStream>();
    xmlScenarioFilenames.add(scenarioStream);
    
    return readConfiguration(masterStream, xmlAlgorithmFilenames, xmlModelFilenames, xmlScenarioFilenames, xmlSimulatorFilenames);
    
  }
    
  /**
   * Read data from a single stream
   * @param fstream
   * @return
   * @throws IOException
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws ClassNotFoundException
   */
  public ProjectConfiguration readConfiguration(InputStream fstream) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {    
    ProjectConfiguration configuration = null;
      Collection<JSONObject> algorithms = new ArrayList<JSONObject>();
      Collection<JSONObject> models = new ArrayList<JSONObject>();
      Collection<JSONObject> scenarios = new ArrayList<JSONObject>();
      Collection<JSONObject> simulators = new ArrayList<JSONObject>();

      JSONReader reader = JSON.getDefaultJSON().createReader(fstream);
      JSONObject object = reader.readObject();

      algorithms.add(object);
      models.add(object);
      scenarios.add(object);
      simulators.add(object);
      
      configuration = createConfigurationFrom(object, algorithms, models, scenarios, simulators);
    return configuration;
  }

  
  
  

}
