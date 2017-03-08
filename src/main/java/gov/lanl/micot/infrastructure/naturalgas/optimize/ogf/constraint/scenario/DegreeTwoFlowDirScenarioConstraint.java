package gov.lanl.micot.infrastructure.naturalgas.optimize.ogf.constraint.scenario;

import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasConnection;
import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasModel;
import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasNode;
import gov.lanl.micot.infrastructure.naturalgas.optimize.ConstraintFactory;
import gov.lanl.micot.infrastructure.naturalgas.optimize.ogf.variable.scenario.FlowDirectionScenarioVariableFactory;
import gov.lanl.micot.util.math.solver.LinearConstraintEquals;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.InvalidConstraintException;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * A cut that indicates that all nodes with no injections and degree two must have their flow direction coordinated
 * 
 * @author Russell Bent
 */
public class DegreeTwoFlowDirScenarioConstraint implements ConstraintFactory {

  private Collection<Scenario> scenarios = new ArrayList<Scenario>();

  private static final String PREFIX1 = "1";
  private static final String PREFIX2 = "2";
  private static final String PREFIX3 = "3";
  private static final String PREFIX4 = "4";
  
  /**
   * Constructor
   * 
   * @param scenarios
   */
  public DegreeTwoFlowDirScenarioConstraint(Collection<Scenario> scenarios) {
    this.scenarios = scenarios;
  }

  /**
   * Get the constraint name
   */
  public static String getConstraintName(Scenario k, NaturalGasConnection c1, NaturalGasConnection c2, String prefix) {
    return "DegreeTwo." + c1 + "," + c2 + "." + k.getIndex() + "-" + prefix;
  }

  /**
   * determine if the constraint should be applied or not
   * 
   * @param node
   * @return
   */
  private boolean applyConstraint(NaturalGasNode node) {
    // if the node has injections, this constraint is not valid
    if (node.getWell() != null) {
      return false;
    }
    if (node.getCityGate() != null) {
      return false;
    }
    return true;
  }

  /**
   * Get the active neighbors
   * 
   * @param node
   * @param model
   * @param problem
   * @param scenario
   * @return
   * @throws NoVariableException
   */
  private Set<NaturalGasNode> getActiveNeighbors(NaturalGasNode node, NaturalGasModel model, MathematicalProgram problem, Scenario scenario) throws NoVariableException {
    FlowDirectionScenarioVariableFactory flowDirVarFactory = new FlowDirectionScenarioVariableFactory(scenarios);
    HashSet<NaturalGasNode> activeNeighbors = new HashSet<NaturalGasNode>();
    for (NaturalGasConnection connection : model.getFlowConnections(node)) {
      Variable v = flowDirVarFactory.getVariable(problem, connection, scenario, FlowDirectionScenarioVariableFactory.REVERSE_PREFIX);
      if (v != null) {
        NaturalGasNode n1 = model.getFirstNode(connection);
        NaturalGasNode n2 = model.getSecondNode(connection);

        if (!n1.equals(node)) {
          activeNeighbors.add(n1);
        }
        if (!n2.equals(node)) {
          activeNeighbors.add(n2);
        }
      }
    }
    return activeNeighbors;
  }

  @Override
  public void constructConstraint(MathematicalProgram problem, NaturalGasModel model) throws VariableExistsException, NoVariableException, InvalidConstraintException {
    FlowDirectionScenarioVariableFactory flowDirVarFactory = new FlowDirectionScenarioVariableFactory(scenarios);

    for (Scenario scenario : scenarios) {
      for (NaturalGasNode node : model.getNodes()) {

    	// don't apply this constraint unless it has no injections
        if (!applyConstraint(node)) {
          continue;
        }

        // don't apply this constraint unless it has degree 2
        Set<NaturalGasNode> activeNeighbors = getActiveNeighbors(node, model, problem, scenario);
        if (activeNeighbors.size() != 2) {
          continue;
        }


        // could make this faster than quadratic, but since there are not many connections, this is fine
        // and easy to follow to the human reader
        for (NaturalGasConnection c1 : model.getFlowConnections(node)) {
          for (NaturalGasConnection c2 : model.getFlowConnections(node)) {

            // don't apply the constraint if c1 and c2 are the same or the comparison is < (only want to add the constraint once)
            if (c1.compareTo(c2) <=0 ) {
              continue;
            }
            
            NaturalGasNode c1N1 = model.getFirstNode(c1);
            NaturalGasNode c1N2 = model.getSecondNode(c1);

            NaturalGasNode c2N1 = model.getFirstNode(c2);
            NaturalGasNode c2N2 = model.getSecondNode(c2);
            
            // don't apply if the edges are in parallel
            if (c1N1.equals(c2N1) && c1N2.equals(c2N2)) {
              continue;
            }
            
            
            if (c1N2.equals(c2N1) && c1N2.equals(node)) {
              LinearConstraintEquals constraint = new LinearConstraintEquals(getConstraintName(scenario, c1,c2, PREFIX1));       
              Variable variable1 = flowDirVarFactory.getVariable(problem, c1, scenario, FlowDirectionScenarioVariableFactory.FORWARD_PREFIX);
              Variable variable2 = flowDirVarFactory.getVariable(problem, c2, scenario, FlowDirectionScenarioVariableFactory.FORWARD_PREFIX);
              
              constraint.setRightHandSide(0.0);
              constraint.addVariable(variable1, 1.0);
              constraint.addVariable(variable2, -1.0);
              problem.addLinearConstraint(constraint);
            }            
            
            
            if (c1N2.equals(c2N2) && c1N2.equals(node)) {
              LinearConstraintEquals constraint = new LinearConstraintEquals(getConstraintName(scenario, c1,c2, PREFIX2));       
              Variable variable1 = flowDirVarFactory.getVariable(problem, c1, scenario, FlowDirectionScenarioVariableFactory.FORWARD_PREFIX);
              Variable variable2 = flowDirVarFactory.getVariable(problem, c2, scenario, FlowDirectionScenarioVariableFactory.REVERSE_PREFIX);
              
              constraint.setRightHandSide(0.0);
              constraint.addVariable(variable1, 1.0);
              constraint.addVariable(variable2, -1.0);
              problem.addLinearConstraint(constraint);                            
            }            

            if (c1N1.equals(c2N1) && c1N1.equals(node)) {
              LinearConstraintEquals constraint = new LinearConstraintEquals(getConstraintName(scenario, c1,c2, PREFIX3));       
              Variable variable1 = flowDirVarFactory.getVariable(problem, c1, scenario, FlowDirectionScenarioVariableFactory.REVERSE_PREFIX);
              Variable variable2 = flowDirVarFactory.getVariable(problem, c2, scenario, FlowDirectionScenarioVariableFactory.FORWARD_PREFIX);
              
              constraint.setRightHandSide(0.0);
              constraint.addVariable(variable1, 1.0);
              constraint.addVariable(variable2, -1.0);
              problem.addLinearConstraint(constraint);
            }

            if (c1N1.equals(c2N2) && c1N1.equals(node)) {
              LinearConstraintEquals constraint = new LinearConstraintEquals(getConstraintName(scenario, c1,c2, PREFIX4));       
              Variable variable1 = flowDirVarFactory.getVariable(problem, c1, scenario, FlowDirectionScenarioVariableFactory.REVERSE_PREFIX);
              Variable variable2 = flowDirVarFactory.getVariable(problem, c2, scenario, FlowDirectionScenarioVariableFactory.REVERSE_PREFIX);
              
              constraint.setRightHandSide(0.0);
              constraint.addVariable(variable1, 1.0);
              constraint.addVariable(variable2, -1.0);
              problem.addLinearConstraint(constraint);
            }            

            
            
          }        	        	
        }
      }
    }
  }
}

