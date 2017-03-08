package gov.lanl.micot.util.math.solver.quadraticprogram.scip;

import java.util.HashMap;
import java.util.Map;

import gov.lanl.micot.util.math.solver.LinearConstraint;
import gov.lanl.micot.util.math.solver.QuadraticConstraint;
import gov.lanl.micot.util.math.solver.Solution;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.SolverException;
import gov.lanl.micot.util.math.solver.exception.SolverExceptionGeneric;
import gov.lanl.micot.util.math.solver.exception.SolverExceptionNoSolution;
import gov.lanl.micot.util.math.solver.exception.SolverExceptionUnboundedSolution;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgramFlags;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgramProfile;
import gov.lanl.micot.util.math.solver.quadraticprogram.QuadraticProgram;
import gov.lanl.micot.util.math.solver.scip.Scip;
import gov.lanl.micot.util.math.solver.scip.ScipConstraint;
import gov.lanl.micot.util.math.solver.scip.ScipStatus;
import gov.lanl.micot.util.math.solver.scip.ScipVariable;

/**
 * Implemention--> Corrected: Implementation of quadratic programs for scip
 * 
 * @author Russell Bent
 * Modified/extended by Conrado, Oct 2014
 */
public class ScipQuadraticProgram extends QuadraticProgram {
	private Scip scip;
	private Map<Variable, ScipVariable> varLookup = null;
	private Map<LinearConstraint, ScipConstraint> linearConstraintLookup = null;
	private Map<QuadraticConstraint, ScipConstraint> quadraticConstraintLookup = null;
  
	private double optimalityGap = 1e-4;
	private double feasGap = 1e-4;
	private int    verboseLevel = 4;
	private int    nlpVerboseLevel = 0;
	
	/**
	 * Cleans up scip
	 */
	private void cleanScip() {
		if (scip != null) {
			scip.clearModel();      
			scip = null;
		}
	}
  
	// Constructor
	protected ScipQuadraticProgram(ScipQuadraticProgramFlags flags) {
		super();
		optimalityGap = flags.getDouble(MathematicalProgramFlags.MIP_GAP_TOLERANCE_FLAG);   
		feasGap = flags.getDouble(MathematicalProgramFlags.FEASABILITY_GAP_TOLERANCE_FLAG);
	}

	@Override
	public void finalize() {
		// make sure the jna memory has been cleaned up
		cleanScip();
	}
  
	/** The default way of creating a ILOCplex object
	 * @throws SolverExceptionGeneric
	 * @throws IloException
	 */
	private void setupScip() throws SolverExceptionGeneric {
		cleanScip();
		scip = new Scip();
		
		varLookup = new HashMap<Variable, ScipVariable>();
		linearConstraintLookup = new HashMap<LinearConstraint, ScipConstraint>();
		quadraticConstraintLookup = new HashMap<QuadraticConstraint, ScipConstraint>();

		VariableInit varFactory = new VariableInit(scip);
		for (Variable var : getVariables()) {
			ScipVariable scipVar = varFactory.initVar(var, getLowerBound(var), getUpperBound(var));
			varLookup.put(var, scipVar);
		}

		ConstraintInit constPost = new ConstraintInit(scip, varLookup);
		for (LinearConstraint constraint : getLinearConstraints()) {
			ScipConstraint cplexConstraint = constPost.initConst(constraint);
			linearConstraintLookup.put(constraint, cplexConstraint);
		}
    
		for (QuadraticConstraint constraint : getQuadraticConstraints()) {
			ScipConstraint cplexConstraint = constPost.initConst(constraint);
			quadraticConstraintLookup.put(constraint, cplexConstraint);
		}
    
		ObjectiveInit objPost = new ObjectiveInit(scip, varLookup);
		objPost.initObj(getQuadraticObjective());
	}

  
	@Override
	public Solution solve() throws SolverException {

		Solution solution = null;
		setupScip();

		scip.setDoubleParam(Scip.SCIP_GAP_LIMIT_FLAG, optimalityGap);
		scip.setDoubleParam(Scip.SCIP_FEASBILITY_GAP_LIMIT_FLAG, feasGap); 
		scip.setIntParam(Scip.SCIP_VERBOSE_FLAG, verboseLevel);
		scip.setIntParam(Scip.SCIP_NLP_VERBOSE_FLAG, nlpVerboseLevel);
		
		scip.preSolve();
		scip.solve();
    
		ScipStatus status = scip.getStatus();
		if (status.equals(ScipStatus.SCIP_STATUS_UNBOUNDED)) {
			throw new SolverExceptionUnboundedSolution();
		}
		else if (status.equals(ScipStatus.SCIP_STATUS_INFEASIBLE)) {
			throw new SolverExceptionNoSolution();
		}
		else if (!status.equals(ScipStatus.SCIP_STATUS_OPTIMAL) && !status.equals(ScipStatus.SCIP_STATUS_GAPLIMIT)) {
			throw new SolverExceptionGeneric(status + "");      
		}
    
		solution = new Solution(scip.getObjValue(), status.equals(ScipStatus.SCIP_STATUS_OPTIMAL));
		for (Variable var : getVariables()) {
			solution.addValue(var, scip.getValue(varLookup.get(var)));
		}
    
		updateProfile();
		
		return solution;
	}

	@Override
	public int getNumSearchTreeNodes() {
		throw new RuntimeException("Haven't implemented ScipIntegerProgram::getNumSearchTreeNodes()");
	}
	
  /**
   * Put some statistics in the profiler
   * @throws IloException 
   */
  private void updateProfile() {
    System.err.println("TODO: Extract statistics from SCIP");
    MathematicalProgramProfile profile = getProfile();
    profile.setCPUTime(-100000);
    profile.setObjectiveValue(scip.getObjValue());
    profile.setNumberOfColumns(0);
    profile.setNumberOfRows(0);
    profile.setSolverStatus(scip.getStatus().toString());
    profile.setNumberOfSOSConstraints(0);
    profile.setNumberOfBinaryVars(0);
    profile.setNumberOfIntegerVars(0);
    profile.setNumberOfContiniousVars(0);
    profile.setNumberOfSemiIntegerVariables(0);
    profile.setNumberOfNonZeroElements(0);
    profile.setNumberOfSemiContiniousVariables(0);
    profile.setNumberOfQuadraticConstraints(0);
  }
}
