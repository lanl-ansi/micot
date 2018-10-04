package gov.lanl.micot.application.rdt.algorithm.ep.bp.constraint.dual;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.ep.model.Generator;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.optimize.mathprogram.constraint.ScenarioConstraintFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import gov.lanl.micot.application.rdt.algorithm.ep.bp.variable.dual.SigmaVariableFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.bp.variable.dual.YGeneratorVariableFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.bp.variable.dual.YLineConstructionVariableFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.bp.variable.dual.YLineHardenVariableFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.bp.variable.dual.YLineSwitchVariableFactory;
import gov.lanl.micot.util.math.solver.LinearConstraint;
import gov.lanl.micot.util.math.solver.LinearConstraintGreaterEq;
import gov.lanl.micot.util.math.solver.Solution;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.InvalidConstraintException;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;


/** 
 * Dual constraint on the lambda variables in the primal space
 * @author Russell Bent
 */
public class ColumnConstraint extends ScenarioConstraintFactory<ElectricPowerNode, ElectricPowerModel>  {

  HashMap<Scenario, ArrayList<Solution>> columns = null;
    
  /**
   * Constraint
   */
  public ColumnConstraint(Collection<Scenario> scenarios, HashMap<Scenario, ArrayList<Solution>> columns) {    
    super(scenarios);
    this.columns = columns;
  }
    
  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException, InvalidConstraintException {
    SigmaVariableFactory sigmaFactory = new SigmaVariableFactory(getScenarios(), columns);
    YGeneratorVariableFactory generatorFactory = new YGeneratorVariableFactory(getScenarios());
    YLineConstructionVariableFactory lineFactory = new YLineConstructionVariableFactory(getScenarios());
    YLineHardenVariableFactory hardenFactory = new YLineHardenVariableFactory(getScenarios());
    YLineSwitchVariableFactory switchFactory = new YLineSwitchVariableFactory(getScenarios());
    
    for (Scenario scenario : getScenarios()) { 
      Variable sigma = sigmaFactory.getVariable(problem, scenario);
      
      ArrayList<Solution> c = columns.get(scenario);
      for (int i = 0; i < c.size(); ++i) {
        LinearConstraint constraint = new LinearConstraintGreaterEq(getName(scenario, i));      
        constraint.setRightHandSide(0.0);        
        constraint.addVariable(sigma, 1.0);
        
        for (Generator producer : model.getGenerators()) {
          if (generatorFactory.hasVariable(producer)) {          
            Variable y = generatorFactory.getVariable(problem, producer, scenario);
            constraint.addVariable(y,c.get(i).getValueDouble(null));
          }
        }
        
        for (ElectricPowerFlowConnection edge : model.getFlowConnections()) {
          if (lineFactory.hasVariable(edge)) {          
            Variable y = lineFactory.getVariable(problem, edge, scenario);
            constraint.addVariable(y,c.get(i).getValueDouble(null));
          }      
          
          if (hardenFactory.hasVariable(edge)) {
            Variable y = hardenFactory.getVariable(problem, edge, scenario);
            constraint.addVariable(y,c.get(i).getValueDouble(null));
          }      

          if (switchFactory.hasVariable(edge)) {
            Variable y = switchFactory.getVariable(problem, edge, scenario);
            constraint.addVariable(y,c.get(i).getValueDouble(null));
          }      
        }        
        problem.addLinearConstraint(constraint);  
      }
    }
  
  }
  
  /**
   * Get the flow constraint name
   * 
   * @param edge
   * @return
   */
  private String getName(Scenario scenario, int i) {
    return "Column." + scenario.getIndex() + "-" + i;
  }

  
}
