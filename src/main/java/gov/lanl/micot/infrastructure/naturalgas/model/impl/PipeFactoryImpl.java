package gov.lanl.micot.infrastructure.naturalgas.model.impl;

import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasNode;
import gov.lanl.micot.infrastructure.naturalgas.model.Pipe;
import gov.lanl.micot.infrastructure.naturalgas.model.PipeFactory;
import gov.lanl.micot.util.geometry.LineImpl;
import gov.lanl.micot.util.geometry.Point;

import java.util.Vector;

/**
 * Factory class for creating creating unique pipes
 * @author Russell Bent
 */
public class PipeFactoryImpl extends PipeFactory {

	/**
	 * Constructor
	 */
	public PipeFactoryImpl() {
	}
	
  /**
   * Register the battery
   * @param legacyId
   * @param bus
   * @return
   */
  private Pipe registerPipe() {
    Pipe pipe = createNewPipe();
    return pipe;
  }

  public Pipe createPipe(NaturalGasNode node1, NaturalGasNode node2, double diameter, double length, double resistance) {
    Pipe pipe = registerPipe();

    pipe.setDiameter(diameter);
    pipe.setLength(length);
    pipe.setResistance(resistance);
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
