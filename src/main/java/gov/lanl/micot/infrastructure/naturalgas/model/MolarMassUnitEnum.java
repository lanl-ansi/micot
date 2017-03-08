package gov.lanl.micot.infrastructure.naturalgas.model;

/**
 * Enum for molar mass units
 * @author Russell Bent
 */
public enum MolarMassUnitEnum {

  KG_PER_KMOL_TYPE(0);
	
	private int molarMassType = -1;
  
  /**
   * Private constructor
   * @param value
   */
  private MolarMassUnitEnum(int value) {
    molarMassType = value;
  }

  public static MolarMassUnitEnum getEnum(int i) {
    for (MolarMassUnitEnum pti : values()) {
      if (pti.molarMassType == i) {
        return pti;
      }
    }
    return null;
  }
  
  /**
   * Get the generator type
   * @return
   */
  public int getMolarMassType() {
    return molarMassType;
  }
}
