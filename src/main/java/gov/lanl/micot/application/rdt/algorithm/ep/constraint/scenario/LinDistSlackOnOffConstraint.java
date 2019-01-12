package gov.lanl.micot.application.rdt.algorithm.ep.constraint.scenario;

import gov.lanl.micot.infrastructure.ep.model.Bus;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.optimize.ConstraintFactory;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.application.rdt.algorithm.ep.variable.scenario.LinDistSlackVariableFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.variable.scenario.LineActiveVariableFactory;
import gov.lanl.micot.util.math.solver.LinearConstraint;
import gov.lanl.micot.util.math.solver.LinearConstraintGreaterEq;
import gov.lanl.micot.util.math.solver.LinearConstraintLessEq;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.InvalidConstraintException;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

/**
 * This constraint turns off the slack in the LinDist Flow Equations
 * 
 * @author Russell Bent
 */
public class LinDistSlackOnOffConstraint implements ConstraintFactory {

  private Scenario scenario = null;
  
  /**
   * Constraint
   */
  public LinDistSlackOnOffConstraint(Scenario scenario) {
    this.scenario = scenario;
  }

  /**
   * Get constraint name for the greater than or equal to constraint
   * @param edge
   * @param phase
   * @param scenario
   * @return
   */
  private String getConstraintNameGE(ElectricPowerFlowConnection connection, String phase) {
    return "slack_on_off_ge-" + connection.toString() + "-" + phase + "." + scenario.getIndex();
  }
  
  /**
   * Get the constraint name for the less than or equal to constraint
   * @param connection
   * @param phase
   * @param scenario
   * @return
   */
  private String getConstraintNameLE(ElectricPowerFlowConnection connection, String phase) {
    return "slack_on_off_le-" + connection.toString() + "-" + phase + "." + scenario.getIndex();
  }
  
  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException, InvalidConstraintException {
    LinDistSlackVariableFactory slackFactory = new LinDistSlackVariableFactory(scenario);
    LineActiveVariableFactory activeFactory = new LineActiveVariableFactory(scenario,null);
    
      for (ElectricPowerFlowConnection edge : model.getFlowConnections()) {
        if (slackFactory.hasVariable(edge)) {        
          Bus bus1 = model.getFirstNode(edge).getBus();
          Bus bus2 = model.getSecondNode(edge).getBus();

          double vmin = Math.min(bus1.getMinimumVoltagePU(), bus2.getMinimumVoltagePU());
          double vmax = Math.max(bus1.getMaximumVoltagePU(), bus2.getMaximumVoltagePU());
          double maxdiff = ((vmax*vmax) - (vmin*vmin));
          Variable z_s = activeFactory.getVariable(problem, edge);
        
          if (edge.getAttribute(ElectricPowerFlowConnection.HAS_PHASE_A_KEY, Boolean.class)) {
            Variable sa_s = slackFactory.getVariable(problem, edge, LinDistSlackVariableFactory.PHASE_A);
          
            LinearConstraint leconstraint = new LinearConstraintLessEq(getConstraintNameLE(edge, LinDistSlackVariableFactory.PHASE_A));
            leconstraint.addVariable(sa_s, 1.0);
            leconstraint.addVariable(z_s, maxdiff);
            leconstraint.setRightHandSide(maxdiff);
            problem.addLinearConstraint(leconstraint);

            LinearConstraint geconstraint = new LinearConstraintGreaterEq(getConstraintNameGE(edge, LinDistSlackVariableFactory.PHASE_A));
            geconstraint.addVariable(sa_s, 1.0);
            geconstraint.addVariable(z_s, -maxdiff);
            geconstraint.setRightHandSide(-maxdiff);
            problem.addLinearConstraint(geconstraint);
          }
        
          if (edge.getAttribute(ElectricPowerFlowConnection.HAS_PHASE_B_KEY, Boolean.class)) {
            Variable sb_s = slackFactory.getVariable(problem, edge, LinDistSlackVariableFactory.PHASE_B);
            LinearConstraint leconstraint = new LinearConstraintLessEq(getConstraintNameLE(edge, LinDistSlackVariableFactory.PHASE_B));
            leconstraint.addVariable(sb_s, 1.0);
            leconstraint.addVariable(z_s, maxdiff);
            leconstraint.setRightHandSide(maxdiff);
            problem.addLinearConstraint(leconstraint);

            LinearConstraint geconstraint = new LinearConstraintGreaterEq(getConstraintNameGE(edge, LinDistSlackVariableFactory.PHASE_B));
            geconstraint.addVariable(sb_s, 1.0);
            geconstraint.addVariable(z_s, -maxdiff);
            geconstraint.setRightHandSide(-maxdiff);
            problem.addLinearConstraint(geconstraint);
          }
                
          if (edge.getAttribute(ElectricPowerFlowConnection.HAS_PHASE_C_KEY, Boolean.class)) {
            Variable sc_s = slackFactory.getVariable(problem, edge, LinDistSlackVariableFactory.PHASE_C);
          
            LinearConstraint leconstraint = new LinearConstraintLessEq(getConstraintNameLE(edge,LinDistSlackVariableFactory.PHASE_C));
            leconstraint.addVariable(sc_s, 1.0);
            leconstraint.addVariable(z_s, maxdiff);
            leconstraint.setRightHandSide(maxdiff);
            problem.addLinearConstraint(leconstraint);

            LinearConstraint geconstraint = new LinearConstraintGreaterEq(getConstraintNameGE(edge,LinDistSlackVariableFactory.PHASE_C));
            geconstraint.addVariable(sc_s, 1.0);
            geconstraint.addVariable(z_s, -maxdiff);
            geconstraint.setRightHandSide(-maxdiff);
            problem.addLinearConstraint(geconstraint);
          }
        }    
      }
    }  
}
