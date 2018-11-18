package gov.lanl.micot.application.rdt.algorithm.ep.constraint.scenario;

import gov.lanl.micot.infrastructure.ep.model.Bus;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.ep.model.Generator;
import gov.lanl.micot.infrastructure.ep.model.Load;
import gov.lanl.micot.infrastructure.ep.optimize.ConstraintFactory;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.application.rdt.algorithm.ep.variable.scenario.GeneratorVariableFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.variable.scenario.LineFlowVariableFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.variable.scenario.LoadVariableFactory;
import gov.lanl.micot.util.math.solver.LinearConstraint;
import gov.lanl.micot.util.math.solver.LinearConstraintEquals;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.InvalidConstraintException;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

/**
 * Balances the flows at each bus
 * 
 * @author Russell Bent
 */
public class BusBalanceConstraint implements ConstraintFactory {

  private Scenario scenario = null;
  
  /**
   * Constraint
   */
  public BusBalanceConstraint(Scenario scenario) {
    this.scenario = scenario;
  }

  /**
   * Get the reactive constraint name
   * 
   * @param load
   * @param phase
   * @return
   */
  private String getReactiveName(Bus bus, String phase) {
    return "Balance(q)-" + bus + "_" + scenario + "." + phase;
  }
  
  /**
   * Get the real constraint name
   * 
   * @param load
   * @param phase
   * @return
   */
  private String getRealName(Bus bus, String phase) {
    return "Balance(p)-" + bus + "_" + scenario + "." + phase;
  }

  /**
   * Real phase A constraint
   * @param problem
   * @param model
   * @param bus
   * @throws NoVariableException 
   * @throws InvalidConstraintException 
   */
  private void addRealPhaseAConstraint(MathematicalProgram problem, ElectricPowerModel model, Bus bus) throws NoVariableException, InvalidConstraintException {
    GeneratorVariableFactory generatorVariableFactory = new GeneratorVariableFactory(scenario);
    LoadVariableFactory loadVariableFactory = new LoadVariableFactory(scenario);
    LineFlowVariableFactory flowVariableFactory = new LineFlowVariableFactory(scenario);
    
    ElectricPowerNode node = model.getNode(bus);

    LinearConstraint constraint = new LinearConstraintEquals(getRealName(bus, LineFlowVariableFactory.PHASE_A));
    constraint.setRightHandSide(0);
    for (ElectricPowerFlowConnection edge : model.getFlowEdges(node)) {
      Variable fp_a = flowVariableFactory.getRealVariable(problem, edge, LineFlowVariableFactory.PHASE_A);
      if (fp_a != null) {
        fp_a = flowVariableFactory.getRealVariable(problem, edge, LineFlowVariableFactory.PHASE_A);
        constraint.addVariable(fp_a, node.equals(model.getFirstNode(edge)) ? -1.0 : 1.0);
      }
    }

    for (Load load : node.getComponents(Load.class)) {
      Variable dp_a = loadVariableFactory.getRealVariable(problem, load, LoadVariableFactory.PHASE_A);
      if (dp_a != null) {
        constraint.addVariable(dp_a, -1.0);
      }
    }

    for (Generator generator : node.getComponents(Generator.class)) {
      Variable gp_a = generatorVariableFactory.getRealVariable(problem, generator, GeneratorVariableFactory.PHASE_A);
      if (gp_a != null) {
        constraint.addVariable(gp_a, 1.0);
      }
    }
      
    if (constraint.getNumberOfVariables() > 0) {
      problem.addLinearConstraint(constraint);
    }     
  }
 
  /** 
   * Real phase B constraint
   * @param problem
   * @param model
   * @param bus
   * @throws InvalidConstraintException 
   * @throws NoVariableException 
   */
  private void addRealPhaseBConstraint(MathematicalProgram problem, ElectricPowerModel model, Bus bus) throws InvalidConstraintException, NoVariableException {
    GeneratorVariableFactory generatorVariableFactory = new GeneratorVariableFactory(scenario);
    LoadVariableFactory loadVariableFactory = new LoadVariableFactory(scenario);
    LineFlowVariableFactory flowVariableFactory = new LineFlowVariableFactory(scenario);
    
    ElectricPowerNode node = model.getNode(bus);

    LinearConstraint constraint = new LinearConstraintEquals(getRealName(bus, LineFlowVariableFactory.PHASE_B));
    constraint.setRightHandSide(0);
    for (ElectricPowerFlowConnection edge : model.getFlowEdges(node)) {
      Variable fp_b = flowVariableFactory.getRealVariable(problem, edge, LineFlowVariableFactory.PHASE_B);
      if (fp_b != null) {
        constraint.addVariable(fp_b, node.equals(model.getFirstNode(edge)) ? -1.0 : 1.0);
      }
    }

    for (Load load : node.getComponents(Load.class)) {
      Variable dp_b = loadVariableFactory.getRealVariable(problem, load, LoadVariableFactory.PHASE_B);
      if (dp_b != null) {
        constraint.addVariable(dp_b, -1.0);
      }
    }

    for (Generator generator : node.getComponents(Generator.class)) {
      Variable gp_b = generatorVariableFactory.getRealVariable(problem, generator, GeneratorVariableFactory.PHASE_B);
      if (gp_b != null) {
        constraint.addVariable(gp_b, 1.0);
      }
    }
      
    if (constraint.getNumberOfVariables() > 0) {
      problem.addLinearConstraint(constraint);
    }    
  }
  
  /** 
   * Real phase C constraint
   * @param problem
   * @param model
   * @param bus
   * @throws InvalidConstraintException 
   * @throws NoVariableException 
   */
  private void addRealPhaseCConstraint(MathematicalProgram problem, ElectricPowerModel model, Bus bus) throws InvalidConstraintException, NoVariableException {
    GeneratorVariableFactory generatorVariableFactory = new GeneratorVariableFactory(scenario);
    LoadVariableFactory loadVariableFactory = new LoadVariableFactory(scenario);
    LineFlowVariableFactory flowVariableFactory = new LineFlowVariableFactory(scenario);
    
    ElectricPowerNode node = model.getNode(bus);

    LinearConstraint constraint = new LinearConstraintEquals(getRealName(bus, LineFlowVariableFactory.PHASE_C));
    constraint.setRightHandSide(0);
    for (ElectricPowerFlowConnection edge : model.getFlowEdges(node)) {
      Variable fp_c = flowVariableFactory.getRealVariable(problem, edge, LineFlowVariableFactory.PHASE_C);
      if (fp_c != null) {
        constraint.addVariable(fp_c, node.equals(model.getFirstNode(edge)) ? -1.0 : 1.0);
      }
    }

    for (Load load : node.getComponents(Load.class)) {
      Variable dp_c = loadVariableFactory.getRealVariable(problem, load, LoadVariableFactory.PHASE_C);
      if (dp_c != null) {
        constraint.addVariable(dp_c, -1.0);
      }
    }

    for (Generator generator : node.getComponents(Generator.class)) {
      Variable gp_c = generatorVariableFactory.getRealVariable(problem, generator, GeneratorVariableFactory.PHASE_C);
      if (gp_c != null) {
        constraint.addVariable(gp_c, 1.0);
      }
    }
      
    if (constraint.getNumberOfVariables() > 0) {
      problem.addLinearConstraint(constraint);
    }    
  }  
  
  /**
   * Reactive phase A constraint
   * @param problem
   * @param model
   * @param bus
   * @throws NoVariableException 
   * @throws InvalidConstraintException 
   */
  private void addReactivePhaseAConstraint(MathematicalProgram problem, ElectricPowerModel model, Bus bus) throws NoVariableException, InvalidConstraintException {
    GeneratorVariableFactory generatorVariableFactory = new GeneratorVariableFactory(scenario);
    LoadVariableFactory loadVariableFactory = new LoadVariableFactory(scenario);
    LineFlowVariableFactory flowVariableFactory = new LineFlowVariableFactory(scenario);
    
    ElectricPowerNode node = model.getNode(bus);

    LinearConstraint constraint = new LinearConstraintEquals(getReactiveName(bus, LineFlowVariableFactory.PHASE_A));
    constraint.setRightHandSide(0);
    for (ElectricPowerFlowConnection edge : model.getFlowEdges(node)) {
      Variable fq_a = flowVariableFactory.getReactiveVariable(problem, edge, LineFlowVariableFactory.PHASE_A);
      if (fq_a != null) {
        constraint.addVariable(fq_a, node.equals(model.getFirstNode(edge)) ? -1.0 : 1.0);
      }
    }

    for (Load load : node.getComponents(Load.class)) {
      Variable dq_a = loadVariableFactory.getReactiveVariable(problem, load, LoadVariableFactory.PHASE_A);
      if (dq_a != null) {
        constraint.addVariable(dq_a, -1.0);
      }
    }

    for (Generator generator : node.getComponents(Generator.class)) {
      Variable gq_a = generatorVariableFactory.getReactiveVariable(problem, generator, GeneratorVariableFactory.PHASE_A);
      if (gq_a != null) {
        constraint.addVariable(gq_a, 1.0);
      }
    }
      
    if (constraint.getNumberOfVariables() > 0) {
      problem.addLinearConstraint(constraint);
    }    
  }
  
  /**
   * Reactive phase B constraint
   * @param problem
   * @param model
   * @param bus
   * @throws NoVariableException 
   * @throws InvalidConstraintException 
   */
  private void addReactivePhaseBConstraint(MathematicalProgram problem, ElectricPowerModel model, Bus bus) throws NoVariableException, InvalidConstraintException {
    GeneratorVariableFactory generatorVariableFactory = new GeneratorVariableFactory(scenario);
    LoadVariableFactory loadVariableFactory = new LoadVariableFactory(scenario);
    LineFlowVariableFactory flowVariableFactory = new LineFlowVariableFactory(scenario);
    
    ElectricPowerNode node = model.getNode(bus);

    LinearConstraint constraint = new LinearConstraintEquals(getReactiveName(bus, LineFlowVariableFactory.PHASE_B));
    constraint.setRightHandSide(0);
    for (ElectricPowerFlowConnection edge : model.getFlowEdges(node)) {
      Variable fq_b = flowVariableFactory.getReactiveVariable(problem, edge, LineFlowVariableFactory.PHASE_B);
      if (fq_b != null) {
        constraint.addVariable(fq_b, node.equals(model.getFirstNode(edge)) ? -1.0 : 1.0);
      }
    }

    for (Load load : node.getComponents(Load.class)) {
      Variable dq_b = loadVariableFactory.getReactiveVariable(problem, load, LoadVariableFactory.PHASE_B);
      if (dq_b != null) {
        constraint.addVariable(dq_b, -1.0);
      }
    }

    for (Generator generator : node.getComponents(Generator.class)) {
      Variable gq_b = generatorVariableFactory.getReactiveVariable(problem, generator, GeneratorVariableFactory.PHASE_B);
      if (gq_b != null) {
        constraint.addVariable(gq_b, 1.0);
      }
    }
      
    if (constraint.getNumberOfVariables() > 0) {
      problem.addLinearConstraint(constraint);
    }    
  }
  
  /**
   * Reactive phase C constraint
   * @param problem
   * @param model
   * @param bus
   * @throws NoVariableException 
   * @throws InvalidConstraintException 
   */
  private void addReactivePhaseCConstraint(MathematicalProgram problem, ElectricPowerModel model, Bus bus) throws NoVariableException, InvalidConstraintException {
    GeneratorVariableFactory generatorVariableFactory = new GeneratorVariableFactory(scenario);
    LoadVariableFactory loadVariableFactory = new LoadVariableFactory(scenario);
    LineFlowVariableFactory flowVariableFactory = new LineFlowVariableFactory(scenario);
    
    ElectricPowerNode node = model.getNode(bus);

    LinearConstraint constraint = new LinearConstraintEquals(getReactiveName(bus, LineFlowVariableFactory.PHASE_C));
    constraint.setRightHandSide(0);
    for (ElectricPowerFlowConnection edge : model.getFlowEdges(node)) {
      Variable fq_c = flowVariableFactory.getReactiveVariable(problem, edge, LineFlowVariableFactory.PHASE_C);
      if (fq_c != null) {
        constraint.addVariable(fq_c, node.equals(model.getFirstNode(edge)) ? -1.0 : 1.0);
      }
    }

    for (Load load : node.getComponents(Load.class)) {
      Variable dq_c = loadVariableFactory.getReactiveVariable(problem, load, LoadVariableFactory.PHASE_C);
      if (dq_c != null) {
        constraint.addVariable(dq_c, -1.0);
      }
    }

    for (Generator generator : node.getComponents(Generator.class)) {
      Variable gq_c = generatorVariableFactory.getReactiveVariable(problem, generator, GeneratorVariableFactory.PHASE_C);
      if (gq_c != null) {
        constraint.addVariable(gq_c, 1.0);
      }
    }
      
    if (constraint.getNumberOfVariables() > 0) {
      problem.addLinearConstraint(constraint);
    }    
  }
  
  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException, InvalidConstraintException {

    for (Bus bus : model.getBuses()) {    
      addRealPhaseAConstraint(problem, model, bus);
      addRealPhaseBConstraint(problem, model, bus);
      addRealPhaseCConstraint(problem, model, bus);

      addReactivePhaseAConstraint(problem, model, bus);
      addReactivePhaseBConstraint(problem, model, bus);
      addReactivePhaseCConstraint(problem, model, bus);
    }
  }
  
}
