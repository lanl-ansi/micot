package gov.lanl.micot.infrastructure.naturalgas.simulate;

import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasModel;
import gov.lanl.micot.infrastructure.naturalgas.optimize.NaturalGasMathProgramOptimizer;

/**
 * This class contains basic information about an opf simulator
 * maximizes load served
 * 
 * @author Russell Bent
 */
public class OptimalGasFlowSimulator extends NaturalGasSimulatorImpl {

  private NaturalGasMathProgramOptimizer optimizer = null;
    
  /**
   * Constructor
   * 
   * @param nextGenerationPFWFilename
   */
  protected OptimalGasFlowSimulator(NaturalGasMathProgramOptimizer optimizer) {
    super();
    this.optimizer = optimizer;
  }
  
  @Override
  protected SimulatorSolveState simulateModel(NaturalGasModel model) {
    return optimizer.solve(model) ? SimulatorSolveState.CONVERGED_SOLUTION : SimulatorSolveState.ERROR_SOLUTION;    
  }
  
  /**
   * Get last CPU time
   * @return
   */
  public double getLastCPUTime() {
    return optimizer.getCPUTime();
  }
  
  /**
   * Get the last objective value
   * @return
   */
  public double getLastObjectiveValue() {
    return optimizer.getObjectiveValue();
  }
  
  /**
   * A function for getting summary statistics about the performance of the algorithm
   * @return
   */
  public int getLastAlgorithmIterations() {   
    return 1;
  }
}
