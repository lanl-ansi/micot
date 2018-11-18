package gov.lanl.micot.application.rdt.algorithm;

import gov.lanl.micot.application.rdt.algorithm.ep.bound.GeneratorConstructionBound;
import gov.lanl.micot.application.rdt.algorithm.ep.bound.LineConstructionBound;
import gov.lanl.micot.application.rdt.algorithm.ep.bound.LineHardenBound;
import gov.lanl.micot.application.rdt.algorithm.ep.bound.LineSwitchBound;
import gov.lanl.micot.application.rdt.algorithm.ep.bound.scenario.LineActiveBound;
import gov.lanl.micot.application.rdt.algorithm.ep.bound.scenario.LineDirectionBound;
import gov.lanl.micot.application.rdt.algorithm.ep.bound.scenario.LineFlowBound;
import gov.lanl.micot.application.rdt.algorithm.ep.bound.scenario.LoadBound;
import gov.lanl.micot.application.rdt.algorithm.ep.bound.scenario.GeneratorBound;
import gov.lanl.micot.application.rdt.algorithm.ep.bound.scenario.VoltageBound;
import gov.lanl.micot.application.rdt.algorithm.ep.constraint.scenario.BusBalanceConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.constraint.scenario.CriticalLoadConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.constraint.scenario.GeneratorConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.constraint.scenario.LineActiveTieConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.constraint.scenario.LineCapacityConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.constraint.scenario.LineDirectionConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.constraint.scenario.LineHardenExistTieConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.constraint.scenario.LineSwitchExistTieConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.constraint.scenario.NonCriticalLoadConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.objective.GeneratorObjectiveFunctionFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.objective.LineConstructionObjectiveFunctionFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.objective.LineHardenObjectiveFunctionFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.objective.LineSwitchObjectiveFunctionFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.variable.GeneratorConstructionVariableFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.variable.LineConstructionVariableFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.variable.LineHardenVariableFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.variable.LineSwitchVariableFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.variable.scenario.GeneratorVariableFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.variable.scenario.LineActiveVariableFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.variable.scenario.LineDirectionVariableFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.variable.scenario.LineFlowVariableFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.variable.scenario.LoadVariableFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.variable.scenario.VoltageVariableFactory;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.optimize.OptimizerImpl;
import gov.lanl.micot.util.math.solver.exception.InvalidConstraintException;
import gov.lanl.micot.util.math.solver.exception.InvalidVariableException;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;
import java.util.Collection;

/**
 * Full MIP algorithm for solving the reslient design problem
 * @author Russell Bent
 */
public abstract class ResilienceAlgorithm extends OptimizerImpl<ElectricPowerNode, ElectricPowerModel> {
  
  private Collection<Scenario> scenarios = null;
  private double criticalLoadMet = -1;
  private double nonCriticalLoadMet = -1;
  
  /**
   * Constructor
   */
  public ResilienceAlgorithm(Collection<Scenario> scenarios) {
    super();
    setScenarios(scenarios);    
  }

  /**
   * Set the available scenarios that we have
   * @param scenarios
   */
  private void setScenarios(Collection<Scenario> scenarios) {
    this.scenarios = scenarios;
  }
  
  /**
   * Get all the scenarios
   * @return
   */
  protected Collection<Scenario> getScenarios() {
    return scenarios;
  }
  
  /**
   * Add the outer variables to the problem
   * @param model
   * @throws InvalidVariableException 
   * @throws VariableExistsException 
   */
  protected void addOuterVariables(ElectricPowerModel model, MathematicalProgram problem) throws VariableExistsException, InvalidVariableException {
    GeneratorConstructionVariableFactory generatorVariableFactory = new GeneratorConstructionVariableFactory();
    LineConstructionVariableFactory    lineConstructionVariableFactory    = new LineConstructionVariableFactory();
    LineHardenVariableFactory    lineHardenVariableFactory    = new LineHardenVariableFactory();
    LineSwitchVariableFactory    lineSwitchVariableFactory    = new LineSwitchVariableFactory();
    
    generatorVariableFactory.createVariables(problem, model);
    lineConstructionVariableFactory.createVariables(problem, model);
    lineHardenVariableFactory.createVariables(problem, model);
    lineSwitchVariableFactory.createVariables(problem, model);
  }
  
  /**
   * Add the objective function to the problem
   * @throws NoVariableException 
   */
  protected void addOuterObjectiveFunction(ElectricPowerModel model, MathematicalProgram problem) throws NoVariableException {
    GeneratorObjectiveFunctionFactory generatorObjective = new GeneratorObjectiveFunctionFactory();
    LineConstructionObjectiveFunctionFactory lineObjective = new LineConstructionObjectiveFunctionFactory();
    LineHardenObjectiveFunctionFactory hardenObjective = new LineHardenObjectiveFunctionFactory();
    LineSwitchObjectiveFunctionFactory switchObjective = new LineSwitchObjectiveFunctionFactory();
    
    generatorObjective.addCoefficients(problem, model);   
    lineObjective.addCoefficients(problem, model);   
    hardenObjective.addCoefficients(problem, model);   
    switchObjective.addCoefficients(problem, model);       
  }
  
  /**
   * Add the outer variable bounds
   * @param model
   * @param problem
   * @throws NoVariableException 
   * @throws VariableExistsException 
   */
  protected void addOuterVariableBounds(ElectricPowerModel model, MathematicalProgram problem, double upperBound) throws VariableExistsException, NoVariableException {
    GeneratorConstructionBound generatorBound = new GeneratorConstructionBound(upperBound);
    LineConstructionBound lineBound = new LineConstructionBound(upperBound);
    LineHardenBound hardenBound = new LineHardenBound(upperBound);
    LineSwitchBound switchBound = new LineSwitchBound(upperBound);
    
    generatorBound.constructConstraint(problem, model);
    lineBound.constructConstraint(problem, model);
    hardenBound.constructConstraint(problem, model);
    switchBound.constructConstraint(problem, model);
  }

  /**
   * Add constraints on the outer problem
   * @param model
   * @param problem
   * @throws InvalidConstraintException 
   * @throws NoVariableException 
   * @throws VariableExistsException 
   */
  protected void addOuterConstraints(ElectricPowerModel model, MathematicalProgram problem) throws VariableExistsException, NoVariableException, InvalidConstraintException {
    
  }

  /**
   * Add all the variables associated with an inner problem
   * @param model
   * @param problem
   * @throws InvalidVariableException 
   * @throws VariableExistsException 
   */
  protected void addInnerVariables(ElectricPowerModel model, MathematicalProgram problem, Scenario scenario) throws VariableExistsException, InvalidVariableException {
    gov.lanl.micot.application.rdt.algorithm.ep.variable.scenario.GeneratorConstructionVariableFactory generatorVariableFactory = new gov.lanl.micot.application.rdt.algorithm.ep.variable.scenario.GeneratorConstructionVariableFactory(scenario);
    gov.lanl.micot.application.rdt.algorithm.ep.variable.scenario.LineExistVariableFactory    lineConstructionVariableFactory    = new gov.lanl.micot.application.rdt.algorithm.ep.variable.scenario.LineExistVariableFactory(scenario);
    gov.lanl.micot.application.rdt.algorithm.ep.variable.scenario.LineHardenVariableFactory    lineHardenVariableFactory    = new gov.lanl.micot.application.rdt.algorithm.ep.variable.scenario.LineHardenVariableFactory(scenario);
    gov.lanl.micot.application.rdt.algorithm.ep.variable.scenario.LineSwitchVariableFactory    lineSwitchVariableFactory    = new gov.lanl.micot.application.rdt.algorithm.ep.variable.scenario.LineSwitchVariableFactory(scenario);
    LineActiveVariableFactory lineActiveVariable = new LineActiveVariableFactory(scenario);
    VoltageVariableFactory voltageVariable = new VoltageVariableFactory(scenario); 
    LoadVariableFactory loadVariable = new LoadVariableFactory(scenario);
    GeneratorVariableFactory genVariable = new GeneratorVariableFactory(scenario);
    LineFlowVariableFactory flowVariable = new LineFlowVariableFactory(scenario);
    LineDirectionVariableFactory directionVariable = new LineDirectionVariableFactory(scenario);
    
    generatorVariableFactory.createVariables(problem, model);
    lineConstructionVariableFactory.createVariables(problem, model);
    lineHardenVariableFactory.createVariables(problem, model);
    lineSwitchVariableFactory.createVariables(problem, model); 
    lineActiveVariable.createVariables(problem, model);
    voltageVariable.createVariables(problem, model);
    loadVariable.createVariables(problem, model);
    genVariable.createVariables(problem, model);
    flowVariable.createVariables(problem, model);
    directionVariable.createVariables(problem, model);
  }
  
  
  /**
   * Add the objective coefficients associated with an inner problem
   * @param model
   * @param problem
   * @throws NoVariableException
   */
  protected void addInnerObjectiveFunction(ElectricPowerModel model, MathematicalProgram problem, Scenario scenario) throws NoVariableException {
  }
  
  /**
   * Create bounds on the inner variables
   * @param model
   * @param problem
   * @param scenario
   * @throws VariableExistsException
   * @throws NoVariableException
   */
  protected void addInnerVariableBounds(ElectricPowerModel model, MathematicalProgram problem, Scenario scenario) throws VariableExistsException, NoVariableException {
    gov.lanl.micot.application.rdt.algorithm.ep.bound.scenario.GeneratorConstructionBound generatorBound = new gov.lanl.micot.application.rdt.algorithm.ep.bound.scenario.GeneratorConstructionBound(scenario);
    gov.lanl.micot.application.rdt.algorithm.ep.bound.scenario.LineConstructionBound lineBound = new gov.lanl.micot.application.rdt.algorithm.ep.bound.scenario.LineConstructionBound(scenario);
    gov.lanl.micot.application.rdt.algorithm.ep.bound.scenario.LineHardenBound hardenBound = new  gov.lanl.micot.application.rdt.algorithm.ep.bound.scenario.LineHardenBound(scenario);
    gov.lanl.micot.application.rdt.algorithm.ep.bound.scenario.LineSwitchBound switchBound = new  gov.lanl.micot.application.rdt.algorithm.ep.bound.scenario.LineSwitchBound(scenario);
    LineActiveBound activeBound = new LineActiveBound(scenario);
    VoltageBound voltageBound = new VoltageBound(scenario); 
    LoadBound loadBound = new LoadBound(scenario);
    GeneratorBound genBound = new GeneratorBound(scenario);
    LineFlowBound flowBound = new LineFlowBound(scenario);
    LineDirectionBound directionBound = new LineDirectionBound(scenario);
    
    generatorBound.constructConstraint(problem, model);
    lineBound.constructConstraint(problem, model);
    hardenBound.constructConstraint(problem, model);
    switchBound.constructConstraint(problem, model);
    activeBound.constructConstraint(problem, model);
    voltageBound.constructConstraint(problem, model);
    loadBound.constructConstraint(problem, model);
    genBound.constructConstraint(problem, model);
    flowBound.constructConstraint(problem, model);    
    directionBound.constructConstraint(problem, model);
  }

  /**
   * Add all the inner constraints
   * @param model
   * @param problem
   * @throws VariableExistsException
   * @throws NoVariableException
   * @throws InvalidConstraintException
   */
  protected void addInnerConstraints(ElectricPowerModel model, MathematicalProgram problem, Scenario scenario) throws VariableExistsException, NoVariableException, InvalidConstraintException {
    LineSwitchExistTieConstraint switchExistTie = new LineSwitchExistTieConstraint(scenario);
    LineActiveTieConstraint activeTie = new LineActiveTieConstraint(scenario);
    LineHardenExistTieConstraint hardenTie = new LineHardenExistTieConstraint(scenario);
    CriticalLoadConstraint critical = new CriticalLoadConstraint(criticalLoadMet, scenario);
    NonCriticalLoadConstraint nonCritical = new NonCriticalLoadConstraint(nonCriticalLoadMet, scenario);
    GeneratorConstraint generator = new GeneratorConstraint(scenario);
    BusBalanceConstraint balance = new BusBalanceConstraint(scenario);
    LineCapacityConstraint capacity = new LineCapacityConstraint(scenario);
    LineDirectionConstraint direction = new LineDirectionConstraint(scenario); 
    
    switchExistTie.constructConstraint(problem, model);
    activeTie.constructConstraint(problem, model);
    hardenTie.constructConstraint(problem, model);
    critical.constructConstraint(problem, model);
    nonCritical.constructConstraint(problem, model);
    generator.constructConstraint(problem, model);
    balance.constructConstraint(problem, model);
    capacity.constructConstraint(problem, model);
    direction.constructConstraint(problem, model);
  }

  /**
   * Set the critical load met parameter
   * @param criticalLoadMet
   */
  public void setCriticalLoadMet(double criticalLoadMet) {
    this.criticalLoadMet = criticalLoadMet;
  }
  
  /**
   * Set the non critical load met parameter
   * @param criticalLoadMet
   */
  public void setNonCriticalLoadMet(double nonCriticalLoadMet) {
    this.nonCriticalLoadMet = nonCriticalLoadMet;
  }
}

