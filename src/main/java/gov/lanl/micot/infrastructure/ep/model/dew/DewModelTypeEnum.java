package gov.lanl.micot.infrastructure.ep.model.dew;

/**
 * Enum for generator types
 * @author Russell Bent
 */
public enum DewModelTypeEnum {

	TEXTFILE_TYPE(0),
	LIBRARY_TYPE(1),
	TEXTFILE_FIRST_TYPE(2),
	COORDINATE_TYPE(3),
	LINECHARGING_TYPE(4);
	
	private int modelType = -1;
  
  /**
   * Private constructor
   * @param value
   */
  private DewModelTypeEnum(int value) {
    modelType = value;
  }

  public static DewModelTypeEnum getEnum(int i) {
    for (DewModelTypeEnum pti : values()) {
      if (pti.modelType == i) {
        return pti;
      }
    }
    return null;
  }
  
  /**
   * Get the generator type
   * @return
   */
  public int getModelType() {
    return modelType;
  }
}
