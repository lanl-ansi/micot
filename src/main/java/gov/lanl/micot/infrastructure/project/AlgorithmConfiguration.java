package gov.lanl.micot.infrastructure.project;

import gov.lanl.micot.util.io.Flags;
import gov.lanl.micot.util.io.FlagsImpl;

/**
 * Algorithm Configuration
 * @author Russell Bent
 */
public class AlgorithmConfiguration {

  private String algorithmFactoryClass                                = null;
  private Flags algorithmFlags                                        = null;

  /**
   * Constructor
   */
  public AlgorithmConfiguration() { 
    algorithmFlags = new FlagsImpl();
  }

  /**
   * @return the algorithmFactoryClass
   */
  public String getAlgorithmFactoryClass() {
    return algorithmFactoryClass;
  }

  /**
   * @param algorithmFactoryClass the algorithmFactoryClass to set
   */
  public void setAlgorithmFactoryClass(String algorithmFactoryClass) {
    this.algorithmFactoryClass = algorithmFactoryClass;
  }

  /**
   * @return the algorithmFlags
   */
  public Flags getAlgorithmFlags() {
    return algorithmFlags;
  }

  /**
   * @param algorithmFlags the algorithmFlags to set
   */
  public void setAlgorithmFlags(Flags algorithmFlags) {
    this.algorithmFlags = algorithmFlags;
  }

  /**
   * Adds an algorithm flag
   * @param key
   * @param value
   */
  public void addAlgorithmFlag(String key, Object value) {
    algorithmFlags.put(key, value);
  }

	
}
