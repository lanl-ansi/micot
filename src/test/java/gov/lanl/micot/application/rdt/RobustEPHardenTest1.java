package gov.lanl.micot.application.rdt;

import gov.lanl.micot.infrastructure.application.Application;
import gov.lanl.micot.infrastructure.application.ApplicationOutput;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.Generator;
import gov.lanl.micot.infrastructure.ep.model.Line;
import gov.lanl.micot.infrastructure.ep.model.Load;
import gov.lanl.micot.infrastructure.ep.model.Transformer;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.model.ScenarioAttribute;
import gov.lanl.micot.infrastructure.project.JsonProjectConfigurationReader;
import gov.lanl.micot.infrastructure.project.ProjectConfiguration;
import gov.lanl.micot.application.lpnorm.LPNormApplicationFactory;
import gov.lanl.micot.application.lpnorm.io.LPNormJsonProjectConfigurationReader;
import gov.lanl.micot.application.rdt.RDTApplication;
import gov.lanl.micot.application.rdt.RDTApplicationFactory;
import gov.lanl.micot.application.rdt.algorithm.AlgorithmConstants;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import junit.framework.TestCase;

public class RobustEPHardenTest1 extends TestCase {

  private int scenarios = 20;
  
  private static String PATH = System.getProperty("user.dir");
  
  /**
   * Test the robust IEP files
   * 
   * @throws IOException
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws ClassNotFoundException
   */
  public void testHardenMIPCycleContiniousDetermine() throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
    if (System.getenv("TEST_RDT") != null) {
      return;
    }
    
    String masterFile = "application_data" + File.separatorChar + "rdt" + File.separatorChar + "config" + File.separatorChar + "Ice Harden" + File.separatorChar + "Rural" + File.separatorChar + "0.75% Per Mile Damage" + File.separatorChar + "config-mip-cycle-continous-deterimine.json";
    String scenarioStart = "application_data" + File.separatorChar + "rdt" + File.separatorChar + "config" + File.separatorChar + "Ice Harden" + File.separatorChar + "Rural" + File.separatorChar + "0.75% Per Mile Damage" + File.separatorChar + "config-";
    ArrayList<String> scenarioFiles = new ArrayList<String>();
    for (int i = 1; i <= scenarios; ++i) {
      scenarioFiles.add(scenarioStart + i + ".json");
    }

    JsonProjectConfigurationReader reader = new JsonProjectConfigurationReader();
    ProjectConfiguration configuration = reader.readConfiguration(masterFile, scenarioFiles);
    RDTApplicationFactory factory = new RDTApplicationFactory();
    Application application = factory.createApplication(configuration);
    ApplicationOutput output = application.execute();
    double objValue = output.getDouble(RDTApplication.OBJECTIVE_FLAG);            
    assertEquals(output.getBoolean(RDTApplication.IS_FEASIBLE_FLAG).booleanValue(), true);
    assertEquals(3410.051385, -objValue, 1e-4);
  }
      
  
  public void testLPNORMVersion() throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
    if (System.getenv("TEST_RDT") != null) {
      return;
    }
    
    String filename = PATH + File.separatorChar + "application_data" + File.separatorChar + "lpnorm" + File.separatorChar + "Ice_Harden_Rural_75_20scenarios.json";

    LPNormJsonProjectConfigurationReader reader = new LPNormJsonProjectConfigurationReader();
    ProjectConfiguration configuration = reader.readConfiguration(filename);    
    LPNormApplicationFactory factory = new LPNormApplicationFactory();
    RDTApplication application = factory.createApplication(configuration);
    ApplicationOutput output = application.execute();
    double objValue = output.getDouble(RDTApplication.OBJECTIVE_FLAG);        
    assertEquals(output.getBoolean(RDTApplication.IS_FEASIBLE_FLAG).booleanValue(), true);
    assertEquals(3410.051385, -objValue, 1e-4);
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
