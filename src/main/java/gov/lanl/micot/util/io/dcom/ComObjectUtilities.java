package gov.lanl.micot.util.io.dcom;

/**
 * General utility class for com objects
 * @author Russell Bent
 */
public class ComObjectUtilities {

  // set this to change the default packages
  public static String COM_OBJECT_CLASS = "gov.lanl.micot.util.io.dcom.jacob.JacobComObjectFactory";
  
  /**
   * Not instantiable
   */
  private ComObjectUtilities() {    
  }
  
  /**
   * Get the default com factory
   * @return
   */
  public static ComObjectFactory getDefaultFactory() {
    try {
      return (ComObjectFactory) Class.forName(COM_OBJECT_CLASS).newInstance();
    }
    catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
      e.printStackTrace();
    }
    return null;
  }
  
}
