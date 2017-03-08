package gov.lanl.micot.infrastructure.naturalgas.optimize.ogf;

import java.util.ArrayList;
import java.util.Collection;

import gov.lanl.micot.infrastructure.naturalgas.optimize.NaturalGasMathProgramOptimizerFlags;
import gov.lanl.micot.infrastructure.naturalgas.optimize.ogf.assignment.scenario.CompressorRatioScenarioAssignmentFactory;
import gov.lanl.micot.infrastructure.naturalgas.optimize.ogf.assignment.scenario.ComputedDirectionScenarioAssignmentFactory;
import gov.lanl.micot.infrastructure.naturalgas.optimize.ogf.assignment.scenario.FlowScenarioAssignmentFactory;
import gov.lanl.micot.infrastructure.naturalgas.optimize.ogf.assignment.scenario.PressureScenarioAssignmentFactory;
import gov.lanl.micot.infrastructure.naturalgas.optimize.ogf.constraint.scenario.CompressorRatioLinearizationConstraint;
import gov.lanl.micot.infrastructure.naturalgas.optimize.ogf.constraint.scenario.ControlValvePressureConstraint;
import gov.lanl.micot.infrastructure.naturalgas.optimize.ogf.constraint.scenario.DegreeTwoFlowDirScenarioConstraint;
import gov.lanl.micot.infrastructure.naturalgas.optimize.ogf.constraint.scenario.FlowBalanceScenarioConstraint;
import gov.lanl.micot.infrastructure.naturalgas.optimize.ogf.constraint.scenario.FlowDirEitherOrConstraint;
import gov.lanl.micot.infrastructure.naturalgas.optimize.ogf.constraint.scenario.FlowDirScenarioBoundConstraint;
import gov.lanl.micot.infrastructure.naturalgas.optimize.ogf.constraint.scenario.FlowScenarioBoundConstraint;
import gov.lanl.micot.infrastructure.naturalgas.optimize.ogf.constraint.scenario.FlowScenarioDirectionConstraint;
import gov.lanl.micot.infrastructure.naturalgas.optimize.ogf.constraint.scenario.ParallelEdgeFlowDirScenarioConstraint;
import gov.lanl.micot.infrastructure.naturalgas.optimize.ogf.constraint.scenario.PressureDiffCutConstraint;
import gov.lanl.micot.infrastructure.naturalgas.optimize.ogf.constraint.scenario.PressureScenarioBoundConstraint;
import gov.lanl.micot.infrastructure.naturalgas.optimize.ogf.constraint.scenario.ShortPipeConstraint;
import gov.lanl.micot.infrastructure.naturalgas.optimize.ogf.constraint.scenario.SinkFlowDirScenarioConstraint;
import gov.lanl.micot.infrastructure.naturalgas.optimize.ogf.constraint.scenario.SourceFlowDirScenarioConstraint;
import gov.lanl.micot.infrastructure.naturalgas.optimize.ogf.constraint.scenario.ValveOperationFlowScenarioConstraint;
import gov.lanl.micot.infrastructure.naturalgas.optimize.ogf.constraint.scenario.ValveOperationPressureScenarioConstraint;
import gov.lanl.micot.infrastructure.naturalgas.optimize.ogf.constraint.scenario.ValveScenarioBoundConstraint;
import gov.lanl.micot.infrastructure.naturalgas.optimize.ogf.variable.scenario.FlowDirectionScenarioVariableFactory;
import gov.lanl.micot.infrastructure.naturalgas.optimize.ogf.variable.scenario.FlowScenarioVariableFactory;
import gov.lanl.micot.infrastructure.naturalgas.optimize.ogf.variable.scenario.PressureScenarioVariableFactory;
import gov.lanl.micot.infrastructure.naturalgas.optimize.ogf.variable.scenario.ValveScenarioVariableFactory;
import gov.lanl.micot.infrastructure.optimize.OptimizerFlags;
import gov.lanl.micot.infrastructure.optimize.mathprogram.assignment.AssignmentFactory;
import gov.lanl.micot.infrastructure.optimize.mathprogram.constraint.ConstraintFactory;
import gov.lanl.micot.infrastructure.optimize.mathprogram.objective.ObjectiveFunctionFactory;
import gov.lanl.micot.infrastructure.optimize.mathprogram.variable.VariableFactory;
//import gov.lanl.micot.util.math.solver.quadraticprogram.cplex.CPLEXQuadraticProgramFactory;

/**
 * Flags used for gas flow optimization
 * @author Russell Bent
 */
public class DefaultFlags extends  NaturalGasMathProgramOptimizerFlags {
	private static final long serialVersionUID = 1L;

	/** Constructor  */
	public DefaultFlags() {
		super();
		put(MATH_PROGRAM_FACTORY_KEY, "gov.lanl.micot.util.math.solver.quadraticprogram.cplex.CPLEXQuadraticProgramFactory");
	}
	
	/** Constructor
	 * @param flags
	 */
	public DefaultFlags(OptimizerFlags flags) {
		super(flags);
		if (get(MATH_PROGRAM_FACTORY_KEY) == null) {
		  put(MATH_PROGRAM_FACTORY_KEY, "gov.lanl.micot.util.math.solver.quadraticprogram.cplex.CPLEXQuadraticProgramFactory");
		}
	}

	@Override
	@SuppressWarnings("rawtypes")
	protected Collection<Class<? extends VariableFactory>> getDefaultVariableFactories() {
		ArrayList<Class<? extends VariableFactory>> defaults =  new ArrayList<Class<? extends VariableFactory>>();
    defaults.add(PressureScenarioVariableFactory.class); // p: pressure at node i \in N: 
    defaults.add(FlowScenarioVariableFactory.class); // x: gas flow through pipeline (i,j) \in A_p \cup A_c (base and new)    
    defaults.add(FlowDirectionScenarioVariableFactory.class); // betaPij and betaPji: flow direction variables; gas may flow from i to j or vice verse in pipe (i,j) \in A_p.
    defaults.add(ValveScenarioVariableFactory.class); // valve open/off variables   
		return defaults;
	}
	
	@Override
	@SuppressWarnings("rawtypes")
	protected Collection<Class<? extends ConstraintFactory>> getDefaultConstraintFactories() {
		ArrayList<Class<? extends ConstraintFactory>> defaults = new ArrayList<Class<? extends ConstraintFactory>>();
		defaults.add(PressureScenarioBoundConstraint.class); // Squared pressure bounds
    defaults.add(FlowDirScenarioBoundConstraint.class); // Eq.20: Making beta binary variables, pipes and CSs
    defaults.add(ValveScenarioBoundConstraint.class); // valve on/off constraint is 0,1
   
    defaults.add(FlowDirEitherOrConstraint.class); // Eq.8: Flow direction either or constraint
    defaults.add(FlowScenarioBoundConstraint.class); // Eq.21: Flow bounds, same for all the models
    defaults.add(FlowScenarioDirectionConstraint.class); // Eq.21: tie flow bounds to the directionality variables
    defaults.add(FlowBalanceScenarioConstraint.class); // Mass flow balance constraint, Eq.4
    defaults.add(PressureDiffCutConstraint.class); // add a cut for pressure differentials
    defaults.add(CompressorRatioLinearizationConstraint.class); // linearization of the compressor ratio constraints
    
    defaults.add(ParallelEdgeFlowDirScenarioConstraint.class); // MIP cut
    defaults.add(DegreeTwoFlowDirScenarioConstraint.class); // MIP Cuts
    defaults.add(SinkFlowDirScenarioConstraint.class); // MIP cuts
    defaults.add(SourceFlowDirScenarioConstraint.class); // MIP cuts
    
    defaults.add(ShortPipeConstraint.class);
    defaults.add(ValveOperationFlowScenarioConstraint.class);
    defaults.add(ValveOperationPressureScenarioConstraint.class);
    defaults.add(ControlValvePressureConstraint.class);
    
    return defaults;
	}

	@Override
	@SuppressWarnings("rawtypes")
	protected Collection<Class<? extends AssignmentFactory>> getDefaultAssignmentFactories() {
		ArrayList<Class<? extends AssignmentFactory>> defaults = new ArrayList<Class<? extends AssignmentFactory>>();
    defaults.add(FlowScenarioAssignmentFactory.class);
    defaults.add(PressureScenarioAssignmentFactory.class);
    defaults.add(CompressorRatioScenarioAssignmentFactory.class);
    defaults.add(ComputedDirectionScenarioAssignmentFactory.class);
		return defaults;
	}

	@Override
	@SuppressWarnings("rawtypes")
	protected Collection<Class<? extends ObjectiveFunctionFactory>> getDefaultObjectiveFunctionFactories() {
		ArrayList<Class<? extends ObjectiveFunctionFactory>> defaults = new ArrayList<Class<? extends ObjectiveFunctionFactory>>();
		return defaults;
	}

}
