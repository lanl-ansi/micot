package gov.lanl.micot.infrastructure.naturalgas.optimize.ogf.constraint.scenario.soc;

import gov.lanl.micot.infrastructure.model.FlowConnection;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasConnection;
import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasModel;
import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasNode;
import gov.lanl.micot.infrastructure.naturalgas.optimize.ConstraintFactory;
import gov.lanl.micot.infrastructure.naturalgas.optimize.ogf.variable.scenario.FlowDirectionScenarioVariableFactory;
import gov.lanl.micot.infrastructure.naturalgas.optimize.ogf.variable.scenario.FlowScenarioVariableFactory;
import gov.lanl.micot.infrastructure.naturalgas.optimize.ogf.variable.scenario.PressureScenarioVariableFactory;
import gov.lanl.micot.infrastructure.naturalgas.optimize.ogf.variable.scenario.soc.RelaxedFlowScenarioVariableFactory;
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
 * @author Conrado Borraz - Extending Russell's factories This file adds the SOC-type
 *  Weymouth equation constraints to the GTNEP model
 * using the McCormick Relaxation
 */
public class WeymouthMcCormickScenarioConstraint implements ConstraintFactory {

  private Collection<Scenario> scenarios = new ArrayList<Scenario>();

  private static final String CONSTRAINT1_PREFIX = "1";
  private static final String CONSTRAINT2_PREFIX = "2";
  private static final String CONSTRAINT3_PREFIX = "3";
  private static final String CONSTRAINT4_PREFIX = "4";

  
  // Constructor
  public WeymouthMcCormickScenarioConstraint(Collection<Scenario> scenarios) {
    this.scenarios = scenarios;
  }

  // Get the name for the mass flow balance constraint: @param scenario number, @param node, @return
  public static String getConstraintName(Scenario k, FlowConnection edge, String prefix) {
    return "SOC_MC_WE" + prefix + "." + k.getIndex() + "." + edge.toString();
  }

  @Override
  public void constructConstraint(MathematicalProgram problem, NaturalGasModel model) throws VariableExistsException, NoVariableException, InvalidConstraintException {
    // Step 1: Instantiate all the variables being used in this constraint
    FlowScenarioVariableFactory NG_flowVarFactory = new FlowScenarioVariableFactory(scenarios);
    PressureScenarioVariableFactory NG_pressureVarFactory = new PressureScenarioVariableFactory(scenarios);
    FlowDirectionScenarioVariableFactory NG_flowDirVarFactory = new FlowDirectionScenarioVariableFactory(scenarios);
    RelaxedFlowScenarioVariableFactory NG_SOC_flowVarFactory = new RelaxedFlowScenarioVariableFactory(scenarios);

    // Get the connections where the Weymouth constraint is applied
    HashSet<NaturalGasConnection> weymouthConnections = new HashSet<NaturalGasConnection>();
    weymouthConnections.addAll(model.getPipes());
    weymouthConnections.addAll(model.getResistors());

    for (Scenario scenario : scenarios) {
//      SystemLogger.getSystemLogger().systemLogger.println("\n\n-- Adding SOC-type Weymouth Equation CONSTRAINTS to the MODEL (Scenario [" + scenario.getIndex() + "]): ");
  //    SystemLogger.getSystemLogger().systemLogger.println("\t      \tActual \tDesired\t       \tInL-P  \tOutL-P \tResis. \t Flow  \tFlow-D");
    //  SystemLogger.getSystemLogger().systemLogger.println("\tEdgeID\tStatus \tStatus \t (i,j) \tVarName\tVarName\tFactor \tVarName\tVarName");
      //SystemLogger.getSystemLogger().systemLogger.print("\t------\t-------\t-------\t-------\t-------\t-------\t-------\t-------\t-------");

      // Add the constraint
      // create the non zero entries of the flow matrix
      // LeftHandSide variables: pressure difference minus flow times resistance factor
      for (NaturalGasConnection edge : weymouthConnections) {
        NaturalGasNode firstNode = model.getFirstNode(edge);
        NaturalGasNode secondNode = model.getSecondNode(edge);

        // Note: Technically, minP and maxP may vary at each node, so
        // we should evaluate these parameters for each end node. To be considered.
        
        double ijDiff = firstNode.getJunction().getMaximumPressure() * firstNode.getJunction().getMaximumPressure() - secondNode.getJunction().getMinimumPressure() * secondNode.getJunction().getMinimumPressure();  
        double jiDiff = secondNode.getJunction().getMaximumPressure() * secondNode.getJunction().getMaximumPressure() - firstNode.getJunction().getMinimumPressure() * firstNode.getJunction().getMinimumPressure();  

        // these are tighter constants on the bounds
        double pDiff1 = -ijDiff; //  pmin - pmax;
        double pDiff2 = jiDiff; //pmax - pmin;
        double pDiff3 = ijDiff; //pmax - pmin;
        double pDiff4 = -jiDiff; // pmin - pmax;
        
        //SystemLogger.getSystemLogger().systemLogger.print("\n\t " + edge + "\t" + edge.getActualStatus() + "\t" + edge.getDesiredStatus() + "\t(" + firstNode + "," + secondNode + ")");

        String ConstraintName1 = getConstraintName(scenario, edge, CONSTRAINT1_PREFIX);
        String ConstraintName2 = getConstraintName(scenario, edge, CONSTRAINT2_PREFIX);
        String ConstraintName3 = getConstraintName(scenario, edge, CONSTRAINT3_PREFIX);
        String ConstraintName4 = getConstraintName(scenario, edge, CONSTRAINT4_PREFIX);

        LinearConstraintGreaterEq WE1 = new LinearConstraintGreaterEq(ConstraintName1);
        LinearConstraintGreaterEq WE2 = new LinearConstraintGreaterEq(ConstraintName2);
        LinearConstraintLessEq WE3 = new LinearConstraintLessEq(ConstraintName3);
        LinearConstraintLessEq WE4 = new LinearConstraintLessEq(ConstraintName4);

        // Linearized WEs are based on a SOC approximation
        // Eq. 1: l_ij \geq p_j - p_i + PminDiff * y_ij^+ - PminDiff * y_ij^- + PminDiff
        // Eq. 2: l_ij \geq p_i - p_j + PmaxDiff * y_ij^+ - PmaxDiff * y_ij^- - PmaxDiff
        // Eq. 3: l_ij \leq p_j - p_i + PmaxDiff * y_ij^+ - PmaxDiff * y_ij^- + PmaxDiff
        // Eq. 4: l_ij \leq p_i - p_j + PminDiff * y_ij^+ - PminDiff * y_ij^- + PminDiff

        // Get flow variable
        Variable xij = NG_flowVarFactory.getVariable(problem, edge, scenario);
        double w_hat = 1 / edge.getResistance(); // Inverse of the resistanceFactor

        // Get flow direction variables
        Variable yijPlus = NG_flowDirVarFactory.getVariable(problem, edge, scenario, FlowDirectionScenarioVariableFactory.FORWARD_PREFIX);
        Variable yijMinus = NG_flowDirVarFactory.getVariable(problem, edge, scenario, FlowDirectionScenarioVariableFactory.REVERSE_PREFIX);

        // Get SOC-flow variable
        Variable lij = NG_SOC_flowVarFactory.getVariable(problem, edge, scenario);

        // Get pressure variables
        Variable pi = NG_pressureVarFactory.getVariable(problem, firstNode, scenario);
        Variable pj = NG_pressureVarFactory.getVariable(problem, secondNode, scenario);

        // Eq. 1: l_ij - p_j + p_i - PminDiff * y_ij^+ + PminDiff * y_ij^- \geq + PminDiff
        // Eq. 2: l_ij - p_i + p_j - PmaxDiff * y_ij^+ + PmaxDiff * y_ij^- \geq - PmaxDiff
        // Eq. 3: l_ij - p_j + p_i - PmaxDiff * y_ij^+ + PmaxDiff * y_ij^- \leq + PmaxDiff
        // Eq. 4: l_ij - p_i + p_j - PminDiff * y_ij^+ + PminDiff * y_ij^- \leq - PminDiff

        // 1st term
        WE1.addVariable(lij, 1.0);
        WE2.addVariable(lij, 1.0);
        WE3.addVariable(lij, 1.0);
        WE4.addVariable(lij, 1.0);

        // 2nd term
        WE1.addVariable(pj, -1.0);
        WE2.addVariable(pi, -1.0);
        WE3.addVariable(pj, -1.0);
        WE4.addVariable(pi, -1.0);

        // 3rd term
        WE1.addVariable(pi, 1.0);
        WE2.addVariable(pj, 1.0);
        WE3.addVariable(pi, 1.0);
        WE4.addVariable(pj, 1.0);

        // 4th term
        WE1.addVariable(yijPlus, -pDiff1);
        WE2.addVariable(yijPlus, -pDiff2);
        WE3.addVariable(yijPlus, -pDiff3);
        WE4.addVariable(yijPlus, -pDiff4);

        // 5th term
        WE1.addVariable(yijMinus, pDiff1);
        WE2.addVariable(yijMinus, pDiff2);
        WE3.addVariable(yijMinus, pDiff3);
        WE4.addVariable(yijMinus, pDiff4);

        // Set right hand side
        WE1.setRightHandSide(pDiff1);
        WE2.setRightHandSide(-pDiff2);
        WE3.setRightHandSide(pDiff3);
        WE4.setRightHandSide(-pDiff4);

//        SystemLogger.getSystemLogger().systemLogger.print("\t" + pi + "\t" + pj + "\t" + w_hat + "\t" + xij + "\t" + yijPlus + "\t" + yijMinus + "\t" + lij);
        problem.addLinearConstraint(WE1);
        problem.addLinearConstraint(WE2);
        problem.addLinearConstraint(WE3);
        problem.addLinearConstraint(WE4);
      }
    }
  }
}
