package gov.lanl.micot.infrastructure.model;

import gov.lanl.micot.util.geometry.Point;

/**
 * An interface for convientently identifying infrastructure components
 * @author Russell Bent
 */
public interface Component extends Asset {
  
	/**
	 * Gets the coordiate
	 * @return
	 */
  public Point getCoordinate();
  
  /**
   * Sets the point
   * @param point
   */
  public void setCoordinate(Point point);
  

}
