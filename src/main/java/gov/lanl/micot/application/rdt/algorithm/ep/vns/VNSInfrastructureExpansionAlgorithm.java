package gov.lanl.micot.application.rdt.algorithm.ep.vns;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.optimize.ElectricPowerMathProgramOptimizer;

/**
 * Abstract implementation of algorithm interface
 * @author Russell Bent
 */
public class VNSInfrastructureExpansionAlgorithm extends ElectricPowerMathProgramOptimizer {

	private double maxTime = 0;
	private int maxRestarts = 0;
	private int maxIterations = 0;
	private double neighborhoodNormalize = 0;
	private String linearRelaxationFactoryName = "gov.lanl.micot.util.math.solver.linearprogram.cplex.CPLEXLinearProgramFactory"; 
		
  /**
   * Constructor
   */
  public VNSInfrastructureExpansionAlgorithm() {
    super();
  }
  
  @Override
  public void solveIsland(ElectricPowerModel m) {
    
//  dddd;
    
    
  }

  /**
   * Get the max time
   * @return
   */
  public double getMaxTime() {
    return maxTime;
  }

  /**
   * Set the max time
   * @param maxTime
   */
  public void setMaxTime(double maxTime) {
    this.maxTime = maxTime;
  }

  /**
   * get the max restarts
   * @return
   */
  public int getMaxRestarts() {
    return maxRestarts;
  }

  /**
   * set the max restarts
   * @param maxRestarts
   */
  public void setMaxRestarts(int maxRestarts) {
    this.maxRestarts = maxRestarts;
  }

  /**
   * get the max iterations
   * @return
   */
  public int getMaxIterations() {
    return maxIterations;
  }

  /**
   * set the max iterations
   * @param maxIterations
   */
  public void setMaxIterations(int maxIterations) {
    this.maxIterations = maxIterations;
  }

  /**
   * Get the neighborhood normalize
   * @return
   */
  public double getNeighborhoodNormalize() {
    return neighborhoodNormalize;
  }

  /**
   * set the neighborhood normalize
   * @param neighborhoodNormalize
   */
  public void setNeighborhoodNormalize(double neighborhoodNormalize) {
    this.neighborhoodNormalize = neighborhoodNormalize;
  }

  /**
   * get linear relaxation
   * @return
   */
  public String getLinearRelaxationFactoryName() {
    return linearRelaxationFactoryName;
  }

  /**
   * set the linear relaxation factory name
   * @param linearRelaxationFactoryName
   */
  public void setLinearRelaxationFactoryName(String linearRelaxationFactoryName) {
    this.linearRelaxationFactoryName = linearRelaxationFactoryName;
  }

  
}
