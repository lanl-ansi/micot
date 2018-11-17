package gov.lanl.micot.application.rdt.algorithm.ep.variable.scenario;

import gov.lanl.micot.infrastructure.ep.model.Bus;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.optimize.VariableFactory;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

import java.util.ArrayList;
import java.util.Collection;

/**
 * General class for creating voltage variables associated with each bus 
 * on each phase
 * 
 * @author Russell Bent
 */
public class VoltageVariableFactory implements VariableFactory {

  public static final String PHASE_A = "a";
  public static final String PHASE_B = "b";
  public static final String PHASE_C = "c";

  private Scenario scenario = null;
  
  /**
   * Constructor
   * 
   * @param scenario
   */
  public VoltageVariableFactory(Scenario scenario) {
    this.scenario = scenario;
  }

  /**
   * Function for create the variable name associated with a bus
   * 
   * @param node
   * @return
   */
  private String getVoltageVariableName(Bus bus, String phase) {
    return "v^s." + phase + "-" + bus.toString() + "." + scenario.getIndex();
  }

  @Override
  public Collection<Variable> createVariables(MathematicalProgram program, ElectricPowerModel model) throws VariableExistsException {
    ArrayList<Variable> variables = new ArrayList<Variable>();
    for (Bus bus : model.getBuses()) {
      variables.add(program.makeContinuousVariable(getVoltageVariableName(bus, PHASE_A)));        
      variables.add(program.makeContinuousVariable(getVoltageVariableName(bus, PHASE_B)));        
      variables.add(program.makeContinuousVariable(getVoltageVariableName(bus, PHASE_C)));        
    }
    return variables;
  }

  @Override
  public Variable getVariable(MathematicalProgram program, Object asset) throws NoVariableException {
    throw new NoVariableException(asset.toString());
  }

  /**
   * Get the specific voltage variable
   * @param program
   * @param asset
   * @param phase
   * @param scenario
   * @return
   * @throws NoVariableException
   */
  public Variable getVariable(MathematicalProgram program, Bus asset, String phase) throws NoVariableException {
    return program.getVariable(getVoltageVariableName(asset, phase));
  }

}
