package gov.lanl.micot.util.math.solver.linearprogram.cplex;

import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

import java.util.Map;

import gov.lanl.micot.util.math.solver.LinearSummation;
import gov.lanl.micot.util.math.solver.Variable;

/**
 * Some simple utility classes
 * @author Carleton Coffrin
 */
public class CPLEXUtilities {

	/**
	 * No instances
	 */
	private CPLEXUtilities() {		
	}
	
	/**
	 * Function for building CPLEX expressions
	 * @param cplex
	 * @param varLookup
	 * @param expr
	 * @return
	 * @throws IloException
	 */
	protected static IloLinearNumExpr buildLinearExpression(IloCplex cplex, Map<Variable, IloNumVar> varLookup, LinearSummation expr) throws IloException {
		IloLinearNumExpr cplexExpr = cplex.linearNumExpr();
		for (Variable var : expr.getVariables()) {
			assert (varLookup.containsKey(var)) : "model vars and constaint vars out of sync";
			cplexExpr.addTerm(expr.getCoefficient(var), varLookup.get(var));
		}
		return cplexExpr;
	}

	
}
