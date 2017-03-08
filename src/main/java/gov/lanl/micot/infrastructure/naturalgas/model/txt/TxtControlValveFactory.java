package gov.lanl.micot.infrastructure.naturalgas.model.txt;

import gov.lanl.micot.infrastructure.naturalgas.model.ControlValve;
import gov.lanl.micot.infrastructure.naturalgas.model.ControlValveFactory;
import gov.lanl.micot.infrastructure.naturalgas.model.Junction;
import gov.lanl.micot.util.geometry.LineImpl;
import gov.lanl.micot.util.geometry.Point;

import java.util.StringTokenizer;
import java.util.Vector;

/**
 * Factory for creating a valve 
 * 
 * @author Russell Bent
 */
public class TxtControlValveFactory extends ControlValveFactory {

//  private static TxtControlValveFactory INSTANCE              = null;
  private static final String LEGACY_TAG = "TXT";
  
  //public static TxtControlValveFactory getInstance() {
    //if (INSTANCE == null) {
      //INSTANCE = new TxtControlValveFactory();
    //}
    //return INSTANCE;
  //}

  /**
   * Constructor
   */
  protected TxtControlValveFactory() {
  }

  /**
   * Create an ieiss compressor based upon another compressor
   * 
   * @param compressor
   * @param shuntCapacitorData
   * @param busId
   * @return
   */
  public void updateValve(ControlValve valve, Junction fromJunction, Junction toJunction, int legacyid) {
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
 private ControlValve registerValve(int legacyId) {
   ControlValve valve = getLegacy(LEGACY_TAG, legacyId);
   if (valve == null) {
     valve = createNewControlValve();
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
// private int findUnusedId() {
  // TxtCompressorFactory cfactory = TxtCompressorFactory.getInstance();
   //TxtValveFactory vfactory = TxtValveFactory.getInstance();
   //TxtResistorFactory rfactory = TxtResistorFactory.getInstance();   
   //TxtControlValveFactory sfactory = TxtControlValveFactory.getInstance();   
   //TxtShortPipeFactory spfactory = TxtShortPipeFactory.getInstance();
   //TxtPipeFactory pfactory = TxtPipeFactory.getInstance();
   
 //  for (int i = 0; i < Integer.MAX_VALUE; ++i) {
   //  if (pfactory.getLegacy(LEGACY_TAG, i) == null && cfactory.getLegacy(LEGACY_TAG, i) == null && vfactory.getLegacy(LEGACY_TAG, i) == null && rfactory.getLegacy(LEGACY_TAG, i) == null && sfactory.getLegacy(LEGACY_TAG, i) == null && spfactory.getLegacy(LEGACY_TAG, i) == null) {
     //  return i;
     //}
   //}
   //throw new RuntimeException("Error: Cannot find an unused id");
 //}
 
 @Override
 protected ControlValve getLegacy(String legacyTag, Object key) {
   return super.getLegacy(legacyTag, key);
 }
 
 /**
  * Creates a line and data from a line
  * 
  * @param line
  * @return
  */
 public ControlValve createControlValveVersion2(String line, Junction fromJcn, Junction toJcn, String delim) {
   StringTokenizer tokenizer = new StringTokenizer(line, delim);
   int legacyid = Integer.parseInt(tokenizer.nextToken());    
   tokenizer.nextToken(); // type
   tokenizer.nextToken(); // from
   tokenizer.nextToken(); // to
   double diameter = Double.parseDouble(tokenizer.nextToken());
   double length = Double.parseDouble(tokenizer.nextToken());
   double resistance = Double.parseDouble(tokenizer.nextToken());
   double minRatio = Double.parseDouble(tokenizer.nextToken());
   double maxRatio = Double.parseDouble(tokenizer.nextToken());

   ControlValve valve = registerValve(legacyid);    
   valve.setDiameter(diameter);
   valve.setLength(length);
   valve.setResistance(resistance);
   valve.setAttribute(ControlValve.MINIMUM_COMPRESSION_RATIO_KEY, minRatio);
   valve.setAttribute(ControlValve.MAXIMUM_COMPRESSION_RATIO_KEY, maxRatio);
   valve.setAttribute(ControlValve.COMPRESSION_RATIO_KEY, maxRatio);
   valve.setAttribute(ControlValve.INITIAL_COMPRESSION_RATIO_KEY, maxRatio);
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
