package gov.lanl.micot.util.io.database.access;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;
import com.healthmarketscience.jackcess.Row;
import com.healthmarketscience.jackcess.Table;


/**
 * Implementation of database extraction that uses the jackaccess data structures
 * @author Russell Bent
 */
public class JackcessDatabaseExtractor implements DatabaseExtractor {
    
  
  /**
   * protected constructor
   */
  protected JackcessDatabaseExtractor() {    
  }
  
  @Override
  public Collection<RowData> getRows(String database, String tableName) {
    Table table = null;
    Database db = null;
    try {
      db = DatabaseBuilder.open(new File(database));
      table = db.getTable(tableName);
    } 
    catch (IOException e) {
      e.printStackTrace();
    }
    ArrayList<RowData> data = new ArrayList<RowData>();
    for(Row row : table) {
      data.add(new JackcessRowData(row));
    }
    
    try {
      db.close();
    } 
    catch (IOException e) {
      e.printStackTrace();
    }
    return data;
  }
  
}
