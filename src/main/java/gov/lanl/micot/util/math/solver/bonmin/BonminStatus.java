package gov.lanl.micot.util.math.solver.bonmin;

/**
 * Enum of the different solver status in Bonmin
 * @author Russell Bent
 *
 */
public enum BonminStatus {  
      BONMIN_STATUS_SUCCEES,
      BONMIN_STATUS_INFEASIBLE,
      BONMIN_STATUS_CONTINUOUS_UNBOUNDED,
      BONMIN_STATUS_LIMIT_EXCEEDED,
      BONMIN_STATUS_MINLP_ERROR
}
