package gov.lanl.micot.application.rdt.algorithm.ep.bp.constraint.dual;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.optimize.mathprogram.constraint.ScenarioConstraintFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import gov.lanl.micot.application.rdt.algorithm.ep.bp.variable.dual.SigmaVariableFactory;
import gov.lanl.micot.util.math.solver.Solution;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

/** 
 * Bounds on the sigma variable
 * @author Russell Bent
 */
public class SigmaBoundConstraint extends ScenarioConstraintFactory<ElectricPowerNode, ElectricPowerModel> {
  
  private HashMap<Scenario, ArrayList<Solution>> columns = null;
  
  /**
   * Constructor
   */
  public SigmaBoundConstraint(Collection<Scenario> scenarios, HashMap<Scenario, ArrayList<Solution>> columns) {
    super(scenarios);
    this.columns = columns;
  }
    
  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException {
   
    // nothing to do. Sigma is unbounded
    //SigmaVariableFactory variableFactory = new SigmaVariableFactory(getScenarios(), columns);
    
//    for (Scenario scenario : getScenarios()) {
  //    if (columns.get(scenario).size() <= 0) {
    //    continue;
     // }
     // Variable variable = variableFactory.getVariable(problem, scenario);
      //problem.addBounds(variable, -Double.MAX_VALUE, Double.MAX_VALUE);
   // }
  }
  
  
}
