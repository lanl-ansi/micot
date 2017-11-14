package gov.lanl.micot.application.lpnorm.io;


/**
 * Some constants associated with interacting with matpower
 * @author Russell Bent
 *
 */
public class LPNormIOConstants {

  public static final String BUSES_TAG = "buses";
  public static final String BUS_ID_TAG = "id";
  public static final String BUS_LONGITUDE_TAG = "x";
  public static final String BUS_LATITUDE_TAG = "y";
  public static final String BUS_MIN_VOLTAGE_TAG = "min_voltage";
  public static final String BUS_MAX_VOLTAGE_TAG = "max_voltage";
  public static final String BUS_VOLTAGE_TAG = "ref_voltage";
  public static final String BUS_HAS_PHASE_TAG = "has_phase";

  public static final String LOADS_TAG = "loads";
  public static final String LOAD_ID_TAG = "id";
  public static final String LOAD_BUS_ID_TAG = "node_id";
  public static final String LOAD_HAS_PHASE_TAG = "has_phase";
  public static final String LOAD_REAL_TAG = "max_real_phase";
  public static final String LOAD_REACTIVE_TAG = "max_reactive_phase";
  public static final String LOAD_IS_CRITICAL_LOAD_TAG = "is_critical";
  
  public static final String GENERATORS_TAG = "generators";
  public static final String GEN_ID_TAG = "id";
  public static final String GEN_BUS_ID_TAG = "node_id";
  public static final String GEN_HAS_PHASE_TAG = "has_phase";
  public static final String GEN_REAL_TAG = "max_real_phase";
  public static final String GEN_REACTIVE_TAG = "max_reactive_phase";
  public static final String GEN_MICROGRID_COST_TAG = "microgrid_cost";
  public static final String GEN_MICROGRID_FIXED_COST_TAG = "microgrid_fixed_cost";
  public static final String GEN_MICROGRID_MAX_TAG = "max_microgrid";
  public static final String GEN_MICROGRID_IS_NEW_TAG = "is_new";

  public static final String LINE_CODES_TAG = "line_codes";
  public static final String LINE_CODE_ID_TAG = "line_code";
  public static final String LINE_CODE_PHASES_TAG = "num_phases";
  public static final String LINE_CODE_RMATRIX_TAG = "rmatrix";
  public static final String LINE_CODE_XMATRIX_TAG = "xmatrix";
  
  public static final String LINES_TAG = "lines";
  public static final String LINE_ID_TAG = "id";
  public static final String LINE_BUS1_ID_TAG = "node1_id";
  public static final String LINE_BUS2_ID_TAG = "node2_id";
  public static final String LINE_HAS_PHASE_TAG = "has_phase";
  public static final String LINE_CAPACITY_TAG = "capacity";
  public static final String LINE_LENGTH_TAG = "length";
  public static final String LINE_NUM_PHASES_TAG = "num_phases";
  public static final String LINE_IS_TRANSFORMER_TAG = "is_transformer";
  public static final String LINE_LINE_CODE_TAG = "line_code";
  public static final String LINE_NUM_POLES_TAG = "num_poles";
  public static final String LINE_CONSTRUCTION_COST_TAG = "construction_cost";
  public static final String LINE_HARDEN_COST_TAG = "harden_cost";
  public static final String LINE_SWITCH_COST_TAG = "switch_cost";
  public static final String LINE_IS_NEW_TAG = "is_new";
  public static final String LINE_CAN_HARDEN_TAG = "can_harden";
  public static final String LINE_CAN_ADD_SWITCH = "can_add_switch";
  public static final String LINE_HAS_SWITCH_TAG = "has_switch";
  
  public static final String CRITICAL_LOAD_MET_TAG = "critical_load_met";
  public static final String TOTAL_LOAD_MET_TAG    = "total_load_met";
  public static final String CHANCE_CONSTRAINT_TAG = "chance_constraint";
  public static final String PHASE_VARIATION_TAG   = "phase_variation";
  public static final String POWER_FLOW_TAG        = "power_flow";
  
  public static final String SCENARIOS_TAG = "scenarios";
  public static final String SCENARIO_ID_TAG = "id";
  public static final String SCENARIO_HARDENED_DISABLED_TAG = "hardened_disabled_lines";
  public static final String SCENARIO_DISABLED_TAG = "disable_lines";

  public static final String MVA_BASE_TAG = "mva_base";
  
  public static final String ALGORITHM_TAG = "algorithm";
  public static final String MIQP_TAG = "miqp";
  public static final String SBD_TAG = "sbd";
  public static final String VNS_TAG = "vns";
  
  public static final String SOLVER_TAG = "solver";
  public static final String CPLEX_TAG = "cplex";
  public static final String SCIP_TAG = "scip";
  public static final String BONMIN_TAG = "bonmin";

  
}
