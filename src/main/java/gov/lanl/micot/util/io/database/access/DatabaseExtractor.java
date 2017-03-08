package gov.lanl.micot.util.io.database.access;

import java.util.Collection;

/**
 * Simple interface for extracting data from a database
 * @author Russell Bent
 *
 */
public interface DatabaseExtractor {

  /**
   * Get all the row data out of the database
   * @param database
   * @param tableName
   * @return
   */
  public Collection<RowData> getRows(String database, String tableName);

}
