package gov.lanl.micot.infrastructure.naturalgas.model;

/**
 * Abstract class for defining resistors
 * @author Russell Bent
 *
 */
public interface Resistor extends NaturalGasConnection {	
    
  public static final String NAME_KEY                       = "NAME";
  public static final Object DRAG_FACTOR_KEY                = "DRAG_FACTOR";
  
  /**
   * Add a listener for data changes
   * @param listener
   */
  public void addResistorChangeListener(ResistorChangeListener listener);
  
  /**
   * Add a listener for data changes
   * @param listener
   */
  public void removeResistorChangeListener(ResistorChangeListener listener);
  
  /**
   * set the diameter
   * @param diameter
   */
  public void setDiameter(double diameter);
  
  /**
   * get the diameter
   * @return
   */
  public double getDiameter();

  /**
   * Set the length of the pipe
   * @param length
   */
  public void setLength(double length);
  
  /**
   * Get the length of the pipe
   * @return
   */
  public double getLength();
  
  /**
   * Clone the compressor
   * @return
   */
  public Resistor clone();
  
  /**
   * get the drag factor
   * @return
   */
  public double getDragFactor();
  
  /**
   * Set the drag factor
   */
  public void setDragFactor(double drag);
}
