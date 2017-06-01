package gov.lanl.micot.infrastructure.ep.io.dew;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import gov.lanl.micot.infrastructure.ep.model.dew.DewPtxfrmData;
import gov.lanl.micot.util.io.database.access.DatabaseExtractor;
import gov.lanl.micot.util.io.database.access.DatabaseExtractorFactory;
import gov.lanl.micot.util.io.database.access.RowData;

/**
 * A simple structure for reading in ptxfrm
 * from the access database
 * 
 * @author Russell Bent
 *
 */
public class DewPtxfrmDatabase  {
  
  private static final String STNAM      = "STNAM";
  private static final String QMENUDIS  = "QMENUDIS"; 
  private static final String ILIBROW = "ILIBROW"; 
  private static final String IPTROW = "IPTROW";
  private static final String STDESC = "STDESC"; 
  
  private static final String DPRIKV = "DPRIKV";
  private static final String DSECKV = "DSECKV";
  private static final String DNOMKVA = "DNOMKVA";
  
  private static final String SNUMSTEPS = "SNUMSTEPS";
  private static final String DSTEPSIZEFR = "DSTEPSIZEFR";
  private static final String QCOMPEXISTS = "QCOMPEXISTS";
  private static final String QUNIDIRECTION = "QUNIDIRECTION";
  
  /**
   * gets all the data associated with ptxfrm
   * @param filename
   * @return
   * @throws SQLException 
   */
  public Collection<DewPtxfrmData> getData(String filename) throws SQLException {
    ArrayList<DewPtxfrmData> data = new ArrayList<DewPtxfrmData>();
    
    String table = "PTXFRM";
  
    DatabaseExtractor extractor = DatabaseExtractorFactory.getInstance().getDefaultExtractor();
    
    Collection<RowData> rowdata = extractor.getRows(filename, table);

    for (RowData row : rowdata) {
      DewPtxfrmData d = new DewPtxfrmData();      
      d.setStnam(row.getString(STNAM));
      d.setQmenudis(row.getShort(QMENUDIS));
      d.setIlibrow(row.getShort(ILIBROW));

// ---------------------------------
// new fields for transformer
      d.setIptrow(row.getShort(IPTROW));
//      d.setIcmp(row.getBigDecimal(ICMP));
      d.setDprikv(row.getBigDecimal(DPRIKV).doubleValue());      
      d.setDseckv(row.getBigDecimal(DSECKV).doubleValue());
      d.setDnomkva(row.getBigDecimal(DNOMKVA).doubleValue());      
      
      d.setSnumsteps(row.getInt(SNUMSTEPS));
      d.setDstepsizefr(row.getBigDecimal(DSTEPSIZEFR).doubleValue());
      d.setQcompexists(row.getShort(QCOMPEXISTS));
      d.setQunidirection(row.getShort(QUNIDIRECTION));
// ---------------------------------
      
      
      d.setStdesc(row.getString(STDESC));        
      data.add(d);      
    }
    return data;
  }
  
}
