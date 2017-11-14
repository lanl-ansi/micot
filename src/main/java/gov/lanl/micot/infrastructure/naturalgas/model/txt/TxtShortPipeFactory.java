package gov.lanl.micot.infrastructure.naturalgas.model.txt;

import gov.lanl.micot.infrastructure.naturalgas.model.Junction;
import gov.lanl.micot.infrastructure.naturalgas.model.ShortPipe;
import gov.lanl.micot.infrastructure.naturalgas.model.ShortPipeFactory;
import gov.lanl.micot.util.geometry.LineImpl;
import gov.lanl.micot.util.geometry.Point;

import java.util.StringTokenizer;
import java.util.Vector;

/**
 * A factory for generating short pipes
 * 
 * @author Russell Bent
 * 
 */
public class TxtShortPipeFactory extends ShortPipeFactory {

  private static final String LEGACY_TAG = "TXT";

  /**
   * Singleton constructor
   */
  protected TxtShortPipeFactory() {
  }

  /**
   * Creates a line and data from a line
   * 
   * @param line
   * @return
   */
  public ShortPipe createPipeVersion2(String line, Junction fromJcn, Junction toJcn, String delim) {
    StringTokenizer tokenizer = new StringTokenizer(line, delim);
    int legacyid = Integer.parseInt(tokenizer.nextToken());
    tokenizer.nextToken(); // type
    tokenizer.nextToken(); // from
    tokenizer.nextToken(); // to
    double resistance = -1;

    ShortPipe pipe = registerShortPipe(legacyid);
    pipe.setResistance(resistance);
    pipe.setFlow(0.0);
    pipe.setCapacity(Double.MAX_VALUE);
    pipe.setStatus(true);

    Vector<Point> points = new Vector<Point>();
    points.add(fromJcn.getCoordinate());
    points.add(toJcn.getCoordinate());
    pipe.setCoordinates(new LineImpl(points));

    return pipe;
  }

  /**
   * Creates an ieiss line from another line
   * 
   * @param line
   * @param id
   * @param id2
   * @return
   */
  public void updateShortPipe(ShortPipe pipe, Junction id1, Junction id2, int legacyId) {
    if (pipe.getAttribute(TxtModelConstants.TXT_LEGACY_ID_KEY) == null) {
      pipe.setAttribute(TxtModelConstants.TXT_LEGACY_ID_KEY, legacyId);
      registerLegacy(LEGACY_TAG, legacyId, pipe);
    }
  }

  /**
   * Register the pipe
   * 
   * @param legacyId
   * @param bus
   * @return
   */
  private ShortPipe registerShortPipe(int legacyId) {
    ShortPipe pipe = getLegacy(LEGACY_TAG, legacyId);
    if (pipe == null) {
      pipe = createNewShortPipe();
      pipe.addOutputKey(TxtModelConstants.TXT_LEGACY_ID_KEY);
      pipe.setAttribute(TxtModelConstants.TXT_LEGACY_ID_KEY, legacyId);
      registerLegacy(LEGACY_TAG, legacyId, pipe);
    }
    return pipe;
  }
  
  @Override
  protected ShortPipe getLegacy(String legacyTag, Object key) {
    return super.getLegacy(legacyTag, key);
  }
  
}
