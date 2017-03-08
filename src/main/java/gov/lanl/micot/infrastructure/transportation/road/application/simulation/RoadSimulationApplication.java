package gov.lanl.micot.infrastructure.transportation.road.application.simulation;

import gov.lanl.micot.infrastructure.application.Application;
import gov.lanl.micot.infrastructure.application.ApplicationOutput;
import gov.lanl.micot.infrastructure.simulate.Simulator.SimulatorSolveState;
import gov.lanl.micot.infrastructure.transportation.road.model.RoadModel;
import gov.lanl.micot.infrastructure.transportation.road.simulate.RoadSimulator;

/**
 * This class defines the telecommunications simulation as an application
 * @author Russell Bent
 *
 */
public class RoadSimulationApplication implements Application {
  
  public static final String MODEL_FLAG = "Model";
  public static final String SIMULATOR_STATE_FLAG = "SimulatorState";
   
  private RoadModel model = null;
  private RoadSimulator simulator = null;
    
  /**
   * Only one way to instantiate it
   */
  protected RoadSimulationApplication() {    
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
  public void setModel(RoadModel model) {
    this.model = model;
  }

  /**
   * Sets the simulator algorithm
   * @param simulator
   */
  public void setSimulator(RoadSimulator simulator) {
    this.simulator = simulator;
  }
  
  /**
   * Get the the road model
   * @return
   */
  public RoadModel getModel() {
    return model;
  }
}
