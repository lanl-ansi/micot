package gov.lanl.micot.application.rdt.algorithm.ep.constraint.scenario;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.Load;
import gov.lanl.micot.infrastructure.ep.optimize.ConstraintFactory;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.application.rdt.algorithm.ep.variable.scenario.LineActiveVariableFactory;
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
 * Forces line flows to 0 if it is inactive. 
 *  
 * @author Russell Bent
 */
public class LineFlowOnOffConstraint implements ConstraintFactory {

  private Scenario scenario = null;
  
  /**
   * Constraint
   */
  public LineFlowOnOffConstraint(Scenario scenario) {
    this.scenario = scenario;
  }

  /**
   * Get the flow le constraint name
   * 
   * @param edge
   * @return
   */
  private String getConstraintLERealName(ElectricPowerFlowConnection edge, String phase) {
    return "FlowOnOffRealLE." + phase + "-" + edge.toString() + "." + scenario.getIndex();
  }

  /**
   * Get the flow ge constraint name
   * 
   * @param edge
   * @return
   */
  private String getConstraintGERealName(ElectricPowerFlowConnection edge, String phase) {
    return "FlowOnOffRealGE." + phase + "-" + edge.toString() + "." + scenario.getIndex();
  }
  
  /**
   * Get the flow le constraint name
   * 
   * @param edge
   * @return
   */
  private String getConstraintLEReactiveName(ElectricPowerFlowConnection edge, String phase) {
    return "FlowOnOffReactiveLE." + phase + "-" + edge.toString() + "." + scenario.getIndex();
  }

  /**
   * Get the flow ge constraint name
   * 
   * @param edge
   * @return
   */
  private String getConstraintGEReactiveName(ElectricPowerFlowConnection edge, String phase) {
    return "FlowOnOffReactiveGE." + phase + "-" + edge.toString() + "." + scenario.getIndex();
  }
  
  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException, InvalidConstraintException {
    LineActiveVariableFactory activeFactory = new LineActiveVariableFactory(scenario,null);
    LineFlowVariableFactory flowFactory = new LineFlowVariableFactory(scenario);
    double mvaBase = model.getMVABase();
    
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

    fp_a_t = fp_b_t = fp_c_t = fq_a_t = fq_b_t = fq_c_t = 200;
    
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
        
        if (edge.getAttribute(ElectricPowerFlowConnection.HAS_PHASE_A_KEY, Boolean.class)) {
          LinearConstraint constraint = new LinearConstraintLessEq(getConstraintLERealName(edge, LineFlowVariableFactory.PHASE_A));
          constraint.addVariable(fp_a, 1.0);
          constraint.addVariable(z_s, Math.max(-capacity,-fp_a_t));
          constraint.setRightHandSide(0.0);
          problem.addLinearConstraint(constraint);
                    
          constraint = new LinearConstraintGreaterEq(getConstraintGERealName(edge, LineFlowVariableFactory.PHASE_A));
          constraint.addVariable(fp_a, 1.0);
          constraint.addVariable(z_s, Math.min(capacity,fp_a_t));
          constraint.setRightHandSide(0.0);
          problem.addLinearConstraint(constraint);
                    
          constraint = new LinearConstraintLessEq(getConstraintLEReactiveName(edge, LineFlowVariableFactory.PHASE_A));
          constraint.addVariable(fq_a, 1.0);
          constraint.addVariable(z_s, Math.max(-capacity,-fq_a_t));
          constraint.setRightHandSide(0.0);
          problem.addLinearConstraint(constraint);
          
          constraint = new LinearConstraintGreaterEq(getConstraintGEReactiveName(edge, LineFlowVariableFactory.PHASE_A));
          constraint.addVariable(fq_a, 1.0);
          constraint.addVariable(z_s, Math.min(capacity,fq_a_t));
          constraint.setRightHandSide(0.0);
          problem.addLinearConstraint(constraint);
        }

        
        if (edge.getAttribute(ElectricPowerFlowConnection.HAS_PHASE_B_KEY, Boolean.class)) {
          LinearConstraint constraint = new LinearConstraintLessEq(getConstraintLERealName(edge, LineFlowVariableFactory.PHASE_B));
          constraint.addVariable(fp_b, 1.0);
          constraint.addVariable(z_s, Math.max(-capacity,-fp_b_t));
          constraint.setRightHandSide(0.0);
          problem.addLinearConstraint(constraint);
          
          constraint = new LinearConstraintGreaterEq(getConstraintGERealName(edge, LineFlowVariableFactory.PHASE_B));
          constraint.addVariable(fp_b, 1.0);
          constraint.addVariable(z_s, Math.min(capacity,fp_b_t));
          constraint.setRightHandSide(0.0);
          problem.addLinearConstraint(constraint);
                    
          constraint = new LinearConstraintLessEq(getConstraintLEReactiveName(edge, LineFlowVariableFactory.PHASE_B));
          constraint.addVariable(fq_b, 1.0);
          constraint.addVariable(z_s, Math.max(-capacity,-fq_b_t));
          constraint.setRightHandSide(0.0);
          problem.addLinearConstraint(constraint);
          
          constraint = new LinearConstraintGreaterEq(getConstraintGEReactiveName(edge, LineFlowVariableFactory.PHASE_B));
          constraint.addVariable(fq_b, 1.0);
          constraint.addVariable(z_s, Math.min(capacity,fq_b_t));
          constraint.setRightHandSide(0.0);
          problem.addLinearConstraint(constraint);
        }
        
        if (edge.getAttribute(ElectricPowerFlowConnection.HAS_PHASE_C_KEY, Boolean.class)) {
          LinearConstraint constraint = new LinearConstraintLessEq(getConstraintLERealName(edge, LineFlowVariableFactory.PHASE_C));
          constraint.addVariable(fp_c, 1.0);
          constraint.addVariable(z_s, Math.max(-capacity,-fp_c_t));
          constraint.setRightHandSide(0.0);
          problem.addLinearConstraint(constraint);
          
          constraint = new LinearConstraintGreaterEq(getConstraintGERealName(edge, LineFlowVariableFactory.PHASE_C));
          constraint.addVariable(fp_c, 1.0);
          constraint.addVariable(z_s, Math.min(capacity,fp_c_t));
          constraint.setRightHandSide(0.0);
          problem.addLinearConstraint(constraint);
          
          
          constraint = new LinearConstraintLessEq(getConstraintLEReactiveName(edge, LineFlowVariableFactory.PHASE_C));
          constraint.addVariable(fq_c, 1.0);
          constraint.addVariable(z_s, Math.max(-capacity,-fq_c_t));
          constraint.setRightHandSide(0.0);
          problem.addLinearConstraint(constraint);
          
          constraint = new LinearConstraintGreaterEq(getConstraintGEReactiveName(edge, LineFlowVariableFactory.PHASE_C));
          constraint.addVariable(fq_c, 1.0);
          constraint.addVariable(z_s, Math.min(capacity,fq_c_t));
          constraint.setRightHandSide(0.0);
          problem.addLinearConstraint(constraint);
        }
      }
    }        
  }
  

}
