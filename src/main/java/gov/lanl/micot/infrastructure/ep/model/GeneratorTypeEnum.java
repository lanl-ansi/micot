package gov.lanl.micot.infrastructure.ep.model;

/**
 * Enum for generator types
 * @author Russell Bent
 */
public enum GeneratorTypeEnum {

	UNREGULATED_TYPE(0),
	HOLD_GENERATION_TYPE(1),
	HOLD_VOLTAGE_TYPE(2),
	REFERENCE_BUS_TYPE(3),
	ISOLATED_BUS_TYPE(4);
	
	private int generatorType = -1;
  
  /**
   * Private constructor
   * @param value
   */
  private GeneratorTypeEnum(int value) {
    generatorType = value;
  }

  public static GeneratorTypeEnum getEnum(int i) {
    for (GeneratorTypeEnum pti : values()) {
      if (pti.generatorType == i) {
        return pti;
      }
    }
    return null;
  }
  
  /**
   * Get the generator type
   * @return
   */
  public int getGeneratorType() {
    return generatorType;
  }
}
