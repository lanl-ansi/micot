package gov.lanl.micot.infrastructure.ep.model;

/**
 * Implementation of a two terminal DC line
 * 
 * @author Russell Bent
 */
public class DCVoltageSourceLine extends DCLineImpl {

  protected static final long       serialVersionUID               = 1L;

  public static final String FROM_AC_CONTROL_MODE_KEY            = "FROM_AC_CONTROL_MODE";
  public static final String TO_AC_CONTROL_MODE_KEY              = "TO_AC_CONTROL_MODE";
  public static final String FROM_AC_SET_POINT_KEY               = "FROM_AC_SET_POINT";
  public static final String TO_AC_SET_POINT_KEY                 = "TO_AC_SET_POINT";
  public static final String NAME_KEY                            = "NAME";
  public static final String FROM_MW_INPUT_KEY                   = "FROM_MW_INPUT";   
  public static final String TO_MW_INPUT_KEY                     = "TO_MW_INPUT";   
  public static final String FROM_DC_CONTROL_MODE_KEY            = "FROM_DC_CONTROL_MODE";
  public static final String TO_DC_CONTROL_MODE_KEY              = "TO_DC_CONTROL_MODE";
  public static final String FROM_DC_SET_POINT_KEY               = "FROM_DC_SET_POINT";
  public static final String TO_DC_SET_POINT_KEY                 = "TO_DC_SET_POINT";
  public static final String FROM_MAX_MVAR_KEY                   = "FROM_MAX_MVAR";
  public static final String TO_MAX_MVAR_KEY                     = "TO_MAX_MVAR";
  public static final String FROM_MIN_MVAR_KEY                   = "FROM_MIN_MVAR";
  public static final String TO_MIN_MVAR_KEY                     = "TO_MIN_MVAR";
  public static final String FROM_MAX_CURRENT_KEY                = "FROM_MAX_CURRENT";
  public static final String TO_MAX_CURRENT_KEY                  = "TO_MAX_CURRENT";
  public static final String FROM_DC_VOLTAGE_KEY                 = "FROM_DC_VOLTAGE";
  public static final String TO_DC_VOLTAGE_KEY                   = "TO_DC_VOLTAGE";
  public static final String FROM_MVA_RATING_KEY                 = "FROM_MVA_RATING";
  public static final String TO_MVA_RATING_KEY                   = "TO_MVA_RATING";
  
  /**
   * Constructor
   */
  protected DCVoltageSourceLine(long assetId) {
    super(assetId);
  }

  @Override
  public DCVoltageSourceLine clone() {
    DCVoltageSourceLine newLine = new DCVoltageSourceLine(getAttribute(ASSET_ID_KEY,Long.class));

    try {
      deepCopy(newLine);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return newLine;
  }

  
}
