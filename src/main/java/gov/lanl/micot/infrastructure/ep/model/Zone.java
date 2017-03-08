package gov.lanl.micot.infrastructure.ep.model;

import gov.lanl.micot.infrastructure.model.Component;

/**
 * Abstract class for zones
 * @author Russell Bent
 */
public interface Zone extends Component {
	
  public static final String ZONE_NAME_KEY             = "ZONE_NAME";

  /**
   * @return the name
   */
  public abstract String getName();

  /**
   * Clone a zone
   * @return
   */
  public Zone clone();
		
}
