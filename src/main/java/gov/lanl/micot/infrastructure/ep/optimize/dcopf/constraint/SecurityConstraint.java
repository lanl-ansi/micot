package gov.lanl.micot.infrastructure.ep.optimize.dcopf.constraint;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerProducer;
import gov.lanl.micot.infrastructure.ep.model.Load;
import gov.lanl.micot.infrastructure.ep.optimize.ConstraintFactory;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.variable.GeneratorVariableFactory;
import gov.lanl.micot.util.math.solver.LinearConstraint;
import gov.lanl.micot.util.math.solver.LinearConstraintEquals;
import gov.lanl.micot.util.math.solver.LinearConstraintGreaterEq;
import gov.lanl.micot.util.math.solver.LinearConstraintLessEq;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.InvalidConstraintException;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgramObjective;

/**
 * This constraint adds n-1 security constraints to the opf
 * @author Russell Bent
 */
public class SecurityConstraint implements ConstraintFactory {

  /**
   * Constraint
   */
  public SecurityConstraint() {    
  }
  
  /**
   * Function for create the variable name associated with a load
   * 
   * @param node
   * @return
   */
  private String getPhaseAngleVariableName(ElectricPowerNode node, ElectricPowerFlowConnection contingency) {
    return "P" + node.toString() + "-" + contingency.toString();
  }
  
  /**
   * Get the slack bus phase angle name
   * 
   * @return
   */
  private String getSlackBusPhaseAngleConstraintName(ElectricPowerFlowConnection contingency) {
    return "SlackBus-" + contingency.toString();
  }
  
  /**
   * Function for create the variable name associated with a load
   * 
   * @param node
   * @return
   */
  private String getPhaseAngleConstraintName(ElectricPowerNode node, ElectricPowerFlowConnection contingency) {
    return "PC" + node.toString() + "-" + contingency.toString();
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
      constraint.setRightHandSide(constraint.getRightHandSide() - load.getRealLoad().doubleValue() / mva);
    }
  }

  /**
   * Get the flow constraint name
   * 
   * @param edge
   * @return
   */
  private String getFlowLessThanConstraintName(ElectricPowerFlowConnection edge, ElectricPowerFlowConnection contingency) {
    return "\"FlowL" + edge.toString() + "\"" + "-" + contingency;
  }

  /**
   * Get the flow constraint name
   * 
   * @param edge
   * @return
   */
  private String getFlowGreaterThanConstraintName(ElectricPowerFlowConnection edge, ElectricPowerFlowConnection contingency) {
    return "\"FlowG" + edge.toString() + "\""+ "-" + contingency;
  }

  
  @Override
  public void constructConstraint(MathematicalProgram problem, /*Collection<ElectricPowerNode> nodes,*/ ElectricPowerModel model) throws VariableExistsException, NoVariableException, InvalidConstraintException {
    MathematicalProgramObjective objective = problem.getLinearObjective();
    double mva = model.getMVABase();
    GeneratorVariableFactory generatorVariableFactory = new GeneratorVariableFactory();
    
    // create the n-1 constraints for links in the model
    for (ElectricPowerFlowConnection contingency : model.getFlowConnections()) {
      if (contingency.getStatus() == false) {
        continue;
      }

      ElectricPowerNode node1 = model.getFirstNode(contingency);
      ElectricPowerNode node2 = model.getSecondNode(contingency);

//      if (!nodes.contains(node1) || !nodes.contains(node2)) {
  //      continue;
   //   }
      
      // link is the component that will "fail"

      // create the phase angle variables for this contingency
      for (ElectricPowerNode node : model.getNodes()) {
        Variable variable = problem.makeContinuousVariable(getPhaseAngleVariableName(node,contingency));
        objective.addVariable(variable, 0.0);
      }

      // create the phase angle constraints (first node has an angle of 0, flow balance constraints)
      boolean setFirstNodeAngle = false;
      for (ElectricPowerNode node : model.getNodes()) {
         // make the first phase angle = 0
        if (!setFirstNodeAngle) {
          LinearConstraint constraint = new LinearConstraintEquals(getSlackBusPhaseAngleConstraintName(contingency));
          constraint.addVariable(problem.getVariable(getPhaseAngleVariableName(node,contingency)),1.0);
          constraint.setRightHandSide(0.0);       
          setFirstNodeAngle = true;
          problem.addLinearConstraint(constraint);
        }
        
        // set up the flow balance constraints
        LinearConstraint constraint = new LinearConstraintEquals(getPhaseAngleConstraintName(node,contingency));
        constraint.setRightHandSide(0);
        for (ElectricPowerProducer producer : node.getComponents(ElectricPowerProducer.class)) {
          constraint.addVariable(generatorVariableFactory.getVariable(problem, producer), -1.0);
        }
        addLoadToConstraint(problem, constraint, node, mva);
        problem.addLinearConstraint(constraint);
      }
      
      // setup the diagonal of the phase angle admittance matrix for this contingency
      for (ElectricPowerNode node : model.getNodes()) {
        double value = 0;
        for (ElectricPowerFlowConnection link : model.getFlowEdges(node)) {
          if (link.getStatus() == false || link.equals(contingency)) {
            continue;
          }

          double impedance = link.getSusceptance();
          value += impedance;
        }
        LinearConstraint constraint = problem.getLinearConstraint(getPhaseAngleConstraintName(node,contingency));
        constraint.addVariable(problem.getVariable(getPhaseAngleVariableName(node,contingency)), value);
      }
      
      // create the non zero entries of the admittance matrix for this contingency
      for (ElectricPowerFlowConnection link : model.getFlowConnections()) {
        if (link.getStatus() == false || link.equals(contingency)) {
          continue;
        }

        ElectricPowerNode firstNode = model.getFirstNode(link);
        ElectricPowerNode secondNode = model.getSecondNode(link);

       // if (!nodes.contains(firstNode) || !nodes.contains(secondNode)) {
        //  continue;
       // }

        double impedance = link.getSusceptance();

        LinearConstraint constraint = problem.getLinearConstraint(getPhaseAngleConstraintName(firstNode, contingency));
        Variable variable = problem.getVariable(getPhaseAngleVariableName(secondNode,contingency));
        double value = constraint.getCoefficient(variable) - impedance;
        constraint.addVariable(variable, value);

        constraint = problem.getLinearConstraint(getPhaseAngleConstraintName(secondNode,contingency));
        variable = problem.getVariable(getPhaseAngleVariableName(firstNode,contingency));
        constraint.addVariable(variable, value);
      }
      
      // create the flow capacity constraints
      for (ElectricPowerFlowConnection link : model.getFlowConnections()) {
        if (link.getStatus() == false || link.equals(contingency)) {
          continue;
        }

        ElectricPowerNode firstNode = model.getFirstNode(link);
        ElectricPowerNode secondNode = model.getSecondNode(link);

 //       if (!nodes.contains(firstNode) || !nodes.contains(secondNode)) {
   //       continue;
    //    }

        double impedance = link.getSusceptance();

        LinearConstraint constraint = new LinearConstraintLessEq(getFlowLessThanConstraintName(link,contingency));
        constraint.setRightHandSide(link.getCapacityRating() / mva);
        constraint.addVariable(problem.getVariable(getPhaseAngleVariableName(firstNode,contingency)), impedance);
        constraint.addVariable(problem.getVariable(getPhaseAngleVariableName(secondNode,contingency)), -impedance);
        problem.addLinearConstraint(constraint);

        constraint = new LinearConstraintGreaterEq(getFlowGreaterThanConstraintName(link,contingency));
        constraint.setRightHandSide(-link.getCapacityRating() / mva);
        constraint.addVariable(problem.getVariable(getPhaseAngleVariableName(firstNode,contingency)), impedance);
        constraint.addVariable(problem.getVariable(getPhaseAngleVariableName(secondNode,contingency)), -impedance);
        problem.addLinearConstraint(constraint);
      }
      
    } 
    
  }

}
