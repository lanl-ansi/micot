package gov.lanl.micot.infrastructure.ep.model;

/**
 * Enum for line installation types
 * @author Russell Bent
 */
public enum LineInstallationTypeEnum {

	OVERHEAD_TYPE(0),
	UNDERGROUND_CONDUIT_TYPE(1),
	UNDERGROUND_BURIED_TYPE(2),	
  UNKNOWN_TYPE(3);

  private int type = -1;
  
  /**
   * Private constructor
   * @param value
   */
  private LineInstallationTypeEnum(int value) {
    type = value;
  }

  public static LineInstallationTypeEnum getEnum(int i) {
    for (LineInstallationTypeEnum pti : values()) {
      if (pti.type == i) {
        return pti;
      }
    }
    return null;
  }
  
  /**
   * Get the generator type
   * @return
   */
  public int getType() {
    return type;
  }
}
