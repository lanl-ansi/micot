package gov.lanl.micot.infrastructure.naturalgas.model.txt;

import gov.lanl.micot.infrastructure.naturalgas.model.Junction;
import gov.lanl.micot.infrastructure.naturalgas.model.Pipe;
import gov.lanl.micot.infrastructure.naturalgas.model.PipeFactory;
import gov.lanl.micot.util.geometry.LineImpl;
import gov.lanl.micot.util.geometry.Point;

import java.util.StringTokenizer;
import java.util.Vector;

/**
 * A factory for generating pipes
 * 
 * @author Russell Bent
 * 
 */
public class TxtPipeFactory extends PipeFactory {

  private static final String LEGACY_TAG = "TXT";

  /**
   * Singleton constructor
   */
  protected TxtPipeFactory() {
  }

  /**
   * Creates a line and data from a line
   * 
   * @param line
   * @return
   */
  public Pipe createPipeVersion1(String line, Junction fromJcn, Junction toJcn, String delim) {
    StringTokenizer tokenizer = new StringTokenizer(line, delim);
    int legacyid = Integer.parseInt(tokenizer.nextToken());
    tokenizer.nextToken(); // from
    tokenizer.nextToken(); // to
    double diameter = Double.parseDouble(tokenizer.nextToken());
    double length = Double.parseDouble(tokenizer.nextToken());
    double resistance = Double.parseDouble(tokenizer.nextToken());

    Pipe pipe = registerPipe(legacyid);
    pipe.setDiameter(diameter);
    pipe.setLength(length);
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
   * Creates a line and data from a line
   * 
   * @param line
   * @return
   */
  public Pipe createPipeVersion2(String line, Junction fromJcn, Junction toJcn, String delim) {
    StringTokenizer tokenizer = new StringTokenizer(line, delim);
    int legacyid = Integer.parseInt(tokenizer.nextToken());
    tokenizer.nextToken(); // type
    tokenizer.nextToken(); // from
    tokenizer.nextToken(); // to
    double diameter = Double.parseDouble(tokenizer.nextToken());
    double length = Double.parseDouble(tokenizer.nextToken());
    double resistance = Double.parseDouble(tokenizer.nextToken());

    Pipe pipe = registerPipe(legacyid);
    pipe.setDiameter(diameter);
    pipe.setLength(length);
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
  public void updatePipe(Pipe pipe, Junction id1, Junction id2, int legacyId) {
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
  private Pipe registerPipe(int legacyId) {
    Pipe pipe = getLegacy(LEGACY_TAG, legacyId);
    if (pipe == null) {
      pipe = createNewPipe();
      pipe.addOutputKey(TxtModelConstants.TXT_LEGACY_ID_KEY);
      pipe.setAttribute(TxtModelConstants.TXT_LEGACY_ID_KEY, legacyId);
      registerLegacy(LEGACY_TAG, legacyId, pipe);
    }
    return pipe;
  }
  
  @Override
  protected Pipe getLegacy(String legacyTag, Object key) {
    return super.getLegacy(legacyTag, key);
  }
  
}
