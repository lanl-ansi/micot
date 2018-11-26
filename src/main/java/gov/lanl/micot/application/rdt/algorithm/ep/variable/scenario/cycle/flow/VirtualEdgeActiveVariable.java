package gov.lanl.micot.application.rdt.algorithm.ep.variable.scenario.cycle.flow;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.ep.optimize.VariableFactory;
import gov.lanl.micot.infrastructure.model.Scenario;
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
public class VirtualEdgeActiveVariable implements VariableFactory {

  private Scenario scenario = null;
  
  /**
   * Constructor
   * @param scenarios
   */
  public VirtualEdgeActiveVariable(Scenario scenario) {
    this.scenario = scenario;
  }
  
  /**
   * Function for create the variable name associated with a load
   * 
   * @param node
   * @return
   */
  private String getVariableName(ElectricPowerNode node) {
    return "VirtualFlowChoice-" + node.toString() + "_" + scenario;
  }
  
  @Override
  public Collection<Variable> createVariables(MathematicalProgram program,  ElectricPowerModel model) throws VariableExistsException, InvalidVariableException {
    ArrayList<Variable> variables = new ArrayList<Variable>();
    
    for (ElectricPowerNode node : model.getNodes()) {
      variables.add(program.makeDiscreteVariable(getVariableName(node)));        
    }
    
    return variables;
  }

  @Override
  public Variable getVariable(MathematicalProgram program, Object asset) throws NoVariableException {
    return null;
  }

  public Variable getVariable(MathematicalProgram program, ElectricPowerNode node) {
    return program.getVariable(getVariableName(node));    
  }

}
