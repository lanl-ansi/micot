package gov.lanl.micot.util.math.solver.integerprogram.cplex;

import gov.lanl.micot.util.math.solver.ContinuousVariable;
import gov.lanl.micot.util.math.solver.DiscreteVariable;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.VariableVisitor;
import ilog.concert.IloException;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

/**
 * Variable initialization class
 * @author Carleton Coffrin
 */
public class VariableInit extends VariableVisitor {
	private IloCplex	_cplex;
	private IloNumVar	_cplexVar;
	private Number lb = null;
	private Number ub = null;

	public VariableInit(IloCplex cplex) {
		_cplex = cplex;
	}

	public IloNumVar initVar(Variable var, Number lb, Number ub) {
		_cplexVar = null;
		this.lb = lb;
		this.ub = ub;
		doIt(var);
		return _cplexVar;
	}

	@Override
	public void applyContinuousVariable(ContinuousVariable v) {
		try {
      double lb = this.lb == null ? -Double.MAX_VALUE : this.lb.doubleValue();
      double ub = this.ub == null ? Double.MAX_VALUE : this.ub.doubleValue();      
			_cplexVar = _cplex.numVar(lb, ub, v.getName());
		}
		catch (IloException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void applyDiscreteVariable(DiscreteVariable v) {
		try {
		  int lb = this.lb == null ? Integer.MIN_VALUE : this.lb.intValue();
	    int ub = this.ub == null ? Integer.MAX_VALUE : this.ub.intValue();
      if (lb != 0 && ub != 1 || lb == ub) { // cplex ignores bounds on boolean variables, so this forces it to behave when we have a boolean variable that is fixed
	      _cplexVar = _cplex.intVar(lb, ub, v.getName());
	    }
	    else {
	      _cplexVar = _cplex.boolVar(v.getName());
	    }
		}
		catch (IloException e) {
			e.printStackTrace();
		}
	}
}
