package gov.lanl.micot.infrastructure.naturalgas.application.ogf;

import gov.lanl.micot.infrastructure.application.Application;
import gov.lanl.micot.infrastructure.application.ApplicationOutput;
import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasModel;
import gov.lanl.micot.infrastructure.naturalgas.optimize.NaturalGasMathProgramOptimizer;

/**
 * This class defines the optimal gas flow as an application
 * @author Russell Bent
 *
 */
public class OGFApplication implements Application {
  
  public static final String MODEL_FLAG = "Model";
  public static final String OBJECTIVE_FLAG = "Objective";
  public static final String IS_OPTIMIZATION_FEASIBLE_FLAG = "IsOptimizationFeasible";
   
  private NaturalGasModel model = null;
  private NaturalGasMathProgramOptimizer algorithm = null;
    
  /**
   * Only one way to instantiate it
   */
  protected OGFApplication() {    
  }
  
  @Override
  public ApplicationOutput execute() {
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
  public void setModel(NaturalGasModel model) {
    this.model = model;
  }

  /**
   * Sets the optimization algorithm
   * @param optimizer
   */
  public void setAlgorithm(NaturalGasMathProgramOptimizer optimizer) {
    this.algorithm = optimizer;
  }  
}
