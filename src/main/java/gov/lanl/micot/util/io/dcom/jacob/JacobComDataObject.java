package gov.lanl.micot.util.io.dcom.jacob;

import java.util.ArrayList;

import com.jacob.com.SafeArray;
import com.jacob.com.Variant;

import gov.lanl.micot.util.io.dcom.ComDataObject;

/**
 * Local jacob version of the data object
 * @author Russell Bent
 *
 */
public class JacobComDataObject implements ComDataObject {
  
  private Variant variant = null;
  
  /**
   * Constructor
   * @param variant
   */
  public JacobComDataObject(Variant variant) {
    this.variant = variant;
  }

  @Override
  public double getDoubleValue() {
    double value = variant.getDouble();
    return value;
  }

  @Override
  public int getIntValue() {
    int value = variant.getInt();
    return value;
  }

  @Override
  public String getStringValue() {
    return variant.getString();
  }

  @Override
  public ArrayList<ComDataObject> getArrayValue() {
    ArrayList<ComDataObject> values = new ArrayList<ComDataObject>();    
    SafeArray array = variant.toSafeArray();
    int lbound = array.getLBound();
    int ubound = array.getUBound();
    for (int i = lbound; i <= ubound; ++i) {
      values.add(new JacobComDataObject(array.getVariant(i)));
    }
    return values;
  }

  @Override
  public ArrayList<Integer> getIntArrayValue() {
    ArrayList<Integer> values = new ArrayList<Integer>();
    if (variant.isNull()) {
      return values;
    }
    SafeArray array = variant.toSafeArray();
    int lbound = array.getLBound();
    int ubound = array.getUBound();
    for (int i = lbound; i <= ubound; ++i) {
      values.add(array.getInt(i));
    }
    return values;
  }

  @Override
  public ArrayList<String> getStringArrayValue() {
    ArrayList<String> values = new ArrayList<String>();
    if (variant.isNull()) {
      return values;
    }
    SafeArray array = variant.toSafeArray();
    int lbound = array.getLBound();
    int ubound = array.getUBound();
    for (int i = lbound; i <= ubound; ++i) {
      values.add(array.getString(i));
    }
    return values;
  }
  
  @Override
  public boolean getBooleanValue() {
    boolean value = variant.getBoolean();
    return value;
  }

  @Override
  public String toString() {
    return variant.toString();
  }
}
