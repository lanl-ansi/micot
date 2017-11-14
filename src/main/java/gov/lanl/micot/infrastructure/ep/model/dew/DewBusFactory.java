package gov.lanl.micot.infrastructure.ep.model.dew;

import gov.lanl.micot.infrastructure.ep.model.Bus;
import gov.lanl.micot.infrastructure.ep.model.BusFactory;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.LineInstallationTypeEnum;
import gov.lanl.micot.infrastructure.model.Asset;
import gov.lanl.micot.util.collection.Pair;
import gov.lanl.micot.util.geometry.PointImpl;

import java.util.HashSet;
import java.util.Map;

/**
 * Factory class for creating DEW Buses an ensuring their uniqueness
 * @author Russell Bent
 */
public class DewBusFactory extends BusFactory {

	private static final String LEGACY_TAG = "DEW";
		
	private static final double DEFAULT_VOLTAGE = 1.0;
	
	/**
	 * Constructor
	 */
	protected DewBusFactory() {
	}
		
	/**
	 * Construct a bus from a substation block
	 * @param line
	 * @return
	 * @throws DewException 
	 */
	public Bus constructBusFromSubstationData(DewLegacyId legacyid, Dew dew) throws DewException {  
    String name = dew.getComponentData(Bus.NAME_KEY, legacyid, null).toString();
    Object obj = dew.getComponentData(Bus.SYSTEM_VOLTAGE_KV_KEY, legacyid, name);
    double baseKV = obj == null ? -1 : Double.parseDouble(obj.toString());  
	  double minVoltage = DEFAULT_MIN_VOLTAGE;
    double maxVoltage = DEFAULT_MAX_VOLTAGE;
    boolean isFailed = Integer.parseInt(dew.getComponentData(Asset.IS_FAILED_KEY, legacyid, name).toString()) > 0;
    boolean status = !isFailed && Integer.parseInt(dew.getComponentData(Asset.STATUS_KEY, legacyid, name).toString()) == 1;
    double x = Double.parseDouble(dew.getComponentData(DewVariables.DEW_X_KEY, legacyid, name).toString());
    double y = Double.parseDouble(dew.getComponentData(DewVariables.DEW_Y_KEY, legacyid, name).toString());
    int dewType = Integer.parseInt(dew.getComponentData(DewVariables.DEW_COMPONENT_TYPE_KEY, legacyid, name).toString());
    
    double voltageMagnitude = DEFAULT_VOLTAGE;
    double voltageAngle = 0;
	  double desiredVoltage = DEFAULT_VOLTAGE;
	  	  
    Bus bus = registerBus(legacyid);
    bus.setAttribute(Bus.NAME_KEY,name);
    bus.setVoltagePU(voltageMagnitude);
    bus.setPhaseAngle(voltageAngle);
    bus.setSystemVoltageKV(baseKV);
    bus.setStatus(status);
    bus.setCoordinate(new PointImpl(x,y));    
    bus.setMaximumVoltagePU(maxVoltage);
    bus.setMinimumVoltagePU(minVoltage);
    bus.setAttribute(Bus.IS_FAILED_KEY, isFailed);
    bus.setAttribute(DewVariables.DEW_COMPONENT_TYPE_KEY, dewType);
    
  	return bus;
	}

	 /**
   * Construct a bus from a substation block
   * @param line
   * @return
	 * @throws DewException 
   */
    public Bus constructClusterBus(DewLegacyId legacyid, Dew dew, Map<Integer,DewPtlinespcData> lineData) throws DewException {  
    
    Object obj =  dew.getComponentData(Bus.NAME_KEY, legacyid, null);  
    String name = obj == null ? "" : obj.toString();
    obj = dew.getComponentData(Bus.SYSTEM_VOLTAGE_KV_KEY, legacyid, name);
    double baseKV = obj == null ? -1 : Double.parseDouble(obj.toString());  
    double minVoltage = DEFAULT_MIN_VOLTAGE;
    double maxVoltage = DEFAULT_MAX_VOLTAGE;
    boolean isFailed = Integer.parseInt(dew.getComponentData(Asset.IS_FAILED_KEY, legacyid, name).toString()) > 0;
    boolean status = !isFailed && Integer.parseInt(dew.getComponentData(Asset.STATUS_KEY, legacyid, name).toString()) == 1;
    double x = Double.parseDouble(dew.getComponentData(DewVariables.DEW_X_KEY, legacyid, name).toString());
    double y = Double.parseDouble(dew.getComponentData(DewVariables.DEW_Y_KEY, legacyid, name).toString());
    int dewType = Integer.parseInt(dew.getComponentData(DewVariables.DEW_COMPONENT_TYPE_KEY, legacyid, name).toString());
    
    int ptrow = Integer.parseInt(dew.getComponentData(DewVariables.DEW_DATABASE_PTROW_KEY, legacyid, name).toString());
    DewPtlinespcData data = lineData.get(ptrow);
    String lineType = ptrow == -1 ? "" : data.getStnam();
    LineInstallationTypeEnum installType = ptrow == -1 ? LineInstallationTypeEnum.UNKNOWN_TYPE : LineInstallationTypeEnum.getEnum(data.getSoverhead());
    String lineDesc = ptrow == -1 ? "" : data.getStdesc();
        
    double voltageMagnitude = DEFAULT_VOLTAGE;
    double voltageAngle = 0;
    double desiredVoltage = DEFAULT_VOLTAGE;
        
    Bus bus = registerBus(legacyid);
    bus.setAttribute(Bus.NAME_KEY,name);
    bus.setVoltagePU(voltageMagnitude);
    bus.setPhaseAngle(voltageAngle);
    bus.setSystemVoltageKV(baseKV);
    bus.setStatus(status);
    bus.setCoordinate(new PointImpl(x,y));    
    bus.setMaximumVoltagePU(maxVoltage);
    bus.setMinimumVoltagePU(minVoltage);
    bus.setAttribute(Bus.IS_FAILED_KEY, isFailed);
    bus.setAttribute(DewVariables.DEW_COMPONENT_TYPE_KEY, dewType);
    bus.setAttribute(DewVariables.DEW_LEGACY_IDS_KEY, new HashSet<Integer>()); // for storing cluster ids
    bus.setAttribute(ElectricPowerFlowConnection.LINE_DESCRIPTION_KEY, lineDesc);
    bus.setAttribute(ElectricPowerFlowConnection.LINE_TYPE_KEY, lineType);
    bus.setAttribute(ElectricPowerFlowConnection.INSTALLATION_TYPE_KEY, installType);
    
    
    return bus;
  }

  /**
  * Construct a bus from a substation block
  * @param line
  * @return
   * @throws DewException 
  */
 public Bus createVirtualBus(DewLegacyId legacyid, Dew dew, Map<Integer,DewPtlinespcData> lineData) throws DewException {  
   Object temp = dew.getComponentData(Bus.NAME_KEY, legacyid, null);
   if (temp == null) {
     temp = dew.getComponentData(DewVariables.DEW_BUS_NAME_KEY, legacyid, null);
   }
   
   String name = temp == null ? " " : temp.toString();
   Object obj = dew.getComponentData(Bus.SYSTEM_VOLTAGE_KV_KEY, legacyid, name);
   double baseKV = obj == null ? -1 : Double.parseDouble(obj.toString());  
   double minVoltage = DEFAULT_MIN_VOLTAGE;
   double maxVoltage = DEFAULT_MAX_VOLTAGE;
   boolean isFailed = Integer.parseInt(dew.getComponentData(Asset.IS_FAILED_KEY, legacyid, name).toString()) > 0;
   boolean status = !isFailed && Integer.parseInt(dew.getComponentData(Asset.STATUS_KEY, legacyid, name).toString()) == 1;
   double x = Double.parseDouble(dew.getComponentData(DewVariables.DEW_X_KEY, legacyid, name).toString());
   double y = Double.parseDouble(dew.getComponentData(DewVariables.DEW_Y_KEY, legacyid, name).toString());
   int dewType = DewVariables.NO_DEW_TYPE;   
   double voltageMagnitude = DEFAULT_VOLTAGE;
   double voltageAngle = 0;
   double desiredVoltage = DEFAULT_VOLTAGE;
       
   Bus bus = registerBus(legacyid);
   bus.setAttribute(Bus.NAME_KEY,name);
   bus.setVoltagePU(voltageMagnitude);
   bus.setPhaseAngle(voltageAngle);
   bus.setSystemVoltageKV(baseKV);
   bus.setStatus(status);
   bus.setCoordinate(new PointImpl(x,y));    
   bus.setMaximumVoltagePU(maxVoltage);
   bus.setMinimumVoltagePU(minVoltage);
   bus.setAttribute(Bus.IS_FAILED_KEY, isFailed);
   bus.setAttribute(DewVariables.DEW_COMPONENT_TYPE_KEY, dewType);
   
   int ptrow = Integer.parseInt(dew.getComponentData(DewVariables.DEW_DATABASE_PTROW_KEY, legacyid, name).toString());
   DewPtlinespcData data = lineData.get(ptrow);
   String lineType = data == null ? "" : data.getStnam();
   LineInstallationTypeEnum installType = data == null ? LineInstallationTypeEnum.UNKNOWN_TYPE :  LineInstallationTypeEnum.getEnum(data.getSoverhead());
   String lineDesc = data == null ? "" : data.getStdesc();
   bus.setAttribute(ElectricPowerFlowConnection.LINE_DESCRIPTION_KEY, lineDesc);
   bus.setAttribute(ElectricPowerFlowConnection.LINE_TYPE_KEY, lineType);
   bus.setAttribute(ElectricPowerFlowConnection.INSTALLATION_TYPE_KEY, installType);
   
   return bus;
 }

  /**
   * Creates a bus from another bus
   * @param bus
   * @return
   */
  public void updateBus(Bus bus, int id) {
    if (bus.getAttribute(DewVariables.DEW_LEGACY_ID_KEY) == null) {
      bus.setAttribute(DewVariables.DEW_LEGACY_ID_KEY, id);
      registerLegacy(LEGACY_TAG,id,bus);
    }        
  } 

  @Override
  protected Bus createEmptyBus() {
    DewLegacyId id = findUnusedId();
    Bus bus = registerBus(id);
    return bus;
  }
  
  /**
   * Find an unused id number
   * @return
   */
  public DewLegacyId findUnusedId() {
    int feeder = 0;
    for (int i = 0; i < Integer.MAX_VALUE; ++i) {
      DewLegacyId temp = new DewLegacyId(feeder, i);      
      if (!doesLegacyExist(LEGACY_TAG,temp)) {
        return temp;
      }
    }
    throw new RuntimeException("Error: Ran out of MatPower ids");    
  }

  /**
   * Register the bus
   * @param legacyId
   * @param bus
   * @return
   */
  private Bus registerBus(Pair<Integer,Integer> legacyId) {
    Bus bus = getLegacy(LEGACY_TAG,legacyId);
    if (bus == null) {
      bus = createNewBus();
      bus.setAttribute(DewVariables.DEW_LEGACY_ID_KEY,legacyId);
      bus.addOutputKey(DewVariables.DEW_LEGACY_ID_KEY);      
      registerLegacy(LEGACY_TAG,legacyId,bus);
    }
    return bus;
  }
  
}
