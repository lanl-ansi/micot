package gov.lanl.micot.util.math.solver.integerprogram;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.HashSet;
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
import gov.lanl.micot.util.math.solver.mathprogram.LinearObjectiveMinimize;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgramImpl;
import gov.lanl.micot.util.math.solver.mathprogram.QuadraticObjective;

/**
 * An implementation of integer programs 
 * @author Russell Bent
 */
public abstract class IntegerProgram extends MathematicalProgramImpl  {
  
  protected LinearConstraintStore _linearConstraints                         = null;
  private LinearObjective    _linearObjective                              = null;

	private Map<ContinuousVariable, Integer>   continuousVariables             = null;
	private Map<DiscreteVariable, Integer>     integerVariables               = null;

	/**
	 * Constructor
	 * @param isMaximizationProblem
	 */
	public IntegerProgram() {
		super();
		integerVariables = new LinkedHashMap<DiscreteVariable, Integer>();
		continuousVariables = new LinkedHashMap<ContinuousVariable, Integer>();
    _linearConstraints = new LinearConstraintStore();  
    _linearObjective = new LinearObjectiveMinimize();
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
  public void addQuadraticConstraint(QuadraticConstraint constraint) throws InvalidConstraintException {
    throw new InvalidConstraintException("Integer Programs cannot contain quadratic constraints");
  }

  @Override
  public void setQuadraticObjective(QuadraticObjective obj) throws InvalidObjectiveException {
    throw new InvalidObjectiveException("Integer Programs cannot contain quadratic objectives");
  }

 // @Override
 // public void addExpressionConstraint(ExpressionConstraint constraint) throws InvalidConstraintException {
   // throw new InvalidConstraintException("Integer Programs cannot contain expression constraints");
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
    return 0;
  }
  
  @Override
  public Iterable<QuadraticConstraint> getQuadraticConstraints() {
    return new HashSet<QuadraticConstraint>();
  }

  @Override
  public  void removeQuadraticConstraint(QuadraticConstraint constraint) {
  }

  @Override
  public QuadraticConstraint getQuadraticConstraint(String constraint) {
    return null;
  }
  
  @Override
  public void setLinearObjective(LinearObjective obj) {
    _linearObjective = obj;
  }

  @Override
  public LinearObjective getLinearObjective() {
    return _linearObjective;    
  }

  @Override
  public QuadraticObjective getQuadraticObjective() {
    return null;
  }


}
