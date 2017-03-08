package gov.lanl.micot.util.io.database.access;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Abstract interface carrying around row data
 * @author Russell Bent
 *
 */
public interface RowData {

  /**
   * Gets the data in boolean format
   * @param columnData
   * @return
   */
  public Boolean getBoolean(String column);

  /**
   * gets the data in double format
   * @param name
   * @return
   */
  public Double getDouble(String column);

  /**
   * Gets the data in integer format
   * @param column
   * @return
   */
  public Integer getInt(String column);

  /**
   * gets the data in string format
   * @param column
   * @return
   */
  public String getString(String column);

  /**
   * Get the data in String format
   * @param column
   * @return
   */
  public Short getShort(String column);

  /**
   * Get the date in big decimal format
   * @param dxph1orr1
   * @return
   */
  public BigDecimal getBigDecimal(String column);

  /**
   * Get the date for column
   * @param column
   * @return
   */
  public Date getDate(String column); 
  
}
