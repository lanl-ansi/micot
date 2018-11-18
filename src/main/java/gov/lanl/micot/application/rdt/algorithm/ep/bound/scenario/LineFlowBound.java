package gov.lanl.micot.application.rdt.algorithm.ep.bound.scenario;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.optimize.ConstraintFactory;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.application.rdt.algorithm.ep.variable.scenario.LineFlowVariableFactory;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

/**
 * Bounds on line flows
 * 
 * @author Russell Bent
 */
public class LineFlowBound implements ConstraintFactory {

  private Scenario scenario = null;
  
  /**
   * Constraint
   */
  public LineFlowBound(Scenario scenario) {
    this.scenario = scenario;
  }

  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws NoVariableException {
    LineFlowVariableFactory variableFactory = new LineFlowVariableFactory(scenario);
    double mvaBase = model.getMVABase();
    for (ElectricPowerFlowConnection edge : model.getFlowConnections()) {
      boolean hasVariable = variableFactory.hasVariable(edge);
      double capacity = edge.getCapacityRating() / mvaBase;
           
      if (hasVariable) {            
        if (edge.getAttribute(ElectricPowerFlowConnection.HAS_PHASE_A_KEY, Boolean.class)) {
          Variable fp_a = variableFactory.getRealVariable(problem, edge, LineFlowVariableFactory.PHASE_A);
          Variable fq_a = variableFactory.getReactiveVariable(problem, edge, LineFlowVariableFactory.PHASE_A);          
          problem.addBounds(fp_a, -capacity, capacity);
          problem.addBounds(fq_a, -capacity, capacity);
        }
        
        if (edge.getAttribute(ElectricPowerFlowConnection.HAS_PHASE_B_KEY, Boolean.class)) {        
          Variable fp_b = variableFactory.getRealVariable(problem, edge, LineFlowVariableFactory.PHASE_B);
          Variable fq_b = variableFactory.getReactiveVariable(problem, edge, LineFlowVariableFactory.PHASE_B);                    
          problem.addBounds(fp_b, -capacity, capacity);
          problem.addBounds(fq_b, -capacity, capacity);
        }
   
        if (edge.getAttribute(ElectricPowerFlowConnection.HAS_PHASE_C_KEY, Boolean.class)) {        
          Variable fp_c = variableFactory.getRealVariable(problem, edge, LineFlowVariableFactory.PHASE_C);
          Variable fq_c = variableFactory.getReactiveVariable(problem, edge, LineFlowVariableFactory.PHASE_C);                     
          problem.addBounds(fp_c, -capacity, capacity);
          problem.addBounds(fq_c, -capacity, capacity);
        }
      }
    }
  }
  
}
