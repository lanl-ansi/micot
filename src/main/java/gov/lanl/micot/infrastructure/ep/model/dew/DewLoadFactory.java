package gov.lanl.micot.infrastructure.ep.model.dew;

import java.util.Map;

import gov.lanl.micot.infrastructure.ep.model.Bus;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.Generator;
import gov.lanl.micot.infrastructure.ep.model.LineInstallationTypeEnum;
import gov.lanl.micot.infrastructure.ep.model.Load;
import gov.lanl.micot.infrastructure.ep.model.LoadFactory;
import gov.lanl.micot.infrastructure.model.Asset;
import gov.lanl.micot.util.geometry.PointImpl;


/**
 * Factory class for creating MatPowerLoads an ensuring their uniqueness
 * @author Russell Bent
 */
public class DewLoadFactory extends LoadFactory {

//	private static DewLoadFactory instance = null;
	private static final String LEGACY_TAG = "DEW";
	
	//public static DewLoadFactory getInstance() {
		//if (instance == null) {
			//instance = new DewLoadFactory();
	//	}
		//return instance;
	//}
	
	/**
	 * Constructor
	 */
	protected DewLoadFactory() {
	}
	
  @Override
  protected Load createEmptyLoad(Bus bus) {
    DewLegacyId legacyId = bus.getAttribute(DewVariables.DEW_LEGACY_ID_KEY, DewLegacyId.class);
    Load load = registerLoad(legacyId);    
    return load;        
  }    

  /**
   * Register the load
   * @param legacyId
   * @param bus
   * @return
   */
  private Load registerLoad(DewLegacyId legacyId) {
    Load load = getLegacy(LEGACY_TAG, legacyId);
    if (load == null) {
      load = createNewLoad();
      load.setAttribute(DewVariables.DEW_LEGACY_ID_KEY,legacyId);
      load.addOutputKey(DewVariables.DEW_LEGACY_ID_KEY);      
      registerLegacy(LEGACY_TAG, legacyId, load);
    }
    return load;
  }

  /**
   * Construct a load from a set of data
   * @param data
   * @return
   * @throws DewException 
   * @throws NumberFormatException 
   */
  public Load constructLoad(DewLegacyId legacyid, Dew dew, Map<Integer,DewPtlinespcData> lineData) throws NumberFormatException, DewException {
    Object obj = dew.getComponentData(DewVariables.DEW_LOAD_NAME_KEY, legacyid, null);
    String name = obj == null ? " " : obj.toString();
    boolean isFailed = Integer.parseInt(dew.getComponentData(Asset.IS_FAILED_KEY, legacyid, name).toString()) > 0;
    boolean status = !isFailed && Integer.parseInt(dew.getComponentData(Asset.DESIRED_STATUS_KEY, legacyid, name).toString()) == 1;
    double x = Double.parseDouble(dew.getComponentData(DewVariables.DEW_X_KEY, legacyid, name).toString());
    double y = Double.parseDouble(dew.getComponentData(DewVariables.DEW_Y_KEY, legacyid, name).toString());
    int dewType = Integer.parseInt(dew.getComponentData(DewVariables.DEW_COMPONENT_TYPE_KEY, legacyid, name).toString());
    int numPhases = Integer.parseInt(dew.getComponentData(Generator.NUM_PHASE_KEY, legacyid, name).toString());
    int phases = Integer.parseInt(dew.getComponentData(DewVariables.DEW_PHASES_KEY, legacyid, name).toString());
    boolean hasPhaseA = false;
    boolean hasPhaseB = false;
    boolean hasPhaseC = false;
    
    if (phases == 1 || phases == 3 || phases == 5 || phases == 7) {
      hasPhaseA = true;
    }
    if (phases == 2 || phases == 3 || phases == 6 || phases == 7) {
      hasPhaseB = true;
    }
    if (phases == 4 || phases == 5 || phases == 6 || phases == 7) {
      hasPhaseC = true;
    }
    
    Object aTargetkW = dew.getComponentData(Load.TARGET_REAL_LOAD_A_KEY, legacyid, name);
    Object aTargetkVar = dew.getComponentData(Load.TARGET_REACTIVE_LOAD_A_KEY, legacyid, name);
    Object bTargetkW = dew.getComponentData(Load.TARGET_REAL_LOAD_B_KEY, legacyid, name);
    Object bTargetkVar = dew.getComponentData(Load.TARGET_REACTIVE_LOAD_B_KEY, legacyid, name);
    Object cTargetkW = dew.getComponentData(Load.TARGET_REAL_LOAD_C_KEY, legacyid, name);
    Object cTargetkVar = dew.getComponentData(Load.TARGET_REACTIVE_LOAD_C_KEY, legacyid, name);

    if (aTargetkW == null) {
    	aTargetkW = 0.0;
    }
    if (aTargetkVar == null) {
    	aTargetkVar = 0.0;
    }
    if (bTargetkW == null) {
    	bTargetkW = 0.0;
    }
    if (bTargetkVar == null) {
    	bTargetkVar = 0.0;
    }
    if (cTargetkW == null) {
    	cTargetkW = 0.0;
    }
    if (cTargetkVar == null) {
    	cTargetkVar = 0.0;
    }
    
    // repeat for target loads    
    double phaseAMW = Double.parseDouble(aTargetkW.toString()) / 1000.0;
    double phaseAMVAR = Double.parseDouble(aTargetkVar.toString()) / 1000.0;
    double phaseBMW = Double.parseDouble(bTargetkW.toString()) / 1000.0;
    double phaseBMVAR = Double.parseDouble(bTargetkVar.toString()) / 1000.0;
    double phaseCMW = Double.parseDouble(cTargetkW.toString()) / 1000.0;
    double phaseCMVAR = Double.parseDouble(cTargetkVar.toString()) / 1000.0;   

    double realLoad = phaseAMW + phaseBMW + phaseCMW;
    double reactiveLoad = phaseAMVAR + phaseBMVAR + phaseCMVAR;
    
    // check to see if the area already exists
    Load load = registerLoad(legacyid);    
    load.setAttribute(Load.LOAD_NAME_KEY, name);
    load.setDesiredRealLoad(realLoad);
    load.setDesiredReactiveLoad(reactiveLoad);
    load.setDesiredStatus(status);
    load.setActualStatus(status);
    load.setActualRealLoad(realLoad);
    load.setActualReactiveLoad(reactiveLoad);
    load.setCoordinate(new PointImpl(x,y));
    load.setAttribute(Load.IS_FAILED_KEY, isFailed);
    load.setAttribute(DewVariables.DEW_COMPONENT_TYPE_KEY, dewType);
    load.setAttribute(Load.NUM_PHASE_KEY, numPhases);
    load.setAttribute(Load.HAS_PHASE_A_KEY, hasPhaseA);
    load.setAttribute(Load.HAS_PHASE_B_KEY, hasPhaseB);
    load.setAttribute(Load.HAS_PHASE_C_KEY, hasPhaseC);
    
    load.setAttribute(Load.DESIRED_REAL_LOAD_A_KEY, phaseAMW);
    load.setAttribute(Load.ACTUAL_REAL_LOAD_A_KEY, phaseAMW);
    
    load.setAttribute(Load.DESIRED_REACTIVE_LOAD_A_KEY, phaseAMVAR);
    load.setAttribute(Load.ACTUAL_REACTIVE_LOAD_A_KEY, phaseAMVAR);

    
    load.setAttribute(Load.DESIRED_REAL_LOAD_B_KEY, phaseBMW);
    load.setAttribute(Load.ACTUAL_REAL_LOAD_B_KEY, phaseBMW);
    
    load.setAttribute(Load.DESIRED_REACTIVE_LOAD_B_KEY, phaseBMVAR);
    load.setAttribute(Load.ACTUAL_REACTIVE_LOAD_B_KEY, phaseBMVAR);

    load.setAttribute(Load.DESIRED_REAL_LOAD_C_KEY, phaseCMW);
    load.setAttribute(Load.ACTUAL_REAL_LOAD_C_KEY, phaseCMW);
    
    load.setAttribute(Load.DESIRED_REACTIVE_LOAD_C_KEY, phaseCMVAR);
    load.setAttribute(Load.ACTUAL_REACTIVE_LOAD_C_KEY, phaseCMVAR);
    
    int ptrow = Integer.parseInt(dew.getComponentData(DewVariables.DEW_DATABASE_PTROW_KEY, legacyid, name).toString());
    DewPtlinespcData data = lineData.get(ptrow);
    String lineType = data == null ? "" : data.getStnam();
    LineInstallationTypeEnum installType = data == null ? LineInstallationTypeEnum.UNKNOWN_TYPE : LineInstallationTypeEnum.getEnum(data.getSoverhead());
    String lineDesc = data == null ? "" : data.getStdesc();
    load.setAttribute(ElectricPowerFlowConnection.LINE_DESCRIPTION_KEY, lineDesc);
    load.setAttribute(ElectricPowerFlowConnection.LINE_TYPE_KEY, lineType);
    load.setAttribute(ElectricPowerFlowConnection.INSTALLATION_TYPE_KEY, installType);

    
    return load;
  }


  /**
   * Construct a load from a set of data
   * @param data
   * @return
   * @throws DewException 
   * @throws NumberFormatException 
   */
  public Load constructEstimatedLoad(DewLegacyId legacyid, Dew dew, Map<Integer,DewPtlinespcData> lineData) throws NumberFormatException, DewException {
    Object obj = dew.getComponentData(Bus.NAME_KEY, legacyid, null);
    String name = obj == null ? " " : obj.toString();
    boolean isFailed = Integer.parseInt(dew.getComponentData(Asset.IS_FAILED_KEY, legacyid, name).toString()) > 0;
    boolean status = !isFailed && Integer.parseInt(dew.getComponentData(Asset.DESIRED_STATUS_KEY, legacyid, name).toString()) == 1;
    double x = Double.parseDouble(dew.getComponentData(DewVariables.DEW_X_KEY, legacyid, name).toString());
    double y = Double.parseDouble(dew.getComponentData(DewVariables.DEW_Y_KEY, legacyid, name).toString());
    int dewType = Integer.parseInt(dew.getComponentData(DewVariables.DEW_COMPONENT_TYPE_KEY, legacyid, name).toString());
    int numPhases = Integer.parseInt(dew.getComponentData(Generator.NUM_PHASE_KEY, legacyid, name).toString());
    int phases = Integer.parseInt(dew.getComponentData(DewVariables.DEW_PHASES_KEY, legacyid, name).toString());
    boolean hasPhaseA = false;
    boolean hasPhaseB = false;
    boolean hasPhaseC = false;
    
    if (phases == 1 || phases == 3 || phases == 5 || phases == 7) {
      hasPhaseA = true;
    }
    if (phases == 2 || phases == 3 || phases == 6 || phases == 7) {
      hasPhaseB = true;
    }
    if (phases == 4 || phases == 5 || phases == 6 || phases == 7) {
      hasPhaseC = true;
    }
    
    double phaseAMW = Double.parseDouble(dew.getComponentData(DewVariables.DEW_DESIRED_REAL_LOAD_A_KEY, legacyid, name).toString());
    double phaseAMVAR = Double.parseDouble(dew.getComponentData(DewVariables.DEW_DESIRED_REACTIVE_LOAD_A_KEY, legacyid, name).toString());
    double phaseBMW = Double.parseDouble(dew.getComponentData(DewVariables.DEW_DESIRED_REAL_LOAD_B_KEY, legacyid, name).toString());
    double phaseBMVAR = Double.parseDouble(dew.getComponentData(DewVariables.DEW_DESIRED_REACTIVE_LOAD_B_KEY, legacyid, name).toString());
    double phaseCMW = Double.parseDouble(dew.getComponentData(DewVariables.DEW_DESIRED_REAL_LOAD_C_KEY, legacyid, name).toString());
    double phaseCMVAR = Double.parseDouble(dew.getComponentData(DewVariables.DEW_DESIRED_REACTIVE_LOAD_C_KEY, legacyid, name).toString());

    double realLoad = phaseAMW + phaseBMW + phaseCMW;
    double reactiveLoad = phaseAMVAR + phaseBMVAR + phaseCMVAR;
    
    // check to see if the area already exists
    Load load = registerLoad(legacyid);    
    load.setAttribute(Load.LOAD_NAME_KEY, name);
    load.setDesiredRealLoad(realLoad);
    load.setDesiredReactiveLoad(reactiveLoad);
    load.setDesiredStatus(status);
    load.setActualStatus(status);
    load.setActualRealLoad(realLoad);
    load.setActualReactiveLoad(reactiveLoad);
    load.setCoordinate(new PointImpl(x,y));
    load.setAttribute(Load.IS_FAILED_KEY, isFailed);
    load.setAttribute(DewVariables.DEW_COMPONENT_TYPE_KEY, dewType);
    load.setAttribute(Load.NUM_PHASE_KEY, numPhases);
    load.setAttribute(Load.HAS_PHASE_A_KEY, hasPhaseA);
    load.setAttribute(Load.HAS_PHASE_B_KEY, hasPhaseB);
    load.setAttribute(Load.HAS_PHASE_C_KEY, hasPhaseC);
    load.setAttribute(Load.DESIRED_REAL_LOAD_A_KEY, phaseAMW);
    load.setAttribute(Load.ACTUAL_REAL_LOAD_A_KEY, phaseAMW);
    load.setAttribute(Load.DESIRED_REACTIVE_LOAD_A_KEY, phaseAMVAR);
    load.setAttribute(Load.ACTUAL_REACTIVE_LOAD_A_KEY, phaseAMVAR);
    load.setAttribute(Load.DESIRED_REAL_LOAD_B_KEY, phaseBMW);
    load.setAttribute(Load.ACTUAL_REAL_LOAD_B_KEY, phaseBMW);
    load.setAttribute(Load.DESIRED_REACTIVE_LOAD_B_KEY, phaseBMVAR);
    load.setAttribute(Load.ACTUAL_REACTIVE_LOAD_B_KEY, phaseBMVAR);
    load.setAttribute(Load.DESIRED_REAL_LOAD_C_KEY, phaseCMW);
    load.setAttribute(Load.ACTUAL_REAL_LOAD_C_KEY, phaseCMW);
    load.setAttribute(Load.DESIRED_REACTIVE_LOAD_C_KEY, phaseCMVAR);
    load.setAttribute(Load.ACTUAL_REACTIVE_LOAD_C_KEY, phaseCMVAR);
    
    int ptrow = Integer.parseInt(dew.getComponentData(DewVariables.DEW_DATABASE_PTROW_KEY, legacyid, name).toString());
    DewPtlinespcData data = lineData.get(ptrow);
    String lineType = data.getStnam();
    LineInstallationTypeEnum installType = LineInstallationTypeEnum.getEnum(data.getSoverhead());
    String lineDesc = data.getStdesc();
    load.setAttribute(ElectricPowerFlowConnection.LINE_DESCRIPTION_KEY, lineDesc);
    load.setAttribute(ElectricPowerFlowConnection.LINE_TYPE_KEY, lineType);
    load.setAttribute(ElectricPowerFlowConnection.INSTALLATION_TYPE_KEY, installType);

    
    return load;
  }

  
}
