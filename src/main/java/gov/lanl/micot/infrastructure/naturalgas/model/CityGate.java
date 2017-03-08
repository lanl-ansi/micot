package gov.lanl.micot.infrastructure.naturalgas.model;

import gov.lanl.micot.infrastructure.model.Component;
import gov.lanl.micot.infrastructure.model.Consumer;

/**
 * Abstract class for defining loads
 * @author Russell Bent
 *
 */
public interface CityGate extends Consumer, Component {	
  
  public static final String CITYGATE_NAME_KEY                   = "NAME";
  
  /**
   * Add a listener for data changes
   * @param listener
   */
  public void addCityGateChangeListener(CityGateChangeListener listener);
  
  /**
   * Add a listener for data changes
   * @param listener
   */
  public void removeCityGateChangeListener(CityGateChangeListener listener);
  
  /**
   * Clone a city gate
   * @return
   */
  public CityGate clone();
  
}
