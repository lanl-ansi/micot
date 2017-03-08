package gov.lanl.micot.util.io;

import java.util.Collection;
import java.util.Set;

/**
 * Interface for flags
 * @author Russell Bent
 *
 */
public interface Flags {
  
  /**
   * Get the value as an integer
   * @param key
   * @return
   */
  public Integer getInt(String key);

  /**
   * Get the value as a double
   * @param key
   * @return
   */
  public Double getDouble(String key);
  
  /**
   * Get the value as a string
   * @param key
   * @return
   */
  public String getString(String key);

  /**
   * Fill up the flags with other flags
   * @param flags
   */
  public void fill(Flags flags);

  /**
   * Get the boolean value
   * @param key
   * @return
   */
  public Boolean getBoolean(String key);

  /**
   * Get an object based on a class
   * @param <E>
   * @param key
   * @param cls
   * @return
   */
  public <E> E get(String key, Class<E> cls);

  /**
   * Generic get option
   * @param key
   * @return
   */
  public Object get(String key);
  
  /**
   * Add some flags
   * @param key
   * @param addFlags
   */
  public void addToCollection(String key, Object obj);
  
  /**
   * Get the flags associated a key
   * @param key
   * @return
   */
  public <E> Collection<E> getCollection(String key, Class<E> cls);

  /**
   * Put something in the flags
   * @param key
   * @param value
   * @return
   */
  public Object put(String key, Object value);
  
  /**
   * Get all the keys in the set
   * @return
   */
  public Set<String> keySet();
}
