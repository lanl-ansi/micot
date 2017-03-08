package gov.lanl.micot.util.io.xml;

/**
 * Interface for interacting with libaries for interacting with XML documents
 * @author Russell Bent
 */
public interface XMLDocument {
  
  /**
   * Get the top element of the XML Document
   * @return
   */
  public XMLElement getTopElement();

  /**
   * Create the top element of an xml document
   * @param rootName
   * @return
   */
  public XMLElement createTopElement(String rootName);

  /**
   * Write the xml document associated with this document
   * @param filename
   */
  public void writeXML(String filename);
    
}
