package gov.lanl.micot.infrastructure.ep.model;

/**
 * Interface for DC power lines
 * @author Russell Bent
 */
public interface DCLine extends ElectricPowerFlowConnection {	
  
  /**
   * Adds the line data listener
   * @param listener
   */
  public void addDCLineChangeListener(DCLineChangeListener listener);
  
  /**
   * Remove the line data listener
   * @param listener
   */
  public void removeDCLineChangeListener(DCLineChangeListener listener);
  
  /**
   * Clone a line
   * @return
   */
  public DCLine clone();

}
