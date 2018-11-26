package gov.lanl.micot.application.rdt.algorithm.ep.variable.scenario;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
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
 * Creates flow variables on a per phase basis and per scenario basis
 * 
 * @author Russell Bent
 */
public class LineFlowVariableFactory implements VariableFactory {

  public static final String PHASE_A = "a";
  public static final String PHASE_B = "b";
  public static final String PHASE_C = "c";
  
  private Scenario scenario = null;

  /**
   * Constructor
   */
  public LineFlowVariableFactory(Scenario scenario) {
    this.scenario = scenario;
  }

  /**
   * Function for create the variable name associated with real flow
   * 
   * @param node
   * @return
   */
  private String getRealVariableName(ElectricPowerFlowConnection edge, String phase) {
    return "f(p)^s." + phase + "-" + edge.toString() + "." + scenario.getIndex();
  }
  
  /**
   * Function for create the variable name associated with real flow
   * 
   * @param node
   * @return
   */
  private String getReactiveVariableName(ElectricPowerFlowConnection edge, String phase) {
    return "f(q)^s." + phase + "-" + edge.toString() + "." + scenario.getIndex();
  }

  /**
   * Has a variable
   * @param edge
   * @return
   */
  public boolean hasVariable(ElectricPowerFlowConnection edge) {
    LineActiveVariableFactory activeFactory = new LineActiveVariableFactory(scenario);
    return activeFactory.hasVariable(edge);
  }
  
  @Override
  public Collection<Variable> createVariables(MathematicalProgram program, ElectricPowerModel model) throws VariableExistsException {
    ArrayList<Variable> variables = new ArrayList<Variable>();
    
    for (ElectricPowerFlowConnection edge : model.getFlowConnections()) {
      boolean hasVariable = hasVariable(edge);
      
      if (hasVariable) {        
        if (edge.getAttribute(ElectricPowerFlowConnection.HAS_PHASE_A_KEY, Boolean.class)) {
          variables.add(program.makeContinuousVariable(getRealVariableName(edge, PHASE_A)));
          variables.add(program.makeContinuousVariable(getReactiveVariableName(edge, PHASE_A)));
        }
        
        if (edge.getAttribute(ElectricPowerFlowConnection.HAS_PHASE_B_KEY, Boolean.class)) {
          variables.add(program.makeContinuousVariable(getRealVariableName(edge, PHASE_B)));
          variables.add(program.makeContinuousVariable(getReactiveVariableName(edge, PHASE_B)));
        }
        
        if (edge.getAttribute(ElectricPowerFlowConnection.HAS_PHASE_C_KEY, Boolean.class)) {
          variables.add(program.makeContinuousVariable(getRealVariableName(edge, PHASE_C)));
          variables.add(program.makeContinuousVariable(getReactiveVariableName(edge, PHASE_C)));
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
   * String real phase
   * 
   * @param program
   * @param asset
   * @param phase
   * @return
   * @throws NoVariableException
   */
  public Variable getRealVariable(MathematicalProgram program, ElectricPowerFlowConnection asset, String phase) throws NoVariableException {
    return program.getVariable(getRealVariableName(asset, phase));
  }

  /**
   * String real phase
   * 
   * @param program
   * @param asset
   * @param phase
   * @return
   * @throws NoVariableException
   */
  public Variable getReactiveVariable(MathematicalProgram program, ElectricPowerFlowConnection asset, String phase) throws NoVariableException {
    return program.getVariable(getReactiveVariableName(asset, phase));
  }
}
