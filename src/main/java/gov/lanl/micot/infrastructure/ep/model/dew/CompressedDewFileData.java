package gov.lanl.micot.infrastructure.ep.model.dew;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Interacts with the dew file data on using compression
 * @author Russell Bent
 */
public class CompressedDewFileData implements DewFileData {

  private LinkedHashMap<DewLegacyId, ArrayList<Object>> _componentDataById = null;
  private DewLegacyId _componentCache = null;
  private ArrayList<Object> _componentDataByIdCache = null;

  
  /**
   * Constructor
   */
  public CompressedDewFileData() {
    _componentDataById = new LinkedHashMap<DewLegacyId, ArrayList<Object>>();
  }
  
  @Override
  public boolean hasId(DewLegacyId id) {
    return _componentDataById.get(id) != null;
  }

  @Override
  public Collection<DewLegacyId> getComponentIds() {
    return _componentDataById.keySet();
  }

  @Override
  public void remove(DewLegacyId id) {
    _componentDataById.remove(id);
  }

  @Override
  public void close() {
  }

  @Override
  public void dataReadComplete() {
  }
  
  @Override
  public void addComponentDataById(DewLegacyId id, ArrayList<String> str) {
    if (!hasId(id)) {
      initComponentDataById(id);
    }
    _componentDataById.get(id).add(compressArrayList(str));
  }

  @Override
  public void initComponentDataById(DewLegacyId id) {
    _componentDataById.put(id, new ArrayList<Object>());
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public ArrayList<String> getComponentDataById(DewLegacyId id, int index) {
    
    // determine if we need to bring another set of data out of the compressed cache
    if (_componentCache == null || !_componentCache.equals(id)) {
      
      // put the data we have cached back in the compression list (in case we changed it)
      if (_componentCache != null) {
        for (int i = 0; i < _componentDataByIdCache.size(); ++i) {
          if (_componentDataByIdCache.get(i) instanceof ArrayList) {
            byte[] temp = compressArrayList((ArrayList)_componentDataByIdCache.get(i));
            _componentDataById.get(_componentCache).set(i, temp);
          }
        }
      }
      
      _componentCache = id;
      _componentDataByIdCache = (ArrayList<Object>) _componentDataById.get(id).clone();
    }
  
    Object obj = _componentDataByIdCache.get(index);
    if (obj instanceof ArrayList) {
      return (ArrayList<String>) obj;
    }
    else {
      ArrayList<String> array = uncompressArrayList((byte[])_componentDataById.get(id).get(index));
      _componentDataByIdCache.set(index, array); // let us cache those entries we use alot...
      return array;
    }
  }

  @Override
  public int componentDataByIdSize() {
    return _componentDataById.size();
  }

  @Override
  public int componentDataByIdSize(DewLegacyId id) {
    return _componentDataById.get(id) == null ? 0 : _componentDataById.get(id).size();
  }
    
  /**
   * compresses a string into bytes
   * @param str
   * @return
   * @throws IOException
   */
  private byte[] compressString(String str) {
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      GZIPOutputStream gzipOut = new GZIPOutputStream(baos);
      ObjectOutputStream objectOut = new ObjectOutputStream(gzipOut);
      objectOut.writeObject(str);
      objectOut.close();
      return baos.toByteArray();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
  
  /**
   * uncompress bytes back into a string
   * @param bytes
   * @return
   * @throws IOException 
   * @throws ClassNotFoundException 
   */
  private String uncompressString(byte[] bytes)  {
    try {
      ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
      GZIPInputStream gzipIn = new GZIPInputStream(bais);
      ObjectInputStream objectIn = new ObjectInputStream(gzipIn);
      String str = (String)objectIn.readObject();
      objectIn.close();
      return str;
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  
  
  /**
   * compresses a string into bytes
   * @param str
   * @return
   * @throws IOException
   */
  private byte[] compressArrayList(ArrayList<String> str) {
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      GZIPOutputStream gzipOut = new GZIPOutputStream(baos);
      ObjectOutputStream objectOut = new ObjectOutputStream(gzipOut);
      objectOut.writeObject(str);
      objectOut.close();
      return baos.toByteArray();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
  
  /**
   * uncompress bytes back into a string
   * @param bytes
   * @return
   * @throws IOException 
   * @throws ClassNotFoundException 
   */
  @SuppressWarnings("rawtypes")
  private ArrayList<String> uncompressArrayList(byte[] bytes)  {
    try {
      ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
      GZIPInputStream gzipIn = new GZIPInputStream(bais);
      ObjectInputStream objectIn = new ObjectInputStream(gzipIn);
      ArrayList<String> str = (ArrayList)objectIn.readObject();
      objectIn.close();
      return str;
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
  
  
  
  
  
    
  
  
}
