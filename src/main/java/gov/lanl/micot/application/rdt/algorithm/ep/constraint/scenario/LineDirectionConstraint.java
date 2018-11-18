package gov.lanl.micot.application.rdt.algorithm.ep.constraint.scenario;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.optimize.ConstraintFactory;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.application.rdt.algorithm.ep.variable.scenario.LineDirectionVariableFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.variable.scenario.LineFlowVariableFactory;
import gov.lanl.micot.util.math.solver.LinearConstraint;
import gov.lanl.micot.util.math.solver.LinearConstraintGreaterEq;
import gov.lanl.micot.util.math.solver.LinearConstraintLessEq;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.InvalidConstraintException;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

/**
 * Puts a direction constraint on the lines. 
 *  
 * @author Russell Bent
 */
public class LineDirectionConstraint implements ConstraintFactory {

  private Scenario scenario = null;
  
  /**
   * Constraint
   */
  public LineDirectionConstraint(Scenario scenario) {
    this.scenario = scenario;
  }

  /**
   * Get the direction constraint name
   * 
   * @param edge
   * @return
   */
  private String getRealNameLE(ElectricPowerFlowConnection edge, String phase) {
    return "DirectionRealLE." + phase + "-" + edge.toString() + "." + scenario.getIndex();
  }

  /**
   * Get the direction constraint name
   * 
   * @param edge
   * @return
   */
  private String getReactiveNameLE(ElectricPowerFlowConnection edge, String phase) {
    return "DirectionReactiveLE." + phase + "-" + edge.toString() + "." + scenario.getIndex();
  }
  
  /**
   * Get the direction constraint name
   * 
   * @param edge
   * @return
   */
  private String getRealNameGE(ElectricPowerFlowConnection edge, String phase) {
    return "DirectionRealGE." + phase + "-" + edge.toString() + "." + scenario.getIndex();
  }

  /**
   * Get the direction constraint name
   * 
   * @param edge
   * @return
   */
  private String getReactiveNameGE(ElectricPowerFlowConnection edge, String phase) {
    return "DirectionReactiveGE." + phase + "-" + edge.toString() + "." + scenario.getIndex();
  }

  
  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException, InvalidConstraintException {
    LineDirectionVariableFactory directionFactory = new LineDirectionVariableFactory(scenario);
    LineFlowVariableFactory flowFactory = new LineFlowVariableFactory(scenario);
    double mvaBase = model.getMVABase();
    
    for (ElectricPowerFlowConnection edge : model.getFlowConnections()) {
      boolean hasVariable = directionFactory.hasVariable(edge, scenario);
      if (hasVariable) {
        Variable b_s = directionFactory.getVariable(problem, edge);
        Variable fp_a = flowFactory.getRealVariable(problem, edge, LineFlowVariableFactory.PHASE_A);
        Variable fp_b = flowFactory.getRealVariable(problem, edge, LineFlowVariableFactory.PHASE_B);
        Variable fp_c = flowFactory.getRealVariable(problem, edge, LineFlowVariableFactory.PHASE_C);
        Variable fq_a = flowFactory.getReactiveVariable(problem, edge, LineFlowVariableFactory.PHASE_A);
        Variable fq_b = flowFactory.getReactiveVariable(problem, edge, LineFlowVariableFactory.PHASE_B);
        Variable fq_c = flowFactory.getReactiveVariable(problem, edge, LineFlowVariableFactory.PHASE_C);
        
        double capacity = edge.getCapacityRating() / mvaBase; 
        
        if (edge.getAttribute(ElectricPowerFlowConnection.HAS_PHASE_A_KEY, Boolean.class)) {
          LinearConstraint constraint = new LinearConstraintLessEq(getRealNameLE(edge, LineFlowVariableFactory.PHASE_A));
          constraint.addVariable(fp_a, 1.0);
          constraint.addVariable(b_s, capacity);
          constraint.setRightHandSide(capacity);
          problem.addLinearConstraint(constraint);

          constraint = new LinearConstraintGreaterEq(getRealNameGE(edge, LineFlowVariableFactory.PHASE_A));
          constraint.addVariable(fp_a, 1.0);
          constraint.addVariable(b_s, capacity);
          constraint.setRightHandSide(0.0);
          problem.addLinearConstraint(constraint);
          
          constraint = new LinearConstraintLessEq(getReactiveNameLE(edge, LineFlowVariableFactory.PHASE_A));
          constraint.addVariable(fq_a, 1.0);
          constraint.addVariable(b_s, capacity);
          constraint.setRightHandSide(capacity);
          problem.addLinearConstraint(constraint);

          constraint = new LinearConstraintGreaterEq(getReactiveNameGE(edge, LineFlowVariableFactory.PHASE_A));
          constraint.addVariable(fq_a, 1.0);
          constraint.addVariable(b_s, capacity);
          constraint.setRightHandSide(0.0);
          problem.addLinearConstraint(constraint);
        }
               
        if (edge.getAttribute(ElectricPowerFlowConnection.HAS_PHASE_B_KEY, Boolean.class)) {
          LinearConstraint constraint = new LinearConstraintLessEq(getRealNameLE(edge, LineFlowVariableFactory.PHASE_B));
          constraint.addVariable(fp_b, 1.0);
          constraint.addVariable(b_s, capacity);
          constraint.setRightHandSide(capacity);
          problem.addLinearConstraint(constraint);

          constraint = new LinearConstraintGreaterEq(getRealNameGE(edge, LineFlowVariableFactory.PHASE_B));
          constraint.addVariable(fp_b, 1.0);
          constraint.addVariable(b_s, capacity);
          constraint.setRightHandSide(0.0);
          problem.addLinearConstraint(constraint);
          
          constraint = new LinearConstraintLessEq(getReactiveNameLE(edge, LineFlowVariableFactory.PHASE_B));
          constraint.addVariable(fq_b, 1.0);
          constraint.addVariable(b_s, capacity);
          constraint.setRightHandSide(capacity);
          problem.addLinearConstraint(constraint);

          constraint = new LinearConstraintGreaterEq(getReactiveNameGE(edge, LineFlowVariableFactory.PHASE_B));
          constraint.addVariable(fq_b, 1.0);
          constraint.addVariable(b_s, capacity);
          constraint.setRightHandSide(0.0);
          problem.addLinearConstraint(constraint);
        }
        
        if (edge.getAttribute(ElectricPowerFlowConnection.HAS_PHASE_C_KEY, Boolean.class)) {
          LinearConstraint constraint = new LinearConstraintLessEq(getRealNameLE(edge, LineFlowVariableFactory.PHASE_C));
          constraint.addVariable(fp_c, 1.0);
          constraint.addVariable(b_s, capacity);
          constraint.setRightHandSide(capacity);
          problem.addLinearConstraint(constraint);

          constraint = new LinearConstraintGreaterEq(getRealNameGE(edge, LineFlowVariableFactory.PHASE_C));
          constraint.addVariable(fp_c, 1.0);
          constraint.addVariable(b_s, capacity);
          constraint.setRightHandSide(0.0);
          problem.addLinearConstraint(constraint);
          
          constraint = new LinearConstraintLessEq(getReactiveNameLE(edge, LineFlowVariableFactory.PHASE_C));
          constraint.addVariable(fq_c, 1.0);
          constraint.addVariable(b_s, capacity);
          constraint.setRightHandSide(capacity);
          problem.addLinearConstraint(constraint);

          constraint = new LinearConstraintGreaterEq(getReactiveNameGE(edge, LineFlowVariableFactory.PHASE_C));
          constraint.addVariable(fq_c, 1.0);
          constraint.addVariable(b_s, capacity);
          constraint.setRightHandSide(0.0);
          problem.addLinearConstraint(constraint);
        }

        
      }
    }
  }
  

}
