package gov.lanl.micot.infrastructure.ep.exec;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import gov.lanl.micot.infrastructure.application.Application;
import gov.lanl.micot.infrastructure.application.ApplicationOutput;
import gov.lanl.micot.infrastructure.ep.application.ac.ACSimulationApplication;
import gov.lanl.micot.infrastructure.ep.io.ElectricPowerModelFileFactory;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.simulate.powerworld.JSONResultExporter;
import gov.lanl.micot.infrastructure.project.JsonProjectConfigurationReader;
import gov.lanl.micot.infrastructure.project.ProjectConfiguration;
import gov.lanl.micot.infrastructure.project.ProjectConfigurationUtility;
import gov.lanl.micot.infrastructure.simulate.Simulator.SimulatorSolveState;
import gov.lanl.micot.util.io.ParameterReader;
import gov.lanl.micot.util.time.Timer;

/**
 * This executible runs powerworld and prints some output of the results
 * @author Russell Bent
 *
 */
public class RunPowerworldConfigurationFile {

  private static final String CONFIG_FLAG = "-c";
  private static final String DEFAULT_MASTER_FILE = "config" + File.separatorChar + "junit" + File.separatorChar + "config-powerworld-outage-raw.json";

   
  public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
    Timer timer = new Timer();
    timer.startTimer();
    
    // associates raw files with Powerworld
    ElectricPowerModelFileFactory.registerExtension("raw",Class.forName("gov.lanl.micot.infrastructure.ep.io.powerworld.PowerworldModelFile"));
    
    String masterFile = ParameterReader.getDefaultStringParameter(args,  CONFIG_FLAG, DEFAULT_MASTER_FILE);
    InputStream masterStream = new FileInputStream(masterFile);
       
    JsonProjectConfigurationReader reader = new JsonProjectConfigurationReader();
    ProjectConfiguration configuration = reader.readConfiguration(masterStream);
    
    
    Application application = ProjectConfigurationUtility.createApplication(configuration);
    ApplicationOutput output = application.execute();

    ElectricPowerModel model = output.get(ACSimulationApplication.MODEL_FLAG, ElectricPowerModel.class);    
    double simulationTime = output.getDouble(ACSimulationApplication.CPU_TIME_FLAG);
    SimulatorSolveState state = output.get(ACSimulationApplication.SIMULATOR_STATE_FLAG, SimulatorSolveState.class);
    double totalTime = timer.getCPUMinutesDec();

    
    JSONResultExporter exporter = new JSONResultExporter();
    exporter.exportJSON(System.out, model, totalTime, simulationTime, state);
  }

}
