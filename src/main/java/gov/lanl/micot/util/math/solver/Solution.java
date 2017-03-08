package gov.lanl.micot.util.math.solver;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import gov.lanl.micot.util.collection.Pair;

/**
 * A solution to a MIP model
 * 
 * @author 236322
 *
 */
public class Solution {
	
	protected double _objectiveValue;
	protected Map<Variable,Number> _values;
	protected Map<LinearConstraint,Number> _shadowPrices;
  protected Map<LinearConstraint,Pair<Number,Number>> _shadowPriceRanges;
  protected Map<Variable,Pair<Number,Number>> _reducedCostRanges;
	private boolean _isFeasible = false;
  
	/**
	 * Constructor
	 * @param objValue
	 */
	public Solution(double objValue, boolean isFeasible){
		_objectiveValue = objValue;
		_values = new LinkedHashMap<Variable,Number>();
		_shadowPrices = new LinkedHashMap<LinearConstraint,Number>();
    _shadowPriceRanges = new LinkedHashMap<LinearConstraint,Pair<Number,Number>>();
    _reducedCostRanges = new LinkedHashMap<Variable, Pair<Number,Number>>();
    _isFeasible = isFeasible;
	}
	
	/**
	 * Add a value
	 * @param var
	 * @param val
	 */
	public void addValue(Variable var, Number val){
		_values.put(var, val);
	}
	
	/**
	 * Get the value of a variable
	 * @param var
	 * @return
	 */
	public Number getValue(Variable var){
		assert(_values.containsKey(var)) : "tried to look up a variable that does not exist";
		return _values.get(var);
	}
	
	/**
   * Add a shadow price range
   * @param var
   * @param val
   */
  public void addShadowPriceRange(LinearConstraint con, Pair<Number,Number> val){
    _shadowPriceRanges.put(con, val);
  }
  
  /**
   * Get the value of a variable
   * @param var
   * @return
   */
  public Pair<Number,Number> getShadowPriceRange(SummationConstraint con){
    assert(_shadowPriceRanges.containsKey(con)) : "tried to look up a constraint that does not exist";
    return _shadowPriceRanges.get(con);
  }

  /**
   * Add a shadow price range
   * @param var
   * @param val
   */
  public void addReducedCostRange(Variable var, Pair<Number,Number> val){
    _reducedCostRanges.put(var, val);
  }
  
  /**
   * Get the value of a variable
   * @param var
   * @return
   */
  public Pair<Number,Number> getReducedCostRanges(Variable var){
    assert(_reducedCostRanges.containsKey(var)) : "tried to look up a variable that does not exist";
    return _reducedCostRanges.get(var);
  }

  
	/**
   * Add a shadow price
   * @param var
   * @param val
   */
  public void addShadowPrice(LinearConstraint con, Number val){
    _shadowPrices.put(con, val);
  }
  
  /**
   * Get the value of a variable
   * @param var
   * @return
   */
  public Number getShadowPrice(SummationConstraint con){
    assert(_shadowPrices.containsKey(con)) : "tried to look up a constraint that does not exist";
    return _shadowPrices.get(con);
  }
	
	/**
	 * Get the double value of a variable
	 * @param var
	 * @return
	 */
	public double getValueDouble(Variable var){
		return getValue(var).doubleValue();
	}
	
	/**
	 * Get the integer value of variable
	 * @param var
	 * @return
	 */
	public int getValueInt(Variable var){
		return (int)Math.round(getValue(var).doubleValue());
	}

	 /**
   * Get the double value of a variable
   * @param var
   * @return
   */
  public double getShadowPriceDouble(SummationConstraint con) {
    return getShadowPrice(con).doubleValue();
  }
  
  /**
   * Get the integer value of variable
   * @param var
   * @return
   */
  public int getShadowPriceInt(SummationConstraint con) {
    return (int) Math.round(getShadowPrice(con).doubleValue());
  }
	
	/**
	 * get the objective value
	 * @return
	 */
	public double getObjectiveValue() {
		return _objectiveValue;
	}
	
	/**
	 * filter out small values
	 * @param tolerance
	 */
	public void removeSmallValues(double tolerance){
		Map<Variable,Number> newValues = new HashMap<Variable,Number>();
		for(Variable var : _values.keySet()){
			Number val = _values.get(var);
			if(val.doubleValue() < tolerance){
				newValues.put(var, 0.0);
			}
			else {
				newValues.put(var, val);
			}
		}
		_values = newValues;
	}
	
	@Override
	public String toString(){
		StringBuffer buff = new StringBuffer();
		buff.append("Objective: ").append(_objectiveValue).append("\n");
		
		buff.append("Variables:\n");
		for(Variable v : _values.keySet()){
			buff.append("\t").append(v).append(": ").append(_values.get(v)).append("\n");
		}		
		return buff.toString();
	}
	
	/**
	 * get the variables
	 * @return
	 */
	public Set<Variable> getVariables() {
	  return _values.keySet();
	}
	
	/**
	 * Is the solution a feasible solution
	 * @return
	 */
	public boolean isFeasible() {
	  return _isFeasible;
	}
}
