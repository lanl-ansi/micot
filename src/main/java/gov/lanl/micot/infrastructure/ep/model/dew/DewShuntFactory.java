package gov.lanl.micot.infrastructure.ep.model.dew;

import java.util.Map;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.LineInstallationTypeEnum;
import gov.lanl.micot.infrastructure.ep.model.ShuntCapacitor;
import gov.lanl.micot.infrastructure.ep.model.ShuntCapacitorFactory;
import gov.lanl.micot.infrastructure.model.Asset;
import gov.lanl.micot.util.geometry.PointImpl;


/**
 * Factory class for creating DEWShunts and ensuring their uniqueness
 * @author Russell Bent
 */
public class DewShuntFactory extends ShuntCapacitorFactory {

//	private static DewShuntFactory instance = null;
	private static final String LEGACY_TAG = "DEW";
	
	//public static DewShuntFactory getInstance() {
		//if (instance == null) {
			//instance = new DewShuntFactory();
		//}
		//return instance;
	//}
	
	/**
	 * Constructor
	 */
	protected DewShuntFactory() {
	}	


	 /**
   * Construction of a shunt
   * @param line
   * @return
	 * @throws DewException 
	 * @throws NumberFormatException 
   */
  public ShuntCapacitor constructCapacitor(DewLegacyId legacyid, Dew dew, Map<Integer,DewPtlinespcData> lineData) throws NumberFormatException, DewException {
    Object obj =  dew.getComponentData(DewVariables.DEW_SHUNT_NAME_KEY, legacyid, null);  
    String name = obj == null ? "" : obj.toString();
    boolean isFailed = Integer.parseInt(dew.getComponentData(Asset.IS_FAILED_KEY, legacyid, name).toString()) > 0;
    boolean status = !isFailed && Integer.parseInt(dew.getComponentData(Asset.DESIRED_STATUS_KEY, legacyid, name).toString()) == 1;
    double x = Double.parseDouble(dew.getComponentData(DewVariables.DEW_X_KEY, legacyid, name).toString());
    double y = Double.parseDouble(dew.getComponentData(DewVariables.DEW_Y_KEY, legacyid, name).toString());
    int dewType = Integer.parseInt(dew.getComponentData(DewVariables.DEW_COMPONENT_TYPE_KEY, legacyid, name).toString());
    double realCompensation = 0;
    double reactiveCompensation = 0;
        
    ShuntCapacitor shunt = registerCapacitor(legacyid);
    shunt.setAttribute(ShuntCapacitor.SHUNT_NAME_KEY, name);
    shunt.setCoordinate(new PointImpl(x,y));
    shunt.setDesiredStatus(status);
    shunt.setActualStatus(status);
    shunt.setAttribute(ShuntCapacitor.IS_FAILED_KEY, isFailed);
    shunt.setAttribute(DewVariables.DEW_COMPONENT_TYPE_KEY, dewType);
    shunt.setRealCompensation(realCompensation);
    shunt.setReactiveCompensation(reactiveCompensation);

    
    int ptrow = Integer.parseInt(dew.getComponentData(DewVariables.DEW_DATABASE_PTROW_KEY, legacyid, name).toString());
    DewPtlinespcData data = lineData.get(ptrow);
    String lineType = data.getStnam();
    LineInstallationTypeEnum installType = LineInstallationTypeEnum.getEnum(data.getSoverhead());
    String lineDesc = data.getStdesc();
    shunt.setAttribute(ElectricPowerFlowConnection.LINE_DESCRIPTION_KEY, lineDesc);
    shunt.setAttribute(ElectricPowerFlowConnection.LINE_TYPE_KEY, lineType);
    shunt.setAttribute(ElectricPowerFlowConnection.INSTALLATION_TYPE_KEY, installType);
    
    return shunt;
  }

	
  /**
   * Register the shunt capacitor
   * @param legacyId
   * @param bus
   * @return
   */
  private ShuntCapacitor registerCapacitor(DewLegacyId legacyId) {
    ShuntCapacitor capacitor = getLegacy(LEGACY_TAG, legacyId);
    if (capacitor == null) {
      capacitor = createNewShuntCapacitor();
      capacitor.setAttribute(DewVariables.DEW_LEGACY_ID_KEY,legacyId);
      capacitor.addOutputKey(DewVariables.DEW_LEGACY_ID_KEY);
      registerLegacy(LEGACY_TAG, legacyId, capacitor);
    }
    return capacitor;
  }

  
}
