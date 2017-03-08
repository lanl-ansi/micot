package gov.lanl.micot.application.rdt.algorithm.ep.sbd;

import java.util.ArrayList;
import java.util.Collection;

import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.optimize.OptimizerFlags;
import gov.lanl.micot.infrastructure.optimize.mathprogram.constraint.ScenarioConstraintFactory;
import gov.lanl.micot.infrastructure.optimize.sbd.ScenarioBasedDecompositionOptimizerFlags;
import gov.lanl.micot.application.rdt.algorithm.AlgorithmConstants;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.ResilienceExpansionFlags;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.constraint.scenario.ScenarioChanceConstrainedCriticalRealLoadMetConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.constraint.scenario.ScenarioChanceConstrainedCriticalReactiveLoadMetConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.constraint.scenario.ScenarioChanceConstrainedRealLoadMetConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.constraint.scenario.ScenarioChanceConstrainedReactiveLoadMetConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.constraint.scenario.ScenarioChanceConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.constraint.scenario.ScenarioCriticalRealLoadMetConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.constraint.scenario.ScenarioCriticalReactiveLoadMetConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.constraint.scenario.ScenarioCriticalRealLoadUnMetBoundConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.constraint.scenario.ScenarioCriticalReactiveLoadUnMetBoundConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.constraint.scenario.ScenarioRealLoadMetConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.constraint.scenario.ScenarioReactiveLoadMetConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.constraint.scenario.ScenarioRealLoadUnMetBoundConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.constraint.scenario.ScenarioReactiveLoadUnMetBoundConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.objective.scenario.ScenarioCriticalRealLoadUnMetObjectiveFunctionFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.objective.scenario.ScenarioCriticalReactiveLoadUnMetObjectiveFunctionFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.objective.scenario.ScenarioRealLoadUnMetObjectiveFunctionFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.objective.scenario.ScenarioReactiveLoadUnMetObjectiveFunctionFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.variable.scenario.ScenarioCriticalRealLoadUnMetVariableFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.variable.scenario.ScenarioCriticalReactiveLoadUnMetVariableFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.variable.scenario.ScenarioRealLoadUnMetVariableFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.variable.scenario.ScenarioReactiveLoadUnMetVariableFactory;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgramFlags;

/**
 * Flags used for expansion planning algorithms that take into account the full optimization model
 * when computing the optimal expansion plan
 * 
 * @author Russell Bent
 */
public class SBDResilienceFlags extends ScenarioBasedDecompositionOptimizerFlags {

	private static final long serialVersionUID = 1L;

  public static final String OUTER_PREFIX = "Outer";
  public static final String INNER_PREFIX = "Inner";
	
	public static final String OUTER_MATH_PROGRAM_FACTORY_KEY = OUTER_PREFIX + MathematicalProgramFlags.MATHEMATICAL_PROGRAM_FACTORY_FLAG;
  public static final String INNER_MATH_PROGRAM_FACTORY_KEY = INNER_PREFIX + MathematicalProgramFlags.MATHEMATICAL_PROGRAM_FACTORY_FLAG;
  
	/**
	 * Constructor
	 */
	public SBDResilienceFlags() {
		super();
		init(new ResilienceExpansionFlags());
	}
	
	/**
	 * Constructor
	 * 
	 * @param flags
	 */
	public SBDResilienceFlags(OptimizerFlags flags) {
		super(flags);
		init(new ResilienceExpansionFlags(flags));
	}

	/**
	 * Initialize everything we need to run the inner and outer problem
	 * @param defaults
	 */
	@SuppressWarnings({ "rawtypes"})
  private void init(ResilienceExpansionFlags defaults) {
	  OptimizerFlags innerFlags = get(INNER_OPTIMIZER_FLAGS_KEY, OptimizerFlags.class);
    OptimizerFlags outerFlags = get(OUTER_OPTIMIZER_FLAGS_KEY, OptimizerFlags.class);
	  
	  // initialize inner and outer math program flag
    if (get(OUTER_MATH_PROGRAM_FACTORY_KEY) == null) {
      put(OUTER_MATH_PROGRAM_FACTORY_KEY,defaults.get(ResilienceExpansionFlags.MATH_PROGRAM_FACTORY_KEY));
    }
    if (get(INNER_MATH_PROGRAM_FACTORY_KEY) == null) {
      put(INNER_MATH_PROGRAM_FACTORY_KEY,defaults.get(ResilienceExpansionFlags.MATH_PROGRAM_FACTORY_KEY));
    }
	      
	  // initialize everything but constraints/variables/objective/assignments
	  for (String key : defaults.keySet()) {
	    if (key.equals(CONSTRAINTS_KEY) || key.equals(ASSIGNMENT_FACTORIES_KEY) || key.equals(VARIABLE_FACTORIES_KEY) || key.equals(OBJECTIVE_FUNCTIONS_KEY)) {
	      continue;
	    }
	    
	    if (get(key) == null) {
	      put(key, defaults.get(key));
	    }
	  }
   
    double loadPercentage = getDouble(AlgorithmConstants.LOAD_MET_KEY);
    double criticalLoadPercentage = getDouble(AlgorithmConstants.CRITICAL_LOAD_MET_KEY);
	  
	  // initialize constraints/variables/objectives
    Collection<Object> constraints = defaults.getCollection(CONSTRAINTS_KEY, Object.class);  
    Collection<Object> objs = defaults.getCollection(OBJECTIVE_FUNCTIONS_KEY, Object.class);  
    Collection<Object> variables = defaults.getCollection(VARIABLE_FACTORIES_KEY, Object.class);  
    Collection<Object> assignments = defaults.getCollection(ASSIGNMENT_FACTORIES_KEY, Object.class);  
	  
    ArrayList<Object> outerConstraints = new ArrayList<Object>();
    ArrayList<Object> outerObjs = new ArrayList<Object>();
    ArrayList<Object> outerAssignments = new ArrayList<Object>();
    ArrayList<Object> outerVariables = new ArrayList<Object>();
    
    ArrayList<Object> innerConstraints = new ArrayList<Object>();
    ArrayList<Object> innerObjs = new ArrayList<Object>();
    ArrayList<Object> innerAssignments = new ArrayList<Object>();
    ArrayList<Object> innerVariables = new ArrayList<Object>();

    outerVariables.addAll(variables);
    outerAssignments.addAll(assignments);
    outerConstraints.addAll(constraints);
    outerObjs.addAll(objs);
    
    for (Object constraint : constraints) {
      if (constraint instanceof Class && ScenarioConstraintFactory.class.isAssignableFrom((Class)constraint)) {
        innerConstraints.add(constraint);        
      }      
      else if (constraint instanceof ScenarioConstraintFactory && !(constraint instanceof ScenarioChanceConstraint)
          && !(constraint instanceof ScenarioChanceConstrainedRealLoadMetConstraint) && !(constraint instanceof ScenarioChanceConstrainedCriticalRealLoadMetConstraint)
          && !(constraint instanceof ScenarioChanceConstrainedCriticalReactiveLoadMetConstraint) && !(constraint instanceof ScenarioChanceConstrainedReactiveLoadMetConstraint)
          && !(constraint instanceof ScenarioRealLoadMetConstraint) && !(constraint instanceof ScenarioReactiveLoadMetConstraint)
          && !(constraint instanceof ScenarioCriticalRealLoadMetConstraint) && !(constraint instanceof ScenarioCriticalReactiveLoadMetConstraint)) {
        innerConstraints.add(constraint);        
      }
    }
    
    innerConstraints.remove(ScenarioChanceConstraint.class);
    innerConstraints.remove(ScenarioChanceConstrainedRealLoadMetConstraint.class);
    innerConstraints.remove(ScenarioChanceConstrainedReactiveLoadMetConstraint.class);
    innerConstraints.remove(ScenarioChanceConstrainedCriticalRealLoadMetConstraint.class);
    innerConstraints.remove(ScenarioChanceConstrainedCriticalReactiveLoadMetConstraint.class);
    innerConstraints.remove(ScenarioRealLoadMetConstraint.class);
    innerConstraints.remove(ScenarioReactiveLoadMetConstraint.class);
    innerConstraints.remove(ScenarioCriticalRealLoadMetConstraint.class);
    innerConstraints.remove(ScenarioCriticalReactiveLoadMetConstraint.class);

    // These constraints essentially count the unmet load below a certain threshold
    innerConstraints.add(new ScenarioCriticalRealLoadUnMetBoundConstraint(criticalLoadPercentage, new ArrayList<Scenario>()));
    innerConstraints.add(new ScenarioCriticalReactiveLoadUnMetBoundConstraint(criticalLoadPercentage, new ArrayList<Scenario>()));
    innerConstraints.add(new ScenarioRealLoadUnMetBoundConstraint(loadPercentage, new ArrayList<Scenario>()));
    innerConstraints.add(new ScenarioReactiveLoadUnMetBoundConstraint(loadPercentage, new ArrayList<Scenario>()));

    // The objective to minimize the amount of unmet load below a certain threshold
    innerObjs.add(new ScenarioCriticalRealLoadUnMetObjectiveFunctionFactory(new ArrayList<Scenario>()));
    innerObjs.add(new ScenarioCriticalReactiveLoadUnMetObjectiveFunctionFactory(new ArrayList<Scenario>()));
    innerObjs.add(new ScenarioRealLoadUnMetObjectiveFunctionFactory(new ArrayList<Scenario>()));
    innerObjs.add(new ScenarioReactiveLoadUnMetObjectiveFunctionFactory(new ArrayList<Scenario>()));

    innerVariables.addAll(variables);
    innerVariables.add(new ScenarioCriticalRealLoadUnMetVariableFactory(new ArrayList<Scenario>()));
    innerVariables.add(new ScenarioCriticalReactiveLoadUnMetVariableFactory(new ArrayList<Scenario>()));
    innerVariables.add(new ScenarioRealLoadUnMetVariableFactory(new ArrayList<Scenario>()));
    innerVariables.add(new ScenarioReactiveLoadUnMetVariableFactory(new ArrayList<Scenario>()));
        
    innerAssignments.addAll(assignments);
        
    innerFlags.put(CONSTRAINTS_KEY, innerConstraints);
    innerFlags.put(OBJECTIVE_FUNCTIONS_KEY, innerObjs);
    innerFlags.put(VARIABLE_FACTORIES_KEY, innerVariables);
    innerFlags.put(ASSIGNMENT_FACTORIES_KEY, innerAssignments);
    
    outerFlags.put(CONSTRAINTS_KEY, outerConstraints);
    outerFlags.put(OBJECTIVE_FUNCTIONS_KEY, outerObjs);
    outerFlags.put(VARIABLE_FACTORIES_KEY, outerVariables);
    outerFlags.put(ASSIGNMENT_FACTORIES_KEY, outerAssignments);  
	}
	
	  
}
