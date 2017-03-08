package gov.lanl.micot.application.rdt;

import gov.lanl.micot.infrastructure.application.Application;
import gov.lanl.micot.infrastructure.application.ApplicationOutput;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.project.JsonProjectConfigurationReader;
import gov.lanl.micot.infrastructure.project.ProjectConfiguration;
import gov.lanl.micot.application.rdt.algorithm.AlgorithmConstants;
import gov.lanl.micot.application.rdt.RDDTApplication;
import gov.lanl.micot.application.rdt.RDDTApplicationFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import junit.framework.TestCase;

public class RobustEPNoHardenTest2SBD extends TestCase {

  /**
   * Test the robust IEP files
   * 
   * @throws IOException
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws ClassNotFoundException
   */
  public void testNoHardenMIPTreeContinousDetermine() throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
    if (System.getenv("TEST_RDT") != null) {
      return;
    }

    
   /* String masterFile = "config_files" + File.separatorChar + "Resilience" + File.separatorChar
        + "34Bus" + File.separatorChar + "Ice No Harden" + File.separatorChar + "Rural" 
        + File.separatorChar + "10% Per Mile Damage" + File.separatorChar 
        + "config-tree-continious-determine-sbd.json";
    String scenarioStart = "config_files" + File.separatorChar + "Resilience" + File.separatorChar + "34Bus" + File.separatorChar + "Ice No Harden" + File.separatorChar + "Rural" + File.separatorChar + "10% Per Mile Damage" + File.separatorChar + "config-";
    ArrayList<String> scenarioFiles = new ArrayList<String>();
    for (int i = 1; i <= 2; ++i) {
      scenarioFiles.add(scenarioStart + i + ".json");
    }

    JsonProjectConfigurationReader reader = new JsonProjectConfigurationReader();
    ProjectConfiguration configuration = reader.readConfiguration(masterFile, scenarioFiles);
    RDDTApplicationFactory factory = new RDDTApplicationFactory();
    Application application = factory.createApplication(configuration);
    ApplicationOutput output = application.execute();
    double objValue = output.getDouble(RDDTApplication.OBJECTIVE_FLAG);        
    ElectricPowerModel model = output.get(RDDTApplication.MODEL_FLAG, ElectricPowerModel.class);

    assertEquals(2890.0998, -objValue, 1e-4);
    for (ElectricPowerFlowConnection connection : model.getFlowConnections()) {
      if (connection.toString().equals("n858_816")) {
        assertEquals(true, connection.getAttribute(AlgorithmConstants.IS_CONSTRUCTED_KEY));
      }
      if (connection.toString().equals("n1816_2816_1")) {
        assertEquals(true, connection.getAttribute(AlgorithmConstants.IS_CONSTRUCTED_KEY));
      }
    }*/
  }


}
