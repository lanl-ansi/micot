package gov.lanl.micot.application.lpnorm;

import java.io.File;
import java.io.IOException;

import gov.lanl.micot.infrastructure.application.Application;
import gov.lanl.micot.infrastructure.application.ApplicationOutput;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.Generator;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.model.ScenarioAttribute;
import gov.lanl.micot.infrastructure.project.ProjectConfiguration;
import gov.lanl.micot.application.lpnorm.LPNormApplicationFactory;
import gov.lanl.micot.application.lpnorm.io.LPNormJsonProjectConfigurationReader;
import gov.lanl.micot.application.rdt.algorithm.AlgorithmConstants;
import gov.lanl.micot.application.rdt.RDTApplication;
import junit.framework.TestCase;

/**
 * Tests of LPNORM functionaility using the column generation technique
 * @author Russell Bent
 *
 */
public class LPNormBPTest  extends TestCase {


  /**
   * Test to make sure network flow works
   * @throws IOException
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws ClassNotFoundException
   */
  public void testNetworkFlow() throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
    if (System.getenv("TEST_LPNORM") != null) {
      return;
    }
    
    String filename = "application_data" + File.separatorChar + "lpnorm" + File.separatorChar + "junit" + File.separatorChar + "network_flow_bp.json";

    LPNormJsonProjectConfigurationReader reader = new LPNormJsonProjectConfigurationReader();
    ProjectConfiguration configuration = reader.readConfiguration(filename);
    
    LPNormApplicationFactory factory = new LPNormApplicationFactory();
    Application application = factory.createApplication(configuration);
    ApplicationOutput output = application.execute();

    ElectricPowerModel model = output.get(RDTApplication.MODEL_FLAG, ElectricPowerModel.class);
    double cost = output.get(RDTApplication.OBJECTIVE_FLAG, Number.class).doubleValue();
//    assertEquals(cost, -5000.0);
    
    Scenario scenario = configuration.getFirstScenario();
    
    for (ElectricPowerFlowConnection connection : model.getFlowConnections()) {
      boolean isConstructed = connection.getAttribute(AlgorithmConstants.IS_CONSTRUCTED_KEY) == null ? false : connection.getAttribute(AlgorithmConstants.IS_CONSTRUCTED_KEY, Boolean.class);
      boolean isSwitchConstructed = connection.getAttribute(AlgorithmConstants.IS_SWITCH_CONSTRUCTED_KEY) == null ? false : connection.getAttribute(AlgorithmConstants.IS_SWITCH_CONSTRUCTED_KEY, Boolean.class);
      ScenarioAttribute isScenarioOpen = connection.getAttribute(AlgorithmConstants.IS_SWITCH_OPEN_KEY, ScenarioAttribute.class);
      boolean isOpen = isScenarioOpen.getValue(scenario).intValue() == 1;
      boolean isHardened = connection.getAttribute(AlgorithmConstants.IS_HARDENED_KEY) == null ? false : connection.getAttribute(AlgorithmConstants.IS_HARDENED_KEY, Boolean.class);
      
  //    assertEquals(isConstructed, false);
   //   assertEquals(isSwitchConstructed, false);      
    //  assertEquals(isOpen, false);
      
      if (connection.toString().equals("l1")) {
     //   assertEquals(isHardened, true);
      }
      else {
      //  assertEquals(isHardened, false);        
      }      
    }
    
    for (Generator generator : model.getGenerators()) {
      boolean isConstructed = generator.getAttribute(AlgorithmConstants.IS_CONSTRUCTED_KEY) == null ? false : generator.getAttribute(AlgorithmConstants.IS_CONSTRUCTED_KEY, Boolean.class);
 //     assertEquals(isConstructed, false);              
    }

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
