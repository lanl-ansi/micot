package gov.lanl.micot.infrastructure.ep.model.powerworld;

import java.util.ArrayList;

import gov.lanl.micot.infrastructure.ep.io.powerworld.PowerworldIOConstants;
import gov.lanl.micot.infrastructure.ep.model.Bus;
import gov.lanl.micot.infrastructure.ep.model.ShuntCapacitor;
import gov.lanl.micot.infrastructure.ep.model.ShuntCapacitorFactory;
import gov.lanl.micot.util.collection.Pair;
import gov.lanl.micot.util.io.dcom.ComDataObject;
import gov.lanl.micot.util.io.dcom.ComObject;

/**
 * Factory for creating a shunt capacitor
 * @author Russell Bent
 */
public class PowerworldShuntCapacitorFactory extends ShuntCapacitorFactory {

	private static final String LEGACY_TAG = "POWERWORLD";
	
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
  public ShuntCapacitor createShuntCapacitor(ComObject powerworld, Bus bus, Pair<Integer, String> id)  {
    String fields[] = new String[]{PowerworldIOConstants.BUS_NUM, PowerworldIOConstants.SHUNT_ID, PowerworldIOConstants.SHUNT_MVAR, PowerworldIOConstants.SHUNT_MW, PowerworldIOConstants.SHUNT_STATUS}; 
    String values[] = new String[] {id.getOne()+"",id.getTwo(), "","",""};
        
    ComDataObject dataObject = powerworld.callData(PowerworldIOConstants.GET_PARAMETERS_SINGLE_ELEMENT, PowerworldIOConstants.SHUNT, fields, values);
    ArrayList<ComDataObject> shuntData = dataObject.getArrayValue();
    String errorString = shuntData.get(0).getStringValue();
    if (!errorString.equals("")) {
      System.err.println("Error getting powerworld shunt data: " + errorString);                
    }

    ArrayList<ComDataObject> lData = shuntData.get(1).getArrayValue();                       
    String mvarString = lData.get(2).getStringValue();
    String mwString = lData.get(3).getStringValue();
    String statusString = lData.get(4).getStringValue();
        
    double reactive = mvarString == null ? 0 : Double.parseDouble(mvarString);
    double real = mwString == null ? 0 : Double.parseDouble(mwString);                
    boolean status = Boolean.parseBoolean(statusString);   
    ShuntCapacitor shunt = registerCapacitor(id);    
        
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
  private ShuntCapacitor registerCapacitor(Pair<Integer,String> legacyId) {
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
