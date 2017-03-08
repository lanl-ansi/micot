package gov.lanl.micot.infrastructure.ep.optimize.dcopf.constraint;

import gov.lanl.micot.infrastructure.ep.model.Battery;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.ep.model.Generator;
import gov.lanl.micot.infrastructure.ep.model.Load;
import gov.lanl.micot.infrastructure.ep.optimize.ConstraintFactory;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.variable.BatteryTimeVariableFactory;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.variable.GeneratorTimeVariableFactory;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.variable.LoadTimeVariableFactory;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.variable.PhaseAngleTimeVariableFactory;
import gov.lanl.micot.util.math.solver.LinearConstraint;
import gov.lanl.micot.util.math.solver.LinearConstraintEquals;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.InvalidConstraintException;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

/**
 * This constraint for balancing flows across time
 * @author Russell Bent
 */
public class FlowBalanceTimeConstraint implements ConstraintFactory {

  private int numberOfIncrements = -1;
  private double incrementSize = -1;
    
  /**
   * Constructor
   * @param numberOfIncrements
   * @param incrementSize
   */
  public FlowBalanceTimeConstraint(int numberOfIncrements, double incrementSize) {
    super();
    this.numberOfIncrements = numberOfIncrements;
    this.incrementSize = incrementSize;
  }
  
  /**
   * Function for create the variable name associated with a load
   * @param node
   * @return
   */
  private String getPhaseAngleConstraintName(ElectricPowerNode node, double time) {
    return "PC" + node.toString()+"-"+time;
  }

  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException, InvalidConstraintException {
    BatteryTimeVariableFactory batteryVariableFactory = new BatteryTimeVariableFactory(numberOfIncrements, incrementSize);
    GeneratorTimeVariableFactory generatorVariableFactory = new GeneratorTimeVariableFactory(numberOfIncrements, incrementSize);
    LoadTimeVariableFactory loadVariableFactory = new LoadTimeVariableFactory(numberOfIncrements, incrementSize);
    PhaseAngleTimeVariableFactory phaseAngleVariableFactory = new PhaseAngleTimeVariableFactory(numberOfIncrements, incrementSize);    
    
    // set the phase angle constraints
    for (ElectricPowerNode node : model.getNodes()) {
      for (int i = 0; i < numberOfIncrements; ++i) {
        double time = i * incrementSize;
        LinearConstraint constraint = new LinearConstraintEquals(getPhaseAngleConstraintName(node,time));
        constraint.setRightHandSide(0);
        for (Generator gen : node.getComponents(Generator.class)) {
          constraint.addVariable(generatorVariableFactory.getVariable(problem, gen, time), -1.0);
        }
        for (Battery battery : node.getComponents(Battery.class)) {
          constraint.addVariable(batteryVariableFactory.getVariable(problem, battery, time), -1.0);
        }       
        for (Load load : node.getComponents(Load.class)) {
          constraint.addVariable(loadVariableFactory.getVariable(problem, load, time), 1.0);
        }
        problem.addLinearConstraint(constraint);
      }
    }    
    
    
    for (ElectricPowerNode node : model.getNodes()) {
      for (int i = 0; i < numberOfIncrements; ++i) {
        double time = i * incrementSize;
        double value = 0;
        for (ElectricPowerFlowConnection link : model.getFlowEdges(node)) {
          if (link.getActualStatus() == false) {
            continue;
          }

         double impedance = link.getSusceptance();
         value += impedance;
        }
        LinearConstraint constraint = problem.getLinearConstraint(getPhaseAngleConstraintName(node, time));
        constraint.addVariable(phaseAngleVariableFactory.getVariable(problem, node, time), value);
      }
    }
        
    // create the non zero entries of the phase angle matrix
    for (ElectricPowerFlowConnection link : model.getFlowConnections()) {
      if (link.getActualStatus() == false) {
        continue;
      }

      for (int i = 0; i < numberOfIncrements; ++i) {
        double time = i * incrementSize;
      
        ElectricPowerNode firstNode = model.getFirstNode(link);
        ElectricPowerNode secondNode = model.getSecondNode(link);

//        if (!nodes.contains(firstNode) || !nodes.contains(secondNode)) {
  //        continue;
   //     }

        double impedance = link.getSusceptance();
      
        LinearConstraint constraint = problem.getLinearConstraint(getPhaseAngleConstraintName(firstNode,time));
        Variable variable =phaseAngleVariableFactory.getVariable(problem, secondNode, time);
        double value = constraint.getCoefficient(variable) - impedance;      
        constraint.addVariable(variable, value);

        constraint = problem.getLinearConstraint(getPhaseAngleConstraintName(secondNode,time));
        variable = phaseAngleVariableFactory.getVariable(problem, firstNode, time);
        constraint.addVariable(variable, value);
      }
    }
    
  }

  
}
