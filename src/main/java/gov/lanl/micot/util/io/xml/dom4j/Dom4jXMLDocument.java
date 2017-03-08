package gov.lanl.micot.util.io.xml.dom4j;

import java.io.PrintStream;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import gov.lanl.micot.util.io.xml.XMLDocument;
import gov.lanl.micot.util.io.xml.XMLElement;

/**
 * Implementation of the XML Document using Dom4j
 * @author Russell Bent
 */
public class Dom4jXMLDocument implements XMLDocument {

  private Document document = null;
  
  /**
   * Constructor
   * @param document
   */
  protected Dom4jXMLDocument(Document document) {
    this.document = document;
  }

  @Override
  public XMLElement getTopElement() {
    return new Dom4jXMLElement(document.getRootElement());
  }

  @Override
  public XMLElement createTopElement(String rootName) {
    if (document == null) {
      document = DocumentHelper.createDocument();
    }    
    return new Dom4jXMLElement(document.addElement(rootName));
  }

  @Override
  public void writeXML(String filename) {
    OutputFormat format = OutputFormat.createPrettyPrint();
    try {      
      XMLWriter writer = new XMLWriter(new PrintStream(filename), format);
      writer.write(document);    
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

}
