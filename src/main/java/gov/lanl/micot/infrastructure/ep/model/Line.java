package gov.lanl.micot.infrastructure.ep.model;

/**
 * Interface for power lines
 * @author Russell Bent
 */
public interface Line extends ElectricPowerFlowConnection {	
  
  /**
   * Adds the line data listener
   * @param listener
   */
  public void addLineChangeListener(LineChangeListener listener);
  
  /**
   * Remove the line data listener
   * @param listener
   */
  public void removeLineChangeListener(LineChangeListener listener);
  
  /**
   * Clone a line
   * @return
   */
  public Line clone();

}
