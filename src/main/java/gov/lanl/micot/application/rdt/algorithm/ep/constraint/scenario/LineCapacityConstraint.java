package gov.lanl.micot.application.rdt.algorithm.ep.constraint.scenario;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.optimize.ConstraintFactory;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.application.rdt.algorithm.ep.variable.scenario.LineActiveVariableFactory;
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
    LineActiveVariableFactory activeFactory = new LineActiveVariableFactory(scenario);
    LineFlowVariableFactory flowFactory = new LineFlowVariableFactory(scenario);
    double mvaBase = model.getMVABase();
    
    for (ElectricPowerFlowConnection edge : model.getFlowConnections()) {
      boolean hasVariable = activeFactory.hasVariable(edge);
      if (hasVariable) {
        Variable z_s = activeFactory.getVariable(problem, edge);
        Variable fp_a = flowFactory.getRealVariable(problem, edge, LineFlowVariableFactory.PHASE_A);
        Variable fp_b = flowFactory.getRealVariable(problem, edge, LineFlowVariableFactory.PHASE_B);
        Variable fp_c = flowFactory.getRealVariable(problem, edge, LineFlowVariableFactory.PHASE_C);
        Variable fq_a = flowFactory.getReactiveVariable(problem, edge, LineFlowVariableFactory.PHASE_A);
        Variable fq_b = flowFactory.getReactiveVariable(problem, edge, LineFlowVariableFactory.PHASE_B);
        Variable fq_c = flowFactory.getReactiveVariable(problem, edge, LineFlowVariableFactory.PHASE_C);
        
        double capacity = edge.getCapacityRating() / mvaBase; 
        capacity = capacity * capacity; // squared capacity
        
        if (edge.getAttribute(ElectricPowerFlowConnection.HAS_PHASE_A_KEY, Boolean.class)) {
          QuadraticConstraint constraint = new QuadraticConstraintLessEq(getConstraintName(edge, LineFlowVariableFactory.PHASE_A));
          constraint.addVariables(fp_a, fp_a, 1.0);
          constraint.addVariables(fq_a, fq_a, 1.0);
          constraint.addVariable(z_s, -capacity);
          constraint.setRightHandSide(0.0);
          problem.addQuadraticConstraint(constraint);
        }
        
        if (edge.getAttribute(ElectricPowerFlowConnection.HAS_PHASE_B_KEY, Boolean.class)) {
          QuadraticConstraint constraint = new QuadraticConstraintLessEq(getConstraintName(edge, LineFlowVariableFactory.PHASE_B));
          constraint.addVariables(fp_b, fp_b, 1.0);
          constraint.addVariables(fq_b, fq_b, 1.0);
          constraint.addVariable(z_s, -capacity);
          constraint.setRightHandSide(0.0);
          problem.addQuadraticConstraint(constraint);
        }

        if (edge.getAttribute(ElectricPowerFlowConnection.HAS_PHASE_C_KEY, Boolean.class)) {
          QuadraticConstraint constraint = new QuadraticConstraintLessEq(getConstraintName(edge, LineFlowVariableFactory.PHASE_C));
          constraint.addVariables(fp_c, fp_c, 1.0);
          constraint.addVariables(fq_c, fq_c, 1.0);
          constraint.addVariable(z_s, -capacity);
          constraint.setRightHandSide(0.0);
          problem.addQuadraticConstraint(constraint);
        }        
      }
    }
  }
  

}
