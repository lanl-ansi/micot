package gov.lanl.micot.infrastructure.naturalgas.optimize.ogf;

import java.util.ArrayList;
import java.util.Collection;

import gov.lanl.micot.infrastructure.naturalgas.optimize.ogf.constraint.scenario.soc.RelaxedFlowScenarioBoundConstraint;
import gov.lanl.micot.infrastructure.naturalgas.optimize.ogf.constraint.scenario.soc.WeymouthMcCormickScenarioConstraint;
import gov.lanl.micot.infrastructure.naturalgas.optimize.ogf.constraint.scenario.soc.WeymouthScenarioConstraint;
import gov.lanl.micot.infrastructure.naturalgas.optimize.ogf.variable.scenario.soc.RelaxedFlowScenarioVariableFactory;
import gov.lanl.micot.infrastructure.optimize.OptimizerFlags;
import gov.lanl.micot.infrastructure.optimize.mathprogram.assignment.AssignmentFactory;
import gov.lanl.micot.infrastructure.optimize.mathprogram.constraint.ConstraintFactory;
import gov.lanl.micot.infrastructure.optimize.mathprogram.objective.ObjectiveFunctionFactory;
import gov.lanl.micot.infrastructure.optimize.mathprogram.variable.VariableFactory;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgramFlags;

/**
 * @author Russell Bent
 * Operation optimization modeling using second order cones
 */
public class SOCFlags extends DefaultFlags {
  private static final long serialVersionUID = 1L;

  private static final boolean DEFAULT_DEBUG = true;

  /** Constructor */
  public SOCFlags() {
    super();
    put(MathematicalProgramFlags.DEBUG_ON_FLAG, DEFAULT_DEBUG);
  }

  /**
   * Constructor
   * 
   * @param flags
   */
  public SOCFlags(OptimizerFlags flags) {
    super(flags);
    if (get(MathematicalProgramFlags.DEBUG_ON_FLAG) == null) {
      put(MathematicalProgramFlags.DEBUG_ON_FLAG, DEFAULT_DEBUG);
    }
  }

  @Override
  @SuppressWarnings("rawtypes")
  protected Collection<Class<? extends VariableFactory>> getDefaultVariableFactories() {
    ArrayList<Class<? extends VariableFactory>> defaults = (ArrayList<Class<? extends VariableFactory>>) super.getDefaultVariableFactories();
//    SystemLogger.getSystemLogger().systemLogger.println("-- (1) Extending VARIABLE_Factory");
    defaults.add(RelaxedFlowScenarioVariableFactory.class); // lij: SOC-flow for SOC-relaxation model
    return defaults;
  }

  @Override
  @SuppressWarnings("rawtypes")
  protected Collection<Class<? extends ConstraintFactory>> getDefaultConstraintFactories() {
    ArrayList<Class<? extends ConstraintFactory>> defaults = (ArrayList<Class<? extends ConstraintFactory>>) super.getDefaultConstraintFactories();
  //  SystemLogger.getSystemLogger().systemLogger.println("-- (2) Extending CONSTRAINT_Factory");
    defaults.add(WeymouthScenarioConstraint.class); // SOC-Weymouth equation constraints:
    defaults.add(WeymouthMcCormickScenarioConstraint.class); // McCormick constraints on Weymouth:
    defaults.add(RelaxedFlowScenarioBoundConstraint.class); // bounds on the l variables
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
