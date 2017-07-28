package gov.lanl.micot.infrastructure.ep.application.ac;

import gov.lanl.micot.infrastructure.application.Application;
import gov.lanl.micot.infrastructure.application.ApplicationOutput;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.simulate.ElectricPowerSimulator;
import gov.lanl.micot.infrastructure.model.Asset;
import gov.lanl.micot.infrastructure.simulate.Simulator.SimulatorSolveState;
import gov.lanl.micot.util.time.Timer;

/**
 * This class defines the severe contigency solver tool as an application
 * @author Russell Bent
 *
 */
public class ACSimulationApplication implements Application {
  
  public static final String MODEL_FLAG = "Model";
  public static final String SIMULATOR_STATE_FLAG = "SimulatorState";
	public static final String CPU_TIME_FLAG = "CPUTime";
   
  private ElectricPowerModel model = null;
  private ElectricPowerSimulator simulator = null;
    
  /**
   * Only one way to instantiate it
   */
  protected ACSimulationApplication() {    
  }
  
  @Override
  public ApplicationOutput execute() {      
    // make the actual status the desired status to start out....
    for (Asset asset : model.getAssets()) {
      asset.setActualStatus(asset.getDesiredStatus());
    }
    
    Timer timer = new Timer();
    timer.startTimer();
    
    // simulate and see if our optimized solution is great
    SimulatorSolveState state = simulator.executeSimulation(model);
       
    ApplicationOutput output = new ApplicationOutput();
    output.put(CPU_TIME_FLAG, timer.getCPUMinutesDec());
    output.put(SIMULATOR_STATE_FLAG, state);
    output.put(MODEL_FLAG, model);
    return output;
  }

  /**
   * Set the model
   * @param model
   */
  public void setModel(ElectricPowerModel model) {
    this.model = model;
  }

  /**
   * Sets the simualtor algorithm
   * @param simulator
   */
  public void setSimulator(ElectricPowerSimulator simulator) {
    this.simulator = simulator;
  }
  
}
