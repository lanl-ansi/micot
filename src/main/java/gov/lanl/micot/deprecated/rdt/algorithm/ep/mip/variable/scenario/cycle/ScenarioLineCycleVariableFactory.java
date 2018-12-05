package gov.lanl.micot.deprecated.rdt.algorithm.ep.mip.variable.scenario.cycle;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.model.Node;
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
 * Creates an auxiliary variable for whether or not a corridor is active or not (for cycle considerations
 * These are the \bar{x}_{ij}^s variables in the AAAI 2015 paper
 * 
 * @author Russell Bent
 */
public class ScenarioLineCycleVariableFactory extends ScenarioVariableFactory<ElectricPowerNode, ElectricPowerModel> {
  
  /**
   * Constructor
   */
  public ScenarioLineCycleVariableFactory(Collection<Scenario> scenarios) {
    super(scenarios);
  }

  /**
   * Function for create the variable name associated with a switch
   * 
   * @param node
   * @return
   */
  private String getFlowVariableName(ElectricPowerNode n1, ElectricPowerNode n2, Scenario scenario) {
    Node node1 = n1.compareTo(n2) <= 0 ? n1 : n2;
    Node node2 = n1.compareTo(n2) <= 0 ? n2 : n1; 
    return "barx-" + node1.toString() + "-" + node2.toString() + "." + scenario.getIndex();
  }

  @Override
  public Collection<Variable> createVariables(MathematicalProgram program, ElectricPowerModel model) throws VariableExistsException, InvalidVariableException {
    ArrayList<Variable> variables = new ArrayList<Variable>();
    for (Scenario scenario : getScenarios()) {
      for (ElectricPowerFlowConnection edge : model.getFlowConnections()) {
        String name = getFlowVariableName(model.getFirstNode(edge),model.getSecondNode(edge),scenario);        
        if (program.getVariable(name) == null) {
          variables.add(program.makeDiscreteVariable(name));
        }
      }
    }
    return variables;
  }

  @Override
  public Variable getVariable(MathematicalProgram program, Object asset) throws NoVariableException {
    return null;
  }
  
  /**
   * Get a variable
   * @param program
   * @param edge
   * @param scenario
   * @return
   * @throws NoVariableException
   */
  public Variable getVariable(MathematicalProgram program, ElectricPowerNode node1, ElectricPowerNode node2, Scenario scenario) throws NoVariableException {
    return program.getVariable(getFlowVariableName(node1, node2, scenario));
  }
  
}
