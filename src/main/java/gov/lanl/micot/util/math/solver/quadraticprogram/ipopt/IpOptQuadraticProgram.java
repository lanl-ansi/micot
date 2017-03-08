package gov.lanl.micot.util.math.solver.quadraticprogram.ipopt;

import java.util.HashMap;
import java.util.Map;

import gov.lanl.micot.util.math.solver.LinearConstraint;
import gov.lanl.micot.util.math.solver.QuadraticConstraint;
import gov.lanl.micot.util.math.solver.Solution;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.SolverException;
import gov.lanl.micot.util.math.solver.exception.SolverExceptionGeneric;
import gov.lanl.micot.util.math.solver.exception.SolverExceptionNoSolution;
import gov.lanl.micot.util.math.solver.ipopt.IpOpt;
import gov.lanl.micot.util.math.solver.ipopt.IpOptConstraint;
import gov.lanl.micot.util.math.solver.ipopt.IpOptStatus;
import gov.lanl.micot.util.math.solver.ipopt.IpOptVariable;
import gov.lanl.micot.util.math.solver.quadraticprogram.QuadraticProgram;

/**
 * Implemention of quadratic programs for ipopt
 * 
 * @author Russell Bent
 */
public class IpOptQuadraticProgram extends QuadraticProgram {

  private IpOpt ipopt;
  private Map<Variable, IpOptVariable> varLookup = null;
  private Map<LinearConstraint, IpOptConstraint> linearConstraintLookup = null;
  private Map<QuadraticConstraint, IpOptConstraint> quadraticConstraintLookup = null;
  
  /**
   * Cleans up ipopt
   */
  private void cleanIpOpt() {
    if (ipopt != null) {
      ipopt.clearModel();
      ipopt = null;
    }
  }
  
  /**
   * Constructor
   */
  protected IpOptQuadraticProgram(IpOptQuadraticProgramFlags flags) {
    super();
  }

  @Override
  public void finalize() {
    // make sure the jna memory has been cleaned up
    cleanIpOpt();
  }
  
  
  /**
   * The default way of creating a ILOCplex object
   * 
   * @throws SolverExceptionGeneric
   * @throws IloException
   */
  private void setupIpOpt() throws SolverExceptionGeneric {
    cleanIpOpt();
    ipopt = new IpOpt();

    varLookup = new HashMap<Variable, IpOptVariable>();
    linearConstraintLookup = new HashMap<LinearConstraint, IpOptConstraint>();
    quadraticConstraintLookup = new HashMap<QuadraticConstraint, IpOptConstraint>();

    VariableInit varFactory = new VariableInit(ipopt);
    for (Variable var : getVariables()) {
      IpOptVariable ipoptVar = varFactory.initVar(var, getLowerBound(var), getUpperBound(var));
      varLookup.put(var, ipoptVar);
    }

    ConstraintInit constPost = new ConstraintInit(ipopt, varLookup);
    for (LinearConstraint constraint : getLinearConstraints()) {
      IpOptConstraint cplexConstraint = constPost.initConst(constraint);
      linearConstraintLookup.put(constraint, cplexConstraint);
    }
    
    for (QuadraticConstraint constraint : getQuadraticConstraints()) {
      IpOptConstraint cplexConstraint = constPost.initConst(constraint);
      quadraticConstraintLookup.put(constraint, cplexConstraint);
    }
    
    ObjectiveInit objPost = new ObjectiveInit(ipopt, varLookup);
    objPost.initObj(getQuadraticObjective());
    
    
    // set the initial solution up
    Solution initialSolution = getInitialSolution();
    Map<IpOptVariable,Double> solution = new HashMap<IpOptVariable,Double>();
    for (Variable v : initialSolution.getVariables()) {
      solution.put(varLookup.get(v), initialSolution.getValueDouble(v));
    }
    
    ipopt.setInitialSolution(solution);
    
    
  }

  
  @Override
  public Solution solve() throws SolverException {
    Solution solution = null;
    setupIpOpt();
    
    ipopt.solve();
    
    IpOptStatus status = ipopt.getStatus();
    if (status.equals(IpOptStatus.IPOPT_STATUS_INFEASIBLE_PROBLEM_DETECTED)) {
      throw new SolverExceptionNoSolution();
    }
    else if (!status.equals(IpOptStatus.IPOPT_STATUS_SOLVE_SUCCEEDED)) {
      throw new SolverExceptionGeneric(status + "");      
    }
    
    solution = new Solution(ipopt.getObjValue(),status.equals(IpOptStatus.IPOPT_STATUS_SOLVE_SUCCEEDED));
    for (Variable var : getVariables()) {
      solution.addValue(var, ipopt.getValue(varLookup.get(var)));
    }
    
    cleanIpOpt();
    return solution;
  }

  @Override
  public int getNumSearchTreeNodes() {
    throw new RuntimeException("Haven't implemented IpOptQuadraticProgram::getNumSearchTreeNodes()");
  }

}
