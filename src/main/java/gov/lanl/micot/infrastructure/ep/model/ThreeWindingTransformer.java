package gov.lanl.micot.infrastructure.ep.model;

import gov.lanl.micot.infrastructure.model.Component;

/**
 * Abstract class for three winding transformers. This is mainly a meta object that allows us to group two winding transformers together
 * as a three winding transformer, plus any information that is associated with the three transformers instead of the pair wise windings
 * This is patterned like zones and control areas
 * @author Russell Bent
 */
public interface ThreeWindingTransformer extends Component {
	
  /**
   * Clone a transformer
   * @return
   */
  public ThreeWindingTransformer clone();

  
}
