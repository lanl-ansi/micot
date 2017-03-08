package gov.lanl.micot.application.lpnorm.exec;

import gov.lanl.micot.infrastructure.application.Application;
import gov.lanl.micot.infrastructure.application.ApplicationOutput;
import gov.lanl.micot.infrastructure.project.ProjectConfiguration;
import gov.lanl.micot.application.lpnorm.LPNormApplicationFactory;
import gov.lanl.micot.application.lpnorm.io.LPNormJsonProjectConfigurationReader;
import gov.lanl.micot.application.rdt.JsonResultExporter;
import gov.lanl.micot.util.io.ParameterReader;

import java.io.File;
import java.io.IOException;

/**
 * Code for running the RDT for an LPNorm application
 * 
 * @author Russell Bent
 */
public class RunLPNorm  {
   
  private static final String CONFIG_FLAG = "-c";
  private static final String EXPORT_FLAG = "-e";
    
  private static final String DEFAULT_CONFIG_FILE = "lpnorm" + File.separatorChar + "example.json";
  private static final String DEFAULT_EXPORT_FILE = "out.json";
  
  /**
   * @param args
   * @throws ClassNotFoundException
   * @throws IllegalAccessException
   * @throws InstantiationException
   * @throws IOException
   */
  public static void main(String[] args) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
    String filename = ParameterReader.getRequiredStringParameter(args, CONFIG_FLAG, "Missing the input file");
    String exportFile = ParameterReader.getDefaultStringParameter(args, EXPORT_FLAG, DEFAULT_EXPORT_FILE);    

    long start = System.currentTimeMillis();

    System.out.println("Using configuration file: " + filename);
    LPNormJsonProjectConfigurationReader reader = new LPNormJsonProjectConfigurationReader();
    ProjectConfiguration configuration = reader.readConfiguration(filename);
    
    LPNormApplicationFactory factory = new LPNormApplicationFactory();
    Application application = factory.createApplication(configuration);
    ApplicationOutput output = application.execute();
    
    // export the results

    JsonResultExporter exporter = new JsonResultExporter();
    exporter.exportResults(output, configuration, exportFile);
    
    long end = System.currentTimeMillis();
    System.out.println("Total execution time: " + ((end - start) / 1000.0) + " seconds");
  }
}
