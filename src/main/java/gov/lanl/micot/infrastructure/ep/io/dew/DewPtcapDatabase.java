package gov.lanl.micot.infrastructure.ep.io.dew;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import gov.lanl.micot.infrastructure.ep.model.dew.DewPtcapData;
import gov.lanl.micot.util.io.database.access.DatabaseExtractor;
import gov.lanl.micot.util.io.database.access.DatabaseExtractorFactory;
import gov.lanl.micot.util.io.database.access.RowData;

/**
 * A simple structure for reading in ptcap
 * from the access database
 * 
 * @author Art Barnes, based on code by Russell Bent
 *
 */
public class DewPtcapDatabase  {
  
  private static final String STNAM      = "STNAM";
  private static final String QMENUDIS  = "QMENUDIS"; 
  private static final String ILIBROW = "ILIBROW"; 
  private static final String IPTROW = "IPTROW";
  private static final String STDESC = "STDESC"; 
  
  private static final String DRATKVAR = "DRATKVAR";
  private static final String SCON = "SCON";
  private static final String SNUMPOSRACK = "SNUMPOSRACK";
  
 
  /**
   * gets all the data associated with ptcap
   * @param filename
   * @return
   * @throws SQLException 
   */
  public Collection<DewPtcapData> getData(String filename) throws SQLException {
    ArrayList<DewPtcapData> data = new ArrayList<DewPtcapData>();
    
    String table = "PTCAP";
  
    DatabaseExtractor extractor = DatabaseExtractorFactory.getInstance().getDefaultExtractor();
    
    Collection<RowData> rowdata = extractor.getRows(filename, table);

    for (RowData row : rowdata) {
      DewPtcapData d = new DewPtcapData();      
      d.setStnam(row.getString(STNAM));
      d.setQmenudis(row.getShort(QMENUDIS));
      d.setIlibrow(row.getShort(ILIBROW));

// ---------------------------------
// new fields for shunt capacitor
      d.setIptrow(row.getShort(IPTROW));
      d.setDratkvar(row.getBigDecimal(DRATKVAR).doubleValue());
      d.setScon(row.getShort(SCON));
      d.setSnumposrack(row.getShort(SNUMPOSRACK));      

      d.setStdesc(row.getString(STDESC));        
      data.add(d);      
    }
    return data;
  }
  
}
