package gov.lanl.micot.infrastructure.naturalgas.model.txt;

import java.util.StringTokenizer;

import gov.lanl.micot.infrastructure.naturalgas.model.Junction;
import gov.lanl.micot.infrastructure.naturalgas.model.Well;
import gov.lanl.micot.infrastructure.naturalgas.model.WellFactory;
import gov.lanl.micot.util.geometry.PointImpl;

/**
 * Factory for creating wells
 * 
 * @author Russell Bent
 */
public class TxtWellFactory extends WellFactory {

  private static final String LEGACY_TAG = "TXT";
  
  /**
   * Singleton constructor
   */
  protected TxtWellFactory() {
  }

  /**
   * Creates a generator and data from a generator
   * 
   * @param line
   * @return
   */
  public Well createWell(String line, Junction junction, String delim) {
    StringTokenizer tokenizer = new StringTokenizer(line, delim);
    int legacyid = Integer.parseInt(tokenizer.nextToken());
    String name = tokenizer.nextToken();
    double y = Double.parseDouble(tokenizer.nextToken());
    double x = Double.parseDouble(tokenizer.nextToken());
    Double.parseDouble(tokenizer.nextToken()); // pressure min
    Double.parseDouble(tokenizer.nextToken()); // pressure max
    double minProduction = Math.max(0, Double.parseDouble(tokenizer.nextToken()));
    double maxProduction = Double.parseDouble(tokenizer.nextToken());
    double cost = Double.parseDouble(tokenizer.nextToken());
    double production = maxProduction;
    
    Well well = registerWell(legacyid, junction);
    initializeWell(well, junction, production);
    
    well.setAttribute(Well.WELL_NAME_KEY, name);
    well.setActualProduction(production);
    well.setStatus(true);
    well.setCoordinate(new PointImpl(x,y));
    well.setDesiredProduction(production);
    well.setMaximumProduction(maxProduction);
    well.setMinimumProduction(minProduction);
    well.setAttribute(Well.ECONOMIC_COST_KEY, cost);
    
    return well;
  }

  /**
   * Creates an ieiss well from another well
   * 
   * @param generator
   * @param generatorData
   * @return
   */
  protected Well createWell(Well well, Junction junction) {
    if (well.getAttribute(TxtModelConstants.TXT_LEGACY_ID_KEY) == null) {
      int legacyid = junction.getAttribute(TxtModelConstants.TXT_LEGACY_ID_KEY, Integer.class);
      well.setAttribute(TxtModelConstants.TXT_LEGACY_ID_KEY, legacyid);
      registerLegacy(LEGACY_TAG, legacyid, well);
    }
    return well;
  }

  /**
   * Register the city gate
   * 
   * @param legacyId
   * @param bus
   * @return
   */
  private Well registerWell(int legacyId, Junction junction) {
    Well well = getLegacy(LEGACY_TAG, legacyId);
    if (well == null) {
      well = createNewWell();
      well.addOutputKey(TxtModelConstants.TXT_LEGACY_ID_KEY);
      well.setAttribute(TxtModelConstants.TXT_LEGACY_ID_KEY, legacyId);
      registerLegacy(LEGACY_TAG, legacyId, well);
    }
    return well;
  }

}
