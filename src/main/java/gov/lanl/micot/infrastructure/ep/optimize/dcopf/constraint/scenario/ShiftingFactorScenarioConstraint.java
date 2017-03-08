package gov.lanl.micot.infrastructure.ep.optimize.dcopf.constraint.scenario;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.ep.model.Generator;
import gov.lanl.micot.infrastructure.ep.model.Load;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.constraint.ShiftingFactorConstraint;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.variable.GeneratorVariableFactory;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.variable.LoadShedVariableFactory;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.variable.LoadVariableFactory;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.variable.scenario.FlowScenarioVariableFactory;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.util.collection.Matrix;
import gov.lanl.micot.util.math.matrix.MatrixMath;
import gov.lanl.micot.util.math.matrix.MatrixMathFactory;
import gov.lanl.micot.util.math.solver.LinearConstraint;
import gov.lanl.micot.util.math.solver.LinearConstraintEquals;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.InvalidConstraintException;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * The shifting factor constraint, based on flow variables and scenarios
 * 
 * @author Russell Bent
 */
public class ShiftingFactorScenarioConstraint extends ShiftingFactorConstraint {
  
  private Map<Scenario,Matrix<Number>> admittances = null; 
  private Map<Scenario,Matrix<Number>> invertedAdmittances = null;
  private Map<Scenario,Matrix<Number>> hessians = null;    
  private Map<Scenario,Matrix<Number>> shiftingFactors = null;
  private Map<Scenario,Matrix<Number>> generatorIncidences = null;
  private Map<Scenario,Matrix<Number>> loadIncidences = null;
  
  private Map<Scenario,Matrix<Number>> generatorCoeffs = null;
  private Map<Scenario,Matrix<Number>> loadCoeffs =  null;
  
  private Map<Scenario,Map<ElectricPowerNode, Integer>> nodeIndicies = null;
  private Map<Scenario, Map<Generator, Integer>> generatorIndicies = null;
  private Map<Scenario,Map<Load, Integer>> loadIndicies = null;
  private Map<Scenario,Map<ElectricPowerFlowConnection, Integer>> edgeIndicies = null;
    
  private Collection<Scenario> scenarios = null;
  
  /**
   * Constraint
   */
  public ShiftingFactorScenarioConstraint(Collection<Scenario> scenarios) {
    super();
    this.scenarios = scenarios;
    
    admittances = new HashMap<Scenario,Matrix<Number>>(); 
    invertedAdmittances = new HashMap<Scenario,Matrix<Number>>();
    hessians = new HashMap<Scenario,Matrix<Number>>();    
    shiftingFactors = new HashMap<Scenario,Matrix<Number>>();
    generatorIncidences = new HashMap<Scenario,Matrix<Number>>();
    loadIncidences = new HashMap<Scenario,Matrix<Number>>();
    
    generatorCoeffs = new HashMap<Scenario,Matrix<Number>>();
    loadCoeffs =  new HashMap<Scenario,Matrix<Number>>();
    
    nodeIndicies = new HashMap<Scenario,Map<ElectricPowerNode, Integer>>();
    generatorIndicies =  new HashMap<Scenario, Map<Generator, Integer>>();
    loadIndicies = new HashMap<Scenario,Map<Load, Integer>>();
    edgeIndicies = new HashMap<Scenario,Map<ElectricPowerFlowConnection, Integer>>();
    
  }

  /**
   * Function for create the variable name associated with a load
   * 
   * @param node
   * @return
   */
  private String getShiftFactorConstraintName(ElectricPowerConnection edge, Scenario k) {
    return "SF-" + edge.toString() + "-" + k.getIndex();
  }

  /**
   * Construct all the book keeping information
   * @param nodes
   * @param model
   */
  public void constructMatrices(Collection<ElectricPowerNode> nodes, Scenario scenario, ElectricPowerModel m) {
//    ElectricPowerModel model = (ElectricPowerModel) scenario.getModel();
    
    Map<ElectricPowerFlowConnection,Boolean> status = new HashMap<ElectricPowerFlowConnection, Boolean>();
    for (ElectricPowerFlowConnection connection : m.getFlowConnections()) {
      status.put(connection, scenario.computeActualStatus(connection, true));
    }
    
    MatrixMathFactory matrixFactory = MatrixMathFactory.getInstance();
    MatrixMath math = matrixFactory.constructMatrixMath();
    
    nodeIndicies.put(scenario,createNodeIndicies(nodes));
    generatorIndicies.put(scenario,createGeneratorIndicies(nodeIndicies.get(scenario)));
    loadIndicies.put(scenario,createLoadIndicies(nodeIndicies.get(scenario)));
    edgeIndicies.put(scenario,createEdgeIndicies(nodes, m, status));
    
    // nothing to do
    if (edgeIndicies.size() == 0) {
      return;
    }
    
    admittances.put(scenario, createAdmittanceMatrix(m, nodeIndicies.get(scenario), edgeIndicies.get(scenario))); 
    invertedAdmittances.put(scenario, createInvertedAdmittanceMatrix(admittances.get(scenario)));
    hessians.put(scenario,createHessianMatrix(m, nodeIndicies.get(scenario), edgeIndicies.get(scenario)));    
    shiftingFactors.put(scenario, createShiftingFactorMatrix(hessians.get(scenario), invertedAdmittances.get(scenario)));
    generatorIncidences.put(scenario,createGeneratorIncidenceMatrix(nodeIndicies.get(scenario), generatorIndicies.get(scenario)));
    loadIncidences.put(scenario, createLoadIncidenceMatrix(nodeIndicies.get(scenario), loadIndicies.get(scenario)));
    
    generatorCoeffs.put(scenario,math.multiply(shiftingFactors.get(scenario), generatorIncidences.get(scenario)));
    loadCoeffs.put(scenario,math.multiply(shiftingFactors.get(scenario), loadIncidences.get(scenario)));
  }
  
  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel m) throws VariableExistsException,
      NoVariableException, InvalidConstraintException {
    for (Scenario scenario : scenarios) {
      constructMatrices(m.getNodes(),scenario, m);
    }

    for (Scenario scenario : scenarios) {
      for (ElectricPowerFlowConnection edge : edgeIndicies.get(scenario).keySet()) {
        LinearConstraint constraint = constructEdgeConstraint(problem, edge, scenario, m);
        problem.addLinearConstraint(constraint);    
      }
    }
  }

  /**
   * Construct a constraint for a single edge
   * @param problem
   * @param model
   * @param edge
   * @throws NoVariableException
   */
  public LinearConstraint constructEdgeConstraint(MathematicalProgram problem, ElectricPowerFlowConnection edge, Scenario scenario, ElectricPowerModel m) throws NoVariableException {
    //ElectricPowerModel model = (ElectricPowerModel) scenario.getModel();
    double mva = m.getMVABase();
    GeneratorVariableFactory generatorVariableFactory = new GeneratorVariableFactory();
    LoadShedVariableFactory loadShedVariableFactory = new LoadShedVariableFactory();
    LoadVariableFactory loadVariableFactory = new LoadVariableFactory();    
    FlowScenarioVariableFactory flowVariableFactory = new FlowScenarioVariableFactory(scenarios);

    if (flowVariableFactory.getVariable(problem, edge, scenario) == null) {
      return null;
    }
    
    if (admittances.get(scenario) == null) {
      constructMatrices(m.getNodes(), scenario, m);
    }
        
    LinearConstraint constraint = new LinearConstraintEquals(getShiftFactorConstraintName(edge, scenario));
    constraint.setRightHandSide(0);   
    constraint.addVariable(flowVariableFactory.getVariable(problem, edge, scenario), -1.0);
    
    for (Generator generator : generatorIndicies.get(scenario).keySet()) {
      constraint.addVariable(generatorVariableFactory.getVariable(problem, generator), generatorCoeffs.get(scenario).get(edgeIndicies.get(scenario).get(edge), generatorIndicies.get(scenario).get(generator)).doubleValue());        
    }
    
    for (Load load : loadIndicies.get(scenario).keySet()) {
      Variable loadVariable = loadShedVariableFactory.getVariable(problem, load);
      if (loadVariable != null) {
        constraint.addVariable(loadVariable, loadCoeffs.get(scenario).get(edgeIndicies.get(scenario).get(edge), loadIndicies.get(scenario).get(load)).doubleValue());        
        constraint.setRightHandSide(constraint.getRightHandSide() + loadCoeffs.get(scenario).get(edgeIndicies.get(scenario).get(edge), loadIndicies.get(scenario).get(load)).doubleValue() * (load.getDesiredRealLoad().doubleValue() / mva));
      }
      else {
        loadVariable = loadVariableFactory.getVariable(problem, load);
        if (loadVariable != null) {
          constraint.addVariable(loadVariable, -loadCoeffs.get(scenario).get(edgeIndicies.get(scenario).get(edge), loadIndicies.get(scenario).get(load)).doubleValue());
        }
        else {
          constraint.setRightHandSide(constraint.getRightHandSide() + loadCoeffs.get(scenario).get(edgeIndicies.get(scenario).get(edge), loadIndicies.get(scenario).get(load)).doubleValue() * (load.getDesiredRealLoad().doubleValue() / mva));
        }
      }
    }      
    return constraint;
  }
  
  /**
   * Get the flow balance constraint
   * 
   * @param node
   * @return
   */
  public LinearConstraint getShiftFactorConstraint(MathematicalProgram problem, ElectricPowerFlowConnection edge, Scenario scenario) {
    return problem.getLinearConstraint(getShiftFactorConstraintName(edge, scenario));
  }

}
