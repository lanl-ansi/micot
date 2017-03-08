package gov.lanl.micot.infrastructure.naturalgas.optimize.ogf.constraint.scenario;

import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.naturalgas.model.Compressor;
import gov.lanl.micot.infrastructure.naturalgas.model.ControlValve;
import gov.lanl.micot.infrastructure.naturalgas.model.Junction;
import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasConnection;
import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasModel;
import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasNode;
import gov.lanl.micot.infrastructure.naturalgas.optimize.ConstraintFactory;
import gov.lanl.micot.infrastructure.naturalgas.optimize.ogf.variable.scenario.FlowDirectionScenarioVariableFactory;
import gov.lanl.micot.infrastructure.naturalgas.optimize.ogf.variable.scenario.PressureScenarioVariableFactory;
import gov.lanl.micot.util.math.solver.LinearConstraint;
import gov.lanl.micot.util.math.solver.LinearConstraintGreaterEq;
import gov.lanl.micot.util.math.solver.LinearConstraintLessEq;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.InvalidConstraintException;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

/**
 * Constrains the pressure changes along a compressor to be with its compression ratios assumes flow is compressed in either direction
 * 
 * Takes the form of 4 constraints for compressor i,j
 * 
 * pi_j <= alpha_max * pi_i + (1 - beta_(ij)) * pi_j^+ (1)              ("M" now replaced with pi_j -alpha_max * p_i)
 * 
 * pi_j >= alpha_min * pi_i - (1 - beta_(ij)) * pi_i^- * alpha_min (2)  ("M" now replaced with alpha_min * p_i - p_j)
 * 
 * pi_i <= alpha_max * pi_j + (1 - beta_(ji)) * pi_i^+ (3)              ("M" now replaced with P_i - alpha_max * p_j)
 * 
 * pi_i >= alpha_min * pi_j - (1 - beta_(ji)) * pi_j^- * alpha_min (4)  ("M" now replaced with alpha_min * p_j - p_i)
 * 
 * @author Conrado, extending Russell's factory
 * 
 */

public class CompressorRatioLinearizationConstraint implements ConstraintFactory {

  private Collection<Scenario> scenarios = new ArrayList<Scenario>();

  // Constructor: @param scenario collection
  public CompressorRatioLinearizationConstraint(Collection<Scenario> scenarios) {
    this.scenarios = scenarios;
  }

  /**
   * Get constraint 1
   * @param compressor
   * @return
   */
  private String getConstraint1Name(NaturalGasConnection compressor) {
    return "CompressorLinearization-" + compressor + ".1";
  }

  /**
   * Get constraint 2
   * @param compressor
   * @return
   */
  private String getConstraint2Name(NaturalGasConnection compressor) {
    return "CompressorLinearization-" + compressor + ".2";
  }

  /**
   * Get constraint 3
   * @param compressor
   * @return
   */
  private String getConstraint3Name(NaturalGasConnection compressor) {
    return "CompressorLinearization-" + compressor + ".3";
  }

  /**
   * Get constraint 4
   * @param compressor
   * @return
   */
  private String getConstraint4Name(NaturalGasConnection compressor) {
    return "CompressorLinearization-" + compressor + ".4";
  }

  // Constraint
  @Override
  public void constructConstraint(MathematicalProgram problem, NaturalGasModel model) throws VariableExistsException, NoVariableException, InvalidConstraintException {
    // Step 1: Instantiate all the variables being used in this constraint
    PressureScenarioVariableFactory pressureFactory = new PressureScenarioVariableFactory(scenarios);
    FlowDirectionScenarioVariableFactory flowFactory = new FlowDirectionScenarioVariableFactory(scenarios);

    HashSet<NaturalGasConnection> connections = new HashSet<NaturalGasConnection>();
    connections.addAll(model.getCompressors());
    
    // Step 2: Add the scenario-based bounds by looping over the set of specific scenarios
    for (Scenario scenario : scenarios)
      for (NaturalGasConnection c : connections) {
        // Get pressure bounds on the compressor arc
        NaturalGasNode ni = model.getFirstNode(c);
        NaturalGasNode nj = model.getSecondNode(c);
        Junction i = ni.getJunction();
        Junction j = nj.getJunction();

        Variable pi = pressureFactory.getVariable(problem, ni, scenario);
        Variable pj = pressureFactory.getVariable(problem, nj, scenario);
        Variable betaij = flowFactory.getVariable(problem, c, scenario, FlowDirectionScenarioVariableFactory.FORWARD_PREFIX);
        Variable betaji = flowFactory.getVariable(problem, c, scenario, FlowDirectionScenarioVariableFactory.REVERSE_PREFIX);
        
        if (betaij == null) {
          continue; // in case the edge is disabled
        }

        double maxC = 0.0;
        double minC = 0.0;

        if (c instanceof Compressor) {
          Compressor cs = (Compressor)c;
          maxC = cs.getMaximumCompressionRatio() * cs.getMaximumCompressionRatio();
          minC = cs.getMinimumCompressionRatio() * cs.getMinimumCompressionRatio();
        }
        else {
          ControlValve cs = (ControlValve)c;
          maxC = cs.getMaximumCompressionRatio() * cs.getMaximumCompressionRatio();
          minC = cs.getMinimumCompressionRatio() * cs.getMinimumCompressionRatio();          
        }
                
        double mini = i.getMinimumPressure() * i.getMinimumPressure();
        double minj = j.getMinimumPressure() * j.getMinimumPressure();
        double maxi = i.getMaximumPressure() * i.getMaximumPressure();
        double maxj = j.getMaximumPressure() * j.getMaximumPressure();

        // pi_j <= alpha_max * pi_i + (1 - beta_(ij)) * max_pi_j^+ (1)              ("M" now replaced with pi_j -alpha_max * min_p_i)
        double m = maxj - maxC* mini;
//        m = maxj;
        LinearConstraint constraint = new LinearConstraintLessEq(getConstraint1Name(c));
        constraint.addVariable(pj, 1.0);
        constraint.addVariable(pi, -maxC);
        constraint.addVariable(betaij, m);
        constraint.setRightHandSide(m);
        problem.addLinearConstraint(constraint);

        // pi_i <= alpha_max * pi_j + (1 - beta_(ji)) * max_pi_i^+ (3)              ("M" now replaced with P_i - alpha_max * min_p_j)
        m = maxi - maxC * minj;
  //      m = maxi;
        constraint = new LinearConstraintLessEq(getConstraint3Name(c));
        constraint.addVariable(pi, 1.0);
        constraint.addVariable(pj, -maxC);
        constraint.addVariable(betaji, m);
        constraint.setRightHandSide(m);
        problem.addLinearConstraint(constraint);

        // pi_j >= alpha_min * pi_i - (1 - beta_(ij)) * max_pi_i^- * alpha_min (2)  ("M" now replaced with alpha_min * p_i - min_p_j)
        m = -(maxi * minC - minj);
        //m = -mini * minC;
        constraint = new LinearConstraintGreaterEq(getConstraint2Name(c));
        constraint.addVariable(pj, 1.0);
        constraint.addVariable(pi, -minC);
        constraint.addVariable(betaij, m);
        constraint.setRightHandSide(m);
        problem.addLinearConstraint(constraint);

        // pi_i >= alpha_min * pi_j - (1 - beta_(ji)) * max_pi_j^- * alpha_min (4)  ("M" now replaced with alpha_min * p_j - min_p_i)
        m = -(maxj * minC - mini);
      ///  m = -minj * minC;        
        constraint = new LinearConstraintGreaterEq(getConstraint4Name(c));
        constraint.addVariable(pi, 1.0);
        constraint.addVariable(pj, -minC);
        constraint.addVariable(betaji, m);
        constraint.setRightHandSide(m);
        problem.addLinearConstraint(constraint);
      }
  }
}
