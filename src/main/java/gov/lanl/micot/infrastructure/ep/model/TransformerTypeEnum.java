package gov.lanl.micot.infrastructure.ep.model;

/**
 * Enum for transformer types
 * @author Russell Bent
 */
public enum TransformerTypeEnum {

	TRANSMISSION_LINE_TYPE(0),
	TRANSMISSION_FIXED_ANGLE_FIXED_TAP_TYPE(1),
	TRANSMISSION_FIXED_ANGLE_VARIABLE_TAP_VOLTAGE_CONTROL_TYPE(2),
	TRANSMISSION_FIXED_ANGLE_VARIABLE_TAP_REACTIVE_FLOW_CONTROL_TYPE(3),
	TRANSMISSION_FIXED_ANGLE_VARIABLE_TAP_REAL_FLOW_CONTROL_TYPE(4);

	private int transformerType = -1;
  
  /**
   * Private constructor
   * @param value
   */
  private TransformerTypeEnum(int value) {
    transformerType = value;
  }

  public static TransformerTypeEnum getEnum(int i) {
    for (TransformerTypeEnum pti : values()) {
      if (pti.transformerType == i) {
        return pti;
      }
    }
    return null;
  }
  
  /**
   * Get the transformer type
   * @return
   */
  public int getTransformerType() {
    return transformerType;
  }
}
