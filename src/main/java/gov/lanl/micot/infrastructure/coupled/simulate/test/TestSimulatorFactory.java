package gov.lanl.micot.infrastructure.coupled.simulate.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Vector;

import gov.lanl.micot.infrastructure.coupled.simulate.CoupledSimulatorFactoryImpl;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.project.ProjectConfiguration;
import gov.lanl.micot.infrastructure.project.ScenarioConfiguration;
import gov.lanl.micot.infrastructure.project.SimulatorConfiguration;
import gov.lanl.micot.infrastructure.simulate.Simulator;
import gov.lanl.micot.infrastructure.simulate.SimulatorFactory;
import gov.lanl.micot.infrastructure.simulate.SimulatorFlags;

/**
 * A factory for creating Ieiss Simulations
 * @author Russell Bent
 */
public class TestSimulatorFactory extends CoupledSimulatorFactoryImpl {
	
	/**
	 *  Constructor
	 */
	public TestSimulatorFactory() {		
	}
	
	@Override
	public TestSimulator createSimulator(SimulatorFlags flags) throws IOException {
	  TestSimulatorFlags f = new TestSimulatorFlags(flags);
	  TestSimulator simulator = new TestSimulator();
		return simulator;
	}

	
}
