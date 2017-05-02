package gov.lanl.micot.infrastructure.ep.model.dew;

/**
 * A simple data structure listing some the data in the ptxfrm table of the DEW database
 * @author Art Barnes
 *
 */
public class DewPtcapmData {
  private String stnam;
  private int qmenudis;
  private int ilibrow;
  private int iptrow;
  private String stdesc;
  
  private double dprikv;
  private double dseckv;
  private double dnomkva;
  
  private int snumsteps;
  private double dstepsizefr;
  private int qcompexists;
  private int qunidirection;

  
  /**
   * @return the stname
   */
  public String getStnam() {
    return stnam;
  }
  
  /**
   * @param stname the stname to set
   */
  public void setStnam(String stnam) {
    this.stnam = stnam;
  }
  
  /**
   * @return the qmenudis
   */
  public int getQmenudis() {
    return qmenudis;
  }
  
  /**
   * @param qmenudis the qmenudis to set
   */
  public void setQmenudis(int qmenudis) {
    this.qmenudis = qmenudis;
  }
  
  /**
   * @return the ilibrow
   */
  public int getIlibrow() {
    return ilibrow;
  }
  
  /**
   * @param ilibrow the ilibrow to set
   */
  public void setIlibrow(int ilibrow) {
    this.ilibrow = ilibrow;
  }
  
  /**
   * @return the iptrow
   */
  public int getIptrow() {
    return iptrow;
  }

  /**
   * @param iptrow the iptrow to set
   */  
  public void setIptrow(int iptrow) {
    this.iptrow = iptrow;
  }  
  
  /**
   * @return the stdesc
   */
  public String getStdesc() {
    return stdesc;
  }
  
  /**
   * @param stdesc the stdesc to set
   */
  public void setStdesc(String stdesc) {
    this.stdesc = stdesc;
  }
  
  /**
   * @return the stcondtyp
   */
  public double getDprikv() {
    return dprikv;
  }

  /**
   * @param stcondtyp the stcondtyp to set
   */
  public void setDprikv(double dprikv) {
    this.dprikv = dprikv;
  }

  /**
   * @return the stcondtyp
   */
  public double getDseckv() {
    return dseckv;
  }

  /**
   * @param stcondtyp the stcondtyp to set
   */
  public void setDseckv(double dseckv) {
    this.dseckv = dseckv;
  }

  /**
   * @return the stcondtyp
   */
  public double getDnomkva() {
    return dnomkva;
  }

  /**
   * @param stcondtyp the stcondtyp to set
   */
  public void setDnomkva(double dnomkva) {
    this.dnomkva = dnomkva;
  }
  

  /**
   * @return the stcondtyp
   */
  public int getSnumsteps() {
    return this.snumsteps;
  }
  
  /**
   * @param stcondtyp the stcondtyp to set
   */
  public void setSnumsteps(int snumsteps) {
    this.snumsteps = snumsteps;
  }
  
  /**
   * @return the stcondtyp
   */
  public double getDstepsizefr() {
    return this.dstepsizefr;
  }
  
  /**
   * @param stcondtyp the stcondtyp to set
   */
  public void setDstepsizefr(double dstepsizefr) {
    this.dstepsizefr = dstepsizefr;
  }
  
  /**
   * @return the stcondtyp
   */
  public int getQcompexists() {
    return this.qcompexists;
  }
  
  /**
   * @param stcondtyp the stcondtyp to set
   */
  public void setQcompexists(int qcompexists) {
    this.qcompexists = qcompexists;
  }  
  
  /**
   * @return the stcondtyp
   */
  public int getQunidirection() {
    return this.qunidirection;
  }
  
  /**
   * @param stcondtyp the stcondtyp to set
   */
  public void setQunidirection(int qunidirection) {
    this.qunidirection = qunidirection;
  }   
}