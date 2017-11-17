package gov.lanl.micot.infrastructure.ep.optimize.dcopf.constraint;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerProducer;
import gov.lanl.micot.infrastructure.ep.model.Load;
import gov.lanl.micot.infrastructure.ep.optimize.ConstraintFactory;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.variable.GeneratorVariableFactory;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.variable.LoadShedVariableFactory;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.variable.PhaseAngleVariableFactory;
import gov.lanl.micot.util.math.solver.LinearConstraint;
import gov.lanl.micot.util.math.solver.LinearConstraintEquals;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.InvalidConstraintException;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

/**
 * The flow balance constraint
 * 
 * @author Russell Bent
 */
public class FlowBalanceConstraint implements ConstraintFactory {

  /**
   * Constraint
   */
  public FlowBalanceConstraint() {
  }

  /**
   * Function for create the variable name associated with a load
   * 
   * @param node
   * @return
   */
  public static String getPhaseAngleConstraintName(ElectricPowerNode node) {
    return "PC" + node.toString();
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
    for (Load load : node.getComponents(Load.class)) {
      constraint.setRightHandSide(constraint.getRightHandSide() - load.getRealLoadMax() / mva);
    }
    
    // if load shed variables exist, add them here.
    LoadShedVariableFactory loadVariableFactory = new LoadShedVariableFactory();    
    for (Load load : node.getComponents(Load.class)) {
      Variable variable = loadVariableFactory.getVariable(program,load);
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
    PhaseAngleVariableFactory phaseAngleVariableFactory = new PhaseAngleVariableFactory();

    for (ElectricPowerNode node : model.getNodes()) {
      LinearConstraint constraint = new LinearConstraintEquals(getPhaseAngleConstraintName(node));
      constraint.setRightHandSide(0);
      for (ElectricPowerProducer producer : node.getComponents(ElectricPowerProducer.class)) {
        constraint.addVariable(generatorVariableFactory.getVariable(problem, producer), -1.0);
      }
      problem.addLinearConstraint(constraint);
      addLoadToConstraint(problem, constraint, node, mva);
    }

    // the diagonal of the phase angle admittance matrix
    for (ElectricPowerNode node : model.getNodes()) {
      double value = 0;
      for (ElectricPowerFlowConnection link : model.getFlowEdges(node)) {
        if (link.getStatus() == false) {
          continue;
        }

        double impedance = link.getSusceptance();
        value += impedance;
      }
      LinearConstraint constraint = problem.getLinearConstraint(getPhaseAngleConstraintName(node));
      constraint.addVariable(phaseAngleVariableFactory.getVariable(problem, node), value);
    }

    // create the non zero entries of the phase angle matrix
    for (ElectricPowerFlowConnection link : model.getFlowConnections()) {
      if (link.getStatus() == false) {
        continue;
      }

      ElectricPowerNode firstNode = model.getFirstNode(link);
      ElectricPowerNode secondNode = model.getSecondNode(link);

//      if (!nodes.contains(firstNode) || !nodes.contains(secondNode)) {
  //      continue;
   //   }

      double impedance = link.getSusceptance();

      LinearConstraint constraint = problem.getLinearConstraint(getPhaseAngleConstraintName(firstNode));
      Variable variable = phaseAngleVariableFactory.getVariable(problem, secondNode);
      double value = constraint.getCoefficient(variable) - impedance;
      constraint.addVariable(variable, value);

      constraint = problem.getLinearConstraint(getPhaseAngleConstraintName(secondNode));
      variable = phaseAngleVariableFactory.getVariable(problem, firstNode);
      constraint.addVariable(variable, value);
    }
  }

  /**
   * Get the flow balance constraint
   * 
   * @param node
   * @return
   */
  public LinearConstraint getFlowBalanceConstraint(MathematicalProgram problem, ElectricPowerNode node) {
    return problem.getLinearConstraint(getPhaseAngleConstraintName(node));
  }

}
