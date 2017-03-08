package gov.lanl.micot.util.math.solver.mathprogram;

import gov.lanl.micot.util.collection.Pair;
import gov.lanl.micot.util.math.solver.Variable;

/**
 * A maximization objective function
 * @author Russell Bent
 */
public class QuadraticObjectiveMaximize extends QuadraticObjective {

	/**
	 * Constructor
	 */
	public QuadraticObjectiveMaximize() {
		super(new LinearObjectiveMaximize());
	}
	
	/**
	 * Constructor
	 * @param vars
	 * @param coefficients
	 */
	public QuadraticObjectiveMaximize(Pair<Variable,Variable>[] vars, double[] coefficients){
		super(vars,coefficients,new LinearObjectiveMaximize());
	}

	@Override
	protected void visit(QuadraticObjectiveVisitor v) {
		v.applyObjectiveMaximize(this);
	}
	
	@Override
	public String toString() {	  
	  return "max " + super.toString();
	}

  @Override
  public void visit(LinearObjectiveVisitor v) {
    v.applyObjectiveMaximize(this);    
  }
  
  @Override
  public void setLinearObjective(LinearObjective obj) {
    if (!(obj instanceof LinearObjectiveMaximize)) {
      throw new RuntimeException("Error: Trying to add a non linear maximization to a quadratic maximization");
    }
    
    super.setLinearObjective(obj);
  }
}
