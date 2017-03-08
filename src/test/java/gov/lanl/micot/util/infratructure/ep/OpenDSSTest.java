package gov.lanl.micot.util.infratructure.ep;

import gov.lanl.micot.infrastructure.application.Application;
import gov.lanl.micot.infrastructure.application.ApplicationOutput;
import gov.lanl.micot.infrastructure.ep.application.ac.ACSimulationApplication;
import gov.lanl.micot.infrastructure.ep.io.ElectricPowerModelFile;
import gov.lanl.micot.infrastructure.ep.io.ElectricPowerModelFileFactory;
import gov.lanl.micot.infrastructure.ep.model.Bus;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.simulate.opendss.OpenDSSSimulator;
import gov.lanl.micot.infrastructure.ep.simulate.opendss.OpenDSSSimulatorFactory;
import gov.lanl.micot.infrastructure.project.JsonProjectConfigurationReader;
import gov.lanl.micot.infrastructure.project.ProjectConfiguration;
import gov.lanl.micot.infrastructure.project.ProjectConfigurationUtility;
import gov.lanl.micot.infrastructure.simulate.SimulatorFlags;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

/**
 * Test cases for open dss models
 * @author Russell Bent
 */
public class OpenDSSTest extends TestCase {
    
  /**
   * Tests the output of demo file
   * @throws IOException 
   * @throws ClassNotFoundException 
   * @throws IllegalAccessException 
   * @throws InstantiationException 
   */
  public void test34Bus() throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
    if (System.getenv("TEST_OPENDSS") != null) {
      return;
    }

    String masterFile = "test_data" + File.separatorChar + "ep" + File.separatorChar + "config-opendss.json";    
    JsonProjectConfigurationReader reader = new JsonProjectConfigurationReader();
    ProjectConfiguration configuration = reader.readConfiguration(masterFile);
    Application application = ProjectConfigurationUtility.createApplication(configuration);
    ApplicationOutput output = application.execute();
    ElectricPowerModel model = output.get(ACSimulationApplication.MODEL_FLAG, ElectricPowerModel.class);    
    assertEquals(model.getBuses().size(), 37);
    
    OpenDSSSimulator simulator = (new OpenDSSSimulatorFactory()).createSimulator(new SimulatorFlags());
    simulator.executeSimulation(model);
    
    for (Bus bus : model.getBuses()) {
      System.out.println(bus + " " + bus.getVoltagePU());
      if (bus.toString().equals("890")) {
        assertEquals(bus.getVoltagePU().doubleValue(), 0.9252393628285298, 1e-4);
      }
    }
    
  }

  /**
   * Tests the output of demo file
   * @throws IOException 
   */
 public void test30Bus() throws IOException { 
    String initialFile = System.getProperty("user.dir") + File.separatorChar + "test_data" + File.separatorChar + "ep" + File.separatorChar + "opendss" + File.separatorChar + "IEEE 30 Bus" + File.separatorChar + "Master.dss";
    ElectricPowerModelFile modelfile = (new ElectricPowerModelFileFactory()).createModelFile(initialFile); 
    ElectricPowerModel model = modelfile.readModel(initialFile);    
    assertEquals(model.getBuses().size(), 30);
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
