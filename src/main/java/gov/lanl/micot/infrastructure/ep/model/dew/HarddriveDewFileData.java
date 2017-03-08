package gov.lanl.micot.infrastructure.ep.model.dew;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Interacts with the dew file data on the harddrive
 * @author Russell Bent
 */
public class HarddriveDewFileData implements DewFileData {

  private ArrayList<ArrayList<String>> _componentDataByIdCache = null;
  private DewLegacyId _componentCache = null;
  private boolean _wasPutLast = false;
  private String _dataDirectory = "";
  private Set<DewLegacyId> _componentIds = null;

  /**
   * Constructor
   */
  public HarddriveDewFileData() {
    Random random = new Random(System.currentTimeMillis());
    _dataDirectory = System.getProperty("user.dir") + File.separatorChar + "tmp-" + random.nextInt();
    File file = new File(_dataDirectory);
    file.mkdir();
    _componentIds = new HashSet<DewLegacyId>();
  }
  
  /**
   * Get the filename of an id
   * @param id
   * @return
   */
  private String getFileName(DewLegacyId id) {
    return _dataDirectory + File.separatorChar + id;
  }

  @Override
  public boolean hasId(DewLegacyId id) {
    return _componentIds.contains(id);
  }

  @Override
  public Collection<DewLegacyId> getComponentIds() {
    return _componentIds;
  }

  @Override
  public void remove(DewLegacyId id) {
    _componentIds.remove(id);
    File file = new File(getFileName(id));
    file.delete();
  }

  @Override
  public void close() {
    File file = new File(_dataDirectory);
    String[] entries = file.list();
    for (String s : entries) {
      File currentFile = new File(file.getPath(),s);
      currentFile.delete();
    }
    file.delete();
  }

  @Override
  public void dataReadComplete() {
    saveData();
  }
  
  @Override
  public void addComponentDataById(DewLegacyId id, ArrayList<String> str) {
    if (!hasId(id)) {
      initComponentDataById(id);
    }
    
    if (_componentCache != null && !id.equals(_componentCache)) {
      saveData();
      _wasPutLast = true;
      _componentCache = id;
      _componentDataByIdCache = new ArrayList<ArrayList<String>>();
    }
    _componentDataByIdCache.add(str);
  }

  @Override
  public void initComponentDataById(DewLegacyId id) {
    if (_componentCache != null && !id.equals(_componentCache)) {
      saveData();
    }
    _wasPutLast = true;
    _componentCache = id;
    _componentDataByIdCache = new ArrayList<ArrayList<String>>();
   _componentIds.add(id);
  }
  
  @Override
  public ArrayList<String> getComponentDataById(DewLegacyId id, int index) {
    if (!id.equals(_componentCache) && _wasPutLast) {
      saveData();
    }
    
    if (!id.equals(_componentCache)) {
      loadData(id);
    }
    return _componentDataByIdCache.get(index);
  }

  @Override
  public int componentDataByIdSize() {
    return _componentIds.size();
  }

  @Override
  public int componentDataByIdSize(DewLegacyId id) {
    loadData(id);
    return _componentDataByIdCache.size();
  }
  
  
  
  
  /**
   * Load the dew data
   * @param id
   */
  private void loadData(DewLegacyId id) {
    try {
      DataInputStream in = new DataInputStream(new FileInputStream(getFileName(id)));
      BufferedReader br = new BufferedReader(new InputStreamReader(in));
      _componentDataByIdCache = new ArrayList<ArrayList<String>>();
      String currentLine = null;
      while ((currentLine = br.readLine()) != null) {
        StringTokenizer tokenizer = new StringTokenizer(currentLine, ",");
        ArrayList<String> data = new ArrayList<String>();
        while (tokenizer.hasMoreTokens()) {
          data.add(tokenizer.nextToken().trim());
        }        
        _componentDataByIdCache.add(data);
      }

      _componentCache = id;
      br.close();
      in.close();
    } 
    catch (IOException e) {
      e.printStackTrace();
    }
    
  }
  
  /**
   * Save the dew data to a file
   */
  private void saveData() {
    PrintStream ps = null;
    try {
      ps = new PrintStream(getFileName(_componentCache));
    } 
    catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    for (ArrayList<String> list : _componentDataByIdCache) {
      for (int i = 0; i < list.size(); ++i) {
        ps.print(list.get(i));
        if (i < list.size() -1) {
          ps.print(", ");
        }
      }
      ps.println();
    }
    ps.close();
  }
  
  
  
}
