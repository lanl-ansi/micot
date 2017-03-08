package gov.lanl.micot.util.math.solver.mathprogram;

import gov.lanl.micot.util.collection.Pair;
import gov.lanl.micot.util.math.solver.DiscreteVariable;
import gov.lanl.micot.util.math.solver.LinearConstraint;
import gov.lanl.micot.util.math.solver.MathModelImpl;
import gov.lanl.micot.util.math.solver.QuadraticConstraint;
import gov.lanl.micot.util.math.solver.Solution;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.VariableImpl;
import gov.lanl.micot.util.math.solver.exception.InvalidVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;

import java.util.Map;
import java.util.TreeMap;

/**
 * Is high level representation of the model
 * which can be translated into a solver specific MP
 * 
 * It is a variable factory, constraint store, and objective store
 * 
 * @author 236322
 *
 */
public abstract class MathematicalProgramImpl extends MathModelImpl implements MathematicalProgram  {

	private Map<Variable,Number> upperBounds                        = null;
	private Map<Variable,Number> lowerBounds                        = null;
	
	private Solution initialSolution                                = null;

	private MathematicalProgramProfile profile                      = null;
	
	/**
	 * Constructor
	 */
	public MathematicalProgramImpl() {
		super();
		lowerBounds = new TreeMap<Variable,Number>();
		upperBounds = new TreeMap<Variable,Number>();
		initialSolution = new Solution(0, false);
		profile = new MathematicalProgramProfile();
	}

	@Override
	public DiscreteVariable makeDiscreteVariable(String name) throws VariableExistsException, InvalidVariableException {
		if (variableByName.containsKey(name)) {
			throw new VariableExistsException(name);
		}
		DiscreteVariable v = new DiscreteVariable(name);
		variableByName.put(name,v);
		_vars.put(v,_vars.size());
		return v;
	}

	@Override
	public DiscreteVariable[] makeDiscreteVariable(int num) throws VariableExistsException, InvalidVariableException {
		DiscreteVariable[] vars = new DiscreteVariable[num];
		int offset = _vars.size();
		for(int i = 0; i < num; i++){
			vars[i] = makeDiscreteVariable((i+offset)+"");
		}
		return vars;
	}

	@Override
	public void addBounds(Variable var, double lb, double ub) {
		upperBounds.put(var, ub);
		lowerBounds.put(var, lb);
	}

	@Override
	public String toString(){
		StringBuffer buff = new StringBuffer();
		buff.append("Variables:\n");
		for(Variable v : getVariables()){
			String type = ((VariableImpl) v).getClass().getSimpleName();
			buff.append("\t").append(type+":").append(v).append("\n");
		}
		buff.append("Constraints:\n");
		for(LinearConstraint v : getLinearConstraints()){
			buff.append("\t").append(v.toString()).append("\n");
		}
		if(upperBounds.size()>0){
			buff.append("Upper Bounds:\n");
			for(Variable v : upperBounds.keySet()){
				buff.append("\t").append(v.toString()).append("\t").append(upperBounds.get(v)).append("\n");
			}
		}
		if(lowerBounds.size()>0){
			buff.append("Lower Bounds:\n");
			for(Variable v : lowerBounds.keySet()){
				buff.append("\t").append(v.toString()).append("\t").append(lowerBounds.get(v)).append("\n");
			}
		}

		buff.append("Objective:\n");

		if (getQuadraticObjective() != null && getQuadraticObjective().getNumberOfVariables()>0) {
			buff.append("\t").append(getQuadraticObjective().toString()).append("\n");
		}
		else if(getLinearObjective().getNumberOfVariables()>0){
			buff.append("\t").append(getLinearObjective().toString()).append("\n");
		}

		return buff.toString();
	}

	@Override
	public void normalizeConstraintsLocally() {
		for (LinearConstraint c : getLinearConstraints()) {
			// The smallest coefficient, or the RHS,
			// whichever is smaller.
			Double factor = Double.POSITIVE_INFINITY;   

			for (Variable v : c.getVariables()) {
				if (c.getCoefficient(v) != 0.0 && Math.abs(c.getCoefficient(v)) < factor) {
					factor = Math.abs(c.getCoefficient(v));
				}
			}

			if (c.getRightHandSide() != 0.0) {
				factor = Math.min(Math.abs(factor), Math.abs(c.getRightHandSide()));
			}

			if (factor <= 0.0 || factor.isInfinite() || factor.isNaN()) {
				throw new RuntimeException("Unnormalizable constraint.");
			}

			for (Variable v : c.getVariables()) {
				c.addVariable(v, c.getCoefficient(v) * 1.0/factor);
			}

			c.setRightHandSide(c.getRightHandSide() * 1.0/factor);
		}

		for (QuadraticConstraint c : getQuadraticConstraints()) {
			// The smallest coefficient, or the RHS,
			// whichever is smaller.
			Double factor = Double.POSITIVE_INFINITY;   

			for (Variable v : c.getVariables()) {
				if (c.getCoefficient(v) != 0.0 && Math.abs(c.getCoefficient(v)) < factor) {
					factor = Math.abs(c.getCoefficient(v));
				}
			}

			for (Pair<Variable,Variable> vars : c.getVariablePairs()) {
				Variable v1 = vars.getOne();
				Variable v2 = vars.getTwo();

				if (c.getCoefficient(v1,v2) != 0.0 && Math.abs(c.getCoefficient(v1,v2)) < factor) {
					factor = Math.abs(c.getCoefficient(v1,v2));
				}
			}

			if (c.getRightHandSide() != 0.0) {
				factor = Math.min(Math.abs(factor), Math.abs(c.getRightHandSide()));
			}

			if (factor <= 0.0 || factor.isInfinite() || factor.isNaN()) {
				throw new RuntimeException("Unnormalizable constraint.");
			}

			for (Variable v : c.getVariables()) {
				c.addVariable(v, c.getCoefficient(v) * 1.0/factor);
			}

			for (Pair<Variable,Variable> vars : c.getVariablePairs()) {
				Variable v1 = vars.getOne();
				Variable v2 = vars.getTwo();
				c.addVariables(v1, v2, c.getCoefficient(v1,v2) * 1.0/factor);
			}

			c.setRightHandSide(c.getRightHandSide() * 1.0/factor);
		}


	}

	@Override
	public void normalizeConstraintsGlobally() {
		Double factor = Double.POSITIVE_INFINITY;   

		for (LinearConstraint c : getLinearConstraints()) {     
			for (Variable v : c.getVariables()) {
				if (c.getCoefficient(v) != 0.0 && Math.abs(c.getCoefficient(v)) < factor) {
					factor = Math.abs(c.getCoefficient(v));
				}
			}
			if (c.getRightHandSide() != 0.0) {
				factor = Math.min(Math.abs(factor), Math.abs(c.getRightHandSide()));
			}
		}

		for (QuadraticConstraint c : getQuadraticConstraints()) {     
			for (Variable v : c.getVariables()) {
				if (c.getCoefficient(v) != 0.0 && Math.abs(c.getCoefficient(v)) < factor) {
					factor = Math.abs(c.getCoefficient(v));
				}
			}

			for (Pair<Variable,Variable> vars : c.getVariablePairs()) {
				Variable v1 = vars.getOne();
				Variable v2 = vars.getTwo();

				if (c.getCoefficient(v1,v2) != 0.0 && Math.abs(c.getCoefficient(v1,v2)) < factor) {
					factor = Math.abs(c.getCoefficient(v1,v2));
				}
			}

			if (c.getRightHandSide() != 0.0) {
				factor = Math.min(Math.abs(factor), Math.abs(c.getRightHandSide()));
			}
		}

		for (LinearConstraint c : getLinearConstraints()) {     
			if (factor <= 0.0 || factor.isInfinite() || factor.isNaN()) {
				throw new RuntimeException("Unnormalizable constraint.");
			}

			for (Variable v : c.getVariables()) {
				c.addVariable(v, c.getCoefficient(v) * 1.0/factor);
			}

			c.setRightHandSide(c.getRightHandSide() * 1.0/factor);
		}

		for (QuadraticConstraint c : getQuadraticConstraints()) {     
			if (factor <= 0.0 || factor.isInfinite() || factor.isNaN()) {
				throw new RuntimeException("Unnormalizable constraint.");
			}

			for (Variable v : c.getVariables()) {
				c.addVariable(v, c.getCoefficient(v) * 1.0/factor);
			}

			for (Pair<Variable,Variable> vars : c.getVariablePairs()) {
				Variable v1 = vars.getOne();
				Variable v2 = vars.getTwo();
				c.addVariables(v1, v2, c.getCoefficient(v1,v2) * 1.0/factor);
			}

			c.setRightHandSide(c.getRightHandSide() * 1.0/factor);
		}

	}

	/**
	 * Get the upper bound
	 * @param v
	 * @return
	 */
	public Number getUpperBound(Variable v) {
		return upperBounds.get(v);
	}

	/**
	 * Get the lower bound
	 * @param v
	 * @return
	 */
	public Number getLowerBound(Variable v) {
		return lowerBounds.get(v);
	}

	@Override
	public void setInitialSolution(Solution initialSolution) {
	  this.initialSolution = initialSolution;
	}
	
	@Override
	public Solution getInitialSolution() {
	  return initialSolution;
	}

	@Override
	public MathematicalProgramProfile getProfile() {
	  return profile;
	}

}
