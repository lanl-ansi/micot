package gov.lanl.micot.application.rdt.algorithm.ep.bp.objective.dual;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.optimize.ObjectiveFunctionFactory;
import gov.lanl.micot.infrastructure.model.Scenario;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import gov.lanl.micot.application.rdt.algorithm.ep.bp.variable.dual.SigmaVariableFactory;
import gov.lanl.micot.util.math.solver.Solution;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgramObjective;

/**
 * General class the dual objective function on the sigma variables
 * @author Russell Bent
 */
public class SigmaObjectiveFunctionFactory implements ObjectiveFunctionFactory {

  private Collection<Scenario> scenarios = null;
  private HashMap<Scenario, ArrayList<Solution>> columns = null;
  
  /**
   * Constructor
   * 
   * @param scenarios
   */
  public SigmaObjectiveFunctionFactory(Collection<Scenario> scenarios, HashMap<Scenario, ArrayList<Solution>> columns) {
    this.scenarios = scenarios;
    this.columns = columns;
  }
    
  @Override
  public void addCoefficients(MathematicalProgram program, ElectricPowerModel model) throws NoVariableException {
    SigmaVariableFactory variableFactory = new SigmaVariableFactory(scenarios, columns);
    MathematicalProgramObjective objective = program.getLinearObjective();
  
    for (Scenario scenario : scenarios) {
      if (variableFactory.hasVariable(scenario)) {
        Variable variable = variableFactory.getVariable(program, scenario);      
        objective.addVariable(variable, 1);
      }
    }          
  }


  
}
