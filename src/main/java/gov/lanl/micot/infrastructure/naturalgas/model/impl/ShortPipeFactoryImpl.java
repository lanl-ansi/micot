package gov.lanl.micot.infrastructure.naturalgas.model.impl;

import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasNode;
import gov.lanl.micot.infrastructure.naturalgas.model.ShortPipe;
import gov.lanl.micot.infrastructure.naturalgas.model.ShortPipeFactory;
import gov.lanl.micot.util.geometry.LineImpl;
import gov.lanl.micot.util.geometry.Point;

import java.util.Vector;

/**
 * Factory class for creating creating unique pipes
 * @author Russell Bent
 */
public class ShortPipeFactoryImpl extends ShortPipeFactory {

	/**
	 * Constructor
	 */
	public ShortPipeFactoryImpl() {
	}
	
  /**
   * Register the battery
   * @param legacyId
   * @param bus
   * @return
   */
  private ShortPipe registerShortPipe() {
    ShortPipe pipe = createNewShortPipe();
    return pipe;
  }

  public ShortPipe createPipe(NaturalGasNode node1, NaturalGasNode node2) {
    ShortPipe pipe = registerShortPipe();

    pipe.setFlow(0.0);
    pipe.setCapacity(Double.MAX_VALUE);
    pipe.setStatus(true);
    
    Vector<Point> points = new Vector<Point>();
    points.add(node1.getJunction().getCoordinate());
    points.add(node2.getJunction().getCoordinate());
    pipe.setCoordinates(new LineImpl(points.toArray(new Point[0])));

    return pipe;
  }

}
