package gov.lanl.micot.infrastructure.ep.model.powerworld;


import gov.lanl.micot.infrastructure.ep.model.BatteryFactory;

/**
 * Factory for creating batteries
 * 
 * @author Russell Bent
 */
public class PowerworldBatteryFactory extends BatteryFactory {

//  private static PowerworldBatteryFactory INSTANCE           = null;
  private static final String LEGACY_TAG = "Powerworld";
  
  //public synchronized static PowerworldBatteryFactory getInstance() {
    //if (INSTANCE == null) {
      //INSTANCE = new PowerworldBatteryFactory();
    //}
    //return INSTANCE;
 // }

  /**
   * Singleton constructor
   */
  protected PowerworldBatteryFactory() {
  }


	
  
}
