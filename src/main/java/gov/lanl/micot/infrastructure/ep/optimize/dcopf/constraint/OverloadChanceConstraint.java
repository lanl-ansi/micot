package gov.lanl.micot.infrastructure.ep.optimize.dcopf.constraint;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.ep.model.Load;
import gov.lanl.micot.infrastructure.ep.optimize.ConstraintFactory;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.variable.scenario.LinkCapacityOverloadScenarioVariableFactory;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.variable.scenario.PhaseAngleScenarioVariableFactory;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.util.math.solver.LinearConstraint;
import gov.lanl.micot.util.math.solver.LinearConstraintGreaterEq;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.InvalidConstraintException;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

import java.util.ArrayList;
import java.util.Collection;

/**
 * This constraint adds overload chance constraints to the OPF
 * @author Russell Bent
 */
public class OverloadChanceConstraint implements ConstraintFactory {

  private Collection<Scenario> scenarios = new ArrayList<Scenario>();
  private double epsilon = 0;
  
  /**
   * Constraint
   */
  public OverloadChanceConstraint(Collection<Scenario> scenarios, double epsilon) {    
    this.scenarios = scenarios;
    this.epsilon = epsilon;
  }
  
  /**
   * Get the name for the first overload constraint
   * 
   * @param k scenario number
   * @param edge
   * @return
   */
  private static String getFirstOverloadConstraintName(int k, ElectricPowerFlowConnection edge) {
    return "FO" + k + "." + edge.toString();
  }
  
  /**
   * Get the name for the second overload constraint
   * 
   * @param k scenario number
   * @param edge
   * @return
   */
  private static String getSecondOverloadConstraintName(int k, ElectricPowerFlowConnection edge) {
    return "SO" + k + "." + edge.toString();
  }

  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException, InvalidConstraintException {   
    LinkCapacityOverloadScenarioVariableFactory factory = new LinkCapacityOverloadScenarioVariableFactory(scenarios, epsilon);  
    double mva = model.getMVABase();
       
    for (Scenario scenario : scenarios) {
      if (scenario.getWeight() > epsilon) {
        continue;
      }
      
      for (ElectricPowerFlowConnection connection : model.getFlowConnections()) {        
        boolean status = scenario.computeActualStatus(connection, true);
        if (status) {
          Variable overloadVariable =  factory.getVariable(problem, connection, scenario); 
          constructConstraint(problem, overloadVariable, scenario, connection, mva, model);          
        }
      }
    }
  }

  /**
   * Construct the constraint
   * @param problem
   * @param overloadVariable
   * @param scenario
   * @param edge
   * @param mva
   * @throws NoVariableException
   * @throws InvalidConstraintException 
   */
  public void constructConstraint(MathematicalProgram problem, Variable overloadVariable, Scenario scenario, ElectricPowerFlowConnection edge, double mva, ElectricPowerModel model) throws NoVariableException, InvalidConstraintException {
    PhaseAngleScenarioVariableFactory pfactory = new PhaseAngleScenarioVariableFactory(scenarios);
    int k = scenario.getIndex();
    Collection<ElectricPowerNode> ns = model.getNodes();
    
    ElectricPowerNode fNode = model.getFirstNode(edge);
    ElectricPowerNode sNode = model.getSecondNode(edge);
    Variable phase1 = pfactory.getVariable(problem, fNode, scenario);
    Variable phase2 = pfactory.getVariable(problem, sNode, scenario);
    
    double susceptance = edge.getSusceptance();
    double capacity    = edge.getCapacityRating() / mva;
    
    LinearConstraint firstOverloadConstraint = new LinearConstraintGreaterEq(getFirstOverloadConstraintName(k, edge));
    LinearConstraint secondOverloadConstraint = new LinearConstraintGreaterEq(getSecondOverloadConstraintName(k, edge));
    
    double totalLoad = 0.0;
    for (ElectricPowerNode node : ns) {
      for (Load load : node.getComponents(Load.class)) {
        double demand = load.getDesiredRealLoad().doubleValue();
        if (scenario.hasModification(load, Load.DESIRED_REAL_LOAD_KEY)) {
          demand = scenario.getModification(load, Load.DESIRED_REAL_LOAD_KEY, Number.class).doubleValue();
        }        
        totalLoad += demand;
      }
      
//      if (node.getLoad() != null) {
  //      totalLoad += node.getLoad().getDesiredRealLoad().doubleValue();
   //   }
    }
    totalLoad = totalLoad / mva;
        
    firstOverloadConstraint.addVariable(phase1, -susceptance);
    firstOverloadConstraint.addVariable(phase2, +susceptance);
    firstOverloadConstraint.setRightHandSide(-capacity);
    
    secondOverloadConstraint.addVariable(phase1, +susceptance);
    secondOverloadConstraint.addVariable(phase2, -susceptance);
    secondOverloadConstraint.setRightHandSide(-capacity);
    
    firstOverloadConstraint.addVariable(overloadVariable, totalLoad);
    secondOverloadConstraint.addVariable(overloadVariable, totalLoad);
    
    problem.addLinearConstraint(firstOverloadConstraint);
    problem.addLinearConstraint(secondOverloadConstraint);
  }
  
 
}
