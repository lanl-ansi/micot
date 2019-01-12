package gov.lanl.micot.application.rdt.algorithm.ep.heuristic;

import gov.lanl.micot.application.rdt.algorithm.ResilienceAlgorithm;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.constraint.GeneratorTieConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.constraint.LineConstructionTieConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.constraint.LineHardenTieConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.constraint.LineSwitchTieConstraint;
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
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgramFlags;

import java.util.Collection;
import java.util.HashMap;

/**
 * Heuristic algorithm for solving the resilient design problem
 * @author Russell Bent
 */
public class HeuristicResilienceAlgorithm extends ResilienceAlgorithm {
  
  private HashMap<Scenario, Solution> innerSolutions = null;
    
  private MathematicalProgramFlags innerFlags = null;
  private MathematicalProgramFlags outerFlags = null;

  private MathematicalProgram outerProblem = null;
  private MathematicalProgram innerProblem = null;

  private Solution outerSolution = null;
  
  /**
   * Constructor
   */
  public HeuristicResilienceAlgorithm(Collection<Scenario> scenarios) {
    super(scenarios);
    innerFlags = new MathematicalProgramFlags();
    outerFlags = new MathematicalProgramFlags();
  }

  @Override
  public boolean solve(ElectricPowerModel model) {
    try {
      
      innerSolutions = new HashMap<Scenario, Solution>();
      
      for (Scenario scenario : getScenarios()) {
        createInnerMathematicalProgram(model, scenario);
        Solution innerSolution = innerProblem.solve();
        innerSolutions.put(scenario, innerSolution);
        performInnerAssignments(model,innerProblem, innerSolution, scenario);
      }

      createOuterMathematicalProgram(model);      
      outerSolution = outerProblem.solve();
      performOuterAssignments(model, outerProblem, outerSolution);
    }
    catch (Exception e) {
      e.printStackTrace();
    }

    setIsFeasible(outerSolution.isFeasible());
    setLastObjectiveValue(outerSolution.getObjectiveValue());
    
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
  }
  
  @Override
  protected void addOuterVariableBounds(ElectricPowerModel model, MathematicalProgram problem, double upperBound) throws VariableExistsException, NoVariableException {
    super.addOuterVariableBounds(model, problem, upperBound);
  }
  
  @Override
  protected void addOuterConstraints(ElectricPowerModel model, MathematicalProgram problem) throws VariableExistsException, NoVariableException, InvalidConstraintException {
    super.addOuterConstraints(model, problem);
    for (Scenario scenario : getScenarios()) {
      
      gov.lanl.micot.application.rdt.algorithm.ep.heuristic.constraint.LineConstructionTieConstraint constructTie = new gov.lanl.micot.application.rdt.algorithm.ep.heuristic.constraint.LineConstructionTieConstraint(scenario,innerSolutions.get(scenario));
      gov.lanl.micot.application.rdt.algorithm.ep.heuristic.constraint.LineHardenTieConstraint hardenTie = new gov.lanl.micot.application.rdt.algorithm.ep.heuristic.constraint.LineHardenTieConstraint(scenario,innerSolutions.get(scenario));
      gov.lanl.micot.application.rdt.algorithm.ep.heuristic.constraint.LineSwitchTieConstraint switchTie = new gov.lanl.micot.application.rdt.algorithm.ep.heuristic.constraint.LineSwitchTieConstraint(scenario,innerSolutions.get(scenario));
      gov.lanl.micot.application.rdt.algorithm.ep.heuristic.constraint.GeneratorTieConstraint genTie = new gov.lanl.micot.application.rdt.algorithm.ep.heuristic.constraint.GeneratorTieConstraint(scenario,innerSolutions.get(scenario));
      
      constructTie.constructConstraint(problem, model);
      hardenTie.constructConstraint(problem, model);
      switchTie.constructConstraint(problem, model);
      genTie.constructConstraint(problem, model);      
    }    
  }
  
  @Override
  protected void addInnerVariables(ElectricPowerModel model, MathematicalProgram problem, Scenario scenario) throws VariableExistsException, InvalidVariableException {
    super.addOuterVariables(model, problem);
    super.addInnerVariables(model, problem, scenario);
  }
  
  protected void addInnerVariableBounds(ElectricPowerModel model, MathematicalProgram problem, Scenario scenario) throws VariableExistsException, NoVariableException {
    super.addOuterVariableBounds(model, problem, 1.0);
    super.addInnerVariableBounds(model, problem, scenario);
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
  protected void createOuterMathematicalProgram(ElectricPowerModel model) throws InvalidObjectiveException, VariableExistsException, InvalidVariableException, NoVariableException, InvalidConstraintException, NoConstraintException {
    outerProblem = DefaultMathematicalProgramFactory.getInstance().constructMathematicalProgram(outerFlags);

    LinearObjective objective = new LinearObjectiveMaximize();
    outerProblem.setLinearObjective(objective);

    // variables of the outer problem
    addOuterVariables(model, outerProblem);
               
    // objective function 
    addOuterObjectiveFunction(model, outerProblem);
         
    // create the bounds of the outer problem
    addOuterVariableBounds(model, outerProblem, 1.0);
        
    // create the constraints associated with the outer problem
    addOuterConstraints(model, outerProblem);
        
  }
  
  @Override
  protected void addInnerObjectiveFunction(ElectricPowerModel model, MathematicalProgram problem, Scenario scenario) throws NoVariableException {
    super.addOuterObjectiveFunction(model, problem);
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
  protected void createInnerMathematicalProgram(ElectricPowerModel model, Scenario scenario) throws InvalidObjectiveException, VariableExistsException, InvalidVariableException, NoVariableException, InvalidConstraintException, NoConstraintException {
    innerProblem = DefaultMathematicalProgramFactory.getInstance().constructMathematicalProgram(innerFlags);

    LinearObjective objective = new LinearObjectiveMaximize();
    innerProblem.setLinearObjective(objective);

    // variables of the inner problem
    addInnerVariables(model, innerProblem, scenario);
           
    // objective function for the inner problem
    addInnerObjectiveFunction(model, innerProblem, scenario);
     
    // create the bounds for the inner problem
    addInnerVariableBounds(model, innerProblem, scenario);
    
    // create the constraints for the inner problem
    addInnerConstraints(model, innerProblem, scenario);        
  }

  @Override
  protected void addInnerConstraints(ElectricPowerModel model, MathematicalProgram problem, Scenario scenario) throws VariableExistsException, NoVariableException, InvalidConstraintException {
    super.addOuterConstraints(model, problem);
    super.addInnerConstraints(model, problem, scenario);
    
    LineConstructionTieConstraint constructTie = new LineConstructionTieConstraint(scenario);
    LineHardenTieConstraint hardenTie = new LineHardenTieConstraint(scenario);
    LineSwitchTieConstraint switchTie = new LineSwitchTieConstraint(scenario);
    GeneratorTieConstraint genTie = new GeneratorTieConstraint(scenario);
    
    constructTie.constructConstraint(problem, model);
    hardenTie.constructConstraint(problem, model);
    switchTie.constructConstraint(problem, model);
    genTie.constructConstraint(problem, model);
  }

  
}

