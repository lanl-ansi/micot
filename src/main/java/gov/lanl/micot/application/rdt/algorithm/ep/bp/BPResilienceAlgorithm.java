package gov.lanl.micot.application.rdt.algorithm.ep.bp;

import gov.lanl.micot.application.rdt.algorithm.ResilienceAlgorithm;
import gov.lanl.micot.application.rdt.algorithm.ep.bp.constraint.GeneratorColumnConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.bp.bound.LambdaBoundConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.bp.constraint.LambdaConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.bp.constraint.LineConstructionColumnConstraint;
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
import gov.lanl.micot.application.rdt.algorithm.ep.bp.objective.dual.SigmaObjectiveFunctionFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.bp.variable.LambdaVariableFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.bp.variable.dual.SigmaVariableFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.bp.variable.dual.YGeneratorVariableFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.bp.variable.dual.YLineConstructionVariableFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.bp.variable.dual.YLineHardenVariableFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.bp.variable.dual.YLineSwitchVariableFactory;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.model.Scenario;
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
public class BPResilienceAlgorithm extends ResilienceAlgorithm {
  
  private HashMap<Scenario, ArrayList<Solution>> columns = null;
  
  private MathematicalProgramFlags innerFlags = null;
  private MathematicalProgramFlags outerFlags = null;

  private MathematicalProgram outerProblem = null;
  private MathematicalProgram outerDualProblem = null;
  private MathematicalProgram innerProblem = null;

  private Solution primalSolution = null;
  private Solution dualSolution   = null;
  
  /**
   * Constructor
   */
  public BPResilienceAlgorithm(Collection<Scenario> scenarios) {
    super(scenarios);
    innerFlags = new MathematicalProgramFlags();
    outerFlags = new MathematicalProgramFlags();
  }


  @Override
  public boolean solve(ElectricPowerModel model) {
    try {
      
      columns = new HashMap<Scenario, ArrayList<Solution>>();
      for (Scenario scenario : getScenarios()) {
        columns.put(scenario, new ArrayList<Solution>());
      }
      
      createOuterMathematicalProgram(model, columns);
      createOuterDualMathematicalProgram(model, columns);
      
      Timer timer = new Timer();
      timer.startTimer();
      
      primalSolution = outerProblem.solve();
      dualSolution = outerDualProblem.solve();
      
      if (primalSolution.getObjectiveValue() != dualSolution.getObjectiveValue()) {
        System.err.println("Primal and Dual Solutions are not equal " + primalSolution.getObjectiveValue() + " " + dualSolution.getObjectiveValue());
      }
      
      for (Scenario scenario : getScenarios()) {
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

  @Override
  protected void addOuterVariables(ElectricPowerModel model, MathematicalProgram problem) throws VariableExistsException, InvalidVariableException {
    super.addOuterVariables(model, problem);
    LambdaVariableFactory    lambdaVariableFactory    = new LambdaVariableFactory(getScenarios(), columns);
    lambdaVariableFactory.createVariables(outerProblem, model);
  }
  
  @Override
  protected void addOuterVariableBounds(ElectricPowerModel model, MathematicalProgram problem, double upperBound) throws VariableExistsException, NoVariableException {
    super.addOuterVariableBounds(model, problem, upperBound);
    LambdaBoundConstraint lambdaBound = new LambdaBoundConstraint(getScenarios(), columns);
    lambdaBound.constructConstraint(outerProblem, model);
  }
  
  @Override
  protected void addOuterConstraints(ElectricPowerModel model, MathematicalProgram problem) throws VariableExistsException, NoVariableException, InvalidConstraintException {
    super.addOuterConstraints(model, problem);
    LambdaConstraint lambdaConstraint = new LambdaConstraint(getScenarios(), columns);
    GeneratorColumnConstraint generatorColumn = new GeneratorColumnConstraint(getScenarios(), columns);
    LineConstructionColumnConstraint lineColumn = new LineConstructionColumnConstraint(getScenarios(), columns);
    LineSwitchColumnConstraint switchColumn = new LineSwitchColumnConstraint(getScenarios(), columns);
    LineHardenColumnConstraint hardenColumn = new LineHardenColumnConstraint(getScenarios(), columns);
      
    lambdaConstraint.constructConstraint(problem, model);
    generatorColumn.constructConstraint(problem, model);
    lineColumn.constructConstraint(problem, model);
    switchColumn.constructConstraint(problem, model);
    hardenColumn.constructConstraint(problem, model);
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
    addOuterVariables(model, outerProblem);
           
    // objective function for the outer problem
    addOuterObjectiveFunction(model, outerProblem);
         
    // create the bounds
    addOuterVariableBounds(model, outerProblem, Double.MAX_VALUE); 
            
    // create the constraints
    addOuterConstraints(model, outerProblem);    
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
    YGeneratorVariableFactory generatorVariableFactory = new YGeneratorVariableFactory(getScenarios());
    SigmaVariableFactory    sigmaVariableFactory    = new SigmaVariableFactory(getScenarios(), columns);
    YLineConstructionVariableFactory    lineConstructionVariableFactory    = new YLineConstructionVariableFactory(getScenarios());
    YLineHardenVariableFactory    lineHardenVariableFactory    = new YLineHardenVariableFactory(getScenarios());
    YLineSwitchVariableFactory    lineSwitchVariableFactory    = new YLineSwitchVariableFactory(getScenarios());
    
    generatorVariableFactory.createVariables(outerDualProblem, model);
    sigmaVariableFactory.createVariables(outerDualProblem, model);
    lineConstructionVariableFactory.createVariables(outerDualProblem, model);
    lineHardenVariableFactory.createVariables(outerDualProblem, model);
    lineSwitchVariableFactory.createVariables(outerDualProblem, model);
           
    // objective function for the outer problem 
    SigmaObjectiveFunctionFactory sigmaObjective = new SigmaObjectiveFunctionFactory(getScenarios(), columns);
    
    sigmaObjective.addCoefficients(outerDualProblem, model);   
     
    // create the bounds
    YGeneratorBoundConstraint generatorBound = new YGeneratorBoundConstraint(getScenarios());
    YLineConstructionBoundConstraint lineBound = new YLineConstructionBoundConstraint(getScenarios());
    YLineHardenBoundConstraint hardenBound = new YLineHardenBoundConstraint(getScenarios());
    YLineSwitchBoundConstraint switchBound = new YLineSwitchBoundConstraint(getScenarios());
    SigmaBoundConstraint sigmaBound = new SigmaBoundConstraint(getScenarios(), columns);
    
    generatorBound.constructConstraint(outerDualProblem, model);
    lineBound.constructConstraint(outerDualProblem, model);
    hardenBound.constructConstraint(outerDualProblem, model);
    switchBound.constructConstraint(outerDualProblem, model);
    sigmaBound.constructConstraint(outerDualProblem, model);
    
    // create the constraints
    ColumnConstraint columnConstraint = new ColumnConstraint(getScenarios(), columns);
    GeneratorConstraint generatorConstraint = new GeneratorConstraint(getScenarios());
    LineConstructionConstraint lineConstraint = new LineConstructionConstraint(getScenarios());
    LineSwitchConstraint switchConstraint = new LineSwitchConstraint(getScenarios());
    LineHardenConstraint hardenConstraint = new LineHardenConstraint(getScenarios());
      
    columnConstraint.constructConstraint(outerDualProblem, model);
    generatorConstraint.constructConstraint(outerDualProblem, model);
    lineConstraint.constructConstraint(outerDualProblem, model);
    switchConstraint.constructConstraint(outerDualProblem, model);
    hardenConstraint.constructConstraint(outerDualProblem, model);
    
  }
  
  @Override
  protected void addInnerObjectiveFunction(ElectricPowerModel model, MathematicalProgram problem, Scenario scenario) throws NoVariableException {
    gov.lanl.micot.application.rdt.algorithm.ep.bp.objective.scenario.GeneratorObjectiveFunctionFactory generatorObjective = new gov.lanl.micot.application.rdt.algorithm.ep.bp.objective.scenario.GeneratorObjectiveFunctionFactory(scenario, dualSolution, outerDualProblem);
    gov.lanl.micot.application.rdt.algorithm.ep.bp.objective.scenario.LineConstructionObjectiveFunctionFactory lineObjective = new gov.lanl.micot.application.rdt.algorithm.ep.bp.objective.scenario.LineConstructionObjectiveFunctionFactory(scenario, dualSolution, outerDualProblem);
    gov.lanl.micot.application.rdt.algorithm.ep.bp.objective.scenario.LineHardenObjectiveFunctionFactory hardenObjective = new gov.lanl.micot.application.rdt.algorithm.ep.bp.objective.scenario.LineHardenObjectiveFunctionFactory(scenario, dualSolution, outerDualProblem);
    gov.lanl.micot.application.rdt.algorithm.ep.bp.objective.scenario.LineSwitchObjectiveFunctionFactory switchObjective = new gov.lanl.micot.application.rdt.algorithm.ep.bp.objective.scenario.LineSwitchObjectiveFunctionFactory(scenario, dualSolution, outerDualProblem);
    
    generatorObjective.addCoefficients(problem, model);   
    lineObjective.addCoefficients(problem, model);   
    hardenObjective.addCoefficients(problem, model);   
    switchObjective.addCoefficients(problem, model);   
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

    // variables of the inner problem
    addInnerVariables(model, innerProblem, scenario);
           
    // objective function for the inner problem
    addInnerObjectiveFunction(model, innerProblem, scenario);
     
    // create the bounds for the inner problem
    addInnerVariableBounds(model, innerProblem, scenario);
    
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

