package gov.lanl.micot.infrastructure.ep.model;

/**
 * Enum for dc line control modes
 * @author Russell Bent
 */
public enum DCMultiTerminalLineTerminalTypeEnum {
	
	TYPE_IS_RECT(0),
	TYPE_IS_INV(1);
	
	private int controlMode = -1;
  
  /**
   * Private constructor
   * @param value
   */
  private DCMultiTerminalLineTerminalTypeEnum(int value) {
    controlMode = value;
  }

  public static DCMultiTerminalLineTerminalTypeEnum getEnum(int i) {
    for (DCMultiTerminalLineTerminalTypeEnum pti : values()) {
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
