package gov.lanl.micot.deprecated.sbd;

import java.util.Collection;

import gov.lanl.micot.infrastructure.model.Model;
import gov.lanl.micot.infrastructure.model.Node;
import gov.lanl.micot.infrastructure.optimize.Optimizer;
import gov.lanl.micot.infrastructure.optimize.OptimizerFactoryImpl;
import gov.lanl.micot.infrastructure.optimize.OptimizerFlags;
import gov.lanl.micot.infrastructure.optimize.mathprogram.variable.VariableFactory;

/**
 * A factory for creating optimal gas flow math programs
 * @author Russell Bent
 */
public abstract class ScenarioBasedDecompositionFactoryImpl<N extends Node, M extends Model> extends OptimizerFactoryImpl<N, M> implements ScenarioBasedDecompositionFactory<N,M> {
	
	/**
	 *  Constructor
	 */
	public ScenarioBasedDecompositionFactoryImpl() {
	  super();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
  @Override
  public void addOuterVariableFactories(OptimizerFlags flags,  ScenarioBasedDecompositionOptimizer<N,M> simulator) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
	  Collection<VariableFactory> variableFactories = flags.get(ScenarioBasedDecompositionOptimizerFlags.OUTER_OPTIMIZER_FLAGS_KEY, OptimizerFlags.class).getCollection(OptimizerFlags.VARIABLE_FACTORIES_KEY, VariableFactory.class);
    for (Object f : variableFactories) {
      simulator.addOuterVariableFactory(getVariableFactory(f));
    }
  }

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void addInnerVariableFactories(OptimizerFlags flags,  ScenarioBasedDecompositionOptimizer<N,M> simulator) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
	  Collection<VariableFactory> variableFactories = flags.get(ScenarioBasedDecompositionOptimizerFlags.INNER_OPTIMIZER_FLAGS_KEY, OptimizerFlags.class).getCollection(OptimizerFlags.VARIABLE_FACTORIES_KEY, VariableFactory.class);
	  for (Object f : variableFactories) {
      simulator.addInnerVariableFactory(getVariableFactory(f));
	  }
	}
	
	/**
	 * Add the variable factories
	 * @param flags
	 * @param simulator
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
  protected void addVariableFactories(OptimizerFlags flags,  Optimizer<N,M> simulator) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
	  System.err.println("Warning: Scenario based decomposition needs to know if a variable is an inner or outer variable.");
	  super.addVariableFactories(flags,  simulator);
	}

	@SuppressWarnings("unchecked")
  @Override
	public void addOuterConstraints(OptimizerFlags flags,  ScenarioBasedDecompositionOptimizer<N,M> simulator) {
	  Collection<Object> constraints = flags.get(ScenarioBasedDecompositionOptimizerFlags.OUTER_OPTIMIZER_FLAGS_KEY, OptimizerFlags.class).getCollection(OptimizerFlags.CONSTRAINTS_KEY, Object.class);
	  for (Object obj : constraints) {
	    simulator.addOuterConstraint(getConstraintFactory(obj));
	  }
	}

  @Override
  @SuppressWarnings({ "unchecked" })
  public void addInnerConstraints(OptimizerFlags flags,  ScenarioBasedDecompositionOptimizer<N,M> simulator) {
    Collection<Object> constraints = flags.get(ScenarioBasedDecompositionOptimizerFlags.INNER_OPTIMIZER_FLAGS_KEY, OptimizerFlags.class).getCollection(OptimizerFlags.CONSTRAINTS_KEY, Object.class);
    for (Object obj : constraints) {
      simulator.addInnerConstraint(getConstraintFactory(obj));
    }
  }
	
	/**
	 * Add the constraints
	 * @param flags
	 * @param simulator
	 */
  protected void addConstraints(OptimizerFlags flags,  Optimizer<N,M> simulator) {
	  System.err.println("Warning: Scenario based decomposition needs to know if a constraint is an inner or outer variable.");
	  super.addConstraints(flags, simulator);
	}

  @SuppressWarnings("unchecked")
  @Override
  public void addOuterVariableAssignments(OptimizerFlags flags,  ScenarioBasedDecompositionOptimizer<N,M> simulator) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
    Collection<Object> assignments = flags.get(ScenarioBasedDecompositionOptimizerFlags.OUTER_OPTIMIZER_FLAGS_KEY, OptimizerFlags.class).getCollection(OptimizerFlags.ASSIGNMENT_FACTORIES_KEY, Object.class);
    for (Object obj : assignments) {
      simulator.addOuterAssignmentFactory(getAssignmentFactory(obj));
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public void addInnerVariableAssignments(OptimizerFlags flags,  ScenarioBasedDecompositionOptimizer<N,M> simulator) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
    Collection<Object> assignments = flags.get(ScenarioBasedDecompositionOptimizerFlags.INNER_OPTIMIZER_FLAGS_KEY, OptimizerFlags.class).getCollection(OptimizerFlags.ASSIGNMENT_FACTORIES_KEY, Object.class);
    for (Object obj : assignments) {
      simulator.addInnerAssignmentFactory(getAssignmentFactory(obj));
    }
  }
  
	/**
	 * Adds the variables assignments
	 * @param flags
	 * @param simulator
	 */
  protected void addVariableAssignments(OptimizerFlags flags,  Optimizer<N,M> simulator) {
	  System.err.println("Warning: Scenario based decomposition needs to know if an assignment is an inner or outer variable.");
	  super.addVariableAssignments(flags, simulator);
	}

	@SuppressWarnings("unchecked")
  @Override
  public void addOuterObjectiveFunctions(OptimizerFlags flags,  ScenarioBasedDecompositionOptimizer<N,M> simulator) {
    Collection<Object> objs = flags.get(ScenarioBasedDecompositionOptimizerFlags.OUTER_OPTIMIZER_FLAGS_KEY, OptimizerFlags.class).getCollection(OptimizerFlags.OBJECTIVE_FUNCTIONS_KEY, Object.class);
    for (Object obj : objs) {
      simulator.addOuterObjectiveFunctionFactory(getObjectiveFunctionFactory(obj));
    }
  }

	 @SuppressWarnings("unchecked")
	 @Override
	 public void addInnerObjectiveFunctions(OptimizerFlags flags,  ScenarioBasedDecompositionOptimizer<N,M> simulator) {
	   Collection<Object> objs = flags.get(ScenarioBasedDecompositionOptimizerFlags.INNER_OPTIMIZER_FLAGS_KEY, OptimizerFlags.class).getCollection(OptimizerFlags.OBJECTIVE_FUNCTIONS_KEY, Object.class);
	   for (Object obj : objs) {
	     simulator.addInnerObjectiveFunctionFactory(getObjectiveFunctionFactory(obj));
	   }
	 }
	
	/**
	 * Add the objective functions
	 * @param flags
	 * @param simulator
	 */
  protected void addObjectiveFunctions(OptimizerFlags flags,  Optimizer<N,M> simulator) {
	  System.err.println("Warning: Scenario based decomposition needs to know if an objective function is an inner or outer variable.");
	  super.addObjectiveFunctions(flags, simulator);
	}
 
	
}
