package gov.lanl.micot.infrastructure.ep.model;

/**
 * Implementation of a two terminal DC line
 * 
 * @author Russell Bent
 */
public class DCTwoTerminalLine extends DCLineImpl {

  protected static final long       serialVersionUID               = 1L;
  
  public static final String INVERTER_TAP_KEY             = "INVERTER_TAP";
  public static final String INVERTER_MIN_TAP_KEY         = "INVERTER_MIN_TAP";
  public static final String INVERTER_MAX_TAP_KEY         = "INVERTER_MAX_TAP";  
  public static final String INVERTER_TAP_RATIO_KEY       = "INVERTER_TAP_RATIO";
  public static final String CONTROL_MODE_KEY             = "CONTROL_MODE";
  public static final String RECTIFIER_TAP_KEY            = "RECTIFIER_TAP";
  public static final String RECTIFIER_MIN_TAP_KEY        = "RECTIFIER_TAP_MIN";
  public static final String RECTIFIER_MAX_TAP_KEY        = "RECTIFIER_TAP_MAX";
  public static final String RECTIFIER_TAP_RATIO_KEY      = "RECTIFIER_TAP_RATIO";
  public static final String DC_VOLTAGE_KEY               = "DC_VOLTAGE";
  public static final String NAME_KEY                     = "NAME";
  public static final String DC_RECTIFIER_VOLTAGE_KEY     = "DC_RECTIFIER_VOLTAGE";
  public static final String DC_INVERTER_VOLTAGE_KEY      = "DC_INVERTER_VOLTAGE";

  /**
   * Constructor
   */
  protected DCTwoTerminalLine(long assetId) {
    super(assetId);
  }

  @Override
  public DCTwoTerminalLine clone() {
    DCTwoTerminalLine newLine = new DCTwoTerminalLine(getAttribute(ASSET_ID_KEY,Long.class));

    try {
      deepCopy(newLine);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return newLine;
  }

  
}
