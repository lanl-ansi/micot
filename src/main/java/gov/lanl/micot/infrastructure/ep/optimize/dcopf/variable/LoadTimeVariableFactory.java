package gov.lanl.micot.infrastructure.ep.optimize.dcopf.variable;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.ep.model.Load;
import gov.lanl.micot.infrastructure.ep.optimize.VariableFactory;
import gov.lanl.micot.infrastructure.model.Asset;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

import java.util.ArrayList;
import java.util.Collection;

/**
 * General class for creating load variables
 * @author Russell Bent
 */
public class LoadTimeVariableFactory implements VariableFactory {

  private int numberOfIncrements = -1;
  private double incrementSize = -1;
    
  /**
   * Constructor
   * @param numberOfIncrements
   * @param incrementSize
   */
  public LoadTimeVariableFactory(int numberOfIncrements, double incrementSize) {
    super();
    this.numberOfIncrements = numberOfIncrements;
    this.incrementSize = incrementSize;
  }

  /**
   * Function for create the variable name associated with a load
   * @param node
   * @return
   */
  private String getLoadVariableName(Load load, double time) {
    return "L" + load.toString()+"-"+time;
  }
  
  @Override
  public Collection<Variable> createVariables(MathematicalProgram program, /*Collection<ElectricPowerNode> nodes,*/ ElectricPowerModel model) throws VariableExistsException {
    ArrayList<Variable> variables = new ArrayList<Variable>();
    
    for (ElectricPowerNode node : model.getNodes()) {
      for (int i = 0; i < numberOfIncrements; ++i) {
        double time = i * incrementSize;
        for (Load load : node.getComponents(Load.class)) {
          Variable variable = program.makeContinuousVariable(getLoadVariableName(load,time));
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
    if (asset instanceof Load) {
      if (program.getVariable(getLoadVariableName((Load) asset, time)) != null) {
        return program.getVariable(getLoadVariableName((Load) asset, time));
      }
    }
    throw new NoVariableException(asset.toString());
  }
  
}
