package gov.lanl.micot.infrastructure.ep.optimize.dcopf.assignment;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.ep.optimize.AssignmentFactory;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.variable.PhaseAngleTimeVariableFactory;
import gov.lanl.micot.util.math.LookupTableTimeDependentFunction;
import gov.lanl.micot.util.math.MathUtils;
import gov.lanl.micot.util.math.solver.Solution;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

/**
 * Assignments when the generator is a variable
 * @author Russell Bent
 */
public class PhaseAngleTimeAssignmentFactory implements AssignmentFactory {

  private int numberOfIncrements = -1;
  private double incrementSize = -1;
    
  /**
   * Constructor
   * @param numberOfIncrements
   * @param incrementSize
   */
  public PhaseAngleTimeAssignmentFactory(int numberOfIncrements, double incrementSize) {
    super();
    this.numberOfIncrements = numberOfIncrements;
    this.incrementSize = incrementSize;
  }
  
  @Override
  public void performAssignment(ElectricPowerModel model, MathematicalProgram problem, Solution solution) throws VariableExistsException, NoVariableException {
    PhaseAngleTimeVariableFactory phaseAngleVariableFactory = new PhaseAngleTimeVariableFactory(numberOfIncrements, incrementSize);
    
    for (ElectricPowerNode node : model.getNodes()) {
      node.getBus().setPhaseAngle(new LookupTableTimeDependentFunction());      
      for (int i = 0; i < numberOfIncrements; ++i) {
        double time = i * incrementSize; 
        Variable variable = phaseAngleVariableFactory.getVariable(problem, node, time);
        ((LookupTableTimeDependentFunction)node.getBus().getPhaseAngle()).addEntry(time,MathUtils.TO_DEGREES(solution.getValueDouble(variable)));        
      }
    }
  }

 
}
