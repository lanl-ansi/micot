package gov.lanl.micot.util.math.solver.linearprogram.cplex;

import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

import java.util.Map;

import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.mathprogram.LinearObjective;
import gov.lanl.micot.util.math.solver.mathprogram.LinearObjectiveMaximize;
import gov.lanl.micot.util.math.solver.mathprogram.LinearObjectiveMinimize;
import gov.lanl.micot.util.math.solver.mathprogram.LinearObjectiveVisitor;

/**
 * Class for visiting the objective function
 * @author Russell Bent
 *
 */
public class ObjectiveInit extends LinearObjectiveVisitor {
		private IloCplex									_cplex;
		private Map<Variable, IloNumVar>	_varLookup;

		public ObjectiveInit(IloCplex cplex, Map<Variable, IloNumVar> varLookup) {
			_cplex = cplex;
			_varLookup = varLookup;
		}

		public void initObj(LinearObjective obj) {
			doIt(obj);
		}

		@Override
		public void applyObjectiveMaximize(LinearObjectiveMaximize o) {
			try {
				IloLinearNumExpr cplexExpr = CPLEXUtilities.buildLinearExpression(_cplex, _varLookup, o);
				_cplex.addMaximize(cplexExpr);
			}
			catch (IloException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void applyObjectiveMinimize(LinearObjectiveMinimize o) {
			try {
				IloLinearNumExpr cplexExpr = CPLEXUtilities.buildLinearExpression(_cplex, _varLookup, o);
				_cplex.addMinimize(cplexExpr);
			}
			catch (IloException e) {
				e.printStackTrace();
			}
		}
	}
	