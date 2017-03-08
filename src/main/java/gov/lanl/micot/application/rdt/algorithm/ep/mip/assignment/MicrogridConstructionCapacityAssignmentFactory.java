package gov.lanl.micot.application.rdt.algorithm.ep.mip.assignment;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.Generator;
import gov.lanl.micot.infrastructure.ep.optimize.AssignmentFactory;
import gov.lanl.micot.application.rdt.algorithm.AlgorithmConstants;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.variable.MicrogridVariableFactory;
import gov.lanl.micot.util.math.solver.Solution;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

/**
 * Assignments microgrid capacity / unit commitment
 * @author Russell Bent
 */
public class MicrogridConstructionCapacityAssignmentFactory implements AssignmentFactory {
    
  /**
   * Constructor
   * @param numberOfIncrements
   * @param incrementSize
   */
  public MicrogridConstructionCapacityAssignmentFactory() {
    super();
  }
  
  @Override
  public void performAssignment(ElectricPowerModel model, MathematicalProgram problem, Solution solution) throws VariableExistsException, NoVariableException {
    MicrogridVariableFactory factory = new MicrogridVariableFactory();
    
    for (Generator generator : model.getGenerators()) {
      Variable variable = factory.getVariable(problem, generator);
      if (variable != null) {
        int built  = solution.getValueInt(variable);
        generator.setAttribute(AlgorithmConstants.IS_CONSTRUCTED_KEY, built == 1 ? true : false);
        double capacity = built * generator.getAttribute(AlgorithmConstants.MAX_MICROGRID_KEY, Number.class).doubleValue();
        generator.setAttribute(AlgorithmConstants.MICROGRID_CAPACITY_PHASE_A_KEY, capacity);
        generator.setAttribute(AlgorithmConstants.MICROGRID_CAPACITY_PHASE_B_KEY, capacity);
        generator.setAttribute(AlgorithmConstants.MICROGRID_CAPACITY_PHASE_C_KEY, capacity);
      }
    }
  }
}
