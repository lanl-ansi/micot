package gov.lanl.micot.util.collection;

import java.util.Arrays;
import java.util.Iterator;

/**
 * Represents an immutable ordered-set of objects. Due to the Java type system
 * an n-ary object cannot be strongly typed. Subclasses fix the tuple size and
 * add strong typing via type variables.
 * 
 * 
 * This class can be used for multi-value keys in maps.
 * 
 * @author 236322
 * 
 */
public class Tuple implements Iterable<Object>, Comparable<Tuple> {
  // These primes were intended to help with hashing
  // for the moment they do not seem nessiary
  /*
   * private static int[] PRIMES =
   * {2,3,5,7,11,13,17,19,23,29,31,37,41,43,47,53,59,61,67,71,73,79,83,89,
   * 97,101,103,107,109,113,127,131,137,139,149,151,157,163,167,173,179,
   * 181,191,193,197,199};
   */
  private Object[] _values;

  public Tuple(Object... values) {
    _values = values;
  }

  /**
   * get the i-th item in the tuple
   * 
   * @param i
   * @return
   */
  public Object get(int i) {
    if (i < 0 || i >= _values.length) {
      throw new IndexOutOfBoundsException("tuple values from [0.." + _values.length + "] : with " + i);
    }
    return _values[i];
  }

  @Override
  public Iterator<Object> iterator() {
    return Arrays.asList(_values).iterator();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + Arrays.hashCode(_values);
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Tuple other = (Tuple) obj;
    if (!Arrays.equals(_values, other._values))
      return false;
    return true;
  }

  @Override
  public String toString() {
    StringBuffer buff = new StringBuffer();

    buff.append("(");
    boolean first = true;
    for (Object o : _values) {
      if (first) {
        first = false;
      }
      else {
        buff.append(",");
      }
      buff.append(o.toString());
    }
    buff.append(")");

    return buff.toString();
  }

  @Override
  public int compareTo(Tuple arg0) {
    Iterator<Object> thisIt = iterator();
    Iterator<Object> thatIt = arg0.iterator();
    while (thisIt.hasNext()) {
      Object me = thisIt.next();
      Object you = thatIt.next();
      if (me instanceof Comparable) {
        int value = ((Comparable) me).compareTo(you);
        if (value != 0) {
          return value;
        }

        // "this" has more objects and "this" and "that" are the same up to the
        // size of "that"
        if (!thatIt.hasNext() && thisIt.hasNext()) {
          return 1;
        }
      }
    }

    // "that" has more objects and "this" and "that" are the same up to the size
    // of "this"
    if (thatIt.hasNext()) {
      return -1;
    }
    return 0;
  }
}
