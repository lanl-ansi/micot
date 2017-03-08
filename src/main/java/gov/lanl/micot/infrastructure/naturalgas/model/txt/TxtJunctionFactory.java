package gov.lanl.micot.infrastructure.naturalgas.model.txt;

import java.util.StringTokenizer;

import gov.lanl.micot.infrastructure.naturalgas.model.Junction;
import gov.lanl.micot.infrastructure.naturalgas.model.JunctionFactory;
import gov.lanl.micot.util.geometry.PointImpl;

/**
 * Factory for create junction related information
 * 
 * @author Russell Bent
 */
public class TxtJunctionFactory extends JunctionFactory {

//  private static TxtJunctionFactory INSTANCE = null;
  private static final String LEGACY_TAG = "TXT";

  //public static TxtJunctionFactory getInstance() {
    //if (INSTANCE == null) {
      //INSTANCE = new TxtJunctionFactory();
   // }
    //return INSTANCE;
  //}

  /**
   * Singleton constructor
   */
  protected TxtJunctionFactory() {
  }

  /**
   * Creates a bus and data from a bus
   * 
   * @param line
   * @return
   */
  public Junction createJunction(String line, String delim) {
    StringTokenizer tokenizer = new StringTokenizer(line, delim);
    int legacyid = Integer.parseInt(tokenizer.nextToken());
    String name = tokenizer.nextToken();
    double y = Double.parseDouble(tokenizer.nextToken());
    double x = Double.parseDouble(tokenizer.nextToken());
    double pressureMin = Double.parseDouble(tokenizer.nextToken());
    double pressureMax = Double.parseDouble(tokenizer.nextToken());
    Junction junction = registerJunction(legacyid);
    initializeJunction(junction);

    junction.setAttribute(Junction.NAME_KEY, name);
    junction.setPressure(0.0);
    junction.setActualStatus(true);
    junction.setCoordinate(new PointImpl(x, y));
    junction.setDesiredStatus(true);
    junction.setInitialPressure(0.0);
    junction.setMaximumPressure(pressureMax);
    junction.setMinimumPressure(pressureMin);

    return junction;
  }

  /**
   * Creates a bus from another bus
   * 
   * @param bus
   * @return
   */
  public void updateJunction(Junction junction) {
    if (junction.getAttribute(TxtModelConstants.TXT_LEGACY_ID_KEY) == null) {
      int legacyId = findUnusedId();
      junction.setAttribute(TxtModelConstants.TXT_LEGACY_ID_KEY, legacyId);
      registerLegacy(LEGACY_TAG, legacyId, junction);
    }
  }

  /**
   * Register the junction
   * 
   * @param legacyId
   * @param bus
   * @return
   */
  private Junction registerJunction(int legacyId) {
    Junction junction = getLegacy(LEGACY_TAG, legacyId);
    if (junction == null) {
      junction = createNewJunction();
      junction.addOutputKey(TxtModelConstants.TXT_LEGACY_ID_KEY);
      junction.setAttribute(TxtModelConstants.TXT_LEGACY_ID_KEY, legacyId);
      registerLegacy(LEGACY_TAG, legacyId, junction);
    }
    return junction;
  }

  /**
   * Find an unused id number
   * 
   * @return
   */
  private int findUnusedId() {
    for (int i = 0; i < Integer.MAX_VALUE; ++i) {
      if (getLegacy(LEGACY_TAG, i) == null) {
        return i;
      }
    }
    throw new RuntimeException("Error: Cannot find an unused id");
  }

  /**
   * Grant access of function to local package
   */
  //protected ModifiedJunction createModifiedJunction(Junction junction) {
    //return super.createModifiedJunction(junction);
  //}
  
}
