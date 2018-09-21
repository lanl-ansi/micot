package gov.lanl.micot.util.infratructure.ep;

import gov.lanl.micot.infrastructure.application.Application;
import gov.lanl.micot.infrastructure.application.ApplicationOutput;
import gov.lanl.micot.infrastructure.ep.application.ac.ACSimulationApplication;
import gov.lanl.micot.infrastructure.ep.exec.RunPowerworld;
import gov.lanl.micot.infrastructure.ep.io.ElectricPowerModelFileFactory;
import gov.lanl.micot.infrastructure.ep.io.powerworld.PowerworldModelFile;
import gov.lanl.micot.infrastructure.ep.model.Bus;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.ep.model.ThreeWindingTransformer;
import gov.lanl.micot.infrastructure.ep.model.Transformer;
import gov.lanl.micot.infrastructure.ep.simulate.powerworld.JSONResultExporter;
import gov.lanl.micot.infrastructure.project.JsonProjectConfigurationReader;
import gov.lanl.micot.infrastructure.project.ProjectConfiguration;
import gov.lanl.micot.infrastructure.project.ProjectConfigurationUtility;
import gov.lanl.micot.infrastructure.simulate.Simulator.SimulatorSolveState;
import gov.lanl.micot.util.io.ParameterReader;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import junit.framework.TestCase;
import gov.lanl.micot.infrastructure.ep.model.Line;


/**
 * Test cases for some SCS test data sets
 * @author Russell Bent
 */
public class SCSTest extends TestCase {
    
  /**
   * Tests the output of demo file
   * @throws IOException 
   * @throws ClassNotFoundException 
   * @throws IllegalAccessException 
   * @throws InstantiationException 
   */
  public void testErcot() throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
    if (System.getenv("TEST_POWERWORLD") != null) {
      return;
    }
    
    ElectricPowerModelFileFactory.registerExtension("raw",Class.forName("gov.lanl.micot.infrastructure.ep.io.powerworld.PowerworldModelFile"));
    
    String modificationsFile = "C:\\Users\\210117\\Data\\SCS Data\\ercot15_pw_cfg.json";       
    String powerModelFile = "C:\\Users\\210117\\Data\\SCS Data\\ercot15.raw";
        
    ProjectConfiguration configuration = RunPowerworld.buildConfiguration(powerModelFile, modificationsFile);    
    Application application = ProjectConfigurationUtility.createApplication(configuration);

    ApplicationOutput output = application.execute();

    SimulatorSolveState state = output.get(ACSimulationApplication.SIMULATOR_STATE_FLAG, SimulatorSolveState.class);
    assertEquals(state, SimulatorSolveState.CONVERGED_SOLUTION);
  }

  public void testPepco() throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
    if (System.getenv("TEST_POWERWORLD") != null) {
      return;
    }
    
    ElectricPowerModelFileFactory.registerExtension("raw",Class.forName("gov.lanl.micot.infrastructure.ep.io.powerworld.PowerworldModelFile"));
    
    String modificationsFile = "C:\\Users\\210117\\Data\\SCS Data\\pepco_dvm_pw_cfg.json";       
    String powerModelFile = "C:\\Users\\210117\\Data\\SCS Data\\pepco_dvm.raw";
        
    ProjectConfiguration configuration = RunPowerworld.buildConfiguration(powerModelFile, modificationsFile);    
    Application application = ProjectConfigurationUtility.createApplication(configuration);

    ApplicationOutput output = application.execute();

    SimulatorSolveState state = output.get(ACSimulationApplication.SIMULATOR_STATE_FLAG, SimulatorSolveState.class);
    assertEquals(state, SimulatorSolveState.CONVERGED_SOLUTION);
  }
  
  
  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
  }
  
  @Override
  protected void setUp() throws Exception {
    super.setUp();          
    ElectricPowerModelFileFactory.registerExtension("raw",Class.forName("gov.lanl.micot.infrastructure.ep.io.powerworld.PowerworldModelFile"));
  } 
  

  
  
}
