package gov.lanl.micot.infrastructure.coupled.simulate.test;

import java.util.Collection;
import java.util.Vector;

import gov.lanl.micot.infrastructure.coupled.model.CoupledComponent;
import gov.lanl.micot.infrastructure.coupled.model.CoupledModel;
import gov.lanl.micot.infrastructure.coupled.model.CoupledNode;
import gov.lanl.micot.infrastructure.coupled.simulate.CoupledSimulatorImpl;
import gov.lanl.micot.infrastructure.model.Edge;

/**
 * This is an instantion of the test simulator that just prints out some statistics
 * packages
 * @author Russell Bent
 */
public class TestSimulator extends CoupledSimulatorImpl {

  /**
	 * Constructor
	 * @param nextGenerationPFWFilename
	 */
	protected TestSimulator() {
	}
	
	@Override
	protected SimulatorSolveState simulateModel(CoupledModel m) {
	  System.out.println("This is a simulator that does absolutely nothing.  It is just exercising the ability to create a project with a coupled model that calls some simulator");
	  
	  for (Edge edge : m.getEdges()) {
	    CoupledNode node1 = m.getFirstNode(edge);
	    CoupledNode node2 = m.getSecondNode(edge);
	    
	    CoupledComponent c1 = (CoupledComponent)node1.getPrimaryComponent();
      CoupledComponent c2 = (CoupledComponent)node2.getPrimaryComponent();
	    
      System.out.println("There is a connection between " + c1.getAsset() + " (" + c1.getAsset().getClass().getSimpleName() + ") and " + c2.getAsset() + " (" + c2.getAsset().getClass().getSimpleName() + ") ");
      
	  }
    return SimulatorSolveState.CONVERGED_SOLUTION;
	}


}

