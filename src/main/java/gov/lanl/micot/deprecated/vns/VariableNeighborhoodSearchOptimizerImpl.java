package gov.lanl.micot.deprecated.vns;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

import gov.lanl.micot.infrastructure.model.Asset;
import gov.lanl.micot.infrastructure.model.Model;
import gov.lanl.micot.infrastructure.model.Node;
import gov.lanl.micot.infrastructure.optimize.OptimizerListener;
import gov.lanl.micot.infrastructure.optimize.OptimizerStatistics;
import gov.lanl.micot.infrastructure.optimize.mathprogram.assignment.AssignmentFactory;
import gov.lanl.micot.infrastructure.optimize.mathprogram.constraint.ConstraintFactory;
import gov.lanl.micot.infrastructure.optimize.mathprogram.initialsolution.InitialSolutionFactory;
import gov.lanl.micot.infrastructure.optimize.mathprogram.objective.ObjectiveFunctionFactory;
import gov.lanl.micot.infrastructure.optimize.mathprogram.variable.VariableFactory;
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
 * variable neighborhood search
 * 
 * @author Russell Bent
 */
public abstract class VariableNeighborhoodSearchOptimizerImpl<N extends Node, M extends Model> implements VariableNeighborhoodSearchOptimizer<N,M> {

  private MathematicalProgram relaxedProblem = null;
  private MathematicalProgram fullProblem = null;
    
  private MathematicalProgramFlags relaxedProblemFlags = null;
  private MathematicalProgramFlags fullProblemFlags = null;
    
  private Map<VariableFactory<N,M>, VariableFactory<N,M>> variableFactories = null;
  private Map<VariableFactory<N,M>, Boolean> isDiscrete = null;
  private Collection<ConstraintFactory<N,M>> constraints = null;
  private Collection<ObjectiveFunctionFactory<N,M>> objectiveFunctionFactories = null;
  private Collection<AssignmentFactory<N,M>> assignmentFactories = null;
  private Collection<InitialSolutionFactory<M>> initialSolutionFactories = null;

  private double objectiveValue = 0;
  private double cpuTime = 0; 
  private boolean isFeasible = true;
  private OptimizerStatistics profile = null;
  
  private Set<OptimizerListener> listeners = null;
  
  private double maxCPUMinutes = -1;
  private int maxRestarts = -1;
  private int maxIterations = -1;
  private double restartStepScalar = 2.0;
  private double nonRestartStepScalar = 0.5;
  private double relaxDiffTolerance = 1e-2;
  
  private Random random = null;
  
  private Collection<Variable> discreteVariables = null;
    
  /**
   * Constructor
   * 
   */
  protected VariableNeighborhoodSearchOptimizerImpl() {
    super();
    listeners = new HashSet<OptimizerListener>();
    relaxedProblemFlags = new MathematicalProgramFlags();
    fullProblemFlags = new MathematicalProgramFlags();
    
    variableFactories = new HashMap<VariableFactory<N,M>, VariableFactory<N,M>>();
    isDiscrete = new HashMap<VariableFactory<N,M>, Boolean>();
    constraints = new ArrayList<ConstraintFactory<N,M>>();
    objectiveFunctionFactories = new ArrayList<ObjectiveFunctionFactory<N,M>>();
    assignmentFactories = new ArrayList<AssignmentFactory<N,M>>();
    initialSolutionFactories = new ArrayList<InitialSolutionFactory<M>>();    
  }
  
  /**
   * Set the tolerance for determining if a relaxed assignment is the same as the full problem assignment
   * @param lpDiffTolerance
   */
  public void setLPDiffTolerance(double relaxDiffTolerance) {
    this.relaxDiffTolerance = relaxDiffTolerance;
  }
  
  /**
   * Set the maximum CPU minutes
   * @param maxTime
   */
  public void setMaxCPUMinutes(double maxTime) {
    maxCPUMinutes = maxTime;
  }
  
  /**
   * Set the maximum restarts
   * @param maxRestarts
   */
  public void setMaxRestarts(int maxRestarts) {
    this.maxRestarts = maxRestarts;
  }

  /**
   * Set the maximum number of iterations
   * @param maxIterations
   */
  public void setMaxIterations(int maxIterations) {
    this.maxIterations = maxIterations;
  }

  /**
   * Set the restart scalar
   * @param restartStepScalar
   */
  public void setRestartStepScalar(double restartStepScalar) {
    this.restartStepScalar = restartStepScalar;
  }
  
  /**
   * Set the non restart scalar
   * @param nonRestartStepScalar
   */
  public void setNonRestartStepScalar(double nonRestartStepScalar) {
    this.nonRestartStepScalar = nonRestartStepScalar;
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
   * Solution for solving the problem
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
  protected void run(M model) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SolverException {
    random = new Random(getSeed());
    
    // get the LP relaxation to help seed the solution process
    createRelaxedProgram(model); // line 1 from AAAI paper
    createFullProgram(model);
    Solution relaxedSolution = relaxedProblem.solve();
    
    // initialize everything
    Solution bestSolution = createInitialSolution(model);
    boolean restart = false;    
    Timer timer = new Timer();
    timer.startTimer();   
    profile = new OptimizerStatistics();
    int numRestarts = 0;

    while (timer.getCPUMinutesDec() <= maxCPUMinutes && numRestarts <= maxRestarts) { // line 2 from AAAI paper
      int numIterations = 0;  // line 3 from AAAI paper
      int numRelaxationDifferences = calculateNumRelaxationDifferences(bestSolution, relaxedSolution); // line 4 from AAAI paper
      ArrayList<Variable> orderedVariables = orderVariables(bestSolution, relaxedSolution); // line 5 from AAAI paper
      double step = 0;
      if (restart) { // line 6 from AAAI paper
        ++numRestarts; // line 7 from AAAI paper
        step = restartStepScalar * numRelaxationDifferences; // line 8 from AAAI paper
        shuffle(orderedVariables); // line 9 from AAAI paper
      }
      else { // line 10 from AAAI paper
        step = nonRestartStepScalar * numRelaxationDifferences; // line 11 from AAAI paper
      }      
      int numFixedVariables = (int)(discreteVariables.size() - step);
      
      while (timer.getCPUMinutesDec() <= maxCPUMinutes && numIterations <= maxIterations) { // line 12 from AAAI paper
        Solution nextSolution = solveNeighborhood(bestSolution, orderedVariables, numFixedVariables); // line 13 from AAAI paper
        if (nextSolution.isFeasible() && nextSolution.getObjectiveValue() > bestSolution.getObjectiveValue()) { // line 14 from AAAI paper
          bestSolution = nextSolution; // line 15 from AAAI paper
          numRestarts = 0; // line 16 from AAAI paper
          restart = false; // line 17 from AAAI paper
          numIterations = maxIterations + 1; // line 18 from AAAI paper
        }
        else { // line 19 from AAAI paper
          ++numIterations; // line 20 from AAAI paper
          numFixedVariables = (int)((double)numFixedVariables - (step/2.0)); // line 21 from AAAI paper
          if (numIterations > maxIterations) { // line 22 from AAAI paper
            restart = true; // line 23 from AAAI paper
          }
        }
      }      
    }

    // pull the solution out of the best solution
    setCPUTime(timer.getCPUMinutesDec());    
    for (AssignmentFactory<N,M> assignmentFactory : getAssignmentFactories()) {
      assignmentFactory.performAssignment(model, fullProblem, bestSolution); // line 24 from AAAI paper
    }          
  }

  /**
   * Solves the full problem by fixing certain values to their incumbent values
   * @param bestSolution
   * @param orderedVariables
   * @param neighborhoodSize
   * @return
   */
  private Solution solveNeighborhood(Solution bestSolution, ArrayList<Variable> orderedVariables, int numFixedVariables) {
    // fix the bounds on some variables
    Map<Variable, Number> oldUB = new HashMap<Variable,Number>();
    Map<Variable, Number> oldLB = new HashMap<Variable,Number>();    
    for (int i = 0; i < numFixedVariables; ++i) {
      Variable variable = orderedVariables.get(i);
      oldUB.put(variable, fullProblem.getUpperBound(variable));
      oldLB.put(variable, fullProblem.getLowerBound(variable));      
      fullProblem.addBounds(variable, bestSolution.getValueInt(variable), bestSolution.getValueInt(variable));
    }
    
    Solution solution = new Solution(Double.NEGATIVE_INFINITY, false);
    try {
      solution = fullProblem.solve();
    }
    catch (SolverException e) {
    }
    
    // undo the setting of variables
    for (int i = 0; i < numFixedVariables; ++i) {
      Variable variable = orderedVariables.get(i);
      oldUB.put(variable, fullProblem.getUpperBound(variable));
      oldLB.put(variable, fullProblem.getLowerBound(variable));      
      fullProblem.addBounds(variable, oldLB.get(variable).doubleValue(), oldUB.get(variable).doubleValue());
    }
    
    return solution;
  }

  /**
   * Randomly shuffle
   * @param orderedVariables
   */
  private void shuffle(ArrayList<Variable> orderedVariables) {
    Collections.shuffle(orderedVariables, random);    
  }

  /**
   * Order the variables by the delta between the two solutions
   * @param bestSolution
   * @param relaxedSolution
   * @return
   */
  protected ArrayList<Variable> orderVariables(Solution bestSolution, Solution relaxedSolution) {
    TreeMap<Double,Collection<Variable>> sorted = new TreeMap<Double,Collection<Variable>>();
    
    for (Variable fullVariable : discreteVariables) {
      double fullValue = bestSolution.getValueDouble(fullVariable);
      double relaxedValue = relaxedSolution.getValueDouble(relaxedProblem.getVariable(fullVariable.getName()));
      
      double diff = Math.abs(fullValue-relaxedValue);
      if (!sorted.containsKey(diff)) {
        sorted.put(diff, new ArrayList<Variable>());
      }
      sorted.get(diff).add(fullVariable);
    }

    ArrayList<Variable> list = new ArrayList<Variable>();
    for (Entry<Double, Collection<Variable>> entry : sorted.entrySet()) {
      list.addAll(entry.getValue());
    }
        
    return list;
  }

  /**
   * Get the relaxed variable factories
   * @return
   */
  protected Collection<VariableFactory<N,M>> getRelaxedVariableFactories() {
    return variableFactories.values();
  }

  /**
   * Get the relaxed variable factories
   * @return
   */
  protected Collection<VariableFactory<N,M>> getFullVariableFactories() {
    return variableFactories.keySet();
  }
  
  /**
   * Get the objective function factories
   * @return
   */
  protected Collection<ObjectiveFunctionFactory<N,M>> getObjectiveFunctionFactories() {
    return objectiveFunctionFactories;
  }
  
  /**
   * Get all the constraints in the collection
   * @return
   */
  protected Collection<ConstraintFactory<N,M>> getConstraints() {
    return constraints;
  }

  /**
   * Get all the constraints in the collection
   * @return
   */
  protected Collection<InitialSolutionFactory<M>> getInitialSolutionFactories() {
    return initialSolutionFactories;
  }

  /**
   * Get all the assignment factories
   * @return
   */
  protected Collection<AssignmentFactory<N,M>> getAssignmentFactories() {
    return assignmentFactories;
  }
  
  /**
   * Create the relaxed mathematical program
   * @param model
   * @throws InvalidObjectiveException
   * @throws InvalidVariableException 
   * @throws VariableExistsException 
   * @throws NoVariableException 
   * @throws InvalidConstraintException 
   * @throws NoConstraintException 
   */
  protected void createRelaxedProgram(M model) throws InvalidObjectiveException, VariableExistsException, InvalidVariableException, NoVariableException, InvalidConstraintException, NoConstraintException {
    relaxedProblem = DefaultMathematicalProgramFactory.getInstance().constructMathematicalProgram(relaxedProblemFlags);

    if (relaxedProblem instanceof QuadraticProgram) {
      QuadraticObjective qobj = new QuadraticObjectiveMaximize();
      relaxedProblem.setQuadraticObjective(qobj);
    }
    
    LinearObjective objective = new LinearObjectiveMaximize();
    relaxedProblem.setLinearObjective(objective);
        
    // create the variables
    for (VariableFactory<N,M> variableFactory: getRelaxedVariableFactories()) {
      variableFactory.createVariables(relaxedProblem, model);
    }
    
    // create the objective function
    for (ObjectiveFunctionFactory<N,M> factory : getObjectiveFunctionFactories()) {
      factory.addCoefficients(relaxedProblem, model);      
    }
    
    // create the constraints
    for (ConstraintFactory<N,M> constraint : getConstraints()) {
      constraint.constructConstraint(relaxedProblem, model);
    }
    
    // create the initial solution
    for (InitialSolutionFactory<M> factory : getInitialSolutionFactories()) {
      factory.updateInitialSolution(model, relaxedProblem);
    }    
  }
  
  /**
   * Create the relaxed mathematical program
   * @param model
   * @throws InvalidObjectiveException
   * @throws InvalidVariableException 
   * @throws VariableExistsException 
   * @throws NoVariableException 
   * @throws InvalidConstraintException 
   * @throws NoConstraintException 
   */
  protected void createFullProgram(M model) throws InvalidObjectiveException, VariableExistsException, InvalidVariableException, NoVariableException, InvalidConstraintException, NoConstraintException {
    fullProblem = DefaultMathematicalProgramFactory.getInstance().constructMathematicalProgram(fullProblemFlags);
    discreteVariables = new ArrayList<Variable>();

    if (fullProblem instanceof QuadraticProgram) {
      QuadraticObjective qobj = new QuadraticObjectiveMaximize();
      fullProblem.setQuadraticObjective(qobj);
    }
    
    LinearObjective objective = new LinearObjectiveMaximize();
    fullProblem.setLinearObjective(objective);
        
    // create the variables
    for (VariableFactory<N,M> variableFactory: getFullVariableFactories()) {
      Collection<Variable> variables = variableFactory.createVariables(fullProblem, model);
      if (isDiscrete.get(variableFactory)) {
        discreteVariables.addAll(variables);
      }
    }
    
    // create the objective function
    for (ObjectiveFunctionFactory<N,M> factory : getObjectiveFunctionFactories()) {
      factory.addCoefficients(fullProblem, model);      
    }
    
    // create the constraints
    for (ConstraintFactory<N,M> constraint : getConstraints()) {
      constraint.constructConstraint(fullProblem, model);
    }
    
    // create the initial solution
    for (InitialSolutionFactory<M> factory : getInitialSolutionFactories()) {
      factory.updateInitialSolution(model, fullProblem);
    }    
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
    
  @Override
  public void addVariableFactory(VariableFactory<N,M> factory, VariableFactory<N,M> lpfactory, boolean isDiscrete) {
    variableFactories.put(factory, lpfactory);
    this.isDiscrete.put(factory, isDiscrete);
  }
  
  
  @Override
  public void addInitialSolutionFactory(InitialSolutionFactory<M> factory) {
    initialSolutionFactories.add(factory);
  }
    
  @Override
  public void addAssignmentFactory(AssignmentFactory<N,M> factory) {
    assignmentFactories.add(factory);
  }
    
  @Override
  public void addObjectiveFunctionFactory(ObjectiveFunctionFactory<N,M> factory) {
    objectiveFunctionFactories.add(factory);
  }
  
  @Override
  public void addConstraint(ConstraintFactory<N,M> constraint) {
    constraints.add(constraint);
  }
    
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
  
  /**
   * Add an relaxed program flag
   * @param key
   * @param object
   */
  public void addRelaxedMathProgramFlag(String key, Object object) {
    relaxedProblemFlags.put(key, object);
  }
  
  /**
   * Add an full math program flag
   * @param key
   * @param object
   */
  public void addMathProgramFlag(String key, Object object) {
    fullProblemFlags.put(key, object);
  }

  /**
   * Just create an initial solution with all 1's
   * @return
   */
  protected Solution createInitialSolution(M model) {
    Solution solution = new Solution(-Double.MAX_VALUE, false);
    for (Variable variable : fullProblem.getVariables()) {
      solution.addValue(variable, 1.0);
    }    
    return solution;
  }

  /**
   * Determine how far apart the relaxed solution is from the incumbent best solution
   * @param bestSolution
   * @param relaxedSolution
   * @return
   */
  protected int calculateNumRelaxationDifferences(Solution bestSolution, Solution relaxedSolution) {
    int diff = 0;
    for (Variable fullVariable : discreteVariables) {
      double fullValue = bestSolution.getValueDouble(fullVariable);
      double relaxedValue = relaxedSolution.getValueDouble(relaxedProblem.getVariable(fullVariable.getName()));
      
      if (Math.abs(fullValue-relaxedValue) > relaxDiffTolerance) {
        ++diff;
      }
    }
    return diff;  
  }

  /**
   * Get a seed from the randomization process
   * @return
   */
  protected long getSeed() {
    return System.currentTimeMillis();
  }
}
