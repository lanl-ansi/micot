package gov.lanl.micot.infrastructure.optimize;

import gov.lanl.micot.infrastructure.model.Asset;
import gov.lanl.micot.infrastructure.model.Scenario;


/**
 * A class for static methods and constant that are useful for electric power optimization
 * @author 210117
 *
 */
public class OptimizationConstants {

  public static String SHADOW_PRICE_KEY       = "SHADOW_PRICE";
  public static String SHADOW_PRICE_RANGE_KEY = "SHADOW_PRICE_RANGE";

  
  /**
   * A general purpose function for determining if all assets we care about are active or not... this 
   * generally used as a check to see if we should create constraints or variables
   * @param scenario
   * @param assets
   * @return
   */
  public static boolean allActive(Scenario scenario, Asset... assets) {
    if (scenario == null) {
      for (Asset asset : assets) {
        if (!asset.getStatus()) {
          return false;
        }
      }
    }
    else {
      for (Asset asset : assets) {
        if (!scenario.computeActualStatus(asset, asset.getStatus())) {
          return false;
        }
      }        
    }
    return true;
  }

}
