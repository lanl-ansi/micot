package gov.lanl.micot.deprecated.rdt.algorithm.ep.mip.variable.scenario.tree;

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
 * Flow variable from a virtual source node to all the nodes in the power network.  This is the source
 * of flow that will force the network to be operated as a tree.
 * @author Russell Bent
 */
public class ScenarioVirtualFlowChoiceVariableFactory extends ScenarioVariableFactory<ElectricPowerNode, ElectricPowerModel> {

  
  /**
   * Constructor
   * @param scenarios
   */
  public ScenarioVirtualFlowChoiceVariableFactory(Collection<Scenario> scenarios) {
    super(scenarios);
  }
  
  
  /**
   * Function for create the variable name associated with a load
   * 
   * @param node
   * @return
   */
  private String getVariableName(ElectricPowerNode node, Scenario scenario) {
    return "VirtualFlowChoice-" + node.toString() + "_" + scenario;
  }
  
  @Override
  public Collection<Variable> createVariables(MathematicalProgram program,  ElectricPowerModel model) throws VariableExistsException, InvalidVariableException {
    ArrayList<Variable> variables = new ArrayList<Variable>();
    
    for (ElectricPowerNode node : model.getNodes()) {
      for (Scenario scenario : getScenarios()) {
        variables.add(program.makeDiscreteVariable(getVariableName(node, scenario)));        
      }
    }
    
    return variables;
  }

  @Override
  public Variable getVariable(MathematicalProgram program, Object asset) throws NoVariableException {
    return null;
  }

  public Variable getVariable(MathematicalProgram program, ElectricPowerNode node, Scenario scenario) {
    return program.getVariable(getVariableName(node, scenario));    
  }

}
