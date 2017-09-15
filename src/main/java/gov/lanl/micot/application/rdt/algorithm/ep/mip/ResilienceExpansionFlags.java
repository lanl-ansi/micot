package gov.lanl.micot.application.rdt.algorithm.ep.mip;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;

import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.optimize.OptimizerFlags;
import gov.lanl.micot.infrastructure.optimize.mathprogram.assignment.AssignmentFactory;
import gov.lanl.micot.infrastructure.optimize.mathprogram.constraint.ConstraintFactory;
import gov.lanl.micot.infrastructure.optimize.mathprogram.objective.ObjectiveFunctionFactory;
import gov.lanl.micot.infrastructure.optimize.mathprogram.variable.VariableFactory;
import gov.lanl.micot.application.rdt.algorithm.AlgorithmConstants;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.assignment.LineConstructionAssignmentFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.assignment.LineHardenAssignmentFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.assignment.LineSwitchAssignmentFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.assignment.MicrogridCapacityAssignmentFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.assignment.MicrogridConstructionAssignmentFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.assignment.MicrogridConstructionCapacityAssignmentFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.assignment.scenario.ScenarioRealFlowPhaseAssignmentFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.assignment.scenario.ScenarioReactiveFlowPhaseAssignmentFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.assignment.scenario.ScenarioGeneratorRealPhaseAssignmentFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.assignment.scenario.ScenarioLineInUseAssignmentFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.assignment.scenario.ScenarioGeneratorReactivePhaseAssignmentFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.assignment.scenario.ScenarioLineSwitchStatusAssignmentFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.assignment.scenario.ScenarioRealLoadPhaseAssignmentFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.assignment.scenario.ScenarioReactiveLoadPhaseAssignmentFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.constraint.LineConstructionBoundConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.constraint.LineHardenBoundConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.constraint.LineSwitchBoundConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.constraint.MicrogridBoundConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.constraint.MicrogridCapacityBoundConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.constraint.scenario.ScenarioChanceConstrainedCriticalRealLoadMetConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.constraint.scenario.ScenarioChanceConstrainedCriticalReactiveLoadMetConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.constraint.scenario.ScenarioChanceConstrainedRealLoadMetConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.constraint.scenario.ScenarioChanceConstrainedReactiveLoadMetConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.constraint.scenario.ScenarioChanceConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.constraint.scenario.ScenarioCriticalRealLoadMetConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.constraint.scenario.ScenarioCriticalReactiveLoadMetConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.constraint.scenario.ScenarioDiscreteRealGenerationConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.constraint.scenario.ScenarioLinDistFlowConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.constraint.scenario.ScenarioLineExistBoundConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.constraint.scenario.ScenarioLineExistLinkingConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.constraint.scenario.ScenarioLinePhaseCapacityConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.constraint.scenario.ScenarioDiscreteReactiveGenerationConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.constraint.scenario.ScenarioRealGenerationConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.constraint.scenario.ScenarioReactiveGenerationConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.constraint.scenario.ScenarioLineReactivePhaseCapacityConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.constraint.scenario.ScenarioLineRealPhaseCapacityConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.constraint.scenario.ScenarioLineSwitchBoundConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.constraint.scenario.ScenarioLineUseBoundConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.constraint.scenario.ScenarioLineUseLinkingConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.constraint.scenario.ScenarioRealLoadMetConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.constraint.scenario.ScenarioReactiveLoadMetConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.constraint.scenario.ScenarioLoadServeBoundConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.constraint.scenario.ScenarioRealLoadServeConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.constraint.scenario.ScenarioReactiveLoadServeConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.constraint.scenario.ScenarioMicrogridBoundConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.constraint.scenario.ScenarioMicrogridCapacityConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.constraint.scenario.ScenarioMicrogridCapacityLinkingConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.constraint.scenario.ScenarioMicrogridLinkingConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.constraint.scenario.ScenarioRealPhaseBalanceConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.constraint.scenario.ScenarioReactivePhaseBalanceConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.constraint.scenario.ScenarioPhaseRealFlowVariationConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.constraint.scenario.ScenarioPhaseReactiveFlowVariationConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.constraint.scenario.ScenarioSwitchLinkingConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.constraint.scenario.ScenarioVoltageBoundConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.constraint.scenario.ScenarioVoltageOnOffConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.constraint.scenario.cycle.ScenarioIncludeEdgeConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.constraint.scenario.cycle.ScenarioLineCycleBoundConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.constraint.scenario.cycle.ScenarioNoCycleConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.constraint.scenario.tree.ScenarioNodeFlowConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.constraint.scenario.tree.ScenarioTreeConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.constraint.scenario.tree.ScenarioTreeFlowCapacityConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.constraint.scenario.tree.ScenarioTreeFlowChoiceBoundConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.constraint.scenario.tree.ScenarioTreeLinkingConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.constraint.scenario.tree.ScenarioVirtualFlowBoundConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.constraint.scenario.tree.ScenarioVirtualFlowChoiceBoundConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.constraint.scenario.tree.ScenarioVirtualFlowConstraint;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.objective.LineConstructionObjectiveFunctionFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.objective.LineHardenObjectiveFunctionFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.objective.LineSwitchObjectiveFunctionFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.objective.MicrogridCapacityObjectiveFunctionFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.objective.MicrogridDiscreteObjectiveFunctionFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.objective.MicrogridObjectiveFunctionFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.variable.LineConstructionVariableFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.variable.LineHardenVariableFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.variable.LineSwitchVariableFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.variable.MicrogridCapacityVariableFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.variable.MicrogridVariableFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.variable.scenario.ScenarioChanceConstrainedVariableFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.variable.scenario.ScenarioRealFlowPhaseVariableFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.variable.scenario.ScenarioReactiveFlowPhaseVariableFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.variable.scenario.ScenarioGeneratorRealPhaseVariableFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.variable.scenario.ScenarioLineExistVariableFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.variable.scenario.ScenarioLineUseVariableFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.variable.scenario.ScenarioGeneratorReactivePhaseVariableFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.variable.scenario.ScenarioRealLoadPhaseVariableFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.variable.scenario.ScenarioSwitchVariableFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.variable.scenario.ScenarioVoltageOnOffVariableFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.variable.scenario.ScenarioVoltageVariableFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.variable.scenario.ScenarioReactiveLoadPhaseVariableFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.variable.scenario.ScenarioLoadServeVariableFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.variable.scenario.ScenarioMicrogridCapacityVariableFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.variable.scenario.ScenarioMicrogridVariableFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.variable.scenario.cycle.ScenarioLineCycleVariableFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.variable.scenario.tree.ScenarioTreeFlowChoiceVariableFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.variable.scenario.tree.ScenarioTreeFlowVariableFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.variable.scenario.tree.ScenarioVirtualFlowChoiceVariableFactory;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.variable.scenario.tree.ScenarioVirtualFlowVariableFactory;
import gov.lanl.micot.util.math.solver.quadraticprogram.scip.ScipQuadraticProgramFactory;

/**
 * Flags used for expansion planning algorithms that take into account the full optimization model
 * when computing the optimal expansion plan
 * 
 * @author Russell Bent
 */
public class ResilienceExpansionFlags extends MIPInfrastructureExpansionAlgorithmFlags {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 */
	public ResilienceExpansionFlags() {
		super();
		put(MATH_PROGRAM_FACTORY_KEY, getDefaultMathProgram());
		put(AlgorithmConstants.LOAD_MET_KEY, AlgorithmConstants.DEFAULT_LOAD_MET);
		put(AlgorithmConstants.CRITICAL_LOAD_MET_KEY, AlgorithmConstants.DEFAULT_CRITICAL_LOAD_MET);
		put(AlgorithmConstants.IS_DISCRETE_GENERATION_KEY, AlgorithmConstants.DEFAULT_IS_DISCRETE_GENERATION);
    put(AlgorithmConstants.IS_CHANCE_CONSTRAINT_KEY, AlgorithmConstants.DEFAULT_IS_CHANCE_CONSTRAINT);
    put(AlgorithmConstants.USE_CYCLE_ENUMERATION_CONSTRAINT_KEY, AlgorithmConstants.DEFAULT_USE_CYCLE_ENUMERATION_CONSTRAINT);
    put(AlgorithmConstants.POWER_FLOW_MODEL_KEY, AlgorithmConstants.DEFAULT_POWER_FLOW_MODEL);
	}

	@Override
  protected String getDefaultMathProgram() {
    return ScipQuadraticProgramFactory.class.getCanonicalName();
  }
	
	/**
	 * Constructor
	 * 
	 * @param flags
	 */
	public ResilienceExpansionFlags(OptimizerFlags flags) {
		super(flags);		
		if (get(MATH_PROGRAM_FACTORY_KEY) == null) {
		  put(MATH_PROGRAM_FACTORY_KEY, getDefaultMathProgram());
		}
		if (get(AlgorithmConstants.LOAD_MET_KEY) == null) {
			put(AlgorithmConstants.LOAD_MET_KEY, AlgorithmConstants.DEFAULT_LOAD_MET);
		}
		if (get(AlgorithmConstants.CRITICAL_LOAD_MET_KEY) == null) {
			put(AlgorithmConstants.CRITICAL_LOAD_MET_KEY, AlgorithmConstants.DEFAULT_CRITICAL_LOAD_MET);
		}
    if (get(AlgorithmConstants.IS_DISCRETE_GENERATION_KEY) == null) {
      put(AlgorithmConstants.IS_DISCRETE_GENERATION_KEY, AlgorithmConstants.DEFAULT_IS_DISCRETE_GENERATION);
    }
    if (get(AlgorithmConstants.IS_CHANCE_CONSTRAINT_KEY) == null) {
      put(AlgorithmConstants.IS_CHANCE_CONSTRAINT_KEY, AlgorithmConstants.DEFAULT_IS_CHANCE_CONSTRAINT);
    }
    if (get(AlgorithmConstants.USE_CYCLE_ENUMERATION_CONSTRAINT_KEY) == null) {
      put(AlgorithmConstants.USE_CYCLE_ENUMERATION_CONSTRAINT_KEY, AlgorithmConstants.DEFAULT_USE_CYCLE_ENUMERATION_CONSTRAINT);
    }
    if (get(AlgorithmConstants.POWER_FLOW_MODEL_KEY) == null) {
      put(AlgorithmConstants.POWER_FLOW_MODEL_KEY, AlgorithmConstants.DEFAULT_POWER_FLOW_MODEL);
    }

	}

	/**
	 * Add the microgrid generation variable factories
	 * @param defaults
	 */
	@SuppressWarnings("rawtypes")
  private void getMicrogridVariables(ArrayList<Class<? extends VariableFactory>> defaults) {
    if (get(AlgorithmConstants.IS_DISCRETE_GENERATION_KEY) == null) {
      put(AlgorithmConstants.IS_DISCRETE_GENERATION_KEY, AlgorithmConstants.DEFAULT_IS_DISCRETE_GENERATION);
    }
	  
    boolean isDiscreteGeneration = getBoolean(AlgorithmConstants.IS_DISCRETE_GENERATION_KEY);
    
    if (isDiscreteGeneration) {
      defaults.add(MicrogridVariableFactory.class);
      defaults.add(ScenarioMicrogridVariableFactory.class);
    }
    else {
      defaults.add(MicrogridCapacityVariableFactory.class);
      defaults.add(MicrogridVariableFactory.class);
      defaults.add(ScenarioMicrogridCapacityVariableFactory.class);
      defaults.add(ScenarioMicrogridVariableFactory.class);
    }    
	}
  
	/**
	 * Add chance constraint variables
	 * @param defaults
	 */
	@SuppressWarnings("rawtypes")
  private void getChanceConstraintVariables(ArrayList<Class<? extends VariableFactory>> defaults) {
    if (get(AlgorithmConstants.IS_CHANCE_CONSTRAINT_KEY) == null) {
      put(AlgorithmConstants.IS_CHANCE_CONSTRAINT_KEY, AlgorithmConstants.DEFAULT_IS_CHANCE_CONSTRAINT);
    }
	  
	  boolean isChanceConstraint = getBoolean(AlgorithmConstants.IS_CHANCE_CONSTRAINT_KEY);
    if (isChanceConstraint) {
      defaults.add(ScenarioChanceConstrainedVariableFactory.class);
    }
	}
	
	/**
   * Add cycle constraint variables
   * @param defaults
   */
  @SuppressWarnings("rawtypes")
  private void getCycleConstraintVariables(ArrayList<Class<? extends VariableFactory>> defaults) {
    if (get(AlgorithmConstants.USE_CYCLE_ENUMERATION_CONSTRAINT_KEY) == null) {
      put(AlgorithmConstants.USE_CYCLE_ENUMERATION_CONSTRAINT_KEY, AlgorithmConstants.DEFAULT_USE_CYCLE_ENUMERATION_CONSTRAINT);
    }
    
    boolean isEnumerationConstraint = getBoolean(AlgorithmConstants.USE_CYCLE_ENUMERATION_CONSTRAINT_KEY);
    if (isEnumerationConstraint) {
      defaults.add(ScenarioLineCycleVariableFactory.class);
    }
    else {
      defaults.add(ScenarioTreeFlowChoiceVariableFactory.class);
      defaults.add(ScenarioTreeFlowVariableFactory.class);
      defaults.add(ScenarioVirtualFlowChoiceVariableFactory.class);
      defaults.add(ScenarioVirtualFlowVariableFactory.class);
    }
  }

  /**
   * Determines what volatage variables to add to the model
   * @param defaults
   */
  private void getVoltageVariables(ArrayList<Class<? extends VariableFactory>> defaults) {
    if (get(AlgorithmConstants.POWER_FLOW_MODEL_KEY) == null) {
      put(AlgorithmConstants.POWER_FLOW_MODEL_KEY, AlgorithmConstants.DEFAULT_POWER_FLOW_MODEL);
    }
    
    String powerflow = getString(AlgorithmConstants.POWER_FLOW_MODEL_KEY);
    
    if (powerflow.equals(AlgorithmConstants.LINDIST_FLOW_POWER_FLOW_MODEL)) {
      defaults.add(ScenarioVoltageVariableFactory.class);
      defaults.add(ScenarioVoltageOnOffVariableFactory.class);    
    }
  }
	
	@Override
  @SuppressWarnings("rawtypes")
  protected Collection<Class<? extends VariableFactory>> getDefaultVariableFactories() {
    ArrayList<Class<? extends VariableFactory>> defaults = new ArrayList<Class<? extends VariableFactory>>();
    defaults.add(LineConstructionVariableFactory.class);
    defaults.add(LineSwitchVariableFactory.class);
    defaults.add(LineHardenVariableFactory.class);    
    defaults.add(ScenarioLineUseVariableFactory.class);
    defaults.add(ScenarioSwitchVariableFactory.class);    
    defaults.add(ScenarioLineExistVariableFactory.class);    
    defaults.add(ScenarioGeneratorRealPhaseVariableFactory.class);
    defaults.add(ScenarioGeneratorReactivePhaseVariableFactory.class);
    defaults.add(ScenarioRealLoadPhaseVariableFactory.class);
    defaults.add(ScenarioReactiveLoadPhaseVariableFactory.class);
    defaults.add(ScenarioLoadServeVariableFactory.class);
    defaults.add(ScenarioRealFlowPhaseVariableFactory.class);
    defaults.add(ScenarioReactiveFlowPhaseVariableFactory.class);
        
    getMicrogridVariables(defaults);
    getChanceConstraintVariables(defaults);
    getCycleConstraintVariables(defaults);
    getVoltageVariables(defaults);
    
    return defaults;
  }

	/**
   * Add the microgrid generation constraint factories
   * @param defaults
   */
  @SuppressWarnings("rawtypes")
  private void getMicrogridConstraints(ArrayList<Class<? extends ConstraintFactory>> defaults) {
    boolean isDiscreteGeneration = getBoolean(AlgorithmConstants.IS_DISCRETE_GENERATION_KEY);    
    if (isDiscreteGeneration) {
      defaults.add(MicrogridBoundConstraint.class);
      defaults.add(ScenarioDiscreteRealGenerationConstraint.class);
      defaults.add(ScenarioDiscreteReactiveGenerationConstraint.class);
    }
    else {
      defaults.add(MicrogridCapacityBoundConstraint.class);
      defaults.add(MicrogridBoundConstraint.class);
      defaults.add(ScenarioMicrogridCapacityLinkingConstraint.class);
      defaults.add(ScenarioRealGenerationConstraint.class);
      defaults.add(ScenarioReactiveGenerationConstraint.class);
    }    
  }
	
  
  /**
   * Add the cycle constraint factories
   * @param defaults
   */
  @SuppressWarnings("rawtypes")
  private void getCycleConstraints(ArrayList<Class<? extends ConstraintFactory>> defaults) {
    boolean isCycleEnumeration = getBoolean(AlgorithmConstants.USE_CYCLE_ENUMERATION_CONSTRAINT_KEY);    
    if (isCycleEnumeration) {
      defaults.add(ScenarioLineCycleBoundConstraint.class);
      defaults.add(ScenarioNoCycleConstraint.class);
      defaults.add(ScenarioIncludeEdgeConstraint.class);
    }
    else {
      defaults.add(ScenarioTreeLinkingConstraint.class);
      defaults.add(ScenarioNodeFlowConstraint.class);
      defaults.add(ScenarioTreeConstraint.class);
      defaults.add(ScenarioTreeFlowCapacityConstraint.class);
      defaults.add(ScenarioTreeFlowChoiceBoundConstraint.class);
      defaults.add(ScenarioVirtualFlowBoundConstraint.class);
      defaults.add(ScenarioVirtualFlowChoiceBoundConstraint.class);
      defaults.add(ScenarioVirtualFlowConstraint.class);
    }    
  }

  /**
   * Get the constraints associated with voltage
   * @param defaults
   */
  private void getVoltageConstraints(ArrayList<Class<? extends ConstraintFactory>> defaults) {
    String powerflow = getString(AlgorithmConstants.POWER_FLOW_MODEL_KEY);    
    if (powerflow.equals(AlgorithmConstants.LINDIST_FLOW_POWER_FLOW_MODEL)) {
      defaults.add(ScenarioVoltageOnOffConstraint.class);    
      defaults.add(ScenarioLinDistFlowConstraint.class);    
    }
  }
  
  @Override
  @SuppressWarnings("rawtypes")
  protected Collection<Class<? extends ConstraintFactory>> getDefaultConstraintFactories() {
    ArrayList<Class<? extends ConstraintFactory>> defaults = new ArrayList<Class<? extends ConstraintFactory>>();
    defaults.add(LineConstructionBoundConstraint.class);
    defaults.add(LineHardenBoundConstraint.class);
    defaults.add(LineSwitchBoundConstraint.class);
    
    defaults.add(ScenarioLineUseBoundConstraint.class);    
    defaults.add(ScenarioLineSwitchBoundConstraint.class);
    defaults.add(ScenarioLineExistBoundConstraint.class);
    
    
    defaults.add(ScenarioMicrogridBoundConstraint.class);
    defaults.add(ScenarioLoadServeBoundConstraint.class);
    defaults.add(ScenarioVoltageBoundConstraint.class); 
    defaults.add(ScenarioLineUseLinkingConstraint.class);    
    defaults.add(ScenarioSwitchLinkingConstraint.class);  
    defaults.add(ScenarioLineExistLinkingConstraint.class);
        
    defaults.add(ScenarioMicrogridLinkingConstraint.class);
    // including these help with numerical stability when turning off the lines
    defaults.add(ScenarioLineRealPhaseCapacityConstraint.class);
    defaults.add(ScenarioLineReactivePhaseCapacityConstraint.class);            
    defaults.add(ScenarioLinePhaseCapacityConstraint.class);    
    defaults.add(ScenarioRealLoadServeConstraint.class);
    defaults.add(ScenarioReactiveLoadServeConstraint.class);
    defaults.add(ScenarioRealPhaseBalanceConstraint.class);
    defaults.add(ScenarioReactivePhaseBalanceConstraint.class);

    defaults.add(ScenarioMicrogridCapacityConstraint.class);    
        
    getMicrogridConstraints(defaults);   
    getCycleConstraints(defaults);
    getVoltageConstraints(defaults);
        
    return defaults;
  }
  
  /**
   * Add the microgrid generation assignment factories
   * @param defaults
   */
  @SuppressWarnings("rawtypes")
  private void getMicrogridAssignmentFactories(ArrayList<Class<? extends AssignmentFactory>> defaults) {
    boolean isDiscreteGeneration = getBoolean(AlgorithmConstants.IS_DISCRETE_GENERATION_KEY);    
    if (isDiscreteGeneration) {
      defaults.add(MicrogridConstructionCapacityAssignmentFactory.class);
    }
    else {
      defaults.add(MicrogridCapacityAssignmentFactory.class);
      defaults.add(MicrogridConstructionAssignmentFactory.class);
    }    
  }
    
  @Override
  @SuppressWarnings("rawtypes")
  protected Collection<Class<? extends AssignmentFactory>> getDefaultAssignmentFactories() {
    ArrayList<Class<? extends AssignmentFactory>> defaults = new ArrayList<Class<? extends AssignmentFactory>>();
    defaults.add(ScenarioLineSwitchStatusAssignmentFactory.class);
    defaults.add(ScenarioRealLoadPhaseAssignmentFactory.class);       
    defaults.add(ScenarioReactiveLoadPhaseAssignmentFactory.class);       
    defaults.add(ScenarioRealFlowPhaseAssignmentFactory.class);   
    defaults.add(ScenarioReactiveFlowPhaseAssignmentFactory.class);   
    defaults.add(ScenarioGeneratorRealPhaseAssignmentFactory.class);   
    defaults.add(ScenarioGeneratorReactivePhaseAssignmentFactory.class);   
    defaults.add(ScenarioLineInUseAssignmentFactory.class);    
    
    defaults.add(LineConstructionAssignmentFactory.class);
    defaults.add(LineSwitchAssignmentFactory.class);
    defaults.add(LineHardenAssignmentFactory.class);   

    getMicrogridAssignmentFactories(defaults);
    return defaults;
  }

  
  /**
   * Add the microgrid generation objective function factories
   * @param defaults
   */
  @SuppressWarnings("rawtypes")
  private void getMicrogridObjectiveFunctionFactories(ArrayList<Class<? extends ObjectiveFunctionFactory>> defaults) {
    boolean isDiscreteGeneration = getBoolean(AlgorithmConstants.IS_DISCRETE_GENERATION_KEY);    
    if (isDiscreteGeneration) {
      defaults.add(MicrogridDiscreteObjectiveFunctionFactory.class);
    }
    else {
      defaults.add(MicrogridCapacityObjectiveFunctionFactory.class);
      defaults.add(MicrogridObjectiveFunctionFactory.class);
    }    
  }

  @Override
  @SuppressWarnings("rawtypes")
  protected Collection<Class<? extends ObjectiveFunctionFactory>> getDefaultObjectiveFunctionFactories() {
    ArrayList<Class<? extends ObjectiveFunctionFactory>> defaults = new ArrayList<Class<? extends ObjectiveFunctionFactory>>();
    defaults.add(LineConstructionObjectiveFunctionFactory.class);
    defaults.add(LineSwitchObjectiveFunctionFactory.class);
    defaults.add(LineHardenObjectiveFunctionFactory.class);
    getMicrogridObjectiveFunctionFactories(defaults);
    return defaults;
  }
  
  /**
   * Add chance constraint variables
   * @param defaults
   */
  private void addDefaultChanceConstraints(Collection<Object> constraintFactories) {
    boolean isChanceConstraint = getBoolean(AlgorithmConstants.IS_CHANCE_CONSTRAINT_KEY);
    if (isChanceConstraint) {
      boolean hasChance = false;   
      boolean hasRealLoadMetChance = false;
      boolean hasCriticalRealLoadMetChance = false;
      boolean hasReactiveLoadMetChance = false;
      boolean hasCriticalReactiveLoadMetChance = false;
      
      double chanceConstraintEpsilon = getDouble(AlgorithmConstants.CHANCE_CONSTRAINT_EPSILON_KEY);  
      double loadPercentage = getDouble(AlgorithmConstants.LOAD_MET_KEY);
      double criticalLoadPercentage = getDouble(AlgorithmConstants.CRITICAL_LOAD_MET_KEY);

      
      Collection<Scenario> scenarios = getCollection(SCENARIOS_KEY, Scenario.class);
      
      for (Object factory : constraintFactories) {             
        if (factory instanceof ScenarioChanceConstraint || factory.toString().equals(ScenarioChanceConstraint.class.getCanonicalName())) {
          hasChance = true;
        }     
        
        if (factory instanceof ScenarioChanceConstrainedRealLoadMetConstraint || factory.toString().equals(ScenarioChanceConstrainedRealLoadMetConstraint.class.getCanonicalName())) {
          hasRealLoadMetChance = true;
        }     
        
        if (factory instanceof ScenarioChanceConstrainedReactiveLoadMetConstraint || factory.toString().equals(ScenarioChanceConstrainedReactiveLoadMetConstraint.class.getCanonicalName())) {
          hasRealLoadMetChance = true;
        }     

        if (factory instanceof ScenarioChanceConstrainedCriticalRealLoadMetConstraint || factory.toString().equals(ScenarioChanceConstrainedCriticalRealLoadMetConstraint.class.getCanonicalName())) {
          hasCriticalReactiveLoadMetChance = true;
        }     
        
        if (factory instanceof ScenarioChanceConstrainedCriticalReactiveLoadMetConstraint || factory.toString().equals(ScenarioChanceConstrainedCriticalReactiveLoadMetConstraint.class.getCanonicalName())) {
          hasCriticalReactiveLoadMetChance = true;
        }     

      }
            
      if (!hasChance) {
        constraintFactories.add(new ScenarioChanceConstraint(chanceConstraintEpsilon, scenarios));
      }
      
      if (!hasRealLoadMetChance) {
        constraintFactories.add(new ScenarioChanceConstrainedRealLoadMetConstraint(loadPercentage, scenarios));
      }
      
      if (!hasReactiveLoadMetChance) {
        constraintFactories.add(new ScenarioChanceConstrainedReactiveLoadMetConstraint(loadPercentage, scenarios));
      }

      
      if (!hasCriticalRealLoadMetChance) {
        constraintFactories.add(new ScenarioChanceConstrainedCriticalRealLoadMetConstraint(criticalLoadPercentage, scenarios));
      }
      
      if (!hasCriticalReactiveLoadMetChance) {
        constraintFactories.add(new ScenarioChanceConstrainedCriticalReactiveLoadMetConstraint(criticalLoadPercentage, scenarios));
      }
      
    }
    else {
      boolean hasRealLoadMet = false;
      boolean hasCriticalRealLoad = false;
      boolean hasReactiveLoadMet = false;
      boolean hasCriticalReactiveLoad = false;
      
      double loadPercentage = getDouble(AlgorithmConstants.LOAD_MET_KEY);
      double criticalLoadPercentage = getDouble(AlgorithmConstants.CRITICAL_LOAD_MET_KEY);
      Collection<Scenario> scenarios = getCollection(SCENARIOS_KEY, Scenario.class);

      for (Object factory : constraintFactories) {
        
        if (factory instanceof ScenarioRealLoadMetConstraint || factory.toString().equals(ScenarioRealLoadMetConstraint.class.getCanonicalName())) {
          hasRealLoadMet = true;
        }
        
        if (factory instanceof ScenarioReactiveLoadMetConstraint || factory.toString().equals(ScenarioReactiveLoadMetConstraint.class.getCanonicalName())) {
          hasReactiveLoadMet = true;
        }

        
        if (factory instanceof ScenarioCriticalRealLoadMetConstraint || factory.toString().equals(ScenarioCriticalRealLoadMetConstraint.class.getCanonicalName())) {
          hasCriticalRealLoad = true;
        }   
        
        if (factory instanceof ScenarioCriticalReactiveLoadMetConstraint || factory.toString().equals(ScenarioCriticalReactiveLoadMetConstraint.class.getCanonicalName())) {
          hasCriticalReactiveLoad = true;
        }        

      }     
            
      if (!hasRealLoadMet) {
        constraintFactories.add(new ScenarioRealLoadMetConstraint(loadPercentage, scenarios));
      }
      
      if (!hasReactiveLoadMet) {
        constraintFactories.add(new ScenarioReactiveLoadMetConstraint(loadPercentage, scenarios));
      }
 
      
      if (!hasCriticalRealLoad) {
        constraintFactories.add(new ScenarioCriticalRealLoadMetConstraint(criticalLoadPercentage, scenarios));
      }

      if (!hasCriticalReactiveLoad) {
        constraintFactories.add(new ScenarioCriticalReactiveLoadMetConstraint(criticalLoadPercentage, scenarios));
      }

    }
  }

  @Override
  protected void addDefaultConstraints() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    super.addDefaultConstraints();
    
    Collection<Object> constraintFactories = getCollection(CONSTRAINTS_KEY, Object.class);

    double phaseVariation = getDouble(AlgorithmConstants.PHASE_VARIATION_KEY);
    Collection<Scenario> scenarios = getCollection(SCENARIOS_KEY, Scenario.class);
    
    boolean hasRealPhaseFlow = false;
    boolean hasReactivePhaseFlow = false;
    
    for (Object factory : constraintFactories) {
      if (factory instanceof ScenarioPhaseRealFlowVariationConstraint || factory.toString().equals(ScenarioPhaseRealFlowVariationConstraint.class.getCanonicalName())) {
        hasRealPhaseFlow = true;
      }     
      
      if (factory instanceof ScenarioPhaseReactiveFlowVariationConstraint || factory.toString().equals(ScenarioPhaseReactiveFlowVariationConstraint.class.getCanonicalName())) {
        hasReactivePhaseFlow = true;
      }     

    }        
        
    if (!hasRealPhaseFlow) {
      constraintFactories.add(new ScenarioPhaseRealFlowVariationConstraint(phaseVariation, scenarios));
    }
    
    if (!hasReactivePhaseFlow) {
      constraintFactories.add(new ScenarioPhaseReactiveFlowVariationConstraint(phaseVariation, scenarios));
    }

       
    addDefaultChanceConstraints(constraintFactories);    
    put(CONSTRAINTS_KEY, constraintFactories);
  }

  
}
