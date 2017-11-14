package gov.lanl.micot.infrastructure.naturalgas.model.txt;

import gov.lanl.micot.infrastructure.naturalgas.model.Compressor;
import gov.lanl.micot.infrastructure.naturalgas.model.CompressorFactory;
import gov.lanl.micot.infrastructure.naturalgas.model.Junction;
import gov.lanl.micot.util.geometry.LineImpl;
import gov.lanl.micot.util.geometry.Point;

import java.util.StringTokenizer;
import java.util.Vector;

/**
 * Factory for creating a compressor 
 * 
 * @author Russell Bent
 */
public class TxtCompressorFactory extends CompressorFactory {

  private static final String LEGACY_TAG = "TXT";
  
  /**
   * Constructor
   */
  protected TxtCompressorFactory() {
  }

  /**
   * Creates a capacitor and data from a capacitor
   * 
   * @param compressor
   * @return
   */
  public Compressor createCompressorVersion1(String line, Junction fromJunction, Junction toJunction, String delim) {
    StringTokenizer tokenizer = new StringTokenizer(line, delim);
    int legacyid = Integer.parseInt(tokenizer.nextToken());    
    tokenizer.nextToken(); // from
    tokenizer.nextToken(); // to
    double diameter = Double.parseDouble(tokenizer.nextToken());
    double length = Double.parseDouble(tokenizer.nextToken());
    double resistance = Double.parseDouble(tokenizer.nextToken());
    double minRatio = Double.parseDouble(tokenizer.nextToken());
    double maxRatio = Double.parseDouble(tokenizer.nextToken());
        
    Compressor compressor = registerCompressor(legacyid);    
    compressor.setDiameter(diameter);
    compressor.setLength(length);
    compressor.setResistance(resistance);
    compressor.setCompressionRatio(maxRatio); 		
    compressor.setMaximumCompressionRatio(maxRatio);
    compressor.setMinimumCompressionRatio(minRatio);
    compressor.setInitialCompressionRatio(maxRatio); 
    compressor.setFlow(0.0);						 
    compressor.setCapacity(Double.MAX_VALUE);
    compressor.setStatus(true);
    
    Vector<Point> points = new Vector<Point>();
    points.add(fromJunction.getCoordinate());
    points.add(toJunction.getCoordinate());
    compressor.setCoordinates(new LineImpl(points));
    
    return compressor;
  }

  
  /**
   * Creates a capacitor and data from a capacitor
   * 
   * @param compressor
   * @return
   */
  public Compressor createCompressorVersion2(String line, Junction fromJunction, Junction toJunction, String delim) {
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

    Compressor compressor = registerCompressor(legacyid);    
    compressor.setDiameter(diameter);
    compressor.setLength(length);
    compressor.setResistance(resistance);
    compressor.setCompressionRatio(maxRatio);     
    compressor.setMaximumCompressionRatio(maxRatio);
    compressor.setMinimumCompressionRatio(minRatio);
    compressor.setInitialCompressionRatio(maxRatio); 
    compressor.setFlow(0.0);             
    compressor.setCapacity(Double.MAX_VALUE);
    compressor.setStatus(true);
    
    Vector<Point> points = new Vector<Point>();
    points.add(fromJunction.getCoordinate());
    points.add(toJunction.getCoordinate());
    compressor.setCoordinates(new LineImpl(points));
    
    return compressor;
  }
  
  /**
   * Create an ieiss compressor based upon another compressor
   * 
   * @param compressor
   * @param shuntCapacitorData
   * @param busId
   * @return
   */
  public void updateCompressor(Compressor compressor, Junction fromJunction, Junction toJunction, int legacyid) {
    if (compressor.getAttribute(TxtModelConstants.TXT_LEGACY_ID_KEY) == null) {
//      int legacyid = findUnusedId();
      compressor.setAttribute(TxtModelConstants.TXT_LEGACY_ID_KEY, legacyid);
      registerLegacy(LEGACY_TAG, legacyid, compressor);
    }
  }

 /**
  * Register the compressor
  * @param legacyId
  * @param bus
  * @return
  */
 private Compressor registerCompressor(int legacyId) {
   Compressor compressor = getLegacy(LEGACY_TAG, legacyId);
   if (compressor == null) {
     compressor = createNewCompressor();
     compressor.addOutputKey(TxtModelConstants.TXT_LEGACY_ID_KEY);
     compressor.setAttribute(TxtModelConstants.TXT_LEGACY_ID_KEY,legacyId);
     registerLegacy(LEGACY_TAG, legacyId, compressor);
   }
   return compressor;
 }
 
 /**
  * Find an unused id number
  * 
  * @return
  */
// private int findUnusedId() {
 //  TxtCompressorFactory cfactory = TxtCompressorFactory.getInstance();
   //TxtValveFactory vfactory = TxtValveFactory.getInstance();
   //TxtResistorFactory rfactory = TxtResistorFactory.getInstance();   
  // TxtControlValveFactory sfactory = TxtControlValveFactory.getInstance();   
   //TxtShortPipeFactory spfactory = TxtShortPipeFactory.getInstance();
   //TxtPipeFactory pfactory = TxtPipeFactory.getInstance();
   
   //for (int i = 0; i < Integer.MAX_VALUE; ++i) {
     //if (pfactory.getLegacy(LEGACY_TAG, i) == null && cfactory.getLegacy(LEGACY_TAG, i) == null && vfactory.getLegacy(LEGACY_TAG, i) == null && rfactory.getLegacy(LEGACY_TAG, i) == null && sfactory.getLegacy(LEGACY_TAG, i) == null && spfactory.getLegacy(LEGACY_TAG, i) == null) {
       //return i;
     //}
   //}
   //throw new RuntimeException("Error: Cannot find an unused id");
 //}
 
 @Override
 protected Compressor getLegacy(String legacyTag, Object key) {
   return super.getLegacy(legacyTag, key);
 }
 
}
