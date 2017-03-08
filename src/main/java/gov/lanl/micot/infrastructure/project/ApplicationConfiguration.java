package gov.lanl.micot.infrastructure.project;

import gov.lanl.micot.util.io.Flags;
import gov.lanl.micot.util.io.FlagsImpl;

/**
 * Configuration information for the overarching applications
 * @author Russell Bent
 */
public class ApplicationConfiguration {

  private String applicationFactoryClass                                = null;
  private Flags applicationFlags                                        = null;

  /**
   * Constructor
   */
  public ApplicationConfiguration() { 
    applicationFlags = new FlagsImpl();
  }

  /**
   * @return the algorithmFactoryClass
   */
  public String getApplicationFactoryClass() {
    return applicationFactoryClass;
  }

  /**
   * @param algorithmFactoryClass the algorithmFactoryClass to set
   */
  public void setApplicationFactoryClass(String applicationFactoryClass) {
    this.applicationFactoryClass = applicationFactoryClass;
  }

  /**
   * @return the algorithmFlags
   */
  public Flags getApplicationFlags() {
    return applicationFlags;
  }

  /**
   * @param algorithmFlags the algorithmFlags to set
   */
  public void setApplicationFlags(Flags applicationFlags) {
    this.applicationFlags = applicationFlags;
  }

  /**
   * Adds an algorithm flag
   * @param key
   * @param value
   */
  public void addApplicationFlag(String key, Object value) {
    applicationFlags.put(key, value);
  }

	
}
