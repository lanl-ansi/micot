package gov.lanl.micot.infrastructure.ep.application.opf;

import gov.lanl.micot.infrastructure.application.Application;
import gov.lanl.micot.infrastructure.application.ApplicationOutput;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.ep.optimize.ElectricPowerMathProgramOptimizer;
import gov.lanl.micot.infrastructure.model.Asset;
import gov.lanl.micot.infrastructure.optimize.Optimizer;

/**
 * This class defines the optimal power flow as an application
 * @author Russell Bent
 *
 */
public class OPFApplication implements Application {
  
  public static final String MODEL_FLAG = "Model";
  public static final String OBJECTIVE_FLAG = "Objective";
  public static final String IS_OPTIMIZATION_FEASIBLE_FLAG = "IsOptimizationFeasible";
   
  private ElectricPowerModel model = null;
  private  Optimizer<ElectricPowerNode, ElectricPowerModel> algorithm = null;
    
  /**
   * Only one way to instantiate it
   */
  protected OPFApplication() {    
  }
  
  @Override
  public ApplicationOutput execute() {
    // make the actual status the desired status to start out....
    for (Asset asset : model.getAssets()) {
      asset.setActualStatus(asset.getDesiredStatus());
    }
    
    // solve the optimization
    algorithm.solve(model);
         
    ApplicationOutput output = new ApplicationOutput();
    output.put(OBJECTIVE_FLAG, algorithm.getObjectiveValue());
    output.put(IS_OPTIMIZATION_FEASIBLE_FLAG, algorithm.isFeasible());
    output.put(MODEL_FLAG, model);
    return output;
  }

  /**
   * Set the model
   * @param model
   */
  public void setModel(ElectricPowerModel model) {
    this.model = model;
  }

  /**
   * Sets the optimization algorithm
   * @param optimizer
   */
  public void setAlgorithm( Optimizer<ElectricPowerNode, ElectricPowerModel> optimizer) {
    this.algorithm = optimizer;
  }  
}
