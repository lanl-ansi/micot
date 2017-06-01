package gov.lanl.micot.application.rdt.algorithm.ep.mip.constraint.scenario;

import gov.lanl.micot.infrastructure.ep.model.Bus;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.optimize.mathprogram.constraint.ScenarioConstraintFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.variable.scenario.ScenarioLineUseVariableFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.variable.scenario.ScenarioVariableFactoryUtility;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.variable.scenario.ScenarioVoltageOnOffVariableFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.variable.scenario.ScenarioVoltageVariableFactory;
import gov.lanl.micot.util.math.solver.LinearConstraint;
import gov.lanl.micot.util.math.solver.LinearConstraintGreaterEq;
import gov.lanl.micot.util.math.solver.LinearConstraintLessEq;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.InvalidConstraintException;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

import java.util.Collection;

/**
 * This constraint turns off the voltages in the LinDist Flow Equations
 * 
 * @author Russell Bent
 */
public class ScenarioVoltageOnOffConstraint extends ScenarioConstraintFactory<ElectricPowerNode, ElectricPowerModel> {

  /**
   * Constraint
   */
  public ScenarioVoltageOnOffConstraint(Collection<Scenario> scenarios) {
    super(scenarios);
  }

  /**
   * Get constraint name for the greater than or equal to constraint
   * @param edge
   * @param phase
   * @param scenario
   * @return
   */
  private String getConstraintNameGE(ElectricPowerFlowConnection connection, String phase, Scenario scenario) {
    return "voltage_on_off_ge-" + connection.toString() + "-" + phase + "." + scenario.getIndex();
  }
  
  /**
   * Get the constraint name for the less than or equal to constraint
   * @param connection
   * @param phase
   * @param scenario
   * @return
   */
  private String getConstraintNameLE(ElectricPowerFlowConnection connection, String phase, Scenario scenario) {
    return "voltage_on_off_le-" + connection.toString() + "-" + phase + "." + scenario.getIndex();
  }
  
  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException, InvalidConstraintException {
    ScenarioVoltageOnOffVariableFactory voltageFactory = new ScenarioVoltageOnOffVariableFactory(getScenarios());
    ScenarioLineUseVariableFactory useFactory = new ScenarioLineUseVariableFactory(getScenarios());
    
    for (Scenario scenario : getScenarios()) {
      for (ElectricPowerFlowConnection connection : model.getFlowConnections()) {

        if (!ScenarioVariableFactoryUtility.doCreateLineUseScenarioVariable(connection, scenario)) {
          continue;
        }
                
        Bus bus1 = model.getFirstNode(connection).getBus();
        Bus bus2 = model.getSecondNode(connection).getBus();

        double vmin = Math.min(bus1.getMinimumVoltagePU(), bus2.getMinimumVoltagePU());
        double vmax = Math.max(bus1.getMaximumVoltagePU(), bus2.getMaximumVoltagePU());
        double maxdiff = ((vmax*vmax) - (vmin*vmin));
        Variable lineUse = useFactory.getVariable(problem, connection, scenario);
        

        if (connection.getAttribute(ElectricPowerFlowConnection.HAS_PHASE_A_KEY, Boolean.class)) {
          Variable variableA = voltageFactory.getVariable(problem, connection, ScenarioVoltageVariableFactory.PHASE_A, scenario);
          
          LinearConstraint leconstraint = new LinearConstraintLessEq(getConstraintNameLE(connection,ScenarioVoltageVariableFactory.PHASE_A, scenario));
          leconstraint.addVariable(variableA, 1.0);
          leconstraint.addVariable(lineUse, maxdiff);
          leconstraint.setRightHandSide(maxdiff);
          problem.addLinearConstraint(leconstraint);

          LinearConstraint geconstraint = new LinearConstraintGreaterEq(getConstraintNameGE(connection,ScenarioVoltageVariableFactory.PHASE_A, scenario));
          geconstraint.addVariable(variableA, 1.0);
          geconstraint.addVariable(lineUse, -maxdiff);
          geconstraint.setRightHandSide(-maxdiff);
          problem.addLinearConstraint(geconstraint);
        }
        
        if (connection.getAttribute(ElectricPowerFlowConnection.HAS_PHASE_B_KEY, Boolean.class)) {
          Variable variableB = voltageFactory.getVariable(problem, connection, ScenarioVoltageVariableFactory.PHASE_B, scenario);
          LinearConstraint leconstraint = new LinearConstraintLessEq(getConstraintNameLE(connection,ScenarioVoltageVariableFactory.PHASE_B, scenario));
          leconstraint.addVariable(variableB, 1.0);
          leconstraint.addVariable(lineUse, maxdiff);
          leconstraint.setRightHandSide(maxdiff);
          problem.addLinearConstraint(leconstraint);

          LinearConstraint geconstraint = new LinearConstraintGreaterEq(getConstraintNameGE(connection,ScenarioVoltageVariableFactory.PHASE_B, scenario));
          geconstraint.addVariable(variableB, 1.0);
          geconstraint.addVariable(lineUse, -maxdiff);
          geconstraint.setRightHandSide(-maxdiff);
          problem.addLinearConstraint(geconstraint);
        }
                
        if (connection.getAttribute(ElectricPowerFlowConnection.HAS_PHASE_C_KEY, Boolean.class)) {
          Variable variableC = voltageFactory.getVariable(problem, connection, ScenarioVoltageVariableFactory.PHASE_C, scenario);
          
          LinearConstraint leconstraint = new LinearConstraintLessEq(getConstraintNameLE(connection,ScenarioVoltageVariableFactory.PHASE_C, scenario));
          leconstraint.addVariable(variableC, 1.0);
          leconstraint.addVariable(lineUse, maxdiff);
          leconstraint.setRightHandSide(maxdiff);
          problem.addLinearConstraint(leconstraint);

          LinearConstraint geconstraint = new LinearConstraintGreaterEq(getConstraintNameGE(connection,ScenarioVoltageVariableFactory.PHASE_C, scenario));
          geconstraint.addVariable(variableC, 1.0);
          geconstraint.addVariable(lineUse, -maxdiff);
          geconstraint.setRightHandSide(-maxdiff);
          problem.addLinearConstraint(geconstraint);
        }
      }    
    }
  }  
  
}
