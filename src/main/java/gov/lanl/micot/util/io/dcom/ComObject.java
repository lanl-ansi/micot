package gov.lanl.micot.util.io.dcom;

import java.util.ArrayList;

/**
 * An interface for interacting with com objects
 * @author Russell Bent
 */
public interface ComObject {

  /**
   * Call a function specified by the com object, expected to return a dispatchable com object
   * @param command
   * @return
   */
  public ComObject call(String command, Object... obj);

  /**
   * Call a function specified by the com object that is expected to return some sort of data
   * @param command
   * @param obj
   * @return
   */
  public ComDataObject callData(String command, Object... obj);
  
  /**
   * Put some value into the com object
   * @param name
   * @param value
   */
  public void put(String name, Object value);

  /**
   * Get a double array
   * @param name
   * @param value
   * @return
   */
  public ArrayList<Double> getDoubleArray(String name, Object... value);

  /**
   * Get a string array
   * @param name
   * @param value
   * @return
   */
  public ArrayList<String> getStringArray(String name, Object... value);

  /**
   * Get the string
   * @param name
   * @param value
   * @return
   */
  public String getString(String name, Object... value);

  /**
   * Get the double
   * @param name
   * @param value
   * @return
   */
  public double getDouble(String name, Object... value);

  /**
   * Get the integer
   * @param name
   * @param value
   * @return
   */
  public int getInteger(String name, Object... value);

  /**
   * Get the boolean
   * @param string
   * @param value
   * @return
   */
  public boolean getBoolean(String string, Object... value);

  /**
   * Close out the object
   */
  public void close();


  
}
