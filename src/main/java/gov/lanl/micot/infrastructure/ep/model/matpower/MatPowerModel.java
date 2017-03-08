package gov.lanl.micot.infrastructure.ep.model.matpower;

/**
 * Interface for MatPower models
 * @author Russell Bent
 */
public interface MatPowerModel {
  
  /**
   * Get the version
   * @return
   */
  public String getVersion();
  
  /**
   * Set the version
   * @param version
   */
  public void setVersion(String version);
  
  /**
   * Get case
   * @return
   */
  public String getCase();
  
  /**
   * Set case
   * @param sCase
   */
  public void setCase(String sCase);
		  
}
