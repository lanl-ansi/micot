package gov.lanl.micot.util.infratructure.ep;

import gov.lanl.micot.infrastructure.application.Application;
import gov.lanl.micot.infrastructure.application.ApplicationOutput;
import gov.lanl.micot.infrastructure.ep.application.ac.ACSimulationApplication;
import gov.lanl.micot.infrastructure.ep.io.powerworld.PowerworldModelFile;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.project.JsonProjectConfigurationReader;
import gov.lanl.micot.infrastructure.project.ProjectConfiguration;
import gov.lanl.micot.infrastructure.project.ProjectConfigurationUtility;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

/**
 * Test cases for power world models
 * @author Russell Bent
 */
public class PowerworldTest extends TestCase {
    
  /**
   * Tests the output of demo file
   * @throws IOException 
   * @throws ClassNotFoundException 
   * @throws IllegalAccessException 
   * @throws InstantiationException 
   */
  public void testOutage() throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
    if (System.getenv("TEST_POWERWORLD") != null) {
      return;
    }
    
    String masterFile = System.getProperty("user.dir") + File.separatorChar + "test_data" + File.separatorChar + "ep" + File.separatorChar + "config-powerworld-outage.json";    
    JsonProjectConfigurationReader reader = new JsonProjectConfigurationReader();
    ProjectConfiguration configuration = reader.readConfiguration(masterFile);
    Application application = ProjectConfigurationUtility.createApplication(configuration);
    ApplicationOutput output = application.execute();

    ElectricPowerModel model = output.get(ACSimulationApplication.MODEL_FLAG, ElectricPowerModel.class);    
    assertEquals(model.getBuses().size(), 7);
    assertEquals(model.getGenerators().size(), 5);
    assertEquals(model.getLoads().size(), 6);

    for (ElectricPowerFlowConnection connection : model.getFlowConnections()) {
    	if (connection.toString().equals("(6,7, 2)")) {
    		assertEquals(connection.getMWFlow(), 0.0);
    	}
    	else {
    		assertTrue(connection.getMWFlow().doubleValue() > 0.0);
    	}
    	
    	System.out.println(connection.toString() + " " + connection.getMWFlow() + " " + connection.getDesiredStatus());
    }
    
  }

  /**
   * Test to see to see if raw inputs can be read by the dcom interfaces
   * @throws IOException
   */
  public void testRawInput() throws IOException {
    if (System.getenv("TEST_POWERWORLD") != null) {
      return;
    }
    
    PowerworldModelFile modelFile = new PowerworldModelFile();
    ElectricPowerModel model = modelFile.readModel("test_data" + File.separatorChar + "ep" + File.separatorChar + "powerworld" + File.separatorChar + "B7OPF.RAW");
    assertEquals(model.getBuses().size(), 7);
    assertEquals(model.getGenerators().size(), 5);
    assertEquals(model.getLoads().size(), 6);
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
