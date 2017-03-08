package gov.lanl.micot.infrastructure.coupled.optimize;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import gov.lanl.micot.infrastructure.coupled.model.CoupledModel;
import gov.lanl.micot.infrastructure.coupled.model.CoupledNode;
import gov.lanl.micot.infrastructure.model.Model;
import gov.lanl.micot.infrastructure.optimize.mathprogram.MathProgramOptimizer;
import gov.lanl.micot.infrastructure.optimize.mathprogram.assignment.AssignmentFactory;
import gov.lanl.micot.infrastructure.optimize.mathprogram.constraint.ConstraintFactory;
import gov.lanl.micot.infrastructure.optimize.mathprogram.objective.ObjectiveFunctionFactory;
import gov.lanl.micot.infrastructure.optimize.mathprogram.variable.VariableFactory;
import gov.lanl.micot.util.math.solver.Solution;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.SolverException;
import gov.lanl.micot.util.math.solver.mathprogram.DefaultMathematicalProgramFactory;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;
import gov.lanl.micot.util.time.Timer;

/**
 * This class contains basic information about an opf simulator maximizes load
 * served
 * 
 * @author Russell Bent
 */
public class CoupledMathProgramOptimizer extends MathProgramOptimizer<CoupledNode, CoupledModel> {

  private Map<Class<? extends Model>, Collection<VariableFactory<?, ?>>> variableFactories = null;
  private Map<Class<? extends Model>, Collection<ConstraintFactory<?, ?>>> constraintFactories = null;
  private Map<Class<? extends Model>, Collection<ObjectiveFunctionFactory<?, ?>>> objectiveFactories = null;
  private Map<Class<? extends Model>, Collection<AssignmentFactory<?, ?>>> assignmentFactories = null;

  /**
   * Constructor
   * 
   * @param nextGenerationPFWFilename
   */
  protected CoupledMathProgramOptimizer() {
    super();
    variableFactories = new HashMap<Class<? extends Model>, Collection<VariableFactory<?, ?>>>();
    constraintFactories = new HashMap<Class<? extends Model>, Collection<ConstraintFactory<?, ?>>>();
    objectiveFactories = new HashMap<Class<? extends Model>, Collection<ObjectiveFunctionFactory<?, ?>>>();
    assignmentFactories = new HashMap<Class<? extends Model>, Collection<AssignmentFactory<?, ?>>>();
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Override
  public boolean solve(CoupledModel model) {
    MathematicalProgram problem = DefaultMathematicalProgramFactory.getInstance().constructMathematicalProgram(getFlags());

    try {
      // create the variables for the coupled model
      for (VariableFactory<CoupledNode, CoupledModel> variableFactory : getVariableFactories()) {
        variableFactory.createVariables(problem, model);
      }

      // create the variables for the individual models
      for (Class<? extends Model> cls : variableFactories.keySet()) {
        Collection<? extends Model> models = model.getModels(cls);
        for (VariableFactory variableFactory : variableFactories.get(cls)) {
          for (Model m : models) {
            variableFactory.createVariables(problem,  m);
          }
        }
      }

      // create the objective function for the coupled model
      for (ObjectiveFunctionFactory<CoupledNode, CoupledModel> factory : getObjectiveFunctionFactories()) {
        factory.addCoefficients(problem, model);
      }

      // create the objective function for the individual model
      for (Class<? extends Model> cls : objectiveFactories.keySet()) {
        Collection<? extends Model> models = model.getModels(cls);
        for (ObjectiveFunctionFactory factory : objectiveFactories.get(cls)) {
          for (Model m : models) {
            factory.addCoefficients(problem, m);
          }
        }
      }

      // create the constraints for the coupled models
      for (ConstraintFactory<CoupledNode, CoupledModel> constraint : getConstraints()) {
        constraint.constructConstraint(problem,  model);
      }

      // create the constraints for the individual models
      for (Class<? extends Model> cls : constraintFactories.keySet()) {
        Collection<? extends Model> models = model.getModels(cls);
        for (ConstraintFactory constraint : constraintFactories.get(cls)) {
          for (Model m : models) {
            constraint.constructConstraint(problem,  m);
          }
        }
      }

      Solution solution = null;
      try {
        Timer timer = new Timer();
        timer.startTimer();
        solution = problem.solve();
        setCPUTime(getCPUTime() + timer.getCPUMinutesDec());
        objectiveValue += solution.getObjectiveValue();
      } 
      catch (SolverException e) {
        solution = new Solution(Double.NaN, false);
        for (Variable variable : problem.getVariables()) {
          solution.addValue(variable, 0.0);
        }

        // do the assignment for the coupled model
        for (AssignmentFactory<CoupledNode, CoupledModel> assignmentFactory : getAssignmentFactories()) {
          assignmentFactory.performAssignment(model, problem, solution);
        }

        // do the assignment for the individual models
        for (Class<? extends Model> cls : assignmentFactories.keySet()) {
          Collection<? extends Model> models = model.getModels(cls);
          for (AssignmentFactory assignmentFactory : assignmentFactories.get(cls)) {
            for (Model m : models) {
              assignmentFactory.performAssignment(m, problem, solution);
            }
          }
        }
        throw e;
      }

      // do the assignment for the coupled model
      for (AssignmentFactory<CoupledNode, CoupledModel> assignmentFactory : getAssignmentFactories()) {
        assignmentFactory.performAssignment(model, problem, solution);
      }

      // do the assignment for the individual models
      for (Class<? extends Model> cls : assignmentFactories.keySet()) {
        Collection<? extends Model> models = model.getModels(cls);
        for (AssignmentFactory assignmentFactory : assignmentFactories.get(cls)) {
          for (Model m : models) {
            assignmentFactory.performAssignment(m, problem, solution);
          }
        }
      }
    } 
    catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }
  
  /**
   * Add the variable factories for a specific model factory
   * @param factory
   */
  public void addVariableFactory(Class<? extends Model> cls, VariableFactory<?,?> factory) {
    if (variableFactories.get(cls) == null) {
      variableFactories.put(cls, new ArrayList<VariableFactory<?,?>>());
    }    
    variableFactories.get(cls).add(factory);
  }
  
  /**
   * Add the variable factories
   * @param factory
   */
  public void addAssignmentFactory(Class<? extends Model> cls, AssignmentFactory<?,?> factory) {
    if (assignmentFactories.get(cls) == null) {
      assignmentFactories.put(cls, new ArrayList<AssignmentFactory<?,?>>());
    }    
    assignmentFactories.get(cls).add(factory);
  }
  
  /**
   * Add the objective function
   * @param factory
   */
  public void addObjectiveFunctionFactory(Class<? extends Model> cls, ObjectiveFunctionFactory<?,?> factory) {
    if (objectiveFactories.get(cls) == null) {
      objectiveFactories.put(cls, new ArrayList<ObjectiveFunctionFactory<?,?>>());
    }    
    objectiveFactories.get(cls).add(factory);
  }


  /**
   * Add a constraint to the system
   * @param constraint
   */
  public void addConstraint(Class<? extends Model> cls, ConstraintFactory<?,?> constraint) {
    if (constraintFactories.get(cls) == null) {
      constraintFactories.put(cls, new ArrayList<ConstraintFactory<?,?>>());
    }    
    constraintFactories.get(cls).add(constraint);
  }

}
