package gov.lanl.micot.infrastructure.ep.model;

/**
 * Enum for generator types
 * @author Russell Bent
 */
public enum FuelTypeEnum {

	UNKNOWN(0),
	COAL(1),
	NATURAL_GAS(2),
	OIL(3),
	NUCLEAR(4),
	HYDRO(5),
	SOLAR(6),
	WIND(7),
	GEOTHERMOL(8),
	BIOMASS(9),
	DIESEL(10);
		
	private int fuelType = -1;
  
  /**
   * Private constructor
   * @param value
   */
  private FuelTypeEnum(int value) {
    fuelType = value;
  }

  public static FuelTypeEnum getEnum(int i) {
    for (FuelTypeEnum pti : values()) {
      if (pti.fuelType == i) {
        return pti;
      }
    }
    return null;
  }
  
  /**
   * Get the generator type
   * @return
   */
  public int getFuelType() {
    return fuelType;
  }
}
