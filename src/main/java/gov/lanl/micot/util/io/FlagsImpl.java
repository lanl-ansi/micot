package gov.lanl.micot.util.io;

import java.util.Collection;
import java.util.TreeMap;
import java.util.Vector;

/**
 * A generic flags class so that some common functionality of flags is available
 * all over the place
 * @author Russell Bent
 */
public class FlagsImpl extends TreeMap<String,Object> implements Flags {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 */
	public FlagsImpl() {
	  super();
	}
	
	@Override
	public Integer getInt(String key){
	  if(containsKey(key)) {
	  	if (get(key) instanceof Number) {
	  		return ((Number) get(key)).intValue();
	  	}
	    return Integer.parseInt(get(key).toString());
	  }
	  return null;
	}
	
	@Override
	public Double getDouble(String key){
    if(containsKey(key)){
	  	if (get(key) instanceof Number) {
	  		return ((Number) get(key)).doubleValue();
	  	}
	    return Double.parseDouble(get(key).toString());
    }
    return null;
  }
	
	@Override
	public String getString(String key) {
		return (get(key)==null) ? null : get(key).toString();
	}
	
	@Override
	public void fill(Flags flags) {
	  if (flags instanceof FlagsImpl) {
	    putAll((FlagsImpl)flags);
	  }
	  else {
	    throw new RuntimeException("Error: FlagsImpl::fill");
	  }
	}

	@Override
  public Boolean getBoolean(String key) {
    if(containsKey(key)){
  	  if (get(key) instanceof Boolean) {
  	  	return (Boolean) get(key);
  	  }
  	   return Boolean.parseBoolean(get(key).toString());
     }
    return null;
  }  

  @Override
  @SuppressWarnings("unchecked")  
  public <E> E get(String key, Class<E> cls) {
    return (E)get(key);
  }

  @Override
  @SuppressWarnings("unchecked")
	public void addToCollection(String key, Object obj) {
    if (get(key) == null) {
      put(key, new Vector<Object>());
    }
    get(key, Collection.class).add(obj);
  }
  
  @Override
  @SuppressWarnings("unchecked")
	public <E> Collection<E> getCollection(String key, Class<E> cls) {
  	if (get(key) instanceof Collection) {
  		return (Collection<E>)get(key, Collection.class);
  	}
  	if (get(key) != null) {
  		E obj = get(key,cls);
  		Vector<E> temp = new Vector<E>();
  		temp.add(obj);
  		return temp;
  	}  	
  	return null;    
  }

  @Override
  public Object get(String key) {
    return super.get(key);
  }

  
}
