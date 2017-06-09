package gov.lanl.micot.infrastructure.ep.model;

/**
 * Implementation of a multi terminal DC line
 * 
 * @author Russell Bent
 */
public class DCMultiTerminalLine extends DCLineImpl {

  protected static final long       serialVersionUID               = 1L;
  
  public static final String BASE_WINDING_KEY                 = "BASE_WINDING";
  public static final String FIRING_ANGLE_KEY                 = "FIRING_ANGLE";
  public static final String FIRING_ANGLE_MAX_KEY             = "FIRING_ANGLE_MAX";
  public static final String FIRING_ANGLE_MIN_KEY             = "FIRING_ANGLE_MIN";
  public static final String CURRENT_ANGLE_KEY                = "CURRENT_ANGLE";
  public static final String SETPOINT_KEY                     = "SET_POINT";
  public static final String TAP_KEY                          = "TAP";
  public static final String TAP_MAX_KEY                      = "TAP_MAX";
  public static final String TAP_MIN_KEY                      = "TAP_MIN";
  public static final String TAP_STEP_SIZE_KEY                = "TAP_STEP_SIZE";
  public static final String TYPE_KEY                         = "TYPE";
  public static final String TRANSFORMER_TAP_KEY              = "TRANSFORMER_TAP";
  public static final String CURRENT_RATING_KEY               = "CURRENT_RATING";
  public static final String DC_VOLTAGE_KEY                   = "DC_VOLTAGE";

  /**
   * Constructor
   */
  protected DCMultiTerminalLine(long assetId) {
    super(assetId);
  }

  @Override
  public DCMultiTerminalLine clone() {
    DCMultiTerminalLine newLine = new DCMultiTerminalLine(getAttribute(ASSET_ID_KEY,Long.class));

    try {
      deepCopy(newLine);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return newLine;
  }

  
}
