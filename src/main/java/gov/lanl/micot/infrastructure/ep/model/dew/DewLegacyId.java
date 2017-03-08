package gov.lanl.micot.infrastructure.ep.model.dew;

import gov.lanl.micot.util.collection.Pair;

/**
 * A definition of dew legacy ids
 * @author 210117
 *
 */
public class DewLegacyId extends Pair<Integer,Integer> {

  /**
   * Constructor
   * @param one
   * @param two
   */
  public DewLegacyId(Integer one, Integer two) {
    super(one, two);    
  }

}
