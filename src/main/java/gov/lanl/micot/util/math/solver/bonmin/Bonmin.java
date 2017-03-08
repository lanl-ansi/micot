package gov.lanl.micot.util.math.solver.bonmin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import com.sun.jna.Pointer;

import gov.lanl.micot.util.collection.Pair;

/**
 * This class manages the interaction with the JNA wrapper of Bonmin
 * @author Russell Bent
 */
public class Bonmin {

  private Pointer quadProgram = null;
  private Collection<Pointer> variables = null;
  private Collection<Pointer> constraints = null;
    
  /**
   * Constructor
   */
  public Bonmin() {
    BonminEngineJNA engine = BonminEngineJNA.INSTANCE;
    quadProgram = engine.createBonminQuadraticProgram();  
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
  public BonminVariable createContinuousVariable(double lb, double ub, String name) {
    BonminEngineJNA engine = BonminEngineJNA.INSTANCE;
    Pointer variable = engine.createBonminVariable(quadProgram, name, lb, ub, 0);
    variables.add(variable);
    return new BonminVariable(variable);
  }

  /**
   * Function for creating a continuous variable in quadProgram
   * @param lb
   * @param ub
   * @param name
   * @return
   */
  public BonminVariable createDiscreteVariable(double lb, double ub, String name) {
    BonminEngineJNA engine = BonminEngineJNA.INSTANCE;
    Pointer variable = engine.createBonminVariable(quadProgram, name, lb, ub, 1);
    variables.add(variable);
    return new BonminVariable(variable);
  }
  
  /**
   * Creates a linear constraint in quadProgram
   * @param name
   * @param rhs
   * @return
   */
  public BonminConstraint createLinearConstraintLE(String name, double rhs, Map<BonminVariable, Double> coeffs) {
    BonminEngineJNA engine = BonminEngineJNA.INSTANCE;
    Pointer variables[] = new Pointer[coeffs.size()];
    double values[] = new double[coeffs.size()];
    
    int i = 0;
    for (Entry<BonminVariable, Double> v : coeffs.entrySet()) {
      variables[i] = v.getKey().getPointer();
      values[i] = v.getValue();
      ++i;
    }    
    Pointer constraint = engine.createBonminConstraint(quadProgram, name, coeffs.size(), variables, values, 0, new Pointer[0], new Pointer[0], new double[0],-Double.MAX_VALUE, rhs);    
    constraints.add(constraint);
    return new BonminConstraint(constraint);
  }

  /**
   * Create a quadratic LE constraint  
   * @param name
   * @param rhs
   * @param linearCoeffs
   * @param quadCoeffs
   * @return
   */
  public BonminConstraint createQuadraticConstraintLE(String name, double rhs, Map<BonminVariable, Double> linearCoeffs, Map<Pair<BonminVariable,BonminVariable>, Double> quadCoeffs) {
    BonminEngineJNA engine = BonminEngineJNA.INSTANCE;
    Pointer linearVariables[] = new Pointer[linearCoeffs.size()];
    double linearValues[] = new double[linearCoeffs.size()];

    Pointer quadVariables1[] = new Pointer[quadCoeffs.size()];
    Pointer quadVariables2[] = new Pointer[quadCoeffs.size()];
    double quadValues[] = new double[quadCoeffs.size()];

    int i = 0;
    for (Entry<BonminVariable, Double> v : linearCoeffs.entrySet()) {
      linearVariables[i] = v.getKey().getPointer();
      linearValues[i] = v.getValue();
      ++i;
    }    

    i = 0;
    for (Entry<Pair<BonminVariable,BonminVariable>, Double> v : quadCoeffs.entrySet()) {
      quadVariables1[i] = v.getKey().getOne().getPointer();
      quadVariables2[i] = v.getKey().getTwo().getPointer();
      quadValues[i] = v.getValue();
      ++i;
    }    

    Pointer constraint = engine.createBonminConstraint(quadProgram, name, linearCoeffs.size(), linearVariables, linearValues, quadCoeffs.size(), quadVariables1, quadVariables2, quadValues, -Double.MAX_VALUE, rhs);
    constraints.add(constraint);
    return new BonminConstraint(constraint);    
  }
  
  /**
   * Creates a linear constraint in quadProgram
   * @param name
   * @param rhs
   * @return
   */
  public BonminConstraint createLinearConstraintGE(String name, double rhs, Map<BonminVariable, Double> coeffs) {
    BonminEngineJNA engine = BonminEngineJNA.INSTANCE;
    Pointer variables[] = new Pointer[coeffs.size()];
    double values[] = new double[coeffs.size()];
    
    int i = 0;
    for (Entry<BonminVariable, Double> v : coeffs.entrySet()) {
      variables[i] = v.getKey().getPointer();
      values[i] = v.getValue();
      ++i;
    }    
    Pointer constraint = engine.createBonminConstraint(quadProgram, name, coeffs.size(), variables, values, 0, new Pointer[0], new Pointer[0], new double[0], rhs, Double.MAX_VALUE);    
    constraints.add(constraint);
    return new BonminConstraint(constraint);
  }

  /**
   * Create a quadratic >= constraint
   * @param name
   * @param rhs
   * @param linearCoeffs
   * @param quadCoeffs
   * @return
   */
  public BonminConstraint createQuadraticConstraintGE(String name, double rhs, Map<BonminVariable, Double> linearCoeffs, Map<Pair<BonminVariable,BonminVariable>, Double> quadCoeffs) {
    BonminEngineJNA engine = BonminEngineJNA.INSTANCE;
    Pointer linearVariables[] = new Pointer[linearCoeffs.size()];
    double linearValues[] = new double[linearCoeffs.size()];

    Pointer quadVariables1[] = new Pointer[quadCoeffs.size()];
    Pointer quadVariables2[] = new Pointer[quadCoeffs.size()];
    double quadValues[] = new double[quadCoeffs.size()];

    int i = 0;
    for (Entry<BonminVariable, Double> v : linearCoeffs.entrySet()) {
      linearVariables[i] = v.getKey().getPointer();
      linearValues[i] = v.getValue();
      ++i;
    }    

    i = 0;
    for (Entry<Pair<BonminVariable,BonminVariable>, Double> v : quadCoeffs.entrySet()) {
      quadVariables1[i] = v.getKey().getOne().getPointer();
      quadVariables2[i] = v.getKey().getTwo().getPointer();
      quadValues[i] = v.getValue();
      ++i;
    }    

    Pointer constraint = engine.createBonminConstraint(quadProgram, name, linearCoeffs.size(), linearVariables, linearValues, quadCoeffs.size(), quadVariables1, quadVariables2, quadValues, rhs, Double.MAX_VALUE);
    constraints.add(constraint);
    return new BonminConstraint(constraint);    
  }

  
  /**
   * Creates a linear constraint in quadProgram
   * @param name
   * @param rhs
   * @return
   */
  public BonminConstraint createLinearConstraintEq(String name, double rhs, Map<BonminVariable, Double> coeffs) {
    BonminEngineJNA engine = BonminEngineJNA.INSTANCE;
    Pointer variables[] = new Pointer[coeffs.size()];
    double values[] = new double[coeffs.size()];
    
    int i = 0;
    for (Entry<BonminVariable, Double> v : coeffs.entrySet()) {
      variables[i] = v.getKey().getPointer();
      values[i] = v.getValue();
      ++i;
    }    
    Pointer constraint = engine.createBonminConstraint(quadProgram, name, coeffs.size(), variables, values, 0, new Pointer[0], new Pointer[0], new double[0], rhs, rhs);    
    constraints.add(constraint);
    return new BonminConstraint(constraint);
  }
  
  /**
   * create a quadrartic constraint in quadProgram
   * @param name
   * @param rightHandSide
   * @param coeffs
   */
  public BonminConstraint createQuadraticConstraintEq(String name, double rhs, Map<BonminVariable, Double> linearCoeffs, Map<Pair<BonminVariable,BonminVariable>, Double> quadCoeffs) {
    BonminEngineJNA engine = BonminEngineJNA.INSTANCE;
    Pointer linearVariables[] = new Pointer[linearCoeffs.size()];
    double linearValues[] = new double[linearCoeffs.size()];

    Pointer quadVariables1[] = new Pointer[quadCoeffs.size()];
    Pointer quadVariables2[] = new Pointer[quadCoeffs.size()];
    double quadValues[] = new double[quadCoeffs.size()];

    int i = 0;
    for (Entry<BonminVariable, Double> v : linearCoeffs.entrySet()) {
      linearVariables[i] = v.getKey().getPointer();
      linearValues[i] = v.getValue();
      ++i;
    }    

    i = 0;
    for (Entry<Pair<BonminVariable,BonminVariable>, Double> v : quadCoeffs.entrySet()) {
      quadVariables1[i] = v.getKey().getOne().getPointer();
      quadVariables2[i] = v.getKey().getTwo().getPointer();
      quadValues[i] = v.getValue();
      ++i;
    }    

    Pointer constraint = engine.createBonminConstraint(quadProgram, name, linearCoeffs.size(), linearVariables, linearValues, quadCoeffs.size(), quadVariables1, quadVariables2, quadValues, rhs, rhs);
    constraints.add(constraint);
    return new BonminConstraint(constraint);    
  }
  
  /**
   * Creates a linear minimization objective
   * @param coeffs
   */
  public void createLinearMinimizeObj(Map<BonminVariable, Double> coeffs) {
    BonminEngineJNA engine = BonminEngineJNA.INSTANCE;
    Pointer linearVariables[] = new Pointer[coeffs.size()];
    double linearValues[] = new double[coeffs.size()];
    int i = 0;
    for (Entry<BonminVariable, Double> v : coeffs.entrySet()) {
      linearVariables[i] = v.getKey().getPointer();
      linearValues[i] = v.getValue();
      ++i;
    }  
    
    engine.createBonminObjective(quadProgram, coeffs.size(), linearVariables, linearValues, 0, new Pointer[0], new Pointer[0], new double[0], true);    
  }
 
  /**
   * Creates a linear minimization objective
   * @param coeffs
   */
  public void createLinearMaximizeObj(Map<BonminVariable, Double> coeffs) {
    BonminEngineJNA engine = BonminEngineJNA.INSTANCE;
    Pointer linearVariables[] = new Pointer[coeffs.size()];
    double linearValues[] = new double[coeffs.size()];
    int i = 0;
    for (Entry<BonminVariable, Double> v : coeffs.entrySet()) {
      linearVariables[i] = v.getKey().getPointer();
      linearValues[i] = v.getValue();
      ++i;
    }  
    
    engine.createBonminObjective(quadProgram, coeffs.size(), linearVariables, linearValues, 0, new Pointer[0], new Pointer[0], new double[0], false);
  }
 

  /**
   * Creates a quadratic maximization function.  
   * with an aux variable  
   * @param linearCoeffs
   * @param quadCoeffs
   */
  public void createQuadraticMaximizeObj(Map<BonminVariable, Double> linearCoeffs, Map<Pair<BonminVariable, BonminVariable>, Double> quadCoeffs) {
    BonminEngineJNA engine = BonminEngineJNA.INSTANCE;
    
    Pointer linearVariables[] = new Pointer[linearCoeffs.size()];
    double linearValues[] = new double[linearCoeffs.size()];
    int i = 0;
    for (Entry<BonminVariable, Double> v : linearCoeffs.entrySet()) {
      linearVariables[i] = v.getKey().getPointer();
      linearValues[i] = v.getValue();
      ++i;
    }  
    
    Pointer quadVariables1[] = new Pointer[quadCoeffs.size()];
    Pointer quadVariables2[] = new Pointer[quadCoeffs.size()];
    double quadValues[] = new double[quadCoeffs.size()];
    i = 0;
    for (Entry<Pair<BonminVariable,BonminVariable>, Double> v : quadCoeffs.entrySet()) {
      quadVariables1[i] = v.getKey().getOne().getPointer();
      quadVariables2[i] = v.getKey().getTwo().getPointer();
      quadValues[i] = v.getValue();
      ++i;
    }  
    
    engine.createBonminObjective(quadProgram, linearCoeffs.size(), linearVariables, linearValues, quadCoeffs.size(), quadVariables1, quadVariables2, quadValues, false);
   }

  /**
   * Creates a quadratic minimize function.  
   * with an aux variable  
   * @param linearCoeffs
   * @param quadCoeffs
   */
  public void createQuadraticMinimizeObj(Map<BonminVariable, Double> linearCoeffs, Map<Pair<BonminVariable, BonminVariable>, Double> quadCoeffs) {
    BonminEngineJNA engine = BonminEngineJNA.INSTANCE;
    Pointer linearVariables[] = new Pointer[linearCoeffs.size()];
    double linearValues[] = new double[linearCoeffs.size()];
    int i = 0;
    for (Entry<BonminVariable, Double> v : linearCoeffs.entrySet()) {
      linearVariables[i] = v.getKey().getPointer();
      linearValues[i] = v.getValue();
      ++i;
    }  
    
    
    Pointer quadVariables1[] = new Pointer[quadCoeffs.size()];
    Pointer quadVariables2[] = new Pointer[quadCoeffs.size()];
    double quadValues[] = new double[quadCoeffs.size()];
    i = 0;
    for (Entry<Pair<BonminVariable,BonminVariable>, Double> v : quadCoeffs.entrySet()) {
      quadVariables1[i] = v.getKey().getOne().getPointer();
      quadVariables2[i] = v.getKey().getTwo().getPointer();
      quadValues[i] = v.getValue();
      ++i;
    }  
    
    engine.createBonminObjective(quadProgram, linearCoeffs.size(), linearVariables, linearValues, quadCoeffs.size(), quadVariables1, quadVariables2, quadValues, true);
  }

  /**
   * Cleans up the memory in the quadProgram objects
   */
  public void clearModel() {
    BonminEngineJNA engine = BonminEngineJNA.INSTANCE;
    engine.destroyBonminQuadraticProgram(quadProgram);
  }

  /**
   * Invoke the quadProgram  solver
   */
  public void solve() {
    BonminEngineJNA engine = BonminEngineJNA.INSTANCE;
    engine.solveBonmin(quadProgram);
  }

  /**
   * Get the objective value
   * @return
   */
  public double getObjValue() {
    BonminEngineJNA engine = BonminEngineJNA.INSTANCE;    
    double value = engine.getBonminObjectiveValue(quadProgram);
    return value;
  }

  /**
   * Get the value of variable in the solution
   * @param IpOptVariable
   * @return
   */
  public double getValue(BonminVariable IpOptVariable) {
    BonminEngineJNA engine = BonminEngineJNA.INSTANCE;
    return engine.getBonminSolutionValue(quadProgram,IpOptVariable.getPointer());
  }

  /**
   * Gets the status of the of the optimization
   * @return
   */
  public BonminStatus getStatus() {
    BonminEngineJNA engine = BonminEngineJNA.INSTANCE;
    int status = engine.getBonminStatus(quadProgram); 
    
    if (status == engine.getBonminSolveSuccessStatus()) {
      return BonminStatus.BONMIN_STATUS_SUCCEES;
    }
    if (status == engine.getBonminSolveInfeasibleStatus()) {
      return BonminStatus.BONMIN_STATUS_INFEASIBLE;
    }
    if (status == engine.getBonminSolveLimitExceededStatus()) {
      return BonminStatus.BONMIN_STATUS_LIMIT_EXCEEDED;
    }
    if (status == engine.getBonminSolveMINLPErrorStatus()) {
      return BonminStatus.BONMIN_STATUS_MINLP_ERROR;
    }
    if (status == engine.getBonminSolveContinuousUnboundedStatus()) {
      return BonminStatus.BONMIN_STATUS_CONTINUOUS_UNBOUNDED;
    }
    return BonminStatus.BONMIN_STATUS_MINLP_ERROR;
  }

  /**
   * Set the initial solution
   * @param solution
   */
  public void setInitialSolution(Map<BonminVariable, Double> solution) {
    BonminEngineJNA engine = BonminEngineJNA.INSTANCE;
    Pointer variables[] = new Pointer[solution.size()];
    double values[] = new double[solution.size()];
    
    int i = 0;
    for (Entry<BonminVariable,Double> entry : solution.entrySet()) {
      variables[i] = entry.getKey().getPointer();
      values[i] = entry.getValue();
      ++i;
    }
    
    engine.setBonminInitialSolution(quadProgram, solution.size(), variables, values);
  }
  
}
