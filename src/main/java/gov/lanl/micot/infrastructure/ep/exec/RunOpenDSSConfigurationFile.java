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
import gov.lanl.micot.infrastructure.project.JsonProjectConfigurationReader;
import gov.lanl.micot.infrastructure.project.ProjectConfiguration;
import gov.lanl.micot.infrastructure.project.ProjectConfigurationUtility;
import gov.lanl.micot.util.io.ParameterReader;

/**
 * This executible runs powerworld and prints some output of the results
 * @author Russell Bent
 *
 */
public class RunOpenDSSConfigurationFile {

  private static final String CONFIG_FLAG = "-c";
  private static final String DEFAULT_MASTER_FILE = "config" + File.separatorChar + "ep" + File.separatorChar + "ac" + File.separatorChar + "junit" + File.separatorChar + "config-opendss.json";

  public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {

    String masterFile = ParameterReader.getDefaultStringParameter(args,  CONFIG_FLAG, DEFAULT_MASTER_FILE);
    InputStream masterStream = new FileInputStream(masterFile);
       
    JsonProjectConfigurationReader reader = new JsonProjectConfigurationReader();
    ProjectConfiguration configuration = reader.readConfiguration(masterStream);
        
    Application application = ProjectConfigurationUtility.createApplication(configuration);
    ApplicationOutput output = application.execute();

    ElectricPowerModel model = output.get(ACSimulationApplication.MODEL_FLAG, ElectricPowerModel.class);    

    JSONResultExporter exporter = new JSONResultExporter();
    exporter.exportJSON(System.out, model);
  }

}
