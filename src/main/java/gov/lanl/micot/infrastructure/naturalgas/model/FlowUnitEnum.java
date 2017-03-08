package gov.lanl.micot.infrastructure.naturalgas.model;

/**
 * Enum for flow units
 * @author Russell Bent
 */
public enum FlowUnitEnum {

  M_CUBED_PER_HOUR_TYPE(0);
	
	private int flowType = -1;
  
  /**
   * Private constructor
   * @param value
   */
  private FlowUnitEnum(int value) {
    flowType = value;
  }

  public static FlowUnitEnum getEnum(int i) {
    for (FlowUnitEnum pti : values()) {
      if (pti.flowType == i) {
        return pti;
      }
    }
    return null;
  }
  
  /**
   * Get the generator type
   * @return
   */
  public int getFlowType() {
    return flowType;
  }
}
