package gov.lanl.micot.util.math.solver;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import gov.lanl.micot.util.collection.Pair;

/**
 * An abstract class which encapsulates the commonalities
 * between quadratic constraints and a quadratic objective
 * 
 * @author Russell Bent
 *
 */
public abstract class QuadraticSummationImpl implements QuadraticSummation {

	private Map<Variable, Map<Variable,Double>> _coefficients = null;
	private Set<Pair<Variable,Variable>> _variables = null;
	
	/**
	 * Constructor
	 */
	public QuadraticSummationImpl(){
		_coefficients = new LinkedHashMap<Variable,Map<Variable,Double>>();
		_variables = new LinkedHashSet<Pair<Variable,Variable>>();
	}
	
	/**
	 * Constructor
	 * @param vars
	 * @param coefficients
	 */
	public QuadraticSummationImpl(Pair<Variable, Variable>[] vars, double[] coefficients){
		this();
		if (vars.length != coefficients.length) {
			throw new RuntimeException("Error: vars and coeffients out of sync");
		}
		for(int i=0; i < vars.length; i++) {
		  addVariables(vars[i].getOne(), vars[i].getTwo(), coefficients[i]);
		}
	}
	

	@Override
  public void addVariables(Variable var1, Variable var2, double coefficient){
		if (var1 == null || var2 == null) {
			throw new RuntimeException("Error: tried to add a null variable");
		}
	
		Variable v1 = var1.compareTo(var2) <= 0 ? var1 : var2;
    Variable v2 = var1.compareTo(var2) <= 0 ? var2 : var1;
				
		if (coefficient == 0.0) {
		  if (_coefficients.get(v1) != null) {
		    _coefficients.get(v1).remove(v2);
		    _variables.remove(new Pair<Variable,Variable>(v1,v2));
		  }
		}
		else {
		  if (_coefficients.get(v1) == null) {
		    _coefficients.put(v1, new LinkedHashMap<Variable, Double>());
		    
		  }
			_coefficients.get(v1).put(v2, coefficient);
			_variables.add(new Pair<Variable,Variable>(v1,v2));
		}
	}
	
	@Override
  public void addVariables(Variable var1, Variable var2) {
		addVariables(var1,var2,1.0);
	}
	
	@Override
  public void sumVariables(Variable var1, Variable var2, double coefficient){
	  Variable v1 = var1.compareTo(var2) <= 0 ? var1 : var2;
    Variable v2 = var1.compareTo(var2) <= 0 ? var2 : var1;
	  
		if(_variables.contains(new Pair<Variable,Variable>(v1,v2))) {
			_coefficients.get(v1).put(v2, _coefficients.get(v1).get(v2) + coefficient);
		}
		else {
			addVariables(var1, var2, coefficient);
		}
	}
	
	@Override
  public void sumVariables(Variable var1, Variable var2) { 
		sumVariables(var1,var2,1.0);
	}
	
	@Override
  public Iterable<Pair<Variable,Variable>> getVariablePairs() {
		return _variables;
	}
	
	@Override
  public int getNumberOfVariablePairs() {
	  return _variables.size();
	}
	
	@Override
  public double getCoefficient(Variable var1, Variable var2) {
		if (_coefficients.get(var1) == null) {
			return 0;
		}
		if (_coefficients.get(var1).get(var2) == null) {
		  return 0;
		}
		return _coefficients.get(var1).get(var2);
	}
	
	@Override
	public String toString(){
		StringBuffer buff = new StringBuffer();
		boolean first = true;
		for(Pair<Variable,Variable> v : _variables){
			if(first){
			  first = false;
			}
			else {
			  buff.append(" + ");
			}			
			buff.append(_coefficients.get(v.getOne()).get(v.getTwo())).append("*").append(v.getOne().getName()).append("*").append(v.getTwo().getName());			
		}
		return buff.toString();
	}
}
