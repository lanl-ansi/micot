package gov.lanl.micot.util.io.dcom.jacob;

import java.util.ArrayList;

import com.jacob.com.ComFailException;
import com.jacob.com.Dispatch;
import com.jacob.com.SafeArray;
import com.jacob.com.Variant;

import gov.lanl.micot.util.io.dcom.ComDataObject;
import gov.lanl.micot.util.io.dcom.ComObject;

/**
 * Interacts with com objectives using the jacob libraries
 * @author Russell Bent
 *
 */
public class JacobComObject implements ComObject {

  private Dispatch comObject = null;

  /**
   * Constructor
   * @param comObject
   */
  public JacobComObject(Dispatch comObject) {
    this.comObject = comObject;
  }
  
  @Override
  public ComObject call(String command, Object... obj) {
    Variant variant = Dispatch.call(comObject, command, obj);  
    if (variant.isNull()) {
      return null;
    }
   
    // an array has been returned, not being handled yet...
    if (variant.getvt() == 8204) {
      System.err.println("Warning: Call of " + command + " returned an array of data.  Interfaces don't support returning this as a dispatch object. Try using \"callReturn\"");
      variant.toSafeArray();
      return null;
    }
    
    try {
      Dispatch object = variant.toDispatch();
      return new JacobComObject(object);
    }
    catch (ComFailException e) {
      System.out.println("Variant is of type " + variant.getvt());
      System.out.println("Variant String: " + variant.toString());
      e.printStackTrace();
      return null;
    }
  }

  
  @Override
  public ComDataObject callData(String command, Object... obj) {
    Variant variant = Dispatch.call(comObject, command, obj);  
    return new JacobComDataObject(variant);
  }
  
  @Override
  public void put(String name, Object value) {
    Dispatch.put(comObject, name, new Variant(value));
  }

  @Override
  public ArrayList<Double> getDoubleArray(String name, Object... value) {
    ArrayList<Double> list = new ArrayList<Double>();
    SafeArray array = Dispatch.call(comObject, name, value).toSafeArray();
    for (int i = 0; i <= array.getElemSize(); ++i) {
      list.add(array.getDouble(i));
    }    
    return list;
  }

  @Override
  public ArrayList<String> getStringArray(String name, Object... value) {
    ArrayList<String> list = new ArrayList<String>();
    Variant variant = Dispatch.call(comObject, name, value);
    SafeArray array = variant.toSafeArray();
    for (int i = 0; i < array.getElemSize(); ++i) {
      list.add(array.getString(i));
    }    
    return list;
  }

  @Override
  public String getString(String name, Object... value) {
    return Dispatch.call(comObject, name, value).getString();
  }

  @Override
  public double getDouble(String name, Object... value) {
    return Dispatch.call(comObject, name, value).getDouble();
  }

  @Override
  public int getInteger(String name, Object... value) {
    return Dispatch.call(comObject, name, value).getInt();
  }
  
  @Override
  public boolean getBoolean(String name, Object... value) {
    return Dispatch.call(comObject, name, value).getBoolean();
  }

  @Override
  public void close() {
    comObject.safeRelease();
  }

}
