package gov.lanl.micot.infrastructure.ep.optimize.dcopf.variable.scenario;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.ep.optimize.VariableFactory;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

import java.util.ArrayList;
import java.util.Collection;

/**
 * General class for creating phase angle variables associated with scenarios
 * @author Russell Bent
 */
public class FlowScenarioVariableFactory implements VariableFactory {

  private Collection<Scenario> scenarios = new ArrayList<Scenario>();
  
  /**
   * Constructor
   * @param models
   */
  public FlowScenarioVariableFactory(Collection<Scenario> scenarios) {
    this.scenarios = scenarios;
  }
  
  /**
   * Get the phase angle variable name
   * 
   * @param edge
   * @return
   */
  private String getFlowVariableName(int k, ElectricPowerFlowConnection edge) {
    return "F" + edge.toString() + "." + k;
  }
     
  @Override
  public Collection<Variable> createVariables(MathematicalProgram program, /*Collection<ElectricPowerNode> nodes,*/ ElectricPowerModel basemodel) throws VariableExistsException {
    ArrayList<Variable> variables = new ArrayList<Variable>();
    for (Scenario scenario : scenarios) {
      int k = scenario.getIndex();
//      ElectricPowerModel model = (ElectricPowerModel)scenario.getModel();
      for (ElectricPowerFlowConnection connection : basemodel.getFlowConnections()) {
    //    ElectricPowerNode node1 = basemodel.getFirstNode(connection);
      //  ElectricPowerNode node2 = basemodel.getSecondNode(connection);
        boolean status = scenario.computeActualStatus(connection,true);
        if (/*nodes.contains(node1) && nodes.contains(node2) && */status ) {
          Variable variable = program.makeContinuousVariable(getFlowVariableName(k, connection));
          variables.add(variable);
        }
      }
    }
    return variables;
  }

  @Override
  public Variable getVariable(MathematicalProgram program, Object asset) throws NoVariableException {
    throw new NoVariableException(asset.toString());
  }

  /**
   * Get a variable
   * @param program
   * @param asset
   * @param scenario
   * @return
   * @throws NoVariableException
   */
  public Variable getVariable(MathematicalProgram program, Object asset, Scenario scenario) throws NoVariableException {
    if (asset instanceof ElectricPowerFlowConnection) {
      Variable v = program.getVariable(getFlowVariableName(scenario.getIndex(), (ElectricPowerFlowConnection) asset));
      if (v != null) {
        return v;
      }
    }     
    return null;
  }
  
}
