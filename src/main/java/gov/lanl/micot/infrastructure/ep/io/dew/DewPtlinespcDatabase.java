package gov.lanl.micot.infrastructure.ep.io.dew;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import gov.lanl.micot.infrastructure.ep.model.dew.DewPtlinespcData;
import gov.lanl.micot.util.io.database.access.DatabaseExtractor;
import gov.lanl.micot.util.io.database.access.DatabaseExtractorFactory;
import gov.lanl.micot.util.io.database.access.RowData;

/**
 * A simple structure for reading in ptlinespcdata
 * from the access database
 * 
 * @author Russell Bent
 *
 */
public class DewPtlinespcDatabase  {
  
  private static final String STNAM = "STNAM";
  private static final String QMENUDIS = "QMENUDIS"; 
  private static final String ILIBROW = "ILIBROW"; 
  private static final String IPTROW = "IPTROW"; 
  private static final String ICMP = "ICMP"; 
  private static final String SOVERHEAD = "SOVERHEAD"; 
  private static final String IOVLD = "IOVLD"; 
  private static final String CHCONSTGRAD = "CHCONSTGRAD"; 
  private static final String CHSTRUCTTYP = "CHSTRUCTTYP";
  private static final String DXPH1ORR1 = "DXPH1ORR1";
  private static final String DYPH1ORX1 = "DYPH1ORX1";
  private static final String DXPH2ORR0 = "DXPH2ORR0";
  private static final String DYPH2ORX0 = "DYPH2ORX0";
  private static final String DXPH3ORY0 = "DXPH3ORY0";
  private static final String DYPH3ORY1 = "DYPH3ORY1";
  private static final String DXNEU = "DXNEU";
  private static final String DYNEU = "DYNEU";
  private static final String DEARTH = "DEARTH";
  private static final String SNUMCKTS = "SNUMCKTS";
  private static final String SNUMPH = "SNUMPH";
  private static final String SNUMNEU = "SNUMNEU";
  private static final String IVRAN = "IVRAN";
  private static final String IBIL = "IBIL";
  private static final String IBSL = "IBSL";
  private static final String DCFOKV = "DCFOKV";
  private static final String DWTHSTDKV = "DWTHSTDKV";
  private static final String STDESC = "STDESC";
  private static final String DCAPTCSTMULT = "DCAPTCSTMULT";
  private static final String SMNRMAINTMON = "SMNRMAINTMON";
  private static final String SMJRMAINTMON = "SMJRMAINTMON";
  private static final String SMNRMAINTHRS = "SMNRMAINTHRS";
  private static final String SMJRMAINTHRS = "SMJRMAINTHRS";
  private static final String DEDATE = "DEDATE";
  private static final String LTOTNUMFAIL = "LTOTNUMFAIL";
  private static final String LYRSEXP = "LYRSEXP";
  private static final String LTOTINSTAL = "LTOTINSTAL";
  private static final String LNUMREPR = "LNUMREPR";
  private static final String DTOTREPRTMHRS = "DTOTREPRTMHRS";
  private static final String DAVGREPRTMHRS = "DAVGREPRTMHRS";
  private static final String DAVGFAILPRYR = "DAVGFAILPRYR";
  private static final String IPHCONDINDX = "IPHCONDINDX";
  private static final String INEUCONDINDX = "INEUCONDINDX";
  private static final String ICONFIGLNKID = "ICONFIGLNKID";
  private static final String F45 = "F45";
  private static final String F46 = "F46";
  private static final String INDXSET = "INDXSET";
  private static final String TMUTSPC = "TMUTSPC";
    
  /**
   * gets all the data associated with ptlinespc
   * @param filename
   * @return
   * @throws SQLException 
   */
  public Collection<DewPtlinespcData> getData(String filename) throws SQLException {
    ArrayList<DewPtlinespcData> data = new ArrayList<DewPtlinespcData>();
    
    String table = "PTLINESPC";
  
    DatabaseExtractor extractor = DatabaseExtractorFactory.getInstance().getDefaultExtractor();
    
    Collection<RowData> rowdata = extractor.getRows(filename, table);

    for (RowData row : rowdata) {
      DewPtlinespcData d = new DewPtlinespcData();      
      d.setStnam(row.getString(STNAM));
      d.setQmenudis(row.getShort(QMENUDIS));
      d.setIlibrow(row.getShort(ILIBROW));
      d.setIptrow(row.getShort(IPTROW));
      d.setIcmp(row.getShort(ICMP));
      d.setSoverhead(row.getShort(SOVERHEAD));
      d.setIovld(row.getShort(IOVLD));
      d.setChconstgrad(row.getString(CHCONSTGRAD));
      d.setChstructtyp(row.getString(CHSTRUCTTYP));
      d.setDxph1orr1(row.getBigDecimal(DXPH1ORR1).doubleValue());       
      d.setDyph1orx1(row.getBigDecimal(DYPH1ORX1).doubleValue());
      d.setDxph2orr0(row.getBigDecimal(DXPH2ORR0).doubleValue());
      d.setDyph2orx0(row.getBigDecimal(DYPH2ORX0).doubleValue());
      d.setDxph3ory0(row.getBigDecimal(DXPH3ORY0).doubleValue());
      d.setDyph3ory1(row.getBigDecimal(DYPH3ORY1).doubleValue());
      d.setDxneu(row.getBigDecimal(DXNEU).doubleValue());
      d.setDyneu(row.getBigDecimal(DYNEU).doubleValue());
      d.setDearth(row.getBigDecimal(DEARTH).doubleValue());
      d.setSnumckts(row.getShort(SNUMCKTS));
      d.setSnumph(row.getShort(SNUMPH));
      d.setSnumneu(row.getShort(SNUMNEU));
      d.setIvran(row.getShort(IVRAN));
      d.setIbil(row.getShort(IBIL));
      d.setIbsl(row.getShort(IBSL));
      d.setDcfokv(row.getBigDecimal(DCFOKV).doubleValue());
      d.setDwthstdkv(row.getBigDecimal(DWTHSTDKV).doubleValue());
      d.setStdesc(row.getString(STDESC));
      d.setDcaptcstmult(row.getBigDecimal(DCAPTCSTMULT).doubleValue());
      d.setSmnrmaintmon(row.getShort(SMNRMAINTMON));
      d.setSmjrmaintmon(row.getShort(SMJRMAINTMON));
      d.setSmnrmainthrs(row.getShort(SMNRMAINTHRS));
      d.setSmjrmainthrs(row.getShort(SMJRMAINTHRS));
      d.setDedate(row.getDate(DEDATE) == null ? "" : row.getDate(DEDATE).toString());
      d.setLtotnumfail(row.getShort(LTOTNUMFAIL));
      d.setLyrsexp(row.getShort(LYRSEXP));
      d.setLtotinstal(row.getShort(LTOTINSTAL));
      d.setLnumrepr(row.getShort(LNUMREPR));
      d.setDtotreprtmhrs(row.getBigDecimal(DTOTREPRTMHRS) == null ? 0 : row.getBigDecimal(DTOTREPRTMHRS).intValue());
      d.setDavgreprtmhrs(row.getBigDecimal(DAVGREPRTMHRS).intValue());
      d.setDavgfailpryr(row.getBigDecimal(DAVGFAILPRYR).doubleValue());
      d.setTmutspc(row.getShort(TMUTSPC));      
      d.setIphcondindx(row.getShort(IPHCONDINDX) == null ? 0 : row.getShort(IPHCONDINDX));      
      d.setIneucondindx(row.getShort(INEUCONDINDX) == null ? 0 : row.getShort(INEUCONDINDX));      
      d.setIconfiglnkid(row.getShort(ICONFIGLNKID) == null ? 0 : row.getShort(ICONFIGLNKID));      
      d.setF45(row.getString(F45) == null ? "" : row.getString(F45));
      d.setF46(row.getString(F46) == null ? "" : row.getString(F46));
      d.setIndxSet(row.getInt(INDXSET) == null ? 0 : row.getInt(INDXSET));                                         
      data.add(d);      
    }

    
    return data;
  }
  
}
