package gov.lanl.micot.util.math.solver.quadraticprogram.cplex;

import ilog.concert.IloException;
import ilog.concert.IloNumExpr;
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
import gov.lanl.micot.util.math.solver.QuadraticConstraint;
import gov.lanl.micot.util.math.solver.QuadraticConstraintEquals;
import gov.lanl.micot.util.math.solver.QuadraticConstraintGreater;
import gov.lanl.micot.util.math.solver.QuadraticConstraintGreaterEq;
import gov.lanl.micot.util.math.solver.QuadraticConstraintLess;
import gov.lanl.micot.util.math.solver.QuadraticConstraintLessEq;
import gov.lanl.micot.util.math.solver.QuadraticConstraintVisitor;
import gov.lanl.micot.util.math.solver.Variable;

/**
 * Class for initiating the constraints
 * @author Russell Bent
 */
public class ConstraintInit extends QuadraticConstraintVisitor {
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

	public IloRange initConst(QuadraticConstraint constraint) {
    _constraint = null;
    doIt(constraint);
    return _constraint;
  }
	
	@Override
	public void applyConstraintEquals(QuadraticConstraintEquals c) {
		try {
			IloNumExpr cplexExpr = CPLEXUtilities.buildQuadraticExpression(_cplex, _varLookup, c, c);
			_constraint = _cplex.addEq(cplexExpr, c.getRightHandSide(), c.getName());
		}
		catch (IloException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void applyConstraintGreater(QuadraticConstraintGreater c) {
		assert (false) : "strict greater not supported by cplex...";
	}

	@Override
	public void applyConstraintGreaterEq(QuadraticConstraintGreaterEq c) {
		try {
		  IloNumExpr cplexExpr = CPLEXUtilities.buildQuadraticExpression(_cplex, _varLookup, c, c);
			_constraint =_cplex.addGe(cplexExpr, c.getRightHandSide(), c.getName());
		}
		catch (IloException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void applyConstraintLess(QuadraticConstraintLess c) {
		assert (false) : "strict less not supported by cplex...";
	}

	@Override
	public void applyConstraintLessEq(QuadraticConstraintLessEq c) {
		try {
		  IloNumExpr cplexExpr = CPLEXUtilities.buildQuadraticExpression(_cplex, _varLookup, c, c);
			_constraint = _cplex.addLe(cplexExpr, c.getRightHandSide(), c.getName());
		}
		catch (IloException e) {
			e.printStackTrace();
		}
	}

	
	@Override
  public void applyConstraintEquals(LinearConstraintEquals c) {
    try {
      IloNumExpr cplexExpr = CPLEXUtilities.buildLinearExpression(_cplex, _varLookup, c);
      _cplex.addEq(cplexExpr, c.getRightHandSide(), c.getName());
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
      IloNumExpr cplexExpr = CPLEXUtilities.buildLinearExpression(_cplex, _varLookup, c);
      _cplex.addGe(cplexExpr, c.getRightHandSide(), c.getName());
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
      IloNumExpr cplexExpr = CPLEXUtilities.buildLinearExpression(_cplex, _varLookup, c);
      _cplex.addLe(cplexExpr, c.getRightHandSide(), c.getName());
    }
    catch (IloException e) {
      e.printStackTrace();
    }
  }

}
