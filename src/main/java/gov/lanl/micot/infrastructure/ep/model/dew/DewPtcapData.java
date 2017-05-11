package gov.lanl.micot.infrastructure.ep.model.dew;

/**
 * A simple data structure listing some the data in the ptxfrm table of the DEW database
 * @author Art Barnes, based on code by Russell Bent
 *
 */
public class DewPtcapData {
  private String stnam;
  private int qmenudis;
  private int ilibrow;
  private int iptrow;
  private String stdesc;
  
  private double dratkvar;
  private int scon;
  private int snumposrack;



  
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
  public double getDratkvar() {
    return dratkvar;
  }

  /**
   * @param stcondtyp the stcondtyp to set
   */
  public void setDratkvar(double dratkvar) {
    this.dratkvar = dratkvar;
  }

  /**
   * @return the stcondtyp
   */
  public int getScon() {
    return scon;
  }

  /**
   * @param stcondtyp the stcondtyp to set
   */
  public void setScon(int scon) {
    this.scon = scon;
  }

  /**
   * @return the snumposrack
   */
  public int getSnumposrack() {
    return this.snumposrack;
  }
  
  /**
   * @param snumposrack the snumposrack to set
   */
  public void setSnumposrack(int snumposrack) {
    this.snumposrack = snumposrack;
  } 
}