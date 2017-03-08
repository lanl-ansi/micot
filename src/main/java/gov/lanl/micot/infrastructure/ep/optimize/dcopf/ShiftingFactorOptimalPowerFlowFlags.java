package gov.lanl.micot.infrastructure.ep.optimize.dcopf;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;

import gov.lanl.micot.infrastructure.ep.optimize.ElectricPowerMathProgramOptimizerFlags;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.assignment.FlowVariableAssignmentFactory;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.assignment.GenerationAssignmentFactory;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.assignment.LoadShedAssignmentFactory;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.assignment.LoadVariableAssignmentFactory;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.assignment.ShadowPriceAssignmentFactory;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.constraint.BalanceConstraint;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.constraint.GenerationConstraint;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.constraint.LinkFlowCapacityConstraint;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.constraint.LoadConstraint;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.constraint.LoadShedConstraint;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.constraint.ShiftingFactorConstraint;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.objective.GenerationObjectiveFunctionFactory;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.objective.LoadObjectiveFunctionFactory;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.objective.LoadShedObjectiveFunctionFactory;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.variable.FlowVariableFactory;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.variable.GeneratorVariableFactory;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.variable.LoadShedVariableFactory;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.variable.LoadVariableFactory;
import gov.lanl.micot.infrastructure.optimize.OptimizerFlags;
import gov.lanl.micot.infrastructure.optimize.mathprogram.assignment.AssignmentFactory;
import gov.lanl.micot.infrastructure.optimize.mathprogram.constraint.ConstraintFactory;
import gov.lanl.micot.infrastructure.optimize.mathprogram.objective.ObjectiveFunctionFactory;
import gov.lanl.micot.infrastructure.optimize.mathprogram.variable.VariableFactory;

/**
 * Flags used for shifting factor opf DC solvers
 * @author Russell Bent
 */
public class ShiftingFactorOptimalPowerFlowFlags extends ElectricPowerMathProgramOptimizerFlags {
	
	private static final long serialVersionUID = 1L;
	
  /**
   * Constructor
   */
	public ShiftingFactorOptimalPowerFlowFlags() {
		super();    
//    addDefaultVariableFactories();
 //   addDefaultConstraints();
   // addDefaultObjectiveFunctions();
    //addDefaultAssignmentFactories();
	}
	
	/**
	 * Constructor
	 * @param flags
	 */
	public ShiftingFactorOptimalPowerFlowFlags(OptimizerFlags flags) {
		super(flags);
    //addDefaultVariableFactories();
    //addDefaultConstraints();
    //addDefaultObjectiveFunctions();
    //addDefaultAssignmentFactories();
	}
	
	
  @Override
  @SuppressWarnings("rawtypes")
  protected Collection<Class<? extends VariableFactory>> getDefaultVariableFactories() {
    ArrayList<Class<? extends VariableFactory>> defaults = new ArrayList<Class<? extends VariableFactory>>();
    defaults.add(GeneratorVariableFactory.class);
//    defaults.add(LoadShedVariableFactory.class);
    defaults.add(FlowVariableFactory.class);    
    return defaults;
  }

  @Override
  @SuppressWarnings("rawtypes")
  protected Collection<Class<? extends ConstraintFactory>> getDefaultConstraintFactories() {
    ArrayList<Class<? extends ConstraintFactory>> defaults = new ArrayList<Class<? extends ConstraintFactory>>();
    defaults.add(GenerationConstraint.class);
    defaults.add(BalanceConstraint.class);
   // defaults.add(LoadShedConstraint.class);
    defaults.add(LinkFlowCapacityConstraint.class);
    defaults.add(ShiftingFactorConstraint.class);
    return defaults;
  }

  @Override
  @SuppressWarnings("rawtypes")
  protected Collection<Class<? extends AssignmentFactory>> getDefaultAssignmentFactories() {
    ArrayList<Class<? extends AssignmentFactory>> defaults = new ArrayList<Class<? extends AssignmentFactory>>();
    defaults.add(GenerationAssignmentFactory.class);
    defaults.add(ShadowPriceAssignmentFactory.class);
    defaults.add(FlowVariableAssignmentFactory.class);
    //defaults.add(LoadShedAssignmentFactory.class);        
    return defaults;
  }

  @Override
  @SuppressWarnings("rawtypes")
  protected Collection<Class<? extends ObjectiveFunctionFactory>> getDefaultObjectiveFunctionFactories() {
    ArrayList<Class<? extends ObjectiveFunctionFactory>> defaults = new ArrayList<Class<? extends ObjectiveFunctionFactory>>();
    defaults.add(GenerationObjectiveFunctionFactory.class);
    //defaults.add(LoadShedObjectiveFunctionFactory.class);
    return defaults;
  }
		    
  @SuppressWarnings("rawtypes")
  @Override
  protected void addDefaultVariableFactories() throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    super.addDefaultVariableFactories();
    
    Collection<VariableFactory> variableFactories = getCollection(VARIABLE_FACTORIES_KEY, VariableFactory.class);
    boolean hasLoadFactory = false;
    
    for (Object factory : variableFactories) {
      if (factory instanceof LoadShedVariableFactory || factory.toString().equals(LoadShedVariableFactory.class.getCanonicalName()) || 
          factory instanceof LoadVariableFactory || factory.toString().equals(LoadVariableFactory.class.getCanonicalName())) {
        hasLoadFactory = true;
      }
    }
    
    if (!hasLoadFactory) {
      variableFactories.add(new LoadShedVariableFactory());
    }
    put(VARIABLE_FACTORIES_KEY,variableFactories);
  }
  
    
  @SuppressWarnings("rawtypes")
  @Override
  protected void addDefaultConstraints() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    super.addDefaultConstraints();
    Collection<ConstraintFactory> constraints = getCollection(CONSTRAINTS_KEY, ConstraintFactory.class);
        
    boolean hasShedConstraint         = false;
    
    for (Object factory : constraints) {
      if (factory instanceof LoadShedConstraint || factory.toString().equals(LoadShedConstraint.class.getCanonicalName()) || 
          factory instanceof LoadConstraint || factory.toString().equals(LoadConstraint.class.getCanonicalName())) {
        hasShedConstraint = true;
      }
    }
    
    if (!hasShedConstraint) {
      constraints.add(new LoadShedConstraint());
    }
    put(CONSTRAINTS_KEY,constraints);
  }

  /**
   * Add the default objective functions
   * @throws InvocationTargetException 
   * @throws IllegalArgumentException 
   * @throws IllegalAccessException 
   * @throws InstantiationException 
   */
  @SuppressWarnings("rawtypes")
  protected void addDefaultObjectiveFunctions() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    super.addDefaultObjectiveFunctions();
    Collection<ObjectiveFunctionFactory> objs = getCollection(OBJECTIVE_FUNCTIONS_KEY, ObjectiveFunctionFactory.class);
        
    boolean hasLoadObj        = false;
    
    for (Object factory : objs) {
      if (factory instanceof LoadShedObjectiveFunctionFactory || factory.toString().equals(LoadShedObjectiveFunctionFactory.class.getCanonicalName()) ||
          factory instanceof LoadObjectiveFunctionFactory || factory.toString().equals(LoadObjectiveFunctionFactory.class.getCanonicalName()) ) {
        hasLoadObj  = true;
      }
    }
    
    if (!hasLoadObj) {
      objs.add(new LoadShedObjectiveFunctionFactory());
    }
    put(OBJECTIVE_FUNCTIONS_KEY,objs);
  }

  
  /**
   * Add the default variable factories
   * @throws InvocationTargetException 
   * @throws IllegalArgumentException 
   * @throws IllegalAccessException 
   * @throws InstantiationException 
   */
  @SuppressWarnings("rawtypes")
  protected void addDefaultAssignmentFactories() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    super.addDefaultAssignmentFactories();
    Collection<AssignmentFactory> assignmentFactories = getCollection(ASSIGNMENT_FACTORIES_KEY, AssignmentFactory.class);
    boolean hasLoad        = false;
    
    for (Object factory : assignmentFactories) {
      if (factory instanceof LoadShedAssignmentFactory || factory.toString().equals(LoadShedAssignmentFactory.class.getCanonicalName()) ||
          factory instanceof LoadVariableAssignmentFactory || factory.toString().equals(LoadVariableAssignmentFactory.class.getCanonicalName())) {
        hasLoad = true;
      }      
    }
    
    if(!hasLoad) {
      assignmentFactories.add(new LoadShedAssignmentFactory());
    }

    put(ASSIGNMENT_FACTORIES_KEY,assignmentFactories);
  }

  
  
  
}
