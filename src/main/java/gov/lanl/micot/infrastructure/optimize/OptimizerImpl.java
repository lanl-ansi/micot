package gov.lanl.micot.infrastructure.optimize;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import gov.lanl.micot.infrastructure.model.Model;
import gov.lanl.micot.infrastructure.model.Node;
import gov.lanl.micot.infrastructure.optimize.mathprogram.assignment.AssignmentFactory;
import gov.lanl.micot.infrastructure.optimize.mathprogram.constraint.ConstraintFactory;
import gov.lanl.micot.infrastructure.optimize.mathprogram.initialsolution.InitialSolutionFactory;
import gov.lanl.micot.infrastructure.optimize.mathprogram.objective.ObjectiveFunctionFactory;
import gov.lanl.micot.infrastructure.optimize.mathprogram.variable.VariableFactory;

/**
 * Some generic methods that are implemented for the optimizers
 * @author 210117
 *
 * @param <M>
 */
public abstract class OptimizerImpl<N extends Node, M extends Model> implements Optimizer<N, M> {

  private boolean isFeasible = false;
  private Set<OptimizerListener> listeners = null;
  private double lastObjective = 0;
  private double cpuTime = 0;
  
  private OptimizerStatistics profile = null;
  
  private Collection<VariableFactory<N,M>> variableFactories = null;
  private Collection<ConstraintFactory<N,M>> constraints = null;
  private Collection<ObjectiveFunctionFactory<N,M>> objectiveFunctionFactories = null;
  private Collection<AssignmentFactory<N,M>> assignmentFactories = null;
  private Collection<InitialSolutionFactory<M>> initialSolutionFactories = null;
  
  /**
   * Constructor
   */
  public OptimizerImpl() {
    listeners = new HashSet<OptimizerListener>();
    profile = new OptimizerStatistics();
    variableFactories = new ArrayList<VariableFactory<N,M>>();
    constraints = new ArrayList<ConstraintFactory<N,M>>();
    objectiveFunctionFactories = new ArrayList<ObjectiveFunctionFactory<N,M>>();
    assignmentFactories = new ArrayList<AssignmentFactory<N,M>>();
    initialSolutionFactories = new ArrayList<InitialSolutionFactory<M>>();
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

  /**
   * Fire an event concerning a model change
   * @param state
   * @param permutation
   */
  protected void fireModelChanged(Model model) {
    for (OptimizerListener listener : listeners) {
      listener.modelChanged(model);       
    }
  }
  
  /**
   * Fire an event concerning a model change
   * @param state
   * @param permutation
   */
  protected void fireModelImproved(Model model) {
    for (OptimizerListener listener : listeners) {
      listener.modelImproved(model);        
    }
  }
  
  /**
   * Fire an event concerning a model change
   * @param state
   * @param permutation
   */
  protected void fireModelBacktrack(Model model) {
    for (OptimizerListener listener : listeners) {
      listener.modelBacktrack(model);       
    }
  }
  
  /**
   * Fire an event concerning a model change
   * @param state
   * @param permutation
   */
  protected void fireModelRestart() {
    for (OptimizerListener listener : listeners) {
      listener.restartFromBestModel();      
    }
  }
  

  /**
   * Fire an event concerning a model change
   * @param state
   * @param permutation
   */
  protected void fireAlgorithmStarted(Model model) {
    for (OptimizerListener listener : listeners) {
      listener.algorithmStart(model);     
    }
  }
  
  /**
   * Get all the algorithm listeners
   * @return
   */
  protected Collection<OptimizerListener> getAlgorithmListeners() {
    return listeners;
  }

  /**
   * Set is infeasible
   * @param isFeasible
   */
  protected void setIsFeasible(boolean isFeasible) {
    this.isFeasible = isFeasible;
  }
  
  @Override
  public boolean isFeasible() {
    return isFeasible;
  }
  
  /**
   * Set the last objective value
   * @param obj
   */
  protected void setLastObjectiveValue(double obj) {
    this.lastObjective = obj;
  }
  
  @Override
  public double getObjectiveValue() {
    return lastObjective;
  }
  
  /**
   * Get the profile
   * @return
   */
  public OptimizerStatistics getProfile() {
    return profile;
  }
  
  @Override
  public void addVariableFactory(VariableFactory<N,M> factory) {
    variableFactories.add(factory);
  }
  
  /**
   * Add the variable factories
   * @param factory
   */
  public void addInitialSolutionFactory(InitialSolutionFactory<M> factory) {
    initialSolutionFactories.add(factory);
  }
    
  /**
   * Get the variable factories
   * @return
   */
  public Collection<VariableFactory<N,M>> getVariableFactories() {
    return variableFactories;
  }

  /**
   * Get the variable factories
   * @return
   */
  public Collection<InitialSolutionFactory<M>> getInitialSolutionFactories() {
    return initialSolutionFactories;
  }
  
  @Override
  public void addAssignmentFactory(AssignmentFactory<N,M> factory) {
    assignmentFactories.add(factory);
  }
  
  /**
   * Get the variable factories
   * @return
   */
  public Collection<AssignmentFactory<N,M>> getAssignmentFactories() {
    return assignmentFactories;
  }
  
  @Override
  public void addObjectiveFunctionFactory(ObjectiveFunctionFactory<N,M> factory) {
    objectiveFunctionFactories.add(factory);
  }

  /**
   * Get the objective function factories
   * @return
   */
  public Collection<ObjectiveFunctionFactory<N,M>> getObjectiveFunctionFactories() {
    return objectiveFunctionFactories;
  }
  
  /**
   * Get the constraints
   * @return
   */
  public Collection<ConstraintFactory<N,M>> getConstraints() {
    return constraints;
  }

  @Override
  public void addConstraint(ConstraintFactory<N,M> constraint) {
    constraints.add(constraint);
  }

  /**
   * Set the CPU time
   * @param time
   */
  protected void setCPUTime(double time) {
    cpuTime = time;
  }
  
  /**
   * Get the CPU time
   */
  public double getCPUTime() {
    return cpuTime;
  }
  
}

