package gov.lanl.micot.deprecated.rdt.algorithm.ep.mip.variable.scenario.tree;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.optimize.mathprogram.variable.ScenarioVariableFactory;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.InvalidVariableException;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Creates a variable for (virtual) flow in a network that forces the operation
 * of the network to be a tree
 * 
 * @author Russell Bent
 */
public class ScenarioTreeFlowVariableFactory extends ScenarioVariableFactory<ElectricPowerNode, ElectricPowerModel> {

  /**
   * Constructor
   */
  public ScenarioTreeFlowVariableFactory(Collection<Scenario> scenarios) {
    super(scenarios);
  }

  /**
   * Get the variable name
   * 
   * @param n1
   * @param n2
   * @return
   */
  private String getVariableName(ElectricPowerNode n1, ElectricPowerNode n2, Scenario scenario) {
    ElectricPowerNode node1 = n1.compareTo(n2) < 0 ? n1 : n2;
    ElectricPowerNode node2 = n1.compareTo(n2) < 0 ? n2 : n1;
    return "TreeFlow-" + node1.toString() + "-" + node2.toString() + "_" + scenario;
  }

  @Override
  public Collection<Variable> createVariables(MathematicalProgram program, ElectricPowerModel model) throws VariableExistsException, InvalidVariableException {
    ArrayList<Variable> variables = new ArrayList<Variable>();
    for (ElectricPowerFlowConnection edge : model.getFlowConnections()) {
      for (Scenario scenario : getScenarios()) {
        ElectricPowerNode node1 = model.getFirstNode(edge);
        ElectricPowerNode node2 = model.getSecondNode(edge);
        String name = getVariableName(node1, node2, scenario);
        if (program.getVariable(name) == null) {
          variables.add(program.makeContinuousVariable(name));
        }
      }
    }
    return variables;
  }

  @Override
  public Variable getVariable(MathematicalProgram program, Object asset) throws NoVariableException {
    throw new NoVariableException("Incorrect usage of TreeFlowVariableFactory");
  }

  /**
   * Get a variable
   * 
   * @param program
   * @param node1
   * @param node2
   * @return
   * @throws NoVariableException
   */
  public Variable getVariable(MathematicalProgram program, ElectricPowerNode node1, ElectricPowerNode node2, Scenario scenario) throws NoVariableException {
    return program.getVariable(getVariableName(node1, node2, scenario));
  }
  
}

