package gov.lanl.micot.infrastructure.optimize.mathprogram;

import gov.lanl.micot.infrastructure.model.Asset;
import gov.lanl.micot.infrastructure.model.Model;
import gov.lanl.micot.infrastructure.model.Node;
import gov.lanl.micot.infrastructure.optimize.OptimizerImpl;
import gov.lanl.micot.infrastructure.optimize.mathprogram.assignment.AssignmentFactory;
import gov.lanl.micot.infrastructure.optimize.mathprogram.constraint.ConstraintFactory;
import gov.lanl.micot.infrastructure.optimize.mathprogram.initialsolution.InitialSolutionFactory;
import gov.lanl.micot.infrastructure.optimize.mathprogram.objective.ObjectiveFunctionFactory;
import gov.lanl.micot.infrastructure.optimize.mathprogram.variable.VariableFactory;
import gov.lanl.micot.util.math.solver.Solution;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.SolverException;
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
 * This class contains basic information about an optimization routine that uses math programming
 * maximizes load served
 * 
 * @author Russell Bent
 */

public class MathProgramOptimizer<N extends Node, M extends Model> extends OptimizerImpl<N,M> {

  protected double objectiveValue = 0;
  private MathematicalProgramFlags flags = null;
  
  /**
   * Constructor
   * 
   */
  protected MathProgramOptimizer() {
    super();
    setupMathProgramFlags();
  }

  /**
   * Set up the math program flags
   */
  private void setupMathProgramFlags() {
    flags = new MathematicalProgramFlags();
    flags.put(MathematicalProgramFlags.DEBUG_ON_FLAG, false);
    flags.put(MathematicalProgramFlags.MIP_GAP_TOLERANCE_FLAG, 1e-6);   
  }
  
  /**
   * Set the factory name
   * @param factory
   */
  public void setFactoryName(String factory) {
    flags.put(MathematicalProgramFlags.MATHEMATICAL_PROGRAM_FACTORY_FLAG, factory);
  }
  
  /**
   * Add a math program flag
   * @param key
   * @param value
   */
  public void addMathProgramFlag(String key, Object value) {
    flags.put(key, value);
  }
  
  /**
   * Get the factory name
   * @return
   */
  protected String getFactoryName() {
    return flags.getString(MathematicalProgramFlags.MATHEMATICAL_PROGRAM_FACTORY_FLAG);
  }
      
  @Override
  public boolean solve(M model) {
            
    updateModelStatus(model);
          
    boolean state = true;
    try {
      setCPUTime(0);
      objectiveValue = 0;
      solveIsland(model);
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

  protected void solveIsland(M model) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SolverException {
    MathematicalProgram problem = DefaultMathematicalProgramFactory.getInstance().constructMathematicalProgram(flags);

    if (problem instanceof QuadraticProgram) {
      QuadraticObjective qobj = new QuadraticObjectiveMaximize();
      problem.setQuadraticObjective(qobj);
    }
    
    LinearObjective objective = new LinearObjectiveMaximize();
    problem.setLinearObjective(objective);
        
    // create the variables
    for (VariableFactory<N,M> variableFactory: getVariableFactories()) {
      variableFactory.createVariables(problem, model);
    }
    
    // create the objective function
    for (ObjectiveFunctionFactory<N,M> factory : getObjectiveFunctionFactories()) {
      factory.addCoefficients(problem, model);      
    }
    
    // create the constraints
    for (ConstraintFactory<N,M> constraint : getConstraints()) {
      constraint.constructConstraint(problem, model);
    }
    
    // create the initial solution
    for (InitialSolutionFactory<M> factory : getInitialSolutionFactories()) {
      factory.updateInitialSolution(model, problem);
    }
    
	  Solution solution = null;
    try {
      Timer timer = new Timer();
      timer.startTimer();
      solution = problem.solve();
      setCPUTime(getCPUTime() +timer.getCPUMinutesDec());
      objectiveValue += solution.getObjectiveValue();
      getProfile().putAll(problem.getProfile());
    }
    catch (SolverException e) {
      solution = new Solution(Double.NaN, false);
      getProfile().putAll(problem.getProfile());
      for (Variable variable : problem.getVariables()) {
        solution.addValue(variable, 0.0);
      }
      
      for (AssignmentFactory<N,M> assignmentFactory : getAssignmentFactories()) {
        assignmentFactory.performAssignment(model, problem, solution);
      }          
      throw e;
    }
    
    for (AssignmentFactory<N,M> assignmentFactory : getAssignmentFactories()) {
      assignmentFactory.performAssignment(model, problem, solution);
    }    
  }
  
  @Override
  public double getObjectiveValue() {
    return objectiveValue;
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
  protected MathematicalProgramFlags getFlags() {
    return flags;
  }
}
