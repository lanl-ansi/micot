package gov.lanl.micot.infrastructure.coupled.optimize;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import gov.lanl.micot.infrastructure.model.Model;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.optimize.OptimizerFlags;
import gov.lanl.micot.infrastructure.optimize.mathprogram.MathProgramOptimizerFlags;
import gov.lanl.micot.infrastructure.optimize.mathprogram.assignment.AssignmentFactory;
import gov.lanl.micot.infrastructure.optimize.mathprogram.constraint.ConstraintFactory;
import gov.lanl.micot.infrastructure.optimize.mathprogram.objective.ObjectiveFunctionFactory;
import gov.lanl.micot.infrastructure.optimize.mathprogram.variable.VariableFactory;

/**
 * Flags used for mathematical programs in coupled models
 * 
 * @author Russell Bent
 */
public class CoupledMathProgramOptimizerFlags extends MathProgramOptimizerFlags {

  private static final long serialVersionUID = 1L;

  public static final String MATH_PROGRAM_FACTORY_KEY = "MATH_PROGRAM_FACTORY_KEY";

  public static final String INDIVIDUAL_CONSTRAINTS_KEY = "INDIVIDUAL_CONSTRAINTS_KEY";
  public static final String INDIVIDUAL_OBJECTIVE_FUNCTIONS_KEY = "INDIVIDUAL_OBJECTIVE_FUNCTIONS_KEY";
  public static final String INDIVIDUAL_VARIABLE_FACTORIES_KEY = "INDIVIDUAL_VARIABLE_FACTORIES_KEY";
  public static final String INDIVIDUAL_ASSIGNMENT_FACTORIES_KEY = "INDIVIDUAL_ASSIGNMENT_FACTORIES_KEY";

  /**
   * Constructor
   */
  @SuppressWarnings("rawtypes")
  public CoupledMathProgramOptimizerFlags() {
    super();

    put(INDIVIDUAL_CONSTRAINTS_KEY, new HashMap<Class<? extends Model>, ArrayList<ConstraintFactory>>());
    put(INDIVIDUAL_VARIABLE_FACTORIES_KEY, new HashMap<Class<? extends Model>, ArrayList<VariableFactory>>());
    put(INDIVIDUAL_OBJECTIVE_FUNCTIONS_KEY, new HashMap<Class<? extends Model>, ArrayList<ObjectiveFunctionFactory>>());
    put(INDIVIDUAL_ASSIGNMENT_FACTORIES_KEY, new HashMap<Class<? extends Model>, ArrayList<AssignmentFactory>>());

    try {
      addDefaultIndividualVariableFactories();
      addDefaultIndividualConstraints();
      addDefaultIndividualObjectiveFunctions();
      addDefaultIndividualAssignmentFactories();
    } catch (SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      e.printStackTrace();
    }
  }

  /**
   * Constructor
   * 
   * @param flags
   */
  @SuppressWarnings("rawtypes")
  public CoupledMathProgramOptimizerFlags(OptimizerFlags flags) {
    fill(flags);

    if (get(INDIVIDUAL_CONSTRAINTS_KEY) == null) {
      put(INDIVIDUAL_CONSTRAINTS_KEY, new HashMap<Class<? extends Model>, Collection<ConstraintFactory>>());
    }

    if (get(INDIVIDUAL_VARIABLE_FACTORIES_KEY) == null) {
      put(INDIVIDUAL_VARIABLE_FACTORIES_KEY, new HashMap<Class<? extends Model>, Collection<VariableFactory>>());
    }

    if (get(INDIVIDUAL_ASSIGNMENT_FACTORIES_KEY) == null) {
      put(INDIVIDUAL_ASSIGNMENT_FACTORIES_KEY, new HashMap<Class<? extends Model>, Collection<AssignmentFactory>>());
    }

    if (get(INDIVIDUAL_OBJECTIVE_FUNCTIONS_KEY) == null) {
      put(INDIVIDUAL_OBJECTIVE_FUNCTIONS_KEY, new HashMap<Class<? extends Model>, Collection<ObjectiveFunctionFactory>>());
    }

    try {
      addDefaultIndividualVariableFactories();
      addDefaultIndividualConstraints();
      addDefaultIndividualObjectiveFunctions();
      addDefaultIndividualAssignmentFactories();
    } catch (SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      e.printStackTrace();
    }
  }

  /**
   * Get the default variable factories for individual models
   * 
   * @return
   */
  @SuppressWarnings("rawtypes")
  protected Map<Class<? extends Model>, Collection<Class<? extends VariableFactory>>> getDefaultIndividualVariableFactories() {
    return new HashMap<Class<? extends Model>, Collection<Class<? extends VariableFactory>>>();
  }

  /**
   * Get the default constraint factories for individual models
   * 
   * @return
   */
  @SuppressWarnings("rawtypes")
  protected Map<Class<? extends Model>, Collection<Class<? extends ConstraintFactory>>> getDefaultIndividualConstraintFactories() {
    return new HashMap<Class<? extends Model>, Collection<Class<? extends ConstraintFactory>>>();
  }

  /**
   * Get the default assignment factories for individual models
   * 
   * @return
   */
  @SuppressWarnings("rawtypes")
  protected Map<Class<? extends Model>, Collection<Class<? extends AssignmentFactory>>> getDefaultIndividualAssignmentFactories() {
    return new HashMap<Class<? extends Model>, Collection<Class<? extends AssignmentFactory>>>();
  }

  /**
   * Get the default objective functin factories for individual models
   * 
   * @return
   */
  @SuppressWarnings("rawtypes")
  protected Map<Class<? extends Model>, Collection<Class<? extends ObjectiveFunctionFactory>>> getDefaultIndividualObjectiveFunctionFactories() {
    return new HashMap<Class<? extends Model>, Collection<Class<? extends ObjectiveFunctionFactory>>>();
  }

  /**
   * Add the default variable factories
   * 
   * @throws InvocationTargetException
   * @throws IllegalArgumentException
   * @throws IllegalAccessException
   * @throws InstantiationException
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  protected void addDefaultIndividualVariableFactories() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    Map<Class<? extends Model>, Collection<Class<? extends VariableFactory>>> defaults = getDefaultIndividualVariableFactories();

    Map<Object, Collection> modelVariableFactories = get(INDIVIDUAL_VARIABLE_FACTORIES_KEY, Map.class);
    Collection<Scenario> scenarios = getCollection(MathProgramOptimizerFlags.SCENARIOS_KEY, Scenario.class);

    // make sure that the factories exist
    for (Class<? extends Model> cls : defaults.keySet()) {
      if (modelVariableFactories.get(cls) == null) {
        modelVariableFactories.put(cls, new ArrayList<VariableFactory>());
      }
    }

    // create the hashmap of booleans
    HashMap<Class<? extends Model>, Map<Class<? extends VariableFactory>, Boolean>> hasFactories = new HashMap<Class<? extends Model>, Map<Class<? extends VariableFactory>, Boolean>>();
    for (Class<? extends Model> mcls : defaults.keySet()) {
      hasFactories.put(mcls, new HashMap<Class<? extends VariableFactory>, Boolean>());
      for (Class<? extends VariableFactory> cls : defaults.get(mcls)) {
        hasFactories.get(mcls).put(cls, false);
      }
    }

    // loop through and see if default variables are in the list
    for (Object mcls : defaults.keySet()) {
      ArrayList<Object> copy = new ArrayList<Object>();
      copy.addAll(modelVariableFactories.get(mcls));
      for (Object factory : copy) {
        for (Class<? extends VariableFactory> cls : defaults.get(mcls)) {
          if (cls.isInstance(factory) || factory.toString().equals(cls.getCanonicalName())) {
            hasFactories.get(mcls).put(cls, true);
            try {
              Constructor constructor = cls.getConstructor(Collection.class);
              modelVariableFactories.get(mcls).remove(factory);
              modelVariableFactories.get(mcls).add(constructor.newInstance(scenarios));
            } catch (NoSuchMethodException e) {
            }
          }
        }
      }
    }

    // loop through and add missing variable factories
    for (Object mcls : defaults.keySet()) {
      for (Class<? extends VariableFactory> cls : defaults.get(mcls)) {
        if (!hasFactories.get(mcls).get(cls)) {
          try {
            Constructor constructor = cls.getConstructor(Collection.class);
            modelVariableFactories.get(mcls).add(constructor.newInstance(scenarios));
          } catch (NoSuchMethodException e) {
            modelVariableFactories.get(mcls).add(cls.newInstance());
          }
        }
      }
    }

    put(INDIVIDUAL_VARIABLE_FACTORIES_KEY, modelVariableFactories);
  }

  /**
   * Add the default variable factories
   * 
   * @throws InvocationTargetException
   * @throws IllegalArgumentException
   * @throws IllegalAccessException
   * @throws InstantiationException
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  protected void addDefaultIndividualConstraints() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    Map<Class<? extends Model>, Collection<Class<? extends ConstraintFactory>>> defaults = getDefaultIndividualConstraintFactories();

    Map<Object, Collection> modelVariableFactories = get(INDIVIDUAL_CONSTRAINTS_KEY, Map.class);
    Collection<Scenario> scenarios = getCollection(MathProgramOptimizerFlags.SCENARIOS_KEY, Scenario.class);

    // make sure that the factories exist
    for (Class<? extends Model> cls : defaults.keySet()) {
      if (modelVariableFactories.get(cls) == null) {
        modelVariableFactories.put(cls, new ArrayList<VariableFactory>());
      }
    }

    // create the hashmap of booleans
    HashMap<Class<? extends Model>, Map<Class<? extends ConstraintFactory>, Boolean>> hasFactories = new HashMap<Class<? extends Model>, Map<Class<? extends ConstraintFactory>, Boolean>>();
    for (Class<? extends Model> mcls : defaults.keySet()) {
      hasFactories.put(mcls, new HashMap<Class<? extends ConstraintFactory>, Boolean>());
      for (Class<? extends ConstraintFactory> cls : defaults.get(mcls)) {
        hasFactories.get(mcls).put(cls, false);
      }
    }

    // loop through and see if default variables are in the list
    for (Object mcls : defaults.keySet()) {
      ArrayList<Object> copy = new ArrayList<Object>();
      copy.addAll(modelVariableFactories.get(mcls));
      for (Object factory : copy) {
        for (Class<? extends ConstraintFactory> cls : defaults.get(mcls)) {
          if (cls.isInstance(factory) || factory.toString().equals(cls.getCanonicalName())) {
            hasFactories.get(mcls).put(cls, true);
            try {
              Constructor constructor = cls.getConstructor(Collection.class);
              modelVariableFactories.get(mcls).remove(factory);
              modelVariableFactories.get(mcls).add(constructor.newInstance(scenarios));
            } catch (NoSuchMethodException e) {
            }
          }
        }
      }
    }

    // loop through and add missing variable factories
    for (Object mcls : defaults.keySet()) {
      for (Class<? extends ConstraintFactory> cls : defaults.get(mcls)) {
        if (!hasFactories.get(mcls).get(cls)) {
          try {
            Constructor constructor = cls.getConstructor(Collection.class);
            modelVariableFactories.get(mcls).add(constructor.newInstance(scenarios));
          } catch (NoSuchMethodException e) {
            modelVariableFactories.get(mcls).add(cls.newInstance());
          }
        }
      }
    }

    put(INDIVIDUAL_CONSTRAINTS_KEY, modelVariableFactories);
  }

  /**
   * Add the default variable factories
   * 
   * @throws InvocationTargetException
   * @throws IllegalArgumentException
   * @throws IllegalAccessException
   * @throws InstantiationException
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  protected void addDefaultIndividualAssignmentFactories() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    Map<Class<? extends Model>, Collection<Class<? extends AssignmentFactory>>> defaults = getDefaultIndividualAssignmentFactories();

    Map<Object, Collection> modelVariableFactories = get(INDIVIDUAL_ASSIGNMENT_FACTORIES_KEY, Map.class);
    Collection<Scenario> scenarios = getCollection(MathProgramOptimizerFlags.SCENARIOS_KEY, Scenario.class);

    // make sure that the factories exist
    for (Class<? extends Model> cls : defaults.keySet()) {
      if (modelVariableFactories.get(cls) == null) {
        modelVariableFactories.put(cls, new ArrayList<VariableFactory>());
      }
    }

    // create the hashmap of booleans
    HashMap<Class<? extends Model>, Map<Class<? extends AssignmentFactory>, Boolean>> hasFactories = new HashMap<Class<? extends Model>, Map<Class<? extends AssignmentFactory>, Boolean>>();
    for (Class<? extends Model> mcls : defaults.keySet()) {
      hasFactories.put(mcls, new HashMap<Class<? extends AssignmentFactory>, Boolean>());
      for (Class<? extends AssignmentFactory> cls : defaults.get(mcls)) {
        hasFactories.get(mcls).put(cls, false);
      }
    }

    // loop through and see if default variables are in the list
    for (Object mcls : defaults.keySet()) {
      ArrayList<Object> copy = new ArrayList<Object>();
      copy.addAll(modelVariableFactories.get(mcls));
      for (Object factory : copy) {
        for (Class<? extends AssignmentFactory> cls : defaults.get(mcls)) {
          if (cls.isInstance(factory) || factory.toString().equals(cls.getCanonicalName())) {
            hasFactories.get(mcls).put(cls, true);
            try {
              Constructor constructor = cls.getConstructor(Collection.class);
              modelVariableFactories.get(mcls).remove(factory);
              modelVariableFactories.get(mcls).add(constructor.newInstance(scenarios));
            } catch (NoSuchMethodException e) {
            }
          }
        }
      }
    }

    // loop through and add missing variable factories
    for (Object mcls : defaults.keySet()) {
      for (Class<? extends AssignmentFactory> cls : defaults.get(mcls)) {
        if (!hasFactories.get(mcls).get(cls)) {
          try {
            Constructor constructor = cls.getConstructor(Collection.class);
            modelVariableFactories.get(mcls).add(constructor.newInstance(scenarios));
          } catch (NoSuchMethodException e) {
            modelVariableFactories.get(mcls).add(cls.newInstance());
          }
        }
      }
    }

    put(INDIVIDUAL_ASSIGNMENT_FACTORIES_KEY, modelVariableFactories);
  }

  /**
   * Add the default variable factories
   * 
   * @throws InvocationTargetException
   * @throws IllegalArgumentException
   * @throws IllegalAccessException
   * @throws InstantiationException
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  protected void addDefaultIndividualObjectiveFunctions() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    Map<Class<? extends Model>, Collection<Class<? extends ObjectiveFunctionFactory>>> defaults = getDefaultIndividualObjectiveFunctionFactories();

    Map<Object, Collection> modelVariableFactories = get(INDIVIDUAL_OBJECTIVE_FUNCTIONS_KEY, Map.class);
    Collection<Scenario> scenarios = getCollection(MathProgramOptimizerFlags.SCENARIOS_KEY, Scenario.class);

    // make sure that the factories exist
    for (Class<? extends Model> cls : defaults.keySet()) {
      if (modelVariableFactories.get(cls) == null) {
        modelVariableFactories.put(cls, new ArrayList<VariableFactory>());
      }
    }

    // create the hashmap of booleans
    HashMap<Class<? extends Model>, Map<Class<? extends ObjectiveFunctionFactory>, Boolean>> hasFactories = new HashMap<Class<? extends Model>, Map<Class<? extends ObjectiveFunctionFactory>, Boolean>>();
    for (Class<? extends Model> mcls : defaults.keySet()) {
      hasFactories.put(mcls, new HashMap<Class<? extends ObjectiveFunctionFactory>, Boolean>());
      for (Class<? extends ObjectiveFunctionFactory> cls : defaults.get(mcls)) {
        hasFactories.get(mcls).put(cls, false);
      }
    }

    // loop through and see if default variables are in the list
    for (Object mcls : defaults.keySet()) {
      ArrayList<Object> copy = new ArrayList<Object>();
      copy.addAll(modelVariableFactories.get(mcls));
      for (Object factory : copy) {
        for (Class<? extends ObjectiveFunctionFactory> cls : defaults.get(mcls)) {
          if (cls.isInstance(factory) || factory.toString().equals(cls.getCanonicalName())) {
            hasFactories.get(mcls).put(cls, true);
            try {
              Constructor constructor = cls.getConstructor(Collection.class);
              modelVariableFactories.get(mcls).remove(factory);
              modelVariableFactories.get(mcls).add(constructor.newInstance(scenarios));
            } catch (NoSuchMethodException e) {
            }
          }
        }
      }
    }

    // loop through and add missing variable factories
    for (Object mcls : defaults.keySet()) {
      for (Class<? extends ObjectiveFunctionFactory> cls : defaults.get(mcls)) {
        if (!hasFactories.get(mcls).get(cls)) {
          try {
            Constructor constructor = cls.getConstructor(Collection.class);
            modelVariableFactories.get(mcls).add(constructor.newInstance(scenarios));
          } catch (NoSuchMethodException e) {
            modelVariableFactories.get(mcls).add(cls.newInstance());
          }
        }
      }
    }

    put(INDIVIDUAL_OBJECTIVE_FUNCTIONS_KEY, modelVariableFactories);
  }

}
