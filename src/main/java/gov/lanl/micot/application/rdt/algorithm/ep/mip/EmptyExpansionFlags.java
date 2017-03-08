package gov.lanl.micot.application.rdt.algorithm.ep.mip;

import java.util.ArrayList;
import java.util.Collection;

import gov.lanl.micot.infrastructure.optimize.OptimizerFlags;
import gov.lanl.micot.infrastructure.optimize.mathprogram.assignment.AssignmentFactory;
import gov.lanl.micot.infrastructure.optimize.mathprogram.constraint.ConstraintFactory;
import gov.lanl.micot.infrastructure.optimize.mathprogram.objective.ObjectiveFunctionFactory;
import gov.lanl.micot.infrastructure.optimize.mathprogram.variable.VariableFactory;
import gov.lanl.micot.application.rdt.algorithm.AlgorithmConstants;

/**
 * Flags used for for the no optimization option
 * 
 * @author Russell Bent
 */
public class EmptyExpansionFlags extends MIPInfrastructureExpansionAlgorithmFlags {

	private static final long serialVersionUID = 1L;


	/**
	 * Constructor
	 */
	public EmptyExpansionFlags() {
		super();
		put(MATH_PROGRAM_FACTORY_KEY, "gov.lanl.micot.util.math.solver.integerprogram.cplex.CPLEXIntegerProgramFactory");
		put(AlgorithmConstants.LOAD_MET_KEY, AlgorithmConstants.DEFAULT_LOAD_MET);
		put(AlgorithmConstants.CRITICAL_LOAD_MET_KEY, AlgorithmConstants.DEFAULT_CRITICAL_LOAD_MET);
	}

	/**
	 * Constructor
	 * 
	 * @param flags
	 */
	public EmptyExpansionFlags(OptimizerFlags flags) {
		super(flags);
		put(MATH_PROGRAM_FACTORY_KEY, "gov.lanl.micot.util.math.solver.integerprogram.cplex.CPLEXIntegerProgramFactory");
		if (get(AlgorithmConstants.LOAD_MET_KEY) == null) {
			put(AlgorithmConstants.LOAD_MET_KEY, AlgorithmConstants.DEFAULT_LOAD_MET);
		}
		if (get(AlgorithmConstants.CRITICAL_LOAD_MET_KEY) == null) {
			put(AlgorithmConstants.CRITICAL_LOAD_MET_KEY, AlgorithmConstants.DEFAULT_CRITICAL_LOAD_MET);
		}
	}

	@Override
  @SuppressWarnings("rawtypes")
  protected Collection<Class<? extends VariableFactory>> getDefaultVariableFactories() {
    ArrayList<Class<? extends VariableFactory>> defaults = new ArrayList<Class<? extends VariableFactory>>();
    return defaults;
  }

  @Override
  @SuppressWarnings("rawtypes")
  protected Collection<Class<? extends ConstraintFactory>> getDefaultConstraintFactories() {
    ArrayList<Class<? extends ConstraintFactory>> defaults = new ArrayList<Class<? extends ConstraintFactory>>();    
    return defaults;
  }

  @Override
  @SuppressWarnings("rawtypes")
  protected Collection<Class<? extends AssignmentFactory>> getDefaultAssignmentFactories() {
    ArrayList<Class<? extends AssignmentFactory>> defaults = new ArrayList<Class<? extends AssignmentFactory>>();
    return defaults;
  }

  @Override
  @SuppressWarnings("rawtypes")
  protected Collection<Class<? extends ObjectiveFunctionFactory>> getDefaultObjectiveFunctionFactories() {
    ArrayList<Class<? extends ObjectiveFunctionFactory>> defaults = new ArrayList<Class<? extends ObjectiveFunctionFactory>>();
    return defaults;
  }
}
