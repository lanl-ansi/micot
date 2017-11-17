package gov.lanl.micot.infrastructure.ep.optimize.dcopf.constraint;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerProducer;
import gov.lanl.micot.infrastructure.ep.model.Load;
import gov.lanl.micot.infrastructure.ep.optimize.ConstraintFactory;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.variable.GeneratorVariableFactory;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.variable.LoadShedVariableFactory;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.variable.LoadVariableFactory;
import gov.lanl.micot.util.math.solver.LinearConstraint;
import gov.lanl.micot.util.math.solver.LinearConstraintEquals;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.InvalidConstraintException;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

/**
 * The flow balance constraint across all nodes
 * 
 * @author Russell Bent
 */
public class BalanceConstraint implements ConstraintFactory {

  /**
   * Constraint
   */
  public BalanceConstraint() {
  }

  /**
   * Function for create the variable name associated with a load
   * 
   * @param node
   * @return
   */
  public static String getConstraintName() {
    return "BalanceConstraint";
  }

  /**
   * Function for adding the load to a constraint
   * @param program
   * @param constraint
   * @param node
   * @param mva
   * @throws NoVariableException 
   */
  protected void addLoadToConstraint(MathematicalProgram program, LinearConstraint constraint, ElectricPowerNode node, double mva) throws NoVariableException {
    LoadShedVariableFactory loadShedVariableFactory = new LoadShedVariableFactory();    
    LoadVariableFactory loadVariableFactory = new LoadVariableFactory();
        
    for (Load load : node.getComponents(Load.class)) {
      Variable variable = loadShedVariableFactory.getVariable(program,load);
      if (variable != null) {
        constraint.setRightHandSide(constraint.getRightHandSide() + load.getRealLoadMax() / mva);
      }
    }
    
    // if load sheds exist, add them here   
    for (Load load : node.getComponents(Load.class)) {
      Variable variable = loadShedVariableFactory.getVariable(program,load);
      if (variable != null) {
        constraint.addVariable(variable, 1.0);
      }
      variable = loadVariableFactory.getVariable(program,load);
      if (variable != null) {
        constraint.addVariable(variable, -1.0);
      }
    }          
  }
    
  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException,
      NoVariableException, InvalidConstraintException {
    double mva = model.getMVABase();
    GeneratorVariableFactory generatorVariableFactory = new GeneratorVariableFactory();
    LinearConstraint constraint = new LinearConstraintEquals(getConstraintName());
    constraint.setRightHandSide(0);
    
    for (ElectricPowerNode node : model.getNodes()) {     
      for (ElectricPowerProducer producer : node.getComponents(ElectricPowerProducer.class)) {
        constraint.addVariable(generatorVariableFactory.getVariable(problem, producer), 1.0);
      }
      addLoadToConstraint(problem, constraint, node, mva);
    }    
    problem.addLinearConstraint(constraint);
  }

  /**
   * Get the flow balance constraint
   * 
   * @param node
   * @return
   */
  public LinearConstraint getFlowBalanceConstraint(MathematicalProgram problem, ElectricPowerNode node) {
    return problem.getLinearConstraint(getConstraintName());
  }
}
