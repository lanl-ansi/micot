package gov.lanl.micot.infrastructure.ep.optimize.dcopf;

import java.util.Collection;

import gov.lanl.micot.infrastructure.ep.optimize.dcopf.assignment.LoadVariableAssignmentFactory;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.constraint.LoadConstraint;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.constraint.VariableLoadFlowBalanceConstraint;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.objective.LoadObjectiveFunctionFactory;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.variable.LoadVariableFactory;
import gov.lanl.micot.infrastructure.optimize.OptimizerFlags;
import gov.lanl.micot.infrastructure.optimize.mathprogram.assignment.AssignmentFactory;
import gov.lanl.micot.infrastructure.optimize.mathprogram.constraint.ConstraintFactory;
import gov.lanl.micot.infrastructure.optimize.mathprogram.objective.ObjectiveFunctionFactory;
import gov.lanl.micot.infrastructure.optimize.mathprogram.variable.VariableFactory;

/**
 * Flags used for OPF DC solvers using the multiple scenario
 * robust solver.  These flags using a cutting style algorithm
 * based on discussions with Dan Bienstock
 * @author Russell Bent
 */
public class LoadShedOptimalPowerFlowFlags extends OptimalPowerFlowFlags {

	private static final long serialVersionUID = 1L;

  private static final String DEFAULT_MATH_PROGRAM = "gov.lanl.micot.util.math.solver.linearprogram.cplex.CPLEXLinearProgram";
	
	public LoadShedOptimalPowerFlowFlags() {
		super();
		put(MATH_PROGRAM_FACTORY_KEY, DEFAULT_MATH_PROGRAM);    
	}

	/**
	 * Constructor
	 * @param flags
	 */
	public LoadShedOptimalPowerFlowFlags(OptimizerFlags flags) {
	  super(flags);
	  if (!flags.containsKey(MATH_PROGRAM_FACTORY_KEY)) {
	    put(MATH_PROGRAM_FACTORY_KEY, DEFAULT_MATH_PROGRAM);
	  } 
	}
			
  @Override
  @SuppressWarnings("rawtypes")
  protected Collection<Class<? extends VariableFactory>> getDefaultVariableFactories() {
    Collection<Class<? extends VariableFactory>> defaults = super.getDefaultVariableFactories();
    defaults.add(LoadVariableFactory.class);
    return defaults;
  }

  @Override
  @SuppressWarnings("rawtypes")
  protected Collection<Class<? extends ConstraintFactory>> getDefaultConstraintFactories() {
    Collection<Class<? extends ConstraintFactory>> defaults = super.getDefaultConstraintFactories();
    defaults.add(LoadConstraint.class);
    defaults.add(VariableLoadFlowBalanceConstraint.class);
    return defaults;

  }

  @Override
  @SuppressWarnings("rawtypes")
  protected Collection<Class<? extends AssignmentFactory>> getDefaultAssignmentFactories() {
    Collection<Class<? extends AssignmentFactory>> defaults = super.getDefaultAssignmentFactories();
    defaults.add(LoadVariableAssignmentFactory.class);
    return defaults;
  }

  @Override
  @SuppressWarnings("rawtypes")
  protected Collection<Class<? extends ObjectiveFunctionFactory>> getDefaultObjectiveFunctionFactories() {
    Collection<Class<? extends ObjectiveFunctionFactory>> defaults = super.getDefaultObjectiveFunctionFactories();
    defaults.add(LoadObjectiveFunctionFactory.class);
    return defaults;
  }

	 
}
