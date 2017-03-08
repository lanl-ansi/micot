package gov.lanl.micot.infrastructure.ep.model.powerworld;

import java.util.ArrayList;

import gov.lanl.micot.infrastructure.ep.io.powerworld.PowerworldIOConstants;
import gov.lanl.micot.infrastructure.ep.model.Bus;
import gov.lanl.micot.infrastructure.ep.model.ShuntCapacitorSwitch;
import gov.lanl.micot.infrastructure.ep.model.ShuntCapacitorSwitchFactory;
import gov.lanl.micot.util.io.dcom.ComDataObject;
import gov.lanl.micot.util.io.dcom.ComObject;

/**
 * Factory for creating shunt capacitor switches
 * @author Russell Bent
 */
public class PowerworldShuntCapacitorSwitchFactory extends ShuntCapacitorSwitchFactory {

//	private static PowerworldShuntCapacitorSwitchFactory INSTANCE = null;
	private static final String LEGACY_TAG = "POWERWORLD";

	
	//public synchronized static PowerworldShuntCapacitorSwitchFactory getInstance() {
		//if (INSTANCE == null) {
			//INSTANCE = new PowerworldShuntCapacitorSwitchFactory();
		//}
		//return INSTANCE;
	//}
	
	/**
	 * Singleton constructor
	 */
	protected PowerworldShuntCapacitorSwitchFactory() {		
	}
	
	/**
   * Creates a capacitor and data from a capacitor
   * @param shunt
   * @return
   */
  public ShuntCapacitorSwitch createShuntCapacitorSwitch(ComObject powerworld, Bus bus, int busId)  {
    String fields[] = new String[]{PowerworldIOConstants.BUS_NUM, PowerworldIOConstants.SHUNT_SS_MVAR, 
        PowerworldIOConstants.SHUNT_SS_MW,  
        PowerworldIOConstants.SHUNT_MAX_MVAR, PowerworldIOConstants.SHUNT_MAX_MW, 
        PowerworldIOConstants.SHUNT_MIN_MVAR, PowerworldIOConstants.SHUNT_MIN_MW }; 
    String values[] = new String[] {busId+"", "","","","","",""};
        
    ComDataObject dataObject = powerworld.callData(PowerworldIOConstants.GET_PARAMETERS_SINGLE_ELEMENT, PowerworldIOConstants.BUS, fields, values);
    ArrayList<ComDataObject> shuntData = dataObject.getArrayValue();
    String errorString = shuntData.get(0).getStringValue();
    if (!errorString.equals("")) {
      System.err.println("Error getting powerworld shunt data: " + errorString);                
    }

    ArrayList<ComDataObject> lData = shuntData.get(1).getArrayValue();                       
    String mvarString = lData.get(1).getStringValue();
    String mwString = lData.get(2).getStringValue();
    String maxMVarString = lData.get(3).getStringValue();
    String maxMWString = lData.get(4).getStringValue();
    String minMVarString = lData.get(5).getStringValue();
    String minMWString = lData.get(6).getStringValue();    

    double maxMVar =  maxMVarString == null ? 0 : Double.parseDouble(maxMVarString);
    double minMVar =  maxMVarString == null ? 0 : Double.parseDouble(minMVarString);
    double minMW =  minMWString == null ? 0 : Double.parseDouble(minMWString);
    double maxMW =  maxMWString == null ? 0 : Double.parseDouble(maxMWString);
    double mw = mwString == null ? 0 : Double.parseDouble(mwString);
    double mvar = mvarString == null ? 0 : Double.parseDouble(mvarString);
    
    boolean status = bus.getActualStatus();   
    ShuntCapacitorSwitch shunt = registerCapacitor(busId);    
        
    shunt.setDesiredStatus(status);
    shunt.setActualStatus(status);
    shunt.setCoordinate(bus.getCoordinate());
    shunt.setMaxMW(maxMW);
    shunt.setMinMW(minMW);
    shunt.setMaxMVar(maxMVar);
    shunt.setMinMVar(minMVar);
    shunt.setRealCompensation(mw);
    shunt.setReactiveCompensation(mvar);

    //System.out.println(bus + " " + mw + " " + mvar + " " + maxMW + " " + minMW + " " + maxMVar + " " + minMVar);
    
    return shunt;    
  }
  
  /**
  * Register the shunt capacitor
  * @param legacyId
  * @param bus
  * @return
  */
  private ShuntCapacitorSwitch registerCapacitor(int legacyId) {
    ShuntCapacitorSwitch capacitor = getLegacy(LEGACY_TAG, legacyId);

    if (capacitor == null) {
      capacitor = createNewSwitch();
      capacitor.setAttribute(PowerworldModelConstants.POWERWORLD_LEGACY_ID_KEY,legacyId);
      capacitor.addOutputKey(PowerworldModelConstants.POWERWORLD_LEGACY_ID_KEY);      
      registerLegacy(LEGACY_TAG, legacyId, capacitor);
    }
   return capacitor;
 }

	
}
