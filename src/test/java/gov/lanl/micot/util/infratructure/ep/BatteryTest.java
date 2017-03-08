package gov.lanl.micot.util.infratructure.ep;

import gov.lanl.micot.infrastructure.application.Application;
import gov.lanl.micot.infrastructure.application.ApplicationOutput;
import gov.lanl.micot.infrastructure.ep.application.ac.ACSimulationApplication;
import gov.lanl.micot.infrastructure.ep.io.pfw.PFWFile;
import gov.lanl.micot.infrastructure.ep.model.Battery;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.Generator;
import gov.lanl.micot.infrastructure.project.JsonProjectConfigurationReader;
import gov.lanl.micot.infrastructure.project.ProjectConfiguration;
import gov.lanl.micot.infrastructure.project.ProjectConfigurationUtility;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import junit.framework.TestCase;


/**
 * Test cases for testing the battery implementations
 * @author Russell Bent
 */
public class BatteryTest extends TestCase {
    
  /**
   * Tests the input of battery files
   * @throws IOException 
   */
  public void testDataInput() throws IOException {
    String initialFile = "test_data" + File.separatorChar + "ep" + File.separatorChar + "battery-test.pfw";
    ElectricPowerModel model = (ElectricPowerModel)new PFWFile().readModel(initialFile);    
    
    Collection<? extends Generator> generators = model.getGenerators();
    Collection<? extends Battery> batteries = model.getBatteries();

    assertEquals(batteries.size(),1);
    assertEquals(generators.size(),1);    
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
