package gov.lanl.micot.util.math.solver.linearprogram.scip;

import java.util.HashMap;
import java.util.Map;

import gov.lanl.micot.util.math.solver.LinearConstraint;
import gov.lanl.micot.util.math.solver.Solution;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.SolverException;
import gov.lanl.micot.util.math.solver.exception.SolverExceptionGeneric;
import gov.lanl.micot.util.math.solver.exception.SolverExceptionNoSolution;
import gov.lanl.micot.util.math.solver.exception.SolverExceptionUnboundedSolution;
import gov.lanl.micot.util.math.solver.linearprogram.LinearProgram;
import gov.lanl.micot.util.math.solver.scip.Scip;
import gov.lanl.micot.util.math.solver.scip.ScipConstraint;
import gov.lanl.micot.util.math.solver.scip.ScipStatus;
import gov.lanl.micot.util.math.solver.scip.ScipVariable;

/**
 * Implemention of linear programs for scip
 * 
 * @author Russell Bent
 */
public class ScipLinearProgram extends LinearProgram {

  private Scip scip;
  private Map<Variable, ScipVariable> varLookup = null;
  private Map<LinearConstraint, ScipConstraint> constraintLookup = null;
  
  /**
   * Cleans up scip
   */
  private void cleanScip() {
    if (scip != null) {
      scip.clearModel();      
      scip = null;
    }
  }
  
  /**
   * Constructor
   */
  protected ScipLinearProgram(ScipLinearProgramFlags flags) {
    super();
  }

  @Override
  public void finalize() {
    // make sure the jna memory has been cleaned up
    cleanScip();
  }
  
  
  /**
   * The default way of creating a ILOCplex object
   * 
   * @throws SolverExceptionGeneric
   * @throws IloException
   */
  private void setupScip() throws SolverExceptionGeneric {
    cleanScip();
    scip = new Scip();

    varLookup = new HashMap<Variable, ScipVariable>();
    constraintLookup = new HashMap<LinearConstraint, ScipConstraint>();
    
    VariableInit varFactory = new VariableInit(scip);
    for (Variable var : getVariables()) {
      ScipVariable scipVar = varFactory.initVar(var, getLowerBound(var), getUpperBound(var));
      varLookup.put(var, scipVar);
    }

    ConstraintInit constPost = new ConstraintInit(scip, varLookup);
    for (LinearConstraint constraint : getLinearConstraints()) {
      ScipConstraint cplexConstraint = constPost.initConst(constraint);
      constraintLookup.put(constraint, cplexConstraint);
    }
    
    ObjectiveInit objPost = new ObjectiveInit(scip, varLookup);
    objPost.initObj(getLinearObjective());        
  }

  
  @Override
  public Solution solve() throws SolverException {
    Solution solution = null;
    setupScip();
    
    scip.preSolve();
    scip.solve();
    
    ScipStatus status = scip.getStatus();
    if (status.equals(ScipStatus.SCIP_STATUS_UNBOUNDED)) {
      throw new SolverExceptionUnboundedSolution();
    }
    else if (status.equals(ScipStatus.SCIP_STATUS_INFEASIBLE)) {
      throw new SolverExceptionNoSolution();
    }
    else if (!status.equals(ScipStatus.SCIP_STATUS_OPTIMAL)) {
      throw new SolverExceptionGeneric(status + "");      
    }
    
    solution = new Solution(scip.getObjValue(), status.equals(ScipStatus.SCIP_STATUS_OPTIMAL));
    for (Variable var : getVariables()) {
      solution.addValue(var, scip.getValue(varLookup.get(var)));
    }
    
    return solution;
  }

}
