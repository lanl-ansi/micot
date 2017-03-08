package gov.lanl.micot.infrastructure.ep.optimize.dcopf.assignment;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerProducer;
import gov.lanl.micot.infrastructure.ep.optimize.AssignmentFactory;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.constraint.GenerationConstraint;
import gov.lanl.micot.infrastructure.optimize.OptimizationConstants;
import gov.lanl.micot.infrastructure.simulate.Simulator;
import gov.lanl.micot.util.collection.Pair;
import gov.lanl.micot.util.math.solver.Solution;
import gov.lanl.micot.util.math.solver.SummationConstraint;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

/**
 * General class for creating phase angle variable assignments
 * @author Russell Bent
 */
public class ShadowPriceAssignmentFactory implements AssignmentFactory {

  @Override
  public void performAssignment(ElectricPowerModel model, MathematicalProgram problem, Solution solution) throws VariableExistsException, NoVariableException {
    GenerationConstraint generationConstraint = new GenerationConstraint();
    double mva = model.getMVABase();    
    
    for (ElectricPowerNode node : model.getNodes()) {
      for (ElectricPowerProducer producer : node.getComponents(ElectricPowerProducer.class)) {
        SummationConstraint constraint = generationConstraint.getUpperBoundConstraint(problem, producer);
        producer.setAttribute(OptimizationConstants.SHADOW_PRICE_KEY, solution.getShadowPrice(constraint));
        Pair<Number, Number> range = solution.getShadowPriceRange(constraint);
        if (range != null) {
          producer.setAttribute(OptimizationConstants.SHADOW_PRICE_RANGE_KEY, new Pair<Number,Number>(range.getOne().doubleValue()*mva,range.getTwo().doubleValue()*mva));
        }
      }
    }
  }

 
}
