package gov.lanl.micot.infrastructure.ep.optimize.dcopf.constraint;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.ep.model.Generator;
import gov.lanl.micot.infrastructure.ep.model.Load;
import gov.lanl.micot.infrastructure.ep.optimize.ConstraintFactory;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.variable.FlowVariableFactory;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.variable.GeneratorVariableFactory;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.variable.LoadShedVariableFactory;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.variable.LoadVariableFactory;
import gov.lanl.micot.util.collection.Matrix;
import gov.lanl.micot.util.collection.SparseMatrix;
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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeSet;

/**
 * The shifting factor constraint, based on flow variables
 * 
 * @author Russell Bent
 */
public class ShiftingFactorConstraint implements ConstraintFactory {
  
  private Matrix<Number> admittance = null; 
  private Matrix<Number> invertedAdmittance = null;
  private Matrix<Number> hessian = null;    
  private Matrix<Number> shiftingFactor = null;
  private Matrix<Number> generatorIncidence = null;
  private Matrix<Number> loadIncidence = null;
  
  private Matrix<Number> generatorCoeffs = null;
  private Matrix<Number> loadCoeffs =  null;
  
  private Map<ElectricPowerNode, Integer> nodeIndicies = null;
  private Map<Generator, Integer> generatorIndicies = null;
  private Map<Load, Integer> loadIndicies = null;
  private Map<ElectricPowerFlowConnection, Integer> edgeIndicies = null;
    
  /**
   * Constraint
   */
  public ShiftingFactorConstraint() {
  }

  /**
   * Function for create the variable name associated with a load
   * 
   * @param node
   * @return
   */
  private String getShiftFactorConstraintName(ElectricPowerConnection edge) {
    return "SF-" + edge.toString();
  }

  /**
   * Create the node indicies map
   * @param nodes
   * @return
   */
  protected Map<ElectricPowerNode, Integer> createNodeIndicies(Collection<ElectricPowerNode> nodes) {
    TreeSet<ElectricPowerNode> sortedNodes = new TreeSet<ElectricPowerNode>();
    sortedNodes.addAll(nodes);
    
    HashMap<ElectricPowerNode, Integer> map =  new LinkedHashMap<ElectricPowerNode, Integer>();
    int idx = -1;
    for (ElectricPowerNode node : sortedNodes) {
      map.put(node, idx);
      ++idx;      
    }
    return map;
  }
    
  /**
   * Create the generator indicies map
   * @param nodes
   * @return
   */
  protected Map<Generator, Integer> createGeneratorIndicies(Map<ElectricPowerNode, Integer> nodeIndicies) {
    TreeSet<Generator> sortedGenerators = new TreeSet<Generator>();
    for (ElectricPowerNode node : nodeIndicies.keySet()) {
      if (nodeIndicies.get(node) >= 0) {
        for (Generator generator : node.getComponents(Generator.class)) {
          if (generator.getStatus()) {
            sortedGenerators.add(generator);
          }
        }
      }
    }
        
    HashMap<Generator, Integer> map =  new LinkedHashMap<Generator, Integer>();
    int idx = 0;
    for (Generator generator : sortedGenerators) {
      map.put(generator, idx);
      ++idx;      
    }
    return map;
  }
  
  /**
   * Create the load indicies map
   * @param nodes
   * @return
   */
  protected Map<Load, Integer> createLoadIndicies(Map<ElectricPowerNode, Integer> nodeIndicies) {
    TreeSet<Load> sortedLoads = new TreeSet<Load>();
    for (ElectricPowerNode node : nodeIndicies.keySet()) {
      if (nodeIndicies.get(node) >= 0) {
        for (Load load : node.getComponents(Load.class)) {
          if (load.getStatus()) {
            sortedLoads.add(load);
          }
        }
      }
    }
        
    HashMap<Load, Integer> map =  new LinkedHashMap<Load, Integer>();
    int idx = 0;
    for (Load load : sortedLoads) {
      map.put(load, idx);
      ++idx;      
    }
    return map;
  }
  
  /**
   * Create the edge indicies
   * @param model
   * @return
   */
  protected Map<ElectricPowerFlowConnection, Integer> createEdgeIndicies(Collection<ElectricPowerNode> ns, ElectricPowerModel m, Map<ElectricPowerFlowConnection,Boolean> status) {
    TreeSet<ElectricPowerFlowConnection> sortedEdges = new TreeSet<ElectricPowerFlowConnection>();    
    for (ElectricPowerFlowConnection connection : m.getFlowConnections()) {
      //if (!edge.getActualStatus()) {
      if (!status.get(connection)) {
        continue;
      }
      
      if (ns.contains(m.getFirstNode(connection)) && ns.contains(m.getSecondNode(connection))) {
        sortedEdges.add(connection);
      }      
    }
    HashMap<ElectricPowerFlowConnection, Integer> map = new LinkedHashMap<ElectricPowerFlowConnection, Integer>(); 
    int idx = 0;
    for (ElectricPowerFlowConnection edge : sortedEdges) {
      map.put(edge, idx);
      ++idx;
    }
    return map;    
  }
  
  /**
   * create the admittance matrix
   * @param nodeIndicies
   * @return
   */
  protected Matrix<Number> createAdmittanceMatrix(ElectricPowerModel m, Map<ElectricPowerNode, Integer> nodeIndicies, Map<ElectricPowerFlowConnection, Integer> edgeIndicies) {
    Matrix<Number> susceptance = new SparseMatrix<Number>();
       
    // diagonal of the susceptance matrix
    for (ElectricPowerNode node : nodeIndicies.keySet()) {
      if (nodeIndicies.get(node) < 0) {
        continue;
      }
      
      double value = 0;
      for (ElectricPowerFlowConnection connection : m.getFlowEdges(node)) {
        if (!edgeIndicies.keySet().contains(connection)) {
          continue;
        }
        double impedance = connection.getSusceptance();
        value += impedance;
      }
      susceptance.set(nodeIndicies.get(node), nodeIndicies.get(node), value);
    }

    // create the non zero entries of the phase angle matrix
    for (ElectricPowerFlowConnection connection : edgeIndicies.keySet()) {
      ElectricPowerNode firstNode = m.getFirstNode(connection);
      ElectricPowerNode secondNode = m.getSecondNode(connection);
      double impedance = connection.getSusceptance();

      if (nodeIndicies.get(firstNode) < 0 || nodeIndicies.get(secondNode) < 0) {
        continue;
      }
      
      double value = susceptance.get(nodeIndicies.get(firstNode), nodeIndicies.get(secondNode)) == null ? 0 : susceptance.get(nodeIndicies.get(firstNode), nodeIndicies.get(secondNode)).doubleValue() ; 
      value =  value - impedance;
      susceptance.set(nodeIndicies.get(firstNode), nodeIndicies.get(secondNode), value);
      susceptance.set(nodeIndicies.get(secondNode), nodeIndicies.get(firstNode), value);      
    }
    return susceptance;
  }
  
  /**
   * Invert the admittance matrix
   * @param admittance
   * @return
   */
  protected Matrix<Number> createInvertedAdmittanceMatrix(Matrix<Number> admittance) {
    MatrixMathFactory matrixFactory = MatrixMathFactory.getInstance();
    MatrixMath math = matrixFactory.constructMatrixMath();
    return math.invert(admittance);
  }
  
  /**
   * Create the edge-vertex hessian
   * @param model
   * @param nodeIndicies
   * @param edgeIndicies
   * @return
   */
  protected Matrix<Number> createHessianMatrix(ElectricPowerModel m, Map<ElectricPowerNode, Integer> nodeIndicies, Map<ElectricPowerFlowConnection, Integer> edgeIndicies) {
    Matrix<Number> hessian = new SparseMatrix<Number>();
    for (ElectricPowerFlowConnection connection : edgeIndicies.keySet()) {
      ElectricPowerNode firstNode = m.getFirstNode(connection);
      ElectricPowerNode secondNode = m.getSecondNode(connection);
      double impedance = connection.getSusceptance();

      if (nodeIndicies.get(firstNode) >= 0) {
        hessian.set(edgeIndicies.get(connection), nodeIndicies.get(firstNode), impedance);
      }
      if (nodeIndicies.get(secondNode) >= 0) {
        hessian.set(edgeIndicies.get(connection), nodeIndicies.get(secondNode), -impedance);
      }
    }
    return hessian;
  }
  
  /**
   * Create the shifting factor matrix
   * @param hessian
   * @param admittance
   * @return
   */
  protected Matrix<Number> createShiftingFactorMatrix(Matrix<Number> hessian, Matrix<Number> admittance) {
    MatrixMathFactory matrixFactory = MatrixMathFactory.getInstance();
    MatrixMath math = matrixFactory.constructMatrixMath();
    return math.multiply(hessian, admittance);
  }
  
  /**
   * Create the generator incidience matrix
   * @param nodeIndicies
   * @param generatorIndicies
   * @return
   */
  protected Matrix<Number> createGeneratorIncidenceMatrix(Map<ElectricPowerNode, Integer> nodeIndicies, Map<Generator, Integer> generatorIndicies) {
    Matrix<Number> incidence = new SparseMatrix<Number>(nodeIndicies.size()-1, generatorIndicies.size());
    for (ElectricPowerNode node : nodeIndicies.keySet()) {
      if (nodeIndicies.get(node) >= 0) {
        for (Generator generator : node.getComponents(Generator.class)) {
          if (generator.getStatus()) {
            incidence.set(nodeIndicies.get(node), generatorIndicies.get(generator), 1);
          }
        }
      }
    }    
    return incidence;
  }
  
  /**
   * Create the load incidience matrix
   * @param nodeIndicies
   * @param generatorIndicies
   * @return
   */
  protected Matrix<Number> createLoadIncidenceMatrix(Map<ElectricPowerNode, Integer> nodeIndicies, Map<Load, Integer> loadIndicies) {
    Matrix<Number> incidence = new SparseMatrix<Number>(nodeIndicies.size()-1, loadIndicies.size());
    for (ElectricPowerNode node : nodeIndicies.keySet()) {
      if (nodeIndicies.get(node) >= 0) {
        for (Load load : node.getComponents(Load.class)) {
          if (load.getStatus()) {
            incidence.set(nodeIndicies.get(node), loadIndicies.get(load), 1);
          }
        }
      }
    }    
    return incidence;
  }
  
  /**
   * Construct all the book keeping information
   * @param nodes
   * @param model
   */
  public void constructMatrices(Collection<ElectricPowerNode> nodes, ElectricPowerModel model) {
    MatrixMathFactory matrixFactory = MatrixMathFactory.getInstance();
    MatrixMath math = matrixFactory.constructMatrixMath();

    Map<ElectricPowerFlowConnection,Boolean> status = new HashMap<ElectricPowerFlowConnection, Boolean>();
    for (ElectricPowerFlowConnection connection : model.getFlowConnections()) {
      status.put(connection, connection.getStatus());
    }
    
    nodeIndicies = createNodeIndicies(nodes);
    generatorIndicies = createGeneratorIndicies(nodeIndicies);
    loadIndicies = createLoadIndicies(nodeIndicies);
    edgeIndicies = createEdgeIndicies(nodes, model, status);
    
    // nothing to do
    if (edgeIndicies.size() == 0) {
      return;
    }
    
    admittance = createAdmittanceMatrix(model, nodeIndicies, edgeIndicies); 
    invertedAdmittance = createInvertedAdmittanceMatrix(admittance);
    hessian = createHessianMatrix(model, nodeIndicies, edgeIndicies);    
    shiftingFactor = createShiftingFactorMatrix(hessian, invertedAdmittance);
    generatorIncidence =  createGeneratorIncidenceMatrix(nodeIndicies, generatorIndicies);
    loadIncidence =  createLoadIncidenceMatrix(nodeIndicies, loadIndicies);
    
    generatorCoeffs =  math.multiply(shiftingFactor, generatorIncidence);
    loadCoeffs =  math.multiply(shiftingFactor, loadIncidence);
  }
  
  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException,
      NoVariableException, InvalidConstraintException {
    constructMatrices(model.getNodes(), model);

    for (ElectricPowerFlowConnection edge : edgeIndicies.keySet()) {
      LinearConstraint constraint = constructEdgeConstraint(problem, model, edge);
      problem.addLinearConstraint(constraint);    
    }
  }

  /**
   * Construct a constraint for a single edge
   * @param problem
   * @param model
   * @param edge
   * @throws NoVariableException
   */
  public LinearConstraint constructEdgeConstraint(MathematicalProgram problem, ElectricPowerModel model, ElectricPowerFlowConnection edge) throws NoVariableException {
    double mva = model.getMVABase();
    GeneratorVariableFactory generatorVariableFactory = new GeneratorVariableFactory();
    LoadShedVariableFactory loadShedVariableFactory = new LoadShedVariableFactory();
    LoadVariableFactory loadVariableFactory = new LoadVariableFactory();    
    FlowVariableFactory flowVariableFactory = new FlowVariableFactory();
    
    LinearConstraint constraint = new LinearConstraintEquals(getShiftFactorConstraintName(edge));
    constraint.setRightHandSide(0);   
    constraint.addVariable(flowVariableFactory.getVariable(problem, edge), -1.0);
    
    for (Generator generator : generatorIndicies.keySet()) {
      constraint.addVariable(generatorVariableFactory.getVariable(problem, generator), generatorCoeffs.get(edgeIndicies.get(edge), generatorIndicies.get(generator)).doubleValue());        
    }
    
    for (Load load : loadIndicies.keySet()) {
      Variable loadVariable = loadShedVariableFactory.getVariable(problem, load);
      if (loadVariable != null) {
        constraint.addVariable(loadVariable, loadCoeffs.get(edgeIndicies.get(edge), loadIndicies.get(load)).doubleValue());        
        constraint.setRightHandSide(constraint.getRightHandSide() + loadCoeffs.get(edgeIndicies.get(edge), loadIndicies.get(load)).doubleValue() * (load.getDesiredRealLoad().doubleValue() / mva));
      }
      else {
        loadVariable = loadVariableFactory.getVariable(problem, load); 
        if (loadVariable != null) {
          constraint.addVariable(loadVariable, -loadCoeffs.get(edgeIndicies.get(edge), loadIndicies.get(load)).doubleValue());
        }
        else {
          constraint.setRightHandSide(constraint.getRightHandSide() + loadCoeffs.get(edgeIndicies.get(edge), loadIndicies.get(load)).doubleValue() * (load.getDesiredRealLoad().doubleValue() / mva));
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
  public LinearConstraint getShiftFactorConstraint(MathematicalProgram problem, ElectricPowerFlowConnection edge) {
    return problem.getLinearConstraint(getShiftFactorConstraintName(edge));
  }

}
