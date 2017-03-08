package gov.lanl.micot.util.math.solver.quadraticprogram.bonmin;

import java.util.HashMap;
import java.util.Map;

import gov.lanl.micot.util.math.solver.LinearConstraint;
import gov.lanl.micot.util.math.solver.QuadraticConstraint;
import gov.lanl.micot.util.math.solver.Solution;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.bonmin.Bonmin;
import gov.lanl.micot.util.math.solver.bonmin.BonminConstraint;
import gov.lanl.micot.util.math.solver.bonmin.BonminStatus;
import gov.lanl.micot.util.math.solver.bonmin.BonminVariable;
import gov.lanl.micot.util.math.solver.exception.SolverException;
import gov.lanl.micot.util.math.solver.exception.SolverExceptionGeneric;
import gov.lanl.micot.util.math.solver.exception.SolverExceptionNoSolution;
import gov.lanl.micot.util.math.solver.quadraticprogram.QuadraticProgram;

/**
 * Implemention of quadratic programs for Bonmin
 * 
 * @author Russell Bent
 */
public class BonminQuadraticProgram extends QuadraticProgram {

  private Bonmin bonmin;
  private Map<Variable, BonminVariable> varLookup = null;
  private Map<LinearConstraint, BonminConstraint> linearConstraintLookup = null;
  private Map<QuadraticConstraint, BonminConstraint> quadraticConstraintLookup = null;
  
  /**
   * Cleans up Bonmin
   */
  private void cleanBonmin() {
    if (bonmin != null) {
      bonmin.clearModel();
      bonmin = null;
    }
  }
  
  /**
   * Constructor
   */
  protected BonminQuadraticProgram(BonminQuadraticProgramFlags flags) {
    super();
  }

  @Override
  public void finalize() {
    // make sure the jna memory has been cleaned up
    cleanBonmin();
  }
  
  
  /**
   * The default way of creating a ILOCplex object
   * 
   * @throws SolverExceptionGeneric
   * @throws IloException
   */
  private void setupBonmin() throws SolverExceptionGeneric {
    cleanBonmin();
    bonmin = new Bonmin();

    varLookup = new HashMap<Variable, BonminVariable>();
    linearConstraintLookup = new HashMap<LinearConstraint, BonminConstraint>();
    quadraticConstraintLookup = new HashMap<QuadraticConstraint, BonminConstraint>();

    VariableInit varFactory = new VariableInit(bonmin);
    for (Variable var : getVariables()) {
      BonminVariable BonminVar = varFactory.initVar(var, getLowerBound(var), getUpperBound(var));
      varLookup.put(var, BonminVar);
    }

    ConstraintInit constPost = new ConstraintInit(bonmin, varLookup);
    for (LinearConstraint constraint : getLinearConstraints()) {
      BonminConstraint cplexConstraint = constPost.initConst(constraint);
      linearConstraintLookup.put(constraint, cplexConstraint);
    }
    
    for (QuadraticConstraint constraint : getQuadraticConstraints()) {
      BonminConstraint cplexConstraint = constPost.initConst(constraint);
      quadraticConstraintLookup.put(constraint, cplexConstraint);
    }
    
    ObjectiveInit objPost = new ObjectiveInit(bonmin, varLookup);
    objPost.initObj(getQuadraticObjective());     
    
 // set the initial solution up
    Solution initialSolution = getInitialSolution();
    Map<BonminVariable,Double> solution = new HashMap<BonminVariable,Double>();
    for (Variable v : initialSolution.getVariables()) {
      solution.put(varLookup.get(v), initialSolution.getValueDouble(v));
    }
    
    bonmin.setInitialSolution(solution);
  }

  
  @Override
  public Solution solve() throws SolverException {
    Solution solution = null;
    setupBonmin();
    
    bonmin.solve();
    
    BonminStatus status = bonmin.getStatus();
    if (status.equals(BonminStatus.BONMIN_STATUS_INFEASIBLE)) {
      throw new SolverExceptionNoSolution();
    }
    else if (!status.equals(BonminStatus.BONMIN_STATUS_SUCCEES)) {
      throw new SolverExceptionGeneric(status + "");      
    }
    
    solution = new Solution(bonmin.getObjValue(), status.equals(BonminStatus.BONMIN_STATUS_SUCCEES));
    for (Variable var : getVariables()) {
      solution.addValue(var, bonmin.getValue(varLookup.get(var)));
    }
    
    cleanBonmin();
    return solution;
  }

  @Override
  public int getNumSearchTreeNodes() {
    throw new RuntimeException("Haven't implemented BonminQuadraticProgram::getNumSearchTreeNodes()");
  }

}
