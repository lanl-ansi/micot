package gov.lanl.micot.infrastructure.ep.optimize;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.optimize.mathprogram.MathProgramOptimizer;

/**
 * This class contains basic information about an opf simulator
 * maximizes load served
 * 
 * @author Russell Bent
 */
public class ElectricPowerMathProgramOptimizer extends MathProgramOptimizer<ElectricPowerNode, ElectricPowerModel> {

  
  /**
   * Constructor
   * 
   * @param nextGenerationPFWFilename
   */
  protected ElectricPowerMathProgramOptimizer() {
    super();
  }

  
}
