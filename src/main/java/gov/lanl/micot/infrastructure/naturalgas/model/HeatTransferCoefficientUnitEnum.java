package gov.lanl.micot.infrastructure.naturalgas.model;

/**
 * Enum for molar mass units
 * @author Russell Bent
 */
public enum HeatTransferCoefficientUnitEnum {

  W_PER_M_SQUARE_PER_K_TYPE(0);
	
	private int coeffType = -1;
  
  /**
   * Private constructor
   * @param value
   */
  private HeatTransferCoefficientUnitEnum(int value) {
    coeffType = value;
  }

  public static HeatTransferCoefficientUnitEnum getEnum(int i) {
    for (HeatTransferCoefficientUnitEnum pti : values()) {
      if (pti.coeffType == i) {
        return pti;
      }
    }
    return null;
  }
  
  /**
   * Get the generator type
   * @return
   */
  public int getHeatTransferType() {
    return coeffType;
  }
}
