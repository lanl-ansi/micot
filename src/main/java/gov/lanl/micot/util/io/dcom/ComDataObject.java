package gov.lanl.micot.util.io.dcom;

import java.util.ArrayList;

/**
 * A com data object is DCom object that contains data
 * @author Russell Bent
 *
 */
public interface ComDataObject {

  /**
   * Get the double value of the object
   * @return
   */
  public double getDoubleValue();
  
  /**
   * Get the int value of the object
   * @return
   */
  public int getIntValue();
  
  /**
   * Get the string value of the object
   * @return
   */
  public String getStringValue();
  
  /**
   *  Get the array value of the object
   */
  public ArrayList<ComDataObject> getArrayValue();
  
  /**
   * Get the array in integer values
   * @return
   */
  public ArrayList<Integer> getIntArrayValue();

  /**
   * Get the array in integer values
   * @return
   */
  public ArrayList<String> getStringArrayValue();
  
  /**
   * Get the boolean value of the object
   * @return
   */
  public boolean getBooleanValue();
  
  /**
   * Return whether or not the entry is null
   * @return
   */
  public boolean isNull();
  
}
