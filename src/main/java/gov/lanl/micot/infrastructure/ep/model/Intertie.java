package gov.lanl.micot.infrastructure.ep.model;

/**
 * Abstract interface for Interties
 * @author Russell Bent
 */
public interface Intertie extends ElectricPowerConnection {
  
  public static final String NAME_KEY                        = "NAME";
    
  /**
   * Add an intertie data listener
   * @param listener
   */
  public void addIntertieChangeListener(IntertieChangeListener listener);
  
  /**
   * Remove an intertie data listener
   * @param listener
   */
  public void removeIntertieChangeListener(IntertieChangeListener listener);

  /**
   * Clone an intertie
   * @return
   */
  public Intertie clone();

  
  
}
