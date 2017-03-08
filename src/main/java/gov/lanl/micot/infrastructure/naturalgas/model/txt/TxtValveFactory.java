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

//  private static TxtValveFactory INSTANCE              = null;
  private static final String LEGACY_TAG = "TXT";
  
  //public static TxtValveFactory getInstance() {
    //if (INSTANCE == null) {
      //INSTANCE = new TxtValveFactory();
    //}
    //return INSTANCE;
  //}

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
//      int legacyid = findUnusedId();
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
 
 /**
  * Find an unused id number
  * 
  * @return
  */
/* private int findUnusedId() {
   TxtCompressorFactory cfactory = TxtCompressorFactory.getInstance();
   TxtValveFactory vfactory = TxtValveFactory.getInstance();
   TxtResistorFactory rfactory = TxtResistorFactory.getInstance();   
   TxtControlValveFactory sfactory = TxtControlValveFactory.getInstance();   
   TxtShortPipeFactory spfactory = TxtShortPipeFactory.getInstance();
   TxtPipeFactory pfactory = TxtPipeFactory.getInstance();
   
   for (int i = 0; i < Integer.MAX_VALUE; ++i) {
     if (pfactory.getLegacy(LEGACY_TAG, i) == null && cfactory.getLegacy(LEGACY_TAG, i) == null && vfactory.getLegacy(LEGACY_TAG, i) == null && rfactory.getLegacy(LEGACY_TAG, i) == null && sfactory.getLegacy(LEGACY_TAG, i) == null && spfactory.getLegacy(LEGACY_TAG, i) == null) {
       return i;
     }
   }
   throw new RuntimeException("Error: Cannot find an unused id");
 }*/
 
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
   valve.setActualStatus(true);
   valve.setDesiredStatus(true);

   Vector<Point> points = new Vector<Point>();
   points.add(fromJcn.getCoordinate());
   points.add(toJcn.getCoordinate());
   valve.setCoordinates(new LineImpl(points));

   return valve;
 }
 
}
