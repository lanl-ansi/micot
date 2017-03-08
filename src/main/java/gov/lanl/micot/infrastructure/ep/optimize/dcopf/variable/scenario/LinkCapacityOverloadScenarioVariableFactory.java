package gov.lanl.micot.infrastructure.ep.optimize.dcopf.variable.scenario;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.optimize.VariableFactory;
import gov.lanl.micot.infrastructure.model.FlowConnection;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.InvalidVariableException;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

import java.util.ArrayList;
import java.util.Collection;

/**
 * General class for creating phase angle variables associated with scenarios
 * @author Russell Bent
 */
public class LinkCapacityOverloadScenarioVariableFactory implements VariableFactory {

  private Collection<Scenario> scenarios = new ArrayList<Scenario>();
  private double epsilon = 0;
  
  /**
   * Constructor
   * @param models
   */
  public LinkCapacityOverloadScenarioVariableFactory(Collection<Scenario> scenarios, double epsilon) {
    this.scenarios = scenarios;
    this.epsilon = epsilon;
  }
  
  /**
   * Get the 0-1 overload variable name
   * 
   * @param edge
   * @return
   */
  protected String getOverloadVariableName(int k, ElectricPowerFlowConnection edge) {
    return "O" + k + "." + edge.toString();
  }
       
  @Override
  public Collection<Variable> createVariables(MathematicalProgram program,  ElectricPowerModel baseModel) throws VariableExistsException, InvalidVariableException {
    ArrayList<Variable> variables = new ArrayList<Variable>();
    for (Scenario scenario : scenarios) {
      // this is hard constraint elsewhere
      if (scenario.getWeight() > epsilon) {
        continue;
      }
      
      for (FlowConnection connection : baseModel.getFlowConnections()) {
        boolean status = scenario.computeActualStatus(connection, true);        
        if (status) {
          variables.add(createVariable(program, scenario, (ElectricPowerFlowConnection)connection));
        }
      }
    }
    return variables;
  }

  /**
   * Make the variable creation process easily accessible by other codes
   * @param program
   * @param k
   * @param lineK
   * @return
   * @throws VariableExistsException
   * @throws InvalidVariableException
   */
  public Variable createVariable(MathematicalProgram program, Scenario k, ElectricPowerFlowConnection lineK) throws VariableExistsException, InvalidVariableException {
    Variable variable = program.makeDiscreteVariable(getOverloadVariableName(k.getIndex(), lineK));
    return variable;
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
      Variable v = program.getVariable(getOverloadVariableName(scenario.getIndex(), (ElectricPowerFlowConnection) asset));
      if (v != null) {
        return v;
      }        
    }
    throw new NoVariableException(asset.toString());
  }
  
  /**
   * Does a variable exist or not?
   * @param program
   * @param asset
   * @param scenario
   * @return
   */
  public boolean variableExists(MathematicalProgram program, Object asset, Scenario scenario) {
    if (asset instanceof ElectricPowerFlowConnection) {
      Variable v = program.getVariable(getOverloadVariableName(scenario.getIndex(), (ElectricPowerFlowConnection) asset));
      if (v != null) {
        return true;
      }        
    }
    return false;    
  }

}
