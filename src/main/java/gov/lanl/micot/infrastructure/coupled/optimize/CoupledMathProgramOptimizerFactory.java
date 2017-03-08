package gov.lanl.micot.infrastructure.coupled.optimize;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import gov.lanl.micot.infrastructure.coupled.model.CoupledModel;
import gov.lanl.micot.infrastructure.coupled.model.CoupledNode;
import gov.lanl.micot.infrastructure.model.Model;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.optimize.Optimizer;
import gov.lanl.micot.infrastructure.optimize.OptimizerFactory;
import gov.lanl.micot.infrastructure.optimize.OptimizerFlags;
import gov.lanl.micot.infrastructure.optimize.mathprogram.MathProgramOptimizerFlags;
import gov.lanl.micot.infrastructure.project.AlgorithmConfiguration;
import gov.lanl.micot.infrastructure.project.ProjectConfiguration;
import gov.lanl.micot.infrastructure.project.ScenarioConfiguration;

/**
 * A factory for creating optimization models for coupled systems
 * @author Russell Bent
 */
public class CoupledMathProgramOptimizerFactory implements OptimizerFactory<CoupledNode, CoupledModel> {
	
	/**
	 *  Constructor
	 */
	public CoupledMathProgramOptimizerFactory() {		
	}
	
	/**
	 * Add the variable factories
	 * @param flags
	 * @param simulator
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
  protected void addVariableFactories(OptimizerFlags flags,  CoupledMathProgramOptimizer simulator) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
	  Collection<Object> variableFactories = flags.getCollection(CoupledMathProgramOptimizerFlags.VARIABLE_FACTORIES_KEY, Object.class);
    for (Object f : variableFactories) {
      if (f instanceof VariableFactory) {
        simulator.addVariableFactory((VariableFactory) f);
      }
      else {
        simulator.addVariableFactory((VariableFactory)Class.forName(f.toString().trim()).newInstance());
      }
    }
    
    Map<Class<? extends Model>, Collection<Object>> map = flags.get(CoupledMathProgramOptimizerFlags.INDIVIDUAL_VARIABLE_FACTORIES_KEY, Map.class);
    for (Class<? extends Model> cls : map.keySet()) {
      for (Object f : map.get(cls)) {
        if (f instanceof gov.lanl.micot.infrastructure.optimize.mathprogram.variable.VariableFactory) {
          simulator.addVariableFactory(cls, (gov.lanl.micot.infrastructure.optimize.mathprogram.variable.VariableFactory) f);
        }
        else {
          simulator.addVariableFactory(cls, (gov.lanl.micot.infrastructure.optimize.mathprogram.variable.VariableFactory)Class.forName(f.toString().trim()).newInstance());
        }
      }
    }    
	}
	
	/**
	 * Add the constraints
	 * @param flags
	 * @param simulator
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
  protected void addConstraints(OptimizerFlags flags,  CoupledMathProgramOptimizer simulator) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
	  Collection<Object> constraints = flags.getCollection(CoupledMathProgramOptimizerFlags.CONSTRAINTS_KEY, Object.class);
    for (Object obj : constraints) {
      ConstraintFactory constraint = null;
      
      if (obj instanceof String) {
        try {
          constraint = (ConstraintFactory) Class.forName((String)obj).newInstance();
        }
        catch (Exception e) {
          e.printStackTrace();
        }
      }
      else {
        constraint = (ConstraintFactory)obj;
      }   
      simulator.addConstraint(constraint);
    }
    
    Map<Class<? extends Model>, Collection<Object>> map = flags.get(CoupledMathProgramOptimizerFlags.INDIVIDUAL_CONSTRAINTS_KEY, Map.class);
    for (Class<? extends Model> cls : map.keySet()) {
      for (Object f : map.get(cls)) {
        if (f instanceof gov.lanl.micot.infrastructure.optimize.mathprogram.constraint.ConstraintFactory) {
          simulator.addConstraint(cls, (gov.lanl.micot.infrastructure.optimize.mathprogram.constraint.ConstraintFactory) f);
        }
        else {
          simulator.addConstraint(cls, (gov.lanl.micot.infrastructure.optimize.mathprogram.constraint.ConstraintFactory)Class.forName(f.toString().trim()).newInstance());
        }
      }
    }    
	}
	
	/**
	 * Adds the variables assignments
	 * @param flags
	 * @param simulator
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
  protected void addVariableAssignments(OptimizerFlags flags,  CoupledMathProgramOptimizer simulator) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
	  Collection<Object> assignments = flags.getCollection(CoupledMathProgramOptimizerFlags.ASSIGNMENT_FACTORIES_KEY, Object.class);
    for (Object obj : assignments) {
      AssignmentFactory f = null;
      
      if (obj instanceof String) {
        try {
          f = (AssignmentFactory) Class.forName((String)obj).newInstance();
        }
        catch (Exception e) {
          e.printStackTrace();
        }
      }
      else {
        f = (AssignmentFactory)obj;
      }   
      simulator.addAssignmentFactory(f);
    }
    
    
    Map<Class<? extends Model>, Collection<Object>> map = flags.get(CoupledMathProgramOptimizerFlags.INDIVIDUAL_ASSIGNMENT_FACTORIES_KEY, Map.class);
    for (Class<? extends Model> cls : map.keySet()) {
      for (Object f : map.get(cls)) {
        if (f instanceof gov.lanl.micot.infrastructure.optimize.mathprogram.assignment.AssignmentFactory) {
          simulator.addAssignmentFactory(cls, (gov.lanl.micot.infrastructure.optimize.mathprogram.assignment.AssignmentFactory) f);
        }
        else {
          simulator.addAssignmentFactory(cls, (gov.lanl.micot.infrastructure.optimize.mathprogram.assignment.AssignmentFactory)Class.forName(f.toString().trim()).newInstance());
        }
      }
    }    
	}

	/**
	 * Add the objective functions
	 * @param flags
	 * @param simulator
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
  protected void addObjectiveFunctions(OptimizerFlags flags,  CoupledMathProgramOptimizer simulator) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
	  Collection<Object> objs = flags.getCollection(CoupledMathProgramOptimizerFlags.OBJECTIVE_FUNCTIONS_KEY, Object.class);
    for (Object obj : objs) {
      ObjectiveFunctionFactory f = null;
      
      if (obj instanceof String) {
        try {
          f = (ObjectiveFunctionFactory) Class.forName((String)obj).newInstance();
        }
        catch (Exception e) {
          e.printStackTrace();
        }
      }
      else {
        f = (ObjectiveFunctionFactory)obj;
      }   
      simulator.addObjectiveFunctionFactory(f);
    }
    
    
    Map<Class<? extends Model>, Collection<Object>> map = flags.get(CoupledMathProgramOptimizerFlags.INDIVIDUAL_OBJECTIVE_FUNCTIONS_KEY, Map.class);
    for (Class<? extends Model> cls : map.keySet()) {
      for (Object f : map.get(cls)) {
        if (f instanceof gov.lanl.micot.infrastructure.optimize.mathprogram.objective.ObjectiveFunctionFactory) {
          simulator.addObjectiveFunctionFactory(cls, (gov.lanl.micot.infrastructure.optimize.mathprogram.objective.ObjectiveFunctionFactory) f);
        }
        else {
          simulator.addObjectiveFunctionFactory(cls, (gov.lanl.micot.infrastructure.optimize.mathprogram.objective.ObjectiveFunctionFactory)Class.forName(f.toString().trim()).newInstance());
        }
      }
    }    

    
	}
	
	@Override
	public CoupledMathProgramOptimizer createOptimizer(OptimizerFlags flags) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
	  CoupledMathProgramOptimizerFlags opfFlags = new CoupledMathProgramOptimizerFlags(flags);
	  String factory = opfFlags.getString(CoupledMathProgramOptimizerFlags.MATH_PROGRAM_FACTORY_KEY);	  
	  CoupledMathProgramOptimizer simulator = new CoupledMathProgramOptimizer();
	  simulator.setFactoryName(factory);
	  
	   // pass in the flags so the internal optimizer can use it..
    for (String key : flags.keySet()) {
      simulator.addMathProgramFlag(key, flags.get(key));
    }
	  
	  addVariableFactories(flags, simulator);
	  addConstraints(flags,simulator);
	  addVariableAssignments(flags,simulator);	  	  
	  addObjectiveFunctions(flags,simulator);
    	  
		return simulator;
	}

	/*@Override
  public Optimizer<CoupledModel> constructOptimizer(ProjectConfiguration configuration, CoupledModel model) {
    OptimizerFlags flags = new OptimizerFlags();
    flags.fill(configuration.getAlgorithmFlags());
    try {
      return createOptimizer(flags);
    }
    catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }*/

  @Override
  public Optimizer<CoupledNode, CoupledModel> constructOptimizer(ProjectConfiguration projectConfiguration, AlgorithmConfiguration configuration, CoupledModel model) {
    OptimizerFlags flags = new OptimizerFlags();
    flags.fill(configuration.getAlgorithmFlags());
    Collection<Scenario> scenarios = new ArrayList<Scenario>();
    for (ScenarioConfiguration sc : projectConfiguration.getScenarioConfigurations()) {
      scenarios.add(sc.getScenario());
    }    
    flags.put(MathProgramOptimizerFlags.SCENARIOS_KEY, scenarios);
    
    try {
      return createOptimizer(flags);
    }
    catch (Exception e) {
      e.printStackTrace();
      return null;
    } 
  }
 


}
