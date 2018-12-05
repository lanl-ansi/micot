package gov.lanl.micot.deprecated.rdt.algorithm.ep.mip.constraint.scenario;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.optimize.mathprogram.constraint.ScenarioConstraintFactory;
import gov.lanl.micot.deprecated.rdt.algorithm.ep.mip.variable.scenario.ScenarioReactiveFlowPhaseVariableFactory;
import gov.lanl.micot.deprecated.rdt.algorithm.ep.mip.variable.scenario.ScenarioRealFlowPhaseVariableFactory;
import gov.lanl.micot.deprecated.rdt.algorithm.ep.mip.variable.scenario.ScenarioVariableFactoryUtility;
import gov.lanl.micot.deprecated.rdt.algorithm.ep.mip.variable.scenario.ScenarioVoltageOnOffVariableFactory;
import gov.lanl.micot.deprecated.rdt.algorithm.ep.mip.variable.scenario.ScenarioVoltageVariableFactory;
import gov.lanl.micot.util.math.solver.LinearConstraintEquals;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.InvalidConstraintException;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

import java.util.Collection;

/**
 * This constraint models the LinDistFlow Constraints with on/off variables
 * 
 * @author Russell Bent
 */
public class ScenarioLinDistFlowConstraint extends ScenarioConstraintFactory<ElectricPowerNode, ElectricPowerModel> {
  
  // 120 degree rotation, i.e. e^{i*2*pi/3}
  private static double R_120 = -0.5;
  private static double X_120 = 0.866025404;
  
  // -120 degree rotation, i.e. e^{-i*2*pi/3}
  private static double R_240 = -0.5;
  private static double X_240 = -0.866025404;

  
  /**
   * Constraint
   */
  public ScenarioLinDistFlowConstraint(Collection<Scenario> scenarios) {
    super(scenarios);
  }

  /**
   * Get constraint name for the greater than or equal to constraint
   * @param edge
   * @param phase
   * @param scenario
   * @return
   */
  private String getConstraintName(ElectricPowerFlowConnection connection, String phase, Scenario scenario) {
    return "lindist-" + connection.toString() + "-" + phase + "." + scenario.getIndex();
  }
    
  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException, InvalidConstraintException {
    ScenarioVoltageOnOffVariableFactory onOffFactory = new ScenarioVoltageOnOffVariableFactory(getScenarios());
    ScenarioVoltageVariableFactory voltageFactory = new ScenarioVoltageVariableFactory(getScenarios());
    ScenarioRealFlowPhaseVariableFactory realFactory = new ScenarioRealFlowPhaseVariableFactory(getScenarios());
    ScenarioReactiveFlowPhaseVariableFactory reactiveFactory = new ScenarioReactiveFlowPhaseVariableFactory(getScenarios());
   
    for (Scenario scenario : getScenarios()) {
      for (ElectricPowerFlowConnection connection : model.getFlowConnections()) {
        boolean hasLineUseVariable = ScenarioVariableFactoryUtility.doCreateLineUseScenarioVariable(connection, scenario);
        Integer lineUseConstant = ScenarioVariableFactoryUtility.getLineUseScenarioConstant(connection, scenario);
        
        // the line is not being used, so don't even create the constraint
        if (!hasLineUseVariable && lineUseConstant == 0) {
          continue;
        }
                
        ElectricPowerNode bus1 = model.getFirstNode(connection);
        ElectricPowerNode bus2 = model.getSecondNode(connection);
        
        Variable onOffA = onOffFactory.getVariable(problem, connection, ScenarioVoltageOnOffVariableFactory.PHASE_A, scenario);
        Variable onOffB = onOffFactory.getVariable(problem, connection, ScenarioVoltageOnOffVariableFactory.PHASE_B, scenario);
        Variable onOffC = onOffFactory.getVariable(problem, connection, ScenarioVoltageOnOffVariableFactory.PHASE_C, scenario);
        Variable bus1vA = voltageFactory.getVariable(problem, bus1, ScenarioVoltageVariableFactory.PHASE_A, scenario);
        Variable bus1vB = voltageFactory.getVariable(problem, bus1, ScenarioVoltageVariableFactory.PHASE_B, scenario);
        Variable bus1vC = voltageFactory.getVariable(problem, bus1, ScenarioVoltageVariableFactory.PHASE_C, scenario);
        Variable bus2vA = voltageFactory.getVariable(problem, bus2, ScenarioVoltageVariableFactory.PHASE_A, scenario);
        Variable bus2vB = voltageFactory.getVariable(problem, bus2, ScenarioVoltageVariableFactory.PHASE_B, scenario);
        Variable bus2vC = voltageFactory.getVariable(problem, bus2, ScenarioVoltageVariableFactory.PHASE_C, scenario);
        Variable realA = realFactory.getVariable(problem, connection, ScenarioRealFlowPhaseVariableFactory.PHASE_A, scenario);
        Variable realB = realFactory.getVariable(problem, connection, ScenarioRealFlowPhaseVariableFactory.PHASE_B, scenario);
        Variable realC = realFactory.getVariable(problem, connection, ScenarioRealFlowPhaseVariableFactory.PHASE_C, scenario);
        Variable reactiveA = reactiveFactory.getVariable(problem, connection, ScenarioReactiveFlowPhaseVariableFactory.PHASE_A, scenario);
        Variable reactiveB = reactiveFactory.getVariable(problem, connection, ScenarioReactiveFlowPhaseVariableFactory.PHASE_B, scenario);
        Variable reactiveC = reactiveFactory.getVariable(problem, connection, ScenarioReactiveFlowPhaseVariableFactory.PHASE_C, scenario);
        
        double r_aa = connection.getAttribute(ElectricPowerFlowConnection.RESISTANCE_PHASE_A_KEY,  Double.class);
        double r_ab = connection.getAttribute(ElectricPowerFlowConnection.RESISTANCE_PHASE_AB_KEY, Double.class);
        double r_ac = connection.getAttribute(ElectricPowerFlowConnection.RESISTANCE_PHASE_AC_KEY, Double.class);
        double r_ba = connection.getAttribute(ElectricPowerFlowConnection.RESISTANCE_PHASE_BA_KEY, Double.class);
        double r_bb = connection.getAttribute(ElectricPowerFlowConnection.RESISTANCE_PHASE_B_KEY,  Double.class);
        double r_bc = connection.getAttribute(ElectricPowerFlowConnection.RESISTANCE_PHASE_BC_KEY, Double.class);
        double r_ca = connection.getAttribute(ElectricPowerFlowConnection.RESISTANCE_PHASE_CA_KEY, Double.class);
        double r_cb = connection.getAttribute(ElectricPowerFlowConnection.RESISTANCE_PHASE_CB_KEY, Double.class);
        double r_cc = connection.getAttribute(ElectricPowerFlowConnection.RESISTANCE_PHASE_C_KEY,  Double.class);
        
        double x_aa = connection.getAttribute(ElectricPowerFlowConnection.REACTANCE_PHASE_A_KEY,  Double.class);
        double x_ab = connection.getAttribute(ElectricPowerFlowConnection.REACTANCE_PHASE_AB_KEY, Double.class);
        double x_ac = connection.getAttribute(ElectricPowerFlowConnection.REACTANCE_PHASE_AC_KEY, Double.class);
        double x_ba = connection.getAttribute(ElectricPowerFlowConnection.REACTANCE_PHASE_BA_KEY, Double.class);
        double x_bb = connection.getAttribute(ElectricPowerFlowConnection.REACTANCE_PHASE_B_KEY,  Double.class);
        double x_bc = connection.getAttribute(ElectricPowerFlowConnection.REACTANCE_PHASE_BC_KEY, Double.class);
        double x_ca = connection.getAttribute(ElectricPowerFlowConnection.REACTANCE_PHASE_CA_KEY, Double.class);
        double x_cb = connection.getAttribute(ElectricPowerFlowConnection.REACTANCE_PHASE_CB_KEY, Double.class);
        double x_cc = connection.getAttribute(ElectricPowerFlowConnection.REACTANCE_PHASE_C_KEY,  Double.class);

        // add in the PHASE A lindist flow equations
        if (connection.getAttribute(ElectricPowerFlowConnection.HAS_PHASE_A_KEY, Boolean.class)) {
          LinearConstraintEquals constraint = new LinearConstraintEquals(getConstraintName(connection, ScenarioVoltageVariableFactory.PHASE_A , scenario));

          // voltage variables
          constraint.addVariable(bus2vA, 1.0);
          constraint.addVariable(bus1vA, -1.0);
          
          // on/off
          if (hasLineUseVariable) {
            constraint.addVariable(onOffA, 1.0);
          }

          // phase A impedances
          constraint.addVariable(realA, 2.0 * r_aa);
          constraint.addVariable(reactiveA, 2.0 * x_aa);

          // phase B impedances
          if (connection.getAttribute(ElectricPowerFlowConnection.HAS_PHASE_B_KEY, Boolean.class)) {
            constraint.addVariable(realB, 2.0 * r_ab * R_120);
            constraint.addVariable(reactiveB, 2.0 * x_ab * X_120);
          }
       
          // phase C impedances
          if (connection.getAttribute(ElectricPowerFlowConnection.HAS_PHASE_C_KEY, Boolean.class)) {  
            constraint.addVariable(realC, 2.0 * r_ac * R_240);
            constraint.addVariable(reactiveC, 2.0 * x_ac * X_240);
          }
          
          // right hand side
          constraint.setRightHandSide(0.0);
          problem.addLinearConstraint(constraint);          
        }
        

        // add in the PHASE B lindist flow equations
        if (connection.getAttribute(ElectricPowerFlowConnection.HAS_PHASE_B_KEY, Boolean.class)) {
          LinearConstraintEquals constraint = new LinearConstraintEquals(getConstraintName(connection, ScenarioVoltageVariableFactory.PHASE_B , scenario));

          // voltage variables
          constraint.addVariable(bus2vB, 1.0);
          constraint.addVariable(bus1vB, -1.0);
          
          // on/off
          if (hasLineUseVariable) {
            constraint.addVariable(onOffB, 1.0);
          }

          // phase A impedances
          if (connection.getAttribute(ElectricPowerFlowConnection.HAS_PHASE_A_KEY, Boolean.class)) {
            constraint.addVariable(realA, 2.0 * r_ba * R_240);
            constraint.addVariable(reactiveA, 2.0 * x_ba * X_240);
          }
          
          // phase B impedances
          constraint.addVariable(realB, 2.0 * r_bb);
          constraint.addVariable(reactiveB, 2.0 * x_bb);
       
          // phase C impedances
          if (connection.getAttribute(ElectricPowerFlowConnection.HAS_PHASE_C_KEY, Boolean.class)) {  
            constraint.addVariable(realC, 2.0 * r_bc * R_120);
            constraint.addVariable(reactiveC, 2.0 * x_bc * X_120);
          }
          
          // right hand side
          constraint.setRightHandSide(0.0);
          problem.addLinearConstraint(constraint);          
        }

        
        // add in the PHASE C lindist flow equations
        if (connection.getAttribute(ElectricPowerFlowConnection.HAS_PHASE_C_KEY, Boolean.class)) {
          LinearConstraintEquals constraint = new LinearConstraintEquals(getConstraintName(connection, ScenarioVoltageVariableFactory.PHASE_C , scenario));

          // voltage variables
          constraint.addVariable(bus2vC, 1.0);
          constraint.addVariable(bus1vC, -1.0);
          
          // on/off
          if (hasLineUseVariable) {
            constraint.addVariable(onOffC, 1.0);
          }

          // phase A impedances
          if (connection.getAttribute(ElectricPowerFlowConnection.HAS_PHASE_A_KEY, Boolean.class)) {
            constraint.addVariable(realA, 2.0 * r_ca * R_120);
            constraint.addVariable(reactiveA, 2.0 * x_ca * X_120);
          }
          
          // phase B impedances
          if (connection.getAttribute(ElectricPowerFlowConnection.HAS_PHASE_B_KEY, Boolean.class)) {
            constraint.addVariable(realB, 2.0 * r_cb * R_240);
            constraint.addVariable(reactiveB, 2.0 * x_cb * X_240);
          }
          
          // phase C impedances
          constraint.addVariable(realC, 2.0 * r_cc);
          constraint.addVariable(reactiveC, 2.0 * x_cc);
          
          // right hand side
          constraint.setRightHandSide(0.0);
          problem.addLinearConstraint(constraint);          
        }
      }        
    }
  }  
}
