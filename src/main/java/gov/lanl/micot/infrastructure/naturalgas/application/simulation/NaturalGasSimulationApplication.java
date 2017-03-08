package gov.lanl.micot.infrastructure.naturalgas.application.simulation;

import gov.lanl.micot.infrastructure.application.Application;
import gov.lanl.micot.infrastructure.application.ApplicationOutput;
import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasModel;
import gov.lanl.micot.infrastructure.naturalgas.simulate.NaturalGasSimulator;
import gov.lanl.micot.infrastructure.simulate.Simulator.SimulatorSolveState;

/**
 * This class defines the natural gas simulation as an application
 * @author Russell Bent
 *
 */
public class NaturalGasSimulationApplication implements Application {
  
  public static final String MODEL_FLAG = "Model";
  public static final String SIMULATOR_STATE_FLAG = "SimulatorState";
   
  private NaturalGasModel model = null;
  private NaturalGasSimulator simulator = null;
    
  /**
   * Only one way to instantiate it
   */
  protected NaturalGasSimulationApplication() {    
  }
  
  @Override
  public ApplicationOutput execute() {      
    // simulate and see if our optimized solution is great
    SimulatorSolveState state = simulator.executeSimulation(model);
       
    ApplicationOutput output = new ApplicationOutput();
    output.put(SIMULATOR_STATE_FLAG, state);
    output.put(MODEL_FLAG, model);
    return output;
  }

  /**
   * Set the model
   * @param model
   */
  public void setModel(NaturalGasModel model) {
    this.model = model;
  }

  /**
   * Sets the simulator algorithm
   * @param simulator
   */
  public void setSimulator(NaturalGasSimulator simulator) {
    this.simulator = simulator;
  }
  
}
