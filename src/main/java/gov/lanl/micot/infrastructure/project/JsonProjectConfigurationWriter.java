package gov.lanl.micot.infrastructure.project;

import gov.lanl.micot.infrastructure.config.AssetModification;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.util.io.json.JSON;
import gov.lanl.micot.util.io.json.JSONArrayBuilder;
import gov.lanl.micot.util.io.json.JSONObjectBuilder;
import gov.lanl.micot.util.io.json.JSONWriter;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Collection;

/**
 * Class for writing out the optimization configuration to a file
 * @author Russell Bent
 */
public class JsonProjectConfigurationWriter {

  /**
   * Constructor
   */
  public JsonProjectConfigurationWriter() {    
  }
  
  /**
   * Function for writing a multi scenario configuration
   * @throws FileNotFoundException 
   */
  public void writeMultiScenarioConfiguration(ProjectConfiguration config, String filename) throws FileNotFoundException {
    JSONObjectBuilder builder = JSON.getDefaultJSON().createObjectBuilder();

    // construct the version tag
    builder = builder.add(JsonConfigurationUtilities.VERSION_TAG, 1.0);
    
    // write the application data
    ApplicationConfiguration application = config.getApplicationConfiguration();
    JSONObjectBuilder applicationBuilder = JSON.getDefaultJSON().createObjectBuilder();
    applicationBuilder = applicationBuilder.add(JsonConfigurationUtilities.APPLICATION_FACTORY_TAG, application.getApplicationFactoryClass());
    for (String key : application.getApplicationFlags().keySet()) {
      applicationBuilder = applicationBuilder.add(key, application.getApplicationFlags().get(key));
    }
    builder = builder.add(JsonConfigurationUtilities.APPLICATION_TAG, applicationBuilder);
        
    // write the model data
    JSONArrayBuilder modelsBuilder = JSON.getDefaultJSON().createArrayBuilder();
    for (ModelConfiguration model : config.getModels()) {
      JSONObjectBuilder modelBuilder = JSON.getDefaultJSON().createObjectBuilder();
      modelBuilder = modelBuilder.add(JsonConfigurationUtilities.MODEL_FILE_TAG, model.getModelFile());
      modelBuilder = modelBuilder.add(JsonConfigurationUtilities.MODEL_FILE_FACTORY_TAG, model.getModelFileFactoryClass());            
      modelBuilder = writeAssetModifications(modelBuilder,model.getComponentModifications());      
      modelsBuilder.add(modelBuilder);
    }
    builder = builder.add(JsonConfigurationUtilities.MODEL_TAG, modelsBuilder);
    
    // write the algorithm data
    JSONArrayBuilder algorithmsBuilder = JSON.getDefaultJSON().createArrayBuilder();
    for (AlgorithmConfiguration algorithm : config.getAlgorithmConfigurations()) {
      JSONObjectBuilder algorithmBuilder = JSON.getDefaultJSON().createObjectBuilder();
      algorithmBuilder = algorithmBuilder.add(JsonConfigurationUtilities.ALGORITHM_FACTORY_TAG, algorithm.getAlgorithmFactoryClass());
      for (String key : algorithm.getAlgorithmFlags().keySet()) {
        algorithmBuilder = algorithmBuilder.add(key, algorithm.getAlgorithmFlags().get(key));
      }
      algorithmsBuilder.add(algorithmBuilder);
    }
    builder = builder.add(JsonConfigurationUtilities.ALGORITHM_TAG, algorithmsBuilder);

    // write the simulator data
    JSONArrayBuilder simulatorsBuilder = JSON.getDefaultJSON().createArrayBuilder();
    for (SimulatorConfiguration simulator : config.getSimulatorConfigurations()) {
      JSONObjectBuilder simulatorBuilder = JSON.getDefaultJSON().createObjectBuilder();
      simulatorBuilder = simulatorBuilder.add(JsonConfigurationUtilities.SIMULATOR_FACTORY_TAG, simulator.getSimulatorFactoryClass());
      for (String key : simulator.getSimulatorFlags().keySet()) {
        simulatorBuilder = simulatorBuilder.add(key, simulator.getSimulatorFlags().get(key));
      }
      simulatorsBuilder.add(simulatorBuilder);
    }
    builder = builder.add(JsonConfigurationUtilities.SIMULATOR_TAG, simulatorsBuilder);
    
    // write the master file
    try {
      PrintStream out = new PrintStream(filename);
      JSONWriter writer = JSON.getDefaultJSON().createWriter(out);
      writer.write(builder.build()); 
      writer.close();
      out.close();
    }
    catch (FileNotFoundException e) {
      e.printStackTrace();
    }

 
    
    // now write the scenarios
    int i = 1;
    for (ScenarioConfiguration scenario : config.getScenarioConfigurations()) {            
      String scenarioFilename = filename.replace(".json", "-" + i + ".json");
      JSONObjectBuilder oBuilder = JSON.getDefaultJSON().createObjectBuilder();          
      JSONArrayBuilder scenariosBuilder = JSON.getDefaultJSON().createArrayBuilder();
      JSONObjectBuilder scenarioBuilder = JSON.getDefaultJSON().createObjectBuilder();
      Scenario s = scenario.getScenario();
      
      scenarioBuilder = scenarioBuilder.add(JsonConfigurationUtilities.SCENARIO_PROBABILITY_TAG, s.getWeight());
      scenarioBuilder = scenarioBuilder.add(JsonConfigurationUtilities.SCENARIO_INDEX_TAG, s.getIndex());
      scenarioBuilder = scenarioBuilder.add(JsonConfigurationUtilities.SCENARIO_BUILDER_TAG, scenario.getScenarioBuilderFactoryClass());
      for (String key : scenario.getScenarioBuilderFlags().keySet()) {
        scenarioBuilder = scenarioBuilder.add(key, scenario.getScenarioBuilderFlags().get(key));
      }
      scenarioBuilder = writeAssetModifications(scenarioBuilder, scenario.getComponentModifications());      
      
      scenariosBuilder.add(scenarioBuilder);
      oBuilder.add(JsonConfigurationUtilities.SCENARIOS_TAG, scenariosBuilder);
      
      try {
        PrintStream out = new PrintStream(scenarioFilename);
        JSONWriter writer = JSON.getDefaultJSON().createWriter(out);
        writer.write(oBuilder.build()); 
        writer.close();
        out.close();
      }
      catch (FileNotFoundException e) {
        e.printStackTrace();
      }

      ++i;
    }
    
  }
  
  /**
   * write the asset modifications
   * @param ps
   * @param config
   */
  private JSONObjectBuilder writeAssetModifications(JSONObjectBuilder builder, Collection<AssetModification> modifications) {
    JSONArrayBuilder modificationsBuilder = JSON.getDefaultJSON().createArrayBuilder();
    
    for (AssetModification modification : modifications) {
      JSONObjectBuilder modificationBuilder = JSON.getDefaultJSON().createObjectBuilder();
      modificationBuilder.add(JsonConfigurationUtilities.MODIFICATION_COMPONENT_TAG, modification.getComponentClass());

      // print the id information
      JSONArrayBuilder idsBuilder = JSON.getDefaultJSON().createArrayBuilder();
      for (String key : modification.getKeys().keySet()) {
        JSONObjectBuilder idBuilder = JSON.getDefaultJSON().createObjectBuilder();
        idBuilder = idBuilder.add(JsonConfigurationUtilities.MODIFICATION_IDENTIFIER_KEY_TAG, key);
        idBuilder = idBuilder.add(JsonConfigurationUtilities.MODIFICATION_IDENTIFIER_VALUE_TAG, modification.getKeys().get(key));
        idBuilder = idBuilder.add(JsonConfigurationUtilities.MODIFICATION_IDENTIFIER_TYPE_TAG, getType(modification.getKeys().get(key).getClass()));        
        idsBuilder = idsBuilder.add(idBuilder);
      }
      modificationBuilder = modificationBuilder.add(JsonConfigurationUtilities.ID_TAG, idsBuilder);
      
      // print the attributes to modify
      JSONArrayBuilder modsBuilder = JSON.getDefaultJSON().createArrayBuilder();
      for (String key : modification.getAttributes().keySet()) {
        JSONObjectBuilder modBuilder = JSON.getDefaultJSON().createObjectBuilder();
        modBuilder = modBuilder.add(JsonConfigurationUtilities.MODIFICATION_ATTRIBUTE_KEY_TAG, key);
        modBuilder = modBuilder.add(JsonConfigurationUtilities.MODIFICATION_ATTRIBUTE_VALUE_TAG, modification.getAttributes().get(key));
        modBuilder = modBuilder.add(JsonConfigurationUtilities.MODIFICATION_ATTRIBUTE_TYPE_TAG, getType(modification.getAttributes().get(key).getClass()));
        modsBuilder = modsBuilder.add(modBuilder);
      }
      modificationBuilder = modificationBuilder.add(JsonConfigurationUtilities.MODIFICATION_ATTRIBUTE_TAG, modsBuilder);

      modificationsBuilder = modificationsBuilder.add(modificationBuilder);
    }
    
    return builder.add(JsonConfigurationUtilities.MODIFICATIONS_TAG, modificationsBuilder);
  }

  /**
   * get the object type
   * @param cls
   * @return
   */
  private String getType(Class<?> cls) {
    if (cls.equals(Integer.class) || cls.equals(Long.class)) {
      return JsonConfigurationUtilities.INT_TAG;
    }
    if (cls.equals(Number.class) || cls.equals(Double.class) || cls.equals(Float.class)) {
      return JsonConfigurationUtilities.DOUBLE_TAG;
    }
    if (cls.equals(Boolean.class)) {
      return JsonConfigurationUtilities.BOOLEAN_TAG;
    }    
    return JsonConfigurationUtilities.STRING_TAG;
  }
  
}
