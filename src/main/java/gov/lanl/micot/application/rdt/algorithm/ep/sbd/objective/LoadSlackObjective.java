package gov.lanl.micot.application.rdt.algorithm.ep.sbd.objective;

import gov.lanl.micot.application.rdt.algorithm.ep.sbd.variable.LoadSlackVariableFactory;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.optimize.ObjectiveFunctionFactory;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgramObjective;

/**
 * General class for tracking load served up to the maximum amount
 * that needs to be served
 * 
 * @author Russell Bent
 */
public class LoadSlackObjective implements ObjectiveFunctionFactory {

  private Scenario scenario = null;
  
  /**
   * Constructor
   * 
   * @param scenario
   */
  public LoadSlackObjective(Scenario scenario) {
    this.scenario = scenario;
  }

  @Override
  public void addCoefficients(MathematicalProgram program, ElectricPowerModel model) throws NoVariableException {
    LoadSlackVariableFactory slack = new LoadSlackVariableFactory(scenario);
    MathematicalProgramObjective objective = program.getLinearObjective();
    objective.addVariable(slack.getVariable(program), 1.0);
  }


}
