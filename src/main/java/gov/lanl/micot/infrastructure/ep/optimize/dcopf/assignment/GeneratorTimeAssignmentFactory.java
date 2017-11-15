package gov.lanl.micot.infrastructure.ep.optimize.dcopf.assignment;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.ep.model.Generator;
import gov.lanl.micot.infrastructure.ep.optimize.AssignmentFactory;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.variable.GeneratorTimeVariableFactory;
import gov.lanl.micot.util.math.LookupTableTimeDependentFunction;
import gov.lanl.micot.util.math.solver.Solution;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

/**
 * Assignments when the generator is a variable
 * @author Russell Bent
 */
public class GeneratorTimeAssignmentFactory implements AssignmentFactory {

  
  private int numberOfIncrements = -1;
  private double incrementSize = -1;
    
  /**
   * Constructor
   * @param numberOfIncrements
   * @param incrementSize
   */
  public GeneratorTimeAssignmentFactory(int numberOfIncrements, double incrementSize) {
    super();
    this.numberOfIncrements = numberOfIncrements;
    this.incrementSize = incrementSize;
  }
  
  @Override
  public void performAssignment(ElectricPowerModel model, MathematicalProgram problem, Solution solution/*, Collection<ElectricPowerNode> nodes*/) throws VariableExistsException, NoVariableException {
    GeneratorTimeVariableFactory generatorVariableFactory = new GeneratorTimeVariableFactory(numberOfIncrements, incrementSize);
    double mva = model.getMVABase(); 
    
    for (ElectricPowerNode node : model.getNodes()) {
      for (Generator gen : node.getComponents(Generator.class)) {
        gen.setRealGeneration(new LookupTableTimeDependentFunction());
      }
      
      for (int i = 0; i < numberOfIncrements; ++i) {
        double time = i * incrementSize;  
        for (Generator gen : node.getComponents(Generator.class)) {
          Variable variable = generatorVariableFactory.getVariable(problem, gen, time);
          ((LookupTableTimeDependentFunction)gen.getRealGeneration()).addEntry(time, solution.getValueDouble(variable) * mva);
        }
      }
    }
  }

 
}
