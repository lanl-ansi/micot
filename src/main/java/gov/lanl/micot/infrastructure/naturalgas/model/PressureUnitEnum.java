package gov.lanl.micot.infrastructure.naturalgas.model;

/**
 * Enum for pressure units
 * @author Russell Bent
 */
public enum PressureUnitEnum {

	BAR_TYPE(0);
	
	private int pressureType = -1;
  
  /**
   * Private constructor
   * @param value
   */
  private PressureUnitEnum(int value) {
    pressureType = value;
  }

  public static PressureUnitEnum getEnum(int i) {
    for (PressureUnitEnum pti : values()) {
      if (pti.pressureType == i) {
        return pti;
      }
    }
    return null;
  }
  
  /**
   * Get the generator type
   * @return
   */
  public int getPressureType() {
    return pressureType;
  }
}
