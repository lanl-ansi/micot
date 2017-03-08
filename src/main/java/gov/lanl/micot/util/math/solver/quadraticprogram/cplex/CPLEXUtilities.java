package gov.lanl.micot.util.math.solver.quadraticprogram.cplex;

import gov.lanl.micot.util.collection.Pair;
import gov.lanl.micot.util.math.solver.LinearSummation;
import gov.lanl.micot.util.math.solver.QuadraticSummation;
import gov.lanl.micot.util.math.solver.Variable;
import ilog.concert.IloException;
import ilog.concert.IloLQNumExpr;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

import java.util.Map;

/**
 * Some simple utiltiy classes
 * @author Carleton Coffrin, Russell Bent
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
	protected static IloNumExpr buildLinearExpression(IloCplex cplex, Map<Variable, IloNumVar> varLookup, LinearSummation expr) throws IloException {
		IloLinearNumExpr cplexExpr = cplex.linearNumExpr();
		for (Variable var : expr.getVariables()) {
			assert (varLookup.containsKey(var)) : "model vars and constaint vars out of sync";
			cplexExpr.addTerm(expr.getCoefficient(var), varLookup.get(var));
		}
		return cplexExpr;
	}

	/**
   * Function for building CPLEX expressions
   * @param cplex
   * @param varLookup
   * @param expr
   * @return
   * @throws IloException
   */
  protected static IloNumExpr buildQuadraticExpression(IloCplex cplex, Map<Variable, IloNumVar> varLookup, LinearSummation linExpr, QuadraticSummation quadExpr) throws IloException {
    if (quadExpr.getNumberOfVariablePairs() <= 0) {
      return buildLinearExpression(cplex, varLookup, linExpr);
    }
    
    IloLQNumExpr cplexExpr = cplex.lqNumExpr();
    for (Variable var : linExpr.getVariables()) {
      assert (varLookup.containsKey(var)) : "model vars and constaint vars out of sync";
      cplexExpr.addTerm(linExpr.getCoefficient(var), varLookup.get(var));
    }
    
    for (Pair<Variable,Variable> vars : quadExpr.getVariablePairs()) {
      cplexExpr.addTerm(quadExpr.getCoefficient(vars.getOne(),vars.getTwo()), varLookup.get(vars.getOne()), varLookup.get(vars.getTwo()));
    }
    
    return cplexExpr;
  }
	
	
}
