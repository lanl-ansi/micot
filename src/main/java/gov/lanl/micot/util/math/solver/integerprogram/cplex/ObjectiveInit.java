package gov.lanl.micot.util.math.solver.integerprogram.cplex;

import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.concert.IloObjective;
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
			  if (_cplex.getObjective() == null) {			    
			    _cplex.addMaximize(cplexExpr);			
			  }
			  else {
			    IloObjective objective = _cplex.getObjective();
			    objective.setExpr(cplexExpr);
			  }
			}
			catch (IloException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void applyObjectiveMinimize(LinearObjectiveMinimize o) {
			try {
        IloLinearNumExpr cplexExpr = CPLEXUtilities.buildLinearExpression(_cplex, _varLookup, o);
        if (_cplex.getObjective() == null) {          
          _cplex.addMinimize(cplexExpr);      
        }
        else {
          IloObjective objective = _cplex.getObjective();
          objective.setExpr(cplexExpr);
        }
			}
			catch (IloException e) {
				e.printStackTrace();
			}
		}
	}
	