package gov.lanl.micot.infrastructure.ep.optimize.dcopf;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;

import gov.lanl.micot.infrastructure.ep.optimize.ElectricPowerMathProgramOptimizerFlags;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.assignment.FlowAssignmentFactory;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.assignment.GenerationAssignmentFactory;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.assignment.LoadConstantAssignmentFactory;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.assignment.LoadShedAssignmentFactory;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.assignment.LoadVariableAssignmentFactory;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.assignment.PhaseAngleAssignmentFactory;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.assignment.ShadowPriceAssignmentFactory;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.constraint.FlowBalanceConstraint;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.constraint.GenerationConstraint;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.constraint.LinkCapacityConstraint;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.constraint.SlackBusConstraint;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.objective.GenerationObjectiveFunctionFactory;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.variable.GeneratorVariableFactory;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.variable.PhaseAngleVariableFactory;
import gov.lanl.micot.infrastructure.optimize.OptimizerFlags;
import gov.lanl.micot.infrastructure.optimize.mathprogram.assignment.AssignmentFactory;
import gov.lanl.micot.infrastructure.optimize.mathprogram.constraint.ConstraintFactory;
import gov.lanl.micot.infrastructure.optimize.mathprogram.objective.ObjectiveFunctionFactory;
import gov.lanl.micot.infrastructure.optimize.mathprogram.variable.VariableFactory;

/**
 * Flags used for opf DC solvers
 * @author Russell Bent
 */
public class OptimalPowerFlowFlags extends ElectricPowerMathProgramOptimizerFlags {
	
	private static final long serialVersionUID = 1L;
	
  /**
   * Constructor
   */
	public OptimalPowerFlowFlags() {
		super();    
	}
	
	/**
	 * Constructor
	 * @param flags
	 */
	public OptimalPowerFlowFlags(OptimizerFlags flags) {
		super(flags);
	}
		
  @Override
  @SuppressWarnings("rawtypes")
  protected Collection<Class<? extends VariableFactory>> getDefaultVariableFactories() {
    ArrayList<Class<? extends VariableFactory>> defaults = new ArrayList<Class<? extends VariableFactory>>();
    defaults.add(GeneratorVariableFactory.class);
    defaults.add(PhaseAngleVariableFactory.class);
    return defaults;
  }

  @Override
  @SuppressWarnings("rawtypes")
  protected Collection<Class<? extends ConstraintFactory>> getDefaultConstraintFactories() {
    ArrayList<Class<? extends ConstraintFactory>> defaults = new ArrayList<Class<? extends ConstraintFactory>>();
    defaults.add(GenerationConstraint.class);
    defaults.add(SlackBusConstraint.class);
    defaults.add(FlowBalanceConstraint.class);
    defaults.add(LinkCapacityConstraint.class);
    return defaults;
  }

  @Override
  @SuppressWarnings("rawtypes")
  protected Collection<Class<? extends AssignmentFactory>> getDefaultAssignmentFactories() {
    ArrayList<Class<? extends AssignmentFactory>> defaults = new ArrayList<Class<? extends AssignmentFactory>>();
    defaults.add(GenerationAssignmentFactory.class);
    defaults.add(PhaseAngleAssignmentFactory.class);
    defaults.add(ShadowPriceAssignmentFactory.class);
    defaults.add(FlowAssignmentFactory.class);    
    return defaults;
  }

  @Override
  @SuppressWarnings("rawtypes")
  protected Collection<Class<? extends ObjectiveFunctionFactory>> getDefaultObjectiveFunctionFactories() {
    ArrayList<Class<? extends ObjectiveFunctionFactory>> defaults = new ArrayList<Class<? extends ObjectiveFunctionFactory>>();
    defaults.add(GenerationObjectiveFunctionFactory.class);
    return defaults;
  }
		
  @Override
  protected void addDefaultAssignmentFactories() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    super.addDefaultAssignmentFactories();
    Collection<Object> assignmentFactories = getCollection(ASSIGNMENT_FACTORIES_KEY, Object.class);
    boolean hasLoad        = false;
    
    for (Object factory : assignmentFactories) {
      if (factory instanceof LoadConstantAssignmentFactory || factory instanceof LoadVariableAssignmentFactory || factory instanceof LoadShedAssignmentFactory 
          || factory.toString().equals(LoadConstantAssignmentFactory.class.getCanonicalName())
          || factory.toString().equals(LoadVariableAssignmentFactory.class.getCanonicalName())
          || factory.toString().equals(LoadShedAssignmentFactory.class.getCanonicalName())) {
        hasLoad = true;
      }      
    }
    
    if(!hasLoad) {
      assignmentFactories.add(new LoadConstantAssignmentFactory());
    }
    put(ASSIGNMENT_FACTORIES_KEY,assignmentFactories);
  }

  
  
	
}
