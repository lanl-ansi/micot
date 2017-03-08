package gov.lanl.micot.infrastructure.ep.model;

/**
 * Enum for transformer control modes
 * @author Russell Bent
 */
public enum TransformerControlModeEnum {
	
	CONTROL_BUS_IS_TERMINAL(0),
	CONTROL_BUS_IS_TAP_SIDE(1),
	CONTROL_BUS_IS_IMPEDANCE_SIDE(2);
	
	private int controlMode = -1;
  
  /**
   * Private constructor
   * @param value
   */
  private TransformerControlModeEnum(int value) {
    controlMode = value;
  }

  public static TransformerControlModeEnum getEnum(int i) {
    for (TransformerControlModeEnum pti : values()) {
      if (pti.controlMode == i) {
        return pti;
      }
    }
    return null;
  }
  
  /**
   * Get the control mode
   * @return
   */
  public int getControlMode() {
    return controlMode;
  }
}
