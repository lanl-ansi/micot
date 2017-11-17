package gov.lanl.micot.infrastructure.ep.optimize.dcopf.constraint.scenario;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerProducer;
import gov.lanl.micot.infrastructure.ep.model.Load;
import gov.lanl.micot.infrastructure.ep.optimize.ConstraintFactory;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.variable.GeneratorVariableFactory;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.variable.scenario.PhaseAngleScenarioVariableFactory;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.util.math.solver.LinearConstraint;
import gov.lanl.micot.util.math.solver.LinearConstraintEquals;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.InvalidConstraintException;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

import java.util.ArrayList;
import java.util.Collection;

/**
 * This constraint adds chance constraints to the OPF
 * @author Russell Bent
 */
public class FlowBalanceScenarioConstraint implements ConstraintFactory {

  private Collection<Scenario> scenarios = new ArrayList<Scenario>();
  
  /**
   * Constraint
   */
  public FlowBalanceScenarioConstraint(Collection<Scenario> scenarios) {    
    this.scenarios = scenarios;
  }

  /**
   * Get the name for the phase angle constraint
   * 
   * @param k scenario number
   * @param node
   * @return
   */
  public static String getPhaseAngleConstraintName(int k, ElectricPowerNode node) {
    return "PC" + k + "." + node.toString();
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
      constraint.setRightHandSide(constraint.getRightHandSide() + load.getRealLoad().doubleValue() / mva);
    }
  }
  
  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException, InvalidConstraintException {   
    GeneratorVariableFactory generatorVariableFactory = new GeneratorVariableFactory();
    PhaseAngleScenarioVariableFactory phaseAngleFactory = new PhaseAngleScenarioVariableFactory(scenarios);
    double mva = model.getMVABase();
    
    for (Scenario scenario : scenarios) {
      int k = scenario.getIndex();
      
      // add the constraint
      for (ElectricPowerNode n : model.getNodes()) {
        LinearConstraint constraint = new LinearConstraintEquals(getPhaseAngleConstraintName(k, n));
        constraint.setRightHandSide(0);
        for (ElectricPowerProducer p : n.getComponents(ElectricPowerProducer.class)) {
          constraint.addVariable(generatorVariableFactory.getVariable(problem, p), 1.0);
        }
        problem.addLinearConstraint(constraint);
        addLoadToConstraint(problem, constraint, n, mva);
      }
            
      // the diagonal of the phase angle admittance matrix
      for (ElectricPowerNode node : model.getNodes()) {
        double value = 0;
        for (ElectricPowerFlowConnection connection : model.getFlowEdges(node)) {
          boolean status = scenario.computeActualStatus(connection, true);
         
          if (!status) {
            continue;
          }
          
          double susceptance = connection.getSusceptance();
          value -= susceptance;
        }
        LinearConstraint constraint = problem.getLinearConstraint(getPhaseAngleConstraintName(k, node));
        constraint.addVariable(phaseAngleFactory.getVariable(problem, node, scenario), value);
      }
      
      
      // create the non zero entries of the phase angle matrix
      for (ElectricPowerFlowConnection connection : model.getFlowConnections()) {
        boolean status = scenario.computeActualStatus(connection, true);
        if (!status) {
          continue;
        }

        ElectricPowerNode fNode = model.getFirstNode(connection);
        ElectricPowerNode sNode = model.getSecondNode(connection);

        double susceptance = connection.getSusceptance();

        LinearConstraint constraint = problem.getLinearConstraint(getPhaseAngleConstraintName(k, fNode));
        Variable variable = phaseAngleFactory.getVariable(problem, sNode, scenario);
        double value = constraint.getCoefficient(variable) + susceptance;
        constraint.addVariable(variable, value);

        constraint = problem.getLinearConstraint(getPhaseAngleConstraintName(k, sNode));
        variable = phaseAngleFactory.getVariable(problem, fNode, scenario);
        constraint.addVariable(variable, value);
      }
      

    }
  }

 
}
