package gov.lanl.micot.util.infratructure.ep;

import gov.lanl.micot.infrastructure.application.Application;
import gov.lanl.micot.infrastructure.application.ApplicationOutput;
import gov.lanl.micot.infrastructure.ep.application.ac.ACSimulationApplication;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.dew.DewLegacyId;
import gov.lanl.micot.infrastructure.ep.model.dew.DewVariables;
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
public class DewTest1 extends TestCase {
    
  /**
   * Test the simulation of a model with batteries to make sure we get the expected data formulation
   * @throws IOException
   * @throws InterruptedException
   * @throws ClassNotFoundException 
   * @throws IllegalAccessException 
   * @throws InstantiationException 
   */
  public void testDewSimulator() throws IOException, InterruptedException, InstantiationException, IllegalAccessException, ClassNotFoundException {
    if (System.getenv("TEST_DEW") != null) {
      return;
    }
    
    String masterFile = "test_data" + File.separatorChar + "ep" + File.separatorChar + "config-dew-test1.json";    
    JsonProjectConfigurationReader reader = new JsonProjectConfigurationReader();
    ProjectConfiguration configuration = reader.readConfiguration(masterFile);
    Application application = ProjectConfigurationUtility.createApplication(configuration);
    ApplicationOutput output = application.execute();
    ElectricPowerModel model = output.get(ACSimulationApplication.MODEL_FLAG, ElectricPowerModel.class);
    
    for (ElectricPowerFlowConnection edge : model.getFlowConnections()) {
      DewLegacyId legacyid = edge.getAttribute(DewVariables.DEW_LEGACY_ID_KEY, DewLegacyId.class);
      if (legacyid.getTwo() == 487) {
        assertEquals(6.600438312997162, edge.getMWFlow().doubleValue(), 1e-4);
        assertEquals(2.711600774710057, edge.getMVarFlow().doubleValue(), 1e-4);

      }
    }

    // turning off bus/edge #487
    for (ElectricPowerFlowConnection edge : model.getFlowConnections()) {
      if (edge.getAttribute(DewVariables.DEW_LEGACY_ID_KEY, DewLegacyId.class).getTwo() == 487) {
        edge.setStatus(false);
      }
    }
    
    output = application.execute();

    for (ElectricPowerFlowConnection edge : model.getFlowConnections()) {
      DewLegacyId legacyid = edge.getAttribute(DewVariables.DEW_LEGACY_ID_KEY, DewLegacyId.class);
      if (legacyid.getTwo() == 487) {
        assertEquals(0.0, edge.getMWFlow().doubleValue(), 1e-4);
        assertEquals(0.0, edge.getMVarFlow().doubleValue(), 1e-4);        
      }
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
