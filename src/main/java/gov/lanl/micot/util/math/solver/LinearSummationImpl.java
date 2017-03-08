package gov.lanl.micot.util.math.solver;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * An abstract class which encapsulates the commonalities
 * between linear constraints and a linear objective
 * 
 * @author 236322
 *
 */
public class LinearSummationImpl implements LinearSummation {

	private Set<Variable> _vars;
	private Map<Variable,Double> _coefficients;
	
	/**
	 * Constructor
	 */
	public LinearSummationImpl(){
		_vars = new TreeSet<Variable>();
		_coefficients = new LinkedHashMap<Variable,Double>();
	}
	
	/**
	 * Constructor
	 * @param vars
	 * @param coefficients
	 */
	public LinearSummationImpl(Variable[] vars, double[] coefficients){
		this();
		if (vars.length != coefficients.length) {
			throw new RuntimeException("Error: vars and coeffients out of sync");
		}
		//assert(vars.length != coefficients.length) : "vars and coeffients out of sync";
		for(int i=0; i < vars.length; i++){
			_vars.add(vars[i]);
			_coefficients.put(vars[i], coefficients[i]);
		}
	}
	
	@Override
  public void addVariable(Variable var, double coefficient){
		if (var == null) {
			throw new RuntimeException("Error: tried to add a null variable");
		}
		if (coefficient == 0.0) {
			_vars.remove(var);
			_coefficients.remove(var);
		}
		else {
			_vars.add(var);
			_coefficients.put(var, coefficient);
		}
	}
	
	@Override
  public void addVariable(Variable var) { 
		addVariable(var,1.0);
	}
	
	@Override
  public void sumVariable(Variable var, double coefficient){
		if(_vars.contains(var)){
			_coefficients.put(var, _coefficients.get(var) + coefficient);
		}
		else {
			addVariable(var, coefficient);
		}
	}
	
	@Override
  public void sumVariable(Variable var) { 
		sumVariable(var,1.0);
	}
	
	@Override
  public Iterable<Variable> getVariables() {
		return _vars;
	}
	
	@Override
  public int getNumberOfVariables() {
	  return _vars.size();
	}
	
	@Override
  public double getCoefficient(Variable var) {
		if (_coefficients.get(var) == null) {
			return 0;
		}
		return _coefficients.get(var);
	}
	
	@Override
	public String toString(){
		StringBuffer buff = new StringBuffer();
		boolean first = true;
		for(Variable v : _vars){
			if(first){first = false;}
			else {buff.append(" + ");}
			
			buff.append(_coefficients.get(v)).append("*").append(v.getName());
			
		}
		return buff.toString();
	}
}
