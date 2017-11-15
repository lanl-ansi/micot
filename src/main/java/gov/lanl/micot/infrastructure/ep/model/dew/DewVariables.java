package gov.lanl.micot.infrastructure.ep.model.dew;

import gov.lanl.micot.infrastructure.ep.model.Bus;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.Generator;
import gov.lanl.micot.infrastructure.ep.model.Load;
import gov.lanl.micot.infrastructure.model.Asset;
import gov.lanl.micot.util.collection.Pair;

import java.util.HashMap;
import java.util.Map;

public class DewVariables {

	private Map<Integer,Map<String,Integer>> variableIndices = new HashMap<Integer,Map<String,Integer>>();
	
	private Map<Integer,Integer> appIdIndices = new HashMap<Integer,Integer>();
	
	public static final Map<String, DewModelTypeEnum> dataSources = new HashMap<String,DewModelTypeEnum>();
	
	public static final Map<String, Integer> phaseSources = new HashMap<String, Integer>();
	
	public static final Map<String, Pair<Integer,String>> libraryIndicies = new HashMap<String, Pair<Integer,String>>();
	
	public static final Map<String, Pair<String,Integer>> textFileIndicies = new HashMap<String, Pair<String,Integer>>();
	
	/**
	 * Could make these data structures non static, but not sure we want to constantly re-create data structures that could
	 * be massive
	 */
	static {
	  dataSources.put(DewVariables.DEW_COMPONENT_TYPE_KEY, DewModelTypeEnum.TEXTFILE_TYPE);
  	dataSources.put(DewVariables.DEW_DATABASE_PTROW_KEY, DewModelTypeEnum.TEXTFILE_TYPE);
  	dataSources.put(DewVariables.DEW_DATABASE_PHASE_CONDUCTOR_KEY, DewModelTypeEnum.TEXTFILE_TYPE);
    dataSources.put(DewVariables.DEW_DATABASE_NEUTRAL_CONDUCTOR_KEY, DewModelTypeEnum.TEXTFILE_TYPE);
    dataSources.put(DewVariables.DEW_DATABASE_XFRM_KEY, DewModelTypeEnum.TEXTFILE_TYPE);      
    dataSources.put(DewVariables.DEW_CONNECTION_KEY, DewModelTypeEnum.TEXTFILE_TYPE);  
    dataSources.put(DewVariables.DEW_SUBSTATION_KEY, DewModelTypeEnum.TEXTFILE_TYPE);
    dataSources.put(DewVariables.DEW_CONNECTION1_KEY, DewModelTypeEnum.TEXTFILE_TYPE);
    dataSources.put(DewVariables.DEW_CONNECTION2_KEY, DewModelTypeEnum.TEXTFILE_TYPE);
    dataSources.put(DewVariables.DEW_FEEDER_CONNECTION_KEY, DewModelTypeEnum.TEXTFILE_TYPE);
    dataSources.put(DewVariables.DEW_FEEDER_CONNECTION_ID_KEY, DewModelTypeEnum.TEXTFILE_TYPE);    
    dataSources.put(Bus.NAME_KEY, DewModelTypeEnum.TEXTFILE_TYPE);
    dataSources.put(Generator.NAME_KEY, DewModelTypeEnum.TEXTFILE_TYPE);
    dataSources.put(DewVariables.DEW_SHUNT_NAME_KEY, DewModelTypeEnum.TEXTFILE_TYPE);
    dataSources.put(DewVariables.DEW_GENERATOR_NAME_KEY, DewModelTypeEnum.TEXTFILE_TYPE);
    dataSources.put(DewVariables.DEW_LOAD_NAME_KEY, DewModelTypeEnum.TEXTFILE_TYPE);
    dataSources.put(DewVariables.DEW_BUS_NAME_KEY, DewModelTypeEnum.TEXTFILE_TYPE);
    dataSources.put(Load.LOAD_NAME_KEY, DewModelTypeEnum.TEXTFILE_TYPE);
    dataSources.put(Bus.SYSTEM_VOLTAGE_KV_KEY, DewModelTypeEnum.TEXTFILE_TYPE);
    dataSources.put(Asset.IS_FAILED_KEY, DewModelTypeEnum.TEXTFILE_TYPE);
    dataSources.put(Asset.STATUS_KEY, DewModelTypeEnum.TEXTFILE_TYPE);
    dataSources.put(DewVariables.DEW_X_KEY, DewModelTypeEnum.TEXTFILE_TYPE);
    dataSources.put(DewVariables.DEW_Y_KEY, DewModelTypeEnum.TEXTFILE_TYPE);
    dataSources.put(Generator.NUM_PHASE_KEY, DewModelTypeEnum.TEXTFILE_TYPE);
    dataSources.put(DewVariables.DEW_PHASES_KEY, DewModelTypeEnum.TEXTFILE_TYPE);
    dataSources.put(Generator.REAL_GENERATION_KEY, DewModelTypeEnum.TEXTFILE_TYPE);
    dataSources.put(Generator.DESIRED_REAL_GENERATION_A_KEY, DewModelTypeEnum.TEXTFILE_TYPE);
    dataSources.put(Generator.DESIRED_REAL_GENERATION_B_KEY, DewModelTypeEnum.TEXTFILE_TYPE);
    dataSources.put(Generator.DESIRED_REAL_GENERATION_C_KEY, DewModelTypeEnum.TEXTFILE_TYPE);
    dataSources.put(Generator.REACTIVE_GENERATION_KEY, DewModelTypeEnum.TEXTFILE_TYPE);
    dataSources.put(Generator.DESIRED_REACTIVE_GENERATION_A_KEY, DewModelTypeEnum.TEXTFILE_TYPE);
    dataSources.put(Generator.DESIRED_REACTIVE_GENERATION_B_KEY, DewModelTypeEnum.TEXTFILE_TYPE);
    dataSources.put(Generator.DESIRED_REACTIVE_GENERATION_C_KEY, DewModelTypeEnum.TEXTFILE_TYPE);
    dataSources.put(Generator.REACTIVE_MIN_KEY, DewModelTypeEnum.TEXTFILE_TYPE);
    dataSources.put(Generator.DESIRED_REACTIVE_MAX_KEY, DewModelTypeEnum.TEXTFILE_TYPE);
    dataSources.put(Bus.VOLTAGE_PU_KEY, DewModelTypeEnum.TEXTFILE_TYPE);
    dataSources.put(Load.DESIRED_REAL_LOAD_A_KEY, DewModelTypeEnum.TEXTFILE_FIRST_TYPE);
    dataSources.put(Load.DESIRED_REAL_LOAD_B_KEY, DewModelTypeEnum.TEXTFILE_FIRST_TYPE);
    dataSources.put(Load.DESIRED_REAL_LOAD_C_KEY, DewModelTypeEnum.TEXTFILE_FIRST_TYPE);
    dataSources.put(Load.DESIRED_REACTIVE_LOAD_A_KEY, DewModelTypeEnum.TEXTFILE_FIRST_TYPE);
    dataSources.put(Load.DESIRED_REACTIVE_LOAD_B_KEY, DewModelTypeEnum.TEXTFILE_FIRST_TYPE);
    dataSources.put(Load.DESIRED_REACTIVE_LOAD_C_KEY, DewModelTypeEnum.TEXTFILE_FIRST_TYPE);
    dataSources.put(Load.TARGET_REAL_LOAD_A_KEY, DewModelTypeEnum.TEXTFILE_FIRST_TYPE);
    dataSources.put(Load.TARGET_REAL_LOAD_B_KEY, DewModelTypeEnum.TEXTFILE_FIRST_TYPE);
    dataSources.put(Load.TARGET_REAL_LOAD_C_KEY, DewModelTypeEnum.TEXTFILE_FIRST_TYPE);
    dataSources.put(Load.TARGET_REACTIVE_LOAD_A_KEY, DewModelTypeEnum.TEXTFILE_FIRST_TYPE);
    dataSources.put(Load.TARGET_REACTIVE_LOAD_B_KEY, DewModelTypeEnum.TEXTFILE_FIRST_TYPE);
    dataSources.put(Load.TARGET_REACTIVE_LOAD_C_KEY, DewModelTypeEnum.TEXTFILE_FIRST_TYPE);    
    dataSources.put(DewVariables.DEW_DESIRED_REAL_LOAD_A_KEY, DewModelTypeEnum.LIBRARY_TYPE);
    dataSources.put(DewVariables.DEW_DESIRED_REAL_LOAD_B_KEY, DewModelTypeEnum.LIBRARY_TYPE);
    dataSources.put(DewVariables.DEW_DESIRED_REAL_LOAD_C_KEY, DewModelTypeEnum.LIBRARY_TYPE);
    dataSources.put(DewVariables.DEW_DESIRED_REACTIVE_LOAD_A_KEY, DewModelTypeEnum.LIBRARY_TYPE);
    dataSources.put(DewVariables.DEW_DESIRED_REACTIVE_LOAD_B_KEY, DewModelTypeEnum.LIBRARY_TYPE);
    dataSources.put(DewVariables.DEW_DESIRED_REACTIVE_LOAD_C_KEY, DewModelTypeEnum.LIBRARY_TYPE);
    dataSources.put(Asset.COORDINATE_KEY, DewModelTypeEnum.COORDINATE_TYPE);
    dataSources.put(ElectricPowerFlowConnection.CAPACITY_RATING_KEY, DewModelTypeEnum.TEXTFILE_TYPE);
    dataSources.put(ElectricPowerFlowConnection.CAPACITY_RATING_A_KEY, DewModelTypeEnum.LIBRARY_TYPE);
    dataSources.put(ElectricPowerFlowConnection.CAPACITY_RATING_B_KEY, DewModelTypeEnum.LIBRARY_TYPE);
    dataSources.put(ElectricPowerFlowConnection.CAPACITY_RATING_C_KEY, DewModelTypeEnum.LIBRARY_TYPE);
    dataSources.put(ElectricPowerFlowConnection.LONG_TERM_EMERGENCY_CAPACITY_RATING_KEY, DewModelTypeEnum.TEXTFILE_TYPE);
    dataSources.put(ElectricPowerFlowConnection.SHORT_TERM_EMERGENCY_CAPACITY_RATING_KEY, DewModelTypeEnum.TEXTFILE_TYPE);
    dataSources.put(ElectricPowerFlowConnection.RESISTANCE_KEY, DewModelTypeEnum.TEXTFILE_FIRST_TYPE);
    dataSources.put(ElectricPowerFlowConnection.REACTANCE_KEY, DewModelTypeEnum.TEXTFILE_FIRST_TYPE);
    dataSources.put(ElectricPowerFlowConnection.LINE_CHARGING_KEY, DewModelTypeEnum.LINECHARGING_TYPE);    
    dataSources.put(ElectricPowerFlowConnection.RESISTANCE_PHASE_A_KEY, DewModelTypeEnum.LIBRARY_TYPE);
    dataSources.put(ElectricPowerFlowConnection.RESISTANCE_PHASE_B_KEY, DewModelTypeEnum.LIBRARY_TYPE);
    dataSources.put(ElectricPowerFlowConnection.RESISTANCE_PHASE_C_KEY, DewModelTypeEnum.LIBRARY_TYPE);
    dataSources.put(ElectricPowerFlowConnection.RESISTANCE_PHASE_AB_KEY, DewModelTypeEnum.LIBRARY_TYPE);
    dataSources.put(ElectricPowerFlowConnection.RESISTANCE_PHASE_BC_KEY, DewModelTypeEnum.LIBRARY_TYPE);
    dataSources.put(ElectricPowerFlowConnection.RESISTANCE_PHASE_CA_KEY, DewModelTypeEnum.LIBRARY_TYPE);      
    dataSources.put(ElectricPowerFlowConnection.REACTANCE_PHASE_A_KEY, DewModelTypeEnum.LIBRARY_TYPE);
    dataSources.put(ElectricPowerFlowConnection.REACTANCE_PHASE_B_KEY, DewModelTypeEnum.LIBRARY_TYPE);
    dataSources.put(ElectricPowerFlowConnection.REACTANCE_PHASE_C_KEY, DewModelTypeEnum.LIBRARY_TYPE);
    dataSources.put(ElectricPowerFlowConnection.REACTANCE_PHASE_AB_KEY, DewModelTypeEnum.LIBRARY_TYPE);
    dataSources.put(ElectricPowerFlowConnection.REACTANCE_PHASE_BC_KEY, DewModelTypeEnum.LIBRARY_TYPE);
    dataSources.put(ElectricPowerFlowConnection.REACTANCE_PHASE_CA_KEY, DewModelTypeEnum.LIBRARY_TYPE);    
    dataSources.put(Bus.VOLTAGE_PU_A_KEY, DewModelTypeEnum.LIBRARY_TYPE);
    dataSources.put(Bus.VOLTAGE_PU_B_KEY, DewModelTypeEnum.LIBRARY_TYPE);
    dataSources.put(Bus.VOLTAGE_PU_C_KEY, DewModelTypeEnum.LIBRARY_TYPE);
    dataSources.put(Bus.PHASE_ANGLE_A_KEY, DewModelTypeEnum.LIBRARY_TYPE);
    dataSources.put(Bus.PHASE_ANGLE_B_KEY, DewModelTypeEnum.LIBRARY_TYPE);
    dataSources.put(Bus.PHASE_ANGLE_C_KEY, DewModelTypeEnum.LIBRARY_TYPE);
    dataSources.put(Generator.ACTUAL_REAL_GENERATION_A_KEY, DewModelTypeEnum.LIBRARY_TYPE);
    dataSources.put(Generator.ACTUAL_REAL_GENERATION_B_KEY, DewModelTypeEnum.LIBRARY_TYPE);
    dataSources.put(Generator.ACTUAL_REAL_GENERATION_C_KEY, DewModelTypeEnum.LIBRARY_TYPE);
    dataSources.put(Generator.ACTUAL_REACTIVE_GENERATION_A_KEY, DewModelTypeEnum.LIBRARY_TYPE);
    dataSources.put(Generator.ACTUAL_REACTIVE_GENERATION_B_KEY, DewModelTypeEnum.LIBRARY_TYPE);
    dataSources.put(Generator.ACTUAL_REACTIVE_GENERATION_C_KEY, DewModelTypeEnum.LIBRARY_TYPE);
    dataSources.put(Load.ACTUAL_REAL_LOAD_A_KEY, DewModelTypeEnum.LIBRARY_TYPE);
    dataSources.put(Load.ACTUAL_REAL_LOAD_B_KEY, DewModelTypeEnum.LIBRARY_TYPE);
    dataSources.put(Load.ACTUAL_REAL_LOAD_C_KEY, DewModelTypeEnum.LIBRARY_TYPE);
    dataSources.put(Load.ACTUAL_REACTIVE_LOAD_A_KEY, DewModelTypeEnum.LIBRARY_TYPE);
    dataSources.put(Load.ACTUAL_REACTIVE_LOAD_B_KEY, DewModelTypeEnum.LIBRARY_TYPE);
    dataSources.put(Load.ACTUAL_REACTIVE_LOAD_C_KEY, DewModelTypeEnum.LIBRARY_TYPE);    
    dataSources.put(ElectricPowerFlowConnection.MW_FLOW_PHASE_A_KEY, DewModelTypeEnum.LIBRARY_TYPE);
    dataSources.put(ElectricPowerFlowConnection.MW_FLOW_PHASE_B_KEY, DewModelTypeEnum.LIBRARY_TYPE);
    dataSources.put(ElectricPowerFlowConnection.MW_FLOW_PHASE_C_KEY, DewModelTypeEnum.LIBRARY_TYPE);
    dataSources.put(ElectricPowerFlowConnection.MVAR_FLOW_PHASE_A_KEY, DewModelTypeEnum.LIBRARY_TYPE);
    dataSources.put(ElectricPowerFlowConnection.MVAR_FLOW_PHASE_B_KEY, DewModelTypeEnum.LIBRARY_TYPE);
    dataSources.put(ElectricPowerFlowConnection.MVAR_FLOW_PHASE_C_KEY, DewModelTypeEnum.LIBRARY_TYPE);
    
    phaseSources.put(DewVariables.DEW_COMPONENT_TYPE_KEY, 0);
    phaseSources.put(DewVariables.DEW_DATABASE_PTROW_KEY, 0);
    phaseSources.put(DewVariables.DEW_DATABASE_NEUTRAL_CONDUCTOR_KEY, 0);
    phaseSources.put(DewVariables.DEW_DATABASE_PHASE_CONDUCTOR_KEY, 0);
    phaseSources.put(DewVariables.DEW_DATABASE_XFRM_KEY, 0);    
    phaseSources.put(DewVariables.DEW_CONNECTION_KEY, 0);
    phaseSources.put(DewVariables.DEW_SUBSTATION_KEY, 0);
    phaseSources.put(DewVariables.DEW_CONNECTION1_KEY, 0);
    phaseSources.put(DewVariables.DEW_CONNECTION2_KEY, 0);
    phaseSources.put(DewVariables.DEW_FEEDER_CONNECTION_KEY, 0);
    phaseSources.put(DewVariables.DEW_FEEDER_CONNECTION_ID_KEY, 0);
    phaseSources.put(Bus.NAME_KEY, 0);
    phaseSources.put(DewVariables.DEW_SHUNT_NAME_KEY, 0);
    phaseSources.put(Load.LOAD_NAME_KEY, 0);
    phaseSources.put(DewVariables.DEW_GENERATOR_NAME_KEY, 0);
    phaseSources.put(DewVariables.DEW_LOAD_NAME_KEY, 0);
    phaseSources.put(DewVariables.DEW_BUS_NAME_KEY, 0);
    phaseSources.put(Generator.NAME_KEY, 0);
    phaseSources.put(Bus.SYSTEM_VOLTAGE_KV_KEY, 0);
    phaseSources.put(Asset.IS_FAILED_KEY, 0);
    phaseSources.put(Asset.STATUS_KEY, 0);
    phaseSources.put(DewVariables.DEW_X_KEY, 0);
    phaseSources.put(DewVariables.DEW_Y_KEY, 0);
    phaseSources.put(Generator.NUM_PHASE_KEY, 0);
    phaseSources.put(DewVariables.DEW_PHASES_KEY, 0);
    phaseSources.put(Generator.REAL_GENERATION_KEY, 0);
    phaseSources.put(Generator.DESIRED_REAL_GENERATION_A_KEY, 0);
    phaseSources.put(Generator.DESIRED_REAL_GENERATION_B_KEY, 1);
    phaseSources.put(Generator.DESIRED_REAL_GENERATION_C_KEY, 2);
    phaseSources.put(Generator.REACTIVE_GENERATION_KEY, 0);
    phaseSources.put(Generator.DESIRED_REACTIVE_GENERATION_A_KEY, 0);
    phaseSources.put(Generator.DESIRED_REACTIVE_GENERATION_B_KEY, 1);
    phaseSources.put(Generator.DESIRED_REACTIVE_GENERATION_C_KEY, 2);
    phaseSources.put(Generator.REACTIVE_MIN_KEY, 0);
    phaseSources.put(Generator.DESIRED_REACTIVE_MAX_KEY, 0);
    phaseSources.put(Bus.VOLTAGE_PU_KEY, 0);
    phaseSources.put(Load.DESIRED_REAL_LOAD_A_KEY, 0);
    phaseSources.put(Load.DESIRED_REAL_LOAD_B_KEY, 1);
    phaseSources.put(Load.DESIRED_REAL_LOAD_C_KEY, 2);
    phaseSources.put(Load.DESIRED_REACTIVE_LOAD_A_KEY, 0);
    phaseSources.put(Load.DESIRED_REACTIVE_LOAD_B_KEY, 1);
    phaseSources.put(Load.DESIRED_REACTIVE_LOAD_C_KEY, 2);
    phaseSources.put(Load.TARGET_REAL_LOAD_A_KEY, 0);
    phaseSources.put(Load.TARGET_REAL_LOAD_B_KEY, 1);
    phaseSources.put(Load.TARGET_REAL_LOAD_C_KEY, 2);
    phaseSources.put(Load.TARGET_REACTIVE_LOAD_A_KEY, 0);
    phaseSources.put(Load.TARGET_REACTIVE_LOAD_B_KEY, 1);
    phaseSources.put(Load.TARGET_REACTIVE_LOAD_C_KEY, 2);    
    phaseSources.put(DewVariables.DEW_DESIRED_REAL_LOAD_A_KEY, 0);
    phaseSources.put(DewVariables.DEW_DESIRED_REAL_LOAD_B_KEY, 1);
    phaseSources.put(DewVariables.DEW_DESIRED_REAL_LOAD_C_KEY, 2);
    phaseSources.put(DewVariables.DEW_DESIRED_REACTIVE_LOAD_A_KEY, 0);
    phaseSources.put(DewVariables.DEW_DESIRED_REACTIVE_LOAD_B_KEY, 1);
    phaseSources.put(DewVariables.DEW_DESIRED_REACTIVE_LOAD_C_KEY, 2);    
    phaseSources.put(Asset.COORDINATE_KEY, 0);
    phaseSources.put(ElectricPowerFlowConnection.CAPACITY_RATING_KEY, 0);
    phaseSources.put(ElectricPowerFlowConnection.CAPACITY_RATING_A_KEY, 0);
    phaseSources.put(ElectricPowerFlowConnection.CAPACITY_RATING_B_KEY, 1);
    phaseSources.put(ElectricPowerFlowConnection.CAPACITY_RATING_C_KEY, 2);
    phaseSources.put(ElectricPowerFlowConnection.LONG_TERM_EMERGENCY_CAPACITY_RATING_KEY, 0);
    phaseSources.put(ElectricPowerFlowConnection.SHORT_TERM_EMERGENCY_CAPACITY_RATING_KEY, 0);
    phaseSources.put(ElectricPowerFlowConnection.RESISTANCE_KEY, 0);
    phaseSources.put(ElectricPowerFlowConnection.REACTANCE_KEY, 0);
    phaseSources.put(ElectricPowerFlowConnection.LINE_CHARGING_KEY, 0);    
    phaseSources.put(ElectricPowerFlowConnection.RESISTANCE_PHASE_A_KEY, 0);
    phaseSources.put(ElectricPowerFlowConnection.RESISTANCE_PHASE_B_KEY, 1);
    phaseSources.put(ElectricPowerFlowConnection.RESISTANCE_PHASE_C_KEY, 2);
    phaseSources.put(ElectricPowerFlowConnection.RESISTANCE_PHASE_AB_KEY, 0);
    phaseSources.put(ElectricPowerFlowConnection.RESISTANCE_PHASE_BC_KEY, 1);
    phaseSources.put(ElectricPowerFlowConnection.RESISTANCE_PHASE_CA_KEY, 2);    
    phaseSources.put(ElectricPowerFlowConnection.REACTANCE_PHASE_A_KEY, 0);
    phaseSources.put(ElectricPowerFlowConnection.REACTANCE_PHASE_B_KEY, 1);
    phaseSources.put(ElectricPowerFlowConnection.REACTANCE_PHASE_C_KEY, 2);
    phaseSources.put(ElectricPowerFlowConnection.REACTANCE_PHASE_AB_KEY, 0);
    phaseSources.put(ElectricPowerFlowConnection.REACTANCE_PHASE_BC_KEY, 1);
    phaseSources.put(ElectricPowerFlowConnection.REACTANCE_PHASE_CA_KEY, 2);    
    phaseSources.put(Bus.VOLTAGE_PU_A_KEY, 0);
    phaseSources.put(Bus.VOLTAGE_PU_B_KEY, 1);
    phaseSources.put(Bus.VOLTAGE_PU_C_KEY, 2);
    phaseSources.put(Bus.PHASE_ANGLE_A_KEY, 0);
    phaseSources.put(Bus.PHASE_ANGLE_B_KEY, 1);
    phaseSources.put(Bus.PHASE_ANGLE_C_KEY, 2);    
    phaseSources.put(Generator.ACTUAL_REAL_GENERATION_A_KEY, 0);
    phaseSources.put(Generator.ACTUAL_REAL_GENERATION_B_KEY, 1);
    phaseSources.put(Generator.ACTUAL_REAL_GENERATION_C_KEY, 2);
    phaseSources.put(Generator.ACTUAL_REACTIVE_GENERATION_A_KEY, 0);
    phaseSources.put(Generator.ACTUAL_REACTIVE_GENERATION_B_KEY, 1);
    phaseSources.put(Generator.ACTUAL_REACTIVE_GENERATION_C_KEY, 2);
    phaseSources.put(Load.ACTUAL_REAL_LOAD_A_KEY, 0);
    phaseSources.put(Load.ACTUAL_REAL_LOAD_B_KEY, 1);
    phaseSources.put(Load.ACTUAL_REAL_LOAD_C_KEY, 2);
    phaseSources.put(Load.ACTUAL_REACTIVE_LOAD_A_KEY, 0);
    phaseSources.put(Load.ACTUAL_REACTIVE_LOAD_B_KEY, 1);
    phaseSources.put(Load.ACTUAL_REACTIVE_LOAD_C_KEY, 2);
    phaseSources.put(ElectricPowerFlowConnection.MW_FLOW_PHASE_A_KEY, 0);
    phaseSources.put(ElectricPowerFlowConnection.MW_FLOW_PHASE_B_KEY, 1);
    phaseSources.put(ElectricPowerFlowConnection.MW_FLOW_PHASE_C_KEY, 2);
    phaseSources.put(ElectricPowerFlowConnection.MVAR_FLOW_PHASE_A_KEY, 0);
    phaseSources.put(ElectricPowerFlowConnection.MVAR_FLOW_PHASE_B_KEY, 1);
    phaseSources.put(ElectricPowerFlowConnection.MVAR_FLOW_PHASE_C_KEY, 2);
    
	  textFileIndicies.put(DewVariables.DEW_COMPONENT_TYPE_KEY, new Pair<String,Integer>(DewVariables.DEW_CMP_HEADER, DewVariables.DEW_TYPE_IDX));
	  textFileIndicies.put(DewVariables.DEW_DATABASE_PTROW_KEY, new Pair<String,Integer>(DewVariables.DEW_CMP_HEADER, DewVariables.DEW_DATABASE_PTROW_IDX));	   	  
	  textFileIndicies.put(DewVariables.DEW_DATABASE_PHASE_CONDUCTOR_KEY, new Pair<String,Integer>(DewVariables.DEW_CMP_CHN_HEADER, DewVariables.DEW_DATABASE_PHASE_CONDUCTOR_IDX));     
    textFileIndicies.put(DewVariables.DEW_DATABASE_NEUTRAL_CONDUCTOR_KEY, new Pair<String,Integer>(DewVariables.DEW_CMP_CHN_HEADER, DewVariables.DEW_DATABASE_NEUTRAL_CONDUCTOR_IDX));
    textFileIndicies.put(DewVariables.DEW_DATABASE_XFRM_KEY, new Pair<String,Integer>(DewVariables.DEW_CMP_HEADER, DewVariables.DEW_DATABASE_XFRM_IDX));         
	  textFileIndicies.put(DewVariables.DEW_CONNECTION1_KEY, new Pair<String,Integer>(DewVariables.DEW_CMP_HEADER, DewVariables.DEW_CONNECTION1_IDX));
    textFileIndicies.put(DewVariables.DEW_CONNECTION2_KEY, new Pair<String,Integer>(DewVariables.DEW_CMP_HEADER, DewVariables.DEW_CONNECTION2_IDX));    
    textFileIndicies.put(DewVariables.DEW_FEEDER_CONNECTION_KEY, new Pair<String,Integer>(DewVariables.DEW_CMP_HEADER, DewVariables.DEW_FEEDER_CONNECTION_IDX));
    textFileIndicies.put(DewVariables.DEW_FEEDER_CONNECTION_ID_KEY, new Pair<String,Integer>(DewVariables.DEW_CMP_HEADER, DewVariables.DEW_FEEDER_CONNECTION_ID_IDX));    
    textFileIndicies.put(DewVariables.DEW_CONNECTION_KEY, new Pair<String,Integer>(DewVariables.DEW_CMP_HEADER, DewVariables.DEW_CONNECTION_IDX));
    textFileIndicies.put(DewVariables.DEW_SUBSTATION_KEY, new Pair<String,Integer>(DewVariables.DEW_CMP_HEADER, DewVariables.DEW_SUBSTATION_IDX));
    textFileIndicies.put(Bus.NAME_KEY, new Pair<String,Integer>(DewVariables.DEW_CMPSERIALNUM_HEADER, DewVariables.DEW_NAME_IDX));
    textFileIndicies.put(DewVariables.DEW_SHUNT_NAME_KEY, new Pair<String,Integer>(DewVariables.DEW_CMPSERIALNUM_HEADER, DewVariables.DEW_SHUNT_NAME_IDX));
    textFileIndicies.put(DewVariables.DEW_GENERATOR_NAME_KEY, new Pair<String,Integer>(DewVariables.DEW_CMPNAM_HEADER, DewVariables.DEW_GENERATOR_NAME_IDX));    
    textFileIndicies.put(DewVariables.DEW_LOAD_NAME_KEY, new Pair<String,Integer>(DewVariables.DEW_CMPSERIALNUM_HEADER, DewVariables.DEW_LOAD_NAME_IDX));        
    textFileIndicies.put(DewVariables.DEW_BUS_NAME_KEY, new Pair<String,Integer>(DewVariables.DEW_CMPNAM_HEADER, DewVariables.DEW_BUS_NAME_IDX));    
    textFileIndicies.put(Load.LOAD_NAME_KEY, new Pair<String,Integer>(DewVariables.DEW_CMPSERIALNUM_HEADER, DewVariables.DEW_NAME_IDX));
    textFileIndicies.put(Generator.NAME_KEY, new Pair<String,Integer>(DewVariables.DEW_CMPSERIALNUM_HEADER, DewVariables.DEW_NAME_IDX));
    textFileIndicies.put(Bus.SYSTEM_VOLTAGE_KV_KEY, new Pair<String,Integer>(DewVariables.DEW_CMPINPSCRDATA_HEADER, DewVariables.DEW_BASEKV_IDX));
    textFileIndicies.put(Asset.IS_FAILED_KEY, new Pair<String,Integer>(DewVariables.DEW_CMPOPER_HEADER, DewVariables.DEW_FAIL_IDX));
    textFileIndicies.put(Asset.STATUS_KEY, new Pair<String,Integer>(DewVariables.DEW_CMP_HEADER, DewVariables.DEW_STATUS_IDX));
    textFileIndicies.put(DewVariables.DEW_X_KEY, new Pair<String,Integer>(DewVariables.DEW_CMP_HEADER, DewVariables.DEW_X_IDX));
    textFileIndicies.put(DewVariables.DEW_Y_KEY, new Pair<String,Integer>(DewVariables.DEW_CMP_HEADER, DewVariables.DEW_Y_IDX));
    textFileIndicies.put(Generator.NUM_PHASE_KEY, new Pair<String,Integer>(DewVariables.DEW_CMP_HEADER, DewVariables.DEW_NUMPHASES_IDX));
    textFileIndicies.put(DewVariables.DEW_PHASES_KEY, new Pair<String,Integer>(DewVariables.DEW_CMP_HEADER, DewVariables.DEW_PHASES_IDX));
    textFileIndicies.put(Generator.REAL_GENERATION_KEY, new Pair<String,Integer>(DewVariables.DEW_CMPCTR_HEADER, DewVariables.DEW_MW_GEN_IDX));
    textFileIndicies.put(Generator.REACTIVE_GENERATION_KEY, new Pair<String,Integer>(DewVariables.DEW_CMPCTR_HEADER, DewVariables.DEW_MVAR_GEN_IDX));
    textFileIndicies.put(Generator.REACTIVE_MIN_KEY, new Pair<String,Integer>(DewVariables.DEW_CMPCTR_HEADER, DewVariables.DEW_MVAR_MIN_GEN_IDX));
    textFileIndicies.put(Generator.DESIRED_REACTIVE_MAX_KEY, new Pair<String,Integer>(DewVariables.DEW_CMPCTR_HEADER, DewVariables.DEW_MVAR_MAX_GEN_IDX));
    textFileIndicies.put(Bus.VOLTAGE_PU_KEY, new Pair<String,Integer>(DewVariables.DEW_CMPCTR_HEADER, DewVariables.DEW_VOLTAGE_GEN_IDX));
    textFileIndicies.put(Load.DESIRED_REAL_LOAD_A_KEY, new Pair<String,Integer>(DewVariables.DEW_CMPCHN_HEADER, DewVariables.DEW_LOAD_MW_A_IDX));
    textFileIndicies.put(Load.DESIRED_REAL_LOAD_B_KEY, new Pair<String,Integer>(DewVariables.DEW_CMPCHN_HEADER, DewVariables.DEW_LOAD_MW_B_IDX));
    textFileIndicies.put(Load.DESIRED_REAL_LOAD_C_KEY, new Pair<String,Integer>(DewVariables.DEW_CMPCHN_HEADER, DewVariables.DEW_LOAD_MW_C_IDX));
    textFileIndicies.put(Load.DESIRED_REACTIVE_LOAD_A_KEY, new Pair<String,Integer>(DewVariables.DEW_CMPCHN_HEADER, DewVariables.DEW_LOAD_MVAR_A_IDX));
    textFileIndicies.put(Load.DESIRED_REACTIVE_LOAD_B_KEY, new Pair<String,Integer>(DewVariables.DEW_CMPCHN_HEADER, DewVariables.DEW_LOAD_MVAR_B_IDX));
    textFileIndicies.put(Load.DESIRED_REACTIVE_LOAD_C_KEY, new Pair<String,Integer>(DewVariables.DEW_CMPCHN_HEADER, DewVariables.DEW_LOAD_MVAR_C_IDX));
    textFileIndicies.put(Load.TARGET_REAL_LOAD_A_KEY, new Pair<String,Integer>(DewVariables.DEW_CHNTARGETLDS_HEADER, DewVariables.DEW_TARGET_LOAD_KW_A_IDX));
    textFileIndicies.put(Load.TARGET_REAL_LOAD_B_KEY, new Pair<String,Integer>(DewVariables.DEW_CHNTARGETLDS_HEADER, DewVariables.DEW_TARGET_LOAD_KW_B_IDX));
    textFileIndicies.put(Load.TARGET_REAL_LOAD_C_KEY, new Pair<String,Integer>(DewVariables.DEW_CHNTARGETLDS_HEADER, DewVariables.DEW_TARGET_LOAD_KW_C_IDX));
    textFileIndicies.put(Load.TARGET_REACTIVE_LOAD_A_KEY, new Pair<String,Integer>(DewVariables.DEW_CHNTARGETLDS_HEADER, DewVariables.DEW_TARGET_LOAD_KVAR_A_IDX));
    textFileIndicies.put(Load.TARGET_REACTIVE_LOAD_B_KEY, new Pair<String,Integer>(DewVariables.DEW_CHNTARGETLDS_HEADER, DewVariables.DEW_TARGET_LOAD_KVAR_B_IDX));
    textFileIndicies.put(Load.TARGET_REACTIVE_LOAD_C_KEY, new Pair<String,Integer>(DewVariables.DEW_CHNTARGETLDS_HEADER, DewVariables.DEW_TARGET_LOAD_KVAR_C_IDX));    
    textFileIndicies.put(Asset.COORDINATE_KEY, new Pair<String,Integer>(DewVariables.DEW_CMP_HEADER, DewVariables.DEW_X_IDX));    
    textFileIndicies.put(ElectricPowerFlowConnection.CAPACITY_RATING_KEY, new Pair<String,Integer>(DewVariables.DEW_CMPRATING_HEADER, DewVariables.DEW_RATING1_IDX));
    textFileIndicies.put(ElectricPowerFlowConnection.LONG_TERM_EMERGENCY_CAPACITY_RATING_KEY, new Pair<String,Integer>(DewVariables.DEW_CMPRATING_HEADER, DewVariables.DEW_RATING2_IDX));
    textFileIndicies.put(ElectricPowerFlowConnection.SHORT_TERM_EMERGENCY_CAPACITY_RATING_KEY, new Pair<String,Integer>(DewVariables.DEW_CMPRATING_HEADER, DewVariables.DEW_RATING3_IDX));
    textFileIndicies.put(ElectricPowerFlowConnection.RESISTANCE_KEY, new Pair<String,Integer>(DewVariables.DEW_CMPACTDAT_HEADER, DewVariables.DEW_RESISTANCE_IDX));
    textFileIndicies.put(ElectricPowerFlowConnection.REACTANCE_KEY, new Pair<String,Integer>(DewVariables.DEW_CMPACTDAT_HEADER, DewVariables.DEW_REACTANCE_IDX));
    textFileIndicies.put(ElectricPowerFlowConnection.LINE_CHARGING_KEY, new Pair<String,Integer>(DewVariables.DEW_CMPRTS_HEADER, DewVariables.DEW_CHARGING_IDX));
    
    libraryIndicies.put(ElectricPowerFlowConnection.CAPACITY_RATING_A_KEY, new Pair<Integer,String>(DewVariables.APPID_RADIAL_POWER_FLOW, DewVariables.RATDAMPS));
    libraryIndicies.put(ElectricPowerFlowConnection.CAPACITY_RATING_B_KEY, new Pair<Integer,String>(DewVariables.APPID_RADIAL_POWER_FLOW, DewVariables.RATDAMPS));
    libraryIndicies.put(ElectricPowerFlowConnection.CAPACITY_RATING_C_KEY, new Pair<Integer,String>(DewVariables.APPID_RADIAL_POWER_FLOW, DewVariables.RATDAMPS));
    libraryIndicies.put(ElectricPowerFlowConnection.RESISTANCE_KEY, new Pair<Integer,String>(DewVariables.APPID_LINE_IMPEDANCE, DewVariables.ZDGRE));
    libraryIndicies.put(ElectricPowerFlowConnection.REACTANCE_KEY, new Pair<Integer,String>(DewVariables.APPID_LINE_IMPEDANCE, DewVariables.ZDGIM));
    libraryIndicies.put(DewVariables.DEW_DESIRED_REAL_LOAD_A_KEY, new Pair<Integer,String>(DewVariables.APPID_RADIAL_POWER_FLOW, DewVariables.PFFKW));
    libraryIndicies.put(DewVariables.DEW_DESIRED_REACTIVE_LOAD_A_KEY, new Pair<Integer,String>(DewVariables.APPID_RADIAL_POWER_FLOW, DewVariables.PFFKVAR));
    libraryIndicies.put(DewVariables.DEW_DESIRED_REAL_LOAD_B_KEY, new Pair<Integer,String>(DewVariables.APPID_RADIAL_POWER_FLOW, DewVariables.PFFKW));
    libraryIndicies.put(DewVariables.DEW_DESIRED_REACTIVE_LOAD_B_KEY, new Pair<Integer,String>(DewVariables.APPID_RADIAL_POWER_FLOW, DewVariables.PFFKVAR));
    libraryIndicies.put(DewVariables.DEW_DESIRED_REAL_LOAD_C_KEY, new Pair<Integer,String>(DewVariables.APPID_RADIAL_POWER_FLOW, DewVariables.PFFKW));
    libraryIndicies.put(DewVariables.DEW_DESIRED_REACTIVE_LOAD_C_KEY, new Pair<Integer,String>(DewVariables.APPID_RADIAL_POWER_FLOW, DewVariables.PFFKVAR));
    libraryIndicies.put(ElectricPowerFlowConnection.RESISTANCE_PHASE_A_KEY, new Pair<Integer,String>(DewVariables.APPID_LINE_IMPEDANCE, DewVariables.ZDGRE));
    libraryIndicies.put(ElectricPowerFlowConnection.RESISTANCE_PHASE_B_KEY, new Pair<Integer,String>(DewVariables.APPID_LINE_IMPEDANCE, DewVariables.ZDGRE));
    libraryIndicies.put(ElectricPowerFlowConnection.RESISTANCE_PHASE_C_KEY, new Pair<Integer,String>(DewVariables.APPID_LINE_IMPEDANCE, DewVariables.ZDGRE));
    libraryIndicies.put(ElectricPowerFlowConnection.RESISTANCE_PHASE_AB_KEY, new Pair<Integer,String>(DewVariables.APPID_LINE_IMPEDANCE, DewVariables.ZODRE));
    libraryIndicies.put(ElectricPowerFlowConnection.RESISTANCE_PHASE_BC_KEY, new Pair<Integer,String>(DewVariables.APPID_LINE_IMPEDANCE, DewVariables.ZODRE));
    libraryIndicies.put(ElectricPowerFlowConnection.RESISTANCE_PHASE_CA_KEY, new Pair<Integer,String>(DewVariables.APPID_LINE_IMPEDANCE, DewVariables.ZODRE));    
    libraryIndicies.put(ElectricPowerFlowConnection.REACTANCE_PHASE_A_KEY, new Pair<Integer,String>(DewVariables.APPID_LINE_IMPEDANCE, DewVariables.ZDGIM));
    libraryIndicies.put(ElectricPowerFlowConnection.REACTANCE_PHASE_B_KEY, new Pair<Integer,String>(DewVariables.APPID_LINE_IMPEDANCE, DewVariables.ZDGIM));
    libraryIndicies.put(ElectricPowerFlowConnection.REACTANCE_PHASE_C_KEY, new Pair<Integer,String>(DewVariables.APPID_LINE_IMPEDANCE, DewVariables.ZDGIM));
    libraryIndicies.put(ElectricPowerFlowConnection.REACTANCE_PHASE_AB_KEY, new Pair<Integer,String>(DewVariables.APPID_LINE_IMPEDANCE, DewVariables.ZODIM));
    libraryIndicies.put(ElectricPowerFlowConnection.REACTANCE_PHASE_BC_KEY, new Pair<Integer,String>(DewVariables.APPID_LINE_IMPEDANCE, DewVariables.ZODIM));
    libraryIndicies.put(ElectricPowerFlowConnection.REACTANCE_PHASE_CA_KEY, new Pair<Integer,String>(DewVariables.APPID_LINE_IMPEDANCE, DewVariables.ZODIM));
      
    libraryIndicies.put(Bus.VOLTAGE_PU_A_KEY, new Pair<Integer,String>(DewVariables.APPID_RADIAL_POWER_FLOW, DewVariables.VOLTMAGPU));
    libraryIndicies.put(Bus.VOLTAGE_PU_B_KEY, new Pair<Integer,String>(DewVariables.APPID_RADIAL_POWER_FLOW, DewVariables.VOLTMAGPU));
    libraryIndicies.put(Bus.VOLTAGE_PU_C_KEY, new Pair<Integer,String>(DewVariables.APPID_RADIAL_POWER_FLOW, DewVariables.VOLTMAGPU));

    libraryIndicies.put(Bus.PHASE_ANGLE_A_KEY, new Pair<Integer,String>(DewVariables.APPID_RADIAL_POWER_FLOW, DewVariables.VANGDG));
    libraryIndicies.put(Bus.PHASE_ANGLE_B_KEY, new Pair<Integer,String>(DewVariables.APPID_RADIAL_POWER_FLOW, DewVariables.VANGDG));
    libraryIndicies.put(Bus.PHASE_ANGLE_C_KEY, new Pair<Integer,String>(DewVariables.APPID_RADIAL_POWER_FLOW, DewVariables.VANGDG));
    libraryIndicies.put(Generator.ACTUAL_REAL_GENERATION_A_KEY, new Pair<Integer,String>(DewVariables.APPID_RADIAL_POWER_FLOW, DewVariables.PFFKW));
    libraryIndicies.put(Generator.ACTUAL_REAL_GENERATION_B_KEY, new Pair<Integer,String>(DewVariables.APPID_RADIAL_POWER_FLOW, DewVariables.PFFKW));
    libraryIndicies.put(Generator.ACTUAL_REAL_GENERATION_C_KEY, new Pair<Integer,String>(DewVariables.APPID_RADIAL_POWER_FLOW, DewVariables.PFFKW));
    libraryIndicies.put(Generator.ACTUAL_REACTIVE_GENERATION_A_KEY, new Pair<Integer,String>(DewVariables.APPID_RADIAL_POWER_FLOW, DewVariables.PFFKVAR));
    libraryIndicies.put(Generator.ACTUAL_REACTIVE_GENERATION_B_KEY, new Pair<Integer,String>(DewVariables.APPID_RADIAL_POWER_FLOW, DewVariables.PFFKVAR));
    libraryIndicies.put(Generator.ACTUAL_REACTIVE_GENERATION_C_KEY, new Pair<Integer,String>(DewVariables.APPID_RADIAL_POWER_FLOW, DewVariables.PFFKVAR));
    libraryIndicies.put(Load.ACTUAL_REAL_LOAD_A_KEY, new Pair<Integer,String>(DewVariables.APPID_RADIAL_POWER_FLOW, DewVariables.PFFKW));
    libraryIndicies.put(Load.ACTUAL_REAL_LOAD_B_KEY, new Pair<Integer,String>(DewVariables.APPID_RADIAL_POWER_FLOW, DewVariables.PFFKW));
    libraryIndicies.put(Load.ACTUAL_REAL_LOAD_C_KEY, new Pair<Integer,String>(DewVariables.APPID_RADIAL_POWER_FLOW, DewVariables.PFFKW));
    libraryIndicies.put(Load.ACTUAL_REACTIVE_LOAD_A_KEY, new Pair<Integer,String>(DewVariables.APPID_RADIAL_POWER_FLOW, DewVariables.PFFKVAR));
    libraryIndicies.put(Load.ACTUAL_REACTIVE_LOAD_B_KEY, new Pair<Integer,String>(DewVariables.APPID_RADIAL_POWER_FLOW, DewVariables.PFFKVAR));
    libraryIndicies.put(Load.ACTUAL_REACTIVE_LOAD_C_KEY, new Pair<Integer,String>(DewVariables.APPID_RADIAL_POWER_FLOW, DewVariables.PFFKVAR));
    libraryIndicies.put(ElectricPowerFlowConnection.MW_FLOW_PHASE_A_KEY, new Pair<Integer,String>(DewVariables.APPID_RADIAL_POWER_FLOW, DewVariables.PFFKW));
    libraryIndicies.put(ElectricPowerFlowConnection.MW_FLOW_PHASE_B_KEY, new Pair<Integer,String>(DewVariables.APPID_RADIAL_POWER_FLOW, DewVariables.PFFKW));
    libraryIndicies.put(ElectricPowerFlowConnection.MW_FLOW_PHASE_C_KEY, new Pair<Integer,String>(DewVariables.APPID_RADIAL_POWER_FLOW, DewVariables.PFFKW));
    libraryIndicies.put(ElectricPowerFlowConnection.MVAR_FLOW_PHASE_A_KEY, new Pair<Integer,String>(DewVariables.APPID_RADIAL_POWER_FLOW, DewVariables.PFFKVAR));
    libraryIndicies.put(ElectricPowerFlowConnection.MVAR_FLOW_PHASE_B_KEY, new Pair<Integer,String>(DewVariables.APPID_RADIAL_POWER_FLOW, DewVariables.PFFKVAR));
    libraryIndicies.put(ElectricPowerFlowConnection.MVAR_FLOW_PHASE_C_KEY, new Pair<Integer,String>(DewVariables.APPID_RADIAL_POWER_FLOW, DewVariables.PFFKVAR));
	}
	
	public DewVariables() {
		init();
	}
	
	private void init() {
		
		// stuff we want to put into maps
		int[] ids = { APPID_LINE_IMPEDANCE, APPID_LOAD_ESTIMATION, APPID_RADIAL_POWER_FLOW,
				APPID_NETWORK_FAULT_ANALYSIS, APPID_SECONDARY_FAULT_ANALYSIS,
				APPID_FALOCATOR, APPID_SECONDARY_FLICKER, APPID_SYSOPTZR,
				APPID_DR_CONTROL, APPID_SYSTEM_LOAD };
		
		// create map to appId indices
		for(int j=0; j<ids.length; j++) appIdIndices.put(ids[j], j);
		
		// create map to variable indices
		for(int j=0; j<ids.length; j++) {
			int appId = ids[j];
			Map<String,Integer> map = new HashMap<String,Integer>();
			for(int i=0; i<vars[j].length; i++) map.put(vars[j][i], i);
			variableIndices.put(appId, map);
		}
	}
	
	/**
	 * @param appId The analysis type.
	 * @param variable The name of  the variable.
	 * @return The index of the specified variable.
	 * @throws IllegalArgumentException
	 */
	public int getVariableIndex(int appId, String variable) throws IllegalArgumentException {
		if(!variableIndices.containsKey(appId))
			throw new IllegalArgumentException("Unrecognized application/analysis type ID.");
		else if(!variableIndices.get(appId).containsKey(variable))
			throw new IllegalArgumentException("Variable name, "+variable+", is not valid for the given application/analysis type.");
		else
			return variableIndices.get(appId).get(variable);
	}
	
	/**
	 * Get list of valid variable names for a specified application/analysis type.
	 * @param appId The application/analysis type
	 * @return An array of valid variable names.
	 */
	public String[] getVariables(int appId) throws IllegalArgumentException {
		if(!appIdIndices.containsKey(appId))
			throw new IllegalArgumentException("Unrecognized application/analysis type ID.");
		return vars[appIdIndices.get(appId)];
	}
	
	public final String[][] vars = { lineImpedenceVariables, loadEstimationVariables, radialPowerFlowVariables,
			networkFaultVariables, secondaryFaultVaribles,
			falocatorVariables, secondaryFlickerVariables, sysOptimizerVariables,
			drControlVariables, systemLoadVariables };
	
  public static final String ZDGRE = "ZDgRe";
  public static final String ZDGIM = "ZDgIm";
  public static final String ZODRE = "ZOfDgRe";
  public static final String ZODIM = "ZOfDgIm";
	
	public final static String[] lineImpedenceVariables =
		{ 	ZDGRE, ZDGIM , "ZOfDgRe" , "ZOfDgIm" ,
        	"YDgRe", "YDgIm" , "YOfDgRe" , "YOfDgIm" };
	
	public static final String LDKW = "LdKw";
  public static final String LDKVAR = "LdKvar";

  public static final String MKWS = "mkWs";
  public static final String MKVARS = "mKVars";
  
	public final static String[] loadEstimationVariables =
		{	LDKW    , LDKVAR , "FKw"   , "FKvar"  , 
        	"NomKv"   , "CktMeas", "kWZnID", "kW_SF"  , 
        	"kVrZnID" , "kVr_SF" , "Zn_kWs", "Zn_kVrs", 
        	"mAmps"   , "mkWs"   , "mKVars", "CmpLdID", 
        	"LdPwFact", "LdScld" , "Est_kVA" };
	
	public final static String RATDAMPS = "RatdAmps";
	public final static String PFFKW    = "Pf_FKw";
	public final static String PFFKVAR  = "Pf_FKvar";
	public final static String VMAGKV   = "VMagKv";
	public final static String VANGDG   = "VAngDg";
	public final static String VOLTMAGPU = "VoltMagPu";
	
	public final static String[] radialPowerFlowVariables =
		{	     VMAGKV        , VANGDG        , 
        	"IMagA"        , "IAngDg"      , 
        	"SeqVMag_kV"   , "SeqVAng_Deg" , 
        	"SeqIMag_A"    , "SeqIAng_Deg" , 
        	 PFFKW         , PFFKVAR       , 
        	"PowFact"      , "PhImbal"     , 
        	"CustVlts"     , "CalcdLd_kW"  , 
        	"CalcdLd_kVar" , "IMagChA"     , 
        	"FChKVA"       , "LssWatt"     , 
        	"LssVar"       , RATDAMPS    , 
        	"RAvCap_A"     , "PrcAvCAP"    , 
        	"Tot_fkWs"     , "Avg_fkWs"    , 
        	"TotfkVrs"     , "AvgfkVrs"    , 
        	"Pf_KVA"       , "LLVMgkV"    ,
        	// these are undefined... rbent just did some pecking around for the 
        	// with larger indices to see what came back....
        	"unknown1"     , "unknown2",
        	"unknown3"     , "unknown4",
        	"unknown5"     , "unknown6",
        	"unknown7"     , "unknown8",
        	"unknown8"     , VOLTMAGPU
		};
	
	public final static String[] networkFaultVariables =
		{   "MAX_FAmps" ,   "MIN_FAmps" , 
        	"3PhZ0FA"   ,   "3PhZgFA"   ,
        	"1PhZ0FA"   ,   "1PhZgFA" ,
        	"PhToPhFA"  ,   "ZTh1"    ,
        	"ZTh0"      ,   "Ref3Ph0" ,
        	"Ref3PhZ"   ,   "Ref1Ph0" ,
        	"Ref1PhZ"   ,   "RefPhPh" ,
        	"kV3Ph0"    ,   "kV3PhZ"  , 
        	"kV2PhAB"   ,   "kV2PhBC" ,
        	"kV2PhCA"   ,   "kV1PhA0" ,
        	"kV1PhB0"   ,   "kV1PhC0" ,
        	"kV1PhAZ"   ,   "kV1PhBZ" ,
			"kV1PhCZ"                   };
	
	public final static String[] secondaryFaultVaribles =
		{	"MaxSd_FA"  , "MinSd_FA"  };
			
	public final static String[] falocatorVariables =
		{ 	"EstFaX"  , "EstFaR"  , "fProtDev"  , "fFaultLoc"  };
	
	public final static String[] secondaryFlickerVariables =
		{ 	"LimitAmps"  , "StrtAmps", "PcntVDip", "FlkrIndc" };
	
	public final static String[] sysOptimizerVariables =
		{ 	"OrgRtdAmps" , "OrgRtdkVA" , "OrgPartID" ,
        	"OpZRtdAmps" , "OpZRtdkVA" , "OpZPartID" ,
        	"CmpOptiZmd" , "dMaxLdPrc" , "TypCondID"  };
	
	public final static String[] drControlVariables = { "needToBeChanged" };
	
	public final static String[] systemLoadVariables =
		{ 	"iTmPtr", "kw_hr0", "kw_hr1","kw_hr2", "kw_hr3","kw_hr4", "kw_hr5","kw_hr6", 
        	"kw_hr7","kw_hr8","kw_hr9", "kw_hr10","kw_hr11", "kw_hr12","kw_hr13", 
        	"kw_hr14","kw_hr15", "kw_hr16","kw_h17", "kw_hr18","kw_hr19", "kw_hr20","kw_hr21", 
        	"kw_hr22","kw_h23","kvar_hr0", "kvar_hr1","kvar_hr2", "kvar_hr3","kvar_hr4", "kvar_hr5",
        	"kvar_hr6", "kvar_hr7","kvar_hr8","kvar_hr9", "kvar_hr10","kvar_hr11", 
        	"kvar_hr12","kvar_hr13", "kvar_hr14","kvar_hr15", "kvar_hr16","kvar_h17", "kvar_hr18",
        	"kvar_hr19", "kvar_hr20","kvar_hr21", "kvar_hr22","kvar_h23" };
	
	public final static int APPID_BUILD_CIRCUITS = 2;
	public final static int APPID_LINE_IMPEDANCE = 4;
	public final static int APPID_LOAD_ESTIMATION = 6;
	public final static int APPID_LOAD_FORECASTING = 8;
	public final static int APPID_MODEL_VALIDATION = 9;
	public final static int APPID_MISSION_PRIORITY = 10;
	public final static int APPID_RADIAL_POWER_FLOW = 12;
	public final static int APPID_FLUID_FLOW_ANALYSIS = 14;
	public final static int APPID_CONFIG_CONTROL = 15;
	public final static int APPID_CIRCUITVIEWER = 17;
	public final static int APPID_NETWORK_FAULT_ANALYSIS = 18;
	public final static int APPID_SECONDARY_FAULT_ANALYSIS = 20;
	public final static int APPID_FALOCATOR = 21;
	public final static int APPID_RELIABILITY_ANALYSIS = 22;
	public final static int APPID_OUTAGE_DISPLAY = 24;
	public final static int APPID_FEEDER_PERFORMANCE = 25;
	public final static int APPID_CONTINGENCY_ANALYSIS = 26;
	public final static int APPID_MONTECARLO_SIMULATION = 27;
	public final static int APPID_FLICKER_ANALYSIS = 28;
	public final static int APPID_SECONDARY_FLICKER = 30;
	public final static int APPID_LIGHTNING_ANALYSIS = 32;
	public final static int APPID_SYSOPTZR = 33;
	public final static int APPID_DR_FUSE_CHECKER = 34;
	public final static int APPID_DR_CONTROL = 36;
	public final static int APPID_HARMONIC_IMPEDANCE_ANALYSIS = 38;
	public final static int APPID_CAPACITOR_DESIGN = 40;
	public final static int APPID_PHASE_BALANCING = 42;
	public final static int APPID_PHASE_PREDICTION = 44;
	public final static int APPID_PROTECTION_SYSTEM_DESIGN = 46;
	public final static int APPID_RECONFIGURATION_RESTORATION = 48;
	public final static int APPID_DR_SUPPORT_PENETRATION = 49;
	public final static int APPID_MINIMUM_LOSS = 50;
	public final static int APPID_REVENUE_ANALYSIS = 51;
	public final static int APPID_ECONMIC_ANALYSIS = 52;
	public final static int APPID_GIS_MAP_INFO = 54;
	public final static int APPID_SYSTEM_LOAD = 56;
	public final static int APPID_GEN_FLOW = 57;
	public final static int APPID_GEN_FLOW_X = 58;
	public final static int APPID_CONTINGENCY = 59;
	public final static int APPID_USER_ANALYSIS = 60;
	public final static int MODID_MAIN = 61;
	public final static int MODID_CKTTOOL = 62;
	public final static int MODID_CTR = 63;
	public final static int MODID_DBINIT = 64;
	public final static int MODID_DBREAD = 65;
	public final static int MODID_DBWRITE = 66;
	public final static int MODID_SYFIL = 67;
	public final static int MODID_TOOLS = 68;
	public final static int MODID_PLNMODS = 69;
	public final static int MODID_PROP = 70;
	public final static int MODID_CKT_BUILD = 72;
	public final static int MODID_CMPEXT = 73;
	public final static int MODID_CUSTOM_TOOLS = 74;
	public final static int MODID_DTEMILFORD = 75;
		
	// constants associated with File I/O
	
  public static final String DEW_LEGACY_ID_KEY                            = "DEW_LEGACY_ID";  
  public static final String DEW_COMPONENT_TYPE_KEY                       = "DEW_COMPONENT_TYPE"; // mark the type of DEW object
  public static final String DEW_LEGACY_IDS_KEY                           = "DEW_LEGACY_IDS";
  public static final String DEW_SUBSTATION_KEY                           = "DEW_SUBSTATION";
  public static final String DEW_CONNECTION_KEY                           = "DEW_CONNECTION";
  public static final String DEW_CONNECTION1_KEY                          = "DEW_CONNECTION1";
  public static final String DEW_CONNECTION2_KEY                          = "DEW_CONNECTION2";
  public static final String DEW_FEEDER_CONNECTION_KEY                    = "DEW_FEEDER_CONNECTION";
  public static final String DEW_FEEDER_CONNECTION_ID_KEY                 = "DEW_FEEDER_CONNECTION_ID";  
  public static final String DEW_X_KEY                                    = "DEW_X"; 
  public static final String DEW_Y_KEY                                    = "DEW_Y"; 
  public static final String DEW_PHASES_KEY                               = "DEW_PHASES"; 
  public static final String DEW_GENERATOR_NAME_KEY                       = "DEW_GENERATOR_NAME"; 
  public static final String DEW_LOAD_NAME_KEY                            = "DEW_LOAD_NAME"; 
  public static final String DEW_BUS_NAME_KEY                             = "DEW_BUS_NAME"; 
  public static final String DEW_SHUNT_NAME_KEY                           = "DEW_SHUNT_NAME"; 
  public static final String DEW_DESIRED_REAL_LOAD_A_KEY                  = "DEW_DESIRED_REAL_LOAD_A";
  public static final String DEW_DESIRED_REAL_LOAD_B_KEY                  = "DEW_DESIRED_REAL_LOAD_B";
  public static final String DEW_DESIRED_REAL_LOAD_C_KEY                  = "DEW_DESIRED_REAL_LOAD_C";
  public static final String DEW_DESIRED_REACTIVE_LOAD_A_KEY              = "DEW_DESIRED_REACTIVE_LOAD_A";
  public static final String DEW_DESIRED_REACTIVE_LOAD_B_KEY              = "DEW_DESIRED_REACTIVE_LOAD_B";
  public static final String DEW_DESIRED_REACTIVE_LOAD_C_KEY              = "DEW_DESIRED_REACTIVE_LOAD_C";
  public static final String DEW_DATABASE_PTROW_KEY                       = "DEW_DATABASE_PTROW";
  public static final String DEW_DATABASE_PHASE_CONDUCTOR_KEY             = "DEW_DATABASE_PHASE_CONDUCTOR";
  public static final String DEW_DATABASE_NEUTRAL_CONDUCTOR_KEY           = "DEW_DATABASE_NEUTRAL_CONDUCTOR";
  public static final String DEW_DATABASE_XFRM_KEY                        = "DEW_DATABASE_PARTID";
  public static final String DEW_DATABASE_CAP_KEY                         = "DEW_DATABASE_PARTID";  
  public static final String DEW_FEEDER_NAME_KEY                          = "DEW_FEEDER_NAME";
  public static final String DEW_FEEDER_SERIAL_KEY                        = "DEW_FEEDER_SERIAL";
  
  public static final int NO_DEW_TYPE                                     = -1;
   
  public static final int DEW_TYPE_IDX                                    = 18;
  public static final int DEW_HEADER_IDX                                  = 0;
  public static final int DEW_ID_IDX                                      = 1;
  public static final int DEW_X_IDX                                       = 13;
  public static final int DEW_Y_IDX                                       = 14;
  public static final int DEW_FAIL_IDX                                    = 4;
  public static final int DEW_BASEKV_IDX                                  = 1;
  public static final int DEW_STATUS_IDX                                  = 17;
  public static final int DEW_NAME_IDX                                    = 1;
  public static final int DEW_SHUNT_NAME_IDX                              = 1;
  public static final int DEW_NUMPHASES_IDX                               = 8;
  public static final int DEW_PHASES_IDX                                  = 7;
  public static final int DEW_CONNECTION_IDX                              = 32;
  public static final int DEW_SUBSTATION_IDX                              = 3;
  public static final int DEW_CONNECTION1_IDX                             = 35;
  public static final int DEW_CONNECTION2_IDX                             = 36;
  public static final int DEW_FEEDER_CONNECTION_IDX                       = 33;
  public static final int DEW_FEEDER_CONNECTION_ID_IDX                    = 34;
  public static final int DEW_GENERATOR_NAME_IDX                          = 1;
  public static final int DEW_BUS_NAME_IDX                                = 1;
  public static final int DEW_LOAD_NAME_IDX                               = 1;
  public static final int DEW_MW_GEN_IDX                                  = 17;
  public static final int DEW_MVAR_MIN_GEN_IDX                            = 14;
  public static final int DEW_MVAR_MAX_GEN_IDX                            = 15;
  public static final int DEW_VOLTAGE_GEN_IDX                             = 18;
  public static final int DEW_MVAR_GEN_IDX                                = 5;
  public static final int DEW_LOAD_MW_A_IDX                               = 21;
  public static final int DEW_LOAD_MVAR_A_IDX                             = 22;
  public static final int DEW_LOAD_MW_B_IDX                               = 23;
  public static final int DEW_LOAD_MVAR_B_IDX                             = 24;
  public static final int DEW_LOAD_MW_C_IDX                               = 25;
  public static final int DEW_LOAD_MVAR_C_IDX                             = 26;
  public static final int DEW_LINE_FIRST_X_IDX                            = 38;
  public static final int DEW_LINE_FIRST_Y_IDX                            = 39;
  public static final int DEW_LINE_LAST_X_IDX                             = 40;
  public static final int DEW_LINE_LAST_Y_IDX                             = 41;
  public static final int DEW_LINE_INTERNAL_X_IDX                         = 1;
  public static final int DEW_LINE_INTERNAL_Y_IDX                         = 2;
  public static final int DEW_RESISTANCE_IDX                              = 1;
  public static final int DEW_REACTANCE_IDX                               = 2;
  public static final int DEW_CHARGING_IDX                                = 4;
  public static final int DEW_CMPRTS_INDICATOR_IDX                        = 2;
  public static final int DEW_RATING1_IDX                                 = 1;
  public static final int DEW_RATING2_IDX                                 = 2;
  public static final int DEW_RATING3_IDX                                 = 3;
  public static final int DEW_DATABASE_PTROW_IDX                          = 6;
  public static final int DEW_DATABASE_PHASE_CONDUCTOR_IDX                = 4;
  public static final int DEW_DATABASE_NEUTRAL_CONDUCTOR_IDX              = 5;
  public static final int DEW_DATABASE_XFRM_IDX                           = 6;
  public static final int DEW_TARGET_LOAD_KW_A_IDX                        = 1;
  public static final int DEW_TARGET_LOAD_KVAR_A_IDX                      = 2;
  public static final int DEW_TARGET_LOAD_KW_B_IDX                        = 3;
  public static final int DEW_TARGET_LOAD_KVAR_B_IDX                      = 4;
  public static final int DEW_TARGET_LOAD_KW_C_IDX                        = 5;
  public static final int DEW_TARGET_LOAD_KVAR_C_IDX                      = 6;
 
  public static final String DEW_CMP_HEADER                               = "$CMP";
  public static final String DEW_CMP_CHN_HEADER                           = "$CMPCHN";
  public static final String DEW_CMPSERIALNUM_HEADER                      = "$CMPSERIALNUM";
  public static final String DEW_CMPOPER_HEADER                           = "$CMPOPER";
  public static final String DEW_CMPINPSCRDATA_HEADER                     = "$CMPINPSCRDATA";
  public static final String DEW_CMPNAM_HEADER                            = "$CMPNAM";
  public static final String DEW_CMPCTR_HEADER                            = "$CMPCTR";
  public static final String DEW_CMPCHN_HEADER                            = "$CMPCHN";
  public static final String DEW_CHNTARGETLDS_HEADER                      = "$CHNTARGETLDS"; 
  public static final String DEW_CMPXY_HEADER                             = "$CMPXY";
  public static final String DEW_CMPACTDAT_HEADER                         = "$CMPACTDAT";
  public static final String DEW_CMPRTS_HEADER                            = "$CMPRTS";
  public static final String DEW_CMPRATING_HEADER                         = "$CMPRATING";
  public static final String DEW_IS_LINE_CHARGING_HEADER                  = "-3";

  public static final String DEW_AGGREGATE_STATUS_KEY                     = "DEW_AGGREGATE_STATUS";
  public static final String DEW_AGGREGATE_FAILED_KEY                     = "DEW_AGGREGATE_FAILED"; 
	
}
