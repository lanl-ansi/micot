package gov.lanl.micot.infrastructure.naturalgas.model.txt;

import gov.lanl.micot.infrastructure.naturalgas.model.Junction;
import gov.lanl.micot.infrastructure.naturalgas.model.Valve;
import gov.lanl.micot.infrastructure.naturalgas.model.ValveFactory;
import gov.lanl.micot.util.geometry.LineImpl;
import gov.lanl.micot.util.geometry.Point;

import java.util.StringTokenizer;
import java.util.Vector;

/**
 * Factory for creating a valve 
 * 
 * @author Russell Bent
 */
public class TxtValveFactory extends ValveFactory {

  private static final String LEGACY_TAG = "TXT";
  
  /**
   * Constructor
   */
  protected TxtValveFactory() {
  }

  /**
   * Create an ieiss compressor based upon another compressor
   * 
   * @param compressor
   * @param shuntCapacitorData
   * @param busId
   * @return
   */
  public void updateValve(Valve valve, Junction fromJunction, Junction toJunction, int legacyid) {
    if (valve.getAttribute(TxtModelConstants.TXT_LEGACY_ID_KEY) == null) {
      valve.setAttribute(TxtModelConstants.TXT_LEGACY_ID_KEY, legacyid);
      registerLegacy(LEGACY_TAG, legacyid, valve);
    }
  }

 /**
  * Register the compressor
  * @param legacyId
  * @param bus
  * @return
  */
 private Valve registerValve(int legacyId) {
   Valve valve = getLegacy(LEGACY_TAG, legacyId);
   if (valve == null) {
     valve = createNewValve();
     valve.addOutputKey(TxtModelConstants.TXT_LEGACY_ID_KEY);
     valve.setAttribute(TxtModelConstants.TXT_LEGACY_ID_KEY,legacyId);
     registerLegacy(LEGACY_TAG, legacyId, valve);
   }
   return valve;
 }
  
 @Override
 protected Valve getLegacy(String legacyTag, Object key) {
   return super.getLegacy(legacyTag, key);
 }
 
 /**
  * Creates a valve and data from a line
  * 
  * @param line
  * @return
  */
 public Valve createValveVersion2(String line, Junction fromJcn, Junction toJcn, String delim) {
   StringTokenizer tokenizer = new StringTokenizer(line, delim);
   int legacyid = Integer.parseInt(tokenizer.nextToken());
   tokenizer.nextToken(); // type
   tokenizer.nextToken(); // from
   tokenizer.nextToken(); // to
   double resistance = -1;

   Valve valve = registerValve(legacyid);
   valve.setResistance(resistance);
   valve.setFlow(0.0);
   valve.setCapacity(Double.MAX_VALUE);
   valve.setStatus(true);

   Vector<Point> points = new Vector<Point>();
   points.add(fromJcn.getCoordinate());
   points.add(toJcn.getCoordinate());
   valve.setCoordinates(new LineImpl(points));

   return valve;
 }
 
}
