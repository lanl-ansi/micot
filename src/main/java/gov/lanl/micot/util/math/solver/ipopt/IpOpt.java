package gov.lanl.micot.util.math.solver.ipopt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import com.sun.jna.Pointer;

import gov.lanl.micot.util.collection.Pair;

/**
 * This class manages the interaction with the JNA wrapper of IpOpt
 * @author Russell Bent
 */
public class IpOpt {

  private Pointer quadProgram = null;
  private Collection<Pointer> variables = null;
  private Collection<Pointer> constraints = null;
    
  /**
   * Constructor
   */
  public IpOpt() {
    IpOptEngineJNA engine = IpOptEngineJNA.INSTANCE;
    quadProgram = engine.createIpoptQuadraticProgram();  
    variables = new ArrayList<Pointer>();
    constraints = new ArrayList<Pointer>();
  }

  /**
   * Function for creating a continuous variable in quadProgram
   * @param lb
   * @param ub
   * @param name
   * @return
   */
  public IpOptVariable createContinuousVariable(double lb, double ub, String name) {
    IpOptEngineJNA engine = IpOptEngineJNA.INSTANCE;
    Pointer variable = engine.createIpoptVariable(quadProgram, name, lb, ub, false);
    variables.add(variable);
    return new IpOptVariable(variable);
  }
    
  /**
   * Creates a linear constraint in quadProgram
   * @param name
   * @param rhs
   * @return
   */
  public IpOptConstraint createLinearConstraintLE(String name, double rhs, Map<IpOptVariable, Double> coeffs) {
    IpOptEngineJNA engine = IpOptEngineJNA.INSTANCE;
    Pointer variables[] = new Pointer[coeffs.size()];
    double values[] = new double[coeffs.size()];
    
    int i = 0;
    for (Entry<IpOptVariable, Double> v : coeffs.entrySet()) {
      variables[i] = v.getKey().getPointer();
      values[i] = v.getValue();
      ++i;
    }    
    Pointer constraint = engine.createIpoptConstraint(quadProgram, name, coeffs.size(), variables, values, 0, new Pointer[0], new Pointer[0], new double[0],-Double.MAX_VALUE, rhs);    
    constraints.add(constraint);
    return new IpOptConstraint(constraint);
  }

  /**
   * Create a quadratic LE constraint  
   * @param name
   * @param rhs
   * @param linearCoeffs
   * @param quadCoeffs
   * @return
   */
  public IpOptConstraint createQuadraticConstraintLE(String name, double rhs, Map<IpOptVariable, Double> linearCoeffs, Map<Pair<IpOptVariable,IpOptVariable>, Double> quadCoeffs) {
    IpOptEngineJNA engine = IpOptEngineJNA.INSTANCE;
    Pointer linearVariables[] = new Pointer[linearCoeffs.size()];
    double linearValues[] = new double[linearCoeffs.size()];

    Pointer quadVariables1[] = new Pointer[quadCoeffs.size()];
    Pointer quadVariables2[] = new Pointer[quadCoeffs.size()];
    double quadValues[] = new double[quadCoeffs.size()];

    int i = 0;
    for (Entry<IpOptVariable, Double> v : linearCoeffs.entrySet()) {
      linearVariables[i] = v.getKey().getPointer();
      linearValues[i] = v.getValue();
      ++i;
    }    

    i = 0;
    for (Entry<Pair<IpOptVariable,IpOptVariable>, Double> v : quadCoeffs.entrySet()) {
      quadVariables1[i] = v.getKey().getOne().getPointer();
      quadVariables2[i] = v.getKey().getTwo().getPointer();
      quadValues[i] = v.getValue();
      ++i;
    }    

    Pointer constraint = engine.createIpoptConstraint(quadProgram, name, linearCoeffs.size(), linearVariables, linearValues, quadCoeffs.size(), quadVariables1, quadVariables2, quadValues, -Double.MAX_VALUE, rhs);
    constraints.add(constraint);
    return new IpOptConstraint(constraint);    
  }
  
  /**
   * Creates a linear constraint in quadProgram
   * @param name
   * @param rhs
   * @return
   */
  public IpOptConstraint createLinearConstraintGE(String name, double rhs, Map<IpOptVariable, Double> coeffs) {
    IpOptEngineJNA engine = IpOptEngineJNA.INSTANCE;
    Pointer variables[] = new Pointer[coeffs.size()];
    double values[] = new double[coeffs.size()];
    
    int i = 0;
    for (Entry<IpOptVariable, Double> v : coeffs.entrySet()) {
      variables[i] = v.getKey().getPointer();
      values[i] = v.getValue();
      ++i;
    }    
    Pointer constraint = engine.createIpoptConstraint(quadProgram, name, coeffs.size(), variables, values, 0, new Pointer[0], new Pointer[0], new double[0], rhs, Double.MAX_VALUE);    
    constraints.add(constraint);
    return new IpOptConstraint(constraint);
  }

  /**
   * Create a quadratic >= constraint
   * @param name
   * @param rhs
   * @param linearCoeffs
   * @param quadCoeffs
   * @return
   */
  public IpOptConstraint createQuadraticConstraintGE(String name, double rhs, Map<IpOptVariable, Double> linearCoeffs, Map<Pair<IpOptVariable,IpOptVariable>, Double> quadCoeffs) {
    IpOptEngineJNA engine = IpOptEngineJNA.INSTANCE;
    Pointer linearVariables[] = new Pointer[linearCoeffs.size()];
    double linearValues[] = new double[linearCoeffs.size()];

    Pointer quadVariables1[] = new Pointer[quadCoeffs.size()];
    Pointer quadVariables2[] = new Pointer[quadCoeffs.size()];
    double quadValues[] = new double[quadCoeffs.size()];

    int i = 0;
    for (Entry<IpOptVariable, Double> v : linearCoeffs.entrySet()) {
      linearVariables[i] = v.getKey().getPointer();
      linearValues[i] = v.getValue();
      ++i;
    }    

    i = 0;
    for (Entry<Pair<IpOptVariable,IpOptVariable>, Double> v : quadCoeffs.entrySet()) {
      quadVariables1[i] = v.getKey().getOne().getPointer();
      quadVariables2[i] = v.getKey().getTwo().getPointer();
      quadValues[i] = v.getValue();
      ++i;
    }    

    Pointer constraint = engine.createIpoptConstraint(quadProgram, name, linearCoeffs.size(), linearVariables, linearValues, quadCoeffs.size(), quadVariables1, quadVariables2, quadValues, rhs, Double.MAX_VALUE);
    constraints.add(constraint);
    return new IpOptConstraint(constraint);    
  }

  
  /**
   * Creates a linear constraint in quadProgram
   * @param name
   * @param rhs
   * @return
   */
  public IpOptConstraint createLinearConstraintEq(String name, double rhs, Map<IpOptVariable, Double> coeffs) {
    IpOptEngineJNA engine = IpOptEngineJNA.INSTANCE;
    Pointer variables[] = new Pointer[coeffs.size()];
    double values[] = new double[coeffs.size()];
    
    int i = 0;
    for (Entry<IpOptVariable, Double> v : coeffs.entrySet()) {
      variables[i] = v.getKey().getPointer();
      values[i] = v.getValue();
      ++i;
    }    
    Pointer constraint = engine.createIpoptConstraint(quadProgram, name, coeffs.size(), variables, values, 0, new Pointer[0], new Pointer[0], new double[0], rhs, rhs);    
    constraints.add(constraint);
    return new IpOptConstraint(constraint);
  }
  
  /**
   * create a quadrartic constraint in quadProgram
   * @param name
   * @param rightHandSide
   * @param coeffs
   */
  public IpOptConstraint createQuadraticConstraintEq(String name, double rhs, Map<IpOptVariable, Double> linearCoeffs, Map<Pair<IpOptVariable,IpOptVariable>, Double> quadCoeffs) {
    IpOptEngineJNA engine = IpOptEngineJNA.INSTANCE;
    Pointer linearVariables[] = new Pointer[linearCoeffs.size()];
    double linearValues[] = new double[linearCoeffs.size()];

    Pointer quadVariables1[] = new Pointer[quadCoeffs.size()];
    Pointer quadVariables2[] = new Pointer[quadCoeffs.size()];
    double quadValues[] = new double[quadCoeffs.size()];

    int i = 0;
    for (Entry<IpOptVariable, Double> v : linearCoeffs.entrySet()) {
      linearVariables[i] = v.getKey().getPointer();
      linearValues[i] = v.getValue();
      ++i;
    }    

    i = 0;
    for (Entry<Pair<IpOptVariable,IpOptVariable>, Double> v : quadCoeffs.entrySet()) {
      quadVariables1[i] = v.getKey().getOne().getPointer();
      quadVariables2[i] = v.getKey().getTwo().getPointer();
      quadValues[i] = v.getValue();
      ++i;
    }    

    Pointer constraint = engine.createIpoptConstraint(quadProgram, name, linearCoeffs.size(), linearVariables, linearValues, quadCoeffs.size(), quadVariables1, quadVariables2, quadValues, rhs, rhs);
    constraints.add(constraint);
    return new IpOptConstraint(constraint);    
  }
  
  /**
   * Set the initial solution
   * @param solution
   */
  public void setInitialSolution(Map<IpOptVariable, Double> solution) {
    IpOptEngineJNA engine = IpOptEngineJNA.INSTANCE;
    Pointer variables[] = new Pointer[solution.size()];
    double values[] = new double[solution.size()];
    
    int i = 0;
    for (Entry<IpOptVariable,Double> entry : solution.entrySet()) {
      variables[i] = entry.getKey().getPointer();
      values[i] = entry.getValue();
      ++i;
    }
    
    engine.setIpoptInitialSolution(quadProgram, solution.size(), variables, values);
  }
  
  /**
   * Creates a linear minimization objective
   * @param coeffs
   */
  public void createLinearMinimizeObj(Map<IpOptVariable, Double> coeffs) {
    IpOptEngineJNA engine = IpOptEngineJNA.INSTANCE;
    Pointer linearVariables[] = new Pointer[coeffs.size()];
    double linearValues[] = new double[coeffs.size()];
    int i = 0;
    for (Entry<IpOptVariable, Double> v : coeffs.entrySet()) {
      linearVariables[i] = v.getKey().getPointer();
      linearValues[i] = v.getValue();
      ++i;
    }  
    
    engine.createIpoptObjective(quadProgram, coeffs.size(), linearVariables, linearValues, 0, new Pointer[0], new Pointer[0], new double[0], true);    
  }
 
  /**
   * Creates a linear minimization objective
   * @param coeffs
   */
  public void createLinearMaximizeObj(Map<IpOptVariable, Double> coeffs) {
    IpOptEngineJNA engine = IpOptEngineJNA.INSTANCE;
    Pointer linearVariables[] = new Pointer[coeffs.size()];
    double linearValues[] = new double[coeffs.size()];
    int i = 0;
    for (Entry<IpOptVariable, Double> v : coeffs.entrySet()) {
      linearVariables[i] = v.getKey().getPointer();
      linearValues[i] = v.getValue();
      ++i;
    }  
    
    engine.createIpoptObjective(quadProgram, coeffs.size(), linearVariables, linearValues, 0, new Pointer[0], new Pointer[0], new double[0], false);
  }
 

  /**
   * Creates a quadratic maximization function.  
   * with an aux variable  
   * @param linearCoeffs
   * @param quadCoeffs
   */
  public void createQuadraticMaximizeObj(Map<IpOptVariable, Double> linearCoeffs, Map<Pair<IpOptVariable, IpOptVariable>, Double> quadCoeffs) {
    IpOptEngineJNA engine = IpOptEngineJNA.INSTANCE;
    
    Pointer linearVariables[] = new Pointer[linearCoeffs.size()];
    double linearValues[] = new double[linearCoeffs.size()];
    int i = 0;
    for (Entry<IpOptVariable, Double> v : linearCoeffs.entrySet()) {
      linearVariables[i] = v.getKey().getPointer();
      linearValues[i] = v.getValue();
      ++i;
    }  
    
    
    Pointer quadVariables1[] = new Pointer[quadCoeffs.size()];
    Pointer quadVariables2[] = new Pointer[quadCoeffs.size()];
    double quadValues[] = new double[quadCoeffs.size()];
    i = 0;
    for (Entry<Pair<IpOptVariable,IpOptVariable>, Double> v : quadCoeffs.entrySet()) {
      quadVariables1[i] = v.getKey().getOne().getPointer();
      quadVariables2[i] = v.getKey().getTwo().getPointer();
      quadValues[i] = v.getValue();
      ++i;
    }  
    
    engine.createIpoptObjective(quadProgram, linearCoeffs.size(), linearVariables, linearValues, quadCoeffs.size(), quadVariables1, quadVariables2, quadValues, false);
   }

  /**
   * Creates a quadratic minimize function.  
   * with an aux variable  
   * @param linearCoeffs
   * @param quadCoeffs
   */
  public void createQuadraticMinimizeObj(Map<IpOptVariable, Double> linearCoeffs, Map<Pair<IpOptVariable, IpOptVariable>, Double> quadCoeffs) {
    IpOptEngineJNA engine = IpOptEngineJNA.INSTANCE;
    Pointer linearVariables[] = new Pointer[linearCoeffs.size()];
    double linearValues[] = new double[linearCoeffs.size()];
    int i = 0;
    for (Entry<IpOptVariable, Double> v : linearCoeffs.entrySet()) {
      linearVariables[i] = v.getKey().getPointer();
      linearValues[i] = v.getValue();
      ++i;
    }  
    
    
    Pointer quadVariables1[] = new Pointer[quadCoeffs.size()];
    Pointer quadVariables2[] = new Pointer[quadCoeffs.size()];
    double quadValues[] = new double[quadCoeffs.size()];
    i = 0;
    for (Entry<Pair<IpOptVariable,IpOptVariable>, Double> v : quadCoeffs.entrySet()) {
      quadVariables1[i] = v.getKey().getOne().getPointer();
      quadVariables2[i] = v.getKey().getTwo().getPointer();
      quadValues[i] = v.getValue();
      ++i;
    }  
    
    engine.createIpoptObjective(quadProgram, linearCoeffs.size(), linearVariables, linearValues, quadCoeffs.size(), quadVariables1, quadVariables2, quadValues, true);
  }

  /**
   * Cleans up the memory in the quadProgram objects
   */
  public void clearModel() {
    IpOptEngineJNA engine = IpOptEngineJNA.INSTANCE;
    engine.destroyIpoptQuadraticProgram(quadProgram);
  }

  /**
   * Invoke the quadProgram  solver
   */
  public void solve() {
    IpOptEngineJNA engine = IpOptEngineJNA.INSTANCE;
    engine.solveIpopt(quadProgram);
  }

  /**
   * Get the objective value
   * @return
   */
  public double getObjValue() {
    IpOptEngineJNA engine = IpOptEngineJNA.INSTANCE;    
    double value = engine.getIpoptObjectiveValue(quadProgram);
    return value;
  }

  /**
   * Get the value of variable in the solution
   * @param IpOptVariable
   * @return
   */
  public double getValue(IpOptVariable IpOptVariable) {
    IpOptEngineJNA engine = IpOptEngineJNA.INSTANCE;
    return engine.getIpoptSolutionValue(quadProgram,IpOptVariable.getPointer());
  }

  /**
   * Gets the status of the of the optimization
   * @return
   */
  public IpOptStatus getStatus() {
    IpOptEngineJNA engine = IpOptEngineJNA.INSTANCE;
    int status = engine.getIpoptStatus(quadProgram); 
    
    if (status == engine.getIpoptSolveSucceededStatus()) {
      return IpOptStatus.IPOPT_STATUS_SOLVE_SUCCEEDED;
    }
    if (status == engine.getIpoptSolvedToAcceptableLevelStatus()) {
      return IpOptStatus.IPOPT_STATUS_SOLVED_TO_ACCEPTABLE_LEVEL;
    }
    if (status == engine.getIpoptInfeasibleProblemDetectedStatus()) {
      return IpOptStatus.IPOPT_STATUS_INFEASIBLE_PROBLEM_DETECTED;
    }
    if (status == engine.getIpoptSearchDirectionBecomesTooSmallStatus()) {
      return IpOptStatus.IPOPT_STATUS_SEARCH_DIRECTION_BECOMES_TOO_SMALL;
    }
    if (status == engine.getIpoptDivergingIteratesStatus()) {
      return IpOptStatus.IPOPT_STATUS_DIVERGING_ITERATES;
    }
    if (status == engine.getIpoptUserRequestedStopStatus()) {
      return IpOptStatus.IPOPT_STATUS_USER_REQUESTED_STOP;
    }
    if (status == engine.getIpoptFeasiblePointFoundStatus()) {
      return IpOptStatus.IPOPT_STATUS_FEASIBLE_POINT_FOUND;
    }
    if (status == engine.getIpoptMaximumIterationsExceededStatus()) {
      return IpOptStatus.IPOPT_STATUS_MAXIMUM_ITERATIONS_EXCEEDED;
    }
    if (status == engine.getIpoptRestorationFailedStatus()) {
      return IpOptStatus.IPOPT_STATUS_RESTORATION_FAILED;
    }
    if (status == engine.getIpoptErrorInStepComputationStatus()) {
      return IpOptStatus.IPOPT_STATUS_ERROR_IN_STEP_COMPUTATION;
    }
    if (status == engine.getIpoptMaximumCPUTimeExceeedStatus()) {
      return IpOptStatus.IPOPT_STATUS_MAXIMUM_CPUTIME_EXCEEDED;
    }
    if (status == engine.getIpoptNotEnoughDegreesOfFreedomStatus()) {
      return IpOptStatus.IPOPT_STATUS_NOT_ENOUGH_DEGREES_OF_FREEDOM;
    }
    if (status == engine.getIpoptInvalidProblemDefinitionStatus()) {
      return IpOptStatus.IPOPT_STATUS_INVALID_PROBLEM_DEFINITION;
    }
    if (status == engine.getIpoptInvalidOptionStatus()) {
      return IpOptStatus.IPOPT_STATUS_INVALID_OPTION;
    }
    if (status == engine.getIpoptInvalidNumberDetectedStatus()) {
      return IpOptStatus.IPOPT_STATUS_INVALID_NUMBER_DETECTED;
    }
    if (status == engine.getIpoptUnrecoverableExceptionStatus()) {
      return IpOptStatus.IPOPT_STATUS_UNRECOVERABLE_EXCEPTION;
    }
    if (status == engine.getIpoptNonIpoptExceptionThrownStatus()) {
      return IpOptStatus.IPOPT_STATUS_NONIOPT_EXCEPTION_THROWN;
    }
    if (status == engine.getIpoptInsufficientMemoryStatus()) {
      return IpOptStatus.IPOPT_STATUS_INSUFFICIENT_MEMORY;
    }
    if (status == engine.getIpoptInternalErrorStatus()) {
      return IpOptStatus.IPOPT_STATUS_INTERNAL_ERROR;
    }
    return IpOptStatus.IPOPT_STATUS_NONIOPT_EXCEPTION_THROWN;
  }

}
