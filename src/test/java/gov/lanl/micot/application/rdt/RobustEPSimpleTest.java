package gov.lanl.micot.application.rdt;

import gov.lanl.micot.infrastructure.application.Application;
import gov.lanl.micot.infrastructure.application.ApplicationOutput;
import gov.lanl.micot.infrastructure.project.JsonProjectConfigurationReader;
import gov.lanl.micot.infrastructure.project.ProjectConfiguration;
import gov.lanl.micot.application.rdt.RDDTApplication;
import gov.lanl.micot.application.rdt.RDDTApplicationFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import junit.framework.TestCase;

public class RobustEPSimpleTest extends TestCase {

  /**
   * test the standard robust IEP formulation
   * 
   * @throws IOException
   * @throws ClassNotFoundException
   * @throws IllegalAccessException
   * @throws InstantiationException
   */
  public void testSimpleMIPCycleContDetermine() throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
    if (System.getenv("TEST_RDT") != null) {
      return;
    }
    
   /* String masterFile = "config_files" + File.separatorChar + "Resilience" + File.separatorChar + "Scenarios" + File.separatorChar + "test" + File.separatorChar + "default" + File.separatorChar + "config-simple-mip-cycle-continous-deterimine.json";
    String scenarioFile1 = "config_files" + File.separatorChar + "Resilience" + File.separatorChar + "Scenarios" + File.separatorChar + "test" + File.separatorChar + "default" + File.separatorChar + "config-simple-1.json";

    ArrayList<String> scenarioFiles = new ArrayList<String>();
    scenarioFiles.add(scenarioFile1);

    JsonProjectConfigurationReader reader = new JsonProjectConfigurationReader();
    ProjectConfiguration configuration = reader.readConfiguration(masterFile, scenarioFiles);
    
    RDDTApplicationFactory factory = new RDDTApplicationFactory();
    Application application = factory.createApplication(configuration);
    ApplicationOutput output = application.execute();
    double objValue = output.getDouble(RDDTApplication.OBJECTIVE_FLAG);        
    assertEquals(2000.0, -objValue);*/
  }

  /**
   * test the standard robust IEP formulation
   * 
   * @throws IOException
   * @throws ClassNotFoundException
   * @throws IllegalAccessException
   * @throws InstantiationException
   */
  public void testSimpleMIPCycleContDetermineSBD() throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
/*    String masterFile = "config_files" + File.separatorChar + "Resilience" + File.separatorChar + "Scenarios" + File.separatorChar + "test" + File.separatorChar + "default" + File.separatorChar + "config-simple-mip-cycle-continous-deterimine-sbd.json";
    String scenarioFile1 = "config_files" + File.separatorChar + "Resilience" + File.separatorChar + "Scenarios" + File.separatorChar + "test" + File.separatorChar + "default" + File.separatorChar + "config-simple-1.json";

    ArrayList<String> scenarioFiles = new ArrayList<String>();
    scenarioFiles.add(scenarioFile1);

    JsonProjectConfigurationReader reader = new JsonProjectConfigurationReader();
    ProjectConfiguration configuration = reader.readConfiguration(masterFile, scenarioFiles);
    
    RDDTApplicationFactory factory = new RDDTApplicationFactory();
    Application application = factory.createApplication(configuration);
    ApplicationOutput output = application.execute();
    double objValue = output.getDouble(RDDTApplication.OBJECTIVE_FLAG);        
    assertEquals(2000.0, -objValue);*/
  }
  
  
  /**
   * test the standard robust IEP formulation
   * 
   * @throws IOException
   * @throws ClassNotFoundException
   * @throws IllegalAccessException
   * @throws InstantiationException
   */
  public void testSimpleMIPTreeContDetermine() throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
/*    String masterFile = "config_files" + File.separatorChar + "Resilience" + File.separatorChar + "Scenarios" + File.separatorChar + "test" + File.separatorChar + "default" + File.separatorChar + "config-simple-mip-tree-continious-deterimine.json";
    String scenarioFile1 = "config_files" + File.separatorChar + "Resilience" + File.separatorChar + "Scenarios" + File.separatorChar + "test" + File.separatorChar + "default" + File.separatorChar + "config-simple-1.json";

    ArrayList<String> scenarioFiles = new ArrayList<String>();
    scenarioFiles.add(scenarioFile1);

    JsonProjectConfigurationReader reader = new JsonProjectConfigurationReader();
    ProjectConfiguration configuration = reader.readConfiguration(masterFile, scenarioFiles);
    RDDTApplicationFactory factory = new RDDTApplicationFactory();
    Application application = factory.createApplication(configuration);
    ApplicationOutput output = application.execute();
    double objValue = output.getDouble(RDDTApplication.OBJECTIVE_FLAG);
    
    assertEquals(2000.0, -objValue);*/
  }


  /**
   * test the standard robust IEP formulation
   * 
   * @throws IOException
   * @throws ClassNotFoundException
   * @throws IllegalAccessException
   * @throws InstantiationException
   */
  public void testSimpleMIPTreeContDetermineSBD() throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
/*    String masterFile = "config_files" + File.separatorChar + "Resilience" + File.separatorChar + "Scenarios" + File.separatorChar + "test" + File.separatorChar + "default" + File.separatorChar + "config-simple-mip-tree-continious-deterimine-sbd.json";
    String scenarioFile1 = "config_files" + File.separatorChar + "Resilience" + File.separatorChar + "Scenarios" + File.separatorChar + "test" + File.separatorChar + "default" + File.separatorChar + "config-simple-1.json";

    ArrayList<String> scenarioFiles = new ArrayList<String>();
    scenarioFiles.add(scenarioFile1);

    JsonProjectConfigurationReader reader = new JsonProjectConfigurationReader();
    ProjectConfiguration configuration = reader.readConfiguration(masterFile, scenarioFiles);
    RDDTApplicationFactory factory = new RDDTApplicationFactory();
    Application application = factory.createApplication(configuration);
    ApplicationOutput output = application.execute();
    double objValue = output.getDouble(RDDTApplication.OBJECTIVE_FLAG);
    
    assertEquals(2000.0, -objValue);*/
  }

  
}
