package gov.lanl.micot.infrastructure.naturalgas.model;

/**
 * Enum for temperature units
 * @author Russell Bent
 */
public enum TemperatureUnitEnum {

  CELSIUS_TYPE(0),
  KELVIN_TYPE(1);
	
	private int temperatureType = -1;
  
  /**
   * Private constructor
   * @param value
   */
  private TemperatureUnitEnum(int value) {
    temperatureType = value;
  }

  public static TemperatureUnitEnum getEnum(int i) {
    for (TemperatureUnitEnum pti : values()) {
      if (pti.temperatureType == i) {
        return pti;
      }
    }
    return null;
  }
  
  /**
   * Get the generator type
   * @return
   */
  public int getTemperatureType() {
    return temperatureType;
  }
}
