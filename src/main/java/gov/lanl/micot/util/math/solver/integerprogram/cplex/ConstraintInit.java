package gov.lanl.micot.util.math.solver.integerprogram.cplex;

import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.concert.IloRange;
import ilog.cplex.IloCplex;

import java.util.Map;

import gov.lanl.micot.util.math.solver.LinearConstraint;
import gov.lanl.micot.util.math.solver.LinearConstraintEquals;
import gov.lanl.micot.util.math.solver.LinearConstraintGreater;
import gov.lanl.micot.util.math.solver.LinearConstraintGreaterEq;
import gov.lanl.micot.util.math.solver.LinearConstraintLess;
import gov.lanl.micot.util.math.solver.LinearConstraintLessEq;
import gov.lanl.micot.util.math.solver.LinearConstraintVisitor;
import gov.lanl.micot.util.math.solver.Variable;

/**
 * Class for initiating the constraints
 * @author Carleton Coffrin
 */
public class ConstraintInit extends LinearConstraintVisitor {
	private IloCplex									_cplex;
	private Map<Variable, IloNumVar>	_varLookup;
	private IloRange                  _constraint;

	public ConstraintInit(IloCplex cplex, Map<Variable, IloNumVar> varLookup) {
		_cplex = cplex;
		_varLookup = varLookup;
	}

	public IloRange initConst(LinearConstraint constraint) {
	  _constraint = null;
		doIt(constraint);
		return _constraint;
	}

	@Override
	public void applyConstraintEquals(LinearConstraintEquals c) {
		try {
			IloLinearNumExpr cplexExpr = CPLEXUtilities.buildLinearExpression(_cplex, _varLookup, c);
			_constraint = _cplex.addEq(cplexExpr, c.getRightHandSide(), c.getName());
		}
		catch (IloException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void applyConstraintGreater(LinearConstraintGreater c) {
		assert (false) : "strict greater not supported by cplex...";
	}

	@Override
	public void applyConstraintGreaterEq(LinearConstraintGreaterEq c) {
		try {
			IloLinearNumExpr cplexExpr = CPLEXUtilities.buildLinearExpression(_cplex, _varLookup, c);
			_constraint =_cplex.addGe(cplexExpr, c.getRightHandSide(), c.getName());
		}
		catch (IloException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void applyConstraintLess(LinearConstraintLess c) {
		assert (false) : "strict less not supported by cplex...";
	}

	@Override
	public void applyConstraintLessEq(LinearConstraintLessEq c) {
		try {
			IloLinearNumExpr cplexExpr = CPLEXUtilities.buildLinearExpression(_cplex, _varLookup, c);
			_constraint = _cplex.addLe(cplexExpr, c.getRightHandSide(), c.getName());
		}
		catch (IloException e) {
			e.printStackTrace();
		}
	}

}
