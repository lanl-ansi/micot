package gov.lanl.micot.util.math.solver.integerprogram.cplex;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
import gov.lanl.micot.util.math.solver.integerprogram.IntegerProgram;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgramFlags;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgramProfile;
import ilog.concert.IloException;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;
import ilog.cplex.IloCplex.Status;
import ilog.cplex.IloCplex.UnknownObjectException;

/**
 * Implemention --> Corrected: Implementation of integer programs for cplex
 * 
 * @author Carleton Coffrin Slightly modified -and more comments added- by
 *         Conrado - Sep 2014
 */
public class CPLEXIntegerProgram extends IntegerProgram {

  private int searchedNodes = 0;
  private boolean debug = false;
  private boolean doDynamicConstraints = false;
  private IloCplex cplex;
  private Map<Variable, IloNumVar> varLookup = null;
  private Set<LinearConstraint> unAddedConstraints = null;
  private double timeout = Double.POSITIVE_INFINITY;
  private boolean numericalEmphasis = true;
  private double optimalityGap = 1e-6;
  private boolean doRepeatedPresolve = false;
  private boolean doPresolve = false;
  private int maxThreads = 0;
  private boolean doExport = false;
  private String modelFilename = null;
  private String solutionFilename = null;

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

  @Override
  public void finalize() {
    cleanCplex();
  }

  /**
   * Constructor
   */
  protected CPLEXIntegerProgram(CPLEXIntegerProgramFlags flags) {
    super();
    debug = flags.getBoolean(MathematicalProgramFlags.DEBUG_ON_FLAG);
    doDynamicConstraints = flags.getBoolean(MathematicalProgramFlags.DYNAMIC_CONSTRAINTS_FLAG);
    unAddedConstraints = new HashSet<LinearConstraint>();
    timeout = flags.getDouble(MathematicalProgramFlags.TIMEOUT_FLAG);
    optimalityGap = flags.getDouble(MathematicalProgramFlags.MIP_GAP_TOLERANCE_FLAG);
    numericalEmphasis = flags.getBoolean(CPLEXIntegerProgramFlags.NUMERICAL_EMPHASIS_FLAG);
    doRepeatedPresolve = flags.getBoolean(CPLEXIntegerProgramFlags.DO_REPEATED_PRESOLVE_FLAG);
    maxThreads = flags.getInt(CPLEXIntegerProgramFlags.MAX_THREADS_FLAG);
    doPresolve = flags.getBoolean(CPLEXIntegerProgramFlags.DO_PRESOLVE_FLAG);
    doExport = flags.getBoolean(CPLEXIntegerProgramFlags.DO_EXPORT_FLAG);
    solutionFilename = flags.getString(CPLEXIntegerProgramFlags.SOLUTION_FILENAME_FLAG);
    modelFilename = flags.getString(CPLEXIntegerProgramFlags.MODEL_FILENAME_FLAG);
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
    VariableInit varFactory = new VariableInit(cplex);

    for (Variable var : getVariables()) {
      IloNumVar cplexVar = varFactory.initVar(var, getLowerBound(var), getUpperBound(var));
      varLookup.put(var, cplexVar);
    }

    ConstraintInit constPost = new ConstraintInit(cplex, varLookup);
    ObjectiveInit objPost = new ObjectiveInit(cplex, varLookup);

    for (LinearConstraint constraint : getLinearConstraints()) {
      constPost.initConst(constraint);
    }
    unAddedConstraints = new HashSet<LinearConstraint>();
    objPost.initObj(getLinearObjective());
    cplex.setParam(IloCplex.IntParam.MIPSearch, IloCplex.MIPSearch.Traditional);
    if (Double.POSITIVE_INFINITY != timeout) {
      cplex.setParam(IloCplex.DoubleParam.TiLim, timeout);
    }

    cplex.setParam(IloCplex.BooleanParam.NumericalEmphasis, numericalEmphasis);
    cplex.setParam(IloCplex.DoubleParam.EpGap, optimalityGap);
    cplex.setParam(IloCplex.IntParam.RepeatPresolve, doRepeatedPresolve ? -1 : 0);
    cplex.setParam(IloCplex.IntParam.Threads, maxThreads);
    cplex.setParam(IloCplex.BooleanParam.PreInd, doPresolve ? true : false);

  }

  /**
   * Adds constraints in a dynamics fashion
   * 
   * @throws IloException
   */
  private void updateExistingCPLEX() throws IloException {
    ConstraintInit constPost = new ConstraintInit(cplex, varLookup);
    for (LinearConstraint constraint : unAddedConstraints) {
      constPost.initConst(constraint);
    }
    unAddedConstraints = new HashSet<LinearConstraint>();
    cplex.setParam(IloCplex.IntParam.RootAlg, IloCplex.Algorithm.Dual);
  }

  @Override
  public Solution solve() throws SolverException {
    searchedNodes = 0;
    Solution solution = null;
    try {
      if (cplex == null || !doDynamicConstraints) {
        setupDefaultCPLEX();
      }
      else if (doDynamicConstraints) {
        updateExistingCPLEX();
      }

      // output the model to an lp file
      if (doExport) {
        try {
          exportModel(modelFilename);
        }
        catch (FileNotFoundException e) {
          e.printStackTrace();
        }
      }      
      
      // this turns off the output from cplex
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
          // cplex is clever, it will eliminate variables if they are in the
          // constraints or objective
          // and then when you got to get them... they don't exist.
          try {
            solution.addValue(var, cplex.getValue(varLookup.get(var)));
          }
          catch (UnknownObjectException e) {
            solution.addValue(var, 0);
          }                            
        }

        if (doExport) {
          cplex.writeSolution(solutionFilename);
        }

        
        searchedNodes = cplex.getNnodes();
      }
      else {
        updateProfile();
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
      
      if (!doDynamicConstraints) {
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
  public int getNumSearchTreeNodes() {
    return searchedNodes;
  }

  @Override
  public void exportModel(String filename) throws FileNotFoundException {
    if (filename.contains(".lp")) {
      try {
        filename = filename.replace('\\', File.separatorChar);
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
    try {
      profile.setObjectiveValue(cplex.getObjValue());
    }
    catch (Exception e) {
      profile.setObjectiveValue(Double.NaN);         
    }
  }

}
