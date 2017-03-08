package gov.lanl.micot.application.rdt.algorithm.ep.cuttingplane;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.ep.optimize.ElectricPowerMathProgramOptimizer;
import gov.lanl.micot.infrastructure.ep.simulate.ElectricPowerSimulator;
import gov.lanl.micot.infrastructure.model.Model;
import gov.lanl.micot.infrastructure.optimize.OptimizerImpl;

/**
 * Implementation of a cutting plane algorithm for expansion planning algorithms
 * This is the simplest version of a cutting plane algorithm that has a black box simulation to verify 
 * solutions.  It simply generates no good cuts.
 * @author Russell Bent
 */
public class CuttingPlaneInfrastructureExpansionAlgorithm extends OptimizerImpl<ElectricPowerNode, ElectricPowerModel>  {

	private ElectricPowerMathProgramOptimizer mipModel = null;
	private ElectricPowerSimulator simulator = null;
  /**
   * Constructor
   */
  public CuttingPlaneInfrastructureExpansionAlgorithm() {
  }
  
  @Override
  public boolean solve(ElectricPowerModel model) {
  	boolean feasible = false;
  	do {
    	boolean mipResult = mipModel.solve((ElectricPowerModel)model);
      if (!mipResult) {
      	feasible = false;
      	return false;
      }	   	
    	simulator.executeSimulation(model);

    	feasible = checkModel(model);
    	if (!feasible) {
      	System.out.println("Need to Add a no good cut if the model is not feasible");
//    		addNoGoodCut(mipModel);
    	}
  	}
  	while (!feasible);
  	
  	setIsFeasible(feasible);
  	setLastObjectiveValue(mipModel.getObjectiveValue());  	
    return true;
  }
  
  /**
   * This function checks to see if a model is feasible or not....
   * @param model
   * @return
   */
  private boolean checkModel(Model model) {
  	System.out.println("Need to use hte simulation to check and see if the model is feasible or not");
  	return true;
  }  
}
