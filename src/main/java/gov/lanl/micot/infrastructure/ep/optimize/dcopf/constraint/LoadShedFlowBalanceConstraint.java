package gov.lanl.micot.infrastructure.ep.optimize.dcopf.constraint;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerProducer;
import gov.lanl.micot.infrastructure.ep.model.Load;
import gov.lanl.micot.infrastructure.ep.optimize.ConstraintFactory;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.variable.FlowVariableFactory;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.variable.GeneratorVariableFactory;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.variable.LoadShedVariableFactory;
import gov.lanl.micot.util.math.solver.LinearConstraint;
import gov.lanl.micot.util.math.solver.LinearConstraintEquals;
import gov.lanl.micot.util.math.solver.exception.InvalidConstraintException;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

import java.util.Collection;

/**
 * The flow shed flow balance constraint for interdictions ... 
 * 
 * @author Russell Bent
 */
public class LoadShedFlowBalanceConstraint implements ConstraintFactory {

  /**
   * Constraint
   */
  public LoadShedFlowBalanceConstraint() {
  }

  /**
   * Function for create the variable name associated with a load
   * 
   * @param node
   * @return
   */
  public static String getFlowBalanceConstraintName(ElectricPowerNode node) {
    return "FB" + node.toString();
  }

  
  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException,
      NoVariableException, InvalidConstraintException {
    GeneratorVariableFactory generatorVariableFactory = new GeneratorVariableFactory();
    LoadShedVariableFactory loadShedVariableFactory = new LoadShedVariableFactory();
    FlowVariableFactory flowVariableFactory = new FlowVariableFactory();
    double mva = model.getMVABase();
        
    for (ElectricPowerNode node : model.getNodes()) {
      LinearConstraint constraint = new LinearConstraintEquals(getFlowBalanceConstraintName(node));
      constraint.setRightHandSide(0);
      for (ElectricPowerProducer producer : node.getComponents(ElectricPowerProducer.class)) {
        constraint.addVariable(generatorVariableFactory.getVariable(problem, producer), 1.0);
      }
      for (Load load : node.getComponents(Load.class)) {
        constraint.addVariable(loadShedVariableFactory.getVariable(problem, load), 1.0);
        constraint.setRightHandSide(constraint.getRightHandSide() + load.getDesiredRealLoad().doubleValue() / mva);
      }      
      problem.addLinearConstraint(constraint);
      
      for (ElectricPowerFlowConnection edge : model.getFlowEdges(node)) {
        ElectricPowerNode node1 = model.getFirstNode(edge);
        
        if (node1.equals(node)) {
          constraint.addVariable(flowVariableFactory.getVariable(problem, edge), -1);
        }
        else {
          constraint.addVariable(flowVariableFactory.getVariable(problem, edge), 1);
        }        
      }      
    }
  }

  /**
   * Get the flow balance constraint
   * 
   * @param node
   * @return
   */
  public LinearConstraint getFlowBalanceConstraint(MathematicalProgram problem, ElectricPowerNode node) {
    return problem.getLinearConstraint(getFlowBalanceConstraintName(node));
  }

}
