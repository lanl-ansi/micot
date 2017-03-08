/**
 * 
 */
package gov.lanl.micot.util.collection;

import java.util.HashMap;
import java.util.Map;

/** A bidirectional map
 * @author Hari S. Khalsa
 *
 */
public class BidirectionalMap<K, V> extends HashMap<K,V> {

  private static final long serialVersionUID = 1L;
  private HashMap<V, K> keyMap;
  
  /** Constructs an empty HashMap with the default initial capacity (16) and the default load factor (0.75).
   * 
   */
  public BidirectionalMap() {
    super();
    keyMap = new HashMap<V, K>();
  }
  
  /** Constructs an empty HashMap with the specified initial capacity and the default load factor (0.75).
   * 
   */
  public BidirectionalMap(int initialCapacity) {
    super(initialCapacity);
    keyMap = new HashMap<V, K>(initialCapacity);
  }
  
  /**Constructs an empty HashMap with the specified initial capacity and load factor.
   * @param m
   */
  public BidirectionalMap(int initialCapacity, float loadFactor) {
    super(initialCapacity, loadFactor);
    keyMap = new HashMap<V, K>(initialCapacity, loadFactor);
  }
  
  /** Constructs a new HashMap with the same mappings as the specified Map.
   * 
   */
  public BidirectionalMap(Map<? extends K,? extends V> m) {    
    super(m);
    
    putAllInKeyMap(m);
  }
  
  private void putAllInKeyMap(Map<? extends K,? extends V> m)
  {
    if (keyMap == null)
      keyMap = new HashMap<V, K>(m.size());
    for (Map.Entry<? extends K, ? extends V> e : m.entrySet())
      keyMap.put(e.getValue(), e.getKey());
  }
  
  /** Removes all of the mappings from this map.
   * 
   */
  public void clear() {
    super.clear();
    keyMap.clear();
  }
  
  /** Associates the specified value with the specified key in this map.
   * 
   */
  public V put(K key, V value) {
    keyMap.put(value, key);
    return super.put(key, value);
  }
  
  /** Copies all of the mappings from the specified map to this map.
   * 
   */
  public void putAll(Map<? extends K,? extends V> m) {
    putAllInKeyMap(m);
    super.putAll(m);
  }
  
  /** Removes the mapping for the specified key from this map if present.
   * 
   */
  public V remove(Object key) {
    V toBeRemoved = super.remove(key);
    keyMap.remove(toBeRemoved);
    return toBeRemoved;
  }
  
  /**
  * Returns a shallow copy of this <tt>HashMap</tt> instance: the keys and
  * values themselves are not cloned.
  *
  * @return a shallow copy of this map
  */
  @SuppressWarnings("unchecked")
  public Object clone() {
    BidirectionalMap<K,V> result = null;
    result = (BidirectionalMap<K,V>)super.clone();
    
    result.keyMap = (HashMap<V,K>)keyMap.clone();
    
    return result;
  } 
  
  
  public K getKey(V value)
  {
    return keyMap.get(value);
  }
  
}
