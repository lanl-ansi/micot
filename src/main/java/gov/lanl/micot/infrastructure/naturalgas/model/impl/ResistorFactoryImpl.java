package gov.lanl.micot.infrastructure.naturalgas.model.impl;

import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasNode;
import gov.lanl.micot.infrastructure.naturalgas.model.Resistor;
import gov.lanl.micot.infrastructure.naturalgas.model.ResistorFactory;
import gov.lanl.micot.util.geometry.LineImpl;
import gov.lanl.micot.util.geometry.Point;

import java.util.Vector;

/**
 * Factory class for creating creating unique resistors
 * @author Russell Bent
 */
public class ResistorFactoryImpl extends ResistorFactory {
  
	protected static final boolean    DEFAULT_STATUS = true;
	
	/**
	 * Constructor
	 */
	public ResistorFactoryImpl() {
	}
	
  /**
   * Register the battery
   * @param legacyId
   * @param bus
   * @return
   */
  private Resistor registerResistor() {
    Resistor resistor = createNewResistor();
    return resistor;
  }

  /**
   * Create a resistor from scratch
   * @param node1
   * @param node2
   * @param diameter
   * @param length
   * @param resistance
   * @param minRatio
   * @param maxRatio
   * @return
   */
  public Resistor createResistor(NaturalGasNode node1, NaturalGasNode node2) {
    boolean status = true;
    
    Resistor resistor = registerResistor();
    resistor.setResistance(0.0);
    resistor.setDiameter(0.0);
    resistor.setLength(0.0);
    resistor.setStatus(status);
     
    Vector<Point> points = new Vector<Point>();
    points.add(node1.getJunction().getCoordinate());
    points.add(node2.getJunction().getCoordinate());
    resistor.setCoordinates(new LineImpl(points.toArray(new Point[0])));
    return resistor;
  }
	

}
