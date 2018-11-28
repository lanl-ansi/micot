package gov.lanl.micot.application.rdt.algorithm.ep.sbd2;

import gov.lanl.micot.application.rdt.algorithm.AlgorithmConstants;
import gov.lanl.micot.application.rdt.algorithm.ResilienceAlgorithm;
import gov.lanl.micot.application.rdt.algorithm.ep.constraint.scenario.BusBalanceConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.constraint.scenario.GeneratorConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.constraint.scenario.LinDistFlowConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.constraint.scenario.LinDistSlackOnOffConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.constraint.scenario.LineActiveTieConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.constraint.scenario.LineCapacityConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.constraint.scenario.LineDirectionConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.constraint.scenario.LineHardenExistTieConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.constraint.scenario.LineSwitchExistTieConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.constraint.scenario.PhaseVariationConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.mip2.constraint.GeneratorTieConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.mip2.constraint.LineConstructionTieConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.mip2.constraint.LineHardenTieConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.mip2.constraint.LineSwitchTieConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.sbd2.bound.GeneratorConstructionBound;
import gov.lanl.micot.application.rdt.algorithm.ep.sbd2.bound.LineConstructionBound;
import gov.lanl.micot.application.rdt.algorithm.ep.sbd2.bound.LineHardenBound;
import gov.lanl.micot.application.rdt.algorithm.ep.sbd2.bound.LineSwitchBound;
import gov.lanl.micot.application.rdt.algorithm.ep.sbd2.bound.LoadSlackBound;
import gov.lanl.micot.application.rdt.algorithm.ep.sbd2.constraint.CriticalLoadSlackConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.sbd2.constraint.NonCriticalLoadSlackConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.sbd2.objective.LoadSlackObjective;
import gov.lanl.micot.application.rdt.algorithm.ep.sbd2.variable.LoadSlackVariableFactory;
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
 * Scenario Based Decomposition algorithm for solving the resilient design problem
 * @author Russell Bent
 */
public class SBDResilienceAlgorithm extends ResilienceAlgorithm {
  
  private HashMap<Scenario, Solution> innerSolutions = null;
    
  private MathematicalProgramFlags innerFlags = null;
  private MathematicalProgramFlags outerFlags = null;

  private MathematicalProgram outerProblem = null;
  private MathematicalProgram innerProblem = null;

  private Solution outerSolution = null;
  private Collection<Scenario> outerScenarios = null;
  private Collection<Scenario> innerScenarios = null;
  
  /**
   * Constructor
   */
  public SBDResilienceAlgorithm(Collection<Scenario> scenarios) {
    super(scenarios);
    innerFlags = new MathematicalProgramFlags();
    outerFlags = new MathematicalProgramFlags();
  }

  @Override
  public boolean solve(ElectricPowerModel model) {
    try {
      
      innerSolutions = new HashMap<Scenario, Solution>();
      outerScenarios = new ArrayList<Scenario>();
      innerScenarios = new ArrayList<Scenario>();
      innerScenarios.addAll(getScenarios());
      
      boolean solved = false;
      
      while (!solved) {
        createOuterMathematicalProgram(model);
      
        Timer timer = new Timer();
        timer.startTimer();
      
        outerSolution = outerProblem.solve();
        performOuterAssignments(model, outerProblem, outerSolution);
        for (Scenario scenario : outerScenarios) {
          performInnerAssignments(model,outerProblem, outerSolution, scenario);
        }   

        innerSolutions = new HashMap<Scenario, Solution>();
        for (Scenario scenario : innerScenarios) {
          createInnerMathematicalProgram(model, scenario);
          Solution innerSolution = innerProblem.solve();
          innerSolutions.put(scenario, innerSolution);
          performInnerAssignments(model,innerProblem, innerSolution, scenario);
        }
        
        solved = checkFeasible();
        if (!solved) {
          Scenario scenario = chooseScenario();
          outerScenarios.add(scenario);
          innerScenarios.remove(scenario);
        }        
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }

    setIsFeasible(outerSolution.isFeasible());
    setLastObjectiveValue(outerSolution.getObjectiveValue());
    
    return true;    
  }

  /**
   *check for feasability
   * @return
   */
  private boolean checkFeasible() {
    for (Solution solution : innerSolutions.values()) {
      if (solution.getObjectiveValue() > 1e-6) {
        return false;
      }
    }
    return true;
  }

  /**
   * Find the scenario to add
   * @return
   */
  private Scenario chooseScenario() {
    Scenario scenario = null;
    for (Scenario s : innerScenarios) {
      if (scenario == null || innerSolutions.get(s).getObjectiveValue() > innerSolutions.get(scenario).getObjectiveValue()) {
        scenario = s;
      }
    }
    return scenario;
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
    for (Scenario scenario : outerScenarios) {
      super.addInnerVariables(model, problem, scenario);
    }
  }
  
  @Override
  protected void addOuterVariableBounds(ElectricPowerModel model, MathematicalProgram problem, double upperBound) throws VariableExistsException, NoVariableException {
    super.addOuterVariableBounds(model, problem, upperBound);
    for (Scenario scenario : outerScenarios) {
      super.addInnerVariableBounds(model, problem, scenario);
    }
  }
  
  @Override
  protected void addOuterConstraints(ElectricPowerModel model, MathematicalProgram problem) throws VariableExistsException, NoVariableException, InvalidConstraintException {
    super.addOuterConstraints(model, problem);
    for (Scenario scenario : outerScenarios) {
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
  
  @Override
  protected void addInnerVariables(ElectricPowerModel model, MathematicalProgram problem, Scenario scenario) throws VariableExistsException, InvalidVariableException {
    super.addInnerVariables(model, problem, scenario);
    LoadSlackVariableFactory factory = new LoadSlackVariableFactory(scenario);
    factory.createVariables(problem, model);
  }
  
  @Override
  protected void addInnerVariableBounds(ElectricPowerModel model, MathematicalProgram problem, Scenario scenario) throws VariableExistsException, NoVariableException {
    super.addInnerVariableBounds(model, problem, scenario);
    LoadSlackBound bound = new LoadSlackBound(scenario);
    GeneratorConstructionBound generator = new GeneratorConstructionBound(scenario, outerSolution, outerProblem); 
    LineConstructionBound line = new LineConstructionBound(scenario, outerSolution, outerProblem); 
    LineSwitchBound lswitch = new LineSwitchBound(scenario, outerSolution, outerProblem); 
    LineHardenBound harden = new LineHardenBound(scenario, outerSolution, outerProblem); 
    
    bound.constructConstraint(problem, model);
    generator.constructConstraint(problem, model);
    lswitch.constructConstraint(problem, model);
    harden.constructConstraint(problem, model);
    line.constructConstraint(problem, model);
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
    LoadSlackObjective slack = new LoadSlackObjective(scenario);    
    slack.addCoefficients(problem, model);   
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

    LinearObjective objective = new LinearObjectiveMinimize();
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

  protected void addInnerConstraints(ElectricPowerModel model, MathematicalProgram problem, Scenario scenario) throws VariableExistsException, NoVariableException, InvalidConstraintException {
    LineSwitchExistTieConstraint switchExistTie = new LineSwitchExistTieConstraint(scenario);
    LineActiveTieConstraint activeTie = new LineActiveTieConstraint(scenario);
    LineHardenExistTieConstraint hardenTie = new LineHardenExistTieConstraint(scenario);
    CriticalLoadSlackConstraint critical = new CriticalLoadSlackConstraint(getCriticalLoadMet(), scenario);
    NonCriticalLoadSlackConstraint nonCritical = new NonCriticalLoadSlackConstraint(getNonCriticalLoadMet(), scenario);
    GeneratorConstraint generator = new GeneratorConstraint(scenario);
    BusBalanceConstraint balance = new BusBalanceConstraint(scenario);
    LineCapacityConstraint capacity = new LineCapacityConstraint(scenario);
    LineDirectionConstraint direction = new LineDirectionConstraint(scenario); 
    LinDistSlackOnOffConstraint slack = new LinDistSlackOnOffConstraint(scenario);
    LinDistFlowConstraint distflow = new LinDistFlowConstraint(scenario);
    PhaseVariationConstraint variation = new PhaseVariationConstraint(getPhaseVariationThreshold(), scenario);
    
    switchExistTie.constructConstraint(problem, model);
    activeTie.constructConstraint(problem, model);
    hardenTie.constructConstraint(problem, model);
    critical.constructConstraint(problem, model);
    nonCritical.constructConstraint(problem, model);
    generator.constructConstraint(problem, model);
    balance.constructConstraint(problem, model);
    capacity.constructConstraint(problem, model);
    direction.constructConstraint(problem, model);
    variation.constructConstraint(problem, model);
    
    if (getFlowModel() == AlgorithmConstants.LINDIST_FLOW_POWER_FLOW_MODEL) {
      slack.constructConstraint(problem, model);
      distflow.constructConstraint(problem, model);
    }
    
    try {
      addCutsetCycleConstraints(model,problem,scenario);
    }
    catch (InvalidVariableException e) {
      e.printStackTrace();
    }
  }

  
}

