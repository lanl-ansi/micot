package gov.lanl.micot.infrastructure.optimize;

import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.optimize.mathprogram.assignment.AssignmentFactory;
import gov.lanl.micot.infrastructure.optimize.mathprogram.constraint.ConstraintFactory;
import gov.lanl.micot.infrastructure.optimize.mathprogram.initialsolution.InitialSolutionFactory;
import gov.lanl.micot.infrastructure.optimize.mathprogram.objective.ObjectiveFunctionFactory;
import gov.lanl.micot.infrastructure.optimize.mathprogram.variable.VariableFactory;
import gov.lanl.micot.util.io.Flags;
import gov.lanl.micot.util.io.FlagsImpl;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * Flags and parameters for setting up optimizer
 * 
 * @author Russell Bent
 */
public class OptimizerFlags extends FlagsImpl {

	private static final long serialVersionUID = 1L;

	public static final String PATH_FLAG = "DIRECTORY_PATH";

	private static final String DEFAULT_DIRECTORY = System.getProperty("user.dir") + File.separatorChar;

	public static final String CONSTRAINTS_KEY = "CONSTRAINTS";
	public static final String OBJECTIVE_FUNCTIONS_KEY = "OBJECTIVE_FUNCTIONS";
	public static final String VARIABLE_FACTORIES_KEY = "VARIABLE_FACTORIES";
	public static final String INITIAL_SOLUTION_FACTORIES_KEY = "INITIAL_SOLUTION_FACTORIES";
	public static final String ASSIGNMENT_FACTORIES_KEY = "ASSIGNMENT_FACTORIES";
	public static final String SCENARIOS_KEY = "SCENARIOS";

	/**
	 * Constructor
	 */
	@SuppressWarnings("rawtypes")
	public OptimizerFlags() {
		super();
		put(PATH_FLAG, DEFAULT_DIRECTORY);

		put(CONSTRAINTS_KEY, new ArrayList<ConstraintFactory>());
		put(VARIABLE_FACTORIES_KEY, new ArrayList<VariableFactory>());
		put(OBJECTIVE_FUNCTIONS_KEY, new ArrayList<ObjectiveFunctionFactory>());
		put(ASSIGNMENT_FACTORIES_KEY, new ArrayList<AssignmentFactory>());
		put(INITIAL_SOLUTION_FACTORIES_KEY, new ArrayList<InitialSolutionFactory>());

		Scenario scenario = new Scenario(0, 1.0);
		ArrayList<Scenario> scenarios = new ArrayList<Scenario>();
		scenarios.add(scenario);
		put(SCENARIOS_KEY, scenarios);

		try {
			addDefaultVariableFactories();
			addDefaultConstraints();
			addDefaultObjectiveFunctions();
			addDefaultAssignmentFactories();
			addDefaultInitialSolutionFactories();
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Constructor
	 * 
	 * @param flags
	 */
	@SuppressWarnings("rawtypes")
	public OptimizerFlags(Flags flags) {
		super();
		fill(flags);
		if (get(PATH_FLAG) == null) {
			put(PATH_FLAG, DEFAULT_DIRECTORY);
		}

		if (get(CONSTRAINTS_KEY) == null) {
			put(CONSTRAINTS_KEY, new ArrayList<ConstraintFactory>());
		}

		if (get(VARIABLE_FACTORIES_KEY) == null) {
			put(VARIABLE_FACTORIES_KEY, new ArrayList<VariableFactory>());
		}

		if (get(OBJECTIVE_FUNCTIONS_KEY) == null) {
			put(OBJECTIVE_FUNCTIONS_KEY, new ArrayList<ObjectiveFunctionFactory>());
		}

		if (get(ASSIGNMENT_FACTORIES_KEY) == null) {
			put(ASSIGNMENT_FACTORIES_KEY, new ArrayList<AssignmentFactory>());
		}

		if (get(INITIAL_SOLUTION_FACTORIES_KEY) == null) {
			put(INITIAL_SOLUTION_FACTORIES_KEY, new ArrayList<InitialSolutionFactory>());
		}

		if (get(SCENARIOS_KEY) == null) {
			Scenario scenario = new Scenario(0, 1.0);
			ArrayList<Scenario> scenarios = new ArrayList<Scenario>();
			scenarios.add(scenario);
			put(SCENARIOS_KEY, scenarios);
		}

		try {
			addDefaultVariableFactories();
			addDefaultConstraints();
			addDefaultObjectiveFunctions();
			addDefaultAssignmentFactories();
			addDefaultInitialSolutionFactories();
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Get the default variable factories
	 * 
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	protected Collection<Class<? extends VariableFactory>> getDefaultVariableFactories() {
		return new ArrayList<Class<? extends VariableFactory>>();
	}

	/**
	 * Get the default variable factories
	 * 
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	protected Collection<Class<? extends InitialSolutionFactory>> getDefaultInitialSolutionFactories() {
		return new ArrayList<Class<? extends InitialSolutionFactory>>();
	}

	/**
	 * Get the default constraint factories
	 * 
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	protected Collection<Class<? extends ConstraintFactory>> getDefaultConstraintFactories() {
		return new ArrayList<Class<? extends ConstraintFactory>>();
	}

	/**
	 * Get the default constraint factories
	 * 
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	protected Collection<Class<? extends AssignmentFactory>> getDefaultAssignmentFactories() {
		return new ArrayList<Class<? extends AssignmentFactory>>();
	}

	/**
	 * Get the default constraint factories
	 * 
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	protected Collection<Class<? extends ObjectiveFunctionFactory>> getDefaultObjectiveFunctionFactories() {
		return new ArrayList<Class<? extends ObjectiveFunctionFactory>>();
	}

	/**
	 * Add the default variable factories
	 * 
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	@SuppressWarnings({ "rawtypes" })
	protected void addDefaultVariableFactories() throws NoSuchMethodException, SecurityException,
			InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		Collection<Class<? extends VariableFactory>> defaults = getDefaultVariableFactories();

		// get the data already stored in the model
		Collection<Object> variableFactories = getCollection(VARIABLE_FACTORIES_KEY, Object.class);
		Collection<Scenario> scenarios = getCollection(SCENARIOS_KEY, Scenario.class);

		// set up the booleans for checking if factories are include
		HashMap<Class<? extends VariableFactory>, Boolean> hasFactories = new HashMap<Class<? extends VariableFactory>, Boolean>();
		for (Class<? extends VariableFactory> cls : defaults) {
			hasFactories.put(cls, false);
		}

		// loop through and see if default variables are in the list
		ArrayList<Object> copy = new ArrayList<Object>();
		copy.addAll(variableFactories);
		for (Object factory : copy) {
			for (Class<? extends VariableFactory> cls : defaults) {
				if (cls.isInstance(factory) || factory.toString().equals(cls.getCanonicalName())) {
					hasFactories.put(cls, true);
					try {
						Constructor constructor = cls.getConstructor(Collection.class);
						variableFactories.remove(factory);
						variableFactories.add(constructor.newInstance(scenarios));
					} catch (NoSuchMethodException e) {
					}
				}
			}
		}

		// loop through and add missing variable factories
		for (Class<? extends VariableFactory> cls : defaults) {
			if (!hasFactories.get(cls)) {
				try {
					Constructor constructor = cls.getConstructor(Collection.class);
					variableFactories.add(constructor.newInstance(scenarios));
				} catch (NoSuchMethodException e) {
					variableFactories.add(cls.newInstance());
				}
			}
		}

		put(VARIABLE_FACTORIES_KEY, variableFactories);
	}

	/**
	 * Add the default variable factories
	 * 
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	@SuppressWarnings({ "rawtypes" })
	protected void addDefaultInitialSolutionFactories() throws NoSuchMethodException, SecurityException,
			InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		Collection<Class<? extends InitialSolutionFactory>> defaults = getDefaultInitialSolutionFactories();

		// get the data already stored in the model
		Collection<Object> initialSolutionFactories = getCollection(INITIAL_SOLUTION_FACTORIES_KEY, Object.class);
		Collection<Scenario> scenarios = getCollection(SCENARIOS_KEY, Scenario.class);

		// set up the booleans for checking if factories are include
		HashMap<Class<? extends InitialSolutionFactory>, Boolean> hasFactories = new HashMap<Class<? extends InitialSolutionFactory>, Boolean>();
		for (Class<? extends InitialSolutionFactory> cls : defaults) {
			hasFactories.put(cls, false);
		}

		// loop through and see if default variables are in the list
		ArrayList<Object> copy = new ArrayList<Object>();
		copy.addAll(initialSolutionFactories);
		for (Object factory : copy) {
			for (Class<? extends InitialSolutionFactory> cls : defaults) {
				if (cls.isInstance(factory) || factory.toString().equals(cls.getCanonicalName())) {
					hasFactories.put(cls, true);
					try {
						Constructor constructor = cls.getConstructor(Collection.class);
						initialSolutionFactories.remove(factory);
						initialSolutionFactories.add(constructor.newInstance(scenarios));
					} catch (NoSuchMethodException e) {
					}
				}
			}
		}

		// loop through and add missing variable factories
		for (Class<? extends InitialSolutionFactory> cls : defaults) {
			if (!hasFactories.get(cls)) {
				try {
					Constructor constructor = cls.getConstructor(Collection.class);
					initialSolutionFactories.add(constructor.newInstance(scenarios));
				} catch (NoSuchMethodException e) {
					initialSolutionFactories.add(cls.newInstance());
				}
			}
		}

		put(INITIAL_SOLUTION_FACTORIES_KEY, initialSolutionFactories);
	}

	/**
	 * Add the default variable factories
	 * 
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	@SuppressWarnings({ "rawtypes" })
	protected void addDefaultConstraints()
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		Collection<Class<? extends ConstraintFactory>> defaults = getDefaultConstraintFactories();

		// get the data already stored in the model
		Collection<Object> constraintFactories = getCollection(CONSTRAINTS_KEY, Object.class);
		Collection<Scenario> scenarios = getCollection(SCENARIOS_KEY, Scenario.class);

		// set up the booleans for checking if factories are include
		HashMap<Class<? extends ConstraintFactory>, Boolean> hasFactories = new HashMap<Class<? extends ConstraintFactory>, Boolean>();
		for (Class<? extends ConstraintFactory> cls : defaults) {
			hasFactories.put(cls, false);
		}

		// loop through and see if default coupled variables are in the list
		ArrayList<Object> copy = new ArrayList<Object>();
		copy.addAll(constraintFactories);
		for (Object factory : copy) {
			for (Class<? extends ConstraintFactory> cls : defaults) {
				if (cls.isInstance(factory) || factory.toString().equals(cls.getCanonicalName())) {
					hasFactories.put(cls, true);
					try {
						Constructor constructor = cls.getConstructor(Collection.class);
						constraintFactories.remove(factory);
						constraintFactories.add(constructor.newInstance(scenarios));
					} catch (NoSuchMethodException e) {
					}
				}
			}
		}

		// loop through and add missing variable factories
		for (Class<? extends ConstraintFactory> cls : defaults) {
			if (!hasFactories.get(cls)) {
				try {
					Constructor constructor = cls.getConstructor(Collection.class);
					constraintFactories.add(constructor.newInstance(scenarios));
				} catch (NoSuchMethodException e) {
					constraintFactories.add(cls.newInstance());
				}
			}
		}

		put(CONSTRAINTS_KEY, constraintFactories);
	}

	/**
	 * Add the default assignment factories
	 * 
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	@SuppressWarnings({ "rawtypes" })
	protected void addDefaultAssignmentFactories()
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		Collection<Class<? extends AssignmentFactory>> defaults = getDefaultAssignmentFactories();

		// get the data already stored in the model
		Collection<Object> assignmentFactories = getCollection(ASSIGNMENT_FACTORIES_KEY, Object.class);
		Collection<Scenario> scenarios = getCollection(SCENARIOS_KEY, Scenario.class);

		// set up the booleans for checking if factories are include
		HashMap<Class<? extends AssignmentFactory>, Boolean> hasFactories = new HashMap<Class<? extends AssignmentFactory>, Boolean>();
		for (Class<? extends AssignmentFactory> cls : defaults) {
			hasFactories.put(cls, false);
		}

		// loop through and see if default coupled variables are in the list
		ArrayList<Object> copy = new ArrayList<Object>();
		copy.addAll(assignmentFactories);
		for (Object factory : copy) {
			for (Class<? extends AssignmentFactory> cls : defaults) {
				if (cls.isInstance(factory) || factory.toString().equals(cls.getCanonicalName())) {
					hasFactories.put(cls, true);
					try {
						Constructor constructor = cls.getConstructor(Collection.class);
						assignmentFactories.remove(factory);
						assignmentFactories.add(constructor.newInstance(scenarios));
					} catch (NoSuchMethodException e) {
					}
				}
			}
		}

		// loop through and add missing variable factories
		for (Class<? extends AssignmentFactory> cls : defaults) {
			if (!hasFactories.get(cls)) {
				try {
					Constructor constructor = cls.getConstructor(Collection.class);
					assignmentFactories.add(constructor.newInstance(scenarios));
				} catch (NoSuchMethodException e) {
					assignmentFactories.add(cls.newInstance());
				}
			}
		}

		put(ASSIGNMENT_FACTORIES_KEY, assignmentFactories);
	}

	/**
	 * Add default objective function factories
	 * 
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	@SuppressWarnings("rawtypes")
	protected void addDefaultObjectiveFunctions()
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		Collection<Class<? extends ObjectiveFunctionFactory>> defaults = getDefaultObjectiveFunctionFactories();

		// get the data already stored in the model
		Collection<Object> objFactories = getCollection(OBJECTIVE_FUNCTIONS_KEY, Object.class);
		Collection<Scenario> scenarios = getCollection(SCENARIOS_KEY, Scenario.class);

		// set up the booleans for checking if factories are include
		HashMap<Class<? extends ObjectiveFunctionFactory>, Boolean> hasFactories = new HashMap<Class<? extends ObjectiveFunctionFactory>, Boolean>();
		for (Class<? extends ObjectiveFunctionFactory> cls : defaults) {
			hasFactories.put(cls, false);
		}

		// loop through and see if default coupled variables are in the list
		ArrayList<Object> copy = new ArrayList<Object>();
		copy.addAll(objFactories);
		for (Object factory : copy) {
			for (Class<? extends ObjectiveFunctionFactory> cls : defaults) {
				if (cls.isInstance(factory) || factory.toString().equals(cls.getCanonicalName())) {
					hasFactories.put(cls, true);
					try {
						Constructor constructor = cls.getConstructor(Collection.class);
						objFactories.remove(factory);
						objFactories.add(constructor.newInstance(scenarios));
					} catch (NoSuchMethodException e) {
					}
				}
			}
		}

		// loop through and add missing variable factories
		for (Class<? extends ObjectiveFunctionFactory> cls : defaults) {
			if (!hasFactories.get(cls)) {
				try {
					Constructor constructor = cls.getConstructor(Collection.class);
					objFactories.add(constructor.newInstance(scenarios));
				} catch (NoSuchMethodException e) {
					objFactories.add(cls.newInstance());
				}
			}
		}

		put(OBJECTIVE_FUNCTIONS_KEY, objFactories);
	}

}
