package gov.lanl.micot.infrastructure.ep.model.dew;

import gov.lanl.micot.infrastructure.ep.model.Bus;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.Generator;
import gov.lanl.micot.infrastructure.ep.model.LineInstallationTypeEnum;
import gov.lanl.micot.infrastructure.ep.model.Transformer;
import gov.lanl.micot.infrastructure.ep.model.TransformerControlModeEnum;
import gov.lanl.micot.infrastructure.ep.model.TransformerFactory;
import gov.lanl.micot.infrastructure.ep.model.TransformerTypeEnum;
import gov.lanl.micot.infrastructure.model.Asset;
import gov.lanl.micot.util.geometry.LineImpl;
import gov.lanl.micot.util.geometry.Point;

import java.util.ArrayList;
import java.util.Map;

/**
 * Factory class for creating MatPower Transformers and ensuring their uniqueness
 * @author Russell Bent
 */
public class DewTransformerFactory extends TransformerFactory {

  protected static final double DEFAULT_TAP_ANGLE = 0;
  protected static final double DEFAULT_TAP_RATIO = 1.0;
  
  
  private static final double DEFAULT_RESISTANCE_A = .001;
  private static final double DEFAULT_RESISTANCE_B = .001;
  private static final double DEFAULT_RESISTANCE_C = .001;
  private static final double DEFAULT_REACTANCE_A = .001;
  private static final double DEFAULT_REACTANCE_B = .001;
  private static final double DEFAULT_REACTANCE_C = .001;
  private static final double DEFAULT_CHARGING = .001;
  private static final double DEFAULT_RATING_A = 1000000;
  private static final double DEFAULT_RATING_B = 1000000;
  private static final double DEFAULT_RATING_C = 1000000;
    
	//private static DewTransformerFactory instance = null;
	private static final String LEGACY_TAG = "DEW";
	
	/**
	 * Get the instance of the transformer factory
	 * @return
	 */
	//public static DewTransformerFactory getInstance() {
		//if (instance == null) {
			//instance = new DewTransformerFactory();
		//}
		//return instance;
	//}

	/**
	 * Constructor
	 */
	protected DewTransformerFactory() {
	}
		
	@Override
	protected Transformer createEmptyTransformer(Bus bus1, Bus bus2, Object circuit) {
	  Transformer transformer = registerTransformer(findUnusedId());   
	  return transformer;
	}
	
  /**
   * Register the line
   * 
   * @param legacyId
   * @param bus
   * @return
   */
  private Transformer registerTransformer(DewLegacyId legacyId) {
    Transformer transformer = getLegacy(LEGACY_TAG, legacyId);
    if (transformer == null) {
      transformer = createNewTransformer();
      transformer.addOutputKey(DewVariables.DEW_LEGACY_ID_KEY);
      transformer.setAttribute(DewVariables.DEW_LEGACY_ID_KEY, legacyId);
      registerLegacy(LEGACY_TAG, legacyId, transformer);
    }
    return transformer;
  }
  
  /**
   * Find an unused id number
   * @return
   */
  public DewLegacyId findUnusedId() {
    for (int i = 0; i < Integer.MAX_VALUE; ++i) {
      DewLegacyId temp = new DewLegacyId(0,i);      
      if (!doesLegacyExist(LEGACY_TAG,temp)) {
        return temp;
      }
    }
    throw new RuntimeException("Error: Ran out of DEW ids");    
  }

  /** 
   * Create a transformer from data
   * @param data
   * @return
   * @throws DewException 
   * @throws NumberFormatException 
   */
  @SuppressWarnings("unchecked")
  public Transformer createTransformer(DewLegacyId legacyid, Dew dew, Map<Integer,DewPtlinespcData> lineData) throws NumberFormatException, DewException {
    Object obj = dew.getComponentData(Bus.NAME_KEY, legacyid, null);
    String name = obj == null ? "" : obj.toString();
    boolean isFailed = Integer.parseInt(dew.getComponentData(Asset.IS_FAILED_KEY, legacyid, name).toString()) > 0;
    boolean status = !isFailed && Integer.parseInt(dew.getComponentData(Asset.DESIRED_STATUS_KEY, legacyid, name).toString()) == 1;
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
    
    ArrayList<Point> points = (ArrayList<Point>) dew.getComponentData(Transformer.COORDINATE_KEY, legacyid, name);

    Object test = dew.getComponentData(ElectricPowerFlowConnection.RESISTANCE_PHASE_A_KEY, legacyid, name);
    double resistanceA = DEFAULT_RESISTANCE_A;
    double resistanceB = DEFAULT_RESISTANCE_B;
    double resistanceC = DEFAULT_RESISTANCE_C;
    double reactanceA = DEFAULT_REACTANCE_A;
    double reactanceB = DEFAULT_REACTANCE_B;
    double reactanceC = DEFAULT_REACTANCE_C;
    double charging = DEFAULT_CHARGING;
    double ratingA = DEFAULT_RATING_A;
    double ratingB = DEFAULT_RATING_B;
    double ratingC= DEFAULT_RATING_C;
    
    if (test != null) {
      resistanceA = Double.parseDouble(dew.getComponentData(ElectricPowerFlowConnection.RESISTANCE_PHASE_A_KEY, legacyid, name).toString());
      resistanceB = Double.parseDouble(dew.getComponentData(ElectricPowerFlowConnection.RESISTANCE_PHASE_B_KEY, legacyid, name).toString());
      resistanceC = Double.parseDouble(dew.getComponentData(ElectricPowerFlowConnection.RESISTANCE_PHASE_C_KEY, legacyid, name).toString());
      reactanceA = Double.parseDouble(dew.getComponentData(ElectricPowerFlowConnection.REACTANCE_PHASE_A_KEY, legacyid, name).toString());
      reactanceB = Double.parseDouble(dew.getComponentData(ElectricPowerFlowConnection.REACTANCE_PHASE_B_KEY, legacyid, name).toString());
      reactanceC = Double.parseDouble(dew.getComponentData(ElectricPowerFlowConnection.REACTANCE_PHASE_C_KEY, legacyid, name).toString());
      charging = dew.getComponentData(ElectricPowerFlowConnection.LINE_CHARGING_KEY, legacyid, name) == null ? 0 : Double.parseDouble(dew.getComponentData(ElectricPowerFlowConnection.LINE_CHARGING_KEY, legacyid, name).toString());
      ratingA = Double.parseDouble(dew.getComponentData(ElectricPowerFlowConnection.CAPACITY_RATING_A_KEY, legacyid, name).toString());
      ratingB = Double.parseDouble(dew.getComponentData(ElectricPowerFlowConnection.CAPACITY_RATING_B_KEY, legacyid, name).toString());
      ratingC = Double.parseDouble(dew.getComponentData(ElectricPowerFlowConnection.CAPACITY_RATING_C_KEY, legacyid, name).toString());    
    }
    
    double resistance = Math.max(0.00001,resistanceA + resistanceB + resistanceC);
    double reactance = Math.max(0.00001, reactanceA + reactanceB + reactanceC);
      
    Double tapRatio = null;
    Double tapAngle = null;
    Double minTapAngle = null;
    Double maxTapAngle = null;
    Double stepSize = null;
    Double controlMin = null;
    Double controlMax = null;
    
    TransformerControlModeEnum gControlSide = null;
    TransformerTypeEnum transformerType = TransformerTypeEnum.TRANSMISSION_FIXED_ANGLE_FIXED_TAP_TYPE;

    Transformer transformer = registerTransformer(legacyid);
    transformer.setResistance(resistance);
    transformer.setReactance(reactance);
    
    transformer.setAttribute(ElectricPowerFlowConnection.RESISTANCE_PHASE_A_KEY, resistanceA);
    transformer.setAttribute(ElectricPowerFlowConnection.RESISTANCE_PHASE_B_KEY, resistanceB);
    transformer.setAttribute(ElectricPowerFlowConnection.RESISTANCE_PHASE_C_KEY, resistanceC);
    transformer.setAttribute(ElectricPowerFlowConnection.REACTANCE_PHASE_A_KEY, reactanceA);
    transformer.setAttribute(ElectricPowerFlowConnection.REACTANCE_PHASE_B_KEY, reactanceB);
    transformer.setAttribute(ElectricPowerFlowConnection.REACTANCE_PHASE_C_KEY, reactanceC);
    transformer.setLineCharging(charging);
    transformer.setCapacityRating(ratingA + ratingB + ratingC);
    transformer.setLongTermEmergencyCapacityRating(ratingA + ratingB + ratingC);
    transformer.setShortTermEmergencyCapacityRating(ratingA + ratingB + ratingC);
    transformer.setAttribute(Transformer.CAPACITY_RATING_A_KEY, ratingA);
    transformer.setAttribute(Transformer.CAPACITY_RATING_B_KEY, ratingB);
    transformer.setAttribute(Transformer.CAPACITY_RATING_C_KEY, ratingC);
    transformer.setDesiredStatus(status);
    transformer.setAttribute(Transformer.TYPE_KEY, transformerType);
    transformer.setAttribute(Transformer.TAP_RATIO_KEY, tapRatio);
    transformer.setAttribute(Transformer.TAP_ANGLE_KEY, tapAngle);
    transformer.setAttribute(Transformer.MIN_TAP_ANGLE_KEY, minTapAngle);
    transformer.setAttribute(Transformer.MAX_TAP_ANGLE_KEY, maxTapAngle);
    transformer.setAttribute(Transformer.STEP_SIZE_KEY, stepSize);
    transformer.setAttribute(Transformer.CONTROL_MIN_KEY, controlMin);
    transformer.setAttribute(Transformer.CONTROL_MAX_KEY, controlMax);
    transformer.setAttribute(Transformer.CONTROL_SIDE_KEY, gControlSide);   
    transformer.setActualStatus(transformer.getDesiredStatus());
    transformer.setMWFlow(0.0);
    transformer.setMVarFlow(0.0);
    transformer.setCoordinates(new LineImpl(points.toArray(new Point[0])));
    transformer.setAttribute(Transformer.IS_FAILED_KEY, isFailed);
    transformer.setAttribute(DewVariables.DEW_COMPONENT_TYPE_KEY, dewType);
    transformer.setAttribute(Transformer.NUMBER_OF_PHASES_KEY, numPhases);
    transformer.setAttribute(Transformer.IS_PHASE_A_KEY, hasPhaseA);
    transformer.setAttribute(Transformer.IS_PHASE_B_KEY, hasPhaseB);
    transformer.setAttribute(Transformer.IS_PHASE_C_KEY, hasPhaseC);    
    transformer.setAttribute(ElectricPowerFlowConnection.NAME_KEY, name);
    
    int ptrow = Integer.parseInt(dew.getComponentData(DewVariables.DEW_DATABASE_PTROW_KEY, legacyid, name).toString());
    DewPtlinespcData data = lineData.get(ptrow);
    String lineType = data == null ? "" : data.getStnam();
    LineInstallationTypeEnum installType = data ==null ? LineInstallationTypeEnum.UNDERGROUND_CONDUIT_TYPE : LineInstallationTypeEnum.getEnum(data.getSoverhead());
    String lineDesc = data == null ? "" : data.getStdesc();
    transformer.setAttribute(ElectricPowerFlowConnection.LINE_DESCRIPTION_KEY, lineDesc);
    transformer.setAttribute(ElectricPowerFlowConnection.LINE_TYPE_KEY, lineType);
    transformer.setAttribute(ElectricPowerFlowConnection.INSTALLATION_TYPE_KEY, installType);

    
    return transformer;        
  }

}
