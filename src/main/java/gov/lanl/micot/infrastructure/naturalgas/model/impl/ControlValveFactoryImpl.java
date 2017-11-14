package gov.lanl.micot.infrastructure.naturalgas.model.impl;

import gov.lanl.micot.infrastructure.naturalgas.model.ControlValve;
import gov.lanl.micot.infrastructure.naturalgas.model.ControlValveFactory;
import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasNode;
import gov.lanl.micot.util.geometry.LineImpl;
import gov.lanl.micot.util.geometry.Point;

import java.util.Vector;

/**
 * Factory class for creating creating unique control valves
 * @author Russell Bent
 */
public class ControlValveFactoryImpl extends ControlValveFactory {
  
	protected static final boolean DEFAULT_STATUS = true;
	
	/**
	 * Constructor
	 */
	public ControlValveFactoryImpl() {
	}
	
  /**
   * Register the battery
   * @param legacyId
   * @param bus
   * @return
   */
  private ControlValve registerControlValve() {
    ControlValve valve = createNewControlValve();
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
  public ControlValve createControlValve(NaturalGasNode node1, NaturalGasNode node2) {
    boolean status = true;
    
    ControlValve valve = registerControlValve();
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
