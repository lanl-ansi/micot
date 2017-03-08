package gov.lanl.micot.infrastructure.ep.optimize.dcopf.assignment;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerProducer;
import gov.lanl.micot.infrastructure.ep.optimize.AssignmentFactory;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.variable.GeneratorVariableFactory;
import gov.lanl.micot.util.math.solver.Solution;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

/**
 * General class for creating phase angle variable assignments
 * @author Russell Bent
 */
public class GenerationAssignmentFactory implements AssignmentFactory {

  @Override
  public void performAssignment(ElectricPowerModel model, MathematicalProgram problem, Solution solution/*, Collection<ElectricPowerNode> nodes*/) throws VariableExistsException, NoVariableException {
    GeneratorVariableFactory generatorVariableFactory = new GeneratorVariableFactory();
    double mva = model.getMVABase();    
    
    for (ElectricPowerNode node : model.getNodes()) {
      for (ElectricPowerProducer producer : node.getComponents(ElectricPowerProducer.class)) {
        Variable variable = generatorVariableFactory.getVariable(problem, producer);
        producer.setActualRealGeneration(solution.getValueDouble(variable) * mva);
      }
    }
  }

 
}
