package gov.lanl.micot.infrastructure.ep.model.dew;

import java.util.ArrayList;
import java.util.Collection;

/**
 * An interface to interacting with the data of the .dew file
 * @author Russell Bent
 *
 */
public interface DewFileData {

  /**
   * Does id exist
   * @param id
   * @return
   */
  public boolean hasId(DewLegacyId id);
  
  /**
   * get the component ids in the data
   * @return
   */
  public Collection<DewLegacyId> getComponentIds();

  /**
   * Remove some data
   * @param id
   */
  public void remove(DewLegacyId id);

  /**
   * Close down the dew file data
   * @throws DewException
   */
  public void close();

  /**
   * A notification that the data load has completed
   */
  public void dataReadComplete();
  
  /**
   * add a string to this data structure
   * @param id
   * @param str
   */
  public void addComponentDataById(DewLegacyId id, ArrayList<String> str);

  /**
   * initialize the component data
   * @param id
   */
  public void initComponentDataById(DewLegacyId id);
  

  /**
   * Get component data by Id
   * @param id
   * @param index
   * @return
   */
  public ArrayList<String> getComponentDataById(DewLegacyId id, int index);

  /**
   * get component data by size
   * @return
   */
  public int componentDataByIdSize();

  /**
   * get a hash map size
   * @param id
   * @return
   */
  public int componentDataByIdSize(DewLegacyId id);

  
  
}
