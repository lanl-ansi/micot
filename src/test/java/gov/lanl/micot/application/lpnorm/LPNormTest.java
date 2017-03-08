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
import gov.lanl.micot.application.rdt.RDDTApplication;
import junit.framework.TestCase;

/**
 * Tests of LPNORM functionaility
 * @author Russell Bent
 *
 */
public class LPNormTest  extends TestCase {

  /**
   * Test to make sure that hardening lines works
   * @throws IOException
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws ClassNotFoundException
   */
  public void testHardenVariable() throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
    if (System.getenv("TEST_LPNORM") != null) {
      return;
    }
    
    String filename = "application_data" + File.separatorChar + "lpnorm" + File.separatorChar + "junit" + File.separatorChar + "harden_only.json";

    LPNormJsonProjectConfigurationReader reader = new LPNormJsonProjectConfigurationReader();
    ProjectConfiguration configuration = reader.readConfiguration(filename);
    
    LPNormApplicationFactory factory = new LPNormApplicationFactory();
    Application application = factory.createApplication(configuration);
    ApplicationOutput output = application.execute();

    ElectricPowerModel model = output.get(RDDTApplication.MODEL_FLAG, ElectricPowerModel.class);
    double cost = output.get(RDDTApplication.OBJECTIVE_FLAG, Number.class).doubleValue();
    assertEquals(cost, -5000.0);
        
    Scenario scenario = configuration.getFirstScenario();
    
    for (ElectricPowerFlowConnection connection : model.getFlowConnections()) {
      boolean isConstructed = connection.getAttribute(AlgorithmConstants.IS_CONSTRUCTED_KEY) == null ? false : connection.getAttribute(AlgorithmConstants.IS_CONSTRUCTED_KEY, Boolean.class);
      boolean isSwitchConstructed = connection.getAttribute(AlgorithmConstants.IS_SWITCH_CONSTRUCTED_KEY) == null ? false : connection.getAttribute(AlgorithmConstants.IS_SWITCH_CONSTRUCTED_KEY, Boolean.class);
      ScenarioAttribute isScenarioOpen = connection.getAttribute(AlgorithmConstants.IS_SWITCH_OPEN_KEY, ScenarioAttribute.class);
      boolean isOpen = isScenarioOpen.getValue(scenario).intValue() == 1;
      boolean isHardened = connection.getAttribute(AlgorithmConstants.IS_HARDENED_KEY) == null ? false : connection.getAttribute(AlgorithmConstants.IS_HARDENED_KEY, Boolean.class);
      
      assertEquals(isConstructed, false);
      assertEquals(isSwitchConstructed, false);      
      assertEquals(isOpen, false);
      
      if (connection.toString().equals("l1")) {
        assertEquals(isHardened, true);
      }
      else {
        assertEquals(isHardened, false);        
      }      
    }
    
    for (Generator generator : model.getGenerators()) {
      boolean isConstructed = generator.getAttribute(AlgorithmConstants.IS_CONSTRUCTED_KEY) == null ? false : generator.getAttribute(AlgorithmConstants.IS_CONSTRUCTED_KEY, Boolean.class);
      assertEquals(isConstructed, false);              
    }

  }

  /**
   * Test to make sure that hardening lines works
   * @throws IOException
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws ClassNotFoundException
   */
  public void testExpansionVariable() throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
    String filename = "application_data" + File.separatorChar + "lpnorm" + File.separatorChar + "junit" + File.separatorChar + "line_only.json";

    LPNormJsonProjectConfigurationReader reader = new LPNormJsonProjectConfigurationReader();
    ProjectConfiguration configuration = reader.readConfiguration(filename);
    
    LPNormApplicationFactory factory = new LPNormApplicationFactory();
    Application application = factory.createApplication(configuration);
    ApplicationOutput output = application.execute();

    ElectricPowerModel model = output.get(RDDTApplication.MODEL_FLAG, ElectricPowerModel.class);
    Scenario scenario = configuration.getFirstScenario();
    
    double cost = output.get(RDDTApplication.OBJECTIVE_FLAG, Number.class).doubleValue();
    assertEquals(cost, -5000.0, 1e-2);
    
    for (ElectricPowerFlowConnection connection : model.getFlowConnections()) {
      boolean isConstructed = connection.getAttribute(AlgorithmConstants.IS_CONSTRUCTED_KEY) == null ? false : connection.getAttribute(AlgorithmConstants.IS_CONSTRUCTED_KEY, Boolean.class);
      boolean isSwitchConstructed = connection.getAttribute(AlgorithmConstants.IS_SWITCH_CONSTRUCTED_KEY) == null ? false : connection.getAttribute(AlgorithmConstants.IS_SWITCH_CONSTRUCTED_KEY, Boolean.class);
      ScenarioAttribute isScenarioOpen = connection.getAttribute(AlgorithmConstants.IS_SWITCH_OPEN_KEY, ScenarioAttribute.class);
      boolean isOpen = isScenarioOpen.getValue(scenario).intValue() == 1;
      boolean isHardened = connection.getAttribute(AlgorithmConstants.IS_HARDENED_KEY) == null ? false : connection.getAttribute(AlgorithmConstants.IS_HARDENED_KEY, Boolean.class);
            
      assertEquals(isSwitchConstructed, false);      
      assertEquals(isOpen, false);
      assertEquals(isHardened, false);        
      
      if (connection.toString().equals("l1-bypass")) {
        assertEquals(isConstructed, true);
      }
      else {
        assertEquals(isConstructed, false);
      }      
    }
    
    for (Generator generator : model.getGenerators()) {
      boolean isConstructed = generator.getAttribute(AlgorithmConstants.IS_CONSTRUCTED_KEY) == null ? false : generator.getAttribute(AlgorithmConstants.IS_CONSTRUCTED_KEY, Boolean.class);
      assertEquals(isConstructed, false);              
    }

  }

  
  /**
   * Test to make sure that hardening lines works
   * @throws IOException
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws ClassNotFoundException
   */
  public void testSwitchVariable() throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
    String filename = "application_data" + File.separatorChar + "lpnorm" + File.separatorChar + "junit" + File.separatorChar + "switch_only.json";

    LPNormJsonProjectConfigurationReader reader = new LPNormJsonProjectConfigurationReader();
    ProjectConfiguration configuration = reader.readConfiguration(filename);
    
    LPNormApplicationFactory factory = new LPNormApplicationFactory();
    Application application = factory.createApplication(configuration);
    ApplicationOutput output = application.execute();

    ElectricPowerModel model = output.get(RDDTApplication.MODEL_FLAG, ElectricPowerModel.class);
    Scenario scenario = configuration.getFirstScenario();
    
    double cost = output.get(RDDTApplication.OBJECTIVE_FLAG, Number.class).doubleValue();
    assertEquals(cost, -5000.0);

    
    for (ElectricPowerFlowConnection connection : model.getFlowConnections()) {
      boolean isConstructed = connection.getAttribute(AlgorithmConstants.IS_CONSTRUCTED_KEY) == null ? false : connection.getAttribute(AlgorithmConstants.IS_CONSTRUCTED_KEY, Boolean.class);
      boolean isSwitchConstructed = connection.getAttribute(AlgorithmConstants.IS_SWITCH_CONSTRUCTED_KEY) == null ? false : connection.getAttribute(AlgorithmConstants.IS_SWITCH_CONSTRUCTED_KEY, Boolean.class);
      ScenarioAttribute isScenarioOpen = connection.getAttribute(AlgorithmConstants.IS_SWITCH_OPEN_KEY, ScenarioAttribute.class);
      boolean isOpen = isScenarioOpen.getValue(scenario).intValue() == 1;
      boolean isHardened = connection.getAttribute(AlgorithmConstants.IS_HARDENED_KEY) == null ? false : connection.getAttribute(AlgorithmConstants.IS_HARDENED_KEY, Boolean.class);
            
      assertEquals(isHardened, false);        
      assertEquals(isConstructed, false);
      
      if (connection.toString().equals("l1-bypass2")) {
        assertEquals(isOpen, true);
        assertEquals(isSwitchConstructed, true);      
      }
      else {
        assertEquals(isSwitchConstructed, false);      
        assertEquals(isOpen, false);
      }      
    }
    
    for (Generator generator : model.getGenerators()) {
      boolean isConstructed = generator.getAttribute(AlgorithmConstants.IS_CONSTRUCTED_KEY) == null ? false : generator.getAttribute(AlgorithmConstants.IS_CONSTRUCTED_KEY, Boolean.class);
      assertEquals(isConstructed, false);              
    }

  }

  

  /**
   * Test to make sure that building generators works
   * @throws IOException
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws ClassNotFoundException
   */
  public void testGeneratorVariable() throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
    String filename = "application_data" + File.separatorChar + "lpnorm" + File.separatorChar + "junit" + File.separatorChar + "generator_only.json";

    LPNormJsonProjectConfigurationReader reader = new LPNormJsonProjectConfigurationReader();
    ProjectConfiguration configuration = reader.readConfiguration(filename);
    
    LPNormApplicationFactory factory = new LPNormApplicationFactory();
    Application application = factory.createApplication(configuration);
    ApplicationOutput output = application.execute();

    ElectricPowerModel model = output.get(RDDTApplication.MODEL_FLAG, ElectricPowerModel.class);
    Scenario scenario = configuration.getFirstScenario();
    double cost = output.get(RDDTApplication.OBJECTIVE_FLAG, Number.class).doubleValue();
    assertEquals(cost, -1001.413, 1.0);

    
    for (Generator generator : model.getGenerators()) {
      boolean isConstructed = generator.getAttribute(AlgorithmConstants.IS_CONSTRUCTED_KEY) == null ? false : generator.getAttribute(AlgorithmConstants.IS_CONSTRUCTED_KEY, Boolean.class);
      if (generator.toString().equals("806-gen")) {
        assertEquals(isConstructed, true);          
      }
      else {
        assertEquals(isConstructed, false);
      }
    }
    
    for (ElectricPowerFlowConnection connection : model.getFlowConnections()) {
      boolean isConstructed = connection.getAttribute(AlgorithmConstants.IS_CONSTRUCTED_KEY) == null ? false : connection.getAttribute(AlgorithmConstants.IS_CONSTRUCTED_KEY, Boolean.class);
      boolean isSwitchConstructed = connection.getAttribute(AlgorithmConstants.IS_SWITCH_CONSTRUCTED_KEY) == null ? false : connection.getAttribute(AlgorithmConstants.IS_SWITCH_CONSTRUCTED_KEY, Boolean.class);
      ScenarioAttribute isScenarioOpen = connection.getAttribute(AlgorithmConstants.IS_SWITCH_OPEN_KEY, ScenarioAttribute.class);
      boolean isOpen = isScenarioOpen.getValue(scenario).intValue() == 1;
      boolean isHardened = connection.getAttribute(AlgorithmConstants.IS_HARDENED_KEY) == null ? false : connection.getAttribute(AlgorithmConstants.IS_HARDENED_KEY, Boolean.class);
            
      assertEquals(isSwitchConstructed, false);      
      assertEquals(isOpen, false);
      assertEquals(isHardened, false);        
      assertEquals(isConstructed, false);
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
