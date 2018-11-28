package gov.lanl.micot.application.rdt.algorithm.ep.mip2;

import gov.lanl.micot.application.rdt.algorithm.ResilienceAlgorithm;
import gov.lanl.micot.application.rdt.algorithm.ep.mip2.constraint.GeneratorTieConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.mip2.constraint.LineConstructionTieConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.mip2.constraint.LineHardenTieConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.mip2.constraint.LineSwitchTieConstraint;
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
import gov.lanl.micot.util.time.Timer;

import java.util.Collection;

/**
 * Full MIP algorithm for solving the reslient design problem
 * @author Russell Bent
 */
public class MIPResilienceAlgorithm extends ResilienceAlgorithm {
  
  private MathematicalProgramFlags flags = null;
  private MathematicalProgram problem = null;
  
  /**
   * Constructor
   */
  public MIPResilienceAlgorithm(Collection<Scenario> scenarios) {
    super(scenarios);
    flags = new MathematicalProgramFlags();
  }

  @Override
  public boolean solve(ElectricPowerModel model) {
    try {      
      createMathematicalProgram(model);
      
      Timer timer = new Timer();
      timer.startTimer();
      
      Solution solution = problem.solve();
      
      setIsFeasible(solution.isFeasible());
      setLastObjectiveValue(solution.getObjectiveValue());
      
      performOuterAssignments(model, problem, solution);
      for (Scenario scenario : getScenarios()) {
        performInnerAssignments(model,problem,solution,scenario);
      }
    }
    catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    return true;    
  }

  /**
   * Add a math program flag
   * @param key
   * @param object
   */
  public void addMathProgramFlag(String key, Object object) {
    flags.put(key, object);
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
  protected void createMathematicalProgram(ElectricPowerModel model) throws InvalidObjectiveException, VariableExistsException, InvalidVariableException, NoVariableException, InvalidConstraintException, NoConstraintException {
    problem = DefaultMathematicalProgramFactory.getInstance().constructMathematicalProgram(flags);

    LinearObjective objective = new LinearObjectiveMaximize();
    problem.setLinearObjective(objective);

    // variables of the outer problem
    addOuterVariables(model, problem);
               
    // objective function 
    addOuterObjectiveFunction(model, problem);
         
    // create the bounds of the outer problem
    addOuterVariableBounds(model, problem, 1.0);
        
    // create the constraints associated with the outer problem
    addOuterConstraints(model, problem);
    
    for (Scenario scenario : getScenarios()) {
      addInnerVariables(model, problem, scenario);
      addInnerObjectiveFunction(model, problem, scenario);
      addInnerVariableBounds(model, problem, scenario);
      addInnerConstraints(model, problem, scenario);
    }
  }
   
  @Override
  protected void addInnerConstraints(ElectricPowerModel model, MathematicalProgram problem, Scenario scenario) throws VariableExistsException, NoVariableException, InvalidConstraintException {
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

