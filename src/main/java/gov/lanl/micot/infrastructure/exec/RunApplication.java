package gov.lanl.micot.infrastructure.exec;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import gov.lanl.micot.infrastructure.application.Application;
import gov.lanl.micot.infrastructure.application.ApplicationOutput;
import gov.lanl.micot.infrastructure.project.JsonProjectConfigurationReader;
import gov.lanl.micot.infrastructure.project.ProjectConfiguration;
import gov.lanl.micot.infrastructure.project.ProjectConfigurationUtility;
import gov.lanl.micot.util.io.ParameterReader;

/**
 * General purpose main file for running applications from a config file
 * @author Russell Bent
 */
public class RunApplication {

  private static final String MASTER_CONFIG_FLAG = "-c";
  private static final String SCENARIO_CONFIG_FILE = "-s";
  
  private static final String DEFAULT_MASTER_CONFIG_FILE = "config" + File.separatorChar + "ep" + File.separatorChar + "scs" + File.separatorChar + "config-simple-test1.xml";
  //private static final String DEFAULT_SCENARIO_CONFIG_FILE = "config" + File.separatorChar + "ep" + File.separatorChar + "scs" + File.separatorChar + "config-emptyscenario.xml";
  
  /**
   * The main file
   * @param args
   * @throws IOException 
   * @throws ClassNotFoundException 
   * @throws IllegalAccessException 
   * @throws InstantiationException 
   */
  public static void main(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException {
    
    String masterFile = ParameterReader.getDefaultStringParameter(args, MASTER_CONFIG_FLAG, DEFAULT_MASTER_CONFIG_FILE);
    ArrayList<String> scenarioFiles = new ArrayList<String>();
    for (int i = 0; i < args.length-1; ++i) {
      if (args[i].equals(SCENARIO_CONFIG_FILE)) {
        scenarioFiles.add(args[i+1]);
      }
    }

    JsonProjectConfigurationReader jsonReader = new JsonProjectConfigurationReader();

    ProjectConfiguration configuration = jsonReader.readConfiguration(masterFile,scenarioFiles);

    Application application = ProjectConfigurationUtility.createApplication(configuration);
    ApplicationOutput output = application.execute();    
   }
  
}
