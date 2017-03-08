package gov.lanl.micot.infrastructure.project;

import gov.lanl.micot.util.io.Flags;
import gov.lanl.micot.util.io.FlagsImpl;

/**
 * Simulator Configuration
 * @author Russell Bent
 */
public class SimulatorConfiguration {

  private String simulatorFactoryClass                                = null;
  private Flags simulatorFlags                                        = null;

  /**
   * Constructor
   */
  public SimulatorConfiguration() { 
    simulatorFlags = new FlagsImpl();
  }

  /**
   * @return the algorithmFactoryClass
   */
  public String getSimulatorFactoryClass() {
    return simulatorFactoryClass;
  }

  /**
   * @param algorithmFactoryClass the algorithmFactoryClass to set
   */
  public void setSimulatorFactoryClass(String simulatorFactoryClass) {
    this.simulatorFactoryClass = simulatorFactoryClass;
  }

  /**
   * @return the algorithmFlags
   */
  public Flags getSimulatorFlags() {
    return simulatorFlags;
  }

  /**
   * @param algorithmFlags the algorithmFlags to set
   */
  public void setSimulatorFlags(Flags simulatorFlags) {
    this.simulatorFlags = simulatorFlags;
  }

  /**
   * Adds an algorithm flag
   * @param key
   * @param value
   */
  public void addSimulatorFlag(String key, Object value) {
    simulatorFlags.put(key, value);
  }

	
}
