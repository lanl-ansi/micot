package gov.lanl.micot.application.rdt;

import gov.lanl.micot.infrastructure.application.Application;
import gov.lanl.micot.infrastructure.application.ApplicationOutput;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.project.JsonProjectConfigurationReader;
import gov.lanl.micot.infrastructure.project.ProjectConfiguration;
import gov.lanl.micot.application.lpnorm.LPNormApplicationFactory;
import gov.lanl.micot.application.lpnorm.io.LPNormJsonProjectConfigurationReader;
import gov.lanl.micot.application.rdt.RDDTApplication;
import gov.lanl.micot.application.rdt.RDDTApplicationFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import junit.framework.TestCase;

/**
 * This test considers the 123 OpenDSS network and tests to make sure everything is working properly
 * @author Russell Bent
 *
 */
public class Test123Network extends TestCase {

  private int scenarios = 1;
  
  private static String PATH = System.getProperty("user.dir");
  
  /**
   * Test the robust IEP files
   * 
   * @throws IOException
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws ClassNotFoundException
   */
  public void testNoDamage() throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
    if (System.getenv("TEST_RDT") != null) {
      return;
    }
    
    String masterFile = PATH + File.separatorChar + "application_data" + File.separatorChar + "rdt" + File.separatorChar + "config" + File.separatorChar + "123_Network" + File.separatorChar + "0.0 Damage Rate" + File.separatorChar + "config.json";
    String scenarioStart = PATH + File.separatorChar + "application_data" + File.separatorChar + "rdt" + File.separatorChar + "config" + File.separatorChar + "123_Network" + File.separatorChar + "0.0 Damage Rate" + File.separatorChar + "config-";
    ArrayList<String> scenarioFiles = new ArrayList<String>();
    for (int i = 1; i <= scenarios; ++i) {
      scenarioFiles.add(scenarioStart + i + ".json");
    }

    JsonProjectConfigurationReader reader = new JsonProjectConfigurationReader();
    ProjectConfiguration configuration = reader.readConfiguration(masterFile, scenarioFiles);
    RDDTApplicationFactory factory = new RDDTApplicationFactory();
    Application application = factory.createApplication(configuration);
    ApplicationOutput output = application.execute();
    double objValue = output.getDouble(RDDTApplication.OBJECTIVE_FLAG);
    
    ElectricPowerModel model = output.get(RDDTApplication.MODEL_FLAG, ElectricPowerModel.class);

    assertEquals(output.getBoolean(RDDTApplication.IS_FEASIBLE_FLAG).booleanValue(), true);
    assertEquals(0.0, -objValue, 1e-4);
    assertEquals(model.getGenerators().size(), 9);
  }
      
  
  public void testNoDamageLPNorm() throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
    if (System.getenv("TEST_RDT") != null) {
      return;
    }
    
//    String filename = PATH + File.separatorChar + "application_data" + File.separatorChar + "lpnorm" + File.separatorChar + "network123_0.json";
    String filename = PATH + File.separatorChar + "application_data" + File.separatorChar + "lpnorm" + File.separatorChar + "network123_35.json";

    LPNormJsonProjectConfigurationReader reader = new LPNormJsonProjectConfigurationReader();
    ProjectConfiguration configuration = reader.readConfiguration(filename);    
    LPNormApplicationFactory factory = new LPNormApplicationFactory();
    RDDTApplication application = factory.createApplication(configuration);
    ApplicationOutput output = application.execute();
    double objValue = output.getDouble(RDDTApplication.OBJECTIVE_FLAG);        
    assertEquals(output.getBoolean(RDDTApplication.IS_FEASIBLE_FLAG).booleanValue(), true);
    assertEquals(0.0, -objValue, 1e-4);
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
