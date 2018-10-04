package gov.lanl.micot.application.rdt.algorithm.ep.bp;

import gov.lanl.micot.application.rdt.algorithm.ep.bp.constraint.GeneratorBoundConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.bp.constraint.GeneratorColumnConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.bp.constraint.LambdaBoundConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.bp.constraint.LambdaConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.bp.constraint.LineConstructionBoundConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.bp.constraint.LineConstructionColumnConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.bp.constraint.LineHardenBoundConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.bp.constraint.LineHardenColumnConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.bp.constraint.LineSwitchColumnConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.bp.constraint.dual.ColumnConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.bp.constraint.dual.GeneratorConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.bp.constraint.dual.LineConstructionConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.bp.constraint.dual.LineHardenConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.bp.constraint.dual.LineSwitchConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.bp.constraint.dual.SigmaBoundConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.bp.constraint.dual.YGeneratorBoundConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.bp.constraint.dual.YLineConstructionBoundConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.bp.constraint.dual.YLineHardenBoundConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.bp.constraint.dual.YLineSwitchBoundConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.bp.objective.GeneratorObjectiveFunctionFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.bp.objective.LineConstructionObjectiveFunctionFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.bp.objective.LineHardenObjectiveFunctionFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.bp.objective.LineSwitchObjectiveFunctionFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.bp.objective.dual.SigmaObjectiveFunctionFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.bp.variable.GeneratorVariableFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.bp.variable.LambdaVariableFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.bp.variable.LineConstructionVariableFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.bp.variable.LineHardenVariableFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.bp.variable.LineSwitchVariableFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.bp.variable.dual.SigmaVariableFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.bp.variable.dual.YGeneratorVariableFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.bp.variable.dual.YLineConstructionVariableFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.bp.variable.dual.YLineHardenVariableFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.bp.variable.dual.YLineSwitchVariableFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.constraint.LineSwitchBoundConstraint;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.optimize.OptimizerImpl;
import gov.lanl.micot.util.math.solver.Solution;
import gov.lanl.micot.util.math.solver.exception.InvalidConstraintException;
import gov.lanl.micot.util.math.solver.exception.InvalidObjectiveException;
import gov.lanl.micot.util.math.solver.exception.InvalidVariableException;
import gov.lanl.micot.util.math.solver.exception.NoConstraintException;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.DefaultMathematicalProgramFactory;
import gov.lanl.micot.util.math.solver.mathprogram.LinearObjective;
import gov.lanl.micot.util.math.solver.mathprogram.LinearObjectiveMaximize;
import gov.lanl.micot.util.math.solver.mathprogram.LinearObjectiveMinimize;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgramFlags;
import gov.lanl.micot.util.time.Timer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * Branch and Price algorithm for solving the reslient design problem
 * @author Russell Bent
 */
public class BPResilienceAlgorithm extends OptimizerImpl<ElectricPowerNode, ElectricPowerModel> {
  
  private Collection<Scenario> scenarios = null;
  
  private MathematicalProgramFlags innerFlags = null;
  private MathematicalProgramFlags outerFlags = null;

  private MathematicalProgram outerProblem = null;
  private MathematicalProgram outerDualProblem = null;
  private MathematicalProgram innerProblem = null;

  
  /**
   * Constructor
   */
  public BPResilienceAlgorithm(Collection<Scenario> scenarios) {
    super();
    setScenarios(scenarios);    
    innerFlags = new MathematicalProgramFlags();
    outerFlags = new MathematicalProgramFlags();
  }


  @Override
  public boolean solve(ElectricPowerModel model) {
    try {
      
      HashMap<Scenario, ArrayList<Solution>> columns = new HashMap<Scenario, ArrayList<Solution>>();
      for (Scenario scenario : scenarios) {
        columns.put(scenario, new ArrayList<Solution>());
      }
      
      createOuterMathematicalProgram(model, columns);
      createOuterDualMathematicalProgram(model, columns);
      
      Timer timer = new Timer();
      timer.startTimer();
      
      Solution primalSolution = outerProblem.solve();
      Solution dualSolution = outerDualProblem.solve();
      
      if (primalSolution.getObjectiveValue() != dualSolution.getObjectiveValue()) {
        System.err.println("Primal and Dual Solutions are not equal " + primalSolution.getObjectiveValue() + " " + dualSolution.getObjectiveValue());
      }
      
      for (Scenario scenario : scenarios) {
        createInnerMathematicalProgram(model, scenario, dualSolution);
        Solution innerSolution = innerProblem.solve();
      }
      
    }
    catch (Exception e) {
      e.printStackTrace();
    }
   

    
    
    return true;    
  }

  /**
   * Set the available scenarios that we have
   * @param scenarios
   */
  private void setScenarios(Collection<Scenario> scenarios) {
    this.scenarios = scenarios;
  }
  
  /**
   * Add an inner program flag
   * @param key
   * @param object
   */
  public void addInnerMathProgramFlag(String key, Object object) {
    innerFlags.put(key, object);
  }
  
  /**
   * Add an inner program flag
   * @param key
   * @param object
   */
  public void addOuterMathProgramFlag(String key, Object object) {
    outerFlags.put(key, object);
  }


  /**
   * Create the outer mathematical program
   * @param model
   * @throws InvalidObjectiveException
   * @throws InvalidVariableException 
   * @throws VariableExistsException 
   * @throws NoVariableException 
   * @throws InvalidConstraintException 
   * @throws NoConstraintException 
   */
  protected void createOuterMathematicalProgram(ElectricPowerModel model, HashMap<Scenario, ArrayList<Solution>> columns) throws InvalidObjectiveException, VariableExistsException, InvalidVariableException, NoVariableException, InvalidConstraintException, NoConstraintException {
    outerProblem = DefaultMathematicalProgramFactory.getInstance().constructMathematicalProgram(outerFlags);

    LinearObjective objective = new LinearObjectiveMaximize();
    outerProblem.setLinearObjective(objective);

    // variables of the outer problem
    GeneratorVariableFactory generatorVariableFactory = new GeneratorVariableFactory();
    LambdaVariableFactory    lambdaVariableFactory    = new LambdaVariableFactory(scenarios, columns);
    LineConstructionVariableFactory    lineConstructionVariableFactory    = new LineConstructionVariableFactory();
    LineHardenVariableFactory    lineHardenVariableFactory    = new LineHardenVariableFactory();
    LineSwitchVariableFactory    lineSwitchVariableFactory    = new LineSwitchVariableFactory();
    
    generatorVariableFactory.createVariables(outerProblem, model);
    lambdaVariableFactory.createVariables(outerProblem, model);
    lineConstructionVariableFactory.createVariables(outerProblem, model);
    lineHardenVariableFactory.createVariables(outerProblem, model);
    lineSwitchVariableFactory.createVariables(outerProblem, model);
           
    // objective function for the outer problem 
    GeneratorObjectiveFunctionFactory generatorObjective = new GeneratorObjectiveFunctionFactory();
    LineConstructionObjectiveFunctionFactory lineObjective = new LineConstructionObjectiveFunctionFactory();
    LineHardenObjectiveFunctionFactory hardenObjective = new LineHardenObjectiveFunctionFactory();
    LineSwitchObjectiveFunctionFactory switchObjective = new LineSwitchObjectiveFunctionFactory();
    
    generatorObjective.addCoefficients(outerProblem, model);   
    lineObjective.addCoefficients(outerProblem, model);   
    hardenObjective.addCoefficients(outerProblem, model);   
    switchObjective.addCoefficients(outerProblem, model);   
     
    // create the bounds
    GeneratorBoundConstraint generatorBound = new GeneratorBoundConstraint();
    LineConstructionBoundConstraint lineBound = new LineConstructionBoundConstraint();
    LineHardenBoundConstraint hardenBound = new LineHardenBoundConstraint();
    LineSwitchBoundConstraint switchBound = new LineSwitchBoundConstraint();
    LambdaBoundConstraint lambdaBound = new LambdaBoundConstraint(scenarios, columns);
    
    generatorBound.constructConstraint(outerProblem, model);
    lineBound.constructConstraint(outerProblem, model);
    hardenBound.constructConstraint(outerProblem, model);
    switchBound.constructConstraint(outerProblem, model);
    lambdaBound.constructConstraint(outerProblem, model);
    
    // create the constraints
    LambdaConstraint lambdaConstraint = new LambdaConstraint(scenarios, columns);
    GeneratorColumnConstraint generatorColumn = new GeneratorColumnConstraint(scenarios, columns);
    LineConstructionColumnConstraint lineColumn = new LineConstructionColumnConstraint(scenarios, columns);
    LineSwitchColumnConstraint switchColumn = new LineSwitchColumnConstraint(scenarios, columns);
    LineHardenColumnConstraint hardenColumn = new LineHardenColumnConstraint(scenarios, columns);
      
    lambdaConstraint.constructConstraint(outerProblem, model);
    generatorColumn.constructConstraint(outerProblem, model);
    lineColumn.constructConstraint(outerProblem, model);
    switchColumn.constructConstraint(outerProblem, model);
    hardenColumn.constructConstraint(outerProblem, model);
    
  }
  
  
  
  /**
   * Create the outer dual mathematical program (Some of the MathProgramming interfaces don't currently expose the dual variables)
   * @param model
   * @throws InvalidObjectiveException
   * @throws InvalidVariableException 
   * @throws VariableExistsException 
   * @throws NoVariableException 
   * @throws InvalidConstraintException 
   * @throws NoConstraintException 
   */
  protected void createOuterDualMathematicalProgram(ElectricPowerModel model, HashMap<Scenario, ArrayList<Solution>> columns) throws InvalidObjectiveException, VariableExistsException, InvalidVariableException, NoVariableException, InvalidConstraintException, NoConstraintException {
    outerDualProblem = DefaultMathematicalProgramFactory.getInstance().constructMathematicalProgram(outerFlags);

    LinearObjective objective = new LinearObjectiveMinimize();
    outerDualProblem.setLinearObjective(objective);

    // variables of the outer problem
    YGeneratorVariableFactory generatorVariableFactory = new YGeneratorVariableFactory(scenarios);
    SigmaVariableFactory    sigmaVariableFactory    = new SigmaVariableFactory(scenarios, columns);
    YLineConstructionVariableFactory    lineConstructionVariableFactory    = new YLineConstructionVariableFactory(scenarios);
    YLineHardenVariableFactory    lineHardenVariableFactory    = new YLineHardenVariableFactory(scenarios);
    YLineSwitchVariableFactory    lineSwitchVariableFactory    = new YLineSwitchVariableFactory(scenarios);
    
    generatorVariableFactory.createVariables(outerDualProblem, model);
    sigmaVariableFactory.createVariables(outerDualProblem, model);
    lineConstructionVariableFactory.createVariables(outerDualProblem, model);
    lineHardenVariableFactory.createVariables(outerDualProblem, model);
    lineSwitchVariableFactory.createVariables(outerDualProblem, model);
           
    // objective function for the outer problem 
    SigmaObjectiveFunctionFactory sigmaObjective = new SigmaObjectiveFunctionFactory(scenarios, columns);
    
    sigmaObjective.addCoefficients(outerDualProblem, model);   
     
    // create the bounds
    YGeneratorBoundConstraint generatorBound = new YGeneratorBoundConstraint(scenarios);
    YLineConstructionBoundConstraint lineBound = new YLineConstructionBoundConstraint(scenarios);
    YLineHardenBoundConstraint hardenBound = new YLineHardenBoundConstraint(scenarios);
    YLineSwitchBoundConstraint switchBound = new YLineSwitchBoundConstraint(scenarios);
    SigmaBoundConstraint sigmaBound = new SigmaBoundConstraint(scenarios, columns);
    
    generatorBound.constructConstraint(outerDualProblem, model);
    lineBound.constructConstraint(outerDualProblem, model);
    hardenBound.constructConstraint(outerDualProblem, model);
    switchBound.constructConstraint(outerDualProblem, model);
    sigmaBound.constructConstraint(outerDualProblem, model);
    
    // create the constraints
    ColumnConstraint columnConstraint = new ColumnConstraint(scenarios, columns);
    GeneratorConstraint generatorConstraint = new GeneratorConstraint(scenarios);
    LineConstructionConstraint lineConstraint = new LineConstructionConstraint(scenarios);
    LineSwitchConstraint switchConstraint = new LineSwitchConstraint(scenarios);
    LineHardenConstraint hardenConstraint = new LineHardenConstraint(scenarios);
      
    columnConstraint.constructConstraint(outerDualProblem, model);
    generatorConstraint.constructConstraint(outerDualProblem, model);
    lineConstraint.constructConstraint(outerDualProblem, model);
    switchConstraint.constructConstraint(outerDualProblem, model);
    hardenConstraint.constructConstraint(outerDualProblem, model);
    
  }
  
  
  /**
   * Create the inner mathematical program
   * @param model
   * @throws InvalidObjectiveException
   * @throws InvalidVariableException 
   * @throws VariableExistsException 
   * @throws NoVariableException 
   * @throws InvalidConstraintException 
   * @throws NoConstraintException 
   */
  protected void createInnerMathematicalProgram(ElectricPowerModel model, Scenario scenario, Solution dualSolution) throws InvalidObjectiveException, VariableExistsException, InvalidVariableException, NoVariableException, InvalidConstraintException, NoConstraintException {
    innerProblem = DefaultMathematicalProgramFactory.getInstance().constructMathematicalProgram(innerFlags);

    LinearObjective objective = new LinearObjectiveMinimize();
    innerProblem.setLinearObjective(objective);

    // variables of the outer problem
    gov.lanl.micot.application.rdt.algorithm.ep.bp.variable.scenario.GeneratorVariableFactory generatorVariableFactory = new gov.lanl.micot.application.rdt.algorithm.ep.bp.variable.scenario.GeneratorVariableFactory(scenario);
    gov.lanl.micot.application.rdt.algorithm.ep.bp.variable.scenario.LineExistVariableFactory    lineConstructionVariableFactory    = new gov.lanl.micot.application.rdt.algorithm.ep.bp.variable.scenario.LineExistVariableFactory(scenario);
    gov.lanl.micot.application.rdt.algorithm.ep.bp.variable.scenario.LineHardenVariableFactory    lineHardenVariableFactory    = new gov.lanl.micot.application.rdt.algorithm.ep.bp.variable.scenario.LineHardenVariableFactory(scenario);
    gov.lanl.micot.application.rdt.algorithm.ep.bp.variable.scenario.LineSwitchVariableFactory    lineSwitchVariableFactory    = new gov.lanl.micot.application.rdt.algorithm.ep.bp.variable.scenario.LineSwitchVariableFactory(scenario);
    
    generatorVariableFactory.createVariables(innerProblem, model);
    lineConstructionVariableFactory.createVariables(innerProblem, model);
    lineHardenVariableFactory.createVariables(innerProblem, model);
    lineSwitchVariableFactory.createVariables(innerProblem, model);
           
    // objective function for the outer problem 
    gov.lanl.micot.application.rdt.algorithm.ep.bp.objective.scenario.GeneratorObjectiveFunctionFactory generatorObjective = new gov.lanl.micot.application.rdt.algorithm.ep.bp.objective.scenario.GeneratorObjectiveFunctionFactory(scenario, dualSolution, outerDualProblem);
    gov.lanl.micot.application.rdt.algorithm.ep.bp.objective.scenario.LineConstructionObjectiveFunctionFactory lineObjective = new gov.lanl.micot.application.rdt.algorithm.ep.bp.objective.scenario.LineConstructionObjectiveFunctionFactory(scenario, dualSolution, outerDualProblem);
    gov.lanl.micot.application.rdt.algorithm.ep.bp.objective.scenario.LineHardenObjectiveFunctionFactory hardenObjective = new gov.lanl.micot.application.rdt.algorithm.ep.bp.objective.scenario.LineHardenObjectiveFunctionFactory(scenario, dualSolution, outerDualProblem);
    gov.lanl.micot.application.rdt.algorithm.ep.bp.objective.scenario.LineSwitchObjectiveFunctionFactory switchObjective = new gov.lanl.micot.application.rdt.algorithm.ep.bp.objective.scenario.LineSwitchObjectiveFunctionFactory(scenario, dualSolution, outerDualProblem);
    
    generatorObjective.addCoefficients(innerProblem, model);   
    lineObjective.addCoefficients(innerProblem, model);   
    hardenObjective.addCoefficients(innerProblem, model);   
    switchObjective.addCoefficients(innerProblem, model);   
     
    // create the bounds
    gov.lanl.micot.application.rdt.algorithm.ep.bp.constraint.scenario.GeneratorBoundConstraint generatorBound = new gov.lanl.micot.application.rdt.algorithm.ep.bp.constraint.scenario.GeneratorBoundConstraint(scenario);
    gov.lanl.micot.application.rdt.algorithm.ep.bp.constraint.scenario.LineConstructionBoundConstraint lineBound = new gov.lanl.micot.application.rdt.algorithm.ep.bp.constraint.scenario.LineConstructionBoundConstraint(scenario);
    gov.lanl.micot.application.rdt.algorithm.ep.bp.constraint.scenario.LineHardenBoundConstraint hardenBound = new  gov.lanl.micot.application.rdt.algorithm.ep.bp.constraint.scenario.LineHardenBoundConstraint(scenario);
    gov.lanl.micot.application.rdt.algorithm.ep.bp.constraint.scenario.LineSwitchBoundConstraint switchBound = new  gov.lanl.micot.application.rdt.algorithm.ep.bp.constraint.scenario.LineSwitchBoundConstraint(scenario);
    
    generatorBound.constructConstraint(innerProblem, model);
    lineBound.constructConstraint(innerProblem, model);
    hardenBound.constructConstraint(innerProblem, model);
    switchBound.constructConstraint(innerProblem, model);
    
    // create the constraints
/*    LambdaConstraint lambdaConstraint = new LambdaConstraint(scenarios, columns);
    GeneratorColumnConstraint generatorColumn = new GeneratorColumnConstraint(scenarios, columns);
    LineConstructionColumnConstraint lineColumn = new LineConstructionColumnConstraint(scenarios, columns);
    LineSwitchColumnConstraint switchColumn = new LineSwitchColumnConstraint(scenarios, columns);
    LineHardenColumnConstraint hardenColumn = new LineHardenColumnConstraint(scenarios, columns);
      
    lambdaConstraint.constructConstraint(outerProblem, model);
    generatorColumn.constructConstraint(outerProblem, model);
    lineColumn.constructConstraint(outerProblem, model);
    switchColumn.constructConstraint(outerProblem, model);
    hardenColumn.constructConstraint(outerProblem, model);*/
    
  }

  
  
}

