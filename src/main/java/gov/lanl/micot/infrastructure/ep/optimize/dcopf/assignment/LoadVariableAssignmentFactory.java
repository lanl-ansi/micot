package gov.lanl.micot.infrastructure.ep.optimize.dcopf.assignment;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.ep.model.Load;
import gov.lanl.micot.infrastructure.ep.optimize.AssignmentFactory;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.constraint.LoadConstraint;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.variable.LoadVariableFactory;
import gov.lanl.micot.infrastructure.optimize.OptimizationConstants;
import gov.lanl.micot.infrastructure.simulate.Simulator;
import gov.lanl.micot.util.collection.Pair;
import gov.lanl.micot.util.math.solver.Solution;
import gov.lanl.micot.util.math.solver.SummationConstraint;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

/**
 * Assignments when the load is a variable
 * @author Russell Bent
 */
public class LoadVariableAssignmentFactory implements AssignmentFactory {

  @Override
  public void performAssignment(ElectricPowerModel model, MathematicalProgram problem, Solution solution) throws VariableExistsException, NoVariableException {

    LoadVariableFactory loadVariableFactory = new LoadVariableFactory();
    LoadConstraint loadConstraintFactory = new LoadConstraint();
    double mva = model.getMVABase(); 
    
    for (ElectricPowerNode node : model.getNodes()) {
      for (Load load : node.getComponents(Load.class)) {
        Variable variable = loadVariableFactory.getVariable(problem,load);
        load.setRealLoad(solution.getValueDouble(variable) * mva);        
        SummationConstraint constraint = loadConstraintFactory.getUpperBoundConstraint(problem, load);
        if (solution.getShadowPrice(constraint) != null) {
          load.setAttribute(OptimizationConstants.SHADOW_PRICE_KEY, solution.getShadowPrice(constraint));
        }
        Pair<Number, Number> range = solution.getShadowPriceRange(constraint);
        if (range != null) {
          load.setAttribute(OptimizationConstants.SHADOW_PRICE_RANGE_KEY, new Pair<Number, Number>(range.getOne().doubleValue() * mva, range.getTwo().doubleValue() * mva));
        }
      }
    }
  }

 
}
