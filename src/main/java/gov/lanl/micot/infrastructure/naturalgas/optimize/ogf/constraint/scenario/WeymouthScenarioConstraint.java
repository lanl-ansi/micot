package gov.lanl.micot.infrastructure.naturalgas.optimize.ogf.constraint.scenario;

import gov.lanl.micot.infrastructure.model.FlowConnection;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.naturalgas.model.CityGate;
import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasConnection;
import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasModel;
import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasNode;
import gov.lanl.micot.infrastructure.naturalgas.optimize.ConstraintFactory;
import gov.lanl.micot.infrastructure.naturalgas.optimize.ogf.constraint.ConstraintUtility;
import gov.lanl.micot.infrastructure.naturalgas.optimize.ogf.variable.scenario.FlowDirectionScenarioVariableFactory;
import gov.lanl.micot.infrastructure.naturalgas.optimize.ogf.variable.scenario.FlowScenarioVariableFactory;
import gov.lanl.micot.infrastructure.naturalgas.optimize.ogf.variable.scenario.PressureScenarioVariableFactory;
import gov.lanl.micot.util.math.solver.QuadraticConstraint;
import gov.lanl.micot.util.math.solver.QuadraticConstraintGreaterEq;
import gov.lanl.micot.util.math.solver.QuadraticConstraintLessEq;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.InvalidConstraintException;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * This file adds the Weymouth equation constraints to the GTNEP model -- 
 * GTNEP = Natural Gas Transmission Network Expansion Planning
 * 
 * It uses the direction variables to turn the directionality of the Weymouth
 * Equations on and off
 * 
 * 
 * 
 * 
 * @author Conrado Borraz - Extending Russell's factories
 */
public class WeymouthScenarioConstraint implements ConstraintFactory {

  private Collection<Scenario> scenarios = new ArrayList<Scenario>();

  // Constructor
  public WeymouthScenarioConstraint(Collection<Scenario> scenarios) {
    this.scenarios = scenarios;
  }

  // Get the name for the mass flow balance constraint: @param scenario number, @param node, @return
  private String getConstraintName1(Scenario k, FlowConnection edge) {
    return "WE1" + "." + k.getIndex() + "." + edge.toString();
  }

  // Get the name for the mass flow balance constraint: @param scenario number, @param node, @return
  private String getConstraintName2(Scenario k, FlowConnection edge) {
    return "WE2" + "." + k.getIndex() + "." + edge.toString();
  }

  // Get the name for the mass flow balance constraint: @param scenario number, @param node, @return
  private String getConstraintName3(Scenario k, FlowConnection edge) {
    return "WE3" + "." + k.getIndex() + "." + edge.toString();
  }

  // Get the name for the mass flow balance constraint: @param scenario number, @param node, @return
  private String getConstraintName4(Scenario k, FlowConnection edge) {
    return "WE4" + "." + k.getIndex() + "." + edge.toString();
  }

  @Override
  public void constructConstraint(MathematicalProgram problem, NaturalGasModel model) throws VariableExistsException, NoVariableException, InvalidConstraintException {
    // Step 1: Instantiate all the variables being used in this constraint
    FlowScenarioVariableFactory NG_flowVarFactory = new FlowScenarioVariableFactory(scenarios);
    
    Set<NaturalGasConnection> connections = new HashSet<NaturalGasConnection>();
    connections.addAll(model.getPipes());
    connections.addAll(model.getResistors());

    double totalGasDemand = 0.0;
    for (CityGate n1: model.getCityGates()) {
      totalGasDemand += n1.getMaximumConsumption().doubleValue();
    }
    
    for (Scenario scenario : scenarios) {
//      SystemLogger.getSystemLogger().systemLogger.println("\n\n-- Adding Weymouth Equation-type CONSTRAINTS to the MODEL (Scenario [" + scenario.getIndex() + "]): " + "\n\t      \tActual \tDesired\t       \tInL-P  \tOutL-P \tResis. \t Flow  \tFlow-D" + "\n\tEdgeID\tStatus \tStatus \t (i,j) \tVarName\tVarName\tFactor \tVarName\tVarName" + "\n\t------\t-------\t-------\t-------\t-------\t-------\t-------\t-------\t-------");

      for (NaturalGasConnection edge : connections) {
        NaturalGasNode firstNode = model.getFirstNode(edge);
        NaturalGasNode secondNode = model.getSecondNode(edge);

  //      SystemLogger.getSystemLogger().systemLogger.print("\n\t " + edge + "\t" + edge.getActualStatus() + "\t" + edge.getDesiredStatus() + "\t(" + firstNode + "," + secondNode + ")");

        // Resistance factor and flow variable for current pipe
        Variable Xij = NG_flowVarFactory.getVariable(problem, edge, scenario);
        
        if (Xij == null) {
          continue; // deactivated edge
        }

        double maxFlow = totalGasDemand * totalGasDemand;

        
//        double ijM = ConstraintUtility.computeflowUpperBound(model, edge, totalGasDemand);
  //      double jiM = -ConstraintUtility.computeflowLowerBound(model, edge, totalGasDemand);
        
    //    ijM = ijM * ijM;
     //   jiM = jiM * jiM;
        
        QuadraticConstraint WE1 = constructConstraint1(scenario, edge, model, problem, maxFlow /*ijM*/);
        problem.addQuadraticConstraint(WE1);
        
        QuadraticConstraint WE2 = constructConstraint2(scenario, edge, model, problem, maxFlow /*ijM*/);
        problem.addQuadraticConstraint(WE2);

        QuadraticConstraint WE3 = constructConstraint3(scenario, edge, model, problem, maxFlow /*jiM*/);
        problem.addQuadraticConstraint(WE3);

        QuadraticConstraint WE4 = constructConstraint4(scenario, edge, model, problem, maxFlow /*jiM*/);
        problem.addQuadraticConstraint(WE4);

      }
    }
  }

  // w * (pi-pj) >= x_ij^2 - (1-beta_ij)*ub
  protected QuadraticConstraint constructConstraint1(Scenario scenario, NaturalGasConnection edge, NaturalGasModel model, MathematicalProgram problem, double ijM) throws NoVariableException {
    PressureScenarioVariableFactory pFactory = new PressureScenarioVariableFactory(scenarios);
    FlowDirectionScenarioVariableFactory dFactory = new FlowDirectionScenarioVariableFactory(scenarios);
    FlowScenarioVariableFactory fFactory = new FlowScenarioVariableFactory(scenarios);

    Variable xij = fFactory.getVariable(problem, edge, scenario);
    double w = edge.getResistance(); // ResistanceFactor
    Variable pi = pFactory.getVariable(problem, model.getFirstNode(edge), scenario);
    Variable pj = pFactory.getVariable(problem, model.getSecondNode(edge), scenario);
    Variable betaij = dFactory.getVariable(problem, edge, scenario, FlowDirectionScenarioVariableFactory.FORWARD_PREFIX);
    
    String name1 = getConstraintName1(scenario, edge);
    QuadraticConstraint WE = new QuadraticConstraintGreaterEq(name1);
    WE.addVariable(pi, w);
    WE.addVariable(pj, -w);
    WE.addVariables(xij, xij, -1.0);
    WE.addVariable(betaij, -ijM);
    WE.setRightHandSide(-ijM);    
    
    return WE;
  }
  
  // w * (pi-pj) <= x_ij^2 + (1-beta_ij)*ub
  protected QuadraticConstraint constructConstraint2(Scenario scenario, NaturalGasConnection edge, NaturalGasModel model, MathematicalProgram problem, double ijM) throws NoVariableException {
    PressureScenarioVariableFactory pFactory = new PressureScenarioVariableFactory(scenarios);
    FlowDirectionScenarioVariableFactory dFactory = new FlowDirectionScenarioVariableFactory(scenarios);
    FlowScenarioVariableFactory fFactory = new FlowScenarioVariableFactory(scenarios);

    Variable xij = fFactory.getVariable(problem, edge, scenario);
    double w = edge.getResistance(); // ResistanceFactor
    Variable pi = pFactory.getVariable(problem, model.getFirstNode(edge), scenario);
    Variable pj = pFactory.getVariable(problem, model.getSecondNode(edge), scenario);
    Variable betaij = dFactory.getVariable(problem, edge, scenario, FlowDirectionScenarioVariableFactory.FORWARD_PREFIX);
    
    String name = getConstraintName2(scenario, edge);
    QuadraticConstraint WE = new QuadraticConstraintLessEq(name);
    WE.addVariable(pi, w);
    WE.addVariable(pj, -w);
    WE.addVariables(xij, xij, -1.0);
    WE.addVariable(betaij, ijM);
    WE.setRightHandSide(ijM);    
    
    return WE;
  }


  // w * (pj-pi) >= x_ij^2 - (1-beta_ji)*lb
  protected QuadraticConstraint constructConstraint3(Scenario scenario, NaturalGasConnection edge, NaturalGasModel model, MathematicalProgram problem, double jiM) throws NoVariableException {
    PressureScenarioVariableFactory pFactory = new PressureScenarioVariableFactory(scenarios);
    FlowDirectionScenarioVariableFactory dFactory = new FlowDirectionScenarioVariableFactory(scenarios);
    FlowScenarioVariableFactory fFactory = new FlowScenarioVariableFactory(scenarios);

    Variable xij = fFactory.getVariable(problem, edge, scenario);
    double w = edge.getResistance(); // ResistanceFactor
    Variable pi = pFactory.getVariable(problem, model.getFirstNode(edge), scenario);
    Variable pj = pFactory.getVariable(problem, model.getSecondNode(edge), scenario);
    Variable betaji = dFactory.getVariable(problem, edge, scenario, FlowDirectionScenarioVariableFactory.REVERSE_PREFIX);
    
    String name = getConstraintName3(scenario, edge);
    QuadraticConstraint WE = new QuadraticConstraintGreaterEq(name);
    WE.addVariable(pj, w);
    WE.addVariable(pi, -w);
    WE.addVariables(xij, xij, -1.0);
    WE.addVariable(betaji, -jiM);
    WE.setRightHandSide(-jiM);    
    
    return WE;
  }

  // w * (pj-pi) <= x_ij^2 + (1-beta_ji)*lb
  protected QuadraticConstraint constructConstraint4(Scenario scenario, NaturalGasConnection edge, NaturalGasModel model, MathematicalProgram problem, double jiM) throws NoVariableException {
    PressureScenarioVariableFactory pFactory = new PressureScenarioVariableFactory(scenarios);
    FlowDirectionScenarioVariableFactory dFactory = new FlowDirectionScenarioVariableFactory(scenarios);
    FlowScenarioVariableFactory fFactory = new FlowScenarioVariableFactory(scenarios);

    Variable xij = fFactory.getVariable(problem, edge, scenario);
    double w = edge.getResistance(); // ResistanceFactor
    Variable pi = pFactory.getVariable(problem, model.getFirstNode(edge), scenario);
    Variable pj = pFactory.getVariable(problem, model.getSecondNode(edge), scenario);
    Variable betaji = dFactory.getVariable(problem, edge, scenario, FlowDirectionScenarioVariableFactory.REVERSE_PREFIX);
    
    String name = getConstraintName4(scenario, edge);
    QuadraticConstraint WE = new QuadraticConstraintLessEq(name);
    WE.addVariable(pj, w);
    WE.addVariable(pi, -w);
    WE.addVariables(xij, xij, -1.0);
    WE.addVariable(betaji, jiM);
    WE.setRightHandSide(jiM);    
    
    return WE;
  }

  
  
  
}
