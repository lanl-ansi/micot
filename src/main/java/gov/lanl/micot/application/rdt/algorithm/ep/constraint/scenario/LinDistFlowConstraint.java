package gov.lanl.micot.application.rdt.algorithm.ep.constraint.scenario;

import gov.lanl.micot.infrastructure.ep.model.Bus;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.optimize.ConstraintFactory;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.application.rdt.algorithm.ep.variable.scenario.LinDistSlackVariableFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.variable.scenario.LineFlowVariableFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.variable.scenario.VoltageVariableFactory;
import gov.lanl.micot.util.math.solver.LinearConstraintEquals;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.InvalidConstraintException;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

/**
 * This constraint models the LinDistFlow Constraints with on/off variables
 * 
 * @author Russell Bent
 */
public class LinDistFlowConstraint implements ConstraintFactory {
  
  // 120 degree rotation, i.e. e^{i*2*pi/3}
  private static double R_120 = -0.5;
  private static double X_120 = 0.866025404;
  
  // -120 degree rotation, i.e. e^{-i*2*pi/3}
  private static double R_240 = -0.5;
  private static double X_240 = -0.866025404;
  
  private Scenario scenario = null;
  
  /**
   * Constraint
   */
  public LinDistFlowConstraint(Scenario scenario) {
    this.scenario = scenario;
  }

  /**
   * Get constraint name for the greater than or equal to constraint
   * @param edge
   * @param phase
   * @param scenario
   * @return
   */
  private String getConstraintName(ElectricPowerFlowConnection connection, String phase) {
    return "lindist-" + connection.toString() + "-" + phase + "." + scenario.getIndex();
  }
    
  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException, InvalidConstraintException {
    LinDistSlackVariableFactory slackFactory = new LinDistSlackVariableFactory(scenario);
    VoltageVariableFactory voltageFactory = new VoltageVariableFactory(scenario);
    LineFlowVariableFactory flowFactory = new LineFlowVariableFactory(scenario);
       
    for (ElectricPowerFlowConnection edge : model.getFlowConnections()) {
      boolean hasVariable = flowFactory.hasVariable(edge);
      if (!hasVariable) {
        continue;
      }
                     
      Bus bus1 = model.getFirstNode(edge).getBus();
      Bus bus2 = model.getSecondNode(edge).getBus();
      
      Variable s_a = slackFactory.getVariable(problem, edge, LinDistSlackVariableFactory.PHASE_A);      
      Variable s_b = slackFactory.getVariable(problem, edge, LinDistSlackVariableFactory.PHASE_B);      
      Variable s_c = slackFactory.getVariable(problem, edge, LinDistSlackVariableFactory.PHASE_C);      
      Variable v1_a = voltageFactory.getVariable(problem, bus1, VoltageVariableFactory.PHASE_A);
      Variable v1_b = voltageFactory.getVariable(problem, bus1, VoltageVariableFactory.PHASE_B);
      Variable v1_c = voltageFactory.getVariable(problem, bus1, VoltageVariableFactory.PHASE_C);
      Variable v2_a = voltageFactory.getVariable(problem, bus2, VoltageVariableFactory.PHASE_A);
      Variable v2_b = voltageFactory.getVariable(problem, bus2, VoltageVariableFactory.PHASE_B);
      Variable v2_c = voltageFactory.getVariable(problem, bus2, VoltageVariableFactory.PHASE_C);
      Variable fp_a = flowFactory.getRealVariable(problem, edge, LineFlowVariableFactory.PHASE_A);
      Variable fp_b = flowFactory.getRealVariable(problem, edge, LineFlowVariableFactory.PHASE_B);
      Variable fp_c = flowFactory.getRealVariable(problem, edge, LineFlowVariableFactory.PHASE_C);
      Variable fq_a = flowFactory.getReactiveVariable(problem, edge, LineFlowVariableFactory.PHASE_A);
      Variable fq_b = flowFactory.getReactiveVariable(problem, edge, LineFlowVariableFactory.PHASE_B);
      Variable fq_c = flowFactory.getReactiveVariable(problem, edge, LineFlowVariableFactory.PHASE_C);
        
      double r_aa = edge.getAttribute(ElectricPowerFlowConnection.RESISTANCE_PHASE_A_KEY,  Double.class);
      double r_ab = edge.getAttribute(ElectricPowerFlowConnection.RESISTANCE_PHASE_AB_KEY, Double.class);
      double r_ac = edge.getAttribute(ElectricPowerFlowConnection.RESISTANCE_PHASE_AC_KEY, Double.class);
      double r_ba = edge.getAttribute(ElectricPowerFlowConnection.RESISTANCE_PHASE_BA_KEY, Double.class);
      double r_bb = edge.getAttribute(ElectricPowerFlowConnection.RESISTANCE_PHASE_B_KEY,  Double.class);
      double r_bc = edge.getAttribute(ElectricPowerFlowConnection.RESISTANCE_PHASE_BC_KEY, Double.class);
      double r_ca = edge.getAttribute(ElectricPowerFlowConnection.RESISTANCE_PHASE_CA_KEY, Double.class);
      double r_cb = edge.getAttribute(ElectricPowerFlowConnection.RESISTANCE_PHASE_CB_KEY, Double.class);
      double r_cc = edge.getAttribute(ElectricPowerFlowConnection.RESISTANCE_PHASE_C_KEY,  Double.class);
        
      double x_aa = edge.getAttribute(ElectricPowerFlowConnection.REACTANCE_PHASE_A_KEY,  Double.class);
      double x_ab = edge.getAttribute(ElectricPowerFlowConnection.REACTANCE_PHASE_AB_KEY, Double.class);
      double x_ac = edge.getAttribute(ElectricPowerFlowConnection.REACTANCE_PHASE_AC_KEY, Double.class);
      double x_ba = edge.getAttribute(ElectricPowerFlowConnection.REACTANCE_PHASE_BA_KEY, Double.class);
      double x_bb = edge.getAttribute(ElectricPowerFlowConnection.REACTANCE_PHASE_B_KEY,  Double.class);
      double x_bc = edge.getAttribute(ElectricPowerFlowConnection.REACTANCE_PHASE_BC_KEY, Double.class);
      double x_ca = edge.getAttribute(ElectricPowerFlowConnection.REACTANCE_PHASE_CA_KEY, Double.class);
      double x_cb = edge.getAttribute(ElectricPowerFlowConnection.REACTANCE_PHASE_CB_KEY, Double.class);
      double x_cc = edge.getAttribute(ElectricPowerFlowConnection.REACTANCE_PHASE_C_KEY,  Double.class);

      // add in the PHASE A lindist flow equations
      if (edge.getAttribute(ElectricPowerFlowConnection.HAS_PHASE_A_KEY, Boolean.class)) {
        LinearConstraintEquals constraint = new LinearConstraintEquals(getConstraintName(edge, VoltageVariableFactory.PHASE_A));

        // voltage variables
        constraint.addVariable(v2_a, 1.0);
        constraint.addVariable(v1_a, -1.0);
        
        // on/off variable
        constraint.addVariable(s_a, 1.0);

        // phase A impedances
        constraint.addVariable(fp_a, 2.0 * r_aa);
        constraint.addVariable(fq_a, 2.0 * x_aa);

        // phase B impedances
        if (edge.getAttribute(ElectricPowerFlowConnection.HAS_PHASE_B_KEY, Boolean.class)) {
          constraint.addVariable(fp_b, 2.0 * r_ab * R_120);
          constraint.addVariable(fq_b, 2.0 * x_ab * X_120);
        }
       
        // phase C impedances
        if (edge.getAttribute(ElectricPowerFlowConnection.HAS_PHASE_C_KEY, Boolean.class)) {  
          constraint.addVariable(fp_c, 2.0 * r_ac * R_240);
          constraint.addVariable(fq_c, 2.0 * x_ac * X_240);
        }
          
        // right hand side
        constraint.setRightHandSide(0.0);
        problem.addLinearConstraint(constraint);          
      }
        
      // add in the PHASE B lindist flow equations
        if (edge.getAttribute(ElectricPowerFlowConnection.HAS_PHASE_B_KEY, Boolean.class)) {
          LinearConstraintEquals constraint = new LinearConstraintEquals(getConstraintName(edge, VoltageVariableFactory.PHASE_B));

          // voltage variables
          constraint.addVariable(v2_b, 1.0);
          constraint.addVariable(v1_b, -1.0);
          
          // on/off
          constraint.addVariable(s_b, 1.0);

          // phase A impedances
          if (edge.getAttribute(ElectricPowerFlowConnection.HAS_PHASE_A_KEY, Boolean.class)) {
            constraint.addVariable(fp_a, 2.0 * r_ba * R_240);
            constraint.addVariable(fq_a, 2.0 * x_ba * X_240);
          }
          
          // phase B impedances
          constraint.addVariable(fp_b, 2.0 * r_bb);
          constraint.addVariable(fq_b, 2.0 * x_bb);
       
          // phase C impedances
          if (edge.getAttribute(ElectricPowerFlowConnection.HAS_PHASE_C_KEY, Boolean.class)) {  
            constraint.addVariable(fp_c, 2.0 * r_bc * R_120);
            constraint.addVariable(fq_c, 2.0 * x_bc * X_120);
          }
          
          // right hand side
          constraint.setRightHandSide(0.0);
          problem.addLinearConstraint(constraint);          
        }

        // add in the PHASE C lindist flow equations
        if (edge.getAttribute(ElectricPowerFlowConnection.HAS_PHASE_C_KEY, Boolean.class)) {
          LinearConstraintEquals constraint = new LinearConstraintEquals(getConstraintName(edge, VoltageVariableFactory.PHASE_C));

          // voltage variables
          constraint.addVariable(v2_c, 1.0);
          constraint.addVariable(v1_c, -1.0);
          
          // on/off
          constraint.addVariable(s_c, 1.0);

          // phase A impedances
          if (edge.getAttribute(ElectricPowerFlowConnection.HAS_PHASE_A_KEY, Boolean.class)) {
            constraint.addVariable(fp_a, 2.0 * r_ca * R_120);
            constraint.addVariable(fq_a, 2.0 * x_ca * X_120);
          }
          
          // phase B impedances
          if (edge.getAttribute(ElectricPowerFlowConnection.HAS_PHASE_B_KEY, Boolean.class)) {
            constraint.addVariable(fp_b, 2.0 * r_cb * R_240);
            constraint.addVariable(fq_b, 2.0 * x_cb * X_240);
          }
          
          // phase C impedances
          constraint.addVariable(fp_c, 2.0 * r_cc);
          constraint.addVariable(fq_c, 2.0 * x_cc);
          
          // right hand side
          constraint.setRightHandSide(0.0);
          problem.addLinearConstraint(constraint);          
        }
      }        
  }  
}
