package gov.lanl.micot.infrastructure.model;

import java.util.Set;

import gov.lanl.micot.util.geometry.Geometry;
import gov.lanl.micot.util.geometry.Line;

/**
 * An implementation the edge interface
 * @author Russell Bent
 */
public abstract class ConnectionImpl extends AssetImpl implements Connection {

  /**
   * Constructor
   */
  public ConnectionImpl() {
    super();
  }
  
  /**
   * Constructor
   * @param component
   */
//  public ConnectionImpl(Connection edge) {
  //  super(edge);
  //}
  
  @Override
  public Line getCoordinates() {
    return getAttribute(COORDINATE_KEY,Line.class);
  }

  @Override
  public void setCoordinates(Line points) {
    setAttribute(COORDINATE_KEY,points);
  }
  
  @Override
  public String toString() {
    Set<Object> keys = (getOutputKeys().size() == 0) ? identifierKeys : getOutputKeys();    
    String string = "";
    if (keys.size() > 1) {    
      string = "(";
    }
    for (Object key : keys) {
      string += getAttribute(key).toString() + ", ";
    }    
    string = string.trim();
    string = string.substring(0,string.length()-1);
    if (keys.size() > 1) {    
      string += ")";
    }
    return string;
  }

  @Override
  public Geometry getGeometry() {
    return getCoordinates();
  }
  
}
