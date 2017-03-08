package gov.lanl.micot.infrastructure.ep.optimize.dcopf.constraint.scenario;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.ep.model.Load;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.variable.LoadVariableFactory;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.util.math.solver.LinearConstraint;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

import java.util.Collection;

/**
 * The flow balance constraint
 * 
 * @author Russell Bent
 */
public class VariableLoadFlowBalanceScenarioConstraint extends FlowBalanceScenarioConstraint {

  /**
   * Constraint
   */
  public VariableLoadFlowBalanceScenarioConstraint(Collection<Scenario> scenarios) {
    super(scenarios);
  }

  @Override
  protected void addLoadToConstraint(MathematicalProgram program, LinearConstraint constraint, ElectricPowerNode node, double mva) throws NoVariableException {
    LoadVariableFactory loadVariableFactory = new LoadVariableFactory();    
    for (Load load : node.getComponents(Load.class)) {
      constraint.addVariable(loadVariableFactory.getVariable(program,load), -1.0);
    }   
  }
  
  
}
