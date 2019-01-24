package gov.lanl.micot.application.lpnorm.io;

/**
 * An enum for lpnorm fidelity
 * @author Russell Bent
 */
public enum LPNormFidelity {
  LOW, MEDIUM_LOW, MEDIUM, MEDIUM_HIGH, HIGH;
  
  /**
   * Map an int to an enum
   * @param i
   * @return
   */
  public static LPNormFidelity get(int i) {
    switch (i) { 
      case 1:
        return LOW;
      case 2:
        return MEDIUM_LOW;
      case 3:
        return MEDIUM;
      case 4:
        return MEDIUM_HIGH;
      case 5:
        return HIGH;
    }    
    return null;
  }
}
