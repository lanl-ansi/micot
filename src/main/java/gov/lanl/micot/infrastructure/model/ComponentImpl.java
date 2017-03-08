package gov.lanl.micot.infrastructure.model;

import gov.lanl.micot.util.geometry.Geometry;
import gov.lanl.micot.util.geometry.Point;

/**
 * Some common code for components that can be used
 * @author Russell Bent
 */
public abstract class ComponentImpl extends AssetImpl implements Component  {

  /**
   * Constructor
   */
  public ComponentImpl() {
    super();
  }

  /**
   * Constructor
   * @param component
   */
//  public ComponentImpl(Component component) {
  //  super(component);
  //}

  @Override
  public Point getCoordinate() {
    return getAttribute(COORDINATE_KEY,Point.class);
  }

  @Override
  public void setCoordinate(Point point) {
    setAttribute(COORDINATE_KEY,point);
  }

  @Override
  public Geometry getGeometry() {
    return getCoordinate();
  }

  public abstract ComponentImpl clone();
  
}
