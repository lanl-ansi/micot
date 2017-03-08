package gov.lanl.micot.util.io.xml;

import java.util.Collection;

public interface XMLElement {

  /**
   * Create an element
   * @param elementName
   * @return
   */
  public XMLElement addElement(String elementName);
  
  /**
   * Add an attribute to the xml file
   * @param attributeName
   * @param value
   */
  public void addAttribute(String attributeName, String value);

  /**
   * Get the XML element at a tag
   * @param tag
   * @return
   */
  public XMLElement getElement(String tag);

  /**
   * Get all elements within this element
   * @return
   */
  public Collection<XMLElement> elements();

  /**
   * Get the string value assoicated with a tag
   * @param nodeIdTag
   * @return
   */
  public String getValue(String tag);

  /**
   * Get the tag name of this element
   * @return
   */
  public String getName();

  /**
   * Get all the elements associated with a tag
   * @param tag
   * @return
   */
  public Collection<XMLElement> getElements(String tag);
  
}
