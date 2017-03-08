package gov.lanl.micot.infrastructure.naturalgas.model.impl;

import gov.lanl.micot.infrastructure.naturalgas.model.Junction;
import gov.lanl.micot.infrastructure.naturalgas.model.JunctionFactory;
import gov.lanl.micot.util.geometry.PointImpl;

/**
 * Factory class for creating Junctions and ensuring their uniqueness
 * @author Russell Bent
 */
public class JunctionFactoryImpl extends JunctionFactory {

//	private static JunctionFactoryImpl instance = null;
	
//	public static JunctionFactoryImpl getInstance() {
	//	if (instance == null) {
	//		instance = new JunctionFactoryImpl();
	//	}
	//	return instance;
	//}
	
	/**
	 * Constructor
	 */
	public JunctionFactoryImpl() {
	}
	
  /**
   * Register the junction
   * @param legacyId
   * @param bus
   * @return
   */
  private Junction registerJunction() {
    Junction junction = createNewJunction();
    return junction;
  }

  /**
   * Create a junction from scratch
   * @param x
   * @param y
   * @param minPressure
   * @param maxPressure
   * @return
   */
  public Junction createJunction(double x, double y, double minPressure, double maxPressure) {
    Junction junction = registerJunction();  
    junction.setDesiredStatus(true);
    junction.setActualStatus(true);
    junction.setMaximumPressure(maxPressure);
    junction.setMinimumPressure(minPressure);
    junction.setCoordinate(new PointImpl(x,y));    
    return junction;
  }
	

}
