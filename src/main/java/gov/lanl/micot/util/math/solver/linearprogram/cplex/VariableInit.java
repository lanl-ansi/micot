package gov.lanl.micot.util.math.solver.linearprogram.cplex;

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
			_cplexVar = _cplex.numVar(lb,ub, v.getName());
		}
		catch (IloException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void applyDiscreteVariable(DiscreteVariable v) {
	  System.err.println("Variable " + v.getName() + " is treated as a continous variable in a linear program");	  
		try {
      double lb = this.lb == null ? -Double.MAX_VALUE : this.lb.doubleValue();
      double ub = this.ub == null ? Double.MAX_VALUE : this.ub.doubleValue();      
			_cplexVar = _cplex.numVar(lb, ub, v.getName());
		}
		catch (IloException e) {
			e.printStackTrace();
		}
	}
}
