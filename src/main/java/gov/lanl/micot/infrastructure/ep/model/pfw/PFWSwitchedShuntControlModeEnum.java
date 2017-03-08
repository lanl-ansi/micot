package gov.lanl.micot.infrastructure.ep.model.pfw;

/**
 * Enum for the control mode of a switched shunt
 * @author Russell Bent
 */
public enum PFWSwitchedShuntControlModeEnum {

	LOCKED_MODE(0),
	DISCRETE_MODE(1),
	CONTINOUS_MODE(2),
	UNKNOWN_MODE(3),
	BAND_MODE(4);
	
	private int controlMode = -1;
  
  /**
   * Private constructor
   * @param value
   */
  private PFWSwitchedShuntControlModeEnum(int value) {
    controlMode = value;
  }

  public static PFWSwitchedShuntControlModeEnum getEnum(int i) {
    for (PFWSwitchedShuntControlModeEnum pti : values()) {
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
