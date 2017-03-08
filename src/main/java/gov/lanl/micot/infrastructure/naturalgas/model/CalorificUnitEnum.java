package gov.lanl.micot.infrastructure.naturalgas.model;

/**
 * Enum for calorific units
 * @author Russell Bent
 */
public enum CalorificUnitEnum {

  M3_PER_M_CUBE_TYPE(0);
	
	private int calorificType = -1;
  
  /**
   * Private constructor
   * @param value
   */
  private CalorificUnitEnum(int value) {
    calorificType = value;
  }

  public static CalorificUnitEnum getEnum(int i) {
    for (CalorificUnitEnum pti : values()) {
      if (pti.calorificType == i) {
        return pti;
      }
    }
    return null;
  }
  
  /**
   * Get the generator type
   * @return
   */
  public int getCalorificType() {
    return calorificType;
  }
}
