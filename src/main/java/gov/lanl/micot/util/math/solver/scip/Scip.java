package gov.lanl.micot.util.math.solver.scip;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.sun.jna.Pointer;

import gov.lanl.micot.util.collection.Pair;

/**
 * This class manages the interaction with the JNA wrapper of Scip
 * @author Russell Bent
 * Slightly modified by Conrado - Nov 2014
 */
public class Scip {

  private Pointer scip = null;
  private Collection<Pointer> variables = null;
  private Collection<Pointer> constraints = null;
  
  public static final String SCIP_GAP_LIMIT_FLAG = "limits/gap";
  public static final String SCIP_FEASBILITY_GAP_LIMIT_FLAG = "numerics/feastol";
  public static final String SCIP_VERBOSE_FLAG = "display/verblevel";
  public static final String SCIP_NLP_VERBOSE_FLAG = "heuristics/subnlp/nlpverblevel";
  public static final String SCIP_TIME_OUT_FLAG = "limits/time";
    
  /**
   * Constructor
   */
  public Scip() {
    ScipEngineJNA engine = ScipEngineJNA.INSTANCE;
    scip = engine.LibSCIPcreate();  
    engine.LibSCIPincludeDefaultPlugins(scip);
    engine.LibSCIPcreateProbBasic(scip, System.currentTimeMillis()+""); 
    variables = new ArrayList<Pointer>();
    constraints = new ArrayList<Pointer>();
    
  }

  /**
   * Function for creating a continuous variable in scip
   * @param lb
   * @param ub
   * @param name
   * @return
   */
  public ScipVariable createContinuousVariable(double lb, double ub, String name) {
    ScipEngineJNA engine = ScipEngineJNA.INSTANCE;
    Pointer variable = engine.LibSCIPcreateVarBasic(scip, name, lb, ub, 0, engine.LibSCIPgetContinuousVarType());
    engine.LibSCIPaddVar(scip,variable);
    variables.add(variable);
    return new ScipVariable(variable);
  }
  
  /**
   * Function for creating a continuous variable in scip
   * @param lb
   * @param ub
   * @param name
   * @return
   */
  public ScipVariable createBinaryVariable(double lb, double ub, String name) {
    ScipEngineJNA engine = ScipEngineJNA.INSTANCE;
    Pointer variable = engine.LibSCIPcreateVarBasic(scip, name, lb, ub, 0, engine.LibSCIPgetBinaryVarType());
    engine.LibSCIPaddVar(scip,variable);
    variables.add(variable);
    return new ScipVariable(variable);
  }

  /**
   * Function for creating a continuous variable in scip
   * @param lb
   * @param ub
   * @param name
   * @return
   */
  public ScipVariable createDiscreteVariable(double lb, double ub, String name) {
    ScipEngineJNA engine = ScipEngineJNA.INSTANCE;
    Pointer variable = engine.LibSCIPcreateVarBasic(scip, name, lb, ub, 0, engine.LibSCIPgetDiscreteVarType());
    engine.LibSCIPaddVar(scip,variable);
    variables.add(variable);
    return new ScipVariable(variable);
  }

  
  /**
   * Creates a linear constraint in scip
   * @param name
   * @param rhs
   * @return
   */
  public ScipConstraint createLinearConstraintLE(String name, double rhs, Map<ScipVariable, Double> coeffs) {
    ScipEngineJNA engine = ScipEngineJNA.INSTANCE;
    Pointer variables[] = new Pointer[coeffs.size()];
    double values[] = new double[coeffs.size()];
    
    int i = 0;
    for (Entry<ScipVariable, Double> v : coeffs.entrySet()) {
      variables[i] = v.getKey().getPointer();
      values[i] = v.getValue();
      ++i;
    }    
    Pointer constraint = engine.LibSCIPcreateConsBasicLinear(scip, name, coeffs.size(), variables, values, -engine.LibSCIPinfinity(scip), rhs);    
    engine.LibSCIPaddCons(scip, constraint); 
    constraints.add(constraint);
    return new ScipConstraint(constraint);
  }

  /**
   * Create a quadratic LE constraint  
   * @param name
   * @param rhs
   * @param linearCoeffs
   * @param quadCoeffs
   * @return
   */
  public ScipConstraint createQuadraticConstraintLE(String name, double rhs, Map<ScipVariable, Double> linearCoeffs, Map<Pair<ScipVariable,ScipVariable>, Double> quadCoeffs) {
    ScipEngineJNA engine = ScipEngineJNA.INSTANCE;
    Pointer linearVariables[] = new Pointer[linearCoeffs.size()];
    double linearValues[] = new double[linearCoeffs.size()];

    Pointer quadVariables1[] = new Pointer[quadCoeffs.size()];
    Pointer quadVariables2[] = new Pointer[quadCoeffs.size()];
    double quadValues[] = new double[quadCoeffs.size()];

    int i = 0;
    for (Entry<ScipVariable, Double> v : linearCoeffs.entrySet()) {
      linearVariables[i] = v.getKey().getPointer();
      linearValues[i] = v.getValue();
      ++i;
    }    

    i = 0;
    for (Entry<Pair<ScipVariable,ScipVariable>, Double> v : quadCoeffs.entrySet()) {
      quadVariables1[i] = v.getKey().getOne().getPointer();
      quadVariables2[i] = v.getKey().getTwo().getPointer();
      quadValues[i] = v.getValue();
      ++i;
    }    

    Pointer constraint = engine.LibSCIPcreateConsBasicQuadratic(scip, name, linearCoeffs.size(), linearVariables, linearValues, quadCoeffs.size(), quadVariables1, quadVariables2, quadValues, -engine.LibSCIPinfinity(scip), rhs);
    engine.LibSCIPaddCons(scip, constraint);  
    constraints.add(constraint);
    return new ScipConstraint(constraint);    
  }

  
  
  /**
   * Creates a linear constraint in scip
   * @param name
   * @param rhs
   * @return
   */
  public ScipConstraint createLinearConstraintGE(String name, double rhs, Map<ScipVariable, Double> coeffs) {
    ScipEngineJNA engine = ScipEngineJNA.INSTANCE;
    Pointer variables[] = new Pointer[coeffs.size()];
    double values[] = new double[coeffs.size()];
    
    int i = 0;
    for (Entry<ScipVariable, Double> v : coeffs.entrySet()) {
      variables[i] = v.getKey().getPointer();
      values[i] = v.getValue();
      ++i;
    }    
    Pointer constraint = engine.LibSCIPcreateConsBasicLinear(scip, name, coeffs.size(), variables, values, rhs, engine.LibSCIPinfinity(scip));    
    engine.LibSCIPaddCons(scip, constraint);
    constraints.add(constraint);
    return new ScipConstraint(constraint);
  }

  /**
   * Create a quadratic >= constraint
   * @param name
   * @param rhs
   * @param linearCoeffs
   * @param quadCoeffs
   * @return
   */
  public ScipConstraint createQuadraticConstraintGE(String name, double rhs, Map<ScipVariable, Double> linearCoeffs, Map<Pair<ScipVariable,ScipVariable>, Double> quadCoeffs) {
    ScipEngineJNA engine = ScipEngineJNA.INSTANCE;
    Pointer linearVariables[] = new Pointer[linearCoeffs.size()];
    double linearValues[] = new double[linearCoeffs.size()];

    Pointer quadVariables1[] = new Pointer[quadCoeffs.size()];
    Pointer quadVariables2[] = new Pointer[quadCoeffs.size()];
    double quadValues[] = new double[quadCoeffs.size()];

    int i = 0;
    for (Entry<ScipVariable, Double> v : linearCoeffs.entrySet()) {
      linearVariables[i] = v.getKey().getPointer();
      linearValues[i] = v.getValue();
      ++i;
    }    

    i = 0;
    for (Entry<Pair<ScipVariable,ScipVariable>, Double> v : quadCoeffs.entrySet()) {
      quadVariables1[i] = v.getKey().getOne().getPointer();
      quadVariables2[i] = v.getKey().getTwo().getPointer();
      quadValues[i] = v.getValue();
      ++i;
    }    

    Pointer constraint = engine.LibSCIPcreateConsBasicQuadratic(scip, name, linearCoeffs.size(), linearVariables, linearValues, quadCoeffs.size(), quadVariables1, quadVariables2, quadValues, rhs, engine.LibSCIPinfinity(scip));
    engine.LibSCIPaddCons(scip, constraint);  
    constraints.add(constraint);
    return new ScipConstraint(constraint);    
  }

  
  /**
   * Creates a linear constraint in scip
   * @param name
   * @param rhs
   * @return
   */
  public ScipConstraint createLinearConstraintEq(String name, double rhs, Map<ScipVariable, Double> coeffs) {
    ScipEngineJNA engine = ScipEngineJNA.INSTANCE;
    Pointer variables[] = new Pointer[coeffs.size()];
    double values[] = new double[coeffs.size()];
    
    int i = 0;
    for (Entry<ScipVariable, Double> v : coeffs.entrySet()) {
      variables[i] = v.getKey().getPointer();
      values[i] = v.getValue();
      ++i;
    }    
    Pointer constraint = engine.LibSCIPcreateConsBasicLinear(scip, name, coeffs.size(), variables, values, rhs, rhs);    
    engine.LibSCIPaddCons(scip, constraint);  
    constraints.add(constraint);
    return new ScipConstraint(constraint);
  }
  
  /**
   * create a quadrartic constraint in scip
   * @param name
   * @param rightHandSide
   * @param coeffs
   */
  public ScipConstraint createQuadraticConstraintEq(String name, double rhs, Map<ScipVariable, Double> linearCoeffs, Map<Pair<ScipVariable,ScipVariable>, Double> quadCoeffs) {
    ScipEngineJNA engine = ScipEngineJNA.INSTANCE;
    Pointer linearVariables[] = new Pointer[linearCoeffs.size()];
    double linearValues[] = new double[linearCoeffs.size()];

    Pointer quadVariables1[] = new Pointer[quadCoeffs.size()];
    Pointer quadVariables2[] = new Pointer[quadCoeffs.size()];
    double quadValues[] = new double[quadCoeffs.size()];

    int i = 0;
    for (Entry<ScipVariable, Double> v : linearCoeffs.entrySet()) {
      linearVariables[i] = v.getKey().getPointer();
      linearValues[i] = v.getValue();
      ++i;
    }    

    i = 0;
    for (Entry<Pair<ScipVariable,ScipVariable>, Double> v : quadCoeffs.entrySet()) {
      quadVariables1[i] = v.getKey().getOne().getPointer();
      quadVariables2[i] = v.getKey().getTwo().getPointer();
      quadValues[i] = v.getValue();
      ++i;
    }    

    Pointer constraint = engine.LibSCIPcreateConsBasicQuadratic(scip, name, linearCoeffs.size(), linearVariables, linearValues, quadCoeffs.size(), quadVariables1, quadVariables2, quadValues, rhs, rhs);
    engine.LibSCIPaddCons(scip, constraint);  
    constraints.add(constraint);
    return new ScipConstraint(constraint);    
  }
  
  /**
   * Creates a linear minimization objective
   * @param coeffs
   */
  public void createLinearMinimizeObj(Map<ScipVariable, Double> coeffs) {
    ScipEngineJNA engine = ScipEngineJNA.INSTANCE;
    for (Entry<ScipVariable, Double> variable : coeffs.entrySet()) {
      engine.LibSCIPchgVarObj(scip, variable.getKey().getPointer(), variable.getValue());
    }
    engine.LibSCIPsetObjsense(scip, engine.LibSCIPgetObjsenseMin());
  }
 
  /**
   * Creates a linear minimization objective
   * @param coeffs
   */
  public void createLinearMaximizeObj(Map<ScipVariable, Double> coeffs) {
    ScipEngineJNA engine = ScipEngineJNA.INSTANCE;
    for (Entry<ScipVariable, Double> variable : coeffs.entrySet()) {
      engine.LibSCIPchgVarObj(scip, variable.getKey().getPointer(), variable.getValue());
    }    
    engine.LibSCIPsetObjsense(scip, engine.LibSCIPgetObjsenseMax());
  }
 

  /**
   * Creates a quadratic maximization function.  Scip does not support this directly. Instead, we have to model it as a constraint
   * with an aux variable  
   * @param linearCoeffs
   * @param quadCoeffs
   */
  public void createQuadraticMaximizeObj(Map<ScipVariable, Double> linearCoeffs, Map<Pair<ScipVariable, ScipVariable>, Double> quadCoeffs) {
    ScipEngineJNA engine = ScipEngineJNA.INSTANCE;
    
    // create the variable
    ScipVariable variable = createContinuousVariable(-engine.LibSCIPinfinity(scip), engine.LibSCIPinfinity(scip), "maximizingVariable");
    engine.LibSCIPchgVarObj(scip, variable.getPointer(), 1.0);
    engine.LibSCIPsetObjsense(scip, engine.LibSCIPgetObjsenseMax());
    
    // create the constraint
    Map<ScipVariable, Double> nlinearCoeffs = new  HashMap<ScipVariable, Double>();
    nlinearCoeffs.putAll(linearCoeffs);
    nlinearCoeffs.put(variable, -1.0);
    
    createQuadraticConstraintGE("maximizingConstraint", 0, nlinearCoeffs, quadCoeffs);
  }


  /**
   * Creates a quadratic minimize function.  Scip does not support this directly. Instead, we have to model it as a constraint
   * with an aux variable  
   * @param linearCoeffs
   * @param quadCoeffs
   */
  public void createQuadraticMinimizeObj(Map<ScipVariable, Double> linearCoeffs, Map<Pair<ScipVariable, ScipVariable>, Double> quadCoeffs) {
    ScipEngineJNA engine = ScipEngineJNA.INSTANCE;
    
    // create the variable
    ScipVariable variable = createContinuousVariable(-engine.LibSCIPinfinity(scip), engine.LibSCIPinfinity(scip), "minimizingVariable");
    engine.LibSCIPchgVarObj(scip, variable.getPointer(), 1.0);
    engine.LibSCIPsetObjsense(scip, engine.LibSCIPgetObjsenseMin());
    
    // create the constraint
    Map<ScipVariable, Double> nlinearCoeffs = new  HashMap<ScipVariable, Double>();
    nlinearCoeffs.putAll(linearCoeffs);
    nlinearCoeffs.put(variable, -1.0);
    
    createQuadraticConstraintLE("minimizingConstraint", 0, nlinearCoeffs, quadCoeffs);
  }

  
  
  /**
   * Cleans up the memory in the scip objects
   */
  public void clearModel() {
    ScipEngineJNA engine = ScipEngineJNA.INSTANCE;
    for (Pointer pointer : constraints) {
      engine.LibSCIPreleaseCons(scip, pointer);
    }
    for (Pointer pointer : variables) {
      engine.LibSCIPreleaseVar(scip, pointer);
    }
     
    engine.LibSCIPfreeTransform(scip);
    engine.LibSCIPfree(scip);
  }

  /**
   * Invoke the scip pre solver
   */
  public void preSolve() {
    ScipEngineJNA engine = ScipEngineJNA.INSTANCE;
    engine.LibSCIPpresolve(scip);
  }
  

  /**
   * Invoke the scip  solver
   */
  public void solve() {
    ScipEngineJNA engine = ScipEngineJNA.INSTANCE;
    engine.LibSCIPsolve(scip);
//    engine.LibSCIPprintSolution(scip); // To print solution on the screen
  }

  /**
   * Get the objective value
   * @return
   */
  public double getObjValue() {
    ScipEngineJNA engine = ScipEngineJNA.INSTANCE;
    Pointer solution = engine.LibSCIPgetBestSol(scip);
    double value = engine.LibSCIPgetObj(scip, solution);
    return value;
  }

  /**
   * Get the value of variable in the solution
   * @param scipVariable
   * @return
   */
  public double getValue(ScipVariable scipVariable) {
    ScipEngineJNA engine = ScipEngineJNA.INSTANCE;
    Pointer solution = engine.LibSCIPgetBestSol(scip);
    return engine.LibSCIPsolGetVal(scip,solution,scipVariable.getPointer());
  }

  public ScipStatus getStatus() {
    ScipEngineJNA engine = ScipEngineJNA.INSTANCE;
    int status = engine.LibSCIPgetStatus(scip); 
    
    if (status == engine.LibSCIPgetScipStatusUnknown()) {
      return ScipStatus.SCIP_STATUS_UNKNOWN;
    }
    if (status == engine.LibSCIPgetScipStatusUserInterrupt()) {
      return ScipStatus.SCIP_STATUS_USERINTERRUPT;
    }
    if (status == engine.LibSCIPgetScipStatusNodeLimit()) {
      return ScipStatus.SCIP_STATUS_NODELIMIT;
    }
    if (status == engine.LibSCIPgetScipStatusTotalNodeLimit()) {
      return ScipStatus.SCIP_STATUS_TOTALNODELIMIT;
    }
    if (status == engine.LibSCIPgetScipStatusStallNodeLimit()) {
      return ScipStatus.SCIP_STATUS_STALLNODELIMIT;      
    }
    if (status == engine.LibSCIPgetScipStatusTimeLimit()) {
      return ScipStatus.SCIP_STATUS_TIMELIMIT;
    }
    if (status == engine.LibSCIPgetScipStatusMemLimit()) {
      return ScipStatus.SCIP_STATUS_MEMLIMIT;
    }
    if (status == engine.LibSCIPgetScipStatusGapLimit()) {
      return ScipStatus.SCIP_STATUS_GAPLIMIT;
    }
    if (status == engine.LibSCIPgetScipStatusSolLimit()) {
      return ScipStatus.SCIP_STATUS_SOLLIMIT;
    }
    if (status == engine.LibSCIPgetScipStatusBestSolLimit()) {
      return ScipStatus.SCIP_STATUS_BESTSOLLIMIT;
    }
    if (status == engine.LibSCIPgetScipStatusOptimal()) {
      return ScipStatus.SCIP_STATUS_OPTIMAL;
    }
    if (status == engine.LibSCIPgetScipStatusInfeasible()) {
      return ScipStatus.SCIP_STATUS_INFEASIBLE;
    }
    if (status == engine.LibSCIPgetScipStatusUnbounded()) {
      return ScipStatus.SCIP_STATUS_UNBOUNDED;
    }
    if (status == engine.LibSCIPgetScipStatusInforunbd()) {
      return ScipStatus.SCIP_STATUS_INFORUNBD;
    }
    return ScipStatus.SCIP_STATUS_UNKNOWN;
  }

  /**
   * Output the model solution
   */
  public void outputModelSolution() {
    ScipEngineJNA engine = ScipEngineJNA.INSTANCE;
    engine.LibSCIPprintSolution(scip); 
  }

  /**
   * Output the model format
   */
  public void outputModelCIP() {
    ScipEngineJNA engine = ScipEngineJNA.INSTANCE;
    engine.LibSCIPprintProblemCIP(scip); 
  }
  
  /**
   * Output the model format
   */
  public void outputModelLP() {
    ScipEngineJNA engine = ScipEngineJNA.INSTANCE;
    engine.LibSCIPprintProblemLP(scip); 
  }

  /**
   * Output the model format
   */
  public void outputModelMPS() {
    ScipEngineJNA engine = ScipEngineJNA.INSTANCE;
    engine.LibSCIPprintProblemMPS(scip); 
  }

  
  /**
   * Output the model format
   */
  public void outputModelCIP(String filename) {
    ScipEngineJNA engine = ScipEngineJNA.INSTANCE;
    engine.LibSCIPprintProblemCIPToFile(scip, filename); 
  }
  
  /**
   * Output the model format
   */
  public void outputModelLP(String filename) {
    ScipEngineJNA engine = ScipEngineJNA.INSTANCE;
    engine.LibSCIPprintProblemLPToFile(scip, filename); 
  }

  /**
   * Output the model format
   */
  public void outputModelMPS(String filename) {
    ScipEngineJNA engine = ScipEngineJNA.INSTANCE;
    engine.LibSCIPprintProblemMPSToFile(scip, filename); 
  }

  /**
   * Set a double parameter
   * @param scipGapLimitFlag
   * @param optimalityGap
   */
  public void setDoubleParam(String scipGapLimitFlag, double param) {
    ScipEngineJNA engine = ScipEngineJNA.INSTANCE;
    engine.LibSCIPsetDoubleParam(scip, scipGapLimitFlag, param);
  }

  /**
   * Set an integer parameter
   * @param scipGapLimitFlag
   * @param optimalityGap
   */
  public void setIntParam(String scipGapLimitFlag, int param) {
    ScipEngineJNA engine = ScipEngineJNA.INSTANCE;
    engine.LibSCIPsetIntParam(scip, scipGapLimitFlag, param);
  }

  /**
   * Set an integer parameter
   * @param scipGapLimitFlag
   * @param optimalityGap
   */
  public void setStringParam(String scipGapLimitFlag, String param) {
    ScipEngineJNA engine = ScipEngineJNA.INSTANCE;
    engine.LibSCIPsetStringParam(scip, scipGapLimitFlag, param);
  }

  /**
   * Set an integer parameter
   * @param scipGapLimitFlag
   * @param optimalityGap
   */
  public void setBoolParam(String scipGapLimitFlag, boolean param) {
    ScipEngineJNA engine = ScipEngineJNA.INSTANCE;
    engine.LibSCIPsetBoolParam(scip, scipGapLimitFlag, param ? 1 : 0);
  }


}
