package gov.lanl.micot.infrastructure.ep.io.dew;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import gov.lanl.micot.infrastructure.ep.model.dew.DewPtlinecondData;
import gov.lanl.micot.util.io.database.access.DatabaseExtractor;
import gov.lanl.micot.util.io.database.access.DatabaseExtractorFactory;
import gov.lanl.micot.util.io.database.access.RowData;

/**
 * A simple structure for reading in ptlinecond
 * from the access database
 * 
 * @author Russell Bent
 *
 */
public class DewPtlinecondDatabase  {
  
  private static final String STNAM      = "STNAM";
  private static final String QMENUDIS  = "QMENUDIS"; 
  private static final String ILIBROW = "ILIBROW"; 
  private static final String STDESC = "STDESC"; 
  private static final String STCONDTYP = "STCONDTYP"; 
  private static final String IWIRE = "IWIRE"; 
  private static final String LAWGORKCMIL = "LAWGORKCMIL"; 
  private static final String ICONDMAT = "ICONDMAT"; 
  private static final String DROHMPRLUL = "DROHMPRLUL"; 
  private static final String DTEMPFORROHMPRLUL = "DTEMPFORROHMPRLUL"; 
  private static final String IRCURTDEPDCRV = "IRCURTDEPDCRV"; 
  private static final String DGMRSUL = "DGMRSUL"; 
  private static final String DRADCONDSUL = "DRADCONDSUL"; 
  private static final String DWINDTEMPRATMULPRS = "DWINDTEMPRATMULPRS"; 
  private static final String DRATAMBTEMP0A = "DRATAMBTEMP0A"; 
  private static final String DRATAMBTEMP1A = "DRATAMBTEMP1A"; 
  private static final String DRATAMBTEMP2A = "DRATAMBTEMP2A"; 
  private static final String DRATAMBTEMP3A = "DRATAMBTEMP3A"; 
  private static final String DRATAMBTEMP4A = "DRATAMBTEMP4A"; 
  private static final String DRATAMBTEMP5A = "DRATAMBTEMP5A"; 
  private static final String DRATAMBTEMP6A = "DRATAMBTEMP6A"; 
  private static final String DRATAMBTEMP7A = "DRATAMBTEMP7A"; 
  private static final String DSTRENGTHFPRSUL2 = "DSTRENGTHFPRSUL2"; 
  private static final String ISTRESSSTRAIN = "ISTRESSSTRAIN"; 
  private static final String DWTPRMUL = "DWTPRMUL"; 
  private static final String DCAPTCSTD = "DCAPTCSTD"; 
  private static final String IMANUFACT = "IMANUFACT"; 
  private static final String INUMWIRESINCOND = "INUMWIRESINCOND"; 
  private static final String QEARTHINGCOND = "QEARTHINGCOND"; 
  private static final String QCONTAINSNEUTRALCOND = "QCONTAINSNEUTRALCOND"; 
  private static final String DNEUTDERATINGFACTOR = "DNEUTDERATINGFACTOR"; 
  private static final String IROWLINESPC = "IROWLINESPC"; 
  private static final String IZINDEX = "IZINDEX"; 
  
  /**
   * gets all the data associated with ptlinespc
   * @param filename
   * @return
   * @throws SQLException 
   */
  public Collection<DewPtlinecondData> getData(String filename) throws SQLException {
    ArrayList<DewPtlinecondData> data = new ArrayList<DewPtlinecondData>();
    
    String table = "PTLINECOND";
  
    DatabaseExtractor extractor = DatabaseExtractorFactory.getInstance().getDefaultExtractor();
    
    Collection<RowData> rowdata = extractor.getRows(filename, table);

    for (RowData row : rowdata) {
      DewPtlinecondData d = new DewPtlinecondData();      
      d.setStnam(row.getString(STNAM));
      d.setQmenudis(row.getShort(QMENUDIS));
      d.setIlibrow(row.getShort(ILIBROW));
      d.setStdesc(row.getString(STDESC));
      d.setStcondtyp(row.getString(STCONDTYP));
      d.setIwire(row.getShort(IWIRE));
      d.setLawgorkcmil(row.getShort(LAWGORKCMIL));
      d.setIcondmat(row.getShort(ICONDMAT));
      d.setDrohmprlul(row.getBigDecimal(DROHMPRLUL).doubleValue());
      d.setDtempforrohmprlul(row.getDouble(DTEMPFORROHMPRLUL));
      d.setIrcurtdepdcrv(row.getShort(IRCURTDEPDCRV) == null ? 0 : row.getShort(IRCURTDEPDCRV));
      d.setDgmrsul(row.getBigDecimal(DGMRSUL).doubleValue());
      d.setDradcondsul(row.getBigDecimal(DRADCONDSUL).doubleValue());
      d.setDwindtempratmulprs(row.getBigDecimal(DWINDTEMPRATMULPRS).doubleValue());
      d.setDratambtemp0a(row.getBigDecimal(DRATAMBTEMP0A).doubleValue());
      d.setDratambtemp1a(row.getBigDecimal(DRATAMBTEMP1A).doubleValue());
      d.setDratambtemp2a(row.getBigDecimal(DRATAMBTEMP2A).doubleValue());
      d.setDratambtemp3a(row.getBigDecimal(DRATAMBTEMP3A).doubleValue());
      d.setDratambtemp4a(row.getBigDecimal(DRATAMBTEMP4A).doubleValue());
      d.setDratambtemp5a(row.getBigDecimal(DRATAMBTEMP5A).doubleValue());
      d.setDratambtemp6a(row.getBigDecimal(DRATAMBTEMP6A).doubleValue());
      d.setDratambtemp7a(row.getBigDecimal(DRATAMBTEMP7A).doubleValue());
      d.setDstrengthfprsul2(row.getBigDecimal(DSTRENGTHFPRSUL2).doubleValue());
      d.setIstressstrain(row.getShort(ISTRESSSTRAIN));
      d.setDwtprmul(row.getBigDecimal(DWTPRMUL).doubleValue());
      d.setDcaptcstd(row.getBigDecimal(DCAPTCSTD).doubleValue());
      d.setImanufact(row.getShort(IMANUFACT));
      d.setInumwiresincond(row.getShort(INUMWIRESINCOND) == null ? 0 : row.getShort(INUMWIRESINCOND));
      d.setQearthingcond(row.getShort(QEARTHINGCOND) == null ? 0 : row.getShort(QEARTHINGCOND));
      d.setQcontainsneutralcond(row.getShort(QCONTAINSNEUTRALCOND) == null ? 0 : row.getShort(QCONTAINSNEUTRALCOND));
      d.setDneutderatingfactor(row.getDouble(DNEUTDERATINGFACTOR) == null ? 0 : row.getDouble(DNEUTDERATINGFACTOR));
      d.setIrowlinespc(row.getShort(IROWLINESPC) == null ? 0 : row.getShort(IROWLINESPC));
      d.setIzindex(row.getShort(IZINDEX) == null ? 0 : row.getShort(IZINDEX));            
      data.add(d);      
    }
    return data;
  }
  
}
