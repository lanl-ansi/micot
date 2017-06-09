package gov.lanl.micot.infrastructure.ep.model;

/**
 * Enum for dc line control modes
 * @author Russell Bent
 */
public enum DCVoltageSourceLineACControlModeEnum {
	
	CONTROL_IS_VOLTAGE(0),
	CONTROL_IS_POWER_FACTOR(1);
	
	private int controlMode = -1;
  
  /**
   * Private constructor
   * @param value
   */
  private DCVoltageSourceLineACControlModeEnum(int value) {
    controlMode = value;
  }

  public static DCVoltageSourceLineACControlModeEnum getEnum(int i) {
    for (DCVoltageSourceLineACControlModeEnum pti : values()) {
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
