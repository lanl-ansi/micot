package gov.lanl.micot.util.infratructure.ep;

import gov.lanl.micot.infrastructure.application.Application;
import gov.lanl.micot.infrastructure.application.ApplicationOutput;
import gov.lanl.micot.infrastructure.ep.application.ac.ACSimulationApplication;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.project.JsonProjectConfigurationReader;
import gov.lanl.micot.infrastructure.project.ProjectConfiguration;
import gov.lanl.micot.infrastructure.project.ProjectConfigurationUtility;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

/**
 * Test cases for testing the dew implementations
 * @author Russell Bent
 */
public class DewTest2 extends TestCase {
    
  
  /**
   * 
   * @throws IOException
   * @throws InterruptedException
   * @throws ClassNotFoundException 
   * @throws IllegalAccessException 
   * @throws InstantiationException 
   */
  public void testDewIEEE34Simulator() throws IOException, InterruptedException, InstantiationException, IllegalAccessException, ClassNotFoundException {
    if (System.getenv("TEST_DEW") != null) {
      return;
    }
    
    String masterFile = "test_data" + File.separatorChar + "ep" + File.separatorChar + "config-dew-ieee34.json";    
    JsonProjectConfigurationReader reader = new JsonProjectConfigurationReader();
    ProjectConfiguration configuration = reader.readConfiguration(masterFile);
    Application application = ProjectConfigurationUtility.createApplication(configuration);
    ApplicationOutput output = application.execute();
    ElectricPowerModel model = output.get(ACSimulationApplication.MODEL_FLAG, ElectricPowerModel.class);
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
