package gov.lanl.micot.infrastructure.ep.optimize.dcopf.constraint;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.ep.model.Load;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.constraint.FlowBalanceConstraint;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.variable.LoadVariableFactory;
import gov.lanl.micot.util.math.solver.LinearConstraint;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;


/**
 * The flow balance constraint
 * 
 * @author Russell Bent
 */
public class VariableLoadFlowBalanceConstraint extends FlowBalanceConstraint {

  /**
   * Constraint
   */
  public VariableLoadFlowBalanceConstraint() {
    super();
  }

  @Override
  protected void addLoadToConstraint(MathematicalProgram program, LinearConstraint constraint, ElectricPowerNode node, double mva) throws NoVariableException {
    LoadVariableFactory loadVariableFactory = new LoadVariableFactory();    
    for (Load load : node.getComponents(Load.class)) {
      constraint.addVariable(loadVariableFactory.getVariable(program,load), 1.0);
    }   
  }
  
  
}
