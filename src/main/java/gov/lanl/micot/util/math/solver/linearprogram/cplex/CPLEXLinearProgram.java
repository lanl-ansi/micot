package gov.lanl.micot.util.math.solver.linearprogram.cplex;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import gov.lanl.micot.util.collection.Pair;
import gov.lanl.micot.util.math.solver.JNAOSUtilities;
import gov.lanl.micot.util.math.solver.LinearConstraint;
import gov.lanl.micot.util.math.solver.Solution;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.InvalidConstraintException;
import gov.lanl.micot.util.math.solver.exception.SolverException;
import gov.lanl.micot.util.math.solver.exception.SolverExceptionGeneric;
import gov.lanl.micot.util.math.solver.exception.SolverExceptionNoSolution;
import gov.lanl.micot.util.math.solver.exception.SolverExceptionTimeout;
import gov.lanl.micot.util.math.solver.exception.SolverExceptionUnboundedSolution;
import gov.lanl.micot.util.math.solver.linearprogram.cplex.ConstraintInit;
import gov.lanl.micot.util.math.solver.linearprogram.cplex.ObjectiveInit;
import gov.lanl.micot.util.math.solver.linearprogram.cplex.VariableInit;
import gov.lanl.micot.util.math.solver.linearprogram.LinearProgram;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgramFlags;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgramProfile;
import ilog.concert.IloException;
import ilog.concert.IloNumVar;
import ilog.concert.IloRange;
import ilog.cplex.IloCplex;
import ilog.cplex.IloCplex.Status;
import ilog.cplex.IloCplex.UnknownObjectException;

/**
 * Implemention of linear programs for cplex
 * 
 * @author Carleton Coffrin
 */
public class CPLEXLinearProgram extends LinearProgram {

  private boolean debug = false;
  private boolean doDynamicConstraints = false;
  private boolean doDynamicObjective = false;
  private IloCplex cplex;
  private Map<Variable, IloNumVar> varLookup = null;
  private Map<LinearConstraint, IloRange> constraintLookup = null;
  private Set<LinearConstraint> unAddedConstraints = null;
  private double timeout = Double.POSITIVE_INFINITY;  
  private int rootAlgorithm = -1;
  private int preSolveDual = -1;
  private boolean numericalEmphasis = false;
  private int maxThreads = 0;
  
  /**
   * Make sure that the path to the typical location of the dlls is added (this
   * is a hack, but it works)
   */
  static {
    JNAOSUtilities.OSType osType = JNAOSUtilities.getOSType();
    String osDirectory = JNAOSUtilities.getOSDirectory(osType);    
    String path = System.getProperty("java.library.path");
    path += File.pathSeparatorChar + System.getProperty("user.dir") + File.separatorChar + ".."
    + File.separatorChar + "micot-libraries" + File.separatorChar + "nativelib" + File.separatorChar + "cplex" + File.separatorChar + osDirectory + File.separatorChar;

    // this is hack to force java to load the new path...
    System.setProperty("java.library.path", path);
    try {
      Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
      fieldSysPath.setAccessible(true);
      fieldSysPath.set(null, null);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Cleans up cplex
   */
  private void cleanCplex() {
    if (cplex != null) {
      try {
        cplex.clearModel();
      }
      catch (IloException e) {
        e.printStackTrace();
      }
      cplex.end();
      cplex = null;
    }
  }
  
  /**
   * Constructor
   */
  protected CPLEXLinearProgram(CPLEXLinearProgramFlags flags) {
    super();
    debug = flags.getBoolean(MathematicalProgramFlags.DEBUG_ON_FLAG);
    doDynamicConstraints = flags.getBoolean(MathematicalProgramFlags.DYNAMIC_CONSTRAINTS_FLAG);
    doDynamicObjective = flags.getBoolean(MathematicalProgramFlags.DYNAMIC_OBJECTIVES_FLAG);
    unAddedConstraints = new HashSet<LinearConstraint>();
    timeout = flags.getDouble(MathematicalProgramFlags.TIMEOUT_FLAG);
    rootAlgorithm = flags.getInt(CPLEXLinearProgramFlags.ROOT_ALGORITHM_FLAG);
    preSolveDual = flags.getInt(CPLEXLinearProgramFlags.PRESOLVE_DUAL_FLAG);
    numericalEmphasis = flags.getBoolean(CPLEXLinearProgramFlags.NUMERICAL_EMPHASIS_FLAG);
    maxThreads = flags.getInt(CPLEXLinearProgramFlags.MAX_THREADS_FLAG);
  }

  @Override
  public void finalize() {
    // make sure the jni memory has been cleaned up
    cleanCplex();
  }
  
  @Override
  public void addLinearConstraint(LinearConstraint constraint) throws InvalidConstraintException {
    super.addLinearConstraint(constraint);
    unAddedConstraints.add(constraint);
  }

  @Override
  public void removeLinearConstraint(LinearConstraint constraint) {
    super.removeLinearConstraint(constraint);
    cleanCplex();
  }
  
  /**
   * The default way of creating a ILOCplex object
   * 
   * @throws SolverExceptionGeneric
   * @throws IloException
   */
  private void setupDefaultCPLEX() throws SolverExceptionGeneric, IloException {
    try {
      cleanCplex();      
      cplex = new IloCplex();
    }
    catch (IloException e) {
      throw new SolverExceptionGeneric(e.getMessage());
    }

    varLookup = new HashMap<Variable, IloNumVar>();
    constraintLookup = new HashMap<LinearConstraint, IloRange>();
    
    VariableInit varFactory = new VariableInit(cplex);

    for (Variable var : getVariables()) {
      IloNumVar cplexVar = varFactory.initVar(var, getLowerBound(var), getUpperBound(var));
      varLookup.put(var, cplexVar);
    }

    ConstraintInit constPost = new ConstraintInit(cplex, varLookup);
    ObjectiveInit objPost = new ObjectiveInit(cplex, varLookup);

    for (LinearConstraint constraint : getLinearConstraints()) {
      IloRange cplexConstraint = constPost.initConst(constraint);
      constraintLookup.put(constraint, cplexConstraint);
    }
    unAddedConstraints = new HashSet<LinearConstraint>();
    objPost.initObj(getLinearObjective());
    
    if (Double.POSITIVE_INFINITY != timeout) {
      cplex.setParam(IloCplex.DoubleParam.TiLim, timeout);
    }
        
    cplex.setParam(IloCplex.IntParam.RootAlg, rootAlgorithm);    
    cplex.setParam(IloCplex.IntParam.PreDual, preSolveDual);
    cplex.setParam(IloCplex.BooleanParam.NumericalEmphasis, numericalEmphasis);
    cplex.setParam(IloCplex.IntParam.Threads, maxThreads);
  }

  /**
   * Adds constraints in a dynamics fashion
   * 
   * @throws IloException
   */
  private void updateExistingCPLEXConstraints() throws IloException {
    ConstraintInit constPost = new ConstraintInit(cplex, varLookup);
    for (LinearConstraint constraint : unAddedConstraints) {
      IloRange cplexConstraint = constPost.initConst(constraint);
      constraintLookup.put(constraint, cplexConstraint);
    }
    unAddedConstraints = new HashSet<LinearConstraint>();
    cplex.setParam(IloCplex.IntParam.RootAlg, IloCplex.Algorithm.Dual);
  }
  
  /**
   * Just update the objective function
   */
  private void updateExistingCPLEXObjective() {
    ObjectiveInit objPost = new ObjectiveInit(cplex, varLookup);
    objPost.initObj(getLinearObjective());
  }

  @Override
  public Solution solve() throws SolverException {
    Solution solution = null;
    try {
      if (cplex == null || !doDynamicConstraints || !doDynamicObjective) {
        setupDefaultCPLEX();
      }
      if (doDynamicConstraints) {
        updateExistingCPLEXConstraints();
      }
      if (doDynamicObjective) {
        updateExistingCPLEXObjective();
      }

      if (!debug) {
        cplex.setOut(null);
      }

      if (cplex.solve()) {
        Status status = cplex.getStatus();
        if (!status.equals(Status.Optimal)) {
          throw new SolverExceptionTimeout();
        }         
        solution = new Solution(cplex.getObjValue(), status.equals(Status.Optimal));
        for (Variable var : getVariables()) {
          // cplex is clever, it will only add variables if they are in the
          // constraints or objective
          try {
            solution.addValue(var, cplex.getValue(varLookup.get(var)));
          }
          catch (UnknownObjectException e) {
            solution.addValue(var, 0);
          }
        }
 
        IloRange ranges[] = constraintLookup.values().toArray(new IloRange[0]);
        double lowers[] = new double[ranges.length];
        double uppers[] = new double[ranges.length];
        if (ranges.length > 0) {
          cplex.getRHSSA(lowers, uppers, ranges);
        }
        
        // set up a reverse hash structure
        LinkedHashMap<IloRange, Integer> temp = new LinkedHashMap<IloRange,Integer>();
        for (int i = 0; i < ranges.length; ++i) {
          temp.put(ranges[i], i);
        }
        
        for (LinearConstraint constraint : getLinearConstraints()) {
          IloRange range = constraintLookup.get(constraint);
          solution.addShadowPrice(constraint, cplex.getDual(range));          
          solution.addShadowPriceRange(constraint, new Pair<Number,Number>(lowers[temp.get(range)],uppers[temp.get(range)]));
        }
        
      }
      else {
        Status status = cplex.getStatus();
        cleanCplex();
        if (status.equals(Status.Unbounded)) {
          throw new SolverExceptionUnboundedSolution();
        }
        if (status.equals(Status.Infeasible)) {
          throw new SolverExceptionNoSolution();
        }
        throw new SolverExceptionGeneric(status + "");
      }
      
      updateProfile();
      
      if (!doDynamicConstraints && !doDynamicObjective) {
        cleanCplex();
      }
    }
    catch (IloException e) {
      e.printStackTrace();
      throw new SolverExceptionGeneric("Concert exception caught: " + e.getMessage());
    }

    return solution;
  }

  @Override
  public void exportModel(String filename) throws FileNotFoundException {
    if (filename.contains(".lp")) {
      try {
        if (cplex == null) {
          setupDefaultCPLEX();
        }
        cplex.exportModel(filename);
      }
      catch (IloException e) {
        e.printStackTrace();
      }
      catch (SolverExceptionGeneric e) {
        e.printStackTrace();
      }
    }
    else {
      super.exportModel(filename);
    }
  }
  
  /**
   * Put some statistics in the profiler
   * @throws IloException 
   */
  private void updateProfile() throws IloException {
    MathematicalProgramProfile profile = getProfile();
    profile.setCPUTime(cplex.getCplexTime());
    profile.setObjectiveValue(cplex.getObjValue());
    profile.setNumberOfColumns(cplex.getNcols());
    profile.setNumberOfRows(cplex.getNrows());
    profile.setSolverStatus(cplex.getStatus().toString());
    profile.setNumberOfSOSConstraints(cplex.getNSOSs());
    profile.setNumberOfBinaryVars(cplex.getNbinVars());
    profile.setNumberOfIntegerVars(cplex.getNintVars());
    profile.setNumberOfContiniousVars(0);
    profile.setNumberOfSemiIntegerVariables(cplex.getNsemiIntVars());
    profile.setNumberOfNonZeroElements(cplex.getNNZs());
    profile.setNumberOfSemiContiniousVariables(cplex.getNsemiContVars());
    profile.setNumberOfQuadraticConstraints(cplex.getNQCs());
  }

}
