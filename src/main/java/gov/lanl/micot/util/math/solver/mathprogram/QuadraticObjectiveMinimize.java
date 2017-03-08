package gov.lanl.micot.util.math.solver.mathprogram;

import gov.lanl.micot.util.collection.Pair;
import gov.lanl.micot.util.math.solver.LinearSummation;
import gov.lanl.micot.util.math.solver.Variable;

/**
 * Implementation of a minimization function
 * @author Russell Bent
 */
public class QuadraticObjectiveMinimize extends QuadraticObjective {

	/**
	 * Constructor
	 */
	public QuadraticObjectiveMinimize() {
		super(new LinearObjectiveMinimize());
	}
	
	/**
	 * Constructor
	 * @param vars
	 * @param coefficients
	 */
	public QuadraticObjectiveMinimize(Pair<Variable,Variable>[] vars, double[] coefficients){
		super(vars,coefficients,new LinearObjectiveMinimize());
	}

	@Override
	protected void visit(QuadraticObjectiveVisitor v) {
		v.applyObjectiveMinimize(this);
	}
	
	 @Override
	 public String toString() {
	   return "min " + super.toString();
	 }

  @Override
  public void visit(LinearObjectiveVisitor v) {
    v.applyObjectiveMinimize(this);
  }

  @Override
  public void setLinearObjective(LinearObjective obj) {
    if (!(obj instanceof LinearObjectiveMinimize)) {
      throw new RuntimeException("Error: Trying to add a non linear minimization to a quadratic minimization");
    }
    
    super.setLinearObjective(obj);
  }
}
