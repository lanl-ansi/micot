package gov.lanl.micot.infrastructure.project;


/**
 * Scenario Configuration
 * @author Russell Bent
 */
public class OutputConfiguration {

  private String statusLog                                            = null;
  private String networkLog                                           = null;
  private String algorithmLog                                         = null;
  private String bestModelFile                                        = null;

  /**
   * Constructor
   */
  public OutputConfiguration() { 
  }

  public String getStatusLog() {
    return statusLog;
  }

  public void setStatusLog(String statusLog) {
    this.statusLog = statusLog;
  }

  public String getNetworkLog() {
    return networkLog;
  }

  public void setNetworkLog(String networkLog) {
    this.networkLog = networkLog;
  }

  public String getAlgorithmLog() {
    return algorithmLog;
  }

  public void setAlgorithmLog(String algorithmLog) {
    this.algorithmLog = algorithmLog;
  }

  public String getBestModelFile() {
    return bestModelFile;
  }

  public void setBestModelFile(String bestModelFile) {
    this.bestModelFile = bestModelFile;
  }

	
}
