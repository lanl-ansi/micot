package gov.lanl.micot.util.math.solver.quadraticprogram.cplex;

import ilog.concert.IloException;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.concert.IloObjective;
import ilog.cplex.IloCplex;

import java.util.Map;

import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.mathprogram.QuadraticObjective;
import gov.lanl.micot.util.math.solver.mathprogram.QuadraticObjectiveMaximize;
import gov.lanl.micot.util.math.solver.mathprogram.QuadraticObjectiveMinimize;
import gov.lanl.micot.util.math.solver.mathprogram.QuadraticObjectiveVisitor;

/**
 * Class for visiting the objective function
 * @author Russell Bent
 *
 */
public class ObjectiveInit extends QuadraticObjectiveVisitor {
		private IloCplex									_cplex;
		private Map<Variable, IloNumVar>	_varLookup;

		public ObjectiveInit(IloCplex cplex, Map<Variable, IloNumVar> varLookup) {
			_cplex = cplex;
			_varLookup = varLookup;
		}

		public void initObj(QuadraticObjective obj) {
			doIt(obj);
		}

		@Override
		public void applyObjectiveMaximize(QuadraticObjectiveMaximize o) {
			try {
			  IloNumExpr cplexExpr = CPLEXUtilities.buildQuadraticExpression(_cplex, _varLookup, o, o);
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
		public void applyObjectiveMinimize(QuadraticObjectiveMinimize o) {
			try {
        IloNumExpr cplexExpr = CPLEXUtilities.buildQuadraticExpression(_cplex, _varLookup, o, o);
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
	