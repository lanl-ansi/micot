package gov.lanl.micot.util.math.solver.integerprogram.scip;

import java.util.HashMap;
import java.util.Map;

import gov.lanl.micot.util.math.solver.LinearConstraint;
import gov.lanl.micot.util.math.solver.LinearConstraintEquals;
import gov.lanl.micot.util.math.solver.LinearConstraintGreater;
import gov.lanl.micot.util.math.solver.LinearConstraintGreaterEq;
import gov.lanl.micot.util.math.solver.LinearConstraintLess;
import gov.lanl.micot.util.math.solver.LinearConstraintLessEq;
import gov.lanl.micot.util.math.solver.LinearConstraintVisitor;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.scip.Scip;
import gov.lanl.micot.util.math.solver.scip.ScipConstraint;
import gov.lanl.micot.util.math.solver.scip.ScipVariable;

/**
 * Class for initiating the constraints
 * @author Russell Bent
 */
public class ConstraintInit extends LinearConstraintVisitor {
	private Scip									scip;
	private ScipConstraint        constraint;
	private Map<Variable, ScipVariable>	_varLookup;

	/**
	 * Constructor
	 * @param cplex
	 * @param varLookup
	 */
	public ConstraintInit(Scip scip, Map<Variable, ScipVariable> varLookup) {
		this.scip = scip;
		_varLookup = varLookup;
	}

	/**
	 * Initialize the constraints
	 * @param constraint
	 */
	public ScipConstraint initConst(LinearConstraint c) {
	  constraint = null;
    doIt(c);
    return constraint;
	}

	@Override
	public void applyConstraintEquals(LinearConstraintEquals c) {
	  Map<ScipVariable, Double> coeffs = new HashMap<ScipVariable, Double>();
	  for (Variable v : c.getVariables()) {
	    coeffs.put(_varLookup.get(v), c.getCoefficient(v));
	  }
	  scip.createLinearConstraintEq(c.getName(), c.getRightHandSide(), coeffs);
	}

	@Override
	public void applyConstraintGreater(LinearConstraintGreater c) {
		assert (false) : "strict greater not supported by scip...";
	}

	@Override
	public void applyConstraintGreaterEq(LinearConstraintGreaterEq c) {
    Map<ScipVariable, Double> coeffs = new HashMap<ScipVariable, Double>();
    for (Variable v : c.getVariables()) {
      coeffs.put(_varLookup.get(v), c.getCoefficient(v));
    }
    scip.createLinearConstraintGE(c.getName(), c.getRightHandSide(), coeffs);
	}

	@Override
	public void applyConstraintLess(LinearConstraintLess c) {
		assert (false) : "strict less not supported by scip...";
	}

	@Override
	public void applyConstraintLessEq(LinearConstraintLessEq c) {
    Map<ScipVariable, Double> coeffs = new HashMap<ScipVariable, Double>();
    for (Variable v : c.getVariables()) {
      coeffs.put(_varLookup.get(v), c.getCoefficient(v));
    }
    scip.createLinearConstraintLE(c.getName(), c.getRightHandSide(), coeffs);
	}

}
