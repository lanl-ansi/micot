package gov.lanl.micot.application.rdt.exec;

import gov.lanl.micot.infrastructure.application.Application;
import gov.lanl.micot.infrastructure.application.ApplicationOutput;
import gov.lanl.micot.infrastructure.project.JsonProjectConfigurationReader;
import gov.lanl.micot.infrastructure.project.ProjectConfiguration;
import gov.lanl.micot.application.rdt.JsonResultExporter;
import gov.lanl.micot.application.rdt.RDDTApplicationFactory;
import gov.lanl.micot.util.io.ParameterReader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Code for running RDDT Code for multiple scenarios simultaneously
 * 
 * @author Russell Bent
 */
public class RunRDT  {
   
  private static final String MASTER_CONFIG_FLAG = "-c";
  private static final String SCENARIO_PREFIX_FLAG = "-s";
  private static final String EXPORT_FLAG = "-e";
  private static final String FIRST_SCENARIO_FLAG = "-s1";
  private static final String SECOND_SCENARIO_FLAG = "-s2";
    
  private static final String DEFAULT_MASTER_CONFIG_FILE = "config_files" + File.separatorChar + "Resilience" + File.separatorChar + "34Bus" + File.separatorChar + "Ice Harden" + File.separatorChar + "Rural" + File.separatorChar + "0.1% Per Mile Damage" + File.separatorChar + "config-scip.json";
	private static final String DEFAULT_SCENARIO_PREFIX = "config_files" + File.separatorChar + "Resilience" + File.separatorChar + "34Bus" + File.separatorChar + "Ice Harden" + File.separatorChar + "Rural" + File.separatorChar + "0.1% Per Mile Damage" + File.separatorChar + "config-";    
  private static final String DEFAULT_EXPORT_FILE = "out.json";
  private static final int DEFAULT_FIRST_SCENARIO = 1;
  private static final int DEFAULT_SECOND_SCENARIO = 2;
  
  /**
   * @param args
   * @throws ClassNotFoundException
   * @throws IllegalAccessException
   * @throws InstantiationException
   * @throws IOException
   */
  public static void main(String[] args) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
    String masterFile = ParameterReader.getDefaultStringParameter(args, MASTER_CONFIG_FLAG, DEFAULT_MASTER_CONFIG_FILE);
    String scenarioStart = ParameterReader.getDefaultStringParameter(args, SCENARIO_PREFIX_FLAG, DEFAULT_SCENARIO_PREFIX);
    String exportFile = ParameterReader.getDefaultStringParameter(args, EXPORT_FLAG, DEFAULT_EXPORT_FILE);    
    int scenarios = ParameterReader.getDefaultIntegerParameter(args, SECOND_SCENARIO_FLAG, DEFAULT_SECOND_SCENARIO);
    int scenario = ParameterReader.getDefaultIntegerParameter(args, FIRST_SCENARIO_FLAG, DEFAULT_FIRST_SCENARIO);

    ArrayList<String> scenarioConfigurationFiles = new ArrayList<String>();
    for (scenario = 1; scenario <= scenarios; ++scenario) {
      String scenarioFile = scenarioStart + scenario + ".json";
      scenarioConfigurationFiles.add(scenarioFile);
    }

    long start = System.currentTimeMillis();

    System.out.println("Using master configuration file: " + masterFile);
    JsonProjectConfigurationReader reader = new JsonProjectConfigurationReader();
    ProjectConfiguration configuration = reader.readConfiguration(masterFile, scenarioConfigurationFiles);
    
    RDDTApplicationFactory factory = new RDDTApplicationFactory();
    Application application = factory.createApplication(configuration);
    ApplicationOutput output = application.execute();
    
    // export the results

    JsonResultExporter exporter = new JsonResultExporter();
    exporter.exportResults(output, configuration, exportFile);
    
    long end = System.currentTimeMillis();
    System.out.println("Total execution time: " + ((end - start) / 1000.0) + " seconds");
  }
}
