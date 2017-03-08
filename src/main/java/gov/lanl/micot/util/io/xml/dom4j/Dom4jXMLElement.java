package gov.lanl.micot.util.io.xml.dom4j;

import java.util.ArrayList;
import java.util.Collection;

import org.dom4j.Element;

import gov.lanl.micot.util.io.xml.XMLElement;

/**
 * A Dom4j implementation of xml elements
 * @author Russell Bent
 */
public class Dom4jXMLElement implements XMLElement {

  private Element element = null;
  
  /**
   * Constructor
   * @param element
   */
  protected Dom4jXMLElement(Element element) {
    this.element = element;
  }
  
  @Override
  public XMLElement addElement(String elementName) {
    return new Dom4jXMLElement(element.addElement(elementName));
  }

  @Override
  public void addAttribute(String attributeName, String value) {
    element.addAttribute(attributeName, value);
  }

  @Override
  public XMLElement getElement(String tag) {
    return new Dom4jXMLElement(element.element(tag));
  }

  @Override
  public Collection<XMLElement> elements() {
    ArrayList<XMLElement> elements = new ArrayList<XMLElement>();
    for (Object obj : element.elements()) {
      elements.add(new Dom4jXMLElement((Element) obj));      
    }    
    return elements;
  }

  @Override
  public String getValue(String tag) {
    return element.elementText(tag);
  }

  @Override
  public String getName() {
    return element.getName();
  }

  @Override
  public Collection<XMLElement> getElements(String tag) {
    ArrayList<XMLElement> elements = new ArrayList<XMLElement>();
    for (Object obj : element.elements(tag)) {
      elements.add(new Dom4jXMLElement((Element) obj));      
    }    
    return elements;
  }

}
