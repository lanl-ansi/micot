package gov.lanl.micot.infrastructure.ep.optimize.dcopf.variable.scenario;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.InvalidVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

import java.util.Collection;

/**
 * General class for creating phase angle variables associated with scenarios that are relaxed to be continous
 * @author Russell Bent
 */
public class ContinousLinkCapacityOverloadScenarioVariableFactory extends LinkCapacityOverloadScenarioVariableFactory {  
  /**
   * Constructor
   * @param models
   */
  public ContinousLinkCapacityOverloadScenarioVariableFactory(Collection<Scenario> scenarios, double epsilon) {
    super(scenarios, epsilon);
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
    Variable variable = program.makeContinuousVariable(getOverloadVariableName(k.getIndex(), lineK));
    return variable;
  }
      

}
