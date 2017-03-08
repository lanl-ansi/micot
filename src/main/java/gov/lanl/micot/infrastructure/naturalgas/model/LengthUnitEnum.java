package gov.lanl.micot.infrastructure.naturalgas.model;

/**
 * Enum for length units
 * @author Russell Bent
 */
public enum LengthUnitEnum {

  KM_TYPE(0),
  MM_TYPE(1);
	
	private int lengthType = -1;
  
  /**
   * Private constructor
   * @param value
   */
  private LengthUnitEnum(int value) {
    lengthType = value;
  }

  public static LengthUnitEnum getEnum(int i) {
    for (LengthUnitEnum pti : values()) {
      if (pti.lengthType == i) {
        return pti;
      }
    }
    return null;
  }
  
  /**
   * Get the length type
   * @return
   */
  public int getLengthType() {
    return lengthType;
  }
}
