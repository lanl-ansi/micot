package gov.lanl.micot.infrastructure.optimize;

import java.util.Collection;

import gov.lanl.micot.infrastructure.model.Model;
import gov.lanl.micot.infrastructure.model.Node;
import gov.lanl.micot.infrastructure.optimize.Optimizer;
import gov.lanl.micot.infrastructure.optimize.OptimizerFactory;
import gov.lanl.micot.infrastructure.optimize.OptimizerFlags;
import gov.lanl.micot.infrastructure.optimize.mathprogram.assignment.AssignmentFactory;
import gov.lanl.micot.infrastructure.optimize.mathprogram.constraint.ConstraintFactory;
import gov.lanl.micot.infrastructure.optimize.mathprogram.objective.ObjectiveFunctionFactory;
import gov.lanl.micot.infrastructure.optimize.mathprogram.variable.VariableFactory;

/**
 * A factory for creating optimal gas flow math programs
 * 
 * @author Russell Bent
 */
public abstract class OptimizerFactoryImpl<N extends Node, M extends Model> implements OptimizerFactory<N, M> {

  /**
   * Constructor
   */
  public OptimizerFactoryImpl() {
    super();
  }

  /**
   * Add the variable factories
   * 
   * @param flags
   * @param simulator
   * @throws ClassNotFoundException
   * @throws IllegalAccessException
   * @throws InstantiationException
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  protected void addVariableFactories(OptimizerFlags flags, Optimizer<N, M> simulator) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
    Collection<VariableFactory> variableFactories = flags.getCollection(OptimizerFlags.VARIABLE_FACTORIES_KEY, VariableFactory.class);
    for (Object f : variableFactories) {
      simulator.addVariableFactory(getVariableFactory(f));
    }
  }

  /**
   * Add the constraints
   * 
   * @param flags
   * @param simulator
   */
  @SuppressWarnings({ "unchecked" })
  protected void addConstraints(OptimizerFlags flags, Optimizer<N, M> simulator) {
    Collection<Object> constraints = flags.getCollection(OptimizerFlags.CONSTRAINTS_KEY, Object.class);
    for (Object obj : constraints) {
      simulator.addConstraint(getConstraintFactory(obj));
    }
  }

  /**
   * Adds the variables assignments
   * 
   * @param flags
   * @param simulator
   */
  @SuppressWarnings({ "unchecked" })
  protected void addVariableAssignments(OptimizerFlags flags, Optimizer<N, M> simulator) {
    Collection<Object> assignments = flags.getCollection(OptimizerFlags.ASSIGNMENT_FACTORIES_KEY, Object.class);
    for (Object obj : assignments) {
      simulator.addAssignmentFactory(getAssignmentFactory(obj));
    }
  }

  /**
   * Add the objective functions
   * 
   * @param flags
   * @param simulator
   */
  @SuppressWarnings({ "unchecked" })
  protected void addObjectiveFunctions(OptimizerFlags flags, Optimizer<N, M> simulator) {
    Collection<Object> objs = flags.getCollection(OptimizerFlags.OBJECTIVE_FUNCTIONS_KEY, Object.class);
    for (Object obj : objs) {
      simulator.addObjectiveFunctionFactory(getObjectiveFunctionFactory(obj));
    }
  }

  /**
   * helper function for getting variable factories
   * 
   * @param f
   * @return
   * @throws ClassNotFoundException
   * @throws IllegalAccessException
   * @throws InstantiationException
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  protected VariableFactory getVariableFactory(Object f) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
    if (f instanceof VariableFactory) {
      return (VariableFactory<N, M>) f;
    }
    else {
      return (VariableFactory) Class.forName(f.toString().trim()).newInstance();
    }
  }

  /**
   * Create a constraint factory from an object
   * 
   * @param obj
   * @return
   */
  @SuppressWarnings("rawtypes")
  protected ConstraintFactory getConstraintFactory(Object obj) {
    if (obj instanceof String) {
      try {
        return (ConstraintFactory) Class.forName((String) obj).newInstance();
      }
      catch (Exception e) {
        e.printStackTrace();
      }
    }
    else {
      return (ConstraintFactory) obj;
    }
    return null;
  }

  /**
   * Create an assignment factory from an object
   * 
   * @param obj
   * @return
   */
  @SuppressWarnings("rawtypes")
  protected AssignmentFactory getAssignmentFactory(Object obj) {
    if (obj instanceof String) {
      try {
        return (AssignmentFactory) Class.forName((String) obj).newInstance();
      }
      catch (Exception e) {
        e.printStackTrace();
      }
    }
    else {
      return (AssignmentFactory) obj;
    }
    return null;
  }

  /**
   * Create an objective Function factory from an object
   * 
   * @param obj
   * @return
   */
  @SuppressWarnings("rawtypes")
  protected ObjectiveFunctionFactory getObjectiveFunctionFactory(Object obj) {
    if (obj instanceof String) {
      try {
        return (ObjectiveFunctionFactory) Class.forName((String) obj).newInstance();
      }
      catch (Exception e) {
        e.printStackTrace();
      }
    }
    else {
      return (ObjectiveFunctionFactory) obj;
    }
    return null;
  }

}
