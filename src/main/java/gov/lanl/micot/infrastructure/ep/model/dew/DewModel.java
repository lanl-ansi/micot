package gov.lanl.micot.infrastructure.ep.model.dew;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;

/**
 * Interface for DEW models
 * @author Russell Bent
 */
public interface DewModel extends ElectricPowerModel {
  
  /**
   * Sync the model with the dew engine
   * @throws DewException
   */
  public void syncModel() throws DewException;
  
  /**
   * Get the dew engine
   * @return
   */
  public Dew getDew();

  
}
