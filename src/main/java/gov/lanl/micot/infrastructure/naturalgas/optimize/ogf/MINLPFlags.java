package gov.lanl.micot.infrastructure.naturalgas.optimize.ogf;

import java.util.ArrayList;
import java.util.Collection;

import gov.lanl.micot.infrastructure.naturalgas.optimize.ogf.constraint.scenario.WeymouthScenarioConstraint;
import gov.lanl.micot.infrastructure.optimize.OptimizerFlags;
import gov.lanl.micot.infrastructure.optimize.mathprogram.assignment.AssignmentFactory;
import gov.lanl.micot.infrastructure.optimize.mathprogram.constraint.ConstraintFactory;
import gov.lanl.micot.infrastructure.optimize.mathprogram.objective.ObjectiveFunctionFactory;
import gov.lanl.micot.infrastructure.optimize.mathprogram.variable.VariableFactory;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgramFlags;

/**
 * Flags used for solving the OGF problem via a full-MINLP formulation
 *
 * @author Conrado Borraz Modified/extended -and more comments added- since Sep 2014.
 */
public class MINLPFlags extends DefaultFlags {
  private static final long serialVersionUID = 1L;

  private static final double DEFAULT_TIMEOUT = 7200.0;
  private static final boolean DEFAULT_DEBUG = true;

  /** Constructor */
  public MINLPFlags() {
    super();
    put(MathematicalProgramFlags.DEBUG_ON_FLAG, DEFAULT_DEBUG);
    put(MathematicalProgramFlags.TIMEOUT_FLAG, DEFAULT_TIMEOUT);
  }

  /**
   * Constructor
   * 
   * @param flags
   */
  public MINLPFlags(OptimizerFlags flags) {
    super(flags);
    if (get(MathematicalProgramFlags.DEBUG_ON_FLAG) == null) {
      put(MathematicalProgramFlags.DEBUG_ON_FLAG, DEFAULT_DEBUG);
    }
    if (get(MathematicalProgramFlags.TIMEOUT_FLAG) == null) {
      put(MathematicalProgramFlags.TIMEOUT_FLAG, DEFAULT_TIMEOUT);
    }
  }

  @Override
  @SuppressWarnings("rawtypes")
  protected Collection<Class<? extends VariableFactory>> getDefaultVariableFactories() {
    ArrayList<Class<? extends VariableFactory>> defaults = (ArrayList<Class<? extends VariableFactory>>) super.getDefaultVariableFactories();
//    SystemLogger.getSystemLogger().systemLogger.println("-- (1) Extending VARIABLE_Factory");
    return defaults;
  }

  @Override
  @SuppressWarnings("rawtypes")
  protected Collection<Class<? extends ConstraintFactory>> getDefaultConstraintFactories() {
    ArrayList<Class<? extends ConstraintFactory>> defaults = (ArrayList<Class<? extends ConstraintFactory>>) super.getDefaultConstraintFactories();
  //  SystemLogger.getSystemLogger().systemLogger.println("-- (2) Extending CONSTRAINT_Factory");
    defaults.add(WeymouthScenarioConstraint.class); // Weymouth equation constraints, Eqs.5-6 (includes both inequalities)    
    return defaults;
  }

  @Override
  @SuppressWarnings("rawtypes")
  protected Collection<Class<? extends AssignmentFactory>> getDefaultAssignmentFactories() {
    ArrayList<Class<? extends AssignmentFactory>> defaults = (ArrayList<Class<? extends AssignmentFactory>>) super.getDefaultAssignmentFactories();
    //SystemLogger.getSystemLogger().systemLogger.println("-- (3) Extending ASSIGMENT_Factory");
    return defaults;
  }

  @Override
  @SuppressWarnings("rawtypes")
  protected Collection<Class<? extends ObjectiveFunctionFactory>> getDefaultObjectiveFunctionFactories() {
    ArrayList<Class<? extends ObjectiveFunctionFactory>> defaults = (ArrayList<Class<? extends ObjectiveFunctionFactory>>) super.getDefaultObjectiveFunctionFactories();
    //SystemLogger.getSystemLogger().systemLogger.println("-- (4) Extending OF_Factory");
    return defaults;
  }

}
