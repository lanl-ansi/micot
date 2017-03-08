package gov.lanl.micot.infrastructure.naturalgas.optimize.ogf.constraint.scenario;

import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasConnection;
import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasModel;
import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasNode;
import gov.lanl.micot.infrastructure.naturalgas.optimize.ConstraintFactory;
import gov.lanl.micot.infrastructure.naturalgas.optimize.ogf.variable.scenario.FlowDirectionScenarioVariableFactory;
import gov.lanl.micot.util.math.solver.LinearConstraintGreaterEq;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.InvalidConstraintException;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A cut that indicates that all nodes that consume flow must have at least one incoming flow direction variable 
 *  
 * @author Russell Bent
 */
public class SinkFlowDirScenarioConstraint implements ConstraintFactory {

  private Collection<Scenario> scenarios = new ArrayList<Scenario>();

  /**
   * Constructor
   * @param scenarios
   */
  public SinkFlowDirScenarioConstraint(Collection<Scenario> scenarios) {
    this.scenarios = scenarios;
  }

  /**
   *  Get the constraint name
   */
  public static String getConstraintName(Scenario k, NaturalGasNode node) {
    return "SinkDir." + node + "." + k.getIndex();
  }

  @Override
  public void constructConstraint(MathematicalProgram problem, NaturalGasModel model) throws VariableExistsException, NoVariableException, InvalidConstraintException {
    FlowDirectionScenarioVariableFactory flowDirVarFactory = new FlowDirectionScenarioVariableFactory(scenarios);
 
    for (Scenario scenario : scenarios) {
       for (NaturalGasNode node : model.getNodes()) {
         
         // if the node has injections, this constraint is not valid
         if (node.getWell() != null && node.getCityGate() != null) {
           continue;
         }
         
         if (node.getCityGate() == null) {
           continue;
         }
         
         LinearConstraintGreaterEq constraint = new LinearConstraintGreaterEq(getConstraintName(scenario,node));
         constraint.setRightHandSide(1.0);
         for (NaturalGasConnection c : model.getFlowConnections(node)) {
           Variable variableF = flowDirVarFactory.getVariable(problem, c, scenario, FlowDirectionScenarioVariableFactory.FORWARD_PREFIX);
           Variable variableR = flowDirVarFactory.getVariable(problem, c, scenario, FlowDirectionScenarioVariableFactory.REVERSE_PREFIX);
           
           if (variableF != null) {
             NaturalGasNode n1 = model.getFirstNode(c);
             if (n1.equals(node)) {
               constraint.addVariable(variableR);
             }
             else {
               constraint.addVariable(variableF);               
             }
           }
         }
         problem.addLinearConstraint(constraint);         
      }
    }
  }
}
