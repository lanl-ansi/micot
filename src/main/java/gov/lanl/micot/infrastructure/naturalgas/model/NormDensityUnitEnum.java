package gov.lanl.micot.infrastructure.naturalgas.model;

/**
 * Enum for norm density units
 * @author Russell Bent
 */
public enum NormDensityUnitEnum {

  KG_PER_M_BUBE_TYPE(0);
	
	private int densityType = -1;
  
  /**
   * Private constructor
   * @param value
   */
  private NormDensityUnitEnum(int value) {
    densityType = value;
  }

  public static NormDensityUnitEnum getEnum(int i) {
    for (NormDensityUnitEnum pti : values()) {
      if (pti.densityType == i) {
        return pti;
      }
    }
    return null;
  }
  
  /**
   * Get the generator type
   * @return
   */
  public int getDensityType() {
    return densityType;
  }
}
