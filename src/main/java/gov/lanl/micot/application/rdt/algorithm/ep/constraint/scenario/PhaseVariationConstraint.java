package gov.lanl.micot.application.rdt.algorithm.ep.constraint.scenario;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.Transformer;
import gov.lanl.micot.infrastructure.ep.optimize.ConstraintFactory;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.application.rdt.algorithm.ep.variable.scenario.LineFlowVariableFactory;
import gov.lanl.micot.util.math.solver.LinearConstraint;
import gov.lanl.micot.util.math.solver.LinearConstraintGreaterEq;
import gov.lanl.micot.util.math.solver.LinearConstraintLessEq;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.InvalidConstraintException;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

/**
 * Limit the variations between phase on a line to be small than some constant
 * 
 * This is constraint 4 in the AAAI 2015 paper
 * 
 * @author Russell Bent
 */
public class PhaseVariationConstraint implements ConstraintFactory {

  private double threshold = 0;
  private Scenario scenario = null;
  
  /**
   * Constraint
   */
  public PhaseVariationConstraint(double threshold, Scenario scenario) {
    this.scenario = scenario;
    this.threshold = threshold;
  }

  /**
   * Get the flow constraint name
   * 
   * @param edge
   * @return
   */
  private String getRealFlowLessThanConstraintName(ElectricPowerFlowConnection edge, String phase) {
    return "FlowPhaseL(p)" + edge.toString() + "_" + scenario + "." + phase + "";
  }

  /**
   * Get the flow constraint name
   * 
   * @param edge
   * @return
   */
  private String getRealFlowGreaterThanConstraintName(ElectricPowerFlowConnection edge, String phase) {
    return "FlowSwitchG(p)" + edge.toString() + "_" + scenario + "." + phase + "";
  }
  
  /**
   * Get the flow constraint name
   * 
   * @param edge
   * @return
   */
  private String getReactiveFlowLessThanConstraintName(ElectricPowerFlowConnection edge, String phase) {
    return "FlowPhaseL(q)" + edge.toString() + "_" + scenario + "." + phase + "";
  }

  /**
   * Get the flow constraint name
   * 
   * @param edge
   * @return
   */
  private String getReactiveFlowGreaterThanConstraintName(ElectricPowerFlowConnection edge, String phase) {
    return "FlowSwitchG(q)" + edge.toString() + "_" + scenario + "." + phase + "";
  }


  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException, InvalidConstraintException {
    LineFlowVariableFactory flowFactory = new LineFlowVariableFactory(scenario);

    for (Transformer link : model.getTransformers()) {
      double numberOfPhases = (double) link.getAttribute(ElectricPowerFlowConnection.NUMBER_OF_PHASES_KEY, Integer.class);
      if (numberOfPhases <= 1) {
        continue;
      }

      Variable fp_a = flowFactory.getRealVariable(problem, link, LineFlowVariableFactory.PHASE_A);
      Variable fp_b = flowFactory.getRealVariable(problem, link, LineFlowVariableFactory.PHASE_B);
      Variable fp_c = flowFactory.getRealVariable(problem, link, LineFlowVariableFactory.PHASE_C);

      Variable fq_a = flowFactory.getReactiveVariable(problem, link, LineFlowVariableFactory.PHASE_A);
      Variable fq_b = flowFactory.getReactiveVariable(problem, link, LineFlowVariableFactory.PHASE_B);
      Variable fq_c = flowFactory.getReactiveVariable(problem, link, LineFlowVariableFactory.PHASE_C);

      
      // phaseA
      if (fp_a != null) {
        // <=
        LinearConstraint constraintReactiveLE = new LinearConstraintLessEq(getReactiveFlowLessThanConstraintName(link, LineFlowVariableFactory.PHASE_A));
        constraintReactiveLE.setRightHandSide(0);
        constraintReactiveLE.sumVariable(fq_a, 1.0);
        constraintReactiveLE.sumVariable(fq_a, -1.0 / numberOfPhases);
        constraintReactiveLE.sumVariable(fq_a, -threshold / numberOfPhases);
        
        // >=
        LinearConstraint constraintReactiveGE = new LinearConstraintGreaterEq(getReactiveFlowGreaterThanConstraintName(link, LineFlowVariableFactory.PHASE_A));
        constraintReactiveGE.setRightHandSide(0);
        constraintReactiveGE.sumVariable(fq_a, 1.0);
        constraintReactiveGE.sumVariable(fq_a, -1.0 / numberOfPhases);
        constraintReactiveGE.sumVariable(fq_a, threshold / numberOfPhases);
                
        // <=
        LinearConstraint constraintRealLE = new LinearConstraintLessEq(getRealFlowLessThanConstraintName(link, LineFlowVariableFactory.PHASE_A));
        constraintRealLE.setRightHandSide(0);
        constraintRealLE.sumVariable(fp_a, 1.0);
        constraintRealLE.sumVariable(fp_a, -1.0 / numberOfPhases);
        constraintRealLE.sumVariable(fp_a, -threshold / numberOfPhases);
        
        // >=
        LinearConstraint constraintRealGE = new LinearConstraintGreaterEq(getRealFlowGreaterThanConstraintName(link, LineFlowVariableFactory.PHASE_A));
        constraintRealGE.setRightHandSide(0);
        constraintRealGE.sumVariable(fp_a, 1.0);
        constraintRealGE.sumVariable(fp_a, -1.0 / numberOfPhases);
        constraintRealGE.sumVariable(fp_a, threshold / numberOfPhases);

        if (fp_b != null) {
          constraintRealLE.sumVariable(fp_b, -1.0 / numberOfPhases);
          constraintRealLE.sumVariable(fp_b, -threshold / numberOfPhases);
          
          constraintRealGE.sumVariable(fp_b, -1.0 / numberOfPhases);
          constraintRealGE.sumVariable(fp_b, threshold / numberOfPhases);
          
          constraintReactiveLE.sumVariable(fq_b, -1.0 / numberOfPhases);
          constraintReactiveLE.sumVariable(fq_b, -threshold / numberOfPhases);
          
          constraintReactiveGE.sumVariable(fq_b, -1.0 / numberOfPhases);
          constraintReactiveGE.sumVariable(fq_b, threshold / numberOfPhases);

        }

        if (fp_c != null) {
          constraintRealLE.sumVariable(fp_c, -1.0 / numberOfPhases);
          constraintRealLE.sumVariable(fp_c, -threshold / numberOfPhases);
          
          constraintRealGE.sumVariable(fp_c, -1.0 / numberOfPhases);
          constraintRealGE.sumVariable(fp_c, threshold / numberOfPhases);
          
          constraintReactiveLE.sumVariable(fq_c, -1.0 / numberOfPhases);
          constraintReactiveLE.sumVariable(fq_c, -threshold / numberOfPhases);
          
          constraintReactiveGE.sumVariable(fq_c, -1.0 / numberOfPhases);
          constraintReactiveGE.sumVariable(fq_c, threshold / numberOfPhases);

        }
        problem.addLinearConstraint(constraintRealLE);
        problem.addLinearConstraint(constraintRealGE);
        problem.addLinearConstraint(constraintReactiveLE);
        problem.addLinearConstraint(constraintReactiveGE);
      }

      // phaseB
      if (fp_b != null) {
        // <=
        LinearConstraint constraintReactiveLE = new LinearConstraintLessEq(getReactiveFlowLessThanConstraintName(link, LineFlowVariableFactory.PHASE_B));
        constraintReactiveLE.setRightHandSide(0);
        constraintReactiveLE.sumVariable(fq_b, 1.0);
        constraintReactiveLE.sumVariable(fq_b, -1.0 / numberOfPhases);
        constraintReactiveLE.sumVariable(fq_b, -threshold / numberOfPhases);
        
        // >=
        LinearConstraint constraintReactiveGE = new LinearConstraintGreaterEq(getReactiveFlowGreaterThanConstraintName(link, LineFlowVariableFactory.PHASE_B));
        constraintReactiveGE.setRightHandSide(0);
        constraintReactiveGE.sumVariable(fq_b, 1.0);
        constraintReactiveGE.sumVariable(fq_b, -1.0 / numberOfPhases);
        constraintReactiveGE.sumVariable(fq_b, threshold / numberOfPhases);
                
        // <=
        LinearConstraint constraintRealLE = new LinearConstraintLessEq(getRealFlowLessThanConstraintName(link, LineFlowVariableFactory.PHASE_B));
        constraintRealLE.setRightHandSide(0);
        constraintRealLE.sumVariable(fp_b, 1.0);
        constraintRealLE.sumVariable(fp_b, -1.0 / numberOfPhases);
        constraintRealLE.sumVariable(fp_b, -threshold / numberOfPhases);
        
        // >=
        LinearConstraint constraintRealGE = new LinearConstraintGreaterEq(getRealFlowGreaterThanConstraintName(link, LineFlowVariableFactory.PHASE_B));
        constraintRealGE.setRightHandSide(0);
        constraintRealGE.sumVariable(fp_b, 1.0);
        constraintRealGE.sumVariable(fp_b, -1.0 / numberOfPhases);
        constraintRealGE.sumVariable(fp_b, threshold / numberOfPhases);

        if (fp_a != null) {
          constraintRealLE.sumVariable(fp_a, -1.0 / numberOfPhases);
          constraintRealLE.sumVariable(fp_a, -threshold / numberOfPhases);
          
          constraintRealGE.sumVariable(fp_a, -1.0 / numberOfPhases);
          constraintRealGE.sumVariable(fp_a, threshold / numberOfPhases);
          
          constraintReactiveLE.sumVariable(fq_a, -1.0 / numberOfPhases);
          constraintReactiveLE.sumVariable(fq_a, -threshold / numberOfPhases);
          
          constraintReactiveGE.sumVariable(fq_a, -1.0 / numberOfPhases);
          constraintReactiveGE.sumVariable(fq_a, threshold / numberOfPhases);
        }

        if (fp_c != null) {
          constraintRealLE.sumVariable(fp_c, -1.0 / numberOfPhases);
          constraintRealLE.sumVariable(fp_c, -threshold / numberOfPhases);
          
          constraintRealGE.sumVariable(fp_c, -1.0 / numberOfPhases);
          constraintRealGE.sumVariable(fp_c, threshold / numberOfPhases);       
          
          constraintReactiveLE.sumVariable(fq_c, -1.0 / numberOfPhases);
          constraintReactiveLE.sumVariable(fq_c, -threshold / numberOfPhases);
          
          constraintReactiveGE.sumVariable(fq_c, -1.0 / numberOfPhases);
          constraintReactiveGE.sumVariable(fq_c, threshold / numberOfPhases);       

        }
        problem.addLinearConstraint(constraintRealLE);
        problem.addLinearConstraint(constraintRealGE);
        problem.addLinearConstraint(constraintReactiveLE);
        problem.addLinearConstraint(constraintReactiveGE);
      }

      // phaseC
      if (fp_c != null) {
        // <=
        LinearConstraint constraintReactiveLE = new LinearConstraintLessEq(getReactiveFlowLessThanConstraintName(link, LineFlowVariableFactory.PHASE_C));
        constraintReactiveLE.setRightHandSide(0);
        constraintReactiveLE.sumVariable(fq_c, 1.0);
        constraintReactiveLE.sumVariable(fq_c, -1.0 / numberOfPhases);
        constraintReactiveLE.sumVariable(fq_c, -threshold / numberOfPhases);
        
        // >=
        LinearConstraint constraintReactiveGE = new LinearConstraintGreaterEq(getReactiveFlowGreaterThanConstraintName(link, LineFlowVariableFactory.PHASE_C));
        constraintReactiveGE.setRightHandSide(0);
        constraintReactiveGE.sumVariable(fq_c, 1.0);
        constraintReactiveGE.sumVariable(fq_c, -1.0 / numberOfPhases);
        constraintReactiveGE.sumVariable(fq_c, threshold / numberOfPhases);
        
        // <=
        LinearConstraint constraintRealLE = new LinearConstraintLessEq(getRealFlowLessThanConstraintName(link, LineFlowVariableFactory.PHASE_C));
        constraintRealLE.setRightHandSide(0);
        constraintRealLE.sumVariable(fp_c, 1.0);
        constraintRealLE.sumVariable(fp_c, -1.0 / numberOfPhases);
        constraintRealLE.sumVariable(fp_c, -threshold / numberOfPhases);
        
        // >=
        LinearConstraint constraintRealGE = new LinearConstraintGreaterEq(getRealFlowGreaterThanConstraintName(link, LineFlowVariableFactory.PHASE_C));
        constraintRealGE.setRightHandSide(0);
        constraintRealGE.sumVariable(fp_c, 1.0);
        constraintRealGE.sumVariable(fp_c, -1.0 / numberOfPhases);
        constraintRealGE.sumVariable(fp_c, threshold / numberOfPhases);

        if (fp_b != null) {
          constraintRealLE.sumVariable(fp_b, -1.0 / numberOfPhases);
          constraintRealLE.sumVariable(fp_b, -threshold / numberOfPhases);
          
          constraintRealGE.sumVariable(fp_b, -1.0 / numberOfPhases);
          constraintRealGE.sumVariable(fp_b, threshold / numberOfPhases);
          
          constraintReactiveLE.sumVariable(fq_b, -1.0 / numberOfPhases);
          constraintReactiveLE.sumVariable(fq_b, -threshold / numberOfPhases);
          
          constraintReactiveGE.sumVariable(fq_b, -1.0 / numberOfPhases);
          constraintReactiveGE.sumVariable(fq_b, threshold / numberOfPhases);

        }

        if (fp_a != null) {
          constraintRealLE.sumVariable(fp_a, -1.0 / numberOfPhases);
          constraintRealLE.sumVariable(fp_a, -threshold / numberOfPhases);
          
          constraintRealGE.sumVariable(fp_a, -1.0 / numberOfPhases);
          constraintRealGE.sumVariable(fp_a, threshold / numberOfPhases);
          
          constraintReactiveLE.sumVariable(fq_a, -1.0 / numberOfPhases);
          constraintReactiveLE.sumVariable(fq_a, -threshold / numberOfPhases);
          
          constraintReactiveGE.sumVariable(fq_a, -1.0 / numberOfPhases);
          constraintReactiveGE.sumVariable(fq_a, threshold / numberOfPhases);

        }
        problem.addLinearConstraint(constraintRealLE);
        problem.addLinearConstraint(constraintRealGE);
        
        problem.addLinearConstraint(constraintReactiveLE);
        problem.addLinearConstraint(constraintReactiveGE);

      }
    }
  }

  
}
