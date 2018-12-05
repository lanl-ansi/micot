package gov.lanl.micot.deprecated.sbd;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import gov.lanl.micot.infrastructure.model.Asset;
import gov.lanl.micot.infrastructure.model.Model;
import gov.lanl.micot.infrastructure.model.Node;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.optimize.OptimizerListener;
import gov.lanl.micot.infrastructure.optimize.OptimizerStatistics;
import gov.lanl.micot.infrastructure.optimize.mathprogram.assignment.AssignmentFactory;
import gov.lanl.micot.infrastructure.optimize.mathprogram.assignment.ScenarioAssignmentFactory;
import gov.lanl.micot.infrastructure.optimize.mathprogram.constraint.ConstraintFactory;
import gov.lanl.micot.infrastructure.optimize.mathprogram.constraint.ScenarioConstraintFactory;
import gov.lanl.micot.infrastructure.optimize.mathprogram.initialsolution.InitialSolutionFactory;
import gov.lanl.micot.infrastructure.optimize.mathprogram.objective.ObjectiveFunctionFactory;
import gov.lanl.micot.infrastructure.optimize.mathprogram.objective.ScenarioObjectiveFunctionFactory;
import gov.lanl.micot.infrastructure.optimize.mathprogram.variable.ScenarioVariableFactory;
import gov.lanl.micot.infrastructure.optimize.mathprogram.variable.VariableFactory;
import gov.lanl.micot.util.math.MathUtils;
import gov.lanl.micot.util.math.solver.Solution;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.InvalidConstraintException;
import gov.lanl.micot.util.math.solver.exception.InvalidObjectiveException;
import gov.lanl.micot.util.math.solver.exception.InvalidVariableException;
import gov.lanl.micot.util.math.solver.exception.NoConstraintException;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.SolverException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.DefaultMathematicalProgramFactory;
import gov.lanl.micot.util.math.solver.mathprogram.LinearObjective;
import gov.lanl.micot.util.math.solver.mathprogram.LinearObjectiveMaximize;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgramFlags;
import gov.lanl.micot.util.math.solver.mathprogram.QuadraticObjective;
import gov.lanl.micot.util.math.solver.mathprogram.QuadraticObjectiveMaximize;
import gov.lanl.micot.util.math.solver.quadraticprogram.QuadraticProgram;
import gov.lanl.micot.util.time.Timer;

/**
 * This class contains basic information about an optimization routine that uses 
 * scenario based decomposition
 * 
 * @author Russell Bent
 */
public abstract class ScenarioBasedDecompositionOptimizerImpl<N extends Node, M extends Model> implements ScenarioBasedDecompositionOptimizer<N,M> {

  private MathematicalProgram outerProblem = null;
  private Map<Scenario,MathematicalProgram> innerProblems = null;
    
  private MathematicalProgramFlags innerFlags = null;
  private MathematicalProgramFlags outerFlags = null;
  
  private Collection<Scenario> scenarios = null;
  
  private Collection<VariableFactory<N,M>> outerVariableFactories = null;
  private Collection<ConstraintFactory<N,M>> outerConstraints = null;
  private Collection<ObjectiveFunctionFactory<N,M>> outerObjectiveFunctionFactories = null;
  private Collection<AssignmentFactory<N,M>> outerAssignmentFactories = null;
  private Collection<InitialSolutionFactory<M>> outerInitialSolutionFactories = null;

  private Collection<VariableFactory<N,M>> innerVariableFactories = null;
  private Collection<ConstraintFactory<N,M>> innerConstraints = null;
  private Collection<ObjectiveFunctionFactory<N,M>> innerObjectiveFunctionFactories = null;
  private Collection<AssignmentFactory<N,M>> innerAssignmentFactories = null;
  private Collection<InitialSolutionFactory<M>> innerInitialSolutionFactories = null;

  private double objectiveValue = 0;
  private double cpuTime = 0; 
  private boolean isFeasible = true;
  private OptimizerStatistics profile = null;
  
  private Set<OptimizerListener> listeners = null;
  
  /**
   * Constructor
   * 
   */
  protected ScenarioBasedDecompositionOptimizerImpl() {
    super();
    scenarios = new LinkedHashSet<Scenario>();
    listeners = new HashSet<OptimizerListener>();
    innerFlags = new MathematicalProgramFlags();
    outerFlags = new MathematicalProgramFlags();
    
    outerVariableFactories = new ArrayList<VariableFactory<N,M>>();
    outerConstraints = new ArrayList<ConstraintFactory<N,M>>();
    outerObjectiveFunctionFactories = new ArrayList<ObjectiveFunctionFactory<N,M>>();
    outerAssignmentFactories = new ArrayList<AssignmentFactory<N,M>>();
    outerInitialSolutionFactories = new ArrayList<InitialSolutionFactory<M>>();
    
    innerVariableFactories = new ArrayList<VariableFactory<N,M>>();
    innerConstraints = new ArrayList<ConstraintFactory<N,M>>();
    innerObjectiveFunctionFactories = new ArrayList<ObjectiveFunctionFactory<N,M>>();
    innerAssignmentFactories = new ArrayList<AssignmentFactory<N,M>>();
    innerInitialSolutionFactories = new ArrayList<InitialSolutionFactory<M>>();
    
  }
  
  /**
   * Set the scenarios of the scenario based decomposition
   * @param scenarios
   */
  public void setScenarios(Collection<Scenario> scenarios) {
    this.scenarios = scenarios;
  }

  /**
   * Set whether or not the solution is feasible or not
   * @param isFeasible
   */
  protected void setIsFeasible(boolean isFeasible) {
    this.isFeasible = isFeasible;
  }
  
  /**
   * Sets the objective value
   * @param obj
   */
  protected void setObjectiveValue(double obj) {
    this.objectiveValue = obj;
  }
  
  @Override
  public double getObjectiveValue() {
    return objectiveValue;
  }
  
  /**
   * Add a scenario to the set of scenarios
   * @param scenario
   */
  public void addScenario(Scenario scenario) {
    scenarios.add(scenario);
  }
  
  /**
   * Set the CPU Time
   * @param cpuTime
   * @return
   */
  protected void setCPUTime(double cpuTime) {
    this.cpuTime = cpuTime;
  }
  
  @Override
  public boolean solve(M model) {
            
    updateModelStatus(model);
          
    boolean state = true;
    try {
      setCPUTime(0);
      setObjectiveValue(0);
      run(model);
    }
    catch (Exception e) {
      e.printStackTrace();
      state = false;
    }
    
    setIsFeasible(state);
    return state;
  }

  /**
   * Solution for solving the island
   * 
   * @param nodes
   * @param phaseAngles
   * @param shadowPricesRanges 
   * @param shadowPrices 
   * @param indicies
   * @throws ClassNotFoundException
   * @throws IllegalAccessException
   * @throws InstantiationException
   * @throws SolverException
   * @throws DuplicateException
   * @throws NotFoundException
   * @throws InvalidException
   * @throws ScaleException
   * @throws ConvergenceException
   * @throws InfeasibleException
   * @throws UnboundedException
   * @throws NoSolutionException
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  protected void run(M model) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SolverException {
    Collection<Scenario> outerScenarios = new ArrayList<Scenario>();
    createOuterMathematicalProgram(model, outerScenarios); 
    createInnerMathematicalPrograms(model);

    Set<Scenario> scenariosNotInOuterProblem = new HashSet<Scenario>();
    scenariosNotInOuterProblem.addAll(scenarios);

    Timer timer = new Timer();
    timer.startTimer();
    
    profile = new OptimizerStatistics();
    
    HashMap<Scenario, Solution> innerSolutions = null;    
    try {
      do {
        Solution solution = outerProblem.solve();
        for (AssignmentFactory<N,M> assignmentFactory : getOuterAssignmentFactories()) {
          if (assignmentFactory instanceof ScenarioAssignmentFactory) {
            ((ScenarioAssignmentFactory) assignmentFactory).setScenarios(outerScenarios);
          }
          assignmentFactory.performAssignment(model, outerProblem, solution);
        }          
      
        innerSolutions = new HashMap<Scenario,Solution>();
        for (Scenario scenario : scenariosNotInOuterProblem) {
          MathematicalProgram innerProblem = innerProblems.get(scenario);
          updateInnerProblem(model, outerProblem, solution, innerProblem);
          Solution innerSolution = null;
          try { 
            innerSolution = innerProblem.solve();
            ArrayList<Scenario> innerScenario = new ArrayList<Scenario>();
            innerScenario.add(scenario);
            for (AssignmentFactory<N,M> assignmentFactory : getInnerAssignmentFactories()) {
              if (assignmentFactory instanceof ScenarioAssignmentFactory) {
                ((ScenarioAssignmentFactory) assignmentFactory).setScenarios(innerScenario);
              }
              assignmentFactory.performAssignment(model, innerProblem, innerSolution);
            }
          }
          catch (SolverException e) {
            innerSolution = new Solution(Double.NaN, false);
          }
          innerSolutions.put(scenario, innerSolution);                  
        }
      
        Scenario worstScenario = chooseScenario(innerSolutions, model);
        if (worstScenario != null) {
          updateOuterProblem(model, outerProblem, innerProblems.get(worstScenario), worstScenario);
          scenariosNotInOuterProblem.remove(worstScenario);
          outerScenarios.add(worstScenario);
        }
        setObjectiveValue(solution.getObjectiveValue());
        getProfile().putAll(outerProblem.getProfile());
    
      }
      while (notConverged(innerSolutions));    
    }
    catch (SolverException e) {
      Solution solution = new Solution(Double.NaN, false);
      for (Variable variable : outerProblem.getVariables()) {
        solution.addValue(variable, 0.0);
      }
      
      for (AssignmentFactory<N,M> assignmentFactory : getOuterAssignmentFactories()) {
        if (assignmentFactory instanceof ScenarioAssignmentFactory) {
          ((ScenarioAssignmentFactory) assignmentFactory).setScenarios(outerScenarios);
        }
        assignmentFactory.performAssignment(model, outerProblem, solution);
      }          
      throw e;
    }

    setCPUTime(timer.getCPUMinutesDec());
  }
  




  /**
   * Create the outer mathematical program
   * @param model
   * @throws InvalidObjectiveException
   * @throws InvalidVariableException 
   * @throws VariableExistsException 
   * @throws NoVariableException 
   * @throws InvalidConstraintException 
   * @throws NoConstraintException 
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  protected void createOuterMathematicalProgram(M model, Collection<Scenario> outerScenarios) throws InvalidObjectiveException, VariableExistsException, InvalidVariableException, NoVariableException, InvalidConstraintException, NoConstraintException {
    outerProblem = DefaultMathematicalProgramFactory.getInstance().constructMathematicalProgram(outerFlags);

    if (outerProblem instanceof QuadraticProgram) {
      QuadraticObjective qobj = new QuadraticObjectiveMaximize();
      outerProblem.setQuadraticObjective(qobj);
    }
    
    LinearObjective objective = new LinearObjectiveMaximize();
    outerProblem.setLinearObjective(objective);
        
    // create the variables
    for (VariableFactory<N,M> variableFactory: getOuterVariableFactories()) {
      if (variableFactory instanceof ScenarioVariableFactory) {
        ((ScenarioVariableFactory) variableFactory).setScenarios(outerScenarios);
      }      
      variableFactory.createVariables(outerProblem, model);
    }
    
    // create the objective function
    for (ObjectiveFunctionFactory<N,M> factory : getOuterObjectiveFunctionFactories()) {
      if (factory instanceof ScenarioObjectiveFunctionFactory) {
        ((ScenarioObjectiveFunctionFactory) factory).setScenarios(outerScenarios);
      }    
      factory.addCoefficients(outerProblem, model);      
    }
    
    // create the constraints
    for (ConstraintFactory<N,M> constraint : getOuterConstraints()) {
      if (constraint instanceof ScenarioConstraintFactory) {
        ((ScenarioConstraintFactory) constraint).setScenarios(outerScenarios);
      }
      constraint.constructConstraint(outerProblem, model);
    }
    
    // create the initial solution
    for (InitialSolutionFactory<M> factory : getOuterInitialSolutionFactories()) {
      factory.updateInitialSolution(model, outerProblem);
    }    
  }
  
  /**
   * Create the outer mathematical program
   * @param model
   * @throws InvalidObjectiveException
   * @throws InvalidVariableException 
   * @throws VariableExistsException 
   * @throws NoVariableException 
   * @throws InvalidConstraintException 
   * @throws NoConstraintException 
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  protected void createInnerMathematicalPrograms(M model) throws InvalidObjectiveException, VariableExistsException, InvalidVariableException, NoVariableException, InvalidConstraintException, NoConstraintException {
	innerProblems = new HashMap<Scenario,MathematicalProgram>();  
	  
    for (Scenario scenario : scenarios) {        
      MathematicalProgram innerProblem = DefaultMathematicalProgramFactory.getInstance().constructMathematicalProgram(innerFlags);
      Collection<Scenario> thisScenario = new ArrayList<Scenario>();
      thisScenario.add(scenario);
      
      
      if (innerProblem instanceof QuadraticProgram) {
        QuadraticObjective qobj = new QuadraticObjectiveMaximize();
        innerProblem.setQuadraticObjective(qobj);
      }
    
      LinearObjective objective = new LinearObjectiveMaximize();
      innerProblem.setLinearObjective(objective);
        
      // create the variables
      for (VariableFactory<N,M> variableFactory: getInnerVariableFactories()) {
        if (variableFactory instanceof ScenarioVariableFactory) {
          ((ScenarioVariableFactory) variableFactory).setScenarios(thisScenario);
        }        
        variableFactory.createVariables(innerProblem, model);
      }
    
      // create the objective function
      for (ObjectiveFunctionFactory<N,M> factory : getInnerObjectiveFunctionFactories()) {
        if (factory instanceof ScenarioObjectiveFunctionFactory) {
          ((ScenarioObjectiveFunctionFactory) factory).setScenarios(thisScenario);
        }       
        factory.addCoefficients(innerProblem, model);      
      }
    
      // create the constraints
      for (ConstraintFactory<N,M> constraint : getInnerConstraints()) {
        if (constraint instanceof ScenarioConstraintFactory) {
          ((ScenarioConstraintFactory) constraint).setScenarios(thisScenario);
        }
        constraint.constructConstraint(innerProblem, model);
      }
    
      // create the initial solution
      for (InitialSolutionFactory<M> factory : getInnerInitialSolutionFactories()) {
        factory.updateInitialSolution(model, innerProblem);
      } 
      innerProblems.put(scenario, innerProblem);
    }    
  }

  
  /**
   * A function for getting summary statistics about the performance of the algorithm
   * @return
   */
  public int getLastAlgorithmIterations() {   
    return 1;
  }
  
  /**
   * Sets all the asset deired status
   * @param model
   */
  protected void updateModelStatus(M model) {
    for (Asset asset : model.getAssets()) {
      boolean status = asset.getStatus();      
      Object failed = asset.getAttribute(Asset.IS_FAILED_KEY);
      if (failed != null) {
          status = status &= !(Boolean)failed;
        }
      asset.setStatus(status);
	  }
	}
  
  /**
   * Get the the flags
   * @return
   */
  protected MathematicalProgramFlags getOuterFlags() {
    return outerFlags;
  }
  
  /**
   * Get the the flags
   * @return
   */
  protected MathematicalProgramFlags getInnerFlags() {
    return innerFlags;
  }
  
  @Override
  public void addOuterVariableFactory(VariableFactory<N,M> factory) {
    outerVariableFactories.add(factory);
  }
  
  @Override
  public Collection<VariableFactory<N,M>> getOuterVariableFactories() {
    return outerVariableFactories;
  }
  
  @Override
  public void addOuterInitialSolutionFactory(InitialSolutionFactory<M> factory) {
    outerInitialSolutionFactories.add(factory);
  }
    
  @Override
  public Collection<InitialSolutionFactory<M>> getOuterInitialSolutionFactories() {
    return outerInitialSolutionFactories;
  }
  
  @Override
  public void addOuterAssignmentFactory(AssignmentFactory<N,M> factory) {
    outerAssignmentFactories.add(factory);
  }
  
  @Override
  public Collection<AssignmentFactory<N,M>> getOuterAssignmentFactories() {
    return outerAssignmentFactories;
  }
  
  @Override
  public void addOuterObjectiveFunctionFactory(ObjectiveFunctionFactory<N,M> factory) {
    outerObjectiveFunctionFactories.add(factory);
  }

  @Override
  public Collection<ObjectiveFunctionFactory<N,M>> getOuterObjectiveFunctionFactories() {
    return outerObjectiveFunctionFactories;
  }
  
  @Override
  public Collection<ConstraintFactory<N,M>> getOuterConstraints() {
    return outerConstraints;
  }

  @Override
  public void addOuterConstraint(ConstraintFactory<N,M> constraint) {
    outerConstraints.add(constraint);
  }

  @Override
  public void addInnerVariableFactory(VariableFactory<N,M> factory) {
    innerVariableFactories.add(factory);
  }
  
  @Override
  public Collection<VariableFactory<N,M>> getInnerVariableFactories() {
    return innerVariableFactories;
  }
  
  @Override
  public void addInnerInitialSolutionFactory(InitialSolutionFactory<M> factory) {
    innerInitialSolutionFactories.add(factory);
  }
    
  @Override
  public Collection<InitialSolutionFactory<M>> getInnerInitialSolutionFactories() {
    return innerInitialSolutionFactories;
  }
  
  @Override
  public void addInnerAssignmentFactory(AssignmentFactory<N,M> factory) {
    innerAssignmentFactories.add(factory);
  }
  
  @Override
  public Collection<AssignmentFactory<N,M>> getInnerAssignmentFactories() {
    return innerAssignmentFactories;
  }
  
  @Override
  public void addInnerObjectiveFunctionFactory(ObjectiveFunctionFactory<N,M> factory) {
    innerObjectiveFunctionFactories.add(factory);
  }

  @Override
  public Collection<ObjectiveFunctionFactory<N,M>> getInnerObjectiveFunctionFactories() {
    return innerObjectiveFunctionFactories;
  }
  
  @Override
  public Collection<ConstraintFactory<N,M>> getInnerConstraints() {
    return innerConstraints;
  }

  @Override
  public void addInnerConstraint(ConstraintFactory<N,M> constraint) {
    innerConstraints.add(constraint);
  }  

  /**
   * Check if a convergence criteria has been met
   * @param innerSolutions
   * @return
   */
  protected abstract boolean notConverged(HashMap<Scenario, Solution> innerSolutions);

  /**
   * Get the profile
   * @return
   */
  public OptimizerStatistics getProfile() {
    return profile;
  }

  @Override
  public void addOptimizerListener(OptimizerListener listener) {
    listeners.add(listener);
  }

  @Override
  public void addOptimizerListeners(Collection<OptimizerListener> listeners) {
    this.listeners.addAll(listeners);
  }

  @Override
  public void removeOptimizerListener(OptimizerListener listener) {
    listeners.remove(listener);
  }

  @Override
  public boolean isFeasible() {
    return isFeasible;
  }

  @Override
  public double getCPUTime() {
    return cpuTime;
  }

  @Override
  public void addAssignmentFactory(AssignmentFactory<N, M> factory) {
    System.err.println("Warning: Unknown Assignment Factory.  Adding to the outer problem");
    addOuterAssignmentFactory(factory);
  }

  @Override
  public void addConstraint(ConstraintFactory<N, M> constraint) {
    System.err.println("Warning: Unknown constraint factory. Adding to outer problem");
    addOuterConstraint(constraint);
  }

  @Override
  public void addVariableFactory(VariableFactory<N, M> factory) {
    System.err.println("Warning: Unknown variable factory. Adding to outer problem");
    addOuterVariableFactory(factory);
  }

  @Override
  public void addObjectiveFunctionFactory(ObjectiveFunctionFactory<N, M> factory) {
    System.err.println("Warning: Unknown objective function factory. Adding to outer problem");
    addOuterObjectiveFunctionFactory(factory);
  }
  
  /**
   * Add an inner program flag
   * @param key
   * @param object
   */
  public void addInnerMathProgramFlag(String key, Object object) {
    innerFlags.put(key, object);
  }
  
  /**
   * Add an inner program flag
   * @param key
   * @param object
   */
  public void addOuterMathProgramFlag(String key, Object object) {
    outerFlags.put(key, object);
  }

  
  /**
   * (Overridable) function for updating the inner problem
   * @param model
   * @param outerProblem
   * @param solution
   * @param innerProblem
   */
  protected void updateInnerProblem(M model, MathematicalProgram outerProblem, Solution solution, MathematicalProgram innerProblem) {
    for (Variable outerVariable : outerProblem.getVariables()) {
      Variable innerVariable = innerProblem.getVariable(outerVariable.getName());
      if (innerVariable != null) {
        double value = solution.getValueDouble(outerVariable);
        innerProblem.addBounds(innerVariable, value, value);
      }
    }
  }

  /**
   * Overrideable function for choosing a scenario to add
   * @param innerSolutions
   * @param model
   * @return
   */
  protected Scenario chooseScenario(HashMap<Scenario, Solution> innerSolutions, M model) {
    Scenario worstScenario = null;
    Solution worstSolution = null;
    for (Scenario scenario : innerSolutions.keySet()) {
      Solution solution = innerSolutions.get(scenario);
      if (!solution.isFeasible() || MathUtils.DOUBLE_GREATER_THAN(Math.abs(solution.getObjectiveValue()), 0.0)) {
        if (worstScenario == null) {
          worstScenario = scenario;
          worstSolution = solution;
        }
        
        if (worstSolution.isFeasible() && !solution.isFeasible()) {
          worstScenario = scenario;
          worstSolution = solution;         
        }
        
        if (worstSolution.isFeasible() && solution.isFeasible() && Math.abs(solution.getObjectiveValue()) > Math.abs(worstSolution.getObjectiveValue())) {
          worstScenario = scenario;
          worstSolution = solution;                   
        }        
      }
    }
    return worstScenario;
  }

  
  /**
   * Overrideable function for updating the outer problem based on the inner problem solution
   * @param model
   * @param outerProblem
   * @param innerProblem
   * @param scenario
   * @throws VariableExistsException
   * @throws InvalidVariableException
   * @throws NoVariableException
   * @throws InvalidConstraintException
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  protected void updateOuterProblem(M model, MathematicalProgram outerProblem, MathematicalProgram innerProblem, Scenario scenario) throws VariableExistsException, InvalidVariableException, NoVariableException, InvalidConstraintException  {
    Collection<Scenario> scenarios = new ArrayList<Scenario>();
    scenarios.add(scenario);
    
    // create the variables
    for (VariableFactory variableFactory: getOuterVariableFactories()) {
      if (variableFactory instanceof ScenarioVariableFactory) {
        ((ScenarioVariableFactory) variableFactory).setScenarios(scenarios);
        variableFactory.createVariables(outerProblem, model);
      } 
    }
    
    // create the objective function
    for (ObjectiveFunctionFactory factory : getOuterObjectiveFunctionFactories()) {
      if (factory instanceof ScenarioObjectiveFunctionFactory) {
        ((ScenarioObjectiveFunctionFactory) factory).setScenarios(scenarios);
        factory.addCoefficients(outerProblem, model);      
      }
    }
    
    // create the constraints
    for (ConstraintFactory constraint : getOuterConstraints()) {
      if (constraint instanceof ScenarioConstraintFactory) {
        ((ScenarioConstraintFactory) constraint).setScenarios(scenarios);
        constraint.constructConstraint(outerProblem, model);
      }
    }    
  }
  
}
