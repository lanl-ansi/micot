package gov.lanl.micot.infrastructure.naturalgas.model.txt;

import gov.lanl.micot.infrastructure.naturalgas.model.CityGate;
import gov.lanl.micot.infrastructure.naturalgas.model.CityGateFactory;
import gov.lanl.micot.infrastructure.naturalgas.model.Junction;
import gov.lanl.micot.util.geometry.PointImpl;

import java.util.StringTokenizer;

/**
 * Factory for creating city gates
 * 
 * @author Russell Bent
 */
public class TxtCityGateFactory extends CityGateFactory {

  private static final String LEGACY_TAG = "TXT";
  
  /**
   * Singleton constructor
   */
  protected TxtCityGateFactory() {
  }

  /**
   * Creates a bus and data from a bus
   * 
   * @param line
   * @return
   */
  public CityGate createCityGate(String line, Junction junction, String delim) {
    StringTokenizer tokenizer = new StringTokenizer(line, delim);
    int legacyid = Integer.parseInt(tokenizer.nextToken());
    String name = tokenizer.nextToken();
    double y = Double.parseDouble(tokenizer.nextToken());
    double x = Double.parseDouble(tokenizer.nextToken());
    Double.parseDouble(tokenizer.nextToken()); // pressure min
    Double.parseDouble(tokenizer.nextToken()); // pressure max
    double maxConsumption = Math.abs(Math.min(0, Double.parseDouble(tokenizer.nextToken())));
    double minConsumption = Math.abs(Double.parseDouble(tokenizer.nextToken()));
    double consumption = maxConsumption;
    
    CityGate gate = registerGate(legacyid);
    initializeGate(gate, junction, consumption);
    
    gate.setAttribute(CityGate.CITYGATE_NAME_KEY, name);
    gate.setActualConsumption(consumption);
    gate.setStatus(true);
    gate.setCoordinate(new PointImpl(x,y));
    gate.setMaximumConsumption(maxConsumption);
    gate.setMinimumConsumption(minConsumption);

    return gate;
  }

  /**
   * Creates an ieiss load from another load object
   * 
   * @param load
   * @param loadData
   * @param busId
   * @return
   */
  protected void updateCityGate(CityGate gate, Junction junction) {
    if (gate.getAttribute(TxtModelConstants.TXT_LEGACY_ID_KEY) == null) {
      int legacyid = junction.getAttribute(TxtModelConstants.TXT_LEGACY_ID_KEY, Integer.class);
      gate.setAttribute(TxtModelConstants.TXT_LEGACY_ID_KEY, legacyid);
      registerLegacy(LEGACY_TAG, legacyid, gate);
    }
  }

  /**
   * Register the city gate
   * 
   * @param legacyId
   * @param bus
   * @return
   */
  private CityGate registerGate(int legacyId) {
    CityGate gate = getLegacy(LEGACY_TAG, legacyId);
    if (gate == null) {
      gate = createNewCityGate();
      gate.setAttribute(TxtModelConstants.TXT_LEGACY_ID_KEY, legacyId);
      gate.addOutputKey(TxtModelConstants.TXT_LEGACY_ID_KEY);
      registerLegacy(LEGACY_TAG, legacyId, gate);      
    }
    return gate;
  }


}
