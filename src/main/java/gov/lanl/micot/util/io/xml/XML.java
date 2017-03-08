package gov.lanl.micot.util.io.xml;

import java.net.URL;

/**
 * Used to create default XML objects
 * @author Russell Bent
 */
public abstract class XML {

  // set this to change the default packages
  public static String defaultXMLName = "gov.lanl.micot.util.io.xml.dom4j.Dom4jXML";
 
  /**
   * The default XML reader, which can be changed by adjusted the class name above.  Removes strict library dependencies.
   * @return
   */
  public static XML getDefaultXML() {
    try {
      return (XML) Class.forName(defaultXMLName).newInstance();
    }
    catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Parse an xml document based on a URL
   * @param url
   * @return
   */
  public abstract XMLDocument parse(URL url);


}
