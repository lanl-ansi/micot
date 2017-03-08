package gov.lanl.micot.util.io.database.access;


/**
 * A simple one access point for getting database extractors
 * @author Russell Bent
 *
 */
public class DatabaseExtractorFactory {

  private static DatabaseExtractorFactory INSTANCE = null;
  
  public static final String EXTRACTOR_CLASS          = "gov.lanl.micot.util.io.database.access.JackcessDatabaseExtractor";

  
  /**
   * Gets the static instance
   * @return
   */
  public static DatabaseExtractorFactory getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new DatabaseExtractorFactory();
    }
    return INSTANCE;
  }
  
  /**
   * Singleton
   */
  private DatabaseExtractorFactory() {    
  }
    
  /**
   * Get the default extractor
   * @return
   */
  public DatabaseExtractor getDefaultExtractor() {
    try {
      return (DatabaseExtractor) Class.forName(EXTRACTOR_CLASS).newInstance();
    }
    catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
      e.printStackTrace();
    }
    return null;
  }
  
}
