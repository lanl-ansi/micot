package gov.lanl.micot.util.io.database.access;

import java.math.BigDecimal;
import java.util.Date;

import com.healthmarketscience.jackcess.Row;

/**
 * Jackcess version of the row data interface
 * @author Russell Bent
 *
 */
public class JackcessRowData implements RowData {

  private Row row;
  
  /**
   * Constructor
   * @param row
   */
  public JackcessRowData(Row row) {
    this.row = row;
  }

  @Override
  public Boolean getBoolean(String column) {
    return row.getBoolean(column);
  }

  @Override
  public Double getDouble(String column) {
    return row.getDouble(column);
  }

  @Override
  public Integer getInt(String column) {
    return row.getInt(column);
  }

  @Override
  public String getString(String column) {
    return row.getString(column);
  }

  @Override
  public Short getShort(String column) {
    return row.getShort(column);
  }

  @Override
  public BigDecimal getBigDecimal(String column) {
    return row.getBigDecimal(column);
  }
  
  @Override
  public Date getDate(String column) {
    return row.getDate(column);
  }
}
