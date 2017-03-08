package gov.lanl.micot.infrastructure.ep.model.powerworld;

import java.util.ArrayList;

import gov.lanl.micot.infrastructure.ep.io.powerworld.PowerworldIOConstants;
import gov.lanl.micot.infrastructure.ep.model.Bus;
import gov.lanl.micot.infrastructure.ep.model.ShuntCapacitor;
import gov.lanl.micot.infrastructure.ep.model.ShuntCapacitorFactory;
import gov.lanl.micot.util.io.dcom.ComDataObject;
import gov.lanl.micot.util.io.dcom.ComObject;

/**
 * Factory for creating a shunt capacitor
 * @author Russell Bent
 */
public class PowerworldShuntCapacitorFactory extends ShuntCapacitorFactory {

//	private static PowerworldShuntCapacitorFactory INSTANCE = null;
	private static final String LEGACY_TAG = "POWERWORLD";
	
	//public synchronized static PowerworldShuntCapacitorFactory getInstance() {
		//if (INSTANCE == null) {
			//INSTANCE = new PowerworldShuntCapacitorFactory();
		//}
		//return INSTANCE;
	//}
		
	/**
	 * Constructor
	 */
	protected PowerworldShuntCapacitorFactory() {	
	}
	
  /**
   * Creates a capacitor and data from a capacitor
   * @param shunt
   * @return
   */
  public ShuntCapacitor createShuntCapacitor(ComObject powerworld, Bus bus, int busid)  {
    String fields[] = new String[]{PowerworldIOConstants.BUS_NUM, PowerworldIOConstants.SHUNT_MVAR, PowerworldIOConstants.SHUNT_MW}; 
    String values[] = new String[] {busid+"", "",""};
        
    ComDataObject dataObject = powerworld.callData(PowerworldIOConstants.GET_PARAMETERS_SINGLE_ELEMENT, PowerworldIOConstants.BUS, fields, values);
    ArrayList<ComDataObject> shuntData = dataObject.getArrayValue();
    String errorString = shuntData.get(0).getStringValue();
    if (!errorString.equals("")) {
      System.err.println("Error getting powerworld shunt data: " + errorString);                
    }

    ArrayList<ComDataObject> lData = shuntData.get(1).getArrayValue();                       
    String mvarString = lData.get(1).getStringValue();
    String mwString = lData.get(2).getStringValue();
        
    double reactive = mvarString == null ? 0 : Double.parseDouble(mvarString);
    double real = mwString == null ? 0 : Double.parseDouble(mwString);                
    boolean status = true;   
    ShuntCapacitor shunt = registerCapacitor(busid);    
        
    shunt.setDesiredStatus(status);
    shunt.setActualStatus(status);
    shunt.setReactiveCompensation(reactive);    
    shunt.setRealCompensation(real);
    shunt.setCoordinate(bus.getCoordinate());
    
  //  System.out.println(bus + " " + real + " " + reactive);
    
    return shunt;    
  }
  
  /**
  * Register the shunt capacitor
  * @param legacyId
  * @param bus
  * @return
  */
  private ShuntCapacitor registerCapacitor(int legacyId) {
    ShuntCapacitor capacitor = getLegacy(LEGACY_TAG, legacyId);

    if (capacitor == null) {
      capacitor = createNewShuntCapacitor();
      capacitor.setAttribute(PowerworldModelConstants.POWERWORLD_LEGACY_ID_KEY,legacyId);
      capacitor.addOutputKey(PowerworldModelConstants.POWERWORLD_LEGACY_ID_KEY);      
      registerLegacy(LEGACY_TAG, legacyId, capacitor);
    }
   return capacitor;
 }

}
