package gov.lanl.micot.infrastructure.ep.model.dew;

import gov.lanl.micot.infrastructure.ep.model.Bus;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.Generator;
import gov.lanl.micot.infrastructure.ep.model.Line;
import gov.lanl.micot.infrastructure.ep.model.LineFactory;
import gov.lanl.micot.infrastructure.ep.model.LineInstallationTypeEnum;
import gov.lanl.micot.infrastructure.ep.model.Transformer;
import gov.lanl.micot.infrastructure.model.Asset;
import gov.lanl.micot.util.collection.Pair;
import gov.lanl.micot.util.geometry.LineImpl;
import gov.lanl.micot.util.geometry.Point;

import java.util.ArrayList;
import java.util.Map;

/**
 * Factory class for creating MatPowerLines and ensuring their uniqueness
 * @author Russell Bent
 */
public class DewLineFactory extends LineFactory {

  private static final String LEGACY_TAG = "DEW";
    	
	/**
	 * Constructor
	 */
	protected DewLineFactory() {
	}
	
  @Override
  protected Line createEmptyLine(Bus fromBus, Bus toBus, Transformer transformer) {
    Line line = registerLine(findUnusedId());   
    return line;
  }
	
	@Override
	protected Line createEmptyLine(Bus fromBus, Bus toBus, Object circuit) {
	  Line line = registerLine(findUnusedId());   
	  return line;
	}

  /**
   * Register the line
   * 
   * @param legacyId
   * @param bus
   * @return
   */
  private Line registerLine(Pair<Integer,Integer> legacyId) {    
    Line line = getLegacy(LEGACY_TAG, legacyId);
    if (line == null) {
      line = createNewLine();
      line.addOutputKey(DewVariables.DEW_LEGACY_ID_KEY);
      line.setAttribute(DewVariables.DEW_LEGACY_ID_KEY, legacyId);
      registerLegacy(LEGACY_TAG, legacyId, line);
    }
    return line;
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
  public Line createLine(DewLegacyId legacyid, Dew dew, Map<Integer,DewPtlinespcData> lineData, Map<Integer,DewPtlinecondData> lineCondData, Map<Integer,DewPtcabcondData> cabCondData, int linetype) throws NumberFormatException, DewException {
    Object obj = dew.getComponentData(Bus.NAME_KEY, legacyid, null);
    String name = obj == null ? "" : obj.toString();
    boolean isFailed = Integer.parseInt(dew.getComponentData(Asset.IS_FAILED_KEY, legacyid, name).toString()) > 0;
    boolean status = !isFailed && Integer.parseInt(dew.getComponentData(Asset.STATUS_KEY, legacyid, name).toString()) == 1;
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
    
    ArrayList<Point> points = (ArrayList<Point>) dew.getComponentData(Line.COORDINATE_KEY, legacyid, name);
        
    Object rA = dew.getComponentData(ElectricPowerFlowConnection.RESISTANCE_PHASE_A_KEY, legacyid, name);
    Object rB = dew.getComponentData(ElectricPowerFlowConnection.RESISTANCE_PHASE_B_KEY, legacyid, name);
    Object rC = dew.getComponentData(ElectricPowerFlowConnection.RESISTANCE_PHASE_C_KEY, legacyid, name);
    Object rAB = dew.getComponentData(ElectricPowerFlowConnection.RESISTANCE_PHASE_AB_KEY, legacyid, name);
    Object rBC = dew.getComponentData(ElectricPowerFlowConnection.RESISTANCE_PHASE_BC_KEY, legacyid, name);
    Object rCA = dew.getComponentData(ElectricPowerFlowConnection.RESISTANCE_PHASE_CA_KEY, legacyid, name);  
    
    Object xA = dew.getComponentData(ElectricPowerFlowConnection.REACTANCE_PHASE_A_KEY, legacyid, name);
    Object xB = dew.getComponentData(ElectricPowerFlowConnection.REACTANCE_PHASE_B_KEY, legacyid, name);
    Object xC = dew.getComponentData(ElectricPowerFlowConnection.REACTANCE_PHASE_C_KEY, legacyid, name);
    Object xAB = dew.getComponentData(ElectricPowerFlowConnection.REACTANCE_PHASE_AB_KEY, legacyid, name);
    Object xBC = dew.getComponentData(ElectricPowerFlowConnection.REACTANCE_PHASE_BC_KEY, legacyid, name);
    Object xCA = dew.getComponentData(ElectricPowerFlowConnection.REACTANCE_PHASE_CA_KEY, legacyid, name);
    
    Object capA = dew.getComponentData(ElectricPowerFlowConnection.CAPACITY_RATING_A_KEY, legacyid, name);
    Object capB = dew.getComponentData(ElectricPowerFlowConnection.CAPACITY_RATING_B_KEY, legacyid, name);
    Object capC = dew.getComponentData(ElectricPowerFlowConnection.CAPACITY_RATING_C_KEY, legacyid, name);
       
    double resistanceA = rA == null ? .0001 : Double.parseDouble(rA.toString());
    double resistanceB = rB == null ? .0001 : Double.parseDouble(rB.toString());
    double resistanceC = rC == null ? .0001 : Double.parseDouble(rC.toString());
    double resistanceAB = rAB == null ? .00001 : Double.parseDouble(rAB.toString());
    double resistanceBC = rBC == null ? .00001 : Double.parseDouble(rBC.toString());
    double resistanceCA = rCA == null ? .00001 : Double.parseDouble(rCA.toString());
    
    double reactanceA = xA == null ? .0001 : Double.parseDouble(xA.toString());
    double reactanceB = xB == null ? .0001 : Double.parseDouble(xB.toString());
    double reactanceC = xC == null ? .0001 : Double.parseDouble(xC.toString());
    double reactanceAB = xAB == null ? .00001 : Double.parseDouble(xAB.toString());
    double reactanceBC = xBC == null ? .00001 : Double.parseDouble(xBC.toString());
    double reactanceCA = xCA == null ? .00001 : Double.parseDouble(xCA.toString());
    
    Double charging = dew.getComponentData(ElectricPowerFlowConnection.LINE_CHARGING_KEY, legacyid, name) == null ? 0.0 : Double.parseDouble(dew.getComponentData(ElectricPowerFlowConnection.LINE_CHARGING_KEY, legacyid, name).toString());
    double ratingA = capA == null ? 1e20 : Double.parseDouble(capA.toString());
    double ratingB = capB == null ? 1e20 : Double.parseDouble(capB.toString());
    double ratingC = capC == null ? 1e20 : Double.parseDouble(capC.toString()); 
        
    double resistance = Math.max(0.00001,resistanceA + resistanceB + resistanceC);
    double reactance = Math.max(0.00001, reactanceA + reactanceB + reactanceC);
        
    Line line = registerLine(legacyid);
    line.setResistance(resistance);
    line.setReactance(reactance);
    
    line.setAttribute(ElectricPowerFlowConnection.RESISTANCE_PHASE_A_KEY, resistanceA);
    line.setAttribute(ElectricPowerFlowConnection.RESISTANCE_PHASE_B_KEY, resistanceB);
    line.setAttribute(ElectricPowerFlowConnection.RESISTANCE_PHASE_C_KEY, resistanceC);
    line.setAttribute(ElectricPowerFlowConnection.RESISTANCE_PHASE_AB_KEY, resistanceAB);
    line.setAttribute(ElectricPowerFlowConnection.RESISTANCE_PHASE_BA_KEY, resistanceAB); // not sure if this right or not
    line.setAttribute(ElectricPowerFlowConnection.RESISTANCE_PHASE_BC_KEY, resistanceBC);
    line.setAttribute(ElectricPowerFlowConnection.RESISTANCE_PHASE_CB_KEY, resistanceBC);
    line.setAttribute(ElectricPowerFlowConnection.RESISTANCE_PHASE_CA_KEY, resistanceCA);
    line.setAttribute(ElectricPowerFlowConnection.RESISTANCE_PHASE_AC_KEY, resistanceCA);
    
    line.setAttribute(ElectricPowerFlowConnection.REACTANCE_PHASE_A_KEY, reactanceA);
    line.setAttribute(ElectricPowerFlowConnection.REACTANCE_PHASE_B_KEY, reactanceB);
    line.setAttribute(ElectricPowerFlowConnection.REACTANCE_PHASE_C_KEY, reactanceC);
    line.setAttribute(ElectricPowerFlowConnection.REACTANCE_PHASE_AB_KEY, reactanceAB);
    line.setAttribute(ElectricPowerFlowConnection.REACTANCE_PHASE_BA_KEY, reactanceAB);
    line.setAttribute(ElectricPowerFlowConnection.REACTANCE_PHASE_BC_KEY, reactanceBC);
    line.setAttribute(ElectricPowerFlowConnection.REACTANCE_PHASE_CB_KEY, reactanceBC);
    line.setAttribute(ElectricPowerFlowConnection.REACTANCE_PHASE_CA_KEY, reactanceCA);    
    line.setAttribute(ElectricPowerFlowConnection.REACTANCE_PHASE_AC_KEY, reactanceCA);    
    
    line.setLineCharging(charging);
    line.setCapacityRating(ratingA + ratingB + ratingC);
    line.setLongTermEmergencyCapacityRating(ratingA + ratingB + ratingC);
    line.setShortTermEmergencyCapacityRating(ratingA + ratingB + ratingC);
    line.setAttribute(Line.CAPACITY_RATING_A_KEY, ratingA);
    line.setAttribute(Line.CAPACITY_RATING_B_KEY, ratingB);
    line.setAttribute(Line.CAPACITY_RATING_C_KEY, ratingC);
    line.setStatus(status);
    line.setMWFlow(0.0);
    line.setMVarFlow(0.0);
    line.setCoordinates(new LineImpl(points.toArray(new Point[0])));
    line.setAttribute(Line.IS_FAILED_KEY, isFailed);
    line.setAttribute(DewVariables.DEW_COMPONENT_TYPE_KEY, dewType);
    line.setAttribute(Line.NUMBER_OF_PHASES_KEY, numPhases);
    line.setAttribute(Line.HAS_PHASE_A_KEY, hasPhaseA);
    line.setAttribute(Line.HAS_PHASE_B_KEY, hasPhaseB);
    line.setAttribute(Line.HAS_PHASE_C_KEY, hasPhaseC);    
    line.setAttribute(ElectricPowerFlowConnection.NAME_KEY, name);
        
    int ptrow = Integer.parseInt(dew.getComponentData(DewVariables.DEW_DATABASE_PTROW_KEY, legacyid, name).toString());
    
    DewPtlinespcData data = lineData.get(ptrow);
    String lineType = ptrow == -1 ? "" : data.getStnam();
    LineInstallationTypeEnum installType = ptrow == -1 ? LineInstallationTypeEnum.UNKNOWN_TYPE : LineInstallationTypeEnum.getEnum(data.getSoverhead());
    String lineDesc = ptrow == -1 ? "" : data.getStdesc();
    line.setAttribute(ElectricPowerFlowConnection.LINE_DESCRIPTION_KEY, lineDesc);
    line.setAttribute(ElectricPowerFlowConnection.LINE_TYPE_KEY, lineType);
    line.setAttribute(ElectricPowerFlowConnection.INSTALLATION_TYPE_KEY, installType);
    
    int subId = Integer.parseInt(dew.getComponentData(DewVariables.DEW_SUBSTATION_KEY, legacyid, name).toString());
    line.setAttribute(DewVariables.DEW_SUBSTATION_KEY, subId);


    if (linetype == 65) {
      int phaseCond = Integer.parseInt(dew.getComponentData(DewVariables.DEW_DATABASE_PHASE_CONDUCTOR_KEY, legacyid, name).toString());
      int neutralCond = Integer.parseInt(dew.getComponentData(DewVariables.DEW_DATABASE_NEUTRAL_CONDUCTOR_KEY, legacyid, name).toString());
      
      DewPtlinecondData pdata = lineCondData.get(phaseCond);
      DewPtlinecondData ndata = lineCondData.get(neutralCond);

      String pDesc = pdata == null ? "" : pdata.getStdesc();
      String nDesc = ndata == null ? "" : ndata.getStdesc();
      
      line.setAttribute(ElectricPowerFlowConnection.LINE_PHASE_CONDUCTOR_KEY, pDesc);
      line.setAttribute(ElectricPowerFlowConnection.LINE_NEUTRAL_CONDUCTOR_KEY, nDesc);
    }
    else if (linetype == 129) {
      int phaseCond = Integer.parseInt(dew.getComponentData(DewVariables.DEW_DATABASE_PHASE_CONDUCTOR_KEY, legacyid, name).toString());
      int neutralCond = Integer.parseInt(dew.getComponentData(DewVariables.DEW_DATABASE_NEUTRAL_CONDUCTOR_KEY, legacyid, name).toString());

      DewPtcabcondData pdata = cabCondData.get(phaseCond);
      DewPtcabcondData ndata = cabCondData.get(neutralCond);

      String pDesc = pdata == null ? "" : pdata.getStdesc();
      String nDesc = ndata == null ? "" : ndata.getStdesc();

      line.setAttribute(ElectricPowerFlowConnection.LINE_PHASE_CONDUCTOR_KEY, pDesc);
      line.setAttribute(ElectricPowerFlowConnection.LINE_NEUTRAL_CONDUCTOR_KEY, nDesc);      
    }
    else {
      line.setAttribute(ElectricPowerFlowConnection.LINE_PHASE_CONDUCTOR_KEY, "");
      line.setAttribute(ElectricPowerFlowConnection.LINE_NEUTRAL_CONDUCTOR_KEY, "");
    }
    
    return line;
  }

  /**
   * This is for creating a psuedo line when we don't have any
   * @param bus1
   * @param bus2
   * @return
   */
  public Line createLine(int subId, Bus bus1, Bus bus2, DewLegacyId legacyid) {
    String name = "pseudoLine-" + bus1 + "-" + bus2;
    boolean isFailed = false;
    boolean status = true;
    int dewType = -1; 
    int numPhases = 3;
    boolean hasPhaseA = true;
    boolean hasPhaseB = true;
    boolean hasPhaseC = true;
  
    ArrayList<Point> points = new ArrayList<Point>();
    points.add(bus1.getCoordinate());
    points.add(bus2.getCoordinate());
        
    double resistanceA = .0001;
    double resistanceB = .0001;
    double resistanceC = .0001;
    double resistanceAB = .00001;
    double resistanceBC = .00001;
    double resistanceCA = .00001;    
    
    double reactanceA = .0001;
    double reactanceB = .0001;
    double reactanceC = .0001;
    double reactanceAB = .00001;
    double reactanceBC = .00001;
    double reactanceCA = .00001;
    
    double charging = 0;
    double ratingA = 1e20;
    double ratingB = 1e20;
    double ratingC = 1e20; 
        
    double resistance = Math.max(0.00001,resistanceA + resistanceB + resistanceC);
    double reactance = Math.max(0.00001, reactanceA + reactanceB + reactanceC);
        
    Line line = registerLine(legacyid);
    line.setResistance(resistance);
    line.setReactance(reactance);
    
    
    line.setAttribute(ElectricPowerFlowConnection.RESISTANCE_PHASE_A_KEY, resistanceA);
    line.setAttribute(ElectricPowerFlowConnection.RESISTANCE_PHASE_B_KEY, resistanceB);
    line.setAttribute(ElectricPowerFlowConnection.RESISTANCE_PHASE_C_KEY, resistanceC);
    line.setAttribute(ElectricPowerFlowConnection.RESISTANCE_PHASE_AB_KEY, resistanceAB);
    line.setAttribute(ElectricPowerFlowConnection.RESISTANCE_PHASE_BC_KEY, resistanceBC);
    line.setAttribute(ElectricPowerFlowConnection.RESISTANCE_PHASE_CA_KEY, resistanceCA);
    line.setAttribute(ElectricPowerFlowConnection.RESISTANCE_PHASE_BA_KEY, resistanceAB);
    line.setAttribute(ElectricPowerFlowConnection.RESISTANCE_PHASE_CB_KEY, resistanceBC);
    line.setAttribute(ElectricPowerFlowConnection.RESISTANCE_PHASE_AC_KEY, resistanceCA);

    
    line.setAttribute(ElectricPowerFlowConnection.REACTANCE_PHASE_A_KEY, reactanceA);
    line.setAttribute(ElectricPowerFlowConnection.REACTANCE_PHASE_B_KEY, reactanceB);
    line.setAttribute(ElectricPowerFlowConnection.REACTANCE_PHASE_C_KEY, reactanceC);
    line.setAttribute(ElectricPowerFlowConnection.REACTANCE_PHASE_AB_KEY, reactanceAB);
    line.setAttribute(ElectricPowerFlowConnection.REACTANCE_PHASE_BC_KEY, reactanceBC);
    line.setAttribute(ElectricPowerFlowConnection.REACTANCE_PHASE_CA_KEY, reactanceCA);
    line.setAttribute(ElectricPowerFlowConnection.REACTANCE_PHASE_BA_KEY, reactanceAB);
    line.setAttribute(ElectricPowerFlowConnection.REACTANCE_PHASE_CB_KEY, reactanceBC);
    line.setAttribute(ElectricPowerFlowConnection.REACTANCE_PHASE_AC_KEY, reactanceCA);
    
//    int subId = Integer.parseInt(dew.getComponentData(DewVariables.DEW_SUBSTATION_KEY, legacyid, "").toString());
    line.setAttribute(DewVariables.DEW_SUBSTATION_KEY, subId);    
    
    line.setLineCharging(charging);
    line.setCapacityRating(ratingA + ratingB + ratingC);
    line.setLongTermEmergencyCapacityRating(ratingA + ratingB + ratingC);
    line.setShortTermEmergencyCapacityRating(ratingA + ratingB + ratingC);
    line.setAttribute(Line.CAPACITY_RATING_A_KEY, ratingA);
    line.setAttribute(Line.CAPACITY_RATING_B_KEY, ratingB);
    line.setAttribute(Line.CAPACITY_RATING_C_KEY, ratingC);
    line.setStatus(status);
    line.setMWFlow(0.0);
    line.setMVarFlow(0.0);
    line.setCoordinates(new LineImpl(points.toArray(new Point[0])));
    line.setAttribute(Line.IS_FAILED_KEY, isFailed);
    line.setAttribute(DewVariables.DEW_COMPONENT_TYPE_KEY, dewType);
    line.setAttribute(Line.NUMBER_OF_PHASES_KEY, numPhases);
    line.setAttribute(Line.HAS_PHASE_A_KEY, hasPhaseA);
    line.setAttribute(Line.HAS_PHASE_B_KEY, hasPhaseB);
    line.setAttribute(Line.HAS_PHASE_C_KEY, hasPhaseC);    
    line.setAttribute(ElectricPowerFlowConnection.NAME_KEY, name);
        
    line.setAttribute(ElectricPowerFlowConnection.LINE_DESCRIPTION_KEY, "");
    line.setAttribute(ElectricPowerFlowConnection.LINE_TYPE_KEY, "");
    line.setAttribute(ElectricPowerFlowConnection.INSTALLATION_TYPE_KEY, LineInstallationTypeEnum.UNKNOWN_TYPE);
    line.setAttribute(ElectricPowerFlowConnection.LINE_PHASE_CONDUCTOR_KEY, "");
    line.setAttribute(ElectricPowerFlowConnection.LINE_NEUTRAL_CONDUCTOR_KEY, "");
    
    return line;
  }

  
}
