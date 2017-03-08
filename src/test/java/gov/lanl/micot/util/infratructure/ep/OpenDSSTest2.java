package gov.lanl.micot.util.infratructure.ep;

import java.io.File;
import java.io.IOException;

import gov.lanl.micot.infrastructure.ep.io.ElectricPowerModelFile;
import gov.lanl.micot.infrastructure.ep.io.ElectricPowerModelFileFactory;
import gov.lanl.micot.infrastructure.ep.model.Bus;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.Line;
import gov.lanl.micot.infrastructure.ep.model.Load;
import gov.lanl.micot.infrastructure.ep.model.opendss.OpenDSSModelConstants;
import junit.framework.TestCase;

/**
 * Test cases for open dss models
 * 
 * Designed for testing reading of opendss models from non windows platforms
 * 
 * @author Russell Bent
 */
public class OpenDSSTest2 extends TestCase {
    

  /**
   * Tests the output of demo file
   * @throws IOException 
   */
 public void test37Bus() throws IOException {
    String initialFile = System.getProperty("user.dir") + File.separatorChar + "test_data" + File.separatorChar + "ep" + File.separatorChar + "opendss" + File.separatorChar + "37Bus" + File.separatorChar + "ieee37" + File.separatorChar + "Master.DSS";
    ElectricPowerModelFile modelfile = (new ElectricPowerModelFileFactory()).createModelFile(initialFile); 
    ElectricPowerModel model = modelfile.readModel(initialFile);    
    assertEquals(model.getBuses().size(), 39);
    assertEquals(model.getGenerators().size(), 1);
    assertEquals(model.getLines().size(), 36);
    assertEquals(model.getLoads().size(), 30);
    assertEquals(model.getTransformers().size(), 4);
    
    for (Bus bus : model.getBuses()) {
      if (bus.getAttribute(OpenDSSModelConstants.OPENDSS_LEGACY_ID_KEY).equals("720")) {
        assertEquals(bus.getCoordinate().getX(), 1.68);
        assertEquals(bus.getCoordinate().getY(), -2.81);
      }
      
    }

    for (Line line : model.getLines()) {
      if (line.getAttribute(OpenDSSModelConstants.OPENDSS_LEGACY_ID_KEY).equals("l31")) {
        assertEquals(line.getAttribute(Line.LENGTH_KEY), 0.4);
        assertEquals(line.getAttribute(Line.IS_PHASE_A_KEY), true);
        assertEquals(line.getAttribute(Line.IS_PHASE_B_KEY), true);
        assertEquals(line.getAttribute(Line.IS_PHASE_C_KEY), true);        
      }      
    }
    
  
    for (Load load : model.getLoads()) {
      if (load.getAttribute(OpenDSSModelConstants.OPENDSS_LEGACY_ID_KEY).equals("s701c")) {
        assertEquals(load.getAttribute(Load.NUM_PHASE_KEY), 1);
        assertEquals(load.getAttribute(Load.HAS_PHASE_A_KEY), false);
        assertEquals(load.getAttribute(Load.HAS_PHASE_C_KEY), true);
        assertEquals(load.getAttribute(Load.ACTUAL_REACTIVE_LOAD_A_KEY), 0.0);
        assertEquals(load.getAttribute(Load.ACTUAL_REAL_LOAD_A_KEY), 0.0);

        assertEquals(load.getAttribute(Load.ACTUAL_REACTIVE_LOAD_C_KEY), 175.0);
        assertEquals(load.getAttribute(Load.ACTUAL_REAL_LOAD_C_KEY), 350.0);
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
