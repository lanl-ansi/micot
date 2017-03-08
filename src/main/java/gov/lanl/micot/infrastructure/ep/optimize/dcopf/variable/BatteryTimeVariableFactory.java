package gov.lanl.micot.infrastructure.ep.optimize.dcopf.variable;

import gov.lanl.micot.infrastructure.ep.model.Battery;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.ep.optimize.VariableFactory;
import gov.lanl.micot.infrastructure.model.Asset;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

import java.util.ArrayList;
import java.util.Collection;

/**
 * General class for creating battery variables
 * @author Russell Bent
 */
public class BatteryTimeVariableFactory implements VariableFactory {

  private int numberOfIncrements = -1;
  private double incrementSize = -1;
    
  /**
   * Constructor
   * @param numberOfIncrements
   * @param incrementSize
   */
  public BatteryTimeVariableFactory(int numberOfIncrements, double incrementSize) {
    super();
    this.numberOfIncrements = numberOfIncrements;
    this.incrementSize = incrementSize;
  }

  /**
   * Function for create the variable name associated with a load
   * @param node
   * @return
   */
  private String getBatteryVariableName(Battery battery, double time) {
    return "B" + battery.toString()+"-"+time;
  }
  
  @Override
  public Collection<Variable> createVariables(MathematicalProgram program, /*Collection<ElectricPowerNode> nodes,*/ ElectricPowerModel model) throws VariableExistsException {
    ArrayList<Variable> variables = new ArrayList<Variable>();
    
    for (ElectricPowerNode node : model.getNodes()) {
      for (int i = 0; i < numberOfIncrements; ++i) {
        double time = i * incrementSize;
        for (Battery battery : node.getComponents(Battery.class)) {
          Variable variable = program.makeContinuousVariable(getBatteryVariableName(battery,time));
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
   * Get a variable by time index
   * @param program
   * @param asset
   * @param time
   * @return
   * @throws NoVariableException
   */
  public Variable getVariable(MathematicalProgram program, Asset asset, double time) throws NoVariableException {
    if (asset instanceof Battery) {
      if (program.getVariable(getBatteryVariableName((Battery) asset, time)) != null) {
        return program.getVariable(getBatteryVariableName((Battery) asset, time));
      }
    }
    throw new NoVariableException(asset.toString());
  }
  
}
