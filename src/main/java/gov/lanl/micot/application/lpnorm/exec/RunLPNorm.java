package gov.lanl.micot.application.lpnorm.exec;

import gov.lanl.micot.infrastructure.application.Application;
import gov.lanl.micot.infrastructure.application.ApplicationOutput;
import gov.lanl.micot.infrastructure.project.ProjectConfiguration;
import gov.lanl.micot.application.lpnorm.LPNormApplicationFactory;
import gov.lanl.micot.application.lpnorm.io.LPNormJsonProjectConfigurationReader;
import gov.lanl.micot.application.rdt.JsonResultExporter;
import gov.lanl.micot.util.io.ParameterReader;

import java.io.IOException;

/**
 * Code for running the RDT for an LPNorm application
 * 
 * @author Russell Bent
 */
public class RunLPNorm  {
   
  /**
   * @param args
   * @throws ClassNotFoundException
   * @throws IllegalAccessException
   * @throws InstantiationException
   * @throws IOException
   */
  public static void main(String[] args) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
    LPNormCommandLineParser parser = new LPNormCommandLineParser(args);
    boolean valid = parser.checkOptions();
    if (!valid) {
      System.exit(-1);
    }
        
    String filename = parser.getRDTInputFile();
    String exportFile = parser.getOutputFile();
    
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
