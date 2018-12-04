package gov.lanl.micot.deprecated.rdt.algorithm.ep.mip.assignment;


import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.Generator;
import gov.lanl.micot.infrastructure.ep.optimize.AssignmentFactory;
import gov.lanl.micot.application.rdt.algorithm.AlgorithmConstants;
import gov.lanl.micot.deprecated.rdt.algorithm.ep.mip.variable.MicrogridCapacityVariableFactory;
import gov.lanl.micot.util.math.solver.Solution;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

/**
 * Assignments microgrid capacity
 * @author Russell Bent
 */
public class MicrogridCapacityAssignmentFactory implements AssignmentFactory {
    
  /**
   * Constructor
   * @param numberOfIncrements
   * @param incrementSize
   */
  public MicrogridCapacityAssignmentFactory() {
    super();
  }
  
  @Override
  public void performAssignment(ElectricPowerModel model, MathematicalProgram problem, Solution solution) throws VariableExistsException, NoVariableException {
    MicrogridCapacityVariableFactory gridVariableFactory = new MicrogridCapacityVariableFactory();
    double mvaBase = model.getMVABase();
    
    for (Generator generator : model.getGenerators()) {
      Variable variable = gridVariableFactory.getVariable(problem, generator, MicrogridCapacityVariableFactory.PHASE_A);
      if (variable != null) {
        double capacity = solution.getValueDouble(variable);
        generator.setAttribute(AlgorithmConstants.MICROGRID_CAPACITY_PHASE_A_KEY, capacity * mvaBase);
      }
      
      variable = gridVariableFactory.getVariable(problem, generator, MicrogridCapacityVariableFactory.PHASE_B);
      if (variable != null) {
        double capacity = solution.getValueDouble(variable);
        generator.setAttribute(AlgorithmConstants.MICROGRID_CAPACITY_PHASE_B_KEY, capacity * mvaBase);
      }

      variable = gridVariableFactory.getVariable(problem, generator, MicrogridCapacityVariableFactory.PHASE_C);
      if (variable != null) {
        double capacity = solution.getValueDouble(variable);
        generator.setAttribute(AlgorithmConstants.MICROGRID_CAPACITY_PHASE_C_KEY, capacity * mvaBase);
      }

      
    }
  }
}
