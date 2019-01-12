package gov.lanl.micot.application.rdt.algorithm.ep.constraint.scenario;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.Load;
import gov.lanl.micot.infrastructure.ep.optimize.ConstraintFactory;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.application.rdt.algorithm.ep.variable.scenario.LineFlowVariableFactory;
import gov.lanl.micot.util.math.solver.QuadraticConstraint;
import gov.lanl.micot.util.math.solver.QuadraticConstraintLessEq;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.InvalidConstraintException;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

/**
 * Puts a capacity constraint on the lines. 
 * This is the convex quadratic constraint
 *  
 * @author Russell Bent
 */
public class LineCapacityConstraint implements ConstraintFactory {

  private Scenario scenario = null;
  
  /**
   * Constraint
   */
  public LineCapacityConstraint(Scenario scenario) {
    this.scenario = scenario;
  }

  /**
   * Get the flow constraint name
   * 
   * @param edge
   * @return
   */
  private String getConstraintName(ElectricPowerFlowConnection edge, String phase) {
    return "Capacity." + phase + "-" + edge.toString() + "." + scenario.getIndex();
  }

  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException, InvalidConstraintException {
    LineFlowVariableFactory flowFactory = new LineFlowVariableFactory(scenario);
    double mvaBase = model.getMVABase();
    
    for (ElectricPowerFlowConnection edge : model.getFlowConnections()) {
      Variable fp_a = flowFactory.getRealVariable(problem, edge, LineFlowVariableFactory.PHASE_A);
      Variable fp_b = flowFactory.getRealVariable(problem, edge, LineFlowVariableFactory.PHASE_B);
      Variable fp_c = flowFactory.getRealVariable(problem, edge, LineFlowVariableFactory.PHASE_C);
      Variable fq_a = flowFactory.getReactiveVariable(problem, edge, LineFlowVariableFactory.PHASE_A);
      Variable fq_b = flowFactory.getReactiveVariable(problem, edge, LineFlowVariableFactory.PHASE_B);
      Variable fq_c = flowFactory.getReactiveVariable(problem, edge, LineFlowVariableFactory.PHASE_C);
        
      double capacity = edge.getCapacityRating() / mvaBase;
      capacity = capacity * capacity; // squared capacity
      
      double fp_a_t = 0;
      double fp_b_t = 0;
      double fp_c_t = 0;
      double fq_a_t = 0;
      double fq_b_t = 0;
      double fq_c_t = 0;
      
      for (Load load : model.getLoads()) {
        fp_a_t += Math.abs(load.getAttribute(Load.REAL_LOAD_A_MAX_KEY) != null ? load.getAttribute(Load.REAL_LOAD_A_MAX_KEY, Double.class) : 0);
        fp_b_t += Math.abs(load.getAttribute(Load.REAL_LOAD_B_MAX_KEY) != null ? load.getAttribute(Load.REAL_LOAD_B_MAX_KEY, Double.class) : 0);
        fp_c_t += Math.abs(load.getAttribute(Load.REAL_LOAD_C_MAX_KEY) != null ? load.getAttribute(Load.REAL_LOAD_C_MAX_KEY, Double.class) : 0);

        fq_a_t += Math.abs(load.getAttribute(Load.REACTIVE_LOAD_A_MAX_KEY) != null ? load.getAttribute(Load.REACTIVE_LOAD_A_MAX_KEY, Double.class) : 0);
        fq_b_t += Math.abs(load.getAttribute(Load.REACTIVE_LOAD_B_MAX_KEY) != null ? load.getAttribute(Load.REACTIVE_LOAD_B_MAX_KEY, Double.class) : 0);
        fq_c_t += Math.abs(load.getAttribute(Load.REACTIVE_LOAD_C_MAX_KEY) != null ? load.getAttribute(Load.REACTIVE_LOAD_C_MAX_KEY, Double.class) : 0);
      }
      
      fp_a_t /= mvaBase; 
      fp_b_t /= mvaBase;
      fp_c_t /= mvaBase;
      fq_a_t /= mvaBase;
      fq_b_t /= mvaBase;
      fq_c_t /= mvaBase;
      
      if (edge.getAttribute(ElectricPowerFlowConnection.HAS_PHASE_A_KEY, Boolean.class)) {
        QuadraticConstraint constraint = new QuadraticConstraintLessEq(getConstraintName(edge, LineFlowVariableFactory.PHASE_A));
        constraint.addVariables(fp_a, fp_a, 1.0);
        constraint.addVariables(fq_a, fq_a, 1.0);
        constraint.setRightHandSide(Math.min(capacity,(fp_a_t + fq_a_t)*(fp_a_t + fq_a_t)));
        problem.addQuadraticConstraint(constraint);
      }
        
      if (edge.getAttribute(ElectricPowerFlowConnection.HAS_PHASE_B_KEY, Boolean.class)) {
        QuadraticConstraint constraint = new QuadraticConstraintLessEq(getConstraintName(edge, LineFlowVariableFactory.PHASE_B));
        constraint.addVariables(fp_b, fp_b, 1.0);
        constraint.addVariables(fq_b, fq_b, 1.0);
        constraint.setRightHandSide(Math.min(capacity,(fp_b_t + fq_b_t)*(fp_b_t + fq_b_t)));
        problem.addQuadraticConstraint(constraint);
      }

      if (edge.getAttribute(ElectricPowerFlowConnection.HAS_PHASE_C_KEY, Boolean.class)) {
        QuadraticConstraint constraint = new QuadraticConstraintLessEq(getConstraintName(edge, LineFlowVariableFactory.PHASE_C));
        constraint.addVariables(fp_c, fp_c, 1.0);
        constraint.addVariables(fq_c, fq_c, 1.0);
        constraint.setRightHandSide(Math.min(capacity,(fp_c_t + fq_c_t)*(fp_c_t + fq_c_t)));
        problem.addQuadraticConstraint(constraint);
      }        
    }
  }
  

}
