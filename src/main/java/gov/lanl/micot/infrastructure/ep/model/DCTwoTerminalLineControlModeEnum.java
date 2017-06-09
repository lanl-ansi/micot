package gov.lanl.micot.infrastructure.ep.model;

/**
 * Enum for dc line control modes
 * @author Russell Bent
 */
public enum DCTwoTerminalLineControlModeEnum {
	
	CONTROL_IS_MW(0),
	CONTROL_IS_AMP(1);
	
	private int controlMode = -1;
  
  /**
   * Private constructor
   * @param value
   */
  private DCTwoTerminalLineControlModeEnum(int value) {
    controlMode = value;
  }

  public static DCTwoTerminalLineControlModeEnum getEnum(int i) {
    for (DCTwoTerminalLineControlModeEnum pti : values()) {
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
