package gov.lanl.micot.infrastructure.ep.model;

/**
 * Enum for dc line control modes
 * @author Russell Bent
 */
public enum DCVoltageSourceLineDCControlModeEnum {
	
	CONTROL_IS_OUT_OF_SERVICE(0),
	CONTROL_IS_VOLTAGE(1),
	CONTROL_IS_POWER(2);
	
	private int controlMode = -1;
  
  /**
   * Private constructor
   * @param value
   */
  private DCVoltageSourceLineDCControlModeEnum(int value) {
    controlMode = value;
  }

  public static DCVoltageSourceLineDCControlModeEnum getEnum(int i) {
    for (DCVoltageSourceLineDCControlModeEnum pti : values()) {
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
