package gov.lanl.micot.infrastructure.naturalgas.model.impl;

import gov.lanl.micot.infrastructure.naturalgas.model.Compressor;
import gov.lanl.micot.infrastructure.naturalgas.model.CompressorFactory;
import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasNode;
import gov.lanl.micot.util.geometry.LineImpl;
import gov.lanl.micot.util.geometry.Point;

import java.util.Vector;

/**
 * Factory class for creating creating unique compressors
 * @author Russell Bent
 */
public class CompressorFactoryImpl extends CompressorFactory {
  
//	private static CompressorFactoryImpl instance = null;
	
	protected static final boolean    DEFAULT_STATUS = true;
	
	//public static CompressorFactoryImpl getInstance() {
	//	if (instance == null) {
		//	instance = new CompressorFactoryImpl();
		//}
		//return instance;
	//}
	
	/**
	 * Constructor
	 */
	public CompressorFactoryImpl() {
	}
	
  /**
   * Register the battery
   * @param legacyId
   * @param bus
   * @return
   */
  private Compressor registerCompressor() {
    Compressor compressor = createNewCompressor();
    return compressor;
  }

  /**
   * Create a compressor from scratch
   * @param node1
   * @param node2
   * @param diameter
   * @param length
   * @param resistance
   * @param minRatio
   * @param maxRatio
   * @return
   */
  public Compressor createCompressor(NaturalGasNode node1, NaturalGasNode node2, double diameter, double length, double resistance, double minRatio, double maxRatio) {
    boolean status = true;
    
    Compressor compressor = registerCompressor();
    compressor.setResistance(resistance);
    compressor.setDiameter(diameter);
    compressor.setLength(length);
    compressor.setMinimumCompressionRatio(minRatio);
    compressor.setMaximumCompressionRatio(maxRatio);
    compressor.setDesiredStatus(status);
    compressor.setActualStatus(status);
     
    Vector<Point> points = new Vector<Point>();
    points.add(node1.getJunction().getCoordinate());
    points.add(node2.getJunction().getCoordinate());
    compressor.setCoordinates(new LineImpl(points.toArray(new Point[0])));
    return compressor;
  }
	

}
