package gov.lanl.micot.infrastructure.naturalgas.model.impl;

import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasNode;
import gov.lanl.micot.infrastructure.naturalgas.model.Valve;
import gov.lanl.micot.infrastructure.naturalgas.model.ValveFactory;
import gov.lanl.micot.util.geometry.LineImpl;
import gov.lanl.micot.util.geometry.Point;

import java.util.Vector;

/**
 * Factory class for creating creating unique valves
 * @author Russell Bent
 */
public class ValveFactoryImpl extends ValveFactory {
  
	protected static final boolean    DEFAULT_STATUS = true;
	
	/**
	 * Constructor
	 */
	public ValveFactoryImpl() {
	}
	
  /**
   * Register the battery
   * @param legacyId
   * @param bus
   * @return
   */
  private Valve registerValve() {
    Valve valve = createNewValve();
    return valve;
  }

  /**
   * Create a valve from scratch
   * @param node1
   * @param node2
   * @param diameter
   * @param length
   * @param resistance
   * @param minRatio
   * @param maxRatio
   * @return
   */
  public Valve createValve(NaturalGasNode node1, NaturalGasNode node2) {
    boolean status = true;
    
    Valve valve = registerValve();
    valve.setResistance(0.0);
    valve.setDiameter(0.0);
    valve.setLength(0.0);
    valve.setStatus(status);
     
    Vector<Point> points = new Vector<Point>();
    points.add(node1.getJunction().getCoordinate());
    points.add(node2.getJunction().getCoordinate());
    valve.setCoordinates(new LineImpl(points.toArray(new Point[0])));
    return valve;
  }
	

}
