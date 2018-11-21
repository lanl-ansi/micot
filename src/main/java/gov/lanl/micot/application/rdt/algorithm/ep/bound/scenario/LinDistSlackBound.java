package gov.lanl.micot.application.rdt.algorithm.ep.bound.scenario;

import gov.lanl.micot.infrastructure.ep.model.Bus;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.optimize.ConstraintFactory;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.application.rdt.algorithm.ep.variable.scenario.LinDistSlackVariableFactory;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;


/**
 * Bounds on the dist flow slack variables
 * @author Russell Bent
 */
public class LinDistSlackBound implements ConstraintFactory {

  private Scenario scenario = null;

  /**
   * Constraint
   */
  public LinDistSlackBound(Scenario scenario) {
    this.scenario = scenario;
  }
    
  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException {
    LinDistSlackVariableFactory variableFactory = new LinDistSlackVariableFactory(scenario);        
    for (ElectricPowerFlowConnection edge : model.getFlowConnections()) {
      if (variableFactory.hasVariable(edge)) {
        Bus bus1 = model.getFirstNode(edge).getBus();
        Bus bus2 = model.getSecondNode(edge).getBus();

        double vmin = Math.min(bus1.getMinimumVoltagePU(), bus2.getMinimumVoltagePU());
        double vmax = Math.max(bus1.getMaximumVoltagePU(), bus2.getMaximumVoltagePU());
        double maxdiff = ((vmax*vmax) - (vmin*vmin));

        if ((edge.getAttribute(ElectricPowerFlowConnection.HAS_PHASE_A_KEY, Boolean.class))) {
          Variable sa_s = variableFactory.getVariable(problem, edge, LinDistSlackVariableFactory.PHASE_A);
          problem.addBounds(sa_s, -maxdiff, maxdiff);
        }
        if ((edge.getAttribute(ElectricPowerFlowConnection.HAS_PHASE_B_KEY, Boolean.class))) {
          Variable sb_s = variableFactory.getVariable(problem, edge, LinDistSlackVariableFactory.PHASE_B);
          problem.addBounds(sb_s, -maxdiff, maxdiff);
        }
        if ((edge.getAttribute(ElectricPowerFlowConnection.HAS_PHASE_C_KEY, Boolean.class))) {
          Variable sc_s = variableFactory.getVariable(problem, edge, LinDistSlackVariableFactory.PHASE_C);        
          problem.addBounds(sc_s, -maxdiff, maxdiff);
        }
      }      
    }
  }

  
  
}
