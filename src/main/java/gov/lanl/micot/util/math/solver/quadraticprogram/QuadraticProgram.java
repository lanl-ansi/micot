package gov.lanl.micot.util.math.solver.quadraticprogram;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import gov.lanl.micot.util.math.solver.ContinuousVariable;
import gov.lanl.micot.util.math.solver.DiscreteVariable;
import gov.lanl.micot.util.math.solver.LinearConstraint;
import gov.lanl.micot.util.math.solver.QuadraticConstraint;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.InvalidConstraintException;
import gov.lanl.micot.util.math.solver.exception.InvalidObjectiveException;
import gov.lanl.micot.util.math.solver.exception.InvalidVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.LinearConstraintStore;
import gov.lanl.micot.util.math.solver.mathprogram.LinearObjective;
import gov.lanl.micot.util.math.solver.mathprogram.LinearObjectiveMaximize;
import gov.lanl.micot.util.math.solver.mathprogram.LinearObjectiveMinimize;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgramImpl;
import gov.lanl.micot.util.math.solver.mathprogram.QuadraticConstraintStore;
import gov.lanl.micot.util.math.solver.mathprogram.QuadraticObjective;
import gov.lanl.micot.util.math.solver.mathprogram.QuadraticObjectiveMaximize;
import gov.lanl.micot.util.math.solver.mathprogram.QuadraticObjectiveMinimize;

/**
 * An implementation of quadratic programs.  These have quadratic constraints and are allowed to have a mix of 
 * continuous and discrete variables 
 * @author Russell Bent
 */
public abstract class QuadraticProgram extends MathematicalProgramImpl  {

  private LinearConstraintStore _linearConstraints                         = null;
  private QuadraticConstraintStore _quadraticConstraints                   = null;
  
  private QuadraticObjective _quadraticObjective                           = null;

	private Map<ContinuousVariable, Integer>   continuousVariables            = null;
	private Map<DiscreteVariable, Integer>     integerVariables               = null;
	
	/**
	 * Constructor
	 * @param isMaximizationProblem
	 */
	public QuadraticProgram() {
		super();
		integerVariables = new LinkedHashMap<DiscreteVariable, Integer>();
		continuousVariables = new LinkedHashMap<ContinuousVariable, Integer>();
    _linearConstraints = new LinearConstraintStore();   
    _quadraticConstraints = new QuadraticConstraintStore();
    _quadraticObjective = new QuadraticObjectiveMinimize();
	}

	@Override
	public DiscreteVariable makeDiscreteVariable(String name) throws VariableExistsException, InvalidVariableException {
		DiscreteVariable variable = super.makeDiscreteVariable(name);		
		integerVariables.put(variable,integerVariables.size());
		return variable;
	}

	@Override
	public ContinuousVariable makeContinuousVariable(String name) throws VariableExistsException {
		ContinuousVariable variable = super.makeContinuousVariable(name);		
		continuousVariables.put(variable,continuousVariables.size());
		return variable;
	}
	
	/**
	 * Get the index of a variable
	 * @param variable
	 * @return
	 */
	protected int getVariableIndex(Variable variable) {
		if (continuousVariables.get(variable) != null) {
			return continuousVariables.get(variable); 
		}
		return integerVariables.get(variable) + continuousVariables.size();
	}

	/**
	 * Get the variables
	 * @return
	 */
	protected Set<DiscreteVariable> getIntegerVariables() {
		return integerVariables.keySet();
	}

	/**
	 * Get the variables
	 * @return
	 */
	protected Set<ContinuousVariable> getContinousVariables() {
		return continuousVariables.keySet();
	}
	
	/**
	 * Get number of variables
	 * @return
	 */
	protected int getNumberOfRealVariables() {
		return continuousVariables.size();
	}

	/**
	 * Get number of variables
	 * @return
	 */
	protected int getNumberOfIntVariables() {
		return integerVariables.size();
	}

	/**
	 * Return the number of search tree nodes
	 * @return
	 */
	public abstract int getNumSearchTreeNodes();
	
  @Override
  public void exportModel(String filename) throws FileNotFoundException {
    PrintStream ps = new PrintStream(filename);
    ps.print(toString());
    ps.close();
  }

  @Override
  public LinearObjective getLinearObjective() {
    return getQuadraticObjective();
  }
  
  @Override
  public void setLinearObjective(LinearObjective obj) throws InvalidObjectiveException {
    // error checking
    if (obj instanceof LinearObjectiveMaximize && getQuadraticObjective() instanceof QuadraticObjectiveMinimize) {
      if (getQuadraticObjective().getNumberOfVariables() == 0) {
        setQuadraticObjective(new QuadraticObjectiveMaximize());
      }
      else {
        LinearObjectiveMinimize minimize = new LinearObjectiveMinimize();
        for (Variable v : obj.getVariables()) {
          minimize.addVariable(v, -obj.getCoefficient(v));
        }        
        obj = minimize;
      }
    }
    
    // more error checking
    if (obj instanceof LinearObjectiveMinimize && getQuadraticObjective() instanceof QuadraticObjectiveMaximize) {
      if (getQuadraticObjective().getNumberOfVariables() == 0) {
        setQuadraticObjective(new QuadraticObjectiveMinimize());
      }
      else {
        LinearObjectiveMaximize maximize = new LinearObjectiveMaximize();
        for (Variable v : obj.getVariables()) {
          maximize.addVariable(v, -obj.getCoefficient(v));
        }        
        obj = maximize;
      }
    }    
    getQuadraticObjective().setLinearObjective(obj);
  }
  
 // @Override
 // public void addExpressionConstraint(ExpressionConstraint constraint) throws InvalidConstraintException {
   // throw new InvalidConstraintException("Quadratic Programs cannot contain expression constraints");
  //}  

  @Override
  public void addLinearConstraint(LinearConstraint constraint) throws InvalidConstraintException {
    _linearConstraints.addLinearConstraint(constraint);   
  }
  
  @Override
  public void removeLinearConstraint(LinearConstraint constraint) {
    _linearConstraints.removeLinearConstraint(constraint);
  }
  
  @Override
  public Iterable<LinearConstraint> getLinearConstraints() {
    return _linearConstraints.getLinearConstraints();
  }
  
  @Override
  public LinearConstraint getLinearConstraint(String constraint) {
     return _linearConstraints.getLinearConstraint(constraint);
  }

  @Override
  public int getNumberOfLinearConstraints() {
    return _linearConstraints.getNumberOfLinearConstraints();
  }

  /**
   * Get the index of a linear constraint
   * @param constraint
   * @return
   */
  protected int getLinearConstraintIndex(LinearConstraint constraint) {
    return _linearConstraints.getIndex(constraint);
  }

  @Override
  public int getNumberOfQuadraticConstraints() {
    return _quadraticConstraints.getNumberOfQuadraticConstraints();
  }

  @Override
  public Iterable<QuadraticConstraint> getQuadraticConstraints() {
    return _quadraticConstraints.getQuadraticConstraints();
  }
  
  @Override
  public void addQuadraticConstraint(QuadraticConstraint constraint) throws InvalidConstraintException {
    _quadraticConstraints.addQuadraticConstraint(constraint);
  }

  @Override
  public  void removeQuadraticConstraint(QuadraticConstraint constraint) {
    _quadraticConstraints.removeQuadraticConstraint(constraint);
  }

  @Override
  public QuadraticConstraint getQuadraticConstraint(String constraint) {
    return _quadraticConstraints.getQuadraticConstraint(constraint);
  }
  
  @Override
  public void setQuadraticObjective(QuadraticObjective obj) throws InvalidObjectiveException {
    _quadraticObjective = obj;
  }

  @Override
  public QuadraticObjective getQuadraticObjective() {
    return _quadraticObjective;
  }

}
