package gov.lanl.micot.infrastructure.ep.model.dew;

/**
 * A simple data structure listing all the data in the ptcabcond table of the DEW database
 * @author Russell Bent
 *
 */
public class DewPtcabcondData {
  private String stnam;
  private String stcondtyp;
  private int qmenudis;
  private int ilibrow;
  private int icondmat;
  private double drohmprlul;
  private double dtempforrohmprlul;
  private double dgmrsul;
  private double dradcondsul;
  private double dwtprmul;
  private double dcaptcstd;
  private String stdesc;
  private int qearthingcond;
  private int izindex;
  private double dneutderatingfactor;
  private int irowlinespc;
  private int lphcondkcmil;
  private int icab;
  private int iinsul;
  private int ineumat;
  private int tconcentineu;
  private double dconjacketsul;
  private int icabvran;
  private double drata0;
  private double drata1;
  private double drata2;
  private double dinsulsul;
  private double dneustrndrohm;
  private int snumneustrnd;
  private double ddianeustrndsul;
  private double dneustrndgmrsul;
  private double dradstrndsul;
  private int inumcablesincond;
  
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
  public String getStcondtyp() {
    return stcondtyp;
  }

  /**
   * @param stcondtyp the stcondtyp to set
   */
  public void setStcondtyp(String stcondtyp) {
    this.stcondtyp = stcondtyp;
  }

  /**
   * @return the icondmat
   */
  public int getIcondmat() {
    return icondmat;
  }

  /**
   * @param icondmat the icondmat to set
   */
  public void setIcondmat(int icondmat) {
    this.icondmat = icondmat;
  }

  /**
   * @return the drohmprlul
   */
  public double getDrohmprlul() {
    return drohmprlul;
  }

  /**
   * @param drohmprlul the drohmprlul to set
   */
  public void setDrohmprlul(double drohmprlul) {
    this.drohmprlul = drohmprlul;
  }

  /**
   * @return the dtempforrohmprlul
   */
  public double getDtempforrohmprlul() {
    return dtempforrohmprlul;
  }

  /**
   * @param dtempforrohmprlul the dtempforrohmprlul to set
   */
  public void setDtempforrohmprlul(double dtempforrohmprlul) {
    this.dtempforrohmprlul = dtempforrohmprlul;
  }

  /**
   * @return the dgmrsul
   */
  public double getDgmrsul() {
    return dgmrsul;
  }

  /**
   * @param dgmrsul the dgmrsul to set
   */
  public void setDgmrsul(double dgmrsul) {
    this.dgmrsul = dgmrsul;
  }

  /**
   * @return the dwtprmul
   */
  public double getDwtprmul() {
    return dwtprmul;
  }

  /**
   * @param dwtprmul the dwtprmul to set
   */
  public void setDwtprmul(double dwtprmul) {
    this.dwtprmul = dwtprmul;
  }

  /**
   * @return the dcaptcstd
   */
  public double getDcaptcstd() {
    return dcaptcstd;
  }

  /**
   * @param dcaptcstd the dcaptcstd to set
   */
  public void setDcaptcstd(double dcaptcstd) {
    this.dcaptcstd = dcaptcstd;
  }

  /**
   * @return the qearthingcond
   */
  public int getQearthingcond() {
    return qearthingcond;
  }

  /**
   * @param qearthingcond the qearthingcond to set
   */
  public void setQearthingcond(int qearthingcond) {
    this.qearthingcond = qearthingcond;
  }

  /**
   * @return the dneutderatingfactor
   */
  public double getDneutderatingfactor() {
    return dneutderatingfactor;
  }

  /**
   * @param dneutderatingfactor the dneutderatingfactor to set
   */
  public void setDneutderatingfactor(double dneutderatingfactor) {
    this.dneutderatingfactor = dneutderatingfactor;
  }

  /**
   * @return the irowlinespc
   */
  public int getIrowlinespc() {
    return irowlinespc;
  }

  /**
   * @param irowlinespc the irowlinespc to set
   */
  public void setIrowlinespc(int irowlinespc) {
    this.irowlinespc = irowlinespc;
  }

  /**
   * @return the izindex
   */
  public int getIzindex() {
    return izindex;
  }

  /**
   * @param izindex the izindex to set
   */
  public void setIzindex(int izindex) {
    this.izindex = izindex;
  }

  /**
   * @return the dradcondsul
   */
  public double getDradcondsul() {
    return dradcondsul;
  }

  /**
   * @param dradcondsul the dradcondsul to set
   */
  public void setDradcondsul(double dradcondsul) {
    this.dradcondsul = dradcondsul;
  }

  /**
   * @return the lphcondkcmil
   */
  public int getLphcondkcmil() {
    return lphcondkcmil;
  }

  /**
   * @param lphcondkcmil the lphcondkcmil to set
   */
  public void setLphcondkcmil(int lphcondkcmil) {
    this.lphcondkcmil = lphcondkcmil;
  }

  /**
   * @return the icab
   */
  public int getIcab() {
    return icab;
  }

  /**
   * @param icab the icab to set
   */
  public void setIcab(int icab) {
    this.icab = icab;
  }

  /**
   * @return the iinsul
   */
  public int getIinsul() {
    return iinsul;
  }

  /**
   * @param iinsul the iinsul to set
   */
  public void setIinsul(int iinsul) {
    this.iinsul = iinsul;
  }

  /**
   * @return the ineumat
   */
  public int getIneumat() {
    return ineumat;
  }

  /**
   * @param ineumat the ineumat to set
   */
  public void setIneumat(int ineumat) {
    this.ineumat = ineumat;
  }

  /**
   * @return the tconcentineu
   */
  public int getTconcentineu() {
    return tconcentineu;
  }

  /**
   * @param tconcentineu the tconcentineu to set
   */
  public void setTconcentineu(int tconcentineu) {
    this.tconcentineu = tconcentineu;
  }

  /**
   * @return the dconjacketsul
   */
  public double getDconjacketsul() {
    return dconjacketsul;
  }

  /**
   * @param dconjacketsul the dconjacketsul to set
   */
  public void setDconjacketsul(double dconjacketsul) {
    this.dconjacketsul = dconjacketsul;
  }

  /**
   * @return the icabvran
   */
  public int getIcabvran() {
    return icabvran;
  }

  /**
   * @param icabvran the icabvran to set
   */
  public void setIcabvran(int icabvran) {
    this.icabvran = icabvran;
  }

  /**
   * @return the drata0
   */
  public double getDrata0() {
    return drata0;
  }

  /**
   * @param drata0 the drata0 to set
   */
  public void setDrata0(double drata0) {
    this.drata0 = drata0;
  }

  /**
   * @return the drata1
   */
  public double getDrata1() {
    return drata1;
  }

  /**
   * @param drata1 the drata1 to set
   */
  public void setDrata1(double drata1) {
    this.drata1 = drata1;
  }

  /**
   * @return the drata2
   */
  public double getDrata2() {
    return drata2;
  }

  /**
   * @param drata2 the drata2 to set
   */
  public void setDrata2(double drata2) {
    this.drata2 = drata2;
  }

  /**
   * @return the dinsulsul
   */
  public double getDinsulsul() {
    return dinsulsul;
  }

  /**
   * @param dinsulsul the dinsulsul to set
   */
  public void setDinsulsul(double dinsulsul) {
    this.dinsulsul = dinsulsul;
  }

  /**
   * @return the dneustrndrohm
   */
  public double getDneustrndrohm() {
    return dneustrndrohm;
  }

  /**
   * @param dneustrndrohm the dneustrndrohm to set
   */
  public void setDneustrndrohm(double dneustrndrohm) {
    this.dneustrndrohm = dneustrndrohm;
  }

  /**
   * @return the snumneustrnd
   */
  public int getSnumneustrnd() {
    return snumneustrnd;
  }

  /**
   * @param snumneustrnd the snumneustrnd to set
   */
  public void setSnumneustrnd(int snumneustrnd) {
    this.snumneustrnd = snumneustrnd;
  }

  /**
   * @return the ddianeustrndsul
   */
  public double getDdianeustrndsul() {
    return ddianeustrndsul;
  }

  /**
   * @param ddianeustrndsul the ddianeustrndsul to set
   */
  public void setDdianeustrndsul(double ddianeustrndsul) {
    this.ddianeustrndsul = ddianeustrndsul;
  }

  /**
   * @return the dneustrndgmrsul
   */
  public double getDneustrndgmrsul() {
    return dneustrndgmrsul;
  }

  /**
   * @param dneustrndgmrsul the dneustrndgmrsul to set
   */
  public void setDneustrndgmrsul(double dneustrndgmrsul) {
    this.dneustrndgmrsul = dneustrndgmrsul;
  }

  /**
   * @return the dradstrndsul
   */
  public double getDradstrndsul() {
    return dradstrndsul;
  }

  /**
   * @param dradstrndsul the dradstrndsul to set
   */
  public void setDradstrndsul(double dradstrndsul) {
    this.dradstrndsul = dradstrndsul;
  }

  /**
   * @return the inumcablesincond
   */
  public int getInumcablesincond() {
    return inumcablesincond;
  }

  /**
   * @param inumcablesincond the inumcablesincond to set
   */
  public void setInumcablesincond(int inumcablesincond) {
    this.inumcablesincond = inumcablesincond;
  }
  

  
  
  
}
