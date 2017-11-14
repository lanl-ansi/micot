package gov.lanl.micot.infrastructure.naturalgas.model.txt;

import java.util.StringTokenizer;
import java.util.Vector;

import gov.lanl.micot.infrastructure.naturalgas.io.txt.TxtModelFile;
import gov.lanl.micot.infrastructure.naturalgas.model.Junction;
import gov.lanl.micot.infrastructure.naturalgas.model.Resistor;
import gov.lanl.micot.infrastructure.naturalgas.model.ResistorFactory;
import gov.lanl.micot.util.geometry.LineImpl;
import gov.lanl.micot.util.geometry.Point;

/**
 * Factory for creating a resistor 
 * 
 * @author Russell Bent
 */
public class TxtResistorFactory extends ResistorFactory {

  private static final String LEGACY_TAG = "TXT";
  
  /**
   * Constructor
   */
  protected TxtResistorFactory() {
  }

  /**
   * Create an ieiss compressor based upon another compressor
   * 
   * @param compressor
   * @param shuntCapacitorData
   * @param busId
   * @return
   */
  public void updateResistor(Resistor resistor, Junction fromJunction, Junction toJunction, int legacyid) {
    if (resistor.getAttribute(TxtModelConstants.TXT_LEGACY_ID_KEY) == null) {
      resistor.setAttribute(TxtModelConstants.TXT_LEGACY_ID_KEY, legacyid);
      registerLegacy(LEGACY_TAG, legacyid, resistor);
    }
  }

 /**
  * Register the resistor
  * @param legacyId
  * @param bus
  * @return
  */
 private Resistor registerResistor(int legacyId) {
   Resistor resistor = getLegacy(LEGACY_TAG, legacyId);
   if (resistor == null) {
     resistor = createNewResistor();
     resistor.addOutputKey(TxtModelConstants.TXT_LEGACY_ID_KEY);
     resistor.setAttribute(TxtModelConstants.TXT_LEGACY_ID_KEY,legacyId);
     registerLegacy(LEGACY_TAG, legacyId, resistor);
   }
   return resistor;
 }
 
 @Override
 protected Resistor getLegacy(String legacyTag, Object key) {
   return super.getLegacy(legacyTag, key);
 }
 
 /**
  * Creates a line and data from a line
  * 
  * @param line
  * @return
  */
 public Resistor createResistorVersion2(String line, Junction fromJcn, Junction toJcn, String delim) {
   StringTokenizer tokenizer = new StringTokenizer(line, delim);
   int legacyid = Integer.parseInt(tokenizer.nextToken());
   tokenizer.nextToken(); // type
   tokenizer.nextToken(); // from
   tokenizer.nextToken(); // to
   double diameter = Double.parseDouble(tokenizer.nextToken());
   double drag = Double.parseDouble(tokenizer.nextToken());
   double resistance = Double.parseDouble(tokenizer.nextToken());

   Resistor resistor = registerResistor(legacyid);
   resistor.setDiameter(diameter);
   resistor.setAttribute(Resistor.DRAG_FACTOR_KEY,drag);
   resistor.setResistance(resistance);
   resistor.setFlow(0.0);
   resistor.setCapacity(Double.MAX_VALUE);
   resistor.setStatus(true);

   Vector<Point> points = new Vector<Point>();
   points.add(fromJcn.getCoordinate());
   points.add(toJcn.getCoordinate());
   resistor.setCoordinates(new LineImpl(points));
   return resistor;
 }
}
