package gov.lanl.micot.application.rdt.algorithm.ep.bp.objective.scenario;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.optimize.ObjectiveFunctionFactory;
import gov.lanl.micot.infrastructure.model.Scenario;

import java.util.ArrayList;

import gov.lanl.micot.application.rdt.algorithm.ep.bp.variable.dual.YLineSwitchVariableFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.bp.variable.scenario.LineSwitchVariableFactory;
import gov.lanl.micot.util.math.solver.Solution;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgramObjective;

/**
 * General class for creating line switch construction objective coefficients
 * @author Russell Bent
 */
public class LineSwitchObjectiveFunctionFactory implements ObjectiveFunctionFactory {

  private Scenario scenario = null;
  private Solution dualSolution = null;
  private MathematicalProgram dualProgram = null;
  
  public LineSwitchObjectiveFunctionFactory(Scenario scenario, Solution dualSolution, MathematicalProgram dualProgram) {
    this.scenario = scenario;
    this.dualSolution = dualSolution;
    this.dualProgram = dualProgram;
  }
  
  @Override
  public void addCoefficients(MathematicalProgram program, ElectricPowerModel model) throws NoVariableException {
    ArrayList<Scenario> scenarios = new ArrayList<Scenario>();
    scenarios.add(scenario);    
   
    LineSwitchVariableFactory lineVariableFactory = new LineSwitchVariableFactory(scenario);
    YLineSwitchVariableFactory yVariableFactory = new YLineSwitchVariableFactory(scenarios);
    
    MathematicalProgramObjective objective = program.getLinearObjective();
  
    for (ElectricPowerFlowConnection edge : model.getFlowConnections()) {
      if (yVariableFactory.hasVariable(edge)) {
        Variable variable = lineVariableFactory.getVariable(program, edge, scenario);
        Variable y = yVariableFactory.getVariable(dualProgram, edge, scenario);
        double cost = dualSolution.getValueDouble(y);
        objective.addVariable(variable, cost);
      }      
    }    
  }


  
}
