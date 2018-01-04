package gov.lanl.micot.infrastructure.ep.exec;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;

import gov.lanl.micot.infrastructure.application.Application;
import gov.lanl.micot.infrastructure.application.ApplicationOutput;
import gov.lanl.micot.infrastructure.config.AssetModification;
import gov.lanl.micot.infrastructure.ep.application.ac.ACSimulationApplication;
import gov.lanl.micot.infrastructure.ep.io.ElectricPowerModelFileFactory;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.simulate.powerworld.JSONResultExporter;
import gov.lanl.micot.infrastructure.ep.simulate.powerworld.PowerworldSimulatorFlags;
import gov.lanl.micot.infrastructure.project.ApplicationConfiguration;
import gov.lanl.micot.infrastructure.project.JsonConfigurationUtilities;
import gov.lanl.micot.infrastructure.project.JsonProjectConfigurationReader;
import gov.lanl.micot.infrastructure.project.ModelConfiguration;
import gov.lanl.micot.infrastructure.project.ProjectConfiguration;
import gov.lanl.micot.infrastructure.project.ProjectConfigurationUtility;
import gov.lanl.micot.infrastructure.project.SimulatorConfiguration;
import gov.lanl.micot.infrastructure.simulate.Simulator.SimulatorSolveState;
import gov.lanl.micot.util.io.ParameterReader;
import gov.lanl.micot.util.io.json.JSON;
import gov.lanl.micot.util.io.json.JSONArray;
import gov.lanl.micot.util.io.json.JSONObject;
import gov.lanl.micot.util.io.json.JSONReader;
import gov.lanl.micot.util.time.Timer;

/**
 * This executible runs powerworld and prints some output of the results
 * @author Russell Bent
 *
 */
public class RunPowerworld {

  private static final String MODIFICATIONS_FLAG = "-m";
  private static final String POWER_MODEL_FLAG   = "-p";

  /**
   * Runs powerworld using a complete configuration file
   * @param args
   * @throws ClassNotFoundException
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws IOException
   */
  public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
    Timer timer = new Timer();
    timer.startTimer();
        
    // associates raw files with Powerworld
    ElectricPowerModelFileFactory.registerExtension("raw",Class.forName("gov.lanl.micot.infrastructure.ep.io.powerworld.PowerworldModelFile"));
    
    String modificationsFile = ParameterReader.getDefaultStringParameter(args,  MODIFICATIONS_FLAG, null);
    String powerModelFile = ParameterReader.getRequiredStringParameter(args,  POWER_MODEL_FLAG, "power model file");

    ProjectConfiguration configuration = buildConfiguration(powerModelFile, modificationsFile);
    
    Application application = null;
    try {
      application = ProjectConfigurationUtility.createApplication(configuration);
    }
    catch (IOException e) {
      String message = "Failure in reading " + powerModelFile;
      if (modificationsFile != null) {
        message += " or " + modificationsFile;
      }      
      System.err.println(message);
      return;
    }
    ApplicationOutput output = application.execute();

    ElectricPowerModel model = output.get(ACSimulationApplication.MODEL_FLAG, ElectricPowerModel.class);    
    double simulationTime = output.getDouble(ACSimulationApplication.CPU_TIME_FLAG);
    SimulatorSolveState state = output.get(ACSimulationApplication.SIMULATOR_STATE_FLAG, SimulatorSolveState.class);
    double totalTime = timer.getCPUMinutesDec();
    
    JSONResultExporter exporter = new JSONResultExporter();
    exporter.exportJSON(System.out, model, totalTime, simulationTime, state);
  }
  
  /**
   * Build the default project configuration
   * @param powerModelFile
   * @param modificationsFile
   * @return
   * @throws FileNotFoundException 
   */
  public static ProjectConfiguration buildConfiguration(String powerModelFile, String modificationsFile) throws FileNotFoundException {
    ProjectConfiguration configuration = new ProjectConfiguration();
    
    // simulator configuration
    SimulatorConfiguration simulatorConfiguration = new SimulatorConfiguration();
    simulatorConfiguration.setSimulatorFactoryClass(gov.lanl.micot.infrastructure.ep.simulate.powerworld.PowerworldSimulatorFactory.class.getCanonicalName());
    simulatorConfiguration.addSimulatorFlag(PowerworldSimulatorFlags.CHECK_PHASE_SHIFTERS_FLAG, false);
    simulatorConfiguration.addSimulatorFlag(PowerworldSimulatorFlags.CHECK_SHUNTS_FLAG, false);
    simulatorConfiguration.addSimulatorFlag(PowerworldSimulatorFlags.CHECK_SVC_FLAG, false);
    simulatorConfiguration.addSimulatorFlag(PowerworldSimulatorFlags.CHECK_TAPS_FLAG, false);
    simulatorConfiguration.addSimulatorFlag(PowerworldSimulatorFlags.CHECK_AREA_FLAG, false);
    simulatorConfiguration.addSimulatorFlag(PowerworldSimulatorFlags.CHECK_MVAR_LIMITS_FLAG, false);
    simulatorConfiguration.addSimulatorFlag(PowerworldSimulatorFlags.CHECK_MW_LIMITS_FLAG, false);
    
    configuration.addSimulatorConfiguration(simulatorConfiguration);
    
    
    // application configuration
    ApplicationConfiguration applicationConfiguration = new ApplicationConfiguration();
    applicationConfiguration.setApplicationFactoryClass(gov.lanl.micot.infrastructure.ep.application.ac.ACSimulationApplicationFactory.class.getCanonicalName());
    configuration.setApplicationConfiguration(applicationConfiguration);
        
    // model configuration
    ModelConfiguration modelConfiguration = new ModelConfiguration();
    modelConfiguration.setModelFile(powerModelFile);
    modelConfiguration.setModelFileFactoryClass(gov.lanl.micot.infrastructure.ep.io.ElectricPowerModelFileFactory.class.getCanonicalName());    
    JsonProjectConfigurationReader reader = new JsonProjectConfigurationReader();
    
    if (modificationsFile != null) {
      JSONReader jreader = JSON.getDefaultJSON().createReader(new FileInputStream(modificationsFile));
      JSONObject obj = jreader.readObject();      
      JSONArray modifications = obj.getArray(JsonConfigurationUtilities.MODIFICATIONS_TAG);
      Collection<AssetModification> mods = reader.readModifications(modifications); 
      modelConfiguration.setComponentModifications(mods);
    }
    configuration.addModelConfiguration(modelConfiguration);
    
    return configuration;
  }

  
  
}
