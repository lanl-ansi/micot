package gov.lanl.micot.infrastructure.ep.io.dew;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import gov.lanl.micot.infrastructure.ep.model.dew.DewPtcabcondData;
import gov.lanl.micot.util.io.database.access.DatabaseExtractor;
import gov.lanl.micot.util.io.database.access.DatabaseExtractorFactory;
import gov.lanl.micot.util.io.database.access.RowData;

/**
 * A simple structure for reading in ptcabcond
 * from the access database
 * 
 * @author Russell Bent
 *
 */
public class DewPtcabcondDatabase  {
  
  private static final String STNAM      = "STNAM";
  private static final String STCONDTYP = "STCONDTYP";
  private static final String QMENUDIS  = "QMENUDIS"; 
  private static final String ILIBROW = "ILIBROW"; 
  private static final String ICONDMAT = "ICONDMAT"; 
  private static final String DROHMPRLUL = "DROHMPRLUL"; 
  private static final String DTEMPFORROHMPRLUL = "DTEMPFORROHMPRLUL"; 
  private static final String DGMRSUL = "DGMRSUL"; 
  private static final String DRADCONDSUL = "DRADCONDSUL"; 
  private static final String DWTPRMUL = "DWTPRMUL";
  private static final String DCAPTCSTD = "DCAPTCSTD";  
  private static final String STDESC = "STDESC"; 
  private static final String QEARTHINGCOND = "QEARTHINGCOND"; 
  private static final String DNEUTDERATINGFACTOR = "DNEUTDERATINGFACTOR"; 
  private static final String IROWLINESPC = "IROWLINESPC"; 
  private static final String IZINDEX = "IZINDEX"; 

  
  
  private static final String LPHCONDKCMIL = "LPHCONDKCMIL";  
  private static final String ICAB = "ICAB"; 
  private static final String IINSUL = "IINSUL"; 
  private static final String INEUMAT = "INEUMAT"; 
  private static final String TCONCENTNEU = "TCONCENTNEU"; 
  private static final String DCONDJACKETSUL = "DCONDJACKETSUL"; 
  private static final String ICABVRAN = "ICABVRAN"; 
  private static final String DRATA0 = "DRATA0"; 
  private static final String DRATA1 = "DRATA1"; 
  private static final String DRATA2 = "DRATA2"; 
  private static final String DINSULSUL = "DINSULSUL"; 
  private static final String DNEUSTRNDROHM = "DNEUSTRNDROHM";
  private static final String SNUMNEUSTRND = "SNUMNEUSTRND";
  private static final String DDIANEUSTRNDSUL = "DDIANEUSTRNDSUL";
  private static final String DNEUSTRNDGMRSUL = "DNEUSTRNDGMRSUL";
  private static final String DRADSTRNDSUL = "DRADSTRNDSUL";
  private static final String INUMCABLESINCOND = "INUMCABLESINCOND"; 
  
    
  /**
   * gets all the data associated with cab cond
   * @param filename
   * @return
   * @throws SQLException 
   */
  public Collection<DewPtcabcondData> getData(String filename) throws SQLException {
    ArrayList<DewPtcabcondData> data = new ArrayList<DewPtcabcondData>();
    
    String table = "PTCABCOND";
  
    DatabaseExtractor extractor = DatabaseExtractorFactory.getInstance().getDefaultExtractor();
    
    Collection<RowData> rowdata = extractor.getRows(filename, table);

    for (RowData row : rowdata) {
      DewPtcabcondData d = new DewPtcabcondData();      
      d.setStnam(row.getString(STNAM));
      d.setQmenudis(row.getShort(QMENUDIS));
      d.setIlibrow(row.getShort(ILIBROW));
      d.setStdesc(row.getString(STDESC));
      d.setStcondtyp(row.getString(STCONDTYP));
      d.setIcondmat(row.getShort(ICONDMAT));
      d.setDrohmprlul(row.getBigDecimal(DROHMPRLUL) == null ? 0 : row.getBigDecimal(DROHMPRLUL).doubleValue());
      d.setDtempforrohmprlul(row.getBigDecimal(DTEMPFORROHMPRLUL).doubleValue());
      d.setDgmrsul(row.getBigDecimal(DGMRSUL).doubleValue());
      d.setDradcondsul(row.getBigDecimal(DRADCONDSUL).doubleValue());
      d.setDwtprmul(row.getBigDecimal(DWTPRMUL).doubleValue());
      d.setDcaptcstd(row.getBigDecimal(DCAPTCSTD).doubleValue());
      d.setQearthingcond(row.getShort(QEARTHINGCOND) == null ? 0 : row.getShort(QEARTHINGCOND));
      d.setDneutderatingfactor(row.getDouble(DNEUTDERATINGFACTOR) == null ? 0 : row.getDouble(DNEUTDERATINGFACTOR));
      d.setIrowlinespc(row.getShort(IROWLINESPC) == null ? 0 : row.getShort(IROWLINESPC));
      d.setIzindex(row.getShort(IZINDEX) == null ? 0 : row.getShort(IZINDEX));            
      d.setLphcondkcmil(row.getInt(LPHCONDKCMIL) == null ? 0 : row.getInt(LPHCONDKCMIL));
      d.setIcab(row.getBigDecimal(ICAB).intValue());
      d.setIinsul(row.getShort(IINSUL));
      d.setIneumat(row.getShort(INEUMAT));
      d.setTconcentineu(row.getShort(TCONCENTNEU));
      d.setDconjacketsul(row.getBigDecimal(DCONDJACKETSUL).doubleValue());
      d.setIcabvran(row.getShort(ICABVRAN));
      d.setDrata0(row.getBigDecimal(DRATA0).doubleValue());
      d.setDrata1(row.getBigDecimal(DRATA1).doubleValue());
      d.setDrata2(row.getBigDecimal(DRATA2).doubleValue());
      d.setDinsulsul(row.getBigDecimal(DINSULSUL) == null ? 0 : row.getBigDecimal(DINSULSUL).doubleValue());
      d.setDneustrndrohm(row.getBigDecimal(DNEUSTRNDROHM).doubleValue());
      d.setSnumneustrnd(row.getShort(SNUMNEUSTRND));
      d.setDdianeustrndsul(row.getBigDecimal(DDIANEUSTRNDSUL).doubleValue());
      d.setDneustrndgmrsul(row.getBigDecimal(DNEUSTRNDGMRSUL).doubleValue());
      d.setDradstrndsul(row.getBigDecimal(DRADSTRNDSUL) == null ? 0 : row.getBigDecimal(DRADSTRNDSUL).doubleValue());
      d.setInumcablesincond(row.getShort(INUMCABLESINCOND) == null ? 0 : row.getShort(INUMCABLESINCOND));
      data.add(d);      
    }
    return data;
  }
  
}
