package gov.lanl.micot.infrastructure.ep.model;

/**
 * Interface for transformers
 * @author Russell Bent
 */
public interface Transformer extends ElectricPowerFlowConnection {

  public static final String CONTROL_MAX_KEY              = "CONTROL_MAX";
  public static final String CONTROL_MIN_KEY              = "CONTROL_MIN";
  public static final String CONTROL_SIDE_KEY             = "CONTROL_SIDE";
  public static final String MAX_TAP_ANGLE_KEY            = "MAX_TAP_ANGLE";
  public static final String MIN_TAP_ANGLE_KEY            = "MIN_TAP_ANGLE";
  public static final String MAX_TAP_RATIO_KEY            = "MAX_TAP_RATIO";
  public static final String MIN_TAP_RATIO_KEY            = "MIN_TAP_RATIO";  
  public static final String STEP_SIZE_KEY                = "STEP_SIZE";
  public static final String TAP_ANGLE_KEY                = "TAP_ANGLE";
  public static final String TAP_RATIO_KEY                = "TAP_RATIO";
  public static final String TAP_RATIO_FORWARD_L_KEY                = "TAP_RATIO_FORWARD_L";
  public static final String TAP_RATIO_FORWARD_B_KEY                = "TAP_RATIO_FORWARD_B";
  public static final String TAP_RATIO_BACKWARD_L_KEY                = "TAP_RATIO_BACKWARD_L";
  public static final String TAP_RATIO_BACKWARD_B_KEY                = "TAP_RATIO_BACKWARD_B";
  public static final String TYPE_KEY                     = "TYPE";
  
  /**
   * Adds the line data listener
   * @param listener
   */
  public void addTransformerChangeListener(TransformerChangeListener listener);
  
  /**
   * Remove the line data listener
   * @param listener
   */
  public void removeTransformerChangeListener(TransformerChangeListener listener);
  
  /**
   * Clone a transformer
   * @return
   */
  public Transformer clone();


		
}
