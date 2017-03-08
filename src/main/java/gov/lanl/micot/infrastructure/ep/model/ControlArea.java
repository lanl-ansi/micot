package gov.lanl.micot.infrastructure.ep.model;

import gov.lanl.micot.infrastructure.model.Component;

/**
 * Abstract class for areas
 * @author Russell Bent
 */
public interface ControlArea extends Component {
	
  public static final String AREA_BUS_NAME_KEY         = "AREA_BUS_NAME";
  public static final String AREA_SCHEDULED_EXPORT_KEY = "AREA_SCHEDULED_EXPORT";
  public static final String AREA_TOLERANCE_KEY        = "AREA_TOLERANCE";
  public static final String AREA_CODE_KEY             = "AREA_AREA_CODE";
  public static final String AREA_NAME_KEY             = "AREA_NAME";

  /**
   * Clone a bus
   * @return
   */
  public ControlArea clone();

  
}
