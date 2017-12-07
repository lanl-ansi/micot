package gov.lanl.micot.infrastructure.ep.model.powerworld;

import java.util.ArrayList;

import gov.lanl.micot.infrastructure.ep.io.powerworld.PowerworldIOConstants;
import gov.lanl.micot.infrastructure.ep.model.Bus;
import gov.lanl.micot.infrastructure.ep.model.Load;
import gov.lanl.micot.infrastructure.ep.model.LoadFactory;
import gov.lanl.micot.util.collection.Pair;
import gov.lanl.micot.util.io.dcom.ComDataObject;
import gov.lanl.micot.util.io.dcom.ComObject;

/**
 * Factory for creating loads
 * @author Russell Bent
 */
public class PowerworldLoadFactory extends LoadFactory {

	private static final String LEGACY_TAG = "Powerworld";
	
	/**
	 * Singleton constructor
	 */
	protected PowerworldLoadFactory() {
	}
		
	/**
   * Creates a bus and data from a bus
   * @param line
   * @return
   */
  public Load createLoad(ComObject powerworld, Bus bus, Pair<Integer, String> legacyId)  {
    String fields[] = new String[]{PowerworldIOConstants.BUS_NUM, PowerworldIOConstants.LOAD_NUM, PowerworldIOConstants.LOAD_MVAR, PowerworldIOConstants.LOAD_MW, PowerworldIOConstants.LOAD_STATUS}; 
    String values[] = new String[] {legacyId.getLeft()+"", legacyId.getRight()+"","","",""};
        
    ComDataObject dataObject = powerworld.callData(PowerworldIOConstants.GET_PARAMETERS_SINGLE_ELEMENT, PowerworldIOConstants.LOAD, fields, values);
    ArrayList<ComDataObject> loadData = dataObject.getArrayValue();
    String errorString = loadData.get(0).getStringValue();
    if (!errorString.equals("")) {
      System.err.println("Error getting powerworld load data: " + errorString);                
    }

    ArrayList<ComDataObject> lData = loadData.get(1).getArrayValue();                       
    String mvarString = lData.get(2).getStringValue();
    String mwString = lData.get(3).getStringValue();
    String statusString = lData.get(4).getStringValue();
        
    double reactiveLoad = Double.parseDouble(mvarString);
    double realLoad = Double.parseDouble(mwString);                
    boolean status = statusString.equalsIgnoreCase(PowerworldIOConstants.LOAD_CLOSED);
    
    Load load = registerLoad(legacyId);    
    initializeLoad(load, bus, realLoad, reactiveLoad);
    
    load.setStatus(status);
    load.setRealLoadMin(0.0);
    load.setReactiveLoadMin(0.0);
    load.setRealLoadMax(realLoad);
    load.setReactiveLoadMax(reactiveLoad);
    
    return load;    
  }
  
  @Override
  protected Load createEmptyLoad(Bus bus) {
    Pair<Integer,String> legacyId = new Pair<Integer,String>(bus.getAttribute(PowerworldModelConstants.POWERWORLD_LEGACY_ID_KEY, Integer.class), findUnusedId());
    Load load = registerLoad(legacyId);
    return load;
  }    

  /**
  * Register the load
  * @param legacyId
  * @param bus
  * @return
  */
  private Load registerLoad(Pair<Integer,String> legacyId) {
    Load load = getLegacy(LEGACY_TAG, legacyId);
    if (load == null) {
      load = createNewLoad();
      load.setAttribute(PowerworldModelConstants.POWERWORLD_LEGACY_ID_KEY,legacyId);
      load.addOutputKey(PowerworldModelConstants.POWERWORLD_LEGACY_ID_KEY);      
      registerLegacy(LEGACY_TAG, legacyId, load);
    }
   return load;
 }
  
  /**
   * Find an unused id number
   * @return
   */
  private String findUnusedId() {
    for (int i = 0; i < Integer.MAX_VALUE; ++i) {
      if (!doesLegacyExist(LEGACY_TAG,i+"")) {
        return i+"";
      }
    }
    throw new RuntimeException("Error: Cannot find an unused id");
  }

	
}
