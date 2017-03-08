package gov.lanl.micot.util.math.solver;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import gov.lanl.micot.util.math.solver.ContinuousVariable;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;

/**
 * Is high level representation of the model
 * which can be translated into a solver specific MIP
 * 
 * It is a variable factory, constraint store, and objective store
 * 
 * @author 236322
 *
 */
public abstract class MathModelImpl implements MathModel {

	protected Map<String, Variable> variableByName              = null;
	protected Map<Variable, Integer> _vars                               = null;
	
	/**
	 * Constructor
	 */
	public MathModelImpl() {
		super();
		_vars = new TreeMap<Variable,Integer>();
		variableByName = new TreeMap<String,Variable>();
	}
	
	@Override
	public ContinuousVariable makeContinuousVariable(String name) throws VariableExistsException {
		if (variableByName.containsKey(name)) {			
			throw new VariableExistsException(name);
		}
		ContinuousVariable v = new ContinuousVariable(name);
		variableByName.put(name,v);
		_vars.put(v,_vars.size());
		return v;
	}
		
	@Override
	public ContinuousVariable[] makeContinuousVariable(int num) throws VariableExistsException {
		ContinuousVariable[] vars = new ContinuousVariable[num];
		int offset = _vars.size();
		for(int i = 0; i < num; i++){
			vars[i] = makeContinuousVariable((i+offset)+"");
		}
		return vars;
	}
		
	@Override
	public Iterable<Variable> getVariables() {
		return _vars.keySet();
	}
	
	@Override
	public String toString(){
		StringBuffer buff = new StringBuffer();
		buff.append("Variables:\n");
		for(Variable v : _vars.keySet()){
			buff.append("\t").append(v).append("\n");
		}		
		return buff.toString();
	}
	
	@Override
	public Variable getVariable(String variable) {
		return variableByName.get(variable);
	}

	@Override
	public int getNumberOfVariables() {
		return _vars.size();
	}
	
	/**
	 * Get the index of a variable
	 * @param variable
	 * @return
	 */
	protected int getVariableIndex(Variable variable) {
		return _vars.get(variable);
	}

}
