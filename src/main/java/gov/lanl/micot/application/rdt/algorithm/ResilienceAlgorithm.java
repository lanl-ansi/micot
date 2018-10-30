package gov.lanl.micot.application.rdt.algorithm;

import gov.lanl.micot.application.rdt.algorithm.ep.bound.GeneratorBoundConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.bound.LineConstructionBoundConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.bound.LineHardenBoundConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.bound.LineSwitchBoundConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.objective.GeneratorObjectiveFunctionFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.objective.LineConstructionObjectiveFunctionFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.objective.LineHardenObjectiveFunctionFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.objective.LineSwitchObjectiveFunctionFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.variable.GeneratorVariableFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.variable.LineConstructionVariableFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.variable.LineHardenVariableFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.variable.LineSwitchVariableFactory;
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
    GeneratorVariableFactory generatorVariableFactory = new GeneratorVariableFactory();
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
    GeneratorBoundConstraint generatorBound = new GeneratorBoundConstraint(upperBound);
    LineConstructionBoundConstraint lineBound = new LineConstructionBoundConstraint(upperBound);
    LineHardenBoundConstraint hardenBound = new LineHardenBoundConstraint(upperBound);
    LineSwitchBoundConstraint switchBound = new LineSwitchBoundConstraint(upperBound);
    
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
    gov.lanl.micot.application.rdt.algorithm.ep.variable.scenario.GeneratorVariableFactory generatorVariableFactory = new gov.lanl.micot.application.rdt.algorithm.ep.variable.scenario.GeneratorVariableFactory(scenario);
    gov.lanl.micot.application.rdt.algorithm.ep.variable.scenario.LineExistVariableFactory    lineConstructionVariableFactory    = new gov.lanl.micot.application.rdt.algorithm.ep.variable.scenario.LineExistVariableFactory(scenario);
    gov.lanl.micot.application.rdt.algorithm.ep.variable.scenario.LineHardenVariableFactory    lineHardenVariableFactory    = new gov.lanl.micot.application.rdt.algorithm.ep.variable.scenario.LineHardenVariableFactory(scenario);
    gov.lanl.micot.application.rdt.algorithm.ep.variable.scenario.LineSwitchVariableFactory    lineSwitchVariableFactory    = new gov.lanl.micot.application.rdt.algorithm.ep.variable.scenario.LineSwitchVariableFactory(scenario);
    
    generatorVariableFactory.createVariables(problem, model);
    lineConstructionVariableFactory.createVariables(problem, model);
    lineHardenVariableFactory.createVariables(problem, model);
    lineSwitchVariableFactory.createVariables(problem, model);    
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
    gov.lanl.micot.application.rdt.algorithm.ep.bound.scenario.GeneratorBoundConstraint generatorBound = new gov.lanl.micot.application.rdt.algorithm.ep.bound.scenario.GeneratorBoundConstraint(scenario);
    gov.lanl.micot.application.rdt.algorithm.ep.bound.scenario.LineConstructionBoundConstraint lineBound = new gov.lanl.micot.application.rdt.algorithm.ep.bound.scenario.LineConstructionBoundConstraint(scenario);
    gov.lanl.micot.application.rdt.algorithm.ep.bound.scenario.LineHardenBoundConstraint hardenBound = new  gov.lanl.micot.application.rdt.algorithm.ep.bound.scenario.LineHardenBoundConstraint(scenario);
    gov.lanl.micot.application.rdt.algorithm.ep.bound.scenario.LineSwitchBoundConstraint switchBound = new  gov.lanl.micot.application.rdt.algorithm.ep.bound.scenario.LineSwitchBoundConstraint(scenario);
    
    generatorBound.constructConstraint(problem, model);
    lineBound.constructConstraint(problem, model);
    hardenBound.constructConstraint(problem, model);
    switchBound.constructConstraint(problem, model);
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
    
  }
}

