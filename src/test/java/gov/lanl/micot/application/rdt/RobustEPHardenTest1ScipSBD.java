package gov.lanl.micot.application.rdt;

import gov.lanl.micot.infrastructure.application.Application;
import gov.lanl.micot.infrastructure.application.ApplicationOutput;
import gov.lanl.micot.infrastructure.project.JsonProjectConfigurationReader;
import gov.lanl.micot.infrastructure.project.ProjectConfiguration;
import gov.lanl.micot.application.rdt.RDTApplication;
import gov.lanl.micot.application.rdt.RDTApplicationFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import junit.framework.TestCase;

public class RobustEPHardenTest1ScipSBD extends TestCase {

  /**
   * Test scenario based decomposition  
   * @throws IOException
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws ClassNotFoundException
   */
  public void testHardenMIPCycleContiniousDetermineScip() throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
    if (System.getenv("TEST_RDT") != null) {
      return;
    }
    
   /* String masterFile = "config_files" + File.separatorChar + "Resilience" + File.separatorChar + "34Bus" + File.separatorChar + "Ice Harden" + File.separatorChar + "Rural" + File.separatorChar + "0.75% Per Mile Damage" + File.separatorChar + "config-mip-cycle-continous-deterimine-scip-sbd.json";
    String scenarioStart = "config_files" + File.separatorChar + "Resilience" + File.separatorChar + "34Bus" + File.separatorChar + "Ice Harden" + File.separatorChar + "Rural" + File.separatorChar + "0.75% Per Mile Damage" + File.separatorChar + "config-";
    ArrayList<String> scenarioFiles = new ArrayList<String>();
    for (int i = 1; i <= 20; ++i) {
      scenarioFiles.add(scenarioStart + i + ".json");
    }

    JsonProjectConfigurationReader reader = new JsonProjectConfigurationReader();
    ProjectConfiguration configuration = reader.readConfiguration(masterFile, scenarioFiles);
    RDTApplicationFactory factory = new RDTApplicationFactory();
    Application application = factory.createApplication(configuration);
    ApplicationOutput output = application.execute();
    double objValue = output.getDouble(RDTApplication.OBJECTIVE_FLAG);        
    assertEquals(3410.051385, -objValue, 1e-2);*/
  }

  
  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
  }



  


}
