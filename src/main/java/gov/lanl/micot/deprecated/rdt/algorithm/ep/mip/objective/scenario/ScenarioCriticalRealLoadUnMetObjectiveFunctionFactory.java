package gov.lanl.micot.deprecated.rdt.algorithm.ep.mip.objective.scenario;

import java.util.Collection;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.optimize.mathprogram.objective.ScenarioObjectiveFunctionFactory;
import gov.lanl.micot.deprecated.rdt.algorithm.ep.mip.variable.scenario.ScenarioCriticalRealLoadUnMetVariableFactory;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgramObjective;


/**
 * Adds the scenario real load un met variables to the objective function
 * @author Russell Bent
 */
public class ScenarioCriticalRealLoadUnMetObjectiveFunctionFactory extends ScenarioObjectiveFunctionFactory<ElectricPowerNode, ElectricPowerModel> {

  public ScenarioCriticalRealLoadUnMetObjectiveFunctionFactory(Collection<Scenario> scenarios) {
    super(scenarios);
  }
  
  @Override
  public void addCoefficients(MathematicalProgram program,  ElectricPowerModel model) throws NoVariableException {
    ScenarioCriticalRealLoadUnMetVariableFactory variableFactory = new ScenarioCriticalRealLoadUnMetVariableFactory(getScenarios());
    MathematicalProgramObjective objective = program.getLinearObjective();

    for (Scenario scenario : getScenarios()) {
      objective.addVariable(variableFactory.getVariable(program, scenario), -1.0);
    }    
  }
  
}
