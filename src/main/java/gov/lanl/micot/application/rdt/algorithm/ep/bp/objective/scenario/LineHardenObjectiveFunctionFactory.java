package gov.lanl.micot.application.rdt.algorithm.ep.bp.objective.scenario;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.optimize.ObjectiveFunctionFactory;
import gov.lanl.micot.infrastructure.model.Scenario;

import java.util.ArrayList;

import gov.lanl.micot.application.rdt.algorithm.ep.bp.variable.dual.YLineHardenVariableFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.variable.scenario.LineHardenVariableFactory;
import gov.lanl.micot.util.math.solver.Solution;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgramObjective;


/**
 * General class for creating line hardening objective coefficients
 * @author Russell Bent
 */
public class LineHardenObjectiveFunctionFactory implements ObjectiveFunctionFactory {
  
  private Scenario scenario = null;
  private Solution dualSolution = null;
  private MathematicalProgram dualProgram = null;
  
  public LineHardenObjectiveFunctionFactory(Scenario scenario, Solution dualSolution, MathematicalProgram dualProgram) {
    this.scenario = scenario;
    this.dualSolution = dualSolution;
    this.dualProgram = dualProgram;
  }
  
  @Override
  public void addCoefficients(MathematicalProgram program,  ElectricPowerModel model) throws NoVariableException {
    ArrayList<Scenario> scenarios = new ArrayList<Scenario>();
    scenarios.add(scenario);    
    
    LineHardenVariableFactory lineVariableFactory = new LineHardenVariableFactory(scenario);
    YLineHardenVariableFactory yVariableFactory = new YLineHardenVariableFactory(scenarios);
    
    MathematicalProgramObjective objective = program.getLinearObjective();
  
    for (ElectricPowerFlowConnection edge : model.getFlowConnections()) {
      if (yVariableFactory.hasVariable(edge)) {
        Variable variable = lineVariableFactory.getVariable(program, edge, scenario);
        Variable y = yVariableFactory.getVariable(dualProgram, edge, scenario);
        System.out.println(edge);
        double cost = dualSolution.getValueDouble(y);
        objective.addVariable(variable, cost);        
      }      
    }    
  }


  
}
